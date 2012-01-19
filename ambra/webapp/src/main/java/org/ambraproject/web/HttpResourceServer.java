/* $HeadURL::                                                                            $
 * $Id$
 *
 * Copyright (c) 2006-2010 by Public Library of Science
 * http://plos.org
 * http://ambraproject.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ambraproject.web;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.File;
import java.io.FileInputStream;

import java.net.URL;
import java.net.URLConnection;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.TimeZone;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A utility class that can serve up static resources (images, css, javascript etc.)  in
 * response to HttpServletRequests. This can be included in any Servlet or Filter as needed.  It
 * is based-on the DefaultServlet in catalina. (Revision 657157 of
 * http://svn.apache.org/repos/asf/tomcat/tc6.0.x/trunk/java/org/apache/catalina/servlets/DefaultServlet.java)
 */
public class HttpResourceServer {
  private static final Logger log = LoggerFactory.getLogger(HttpResourceServer.class);

  /**
   * The input buffer size to use when serving resources.
   */
  private static final int INPUT_BUFFER_SIZE = 16384;

  /**
   * The output buffer size to use when serving resources.
   */
  private static final int OUTPUT_BUFFER_SIZE = 16384;

  /**
   * File encoding to be used when reading static files. If none is specified  UTF-8 is used.
   */
  private static final String FILE_ENCODING = "UTF-8";

  /**
   * Full range marker.
   */
  private static final ArrayList<Range> FULL = new ArrayList<Range>();

  /**
   * MIME multipart separation string
   */
  private static final String MIME_SEPARATION = "AMBRA_MIME_BOUNDARY";

  /**
   * Serve the specified resource, optionally including the data content.
   *
   * @param request The servlet request we are processing
   * @param response The servlet response we are creating
   * @param resource The resource to send
   *
   * @exception IOException if an input/output error occurs
   */
  public void serveResource(HttpServletRequest request, HttpServletResponse response,
                            Resource resource, String xReproxy) throws IOException {
    boolean content = "HEAD".equals(request.getMethod()) || xReproxy != null;
    serveResource(request, response, !content, resource);
  }

  /**
   * Serve the specified resource, optionally including the data content.
   *
   * @param request The servlet request we are processing
   * @param response The servlet response we are creating
   * @param content Should the content be included?
   * @param resource The resource to send
   *
   * @exception IOException if an input/output error occurs
   */
  public void serveResource(HttpServletRequest request, HttpServletResponse response,
                            boolean content, Resource resource)
                     throws IOException {
    // Check if the conditions specified in the optional If headers are satisfied.
    if (!checkIfHeaders(request, response, resource))
      return;

    // Parse range specifier
    ArrayList<Range> ranges = parseRange(request, response, resource);
    // ETag header
    response.setHeader("ETag", getETag(resource));
    // Last-Modified header
    response.setHeader("Last-Modified", resource.getLastModifiedHttp());

    // Special case for zero length files, which would cause a
    // (silent) ISE when setting the output buffer size
    if (resource.getContentLength() == 0L)
      content = false;

    ServletOutputStream ostream       = null;
    PrintWriter         writer        = null;
    String              contentType   = resource.getContentType();
    long                contentLength = resource.getContentLength();

    if (content) {
      // Trying to retrieve the servlet output stream
      try {
        ostream = response.getOutputStream();
      } catch (IllegalStateException e) {
        // If it fails, we try to get a Writer instead if we're trying to serve a text file
        if ((contentType == null) || (contentType.startsWith("text"))
             || (contentType.endsWith("xml"))) {
          writer = response.getWriter();
        } else {
          throw e;
        }
      }
    }

    if ((((ranges == null) || (ranges.isEmpty())) && (request.getHeader("Range") == null))
         || (ranges == FULL)) {
      if (log.isDebugEnabled())
        log.debug("Full content response for " + resource);

      setOutputHeaders(response, contentType, contentLength, content);

      // Copy the input stream to our output stream (if requested)
      if (content) {
        if (ostream != null) {
          copy(resource, ostream);
        } else {
          copy(resource, writer);
        }
      }
    } else {
      if ((ranges == null) || (ranges.isEmpty()))
        return;

      if (log.isDebugEnabled())
        log.debug("Partial content response for " + resource);

      response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);

      if (ranges.size() == 1) {
        Range range = ranges.get(0);
        response.addHeader("Content-Range",
                           "bytes " + range.start + "-" + range.end + "/" + range.length);

        long length = range.end - range.start + 1;

        setOutputHeaders(response, contentType, length, content);

        if (content) {
          if (ostream != null) {
            copy(resource, ostream, range);
          } else {
            copy(resource, writer, range);
          }
        }
      } else {
        response.setContentType("multipart/byteranges; boundary=" + MIME_SEPARATION);

        if (content) {
          try {
            response.setBufferSize(OUTPUT_BUFFER_SIZE);
          } catch (IllegalStateException e) {
            // Silent catch
          }

          if (ostream != null) {
            copy(resource, ostream, ranges.iterator(), contentType);
          } else {
            copy(resource, writer, ranges.iterator(), contentType);
          }
        }
      }
    }
  }

  /**
   * Set the headers before streaming out content.
   *
   * @param response the response we are working with
   * @param contentType the contentType to set
   * @param contentLength the content length to set
   * @param content false for sending only the headers
   */
  protected void setOutputHeaders(HttpServletResponse response, String contentType,
                                  long contentLength, boolean content) {
    // Set the appropriate output headers
    if (contentType != null)
      response.setContentType(contentType);

    if (contentLength >= 0) {
      if (contentLength < Integer.MAX_VALUE) {
        response.setContentLength((int) contentLength);
      } else {
        // Set the content-length as String to be able to use a long
        response.setHeader("content-length", "" + contentLength);
      }
    }

    // Copy the input stream to our output stream (if requested)
    if (content) {
      try {
        response.setBufferSize(OUTPUT_BUFFER_SIZE);
      } catch (IllegalStateException e) {
        // Silent catch
      }
    }
  }

  /**
   * Parse the content-range header.
   *
   * @param request The servlet request we are processing
   * @param response The servlet response we are creating
   *
   * @return Range
   *
   * @throws IOException on an error
   */
  protected Range parseContentRange(HttpServletRequest request, HttpServletResponse response)
                             throws IOException {
    // Retrieving the content-range header (if any is specified
    String rangeHeader = request.getHeader("Content-Range");

    if (rangeHeader == null)
      return null;

    // bytes is the only range unit supported
    if (!rangeHeader.startsWith("bytes")) {
      response.sendError(HttpServletResponse.SC_BAD_REQUEST);

      return null;
    }

    rangeHeader = rangeHeader.substring(6).trim();

    int dashPos  = rangeHeader.indexOf('-');
    int slashPos = rangeHeader.indexOf('/');

    if (dashPos == -1) {
      response.sendError(HttpServletResponse.SC_BAD_REQUEST);

      return null;
    }

    if (slashPos == -1) {
      response.sendError(HttpServletResponse.SC_BAD_REQUEST);

      return null;
    }

    Range range = new Range();

    try {
      range.start    = Long.parseLong(rangeHeader.substring(0, dashPos));
      range.end      = Long.parseLong(rangeHeader.substring(dashPos + 1, slashPos));
      range.length   = Long.parseLong(rangeHeader.substring(slashPos + 1, rangeHeader.length()));
    } catch (NumberFormatException e) {
      response.sendError(HttpServletResponse.SC_BAD_REQUEST);

      return null;
    }

    if (!range.validate()) {
      response.sendError(HttpServletResponse.SC_BAD_REQUEST);

      return null;
    }

    return range;
  }

  /**
   * Parse the range header.
   *
   * @param request The servlet request we are processing
   * @param response The servlet response we are creating
   * @param resource The resource we are serving
   *
   * @return Vector of ranges
   *
   * @throws IOException on an error
   */
  protected ArrayList<Range> parseRange(HttpServletRequest request, HttpServletResponse response,
                                 Resource resource) throws IOException {
    // Checking If-Range
    String headerValue = request.getHeader("If-Range");

    if (headerValue != null) {
      long headerValueTime = (-1L);

      try {
        headerValueTime = request.getDateHeader("If-Range");
      } catch (IllegalArgumentException e) {
      }

      String eTag         = getETag(resource);
      long   lastModified = resource.getLastModified();

      if (headerValueTime == (-1L)) {
        /* 
         * If the ETag the client gave does not match the entity etag, then the entire entity is
         * returned.
         */
        if (!eTag.equals(headerValue.trim()))
          return FULL;
      } else {
        /*
         * If the timestamp of the entity the client got is older than the last modification date of
         * the entity, the entire entity is returned.
         */
        if (lastModified > (headerValueTime + 1000))
          return FULL;
      }
    }

    long fileLength = resource.getContentLength();

    if (fileLength == 0)
      return null;

    // Retrieving the range header (if any is specified
    String rangeHeader = request.getHeader("Range");

    if (rangeHeader == null)
      return null;

    // bytes is the only range unit supported (and I don't see the point
    // of adding new ones).
    if (!rangeHeader.startsWith("bytes")) {
      response.addHeader("Content-Range", "bytes */" + fileLength);
      response.sendError(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);

      return null;
    }

    rangeHeader = rangeHeader.substring(6);

    // Vector which will contain all the ranges which are successfully
    // parsed.
    ArrayList<Range> result         = new ArrayList<Range>();
    StringTokenizer  commaTokenizer = new StringTokenizer(rangeHeader, ",");

    // Parsing the range list
    while (commaTokenizer.hasMoreTokens()) {
      String rangeDefinition = commaTokenizer.nextToken().trim();

      Range  currentRange    = new Range();
      currentRange.length    = fileLength;

      int dashPos            = rangeDefinition.indexOf('-');

      if (dashPos == -1) {
        response.addHeader("Content-Range", "bytes */" + fileLength);
        response.sendError(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);

        return null;
      }

      if (dashPos == 0) {
        try {
          long offset = Long.parseLong(rangeDefinition);
          currentRange.start   = fileLength + offset;
          currentRange.end     = fileLength - 1;
        } catch (NumberFormatException e) {
          response.addHeader("Content-Range", "bytes */" + fileLength);
          response.sendError(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);

          return null;
        }
      } else {
        try {
          currentRange.start = Long.parseLong(rangeDefinition.substring(0, dashPos));

          if (dashPos < (rangeDefinition.length() - 1))
            currentRange.end = Long.parseLong(rangeDefinition.substring(dashPos + 1,
                                                                        rangeDefinition.length()));
          else
            currentRange.end = fileLength - 1;
        } catch (NumberFormatException e) {
          response.addHeader("Content-Range", "bytes */" + fileLength);
          response.sendError(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);

          return null;
        }
      }

      if (!currentRange.validate()) {
        response.addHeader("Content-Range", "bytes */" + fileLength);
        response.sendError(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);

        return null;
      }

      result.add(currentRange);
    }

    return result;
  }

  /**
   * Check if the conditions specified in the optional If headers are satisfied.
   *
   * @param request The servlet request we are processing
   * @param response The servlet response we are creating
   * @param resource The resource information
   *
   * @return boolean true if the resource meets all the specified conditions, and false if any of
   *         the conditions is not satisfied, in which case request processing is stopped
   *
   * @throws IOException on an error
   */
  protected boolean checkIfHeaders(HttpServletRequest request, HttpServletResponse response,
                                   Resource resource) throws IOException {
    return checkIfMatch(request, response, resource)
            && checkIfModifiedSince(request, response, resource)
            && checkIfNoneMatch(request, response, resource)
            && checkIfUnmodifiedSince(request, response, resource);
  }

  /**
   * Get the ETag associated with a file.
   *
   * @param resource The resource information
   *
   * @return the ETag
   */
  protected String getETag(Resource resource) {
    return "W/\"" + resource.getContentLength() + "-" + resource.getLastModified() + "\"";
  }

  /**
   * Check if the if-match condition is satisfied.
   *
   * @param request The servlet request we are processing
   * @param response The servlet response we are creating
   * @param resource File object
   *
   * @return boolean true if the resource meets the specified condition, and false if the condition
   *         is not satisfied, in which case request processing is stopped
   *
   * @throws IOException on an error
   */
  protected boolean checkIfMatch(HttpServletRequest request, HttpServletResponse response,
                                 Resource resource) throws IOException {
    String eTag        = getETag(resource);
    String headerValue = request.getHeader("If-Match");

    if (headerValue != null) {
      if (headerValue.indexOf('*') == -1) {
        StringTokenizer commaTokenizer     = new StringTokenizer(headerValue, ",");
        boolean         conditionSatisfied = false;

        while (!conditionSatisfied && commaTokenizer.hasMoreTokens()) {
          String currentToken = commaTokenizer.nextToken();

          if (currentToken.trim().equals(eTag))
            conditionSatisfied = true;
        }

        // If none of the given ETags match, 412 Precodition failed is
        // sent back
        if (!conditionSatisfied) {
          response.sendError(HttpServletResponse.SC_PRECONDITION_FAILED);

          if (log.isDebugEnabled())
            log.debug("If-Match: " + headerValue + ": Sending 'Pre-condition Failed' for "
                      + resource);

          return false;
        }
      }
    }

    return true;
  }

  /**
   * Check if the if-modified-since condition is satisfied.
   *
   * @param request The servlet request we are processing
   * @param response The servlet response we are creating
   * @param resource File object
   *
   * @return boolean true if the resource meets the specified condition, and false if the condition
   *         is not satisfied, in which case request processing is stopped
   *
   * @throws IOException on an error
   */
  protected boolean checkIfModifiedSince(HttpServletRequest request, HttpServletResponse response,
                                         Resource resource)
                                  throws IOException {
    try {
      long headerValue  = request.getDateHeader("If-Modified-Since");
      long lastModified = resource.getLastModified();

      if (headerValue != -1) {
        // If an If-None-Match header has been specified, if modified since is ignored.
        if ((request.getHeader("If-None-Match") == null) && (lastModified < (headerValue + 1000))) {
          /*
           * The entity has not been modified since the date specified by the client. This is not an
           * error case.
           */
          response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
          response.setHeader("ETag", getETag(resource));

          if (log.isDebugEnabled())
            log.debug("If-Modified-Since: " + headerValue + ": Sending 'Not Modified' for "
                      + resource);

          return false;
        }
      }
    } catch (IllegalArgumentException illegalArgument) {
      return true;
    }

    return true;
  }

  /**
   * Check if the if-none-match condition is satisfied.
   *
   * @param request The servlet request we are processing
   * @param response The servlet response we are creating
   * @param resource File object
   *
   * @return boolean true if the resource meets the specified condition, and false if the condition
   *         is not satisfied, in which case request processing is stopped
   *
   * @throws IOException on an error
   */
  protected boolean checkIfNoneMatch(HttpServletRequest request, HttpServletResponse response,
                                     Resource resource)
                              throws IOException {
    String eTag        = getETag(resource);
    String headerValue = request.getHeader("If-None-Match");

    if (headerValue != null) {
      boolean conditionSatisfied = false;

      if (!headerValue.equals("*")) {
        StringTokenizer commaTokenizer = new StringTokenizer(headerValue, ",");

        while (!conditionSatisfied && commaTokenizer.hasMoreTokens()) {
          String currentToken = commaTokenizer.nextToken();

          if (currentToken.trim().equals(eTag))
            conditionSatisfied = true;
        }
      } else {
        conditionSatisfied = true;
      }

      if (conditionSatisfied) {
        /*
         * For GET and HEAD, we should respond with 304 Not Modified.
         * For every other method, 412 Precondition Failed is sent back.
         */
        if (("GET".equals(request.getMethod())) || ("HEAD".equals(request.getMethod()))) {
          response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
          response.setHeader("ETag", getETag(resource));

          if (log.isDebugEnabled())
            log.debug("If-None-Match: " + headerValue + ": Sending 'Not Modified' for " + resource);

          return false;
        } else {
          response.sendError(HttpServletResponse.SC_PRECONDITION_FAILED);

          if (log.isDebugEnabled())
            log.debug("If-None-Match: " + headerValue + ": Sending 'Pre-condition failed' for " +
                      resource);

          return false;
        }
      }
    }

    return true;
  }

  /**
   * Check if the if-unmodified-since condition is satisfied.
   *
   * @param request The servlet request we are processing
   * @param response The servlet response we are creating
   * @param resource File object
   *
   * @return boolean true if the resource meets the specified condition, and false if the condition
   *         is not satisfied, in which case request processing is stopped
   *
   * @throws IOException on an error
   */
  protected boolean checkIfUnmodifiedSince(HttpServletRequest request,
                                           HttpServletResponse response, Resource resource)
                                    throws IOException {
    try {
      long lastModified = resource.getLastModified();
      long headerValue  = request.getDateHeader("If-Unmodified-Since");

      if (headerValue != -1) {
        if (lastModified >= (headerValue + 1000)) {
          /*
           * The entity has not been modified since the date specified by the client. This is not an
           * error case.
           */
          response.sendError(HttpServletResponse.SC_PRECONDITION_FAILED);

          if (log.isDebugEnabled())
            log.debug("If-Unmodified-Since: " + headerValue
                      + ": Sending 'Pre-condition failed' for " + resource);

          return false;
        }
      }
    } catch (IllegalArgumentException illegalArgument) {
      return true;
    }

    return true;
  }

  /**
   * Copy the contents of the specified input stream to the specified output stream, and
   * ensure that both streams are closed before returning (even in the face of an exception).
   *
   * @param resource The resource information
   * @param ostream The output stream to write to
   *
   * @exception IOException if an input/output error occurs
   */
  protected void copy(Resource resource, ServletOutputStream ostream)
               throws IOException {
    // Optimization: If the binary content has already been loaded, send it directly
    byte[] buffer = resource.getContent();

    if (buffer != null) {
      ostream.write(buffer, 0, buffer.length);

      return;
    }

    // Copy the input stream to the output stream
    InputStream istream   = resource.streamContent();
    IOException exception = copyRange(istream, ostream);

    // Clean up the input stream
    istream.close();

    // Rethrow any exception that has occurred
    if (exception != null)
      throw exception;
  }

  /**
   * Copy the contents of the specified input stream to the specified output stream, and
   * ensure that both streams are closed before returning (even in the face of an exception).
   *
   * @param resource The resource info
   * @param writer The writer to write to
   *
   * @exception IOException if an input/output error occurs
   */
  protected void copy(Resource resource, PrintWriter writer) throws IOException {
    InputStream resourceInputStream = resource.streamContent();

    Reader reader = new InputStreamReader(resourceInputStream, FILE_ENCODING);

    // Copy the input stream to the output stream
    IOException exception = copyRange(reader, writer);

    // Clean up the reader
    reader.close();

    // Rethrow any exception that has occurred
    if (exception != null)
      throw exception;
  }

  /**
   * Copy the contents of the specified input stream to the specified output stream, and
   * ensure that both streams are closed before returning (even in the face of an exception).
   *
   * @param resource The ResourceInfo object
   * @param ostream The output stream to write to
   * @param range Range the client wanted to retrieve
   *
   * @exception IOException if an input/output error occurs
   */
  protected void copy(Resource resource, ServletOutputStream ostream, Range range)
               throws IOException {
    InputStream istream   = resource.streamContent();
    IOException exception = copyRange(istream, ostream, range.start, range.end);

    // Clean up the input stream
    istream.close();

    // Rethrow any exception that has occurred
    if (exception != null)
      throw exception;
  }

  /**
   * Copy the contents of the specified input stream to the specified output stream, and
   * ensure that both streams are closed before returning (even in the face of an exception).
   *
   * @param resource The ResourceInfo object
   * @param writer The writer to write to
   * @param range Range the client wanted to retrieve
   *
   * @exception IOException if an input/output error occurs
   */
  protected void copy(Resource resource, PrintWriter writer, Range range)
               throws IOException {
    InputStream resourceInputStream = resource.streamContent();

    Reader      reader = new InputStreamReader(resourceInputStream, FILE_ENCODING);

    IOException exception = copyRange(reader, writer, range.start, range.end);

    // Clean up the input stream
    reader.close();

    // Rethrow any exception that has occurred
    if (exception != null)
      throw exception;
  }

  /**
   * Copy the contents of the specified input stream to the specified output stream, and
   * ensure that both streams are closed before returning (even in the face of an exception).
   *
   * @param resource The ResourceInfo object
   * @param ostream The output stream to write to
   * @param ranges Enumeration of the ranges the client wanted to retrieve
   * @param contentType Content type of the resource
   *
   * @exception IOException if an input/output error occurs
   */
  protected void copy(Resource resource, ServletOutputStream ostream, Iterator ranges,
                      String contentType) throws IOException {
    IOException exception = null;

    while ((exception == null) && (ranges.hasNext())) {
      InputStream istream      = resource.streamContent();
      Range       currentRange = (Range) ranges.next();

      // Writing MIME header.
      ostream.println();
      ostream.println("--" + MIME_SEPARATION);

      if (contentType != null)
        ostream.println("Content-Type: " + contentType);

      ostream.println("Content-Range: bytes " + currentRange.start + "-" + currentRange.end + "/"
                      + currentRange.length);
      ostream.println();

      // Printing content
      exception = copyRange(istream, ostream, currentRange.start, currentRange.end);

      istream.close();
    }

    ostream.println();
    ostream.print("--" + MIME_SEPARATION + "--");

    // Rethrow any exception that has occurred
    if (exception != null)
      throw exception;
  }

  /**
   * Copy the contents of the specified input stream to the specified output stream, and
   * ensure that both streams are closed before returning (even in the face of an exception).
   *
   * @param resource The ResourceInfo object
   * @param writer The writer to write to
   * @param ranges Enumeration of the ranges the client wanted to retrieve
   * @param contentType Content type of the resource
   *
   * @exception IOException if an input/output error occurs
   */
  protected void copy(Resource resource, PrintWriter writer, Iterator ranges, String contentType)
               throws IOException {
    IOException exception = null;

    while ((exception == null) && (ranges.hasNext())) {
      InputStream resourceInputStream = resource.streamContent();

      Reader      reader = new InputStreamReader(resourceInputStream, FILE_ENCODING);

      Range currentRange = (Range) ranges.next();

      // Writing MIME header.
      writer.println();
      writer.println("--" + MIME_SEPARATION);

      if (contentType != null)
        writer.println("Content-Type: " + contentType);

      writer.println("Content-Range: bytes " + currentRange.start + "-" + currentRange.end + "/" +
                     currentRange.length);
      writer.println();

      // Printing content
      exception = copyRange(reader, writer, currentRange.start, currentRange.end);

      reader.close();
    }

    writer.println();
    writer.print("--" + MIME_SEPARATION + "--");

    // Rethrow any exception that has occurred
    if (exception != null)
      throw exception;
  }

  /**
   * Copy the contents of the specified input stream to the specified output stream, and
   * ensure that both streams are closed before returning (even in the face of an exception).
   * <br/>
   * If an IOException occurs, then log that exception but do not return it like the other
   * <code>copyRange(...)</code> methods do.
   *
   * @param istream The input stream to read from
   * @param ostream The output stream to write to
   *
   * @return <code>null</code> in all circumstances; returning IOException does nothing useful
   */
  protected IOException copyRange(InputStream istream, ServletOutputStream ostream) {
    // Copy the input stream to the output stream
    byte[]      buffer    = new byte[INPUT_BUFFER_SIZE];
    int         len;

    while (true) {
      try {
        len = istream.read(buffer);

        if (len == -1)
          break;

        ostream.write(buffer, 0, len);
      } catch (IOException e) {
        // If there is an exception, then log it and ignore it.  IOException here tends to happen
        // because of a loss of connectivity with the client, usually due to a "broken pipe".
        // Throwing does no good because there is no mechanism for re-sending the failed content.
        log.warn("Failure while attempting to copy an Input Stream to an Output Stream.", e);
        break;
      }
    }

    return null;
  }

  /**
   * Copy the contents of the specified input stream to the specified output stream, and
   * ensure that both streams are closed before returning (even in the face of an exception).
   *
   * @param reader The reader to read from
   * @param writer The writer to write to
   *
   * @return Exception which occurred during processing
   */
  protected IOException copyRange(Reader reader, PrintWriter writer) {
    // Copy the input stream to the output stream
    IOException exception = null;
    char[]      buffer    = new char[INPUT_BUFFER_SIZE];
    int         len;

    while (true) {
      try {
        len = reader.read(buffer);

        if (len == -1)
          break;

        writer.write(buffer, 0, len);
      } catch (IOException e) {
        exception   = e;
        break;
      }
    }

    return exception;
  }

  /**
   * Copy the contents of the specified input stream to the specified output stream, and
   * ensure that both streams are closed before returning (even in the face of an exception).
   *
   * @param istream The input stream to read from
   * @param ostream The output stream to write to
   * @param start Start of the range which will be copied
   * @param end End of the range which will be copied
   *
   * @return Exception which occurred during processing
   */
  protected IOException copyRange(InputStream istream, ServletOutputStream ostream, long start,
                                  long end) {
    if (log.isTraceEnabled())
      log.trace("Serving bytes:" + start + "-" + end);

    try {
      istream.skip(start);
    } catch (IOException e) {
      return e;
    }

    IOException exception   = null;
    long        bytesToRead = end - start + 1;

    byte[]      buffer      = new byte[INPUT_BUFFER_SIZE];
    int         len         = buffer.length;

    while ((bytesToRead > 0) && (len >= buffer.length)) {
      try {
        len = istream.read(buffer);

        if (bytesToRead >= len) {
          ostream.write(buffer, 0, len);
          bytesToRead -= len;
        } else {
          ostream.write(buffer, 0, (int) bytesToRead);
          bytesToRead = 0;
        }
      } catch (IOException e) {
        exception   = e;
        len         = -1;
      }

      if (len < buffer.length)
        break;
    }

    return exception;
  }

  /**
   * Copy the contents of the specified input stream to the specified output stream, and
   * ensure that both streams are closed before returning (even in the face of an exception).
   *
   * @param reader The reader to read from
   * @param writer The writer to write to
   * @param start Start of the range which will be copied
   * @param end End of the range which will be copied
   *
   * @return Exception which occurred during processing
   */
  protected IOException copyRange(Reader reader, PrintWriter writer, long start, long end) {
    try {
      reader.skip(start);
    } catch (IOException e) {
      return e;
    }

    IOException exception   = null;
    long        bytesToRead = end - start + 1;

    char[]      buffer      = new char[INPUT_BUFFER_SIZE];
    int         len         = buffer.length;

    while ((bytesToRead > 0) && (len >= buffer.length)) {
      try {
        len = reader.read(buffer);

        if (bytesToRead >= len) {
          writer.write(buffer, 0, len);
          bytesToRead -= len;
        } else {
          writer.write(buffer, 0, (int) bytesToRead);
          bytesToRead = 0;
        }
      } catch (IOException e) {
        exception   = e;
        len         = -1;
      }

      if (len < buffer.length)
        break;
    }

    return exception;
  }

  public static abstract class Resource {
    private final String name;
    private final long      contentLength;
    private final long      lastModified;
    private final String    contentType;
    private final String    lastModifiedHttp;

    public Resource(String name, String contentType, long contentLength, long lastModified) {
      this.name = name;
      this.contentType = (contentType == null) ? guessContentType(name) : contentType;
      this.contentLength = contentLength;
      this.lastModified = lastModified;

      //RFC 1123 date. eg. Tue, 20 May 2008 13:45:26 GMT and always in English
      SimpleDateFormat fmt = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
      fmt.setTimeZone(TimeZone.getTimeZone("GMT"));
      lastModifiedHttp = fmt.format(new Date(lastModified));
    }

    // Copied from Struts FilterDispatcher
    public static String guessContentType(String name) {
      // NOT using the code provided activation.jar to avoid adding yet another dependency
      // this is generally OK, since these are the main files we server up
      if (name.endsWith(".js")) {
        return "text/javascript";
      } else if (name.endsWith(".css")) {
        return "text/css";
      } else if (name.endsWith(".jpg") || name.endsWith(".jpeg")) {
        return "image/jpeg";
      } else if (name.endsWith(".png")) {
        return "image/png";
      } else if (name.endsWith(".gif")) {
        return "image/gif";
      } else if (name.endsWith(".html")) {
        return "text/html";
      } else if (name.endsWith(".txt")) {
        return "text/plain";
      } else {
        return null;
      }
    }

    public String getContentType() {
      return contentType;
    }

    public long getContentLength() {
      return contentLength;
    }

    public abstract InputStream streamContent() throws IOException;

    public abstract byte[] getContent();

    public long getLastModified() {
      return lastModified;
    }

    public String getLastModifiedHttp() {
      return lastModifiedHttp;
    }

    public String toString() {
      return "Resource[name=" + name +
             ", contentType=" + contentType +
             ", contentLength=" + contentLength +
             ",lastModified=" + lastModified +
             "(" + lastModifiedHttp + ")]";
    }
  }

  public static class FileResource extends Resource {

    private final File file;

    public FileResource(File file) {
      super(file.getName(), guessContentType(file.getName()), file.length(), file.lastModified());
      this.file = file;
    }

    public InputStream streamContent() throws IOException {
      return new FileInputStream(file);
    }

    public byte[] getContent() {
      return null;
    }
  }


  public static class URLResource extends Resource {
    private final URL url;

    public URLResource(URL url) throws IOException {
      this(url, url.openConnection());
    }

    private URLResource(URL url, URLConnection con) {
      super(url.toString(), urlContentType(url, con), con.getContentLength(), con.getLastModified());
      this.url = url;
    }

    private static String urlContentType(URL url, URLConnection con) {
      //XXX: guess first and then look in con
      String contentType = guessContentType(url.toString());
      if (contentType == null)
        contentType = con.getContentType();
      return contentType;
    }

    public InputStream streamContent() throws IOException {
      return url.openStream();
    }

    public byte[] getContent() {
      return null;
    }
  }

  protected static class Range {
    public long start;
    public long end;
    public long length;

    /**
     * Validate range.
     *
     * @return true if this is a valid range
     */
    public boolean validate() {
      if (end >= length)
        end = length - 1;

      return ((start >= 0) && (end >= 0) && (start <= end) && (length > 0));
    }

    public void recycle() {
      start    = 0;
      end      = 0;
      length   = 0;
    }
  }
}
