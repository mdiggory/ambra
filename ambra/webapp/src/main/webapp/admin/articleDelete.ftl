<#--
  $HeadURL:: http://ambraproject.org/svn/ambra/branches/ANH-Conversion/ambra/webapp/src#$
  $Id: manageAnnotation.ftl 8546 2010-06-23 00:03:48Z ssterling $

  Copyright (c) 2007-2010 by Public Library of Science
  http://plos.org
  http://ambraproject.org

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
-->
<#include "includes/globals.ftl">
<html>
  <head>
    <title>Ambra: Administration: Delete Article</title>

    <script type="text/javascript">
      function confirmDeleteArticle() {
        if(confirm('Are you sure you want to delete this article?')) {
          return confirm('Are you REALLY sure? (All associated data will be removed as well and this can not be undone)');
        }
      }
    </script>

    <#include "includes/header.ftl">
  </head>
  <body>
    <h1 style="text-align: center">Ambra: Administration: Delete Article</h1>
    <#include "includes/navigation.ftl">

    <@messages />

    <fieldset>
      <legend><strong>Delete and Revert Article Ingest (This will revert the ingestion queue and remove the article data and all annotations, comments and ratings)</strong></legend>
      <@s.form name="deleteArticle" action="deleteArticleDelete" method="post" namespace="/admin" onsubmit="return confirmDeleteArticle();">
        Article Uri: <input type="article" name="article" label="Article Uri" size="80" value=""/>&nbsp;
        <input type="submit" name="action" value="Delete" />
      </@s.form>
    </fieldset>

    <br/>
    <br/>
    <br/>

  </body>
</html>