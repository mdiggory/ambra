<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%--
  ~ $HeadURL$
  ~ $Id$
  ~
  ~ Copyright (c) 2006-2011 by Public Library of Science
  ~ http://plos.org
  ~ http://ambraproject.org
  ~
  ~ Licensed under the Apache License, Version 2.0 (the "License");
  ~ you may not use this file except in compliance with the License.
  ~ You may obtain a copy of the License at
  ~
  ~ http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing, software
  ~ distributed under the License is distributed on an "AS IS" BASIS,
  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~ See the License for the specific language governing permissions and
  ~ limitations under the License.
  --%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en">

<head>
<title>PLoS Journals : A Peer-Reviewed, Open-Access Journal</title>

<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />

<link rel="shortcut icon" href="images/favicon.ico" type="image/x-icon" />

<link rel="home" title="home" href="http://www.plosjournals.org"></link>

<link rel="stylesheet" type="text/css" href="../ambra-registration/css/user_screen.css" />
<link rel="stylesheet" type="text/css" href="../ambra-registration/css/user_forms.css" />


<!--
<rdf:RDF xmlns="http://web.resource.org/cc/"
   xmlns:dc="http://purl.org/dc/elements/1.1/"
   xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#">
<Work rdf:about="http://register.plos.org">
   <license rdf:resource="http://creativecommons.org/licenses/by/2.5/" />
</Work>
<License rdf:about="http://creativecommons.org/licenses/by/2.5/">
   <permits rdf:resource="http://web.resource.org/cc/Reproduction" />
   <permits rdf:resource="http://web.resource.org/cc/Distribution" />
   <requires rdf:resource="http://web.resource.org/cc/Notice" />
   <requires rdf:resource="http://web.resource.org/cc/Attribution" />
   <permits rdf:resource="http://web.resource.org/cc/DerivativeWorks" />
</License>
</rdf:RDF>
-->

<script type="text/javascript" src="../ambra-registration/javascript/config_default.js"></script>
<script type="text/javascript" src="../ambra-registration/javascript/dojo/dojo/dojo.js"></script>
<script type="text/javascript" src="../ambra-registration/javascript/init_global.js"></script>
<script type="text/javascript" src="../ambra-registration/javascript/init_navigation.js"></script>
</head>
<body>

<!-- begin : container -->
<div id="container">
	
<!-- begin : header -->
<div id="hdr">
	<div id="logo"><a href="http://www.plosjournals.org" title="PLoS Journals"><span>PLoS Journals</span></a></div>
	<div id="tagline"><span>A peer-reviewed, open-access journal published by the Public Library of Science</span></div>
</div>
<!-- end : header -->


<!-- begin : navigation -->
  <ul id="nav">
    <li class="none"><a href="http://www.plosjournals.org" title="PLoS Journals Home Page" tabindex="101">Home</a></li>
    <li class="none"><a href="http://www.plosone.org/profile" title="My Profile" tabindex="102">My Profile</a></li>
    <li class="journalnav"><a href="http://www.plos.org" title="Public Library of Science" class="drop" tabindex="111">PLoS.org</a>
      <ul>
        <li><a href="http://www.plos.org/oa/index.html" title="PLoS.org | Open Access Statement">Open Access</a></li>
        <li><a href="http://www.plos.org/support/donate.php" title="PLoS.org | Join PLoS">Join PLoS</a></li>
        <li><a href="http://www.plos.org/cms/blog" title="PLoS.org | PLoS Blog">PLoS Blog</a></li>
        <li><a href="http://www.plos.org/connect.html" title="PLoS.org | Stay Connected">Stay Connected</a></li>
      </ul>
    </li>
    <li class="journalnav"><a href="http://www.ploshubs.org/" title="PLoSHubs.org" tabindex="110" class="drop">Hubs</a>
      <ul>
        <li><a href="http://hubs.plos.org/biodiversity" title="PLoS Hubs: Biodiversity">Biodiversity</a></li>
        <li><a href="http://clinicaltrials.ploshubs.org/" title="PLoSHubs.org | Clinical Trials">Clinical Trials</a></li>
      </ul>
    </li>
    <li class="journalnav"><a href="http://www.plosjournals.org/" title="PLoSjournals.org" class="drop" tabindex="109">Journals</a>
      <ul>
        <li><a href="http://www.plosbiology.org" title="PLoSBiology.org">PLoS Biology</a></li>
        <li><a href="http://www.plosmedicine.org" title="PLoSMedicine.org">PLoS Medicine</a></li>
        <li><a href="http://www.ploscompbiol.org/" title="PLoSCompBiol.org">PLoS Computational Biology</a></li>
        <li><a href="http://www.plosgenetics.org/" title="PLoSGenetics.org">PLoS Genetics</a></li>
        <li><a href="http://www.plospathogens.org/" title="PLoSPathogens.org">PLoS Pathogens</a></li>
        <li><a href="http://www.plosone.org/" title="PLoSONE.org">PLoS ONE</a></li>
        <li><a href="http://www.plosntds.org/" title="PLoSNTDs.org">PLoS Neglected Tropical Diseases</a></li>
      </ul>
    </li>
  </ul>
<!-- end : navigation -->

<div id="content">
   <h1>Login to Your PLoS Journals Account</h1>

  <%--
    The "form" section was copied from the original "casLoginView.jsp"
      (which was distributed with the CAS server example application from JASIG)
      and modified to emulate the previous PLoS CAS login page.

    The behavior of the error messages approximates the look and feel of the previous
      CAS login page, which is why the actual error messages
      (generated by the Authentication Handler) are not shown to the user.
  --%>
  <form:form method="post" id="fm1" name="login_form" cssClass="ambra-form" commandName="${commandName}" htmlEscape="true">
    <fieldset>

      <input type="hidden" name="lt" value="${flowExecutionKey}" />

      <ol>

      <%--
        If there is at least one error message, then display a generic error message
          at the top of the form and another error message next to the email field.

        This use of the "form:errors" tag's "elements" attribute actually hides the original
          messages (which were set in the "messages_en.properties" file).
      --%>
        <form:errors path="*" element="!--">
        <c:forEach items="${messages}" var="message">
          <c:set var="anyErrorMessage" value="${message}"/>
        </c:forEach>
        </form:errors>

        <c:if test="${ ! empty anyErrorMessage}">
          <li class="form-error"><p class="required">Please correct the error below.</p></li>
          <li class=form-error>
        </c:if>
        <c:if test="${empty anyErrorMessage}">
          <li>
        </c:if>

        <label for="username">E-mail</label>
        <c:if test="${empty sessionScope.openIdLocalId}">
          <form:input id="username" size="25" tabindex="1" accesskey="${userNameAccessKey}" path="username" autocomplete="false" htmlEscape="true" />
          <c:if test="${ ! empty anyErrorMessage}">
            Please enter a valid e-mail address and password
          </c:if>
        </c:if>

          </li>

      <%--
        On the previous PLoS CAS login page, the "password" field never displayed any errors.
      --%>
        <li>
          <label for="password">Password</label>
          <%--
            NOTE: Certain browsers will offer the option of caching passwords for a user.  There is a non-standard attribute,
            "autocomplete" that when set to "off" will tell certain browsers not to prompt to cache credentials.  For more
            information, see the following web page:
            http://www.geocities.com/technofundo/tech/web/ie_autocomplete.html
          --%>
          <form:password id="password" size="25" tabindex="2" path="password"  accesskey="${passwordAccessKey}" htmlEscape="true" autocomplete="off" />
        </li>
      </ol>

        <input type="hidden" name="_eventId" value="submit" />
        <div class="btnwrap">
          <input name="submit" accesskey="l" value="Login" tabindex="3" type="submit" />
        </div>

    </fieldset>

  </form:form>

        <ul>
          <li><a href="/ambra-registration/help.action">Help</a></li>
          <li><a href="/ambra-registration/register.action" tabindex="12">Register for a New Account</a></li>
          <li><a href="/ambra-registration/forgotPassword.action" title="Click here if you forgot your password" tabindex="11">Forgotten Password?</a></li>
	  <li><a href="/ambra-registration/resendRegistration.action" title="Click here if you need to confirm your e-mail address" tabindex="11">Resend e-mail address confirmation</a></li>
	</ul>
      </div>
</div>
<!-- end : container -->	

<!-- begin : footer -->
<div id="ftr">
<ul>
<li><a href="http://journals.plos.org/privacy.php" title="PLoS Privacy Statement" tabindex="501">Privacy Statement</a></li>
<li><a href="http://journals.plos.org/terms.php" title="PLoS Terms of Use" tabindex="502">Terms of Use</a></li>
<li><a href="http://www.plos.org/advertise/" title="Advertise with PLoS" tabindex="503">Advertise</a></li>
<li><a href="/ambra-registration/help.action" title="Help Using this Site" tabindex="504">Help</a></li>
<li><a href="http://www.plos.org" title="PLoS.org" tabindex="504">PLoS.org</a></li>
</ul>

</div>
<!-- end : footer -->
<script type="text/javascript">
var gaJsHost = (("https:" == document.location.protocol) ? "https://ssl." : "http://www.");
document.write(unescape("%3Cscript src='" + gaJsHost + "google-analytics.com/ga.js' type='text/javascript'%3E%3C/script%3E"));
</script>
<script type="text/javascript">
var pageTracker = _gat._getTracker("UA-338393-1");
pageTracker._trackPageview();
pageTracker._setDomainName("register.plos.org");
</script>
</body>
</html>
