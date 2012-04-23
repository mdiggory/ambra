
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

package org.ambraproject.article.service;

import org.ambraproject.ApplicationException;
import org.ambraproject.article.BrowseParameters;
import org.ambraproject.article.BrowseResult;
import org.ambraproject.article.action.TOCArticleGroup;
import org.ambraproject.model.IssueInfo;
import org.ambraproject.model.VolumeInfo;
import org.ambraproject.model.article.ArticleInfo;
import org.ambraproject.model.article.Years;
import org.apache.commons.configuration.Configuration;
import org.apache.solr.client.solrj.SolrServerException;
import org.topazproject.ambra.models.Issue;
import org.topazproject.ambra.models.Journal;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.SortedMap;

/**
 * Class to get all Articles in system and organize them by date and by category
 *
 * @author Alex Worden, stevec
 */
public interface BrowseService {

  /**
   * The map of sorts that are valid for this provider
   * @return
   */
  public List getSorts();

  /**
   * Get the dates of all articles with a <code>state</code> of <code>ACTIVE</code>
   * (meaning the articles have been published).
   * The outer map is a map of years, the next inner map a map
   * of months, and finally the innermost is a list of days.
   * <br/>
   *
   * @param journalKey The current journal
   *
   * @return the article dates.
   */
  public Years getArticleDatesForJournal(final String journalKey);

  /**
   * Get articles in the given category. One "page" of articles will be returned, i.e. articles
   * pageNum * pageSize .. (pageNum + 1) * pageSize - 1 . Note that less than a pageSize articles
   * may be returned, either because it's the end of the list or because some articles are not
   * accessible.
   *
   * @param browseParameters A collection filters / parameters to browse by
   * @return the articles.
   */
  public BrowseResult getArticlesBySubject(final BrowseParameters browseParameters);

  /**
   * Get articles in the given date range, from newest to oldest, of the given article type(s).
   * One "page" of articles will be returned,
   * i.e. articles pageNum * pageSize .. (pageNum + 1) * pageSize - 1 .
   * Note that less than a pageSize articles may be returned, either because it's the end
   * of the list or because some articles are not accessible.
   * <p/>
   * Note: this method assumes the dates are truly just dates, i.e. no hours, minutes, etc.
   * <p/>
   * If the <code>articleTypes</code> parameter is null or empty,
   * then all types of articles are returned.
   * <p/>
   * This method should never return null.
   *
   * @param browseParameters A collection filters / parameters to browse by
   * @return the articles.
   */
  public BrowseResult getArticlesByDate(final BrowseParameters browseParameters);

  /**
   * Get a list of article-counts for each category.
   *
   * @param journalKey the current journal
   *
   * @return the category infos.
   */
  public SortedMap<String, Long> getSubjectsForJournal(final String journalKey);

  /**
   * Get Issue information.
   *
   * @param issueDoi DOI of Issue.
   * @return the Issue information.
   */
  public IssueInfo getIssueInfo(final URI issueDoi);

  /**
   * Return the ID of the latest issue from the latest volume.
   * If no issue exists in the latest volume, then look at the previous volume and so on.
   * The Current Issue for each Journal should be configured via the admin console.
   * This method is a reasonable way to get the most recent Issue if Current Issue was not set.
   *
   * @param journal The journal in which to seek the most recent Issue
   * @return The most recent Issue from the most recent Volume, or null if there are no Issues
   */
  public URI getLatestIssueFromLatestVolume(Journal journal);

  /**
   * Returns the list of ArticleInfos contained in this Issue. The list will contain only
   * ArticleInfos for Articles that the current user has permission to view.
   *
   * @param issueDOI Issue ID
   * @param authId the AuthId of the current user
   * @return List of ArticleInfo objects.
   */
  public List<ArticleInfo> getArticleInfosForIssue(final URI issueDOI, String authId);

  /**
   * Get a VolumeInfo for the given id. This only works if the volume is in the current journal.
   *
   * @param id Volume ID
   * @param journalKey the current journal
   * @return VolumeInfo
   */
  public VolumeInfo getVolumeInfo(URI id, String journalKey);

  /**
   * Returns a list of VolumeInfos for the given Journal. VolumeInfos are sorted in reverse order
   * to reflect most common usage. Uses the pull-through cache.
   *
   * @param journal To find VolumeInfos for.
   * @return VolumeInfos for journal in reverse order.
   */
  public List<VolumeInfo> getVolumeInfosForJournal(final Journal journal);

  /**
   * Given a list of Article Groups with correctly ordered articles
   * create a CSV string of article URIs. The URIs will be in the
   * order that they show up on the TOC.
   *
   * @param  articleGroups the list of TOCArticleGroup to process.
   * @return a string of a comma separated list of article URIs
   */
  public String articleGrpListToCSV( List<TOCArticleGroup> articleGroups);

  /**
   *
   */
  public List<TOCArticleGroup> getArticleGrpList(URI issueURI, String authId);

  /**
   *
   */
  public List<TOCArticleGroup> getArticleGrpList(Issue issue, String authId);
  /**
   *
   */
  public List<TOCArticleGroup> buildArticleGroups(Issue issue, List<TOCArticleGroup> articleGroups, String authId);

  /**
   * Get ordered list of articles. Either from articleList or from
   * simpleCollection if articleList is empty.
   * @param issue
   * @return List of article URI's
   */
  public List<URI> getArticleList(Issue issue);

  /**
   * Set the configuration class
   *
   * @param config the configuration class to use
   * @throws ApplicationException if required configuration settings are missing
   */
  public void setConfiguration(Configuration config) throws ApplicationException;

  /**
   * Checks to see if solr is up or not
   *
   * @throws SolrServerException
   * @throws IOException
   */
  public void pingSolr() throws SolrServerException, IOException;
}
