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
package org.topazproject.ambra.email.impl;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.mail.MailPreparationException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import org.topazproject.ambra.email.TemplateMailer;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 * Freemarker template based emailer.
 */
public class FreemarkerTemplateMailer implements TemplateMailer {
  private JavaMailSender mailSender;
  private Configuration configuration;
  private String fromEmailAddress;
  private String fromEmailName;

  private final String MIME_TYPE_TEXT_PLAIN = "text/plain";
  private final String MIME_TYPE_TEXT_HTML = "text/html";
  private final Map<String, String> mailContentTypes = new HashMap<String, String>();
  {
    mailContentTypes.put(MIME_TYPE_TEXT_PLAIN, "text");
    mailContentTypes.put(MIME_TYPE_TEXT_HTML, "HTML");
  }

  private static final String TEXT = "text";
  private static final String HTML = "html";
  private static final String SUBJECT = "subject";

  public static final String TO_EMAIL_ADDRESS = "toEmailAddress";
  public static final String USER_NAME_KEY = "name";
  private static final Log log = LogFactory.getLog(FreemarkerTemplateMailer.class);

  /**
   * Mail the email formatted using the given templates
   * @param toEmailAddresses List of email addresses to which emails should be sent.  White space delimited.
   * @param fromEmailAddress fromEmailAddress
   * @param subject subject of the email
   * @param context context to set the values from for the template
   * @param textTemplateFilename textTemplateFilename
   * @param htmlTemplateFilename htmlTemplateFilename
   */
  public void mail(final String toEmailAddresses, final String fromEmailAddress, final String subject,
                   final Map<String, Object> context, final String textTemplateFilename,
                   final String htmlTemplateFilename) {
    final StringTokenizer emailTokens = new StringTokenizer(toEmailAddresses);

    while (emailTokens.hasMoreTokens()) {
      final String toEmailAddress = emailTokens.nextToken();
      final MimeMessagePreparator preparator = new MimeMessagePreparator() {
        public void prepare(final MimeMessage mimeMessage) throws MessagingException, IOException {
          final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true,
                                                                  configuration.getDefaultEncoding());
          message.setTo(new InternetAddress(toEmailAddress));
          message.setFrom(new InternetAddress(fromEmailAddress, (String) context.get(USER_NAME_KEY)));
          message.setSubject(subject);

          // Create a "text" Multipart message
          final Multipart mp = createPartForMultipart(textTemplateFilename, context, "alternative",
                                                      MIME_TYPE_TEXT_PLAIN + "; charset=" +
                                                      configuration.getDefaultEncoding());

          // Create a "HTML" Multipart message
          final Multipart htmlContent = createPartForMultipart(htmlTemplateFilename, context, "related",
                                                               MIME_TYPE_TEXT_HTML + "; charset=" +
                                                               configuration.getDefaultEncoding());

          final BodyPart htmlPart = new MimeBodyPart();
          htmlPart.setContent(htmlContent);
          mp.addBodyPart(htmlPart);

          mimeMessage.setContent(mp);
        }
      };
      mailSender.send(preparator);
      if (log.isDebugEnabled()) {
        log.debug("Mail sent to:" + toEmailAddress);
      }
    }
  }

  private Multipart createPartForMultipart(final String templateFilename, final Map<String, Object> context,
                                           final String multipartType, final String mimeType)
    throws IOException, MessagingException {
    final Multipart multipart = new MimeMultipart(multipartType);
    multipart.addBodyPart(createBodyPart(mimeType, templateFilename, context));
    return multipart;
  }

  private BodyPart createBodyPart(final String mimeType, final String htmlTemplateFilename,
                                  final Map<String, Object> context)
    throws IOException, MessagingException {
    final BodyPart htmlPage = new MimeBodyPart();
    final Template htmlTemplate = configuration.getTemplate(htmlTemplateFilename);
    final String encoding = configuration.getDefaultEncoding();
    final ByteArrayOutputStream outputStream = new ByteArrayOutputStream(100);
    final Writer writer = new BufferedWriter(new OutputStreamWriter(outputStream, encoding));
    htmlTemplate.setOutputEncoding(encoding);
    htmlTemplate.setEncoding(encoding);
    try {
      htmlTemplate.process(context, writer);
    } catch (TemplateException e) {
      throw new MailPreparationException("Can't generate " + mailContentTypes.get(mimeType) +
                                         " subscription mail", e);
    }

    htmlPage.setDataHandler(new BodyPartDataHandler(outputStream, mimeType));
    return htmlPage;
  }

  /**
   * @see org.topazproject.ambra.email.TemplateMailer#massMail(java.util.Map, java.lang.String,
   *      java.lang.String, java.lang.String)
   */
  public void massMail(final Map<String, Map<String, Object>> emailAddressContextMap,
                       final String subject, final String textTemplateFilename,
                       final String htmlTemplateFilename) {
    for (final Map.Entry<String, Map<String, Object>> entry : emailAddressContextMap.entrySet()) {
      mail(entry.getKey(), getFromEmailAddress(), subject, entry.getValue(), textTemplateFilename,
           htmlTemplateFilename);
    }
  }

  /**
   * Set the free marker configurer
   * @param freemarkerConfig freeMarkerConfigurer
   */
  public void setFreemarkerConfig(final FreeMarkerConfigurer freemarkerConfig) {
    this.configuration = freemarkerConfig.getConfiguration();
  }

  protected String getFromEmailName() {
    if (fromEmailName == null) {
      return "";
    }
    return fromEmailName;
  }

  /**
   * Set the from email name
   * @param fromEmailName fromEmailName
   */
  public void setFromEmailName(final String fromEmailName) {
    this.fromEmailName = fromEmailName;
  }

  public String getFromEmailAddress() {
    return fromEmailAddress;
  }

  /**
   * Set the from email address
   * @param fromEmailAddress fromEmailAddress
   */
  public void setFromEmailAddress(final String fromEmailAddress) {
    this.fromEmailAddress = fromEmailAddress;
  }

  /**
   * Set the mail sender
   * @param mailSender mailSender
   */
  public void setMailSender(final JavaMailSender mailSender) {
    this.mailSender = mailSender;
  }

  /**
   * @param toEmailAddress toEmailAddress
   * @param mapValues contains the url for verification and html + text template names
   */
  public void sendEmail(final String toEmailAddress, final Map<String, Object> mapValues) {
    sendEmail(toEmailAddress, getFromEmailAddress(), mapValues);
  }

  /**
   * @param toEmailAddress toEmailAddress
   * @param fromEmailAddress fromEmailAddress
   * @param mapValues contains the url for verification and html + text template names
   */
  public void sendEmail(final String toEmailAddress, final String fromEmailAddress,
                        final Map<String, Object> mapValues) {
    mail(toEmailAddress, fromEmailAddress, (String)mapValues.get(SUBJECT), mapValues,
         (String)mapValues.get(TEXT), (String)mapValues.get(HTML));
  }
}

class BodyPartDataHandler extends DataHandler {
  public BodyPartDataHandler(final ByteArrayOutputStream outputStream, final String contentType) {
    super(new DataSource() {
      public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(outputStream.toByteArray());
      }

      public OutputStream getOutputStream() throws IOException {
        throw new IOException("Read-only data");
      }

      public String getContentType() {
        return contentType;
      }

      public String getName() {
        return "main";
      }
    });
  }
}
