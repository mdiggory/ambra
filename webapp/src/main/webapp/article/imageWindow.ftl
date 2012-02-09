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
<#assign jDocURL = freemarker_config.getJournalUrl(journalContext) + "/article/" + uri />
<a href="#" class="close" onclick="ambra.slideshow.closeReturn('${jDocURL}');">Return to article</a>

<div id="figure-window-wrapper">
<#if Parameters.imageURI?exists>
   <#assign imageURI = Parameters.imageURI>
<#else>
   <#assign imageURI = "">
</#if>
	<div id="figure-window-nav">

	  <#list secondaryObjects as image>
			<#if image.uri == imageURI>
				<#assign currentImage = image>
				<div class="figure-window-nav-item current">
			<#else>
				<div class="figure-window-nav-item">
			</#if>
	    <@s.url id="imageUrl" includeParams="none"  action="fetchObject" namespace="/article" uri="${image.uri}"/>
	    <@s.a title="Click for larger image" href="#"> <!--put code here for onclick and change the pane-->
	    <img border="0" class="thumbnail" id="tn${image_index}" src="${imageUrl}&representation=${image.repSmall}" onclick="ambra.slideshow.show(this, ${image_index});" title="${image.title} ${image.plainCaptionTitle}" />
	    </@s.a>
		    
	</div>
	  </#list>
		<#if !currentImage?exists && secondaryObjects?size gt 0>
			<#assign currentImage = secondaryObjects?first>
		</#if>

	</div>
	<div id="figure-window-container">
	    <@s.url id="currentImageViewLarger" includeParams="none"  namespace="/article" action="showImageLarge" uri="${currentImage.uri}"/>
	    <@s.url id="currentImageUrl" includeParams="none" action="fetchObject" namespace="/article" uri="${currentImage.uri}"/>
 	    <@s.url id="currentImageAttachmentUrl" includeParams="none"  action="fetchObjectAttachment" namespace="/article" uri="${currentImage.uri}"/>
		<div id="figure-window-hdr">
			<div class="figure-next">
				<a href="#" id="previous" onclick="return ambra.slideshow.showPrevious(this);" class="previous icon<#if currentImage == secondaryObjects?first> hidden</#if>">Previous</a> | <a href="#" id="next" onclick="return ambra.slideshow.showNext(this);" class="next icon<#if currentImage == secondaryObjects?last> hidden</#if>">Next</a>
			</div>
			<div id="figure-window-hdr-links">
				<a href="${currentImageViewLarger}" id="viewL" class="larger icon" title="Click to view a larger version of this image">View Larger Image</a> 
				<a href="${currentImageAttachmentUrl}&representation=TIF" id="downloadTiff" class="image icon" title="Click to download the original TIFF">		Download original TIFF</a> 
				<a href="${currentImageAttachmentUrl}&representation=${currentImage.repMedium}" id="downloadPpt" class="ppt icon" title="Click to download a PowerPoint friendly version">Download PowerPoint Friendly Image</a>		
			</div>
				
		</div>
		
		<div id="figure-window-viewer">
			<img src="${currentImageUrl}&representation=${currentImage.repMedium}" title="${currentImage.title} ${currentImage.plainCaptionTitle}" class="large" id="figureImg" />
			<span id="figureTitle">
			<#if currentImage.title != "" > 
				<strong>${currentImage.title}.</strong> 
                	</#if>
			${currentImage.transformedCaptionTitle}</span>
		</div>
		<div id="figure-window-description">
				${currentImage.transformedDescription}
		</div>
	</div>
</div>
<script type="text/javascript">
dojo.addOnLoad(function() {
  dojo.connect(window, "resize", function() {
    ambra.slideshow.adjustViewerHeight();
  });
  ambra.slideshow.setLinkView(dojo.byId("viewL"));
  ambra.slideshow.setLinkTiff(dojo.byId("downloadTiff"));
  ambra.slideshow.setLinkPpt(dojo.byId("downloadPpt"));
  ambra.slideshow.setFigImg(dojo.byId("figureImg"));
  ambra.slideshow.setFigTitle(dojo.byId("figureTitle"));
  ambra.slideshow.setFigCaption(dojo.byId("figure-window-description"));
  ambra.slideshow.setInitialThumbnailIndex();
  ambra.slideshow.adjustViewerHeight();
});
</script>