/* $HeadURL::                                                                                     $
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

package org.topazproject.ambra.models;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

import org.topazproject.otm.annotations.Entity;
import org.topazproject.otm.annotations.GeneratedValue;
import org.topazproject.otm.annotations.Id;
import org.topazproject.otm.annotations.Predicate;
import org.topazproject.otm.annotations.UriPrefix;

/**
 * A person according to foaf. This is not complete, but just the subset used by Ambra.
 *
 * @author Ronald Tschalär
 */
@UriPrefix("foaf:")
@Entity(types = {"foaf:Person"})
public abstract class FoafPerson implements Serializable, Cloneable {
  private static final long serialVersionUID = -7482176479835783037L;
  
  private URI      id;
  private String   realName;
  private String   givenNames;
  private String   surnames;
  private String   title;
  private String   gender;
  private URI      email;
  private URI      homePage;
  private URI      weblog;
  private URI      publications;
  private Set<URI> interests = new HashSet<URI>();

  public FoafPerson clone() {
    try {
      FoafPerson copy =  (FoafPerson) super.clone();
      copy.setInterests(new HashSet<URI>(getInterests()));
      return copy;
    } catch (CloneNotSupportedException e) {
      throw new Error("", e);
    }
  }

  /**
   * @return the id
   */
  public URI getId() {
    return id;
  }

  /**
   * @param id the id to set
   */
  @Id @GeneratedValue(uriPrefix = "id:profile/")
  public void setId(URI id) {
    this.id = id;
  }

  /**
   * Get the real name, usually as &lt;first&gt;, &lt;last&gt;.
   *
   * @return real name, or null
   */
  public String getRealName() {
    return realName;
  }

  /**
   * Set the real name, usually as &lt;first&gt;, &lt;last&gt;.
   *
   * @param realName the real name; may be null
   */
  @Predicate(uri = "foaf:name")
  public void setRealName(String realName) {
    this.realName = realName;
  }

  /**
   * Get the given names.
   *
   * @return the given names.
   */
  public String getGivenNames() {
    return givenNames;
  }

  /**
   * Set the given names.
   *
   * @param givenNames the given names.
   */
  @Predicate(uri = "foaf:givenname")
  public void setGivenNames(String givenNames) {
    this.givenNames = givenNames;
  }

  /**
   * Get the surnames.
   *
   * @return the surnames.
   */
  public String getSurnames() {
    return surnames;
  }

  /**
   * Set the surnames.
   *
   * @param surnames the surnames.
   */
  @Predicate(uri = "foaf:surname")
  public void setSurnames(String surnames) {
    this.surnames = surnames;
  }

  /**
   * Get the title (e.g. 'Mrs', 'Dr', etc).
   *
   * @return the title, or null
   */
  public String getTitle() {
    return title;
  }

  /**
   * Set the title (e.g. 'Mrs', 'Dr', etc).
   *
   * @param title the title; may be null
   */
  @Predicate
  public void setTitle(String title) {
    this.title = title;
  }

  /**
   * Get the gender. Valid values are 'male' and 'female'.
   *
   * @return the gender, or null
   */
  public String getGender() {
    return gender;
  }

  /**
   * Set the gender. Valid values are 'male' and 'female'.
   *
   * @param gender the gender; may be null
   */
  @Predicate
  public void setGender(String gender) {
    this.gender = gender;
  }

  /**
   * Get the email address.
   *
   * @return the email address.
   */
  public URI getEmail() {
    return email;
  }

  /**
   * Set the email address.
   *
   * @param email the email address.
   */
  @Predicate(uri = "foaf:mbox")
  public void setEmail(URI email) {
    this.email = email;
  }

  /**
   * Get the raw email address.
   *
   * @return the email address.
   */
  public String getEmailAsString() {
    return (email != null) ? email.getSchemeSpecificPart() : null;
  }

  /**
   * Set the raw email address.
   *
   * @param email the email address.
   */
  public void setEmailFromString(String email) {
    try {
      this.email = new URI("mailto", email, null);
    } catch (URISyntaxException use) {
      throw new RuntimeException("Unexpected exception creating mailto url from '" + email + "'",
                                 use);
    }
  }

  /**
   * Get the url of the user's homepage.
   *
   * @return the url of the homepage, or null.
   */
  public URI getHomePage() {
    return homePage;
  }

  /**
   * Set the url of the user's homepage.
   *
   * @param homePage the url of the homepage; may be null.
   */
  @Predicate(uri = "foaf:homepage")
  public void setHomePage(URI homePage) {
    this.homePage = homePage;
  }

  /**
   * Get the url of the user's blog.
   *
   * @return the url of the blog, or null.
   */
  public URI getWeblog() {
    return weblog;
  }

  /**
   * Set the url of the user's blog.
   *
   * @param weblog the url of the blog; may be null.
   */
  @Predicate
  public void setWeblog(URI weblog) {
    this.weblog = weblog;
  }

  /**
   * Get the url of the page listing the user's publications.
   *
   * @return the url, or null.
   */
  public URI getPublications() {
    return publications;
  }

  /**
   * Set the url of the page listing the user's publications.
   *
   * @param publications the url; may be null.
   */
  @Predicate
  public void setPublications(URI publications) {
    this.publications = publications;
  }

  /**
   * Get a list of url's, usually of webpages, representing the user's interests.
   *
   * @return the list of url's, or null.
   */
  public Set<URI> getInterests() {
    return interests;
  }

  /**
   * Set a list of url's, usually of webpages, representing the user's interests.
   *
   * @param interests the list of url's; may be null.
   */
  @Predicate(uri = "foaf:interest")
  public void setInterests(Set<URI> interests) {
    this.interests = interests;
  }
}
