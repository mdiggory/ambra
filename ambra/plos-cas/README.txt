The plos-cas project is a re-architecting of the PLoS implemention of the CAS authentication server.


---  Build Notes  ---

The easiest way to build this project:

  mvn clean package

This Maven2 command creates a new "cas.war" file which contains 1) the original Jasig CAS Server
  combined with 2) the customizations included in this "plos-cas" project.
  
The resulting "cas.war" file can be deployed directly into any Tomcat (tested on version 6.0.20)
  through the "Tomcat Web Application Manager" HTML interface.


---  Deployment Notes  ---

This code was developed using the CAS server version 3.4.7 and should perform all the tasks
  that Ambra expects without having to make changes to the existent Ambra code or config.
The ambra-registration project has not been modified, either.

The JASIG CAS server makes extensive use of the Spring framework, so the new architecture relies on it, too.
The only JSP (login.jsp) uses only standard tag libraries which are avalible in the CAS server WAR file.

When deployed onto Tomcat, this project should not depend on any other PLoS projects, except ambra-registration.
Both this project and ambra-registration talk to the same database.
The CSS and Javascript files needed to properly render "login.jsp" all live in ambra-registration.
These files are referenced (in that JSP) by URL, so the location of those ambra-registration CSS and Javascript files is important.


---  Organizational Notes  ---

The plos-cas project is built on the CAS server application distributed by JASIG ( https://wiki.jasig.org/display/CAS/Home ).

These files represent a condensation and reengineering of the esup (based on CAS server version 2.0.12),
  esup-bundle, and cas-mod projects, along with code from the ambra project and template files from plos-templates/cas.

The database ("casdb") used by this project is written to by the ambra-registration application.
None of this project's code actually writes to that database.


---  Config Files ---

cas.properties
  Tells CAS to look in the "plos_views.properties" file for which JSPs to use.

deployerConfigContext.xml
  Configures the Java Beans needed to customize this class for PLoS use, including the Handler, DataSource, and PasswordEncoder beans.

plos_views.properties
  Tells CAS to use the PLoS-specific "login.jsp" file when letting user's log in.
  Also tells CAS to, after logging a user out, redirect that user to the URL specified in the HttpRequest's "service" parameter.

web.xml
  Add a Filter that, when given an ID (specified by HttpRequest's "guid" parameter) will return that user's Email Address.

log4j.xml
  Relies on the "catalina.base" variable to determine where to put "cas.log" and "perfStats.log".

cas-servlet.xml
  Added the property "followServiceRedirects" to the Logout Controler.
  This allows the user, on logout, to be redirected to the URL specified in the "service" parameter of the /logout URL


---  JSP Files ---

login.jsp
  The only JSP used by CAS.  Minimal changes have been made to the Jasig CAS login.jsp to make it look/feel like the old Ambra login page.


---  Java Files ---

DatabaseService.java
  Provides a connection to the database.  Only used by GetGuidReturnEmailFilter.

GetGuidReturnEmailFilter.java
  If a URL matching the pattern "email" is submitted, then the "guid" parameter is used to look up a corresponding email address.
  The email address is returned (as a simple String) and then FilterChain.doFilter(...) is NOT called.
  If there is no email address in the database for that "guid", then an ugly String (that the user will see) is returned.
  If the machine submitting the URL is not authenticated, then the login page will be returned and displayed to the user
    in the middle of the User Profile page.  This miserable failure mode is how it was written in the first place;
    Ambra will have to be changed before this unfortunate choice can be excised.

PlosSearchModeSearchDatabaseAuthenticationHandler.java
  The authentication handler which takes an Email Address and a Password.
  If the user can be authenticated, CAS issues a certificate for the ID of the user with that Email Address.
  The Email Address is NOT issued a certificate.

