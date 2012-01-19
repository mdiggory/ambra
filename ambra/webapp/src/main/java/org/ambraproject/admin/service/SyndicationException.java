package org.ambraproject.admin.service;

/**
 *
 * @author Alex Kudlick  11/18/11
 */
public class SyndicationException extends RuntimeException {
  public SyndicationException() {
  }

  public SyndicationException(String message) {
    super(message);
  }

  public SyndicationException(String message, Throwable cause) {
    super(message, cause);
  }

  public SyndicationException(Throwable cause) {
    super(cause);
  }
}
