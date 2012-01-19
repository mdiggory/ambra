<#--
  $HeadURL::                                                                            $
  $Id$
  
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
    <title>Ambra: Administration: Manage Caches</title>
    <#include "includes/header.ftl">
  </head>
  <body>
    <h1 style="text-align: center">Ambra: Administration: Manage Caches</h1>
    <#include "includes/navigation.ftl">

    <@messages />

    <#if cacheKeys?has_content>
      <fieldset>
        <legend><strong>${cacheName}</strong> Keys</legend>
        <p>
          <#list cacheKeys as cacheKey>
            <@s.url id="remove" namespace="/admin" action="manageCaches"
              cacheAction="remove" cacheName="${cacheName}" cacheKey="${cacheKey}"/>
            <@s.url id="get" namespace="/admin" action="manageCaches"
              cacheAction="get" cacheName="${cacheName}" cacheKey="${cacheKey}"/>
            ${cacheKey} <@s.a href="%{remove}">remove()</@s.a> <@s.a href="%{get}">get()</@s.a><br/>
          </#list>
        </p>
      </fieldset>
      <br/>
      <hr/>
    </#if>

    <table border="1" cellpadding="2" cellspacing="0">
      <#list cacheStats.keySet().toArray() as cacheName>
        <tr>
          <th>${cacheName}</th>
          <#if cacheName != "">
            <@s.url id="clearStatistics" namespace="/admin" action="manageCaches"
              cacheAction="clearStatistics" cacheName="${cacheName}" />
            <@s.url id="removeAll" namespace="/admin" action="manageCaches"
              cacheAction="removeAll" cacheName="${cacheName}" />
            <@s.url id="getKeys" namespace="/admin" action="manageCaches"
              cacheAction="getKeys" cacheName="${cacheName}" />
            <td><@s.a href="%{clearStatistics}">clearStatistics</@s.a></td>
            <td><@s.a href="%{removeAll}">removeAll</@s.a></td>
            <td><@s.a href="%{getKeys}">getKeys</@s.a></td>
          <#else>
            <td>&nbsp;</td>
            <td>&nbsp;</td>
            <td>&nbsp;</td>
          </#if>
          <#assign colums=cacheStats.get(cacheName)>
          <#list colums as column>
            <td>${column}</td>
          </#list>
        </tr>
      </#list>
    </table>
  </body>
</html>
