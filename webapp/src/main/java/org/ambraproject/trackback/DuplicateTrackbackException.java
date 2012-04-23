package org.ambraproject.trackback;

/**
 * Exception indicating that a trackback for an article with the given blog url already exists
 *
 * @author Alex Kudlick 4/4/12
 */
public class DuplicateTrackbackException extends Exception {

  public DuplicateTrackbackException(String articleDoi, String url) {
    super("A trackback for article " + articleDoi + " and url " + url + " already exists");
  }
}
