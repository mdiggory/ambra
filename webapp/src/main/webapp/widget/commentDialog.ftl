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
<div dojoType="ambra.widget.RegionalDialog" id="CommentDialog" style="padding:0;margin:0;">
  <div class="dialog preview">
    <div class="tipu" id="cTipu"></div>
    <div class="btn close" id="btn_close" title="Click to close"><a title="Click to close">Close</a></div>
    <div id="cmtContainer" class="comment">
      <h6 id="viewCmtTitle"></h6>
      <div class="detail" id="viewCmtDetail"></div>
      <div class="contentwrap" id="viewComment"></div>
      <div class="contentwrap" id="viewCIStatement"></div>
      <div class="detail" id="viewLink">
        <!--<a href="#" class="commentary icon" title="Click to view full thread and respond">View all responses</a>
        <a href="#" class="respond tooltip" title="Click to respond to this posting">Respond to this</a>-->
      </div>
    </div>
    <div class="tip" id="cTip"></div>
  </div>
</div>
<div dojoType="ambra.widget.RegionalDialog" id="CommentDialogMultiple" style="padding:0;margin:0;">
  <div class="dialog multiple preview">
    <div class="tipu" id="mTipu"></div>
    <div class="btn close" id="btn_close_multi" title="Click to close"><a title="Click to close">Close</a></div>
    <ol id="multilist"></ol>
    <br/>
    <div id="multidetail"></div>
    <div class="tip" id="mTip"></div>
  </div>
</div>


