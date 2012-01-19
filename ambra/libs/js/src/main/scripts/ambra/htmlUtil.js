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
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * ambra.htmlUtil
 * 
 * Utility to help get the attribute from the url to be used in the JS.
 * 
 **/
dojo.provide("ambra.htmlUtil");
ambra.htmlUtil = {
  getQuerystring: function() {
    var paramQuery = unescape(document.location.search.substring(1));
    var paramArray = paramQuery.split("&");
    
    var queryArray = new Array();
    
    for (var i=0;i<paramArray.length;i++) {
      var pair = paramArray[i].split("=");
      
      queryArray.push({param: pair[0], value: pair[1]});
    }     
    
    return queryArray;
  }  
}  