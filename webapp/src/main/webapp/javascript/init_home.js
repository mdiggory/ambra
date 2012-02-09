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
var _ldc;

var homeConfig = {
  tabPaneSetId :"tabPaneSet",
  tabsContainer :"tabsContainer"
}

var tabsListMap = null;

dojo.addOnLoad( function() {

  // widget-ize tab control if present
  var tabNode = dojo.byId(homeConfig.tabPaneSetId);
  if(tabNode) {
    tabsListMap = [];
    tabsListMap[tabsListMap.length] = {
      tabKey :"recentContent",
      title :"Recently Published",
      className :"published",
      urlLoad :"/recentArticles.action",
      urlSave :""
    };
    tabsListMap[tabsListMap.length] = {
      tabKey :"featuredDiscussions",
      title :"Featured Discussions",
      className :"annotated",
      urlLoad :"/mostCommented.action",
      urlSave :""
    };
    tabsListMap[tabsListMap.length] = {
      tabKey :"mostViewed",
      title :"Most Viewed",
      className :"viewed",
      urlLoad :"/mostViewed.action",
      urlSave :""
    };

    var tabSelectId = "";
    
    // resolve user tab selection
    var querystring = ambra.htmlUtil.getQuerystring();
    for ( var i = 0; i < querystring.length; i++) {
      if (querystring[i].param == "tabId") {
        tabSelectId = querystring[i].value;
        break;
      }
    }

    ambra.horizontalTabs.setTabPaneSet();
    ambra.horizontalTabs.setTabsListObject(tabsListMap);
    ambra.horizontalTabs.setTabsContainer(dojo.byId(homeConfig.tabsContainer));
    ambra.horizontalTabs.initSimple(tabSelectId);
  }
});
