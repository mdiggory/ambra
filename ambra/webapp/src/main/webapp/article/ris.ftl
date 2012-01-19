<#--
  $HeadURL$
  $Id$

  Copyright (c) 2007-2011 by Public Library of Science
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
TY  - JOUR
T1  - ${title}
<#assign authorTag = "A1">
<#list authorList as author>
<#if author.suffix?exists>
  <#assign authorSuffix = ", " + author.suffix>
<#else>
  <#assign authorSuffix = "">
</#if>
${authorTag}  - ${author.surnames!}, ${author.givenNames!} ${authorSuffix!}
</#list>
<#list collaborativeAuthors as collab>
${authorTag}  - ${collab}
</#list>
Y1  - ${year!"0000"}/${month!}/${day!}
N2  - ${summary!}
JF  - ${journal!}
JA  - ${journal!}
VL  - ${volume!}
IS  - ${issue!}
UR  - ${url!}
SP  - ${eLocationId!}
EP  -
PB  - ${publisherName!}
M3  - doi:${doi!}
ER  -
