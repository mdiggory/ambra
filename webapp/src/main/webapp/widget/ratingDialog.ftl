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
<div dojoType="dijit.Dialog" id="Rating">
  <div class="dialog annotate">
    <div class="tipu" id="dTipu"></div>
    <div class="comment">
      <h5><span>Rate This Article</span></h5>
      <@s.url id="competingInterestURL" action="competing" namespace="/static" includeParams="none"/>
      <div class="instructions">Please follow our <a  href="${rating}">guidelines for rating</a> and review our <@s.a href="%{competingInterestURL}">competing interests policy</@s.a>. Comments that do not conform to our guidelines will be promptly removed and the user account disabled. The following must be avoided:
        <ol>
          <li>Remarks that could be interpreted as allegations of misconduct</li>
          <li>Unsupported assertions or statements</li>
          <li>Inflammatory or insulting language</li>
        </ol>
      </div>
      <div class="posting pane">
        <form name="ratingForm" id="ratingForm" method="post" action="">
          <input type="hidden" name="articleURI" value="${articleURI}" />  
          <input type="hidden" name="commentTitle" id="commentTitle" value="" />
          <input type="hidden" name="comment" id="commentArea" value="" />
          <input type="hidden" name="ciStatement" id="statementArea" value="" />
          <input type="hidden" name="isCompetingInterest" id="isCompetingInterest" value="" />
          <fieldset>
            <legend>Compose Your Annotation</legend>
            <span id="submitRatingMsg" class="error" style="display:none;"></span>
            <table class="layout">
              <tr>
                <td rowspan="2">
      <#if isResearchArticle == true>
                  <label for="insight">Insight</label>
                  <ul class="star-rating rating edit" title="Rate insight" id="rateInsight">
                    <li class="current-rating pct0"></li>
                    <li><a href="javascript:void(0);" title="Bland" class="one-star" onclick="ambra.rating.setRatingCategory(this, 'insight', 1);">1</a></li>
                    <li><a href="javascript:void(0);" title="" class="two-stars" onclick="ambra.rating.setRatingCategory(this, 'insight', 2);">2</a></li>
                    <li><a href="javascript:void(0);" title="" class="three-stars" onclick="ambra.rating.setRatingCategory(this, 'insight', 3);">3</a></li>
                    <li><a href="javascript:void(0);" title="" class="four-stars" onclick="ambra.rating.setRatingCategory(this, 'insight', 4);">4</a></li>
                    <li><a href="javascript:void(0);" title="Profound" class="five-stars" onclick="ambra.rating.setRatingCategory(this, 'insight', 5);">5</a></li>
                  </ul>    
                  <input type="hidden" name="insight" title="insight" value="" />
                  <label for="reliability">Reliability</label>
                  <ul class="star-rating rating edit" title="Rate reliability" id="rateReliability">
                    <li class="current-rating pct0"></li>
                    <li><a href="javascript:void(0);" title="Tenuous" class="one-star" onclick="ambra.rating.setRatingCategory(this, 'reliability', 1);">1</a></li>
                    <li><a href="javascript:void(0);" title="" class="two-stars" onclick="ambra.rating.setRatingCategory(this, 'reliability', 2);">2</a></li>
                    <li><a href="javascript:void(0);" title="" class="three-stars" onclick="ambra.rating.setRatingCategory(this, 'reliability', 3);">3</a></li>
                    <li><a href="javascript:void(0);" title="" class="four-stars" onclick="ambra.rating.setRatingCategory(this, 'reliability', 4);">4</a></li>
                    <li><a href="javascript:void(0);" title="Unassailable" class="five-stars" onclick="ambra.rating.setRatingCategory(this, 'reliability', 5);">5</a></li>
                  </ul>    
                  <input type="hidden" name="reliability" title="reliability" value="" />
                  <label for="style">Style</label>
                  <ul class="star-rating rating edit" title="Rate style" id="rateStyle">
                    <li class="current-rating pct0"></li>
                    <li><a href="javascript:void(0);" title="Crude" class="one-star" onclick="ambra.rating.setRatingCategory(this, 'style', 1);">1</a></li>
                    <li><a href="javascript:void(0);" title="" class="two-stars" onclick="ambra.rating.setRatingCategory(this, 'style', 2);">2</a></li>
                    <li><a href="javascript:void(0);" title="" class="three-stars" onclick="ambra.rating.setRatingCategory(this, 'style', 3);">3</a></li>
                    <li><a href="javascript:void(0);" title="" class="four-stars" onclick="ambra.rating.setRatingCategory(this, 'style', 4);">4</a></li>
                    <li><a href="javascript:void(0);" title="Elegant" class="five-stars" onclick="ambra.rating.setRatingCategory(this, 'style', 5);">5</a></li>
                  </ul>    
                  <input type="hidden" name="style" title="style" value="" />
      <#else>
                  <label for="singleRating">Rating</label>
                  <ul class="star-rating rating edit" title="Rate single" id="rateSingleRating">
                    <li class="current-rating pct0"></li>
                    <li><a href="javascript:void(0);" title="Bland" class="one-star" onclick="ambra.rating.setRatingCategory(this, 'singleRating', 1);">1</a></li>
                    <li><a href="javascript:void(0);" title="" class="two-stars" onclick="ambra.rating.setRatingCategory(this, 'singleRating', 2);">2</a></li>
                    <li><a href="javascript:void(0);" title="" class="three-stars" onclick="ambra.rating.setRatingCategory(this, 'singleRating', 3);">3</a></li>
                    <li><a href="javascript:void(0);" title="" class="four-stars" onclick="ambra.rating.setRatingCategory(this, 'singleRating', 4);">4</a></li>
                    <li><a href="javascript:void(0);" title="Profound" class="five-stars" onclick="ambra.rating.setRatingCategory(this, 'singleRating', 5);">5</a></li>
                  </ul>    
                  <input type="hidden" name="singleRating" title="singleRating" value="" />
      </#if>
                  <label for="cTitle"><span class="none">Enter your comment title</span><!-- error message text <em>A title is required for all public annotations</em>--></label>
                  <input type="text" name="cTitle" id="cTitle" value="Enter your comment title..." class="title" alt="Enter your comment title..." />
                  <label for="cArea"><span class="none">Enter your comment</span><!-- error message text <em>Please enter your annotation</em>--></label>
                  <textarea name="cArea" id="cArea" value="Enter your comment..." alt="Enter your comment...">Enter your comment...</textarea>
                </td>
                <td rowspan="2">&nbsp;</td>
                <td class="coi">
                  <fieldset>
                    <legend>Declare any competing interests.</legend>
                    <ul>
                      <li><label><input id="isCompetingInterestNo" type="radio" name="competingInterest" value="false"  /> No, I don't have any competing interests to declare.</label></li>
                      <li><label><input id="isCompetingInterestYes" type="radio" name="competingInterest" value="true"  /> Yes, I have competing interests to declare (enter below):</label></li>
                    </ul>
                    <textarea name="ciStatementArea" id="ciStatementArea" disabled value="Enter your competing interests..." title="Enter your competing interests...">Enter your competing interests...</textarea>
                  </fieldset>
                </td>
              </tr>
              <tr>
                <td class="buttons">
                  <input type="button" value="Cancel" title="Click to close and cancel" id="btn_cancel_rating"/>
                  <input type="button" value="Submit" title="Click to post your annotation publicly" id="btn_post_rating" class="primary"/>
                </td>
              </tr>
              <tr>
                  <td colspan="3"><p>Ratings can include the following markup tags:</p>
                  <p><strong>Emphasis:</strong> ''<em>italic</em>''&nbsp;&nbsp;'''<strong>bold</strong>'''&nbsp;&nbsp;'''''<strong><em>bold italic</em></strong>'''''</p>
                  <p><strong>Other:</strong> ^^<sup>superscript</sup>^^&nbsp;&nbsp;~~<sub>subscript</sub>~~</p></td>
              </tr>
            </table>
          </fieldset>
        </form>
      </div>
    </div>
  </div>
</div>
