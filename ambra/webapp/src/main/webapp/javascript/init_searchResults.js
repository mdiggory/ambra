/*
 * $HeadURL::                                                                            $
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
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

dojo.addOnLoad(function() {
  ldc = dijit.byId("LoadingCycle");

  //Set up clear filter events.
  var journalFilterImage = dojo.byId('clearJournalFilter');
  if(journalFilterImage != null) {
    //uncheck all journals and re-submit.
    dojo.connect(journalFilterImage,'click',function() {
      dojo.query('dl#journalFacet input[name="filterJournals"]').forEach(
        function(node, index, art) {
          node.checked = false;
        }
      );

      submitSearch();
    });
  }

  var subjectFilterImage = dojo.byId('clearSubjectFilter');

  if(subjectFilterImage != null) {
    //uncheck all subject categories and re-submit.
    dojo.connect(subjectFilterImage,'click',function() {
      dojo.query('dl#subjectFacet input[name="filterSubjects"]').forEach(
        function(node, index, art) {
          node.checked = false;
        }
      );

      submitSearch();
    });
  }

  //IE and everyone else have slightly different event models
  var eventName = (dojo.isIE)?"onclick":"onchange";

  dojo.query('dl#subjectFacet input[name="filterSubjects"]').forEach(
    function(node, index, art) {
      dojo.connect(node,eventName,function(e) {
        submitSearch();
      })
    }
  );

  dojo.query('dl#journalFacet input[name="filterJournals"]').forEach(
    function(node, index, art) {
      dojo.connect(node,eventName,function(e) {
        submitSearch();
      })
    }
  );

  var clearArticleTypeFilter = dojo.byId('clearArticleTypeFilter');

  if(clearArticleTypeFilter != null) {
    dojo.connect(clearArticleTypeFilter,"onclick",function(e) {
      var searchForm = dojo.byId('searchFormOnSearchResultsPage');

      ldc.show();

      return true;
    });
  }

  var clearKeywordFilter = dojo.byId('clearKeywordFilter');

  if(clearKeywordFilter != null) {
    dojo.connect(clearKeywordFilter,"onclick",function(e) {
      var searchForm = dojo.byId('searchFormOnSearchResultsPage');

      ldc.show();

      return true;
    });
  }

  var clearAllFilters = dojo.byId('clearAllFilters');

  if(clearAllFilters != null) {
    dojo.connect(clearAllFilters,"onclick",function(e) {
      var searchForm = dojo.byId('searchFormOnSearchResultsPage');

      ldc.show();

      return true;
    });
  }

  var sortList = dojo.byId('sortPicklist');

  if(sortList != null) {
    dojo.connect(sortList,"onchange",function(e) {
      submitSearch();
    });
  }

  var pageSizeList = dojo.byId('pageSizePickList');

  if(pageSizeList != null) {
    dojo.connect(pageSizeList,"onchange",function(e) {
      submitSearch();
    });
  }
});

function submitSearch()
{
  var searchForm = dojo.byId('searchFormOnSearchResultsPage');

  ldc.show();

  searchForm.startPage.value = 0;
  searchForm.submit();
}
