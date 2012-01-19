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

package org.topazproject.ambra.models;

import org.topazproject.otm.annotations.Entity;
import org.topazproject.otm.annotations.Predicate;
import org.topazproject.otm.annotations.UriPrefix;

/**
 * This defines a user's profile in Ambra. It is modeled on <a
 * href="http://xmlns.com/foaf/0.1/">foaf</a> and <a href="http://vocab.org/bio/0.1/">bio</a>,
 * consisting of a subset of the <var>foaf:Person</var> and <var>bio:*</var> plus a number topaz
 * specific additions.
 *
 * <p>Schema's borrowed from:
 * <dl>
 *   <dt>foaf
 *   <dd><a href="http://xmlns.com/foaf/0.1/">http://xmlns.com/foaf/0.1/</a></dd>
 *   <dt>bio
 *   <dd><a href="http://vocab.org/bio/0.1/">http://vocab.org/bio/0.1/</a></dd>
 *   <dt>address
 *   <dd><a href="http://rdfweb.org/topic/AddressVocab">http://rdfweb.org/topic/AddressVocab</a></dd>
 * </dl>
 *
 * @author Ronald Tschalär
 */
@UriPrefix("topaz:")
@Entity(graph = "profiles")
public class UserProfile extends FoafPerson {
  private static final long serialVersionUID = -4974622670706984079L;

  /** Biography namespace */
  public static final String BIO_URI  = "http://purl.org/vocab/bio/0.1/";
  /** FOAF namespace */
  public static final String ADDR_URI = "http://wymiwyg.org/ontologies/foaf/postaddress#";
  /** PIM namespace */
  public static final String contact = "http://www.w3.org/2000/10/swap/pim/contact#";

  private String displayName;
  private String suffix;
  private String positionType;
  private String organizationName;
  private String organizationType;
  private boolean organizationVisibility;
  private String postalAddress;
  private String city;
  private String country;
  private String biography;
  private String biographyText;
  private String interestsText;
  private String researchAreasText;


  public UserProfile clone() {
    return (UserProfile) super.clone();
  }

  /**
   * Get the name to use for display on the site.
   *
   * @return the display name, or null
   */
  public String getDisplayName() {
    return displayName;
  }

  /**
   * Set the name to use for display on the site.
   *
   * @param displayName the display name; may be null
   */
  @Predicate
  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  /**
   * Get the name suffix ("Jr", "Sr" etc.)
   *
   * @return the suffix or null
   */
  public String getSuffix() {
    return suffix;
  }

  /**
   * Set the name suffix ("Jr", "Sr" etc)
   *
   * @param suffix the suffix name; may be null
   */
  @Predicate(uri = "contact:personalSuffix")
  public void setSuffix(String suffix) {
    this.suffix = suffix;
  }

  /**
   * Get the organizational position type.
   *
   * @return the position type.
   */
  public String getPositionType() {
    return positionType;
  }

  /**
   * Set the organizational position type.
   *
   * @param positionType the position type.
   */
  @Predicate
  public void setPositionType(String positionType) {
    this.positionType = positionType;
  }

  /**
   * Get the organization name.
   *
   * @return the organization name.
   */
  public String getOrganizationName() {
    return organizationName;
  }

  /**
   * Set the organization name.
   *
   * @param organizationName the organization name.
   */
  @Predicate
  public void setOrganizationName(String organizationName) {
    this.organizationName = organizationName;
  }

  /**
   * Get the organization type.
   *
   * @return the organization type.
   */
  public String getOrganizationType() {
    return organizationType;
  }

  /**
   * Set the organization type.
   *
   * @param organizationType the organization type.
   */
  @Predicate
  public void setOrganizationType(String organizationType) {
    this.organizationType = organizationType;
  }

  /**
   * Get the organization visibility.
   *
   * @return the organization visibility.
   */
  public boolean getOrganizationVisibility() {
    return organizationVisibility;
  }

  /**
   * Set the organization visibility.
   *
   * @param visibility set the organization visibility.
   */
  public void setOrganizationVisibility(boolean visibility) {
    this.organizationVisibility = visibility;
  }


  /**
   * Get the postal address.
   *
   * @return the postal address.
   */
  public String getPostalAddress() {
    return postalAddress;
  }

  /**
   * Set the postal address.
   *
   * @param postalAddress the postal address.
   */
  @Predicate
  public void setPostalAddress(String postalAddress) {
    this.postalAddress = postalAddress;
  }

  /**
   * Get the city.
   *
   * @return the city.
   */
  public String getCity() {
    return city;
  }

  /**
   * Set the city.
   *
   * @param city the city.
   */
  @Predicate(uri = "address:town")
  public void setCity(String city) {
    this.city = city;
  }

  /**
   * Get the country.
   *
   * @return the country.
   */
  public String getCountry() {
    return country;
  }

  /**
   * Set the country.
   *
   * @param country the country.
   */
  @Predicate(uri = "address:country")
  public void setCountry(String country) {
    this.country = country;
  }

  /**
   * Get the url of the user's biography.
   *
   * @return the biography url, or null
   */
  public String getBiography() {
    return biography;
  }

  /**
   * Set the url of the user's biography.
   *
   * @param biography the biography url; may be null
   */
  @Predicate(uri = "bio:olb")
  public void setBiography(String biography) {
    this.biography = biography;
  }

  /**
   * Get the text biography.
   *
   * @return the biographyText.
   */
  public String getBiographyText() {
    return biographyText;
  }

  /**
   * Set the text description of the biography.
   *
   * @param biographyText the text description of the biography.
   */
  @Predicate(uri = "topaz:bio")
  public void setBiographyText(String biographyText) {
    this.biographyText = biographyText;
  }

  /**
   * Get the text description of the interests.
   *
   * @return the text description of the interests.
   */
  public String getInterestsText() {
    return interestsText;
  }

  /**
   * Set the text description of the interests.
   *
   * @param interestsText the text description of the interests.
   */
  @Predicate(uri = "topaz:interests")
  public void setInterestsText(String interestsText) {
    this.interestsText = interestsText;
  }

  /**
   * Get the text description of the researchAreas.
   *
   * @return the text description of the research areas.
   */
  public String getResearchAreasText() {
    return researchAreasText;
  }

  /**
   * Set the text description of the researchAreas.
   *
   * @param researchAreasText the text description of the research areas.
   */
  @Predicate(uri = "topaz:researchAreas")
  public void setResearchAreasText(String researchAreasText) {
    this.researchAreasText = researchAreasText;
  }
}
