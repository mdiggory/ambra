/* $$HeadURL::                                                                            $$
 * $$Id$$
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

package org.ambraproject.admin.action;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Required;

import org.ambraproject.annotation.service.AnnotationConverter;
import org.ambraproject.annotation.service.AnnotationService;
import org.ambraproject.annotation.service.WebAnnotation;
import org.topazproject.ambra.models.ArticleAnnotation;
import org.topazproject.ambra.models.ArticleContributor;
import org.ambraproject.admin.service.CitationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@SuppressWarnings("serial")
public class ManageAnnotationAction extends BaseAdminActionSupport {
  private static final Logger log = LoggerFactory.getLogger(ManageAnnotationAction.class);

  private String        annotationId;
  private WebAnnotation annotation;
  private String        annotationBody;
  private String        annotationContext;
  private String        citationId;
  private String        citationDisplayYear;
  private String        citationVolumeNumber;
  private String        citationIssue;
  private String        citationTitle;
  private String        citationELocationId;
  private String        citationJournal;
  private String        citationDoi;
  private String[]      citationAuthorIds;
  private String[]      citationAuthorGivenNames;
  private String[]      citationAuthorSurnames;
  private String[]      citationAuthorSuffixes;
  private int           citationAuthorDeleteIndex;
  private String[]      citationCollaborativeAuthorNames;
  private int           citationCollaborativeAuthorDeleteIndex;

  private AnnotationService annotationService;
  private AnnotationConverter annotationConverter;
  private CitationService citationService;

  @Override
  public String execute() throws Exception {
    if (setCommonFields())
      return ERROR;
    return SUCCESS;  // default action is just to display the template
  }

  /**
   * Struts Action to load (query from the database) an Annotation and its Citation,
   * if a Citation exists.
   *
   * @return the struts status
   * @throws Exception on an error
   */
  @Transactional(readOnly = true)
  public String loadAnnotation() throws Exception {
    if (annotationId == null || annotationId.length() < 1) {
      addActionError("Please enter an Annotation ID");
      return ERROR;
    }
    try {
      ArticleAnnotation a = annotationService.getArticleAnnotation(annotationId);
      annotation = annotationConverter.convert(a, true, true);
      if (annotationId == null)
        annotationId = annotation.getId();
      if (annotationBody == null)
        annotationBody = annotation.getOriginalBodyContent(); // Avoids character encoding issues.
      if (annotationContext == null)
        annotationContext = annotation.getContext();
      if (annotation.getCitation() != null) {
        if (citationId == null && annotation.getCitation().getId() != null)
          citationId = annotation.getCitation().getId().toString();
        if (citationDisplayYear == null)
          citationDisplayYear = annotation.getCitation().getDisplayYear();
        if (citationVolumeNumber == null) {
          if (annotation.getCitation().getVolumeNumber() != null)
            citationVolumeNumber = annotation.getCitation().getVolumeNumber().toString();
          if (citationVolumeNumber == null || citationVolumeNumber.trim().length() < 1)
            citationVolumeNumber = annotation.getCitation().getVolume();
        }
        if (citationIssue == null)
          citationIssue = annotation.getCitation().getIssue();
        if (citationTitle == null)
          citationTitle = annotation.getCitation().getTitle();
        if (citationELocationId == null)
          citationELocationId = annotation.getCitation().getELocationId();
        if (citationJournal == null)
          citationJournal = annotation.getCitation().getJournal();
        if (citationDoi == null)
          citationDoi = annotation.getCitation().getDoi();

        if (annotation.getCitation().getAnnotationArticleAuthors() != null
            && annotation.getCitation().getAnnotationArticleAuthors().size() > 0) {
          citationAuthorIds = new String[annotation.getCitation().getAnnotationArticleAuthors().size()];
          citationAuthorGivenNames = new String[annotation.getCitation().getAnnotationArticleAuthors().size()];
          citationAuthorSurnames = new String[annotation.getCitation().getAnnotationArticleAuthors().size()];
          citationAuthorSuffixes = new String[annotation.getCitation().getAnnotationArticleAuthors().size()];
          int authorIndex = 0;
          for (ArticleContributor author : annotation.getCitation().getAnnotationArticleAuthors()) {
            if (author.getId() != null) {
              citationAuthorIds[authorIndex] = author.getId().toString();
              citationAuthorGivenNames[authorIndex] = author.getGivenNames();
              citationAuthorSurnames[authorIndex] = author.getSurnames();
              citationAuthorSuffixes[authorIndex] = author.getSuffix();
              authorIndex++;
            }
          }
        }
        if (annotation.getCitation().getCollaborativeAuthors() != null
            && annotation.getCitation().getCollaborativeAuthors().size() > 0) {
          citationCollaborativeAuthorNames
              = new String[annotation.getCitation().getCollaborativeAuthors().size()];
          int authorIndex = 0;
          for (String author : annotation.getCitation().getCollaborativeAuthors()) {
            citationCollaborativeAuthorNames[authorIndex] = author;
            authorIndex++;
          }
        }
      }

    } catch (IllegalArgumentException iae) {
      addActionError("There is no Annotation with the ID: " + annotationId);
      return ERROR;
    }
    // tell Struts to continue
    return SUCCESS;
  }

  /**
   * Struts Action to save an Annotation and its Citation (if the Annotation has a Citation).
   *
   * @return the struts status
   * @throws Exception on an error
   */
  @Transactional(rollbackFor = { Throwable.class })
  public String saveAnnotation() throws Exception {
    annotationService.updateBodyAndContext(annotationId, annotationBody, annotationContext, getAuthId());
    addActionMessage("Annotation Context is now: " + annotationContext);
    addActionMessage("Annotation Body is now: " + annotationBody);

    //  If there is a Citation, update its information.
    if (citationId != null && citationId.trim().length() > 0) {
      citationService.updateCitation(citationId, citationTitle, citationDisplayYear,
          citationJournal, citationVolumeNumber, citationIssue, citationELocationId, citationDoi);
      if (citationDisplayYear != null)
        addActionMessage("Citation Display Year is now: " + citationDisplayYear);
      if (citationVolumeNumber != null)
        addActionMessage("Citation Volume Number is now: " + citationVolumeNumber);
      if (citationIssue != null)
        addActionMessage("Citation Issue is now: " + citationIssue);
      if (citationTitle != null)
        addActionMessage("Citation Title is now: " + citationTitle);
      if (citationELocationId != null)
        addActionMessage("Citation eLocationId is now: " + citationELocationId);
      if (citationJournal != null)
        addActionMessage("Citation Journal is now: " + citationJournal);
      if (citationDoi != null)
        addActionMessage("Citation DOI is now: " + citationDoi);

      //  Update all existing Citation Authors.
      if (citationAuthorSurnames.length > 1) {
        for (int i=0; i < citationAuthorSurnames.length - 1; i++) {
          citationService.updateAnnotationAuthor(citationAuthorIds[i].trim(), citationAuthorSurnames[i].trim(),
              citationAuthorGivenNames[i].trim(), citationAuthorSuffixes[i].trim());
          if (i != citationAuthorDeleteIndex) { // No "updated" message for deleted user.
            addActionMessage("Updated citation author: " + citationAuthorGivenNames[i].trim() + " "
                + citationAuthorSurnames[i].trim() + " " + citationAuthorSuffixes[i].trim());
          }
        }
      }

      //  Delete a Citation Author if a "Delete Author" link has been clicked.
      if (citationAuthorDeleteIndex > -1) {
        citationService.deleteAnnotationAuthor(citationId, citationAuthorIds[citationAuthorDeleteIndex]);
        addActionMessage("Deleted citation author: "
            + citationAuthorGivenNames[citationAuthorDeleteIndex].trim()+ " "
            + citationAuthorSurnames[citationAuthorDeleteIndex].trim() + " "
            + citationAuthorSuffixes[citationAuthorDeleteIndex].trim());
      }

      //  Add a new Citation Author, if one exists.
      if (citationAuthorGivenNames[citationAuthorSurnames.length - 1].trim().length() > 0
          || citationAuthorSurnames[citationAuthorSurnames.length - 1].trim().length() > 0) {
        citationService.addAnnotationAuthor(citationId,
            citationAuthorSurnames[citationAuthorSurnames.length - 1].trim(),
            citationAuthorGivenNames[citationAuthorSurnames.length - 1].trim(),
            citationAuthorSuffixes[citationAuthorSurnames.length - 1].trim());
        addActionMessage("Added citation author: "
            + citationAuthorGivenNames[citationAuthorSurnames.length - 1].trim() + " "
            + citationAuthorSurnames[citationAuthorSurnames.length - 1].trim() + " "
            + citationAuthorSuffixes[citationAuthorSurnames.length - 1].trim());
      }
      citationAuthorIds = null;
      citationAuthorGivenNames = null;
      citationAuthorSurnames = null;
      citationAuthorSuffixes = null;


      //  Update all existing Collaborative Authors.
      if (citationCollaborativeAuthorNames.length > 0) {
        for (int i=0; i < citationCollaborativeAuthorNames.length - 1; i++) {
          citationService.updateCollaborativeAuthor(
              citationId, i, citationCollaborativeAuthorNames[i].trim());
          if (i != citationCollaborativeAuthorDeleteIndex) { // No "updated" message for deleted user.
            addActionMessage("Updated collaborative author: "
                + citationCollaborativeAuthorNames[i].trim());
          }
        }
      }

      //  Delete a Collaborative Author if a "Delete Collaborative Author" link has been clicked.
      if (citationCollaborativeAuthorDeleteIndex > -1) {
        citationService.deleteCollaborativeAuthor(
            citationId, citationCollaborativeAuthorDeleteIndex);
        addActionMessage("Deleted collaborative author: "
            + citationCollaborativeAuthorNames[citationCollaborativeAuthorDeleteIndex].trim());
      }

      //  Add a new Collaborative Author, if one exists.
      if (citationCollaborativeAuthorNames[citationCollaborativeAuthorNames.length - 1].trim()
          .length() > 0) {
        citationService.addCollaborativeAuthor(citationId,
            citationCollaborativeAuthorNames[citationCollaborativeAuthorNames.length - 1].trim());
        addActionMessage("Added collaborative author: "
            + citationCollaborativeAuthorNames[citationCollaborativeAuthorNames.length - 1].trim());
      }
      citationCollaborativeAuthorNames = null;
    }  //  End if citation exists.

    return loadAnnotation();  //  returns SUCCESS if the Annotation query succeeded.
  }

  /**
   * Perform basic action necessary for the querying and display of data through this Action class.
   * @return Always return false.
   */
  private boolean setCommonFields() {
    initJournal(); // create a faux journal object for template
    return false;
  }


  /**
   * Get the Annotation that is to be edited.
   * @return the Annotation that is to be edited
   */
  public WebAnnotation getAnnotation() {
    return annotation;
  }

  /**
   * Get Annotation Id.
   * @return the annotation id
   */
  public String getAnnotationId() {
    return annotationId;
  }
  /**
   * Set the Annotation ID.
   * @param annotationId the annotation ID that is being operated upon.
   */
  public void setAnnotationId(String annotationId) {
    this.annotationId = annotationId;
  }

  /**
   * Get the body of the Annotation.
   * @return The annotation body
   */
  public String getAnnotationBody() {
    return annotationBody;
  }
  /**
   * Set the body of this Annotation.
   * @param annotationBody The body of this Annotation
   */
  public void setAnnotationBody(String annotationBody) {
    this.annotationBody = annotationBody;
  }

  /**
   * Get Annotation Context, meaning the x-pointer location of this Annotation inside its Article.
   * @return The annotation context, the x-pointer location of this Annotation inside its Article
   */
  public String getAnnotationContext() {
    return annotationContext;
  }
  /**
   * Set the Annotation Context, the x-pointer location of this Annotation inside its Article.
   * @param annotationContext The x-pointer location of this Annotation inside it Article
   */
  public void setAnnotationContext(String annotationContext) {
    this.annotationContext = annotationContext;
  }

  /**
   * Get Citation Id.
   * @return the ID of this Citation
   */
  public String getCitationId() {
    return citationId;
  }
  /**
   * Set the Citation ID.
   * @param citationId the ID of this Citation
   */
  public void setCitationId(String citationId) {
    this.citationId = citationId;
  }

  /**
   * Get the Display Year of this Annotation Citation.
   * @return The Display Year of this Annotation Citation
   */
  public String getCitationDisplayYear() {
    return citationDisplayYear;
  }
  /**
   * Set the Display Year of this Annotation Citation.
   * @param citationDisplayYear The Display Year of this Annotation Citation
   */
  public void setCitationDisplayYear(String citationDisplayYear) {
    this.citationDisplayYear = citationDisplayYear;
  }

  /**
   * Get the Volume Number of the Article to which this Annotation Citation is relevant.
   * @return The Volume Number of the Article to which this Annotation Citation is relevant
   */
  public String getCitationVolumeNumber() {
    return citationVolumeNumber;
  }
  /**
   * Set the Volume Number of the Article to which this Annotation Citation is relevant.
   * @param citationVolumeNumber The Volume Number of the Article to
   *   which this Annotation Citation is relevant
   */
  public void setCitationVolumeNumber(String citationVolumeNumber) {
    this.citationVolumeNumber = citationVolumeNumber;
  }

  /**
   * Get the Issue of the Article to which this Annotation Citation is relevant.
   * @return The Issue of the Article to which this Annotation Citation is relevant
   */
  public String getCitationIssue() {
    return citationIssue;
  }
  /**
   * Set the Issue of the Article to which this Annotation Citation is relevant.
   * @param citationIssue The Issue of the Article to
   *   which this Annotation Citation is relevant
   */
  public void setCitationIssue(String citationIssue) {
    this.citationIssue = citationIssue;
  }

  /**
   * Get the Title of this Annotation Citation.
   * @return The Title of this Annotation Citation
   */
  public String getCitationTitle() {
    return citationTitle;
  }
  /**
   * Set the Title of this Annotation Citation.
   * @param citationTitle The Title of this Annotation Citation
   */
  public void setCitationTitle(String citationTitle) {
    this.citationTitle = citationTitle;
  }

  /**
   * Get the eLocationId for this Annotation Citation.
   * @return The eLocationId for this Annotation Citation
   */
  public String getCitationELocationId() {
    return citationELocationId;
  }
  /**
   * Set the eLocationId for this Annotation Citation.
   * @param citationELocationId The eLocationId for this Annotation Citation
   */
  public void setCitationELocationId(String citationELocationId) {
    this.citationELocationId = citationELocationId;
  }

  /**
   * Get the Journal of the Article to which this Annotation Citation is relevant.
   * @return The Journal of the Article to which this Annotation Citation is relevant
   */
  public String getCitationJournal() {
    return citationJournal;
  }
  /**
   * Set the Journal of the Article to which this Annotation Citation is relevant.
   * @param citationJournal The Journal of the Article to
   *   which this Annotation Citation is relevant
   */
  public void setCitationJournal(String citationJournal) {
    this.citationJournal = citationJournal;
  }

  /**
   * Get the DOI of this Annotation Citation.
   * @return The DOI of this Annotation Citation
   */
  public String getCitationDoi() {
    return citationDoi;
  }
  /**
   * Set the DOI of this Annotation Citation.
   * @param citationDoi The DOI of this Annotation Citation
   */
  public void setCitationDoi(String citationDoi) {
    this.citationDoi = citationDoi;
  }

  /**
   * Get all of the IDs for the Authors of this Citation.
   * @return the IDs for the Authors of this Citation
   */
  public String[] getCitationAuthorIds() {
    return citationAuthorIds;
  }
  /**
   * Set all of the IDs for the Authors of this Citation.
   * @param citationAuthorIds the IDs for the Authors of this Citation
   */
  public void setCitationAuthorIds(String[] citationAuthorIds) {
    this.citationAuthorIds = citationAuthorIds;
  }

  /**
   * Get all of the Given Names for the Authors of this Citation.
   * @return the Given Names for the Authors of this Citation
   */
  public String[] getCitationAuthorGivenNames() {
    return citationAuthorGivenNames;
  }
  /**
   * Set all of the Given Names for the Authors of this Citation.
   * @param citationAuthorGivenNames the Given Names for the Authors of this Citation
   */
  public void setCitationAuthorGivenNames(String[] citationAuthorGivenNames) {
    this.citationAuthorGivenNames = citationAuthorGivenNames;
  }

  /**
   * Get all of the Surnames for the Authors of this Citation.
   * @return the Surnames for the Authors of this Citation
   */
  public String[] getCitationAuthorSurnames() {
    return citationAuthorSurnames;
  }
  /**
   * Set all of the Surnames for the Authors of this Citation.
   * @param citationAuthorSurnames the Surnames for the Authors of this Citation
   */
  public void setCitationAuthorSurnames(String[] citationAuthorSurnames) {
    this.citationAuthorSurnames = citationAuthorSurnames;
  }

  /**
   * Get all of the name Suffixes for the Authors of this Citation.
   * @return the name Suffixes for the Authors of this Citation
   */
  public String[] getCitationAuthorSuffixes() {
    return citationAuthorSuffixes;
  }
  /**
   * Set all of the name Suffixes for the Authors of this Citation.
   * @param citationAuthorSuffixes the name Suffixes for the Authors of this Citation
   */
  public void setCitationAuthorSuffixes(String[] citationAuthorSuffixes) {
    this.citationAuthorSuffixes = citationAuthorSuffixes;
  }

  /**
   * Get the Index of the Citation Author which is to be deleted from this Citation.
   * @return The Index of the Citation Author to be deleted
   */
  public int getCitationAuthorDeleteIndex() {
    return citationAuthorDeleteIndex;
  }
  /**
   * Set the Index of the Citation Author which is to be deleted from this Citation.
   * @param citationAuthorDeleteIndex The Index of the Citation Author to be deleted
   */
  public void setCitationAuthorDeleteIndex(int citationAuthorDeleteIndex) {
    this.citationAuthorDeleteIndex = citationAuthorDeleteIndex;
  }

  /**
   * Get all of the Names for the Collaborative Authors for this Citation.
   * @return the Names for the Collaborative Authors for this Citation
   */
  public String[] getCitationCollaborativeAuthorNames() {
    return citationCollaborativeAuthorNames;
  }
  /**
   * Set all of the Names for the Collaborative Authors for this Citation.
   * @param citationCollaborativeAuthorNames Names for the Collaborative Authors for this Citation
   */
  public void setCitationCollaborativeAuthorNames(String[] citationCollaborativeAuthorNames) {
    this.citationCollaborativeAuthorNames = citationCollaborativeAuthorNames;
  }

  /**
   * Get the Index of the Collaborative Author which is to be deleted from this Citation.
   * @return The Index of the Collaborative Author to be deleted
   */
  public int getCitationCollaborativeAuthorDeleteIndex() {
    return citationCollaborativeAuthorDeleteIndex;
  }
  /**
   * Set the Index of the Collaborative Author which is to be deleted from this Citation.
   * @param citationCollaborativeAuthorDeleteIndex Index of the Collaborative Author to be deleted
   */
  public void setCitationCollaborativeAuthorDeleteIndex(int citationCollaborativeAuthorDeleteIndex){
    this.citationCollaborativeAuthorDeleteIndex = citationCollaborativeAuthorDeleteIndex;
  }


  /**
   * Set AnnotationService.
   * @param annotationService the annotation service to set
   */
  public void setAnnotationService(AnnotationService annotationService) {
    this.annotationService = annotationService;
  }

  @Required
  public void setAnnotationConverter(AnnotationConverter converter) {
    this.annotationConverter = converter;
  }

  /**
   * Set CitationService.
   * @param citationService the citation service to set
   */
  public void setCitationService(CitationService citationService) {
    this.citationService = citationService;
  }

}
