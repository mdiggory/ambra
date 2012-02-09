/* $HeadURL:: http://gandalf.topazproject.org/svn/head/plosone/libs/article-util/src/main/groovy/#$
 * $Id: Delete.groovy 2686 2007-05-15 08:22:38Z ebrown $
 *
 * Copyright (c) 2007-2010 by Public Library of Science
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
  * ---------
  * dojoBuild
  * ---------
  * This script performs a dojo custom build of the dojo library and the defined ambra dojo widgets.
  * Specifically, it performs the following:
  * 1) Perfoms a custom dojo build. 
  * 2) Injects interned locale data into the built js files.
  * 
  * IMPT: The invoking JVM's working directory is critical: it must be the js ancestor dir (the dir containing the pom.xml).
  * IMPT: This script sadly depends on the gmaven plugin scripting context and therefore can NOT be run standalone!! 
  * 
  * @see http://dojotoolkit.org/book/dojo-book-0-9/part-4-meta-dojo/package-system-and-custom-builds
  */

static final String NL = System.getProperty("line.separator")

static final String locales =  
  "dojo.provide(\"dojo.nls.ambra_xx\");dojo.provide(\"dijit.nls.loading\");dijit.nls.loading._built=true;dojo.provide(\"dijit.nls.loading.xx\");dijit.nls.loading.xx={\"loadingState\":\"Loading...\",\"errorState\":\"Sorry, an error occurred\"};dojo.provide(\"dijit.nls.common\");dijit.nls.common._built=true;dojo.provide(\"dijit.nls.common.xx\");dijit.nls.common.xx={\"buttonOk\":\"OK\",\"buttonCancel\":\"Cancel\",\"buttonSave\":\"Save\",\"itemClose\":\"Close\"};dojo.provide(\"dojo.nls.ambra_ROOT\");dojo.provide(\"dijit.nls.loading\");dijit.nls.loading._built=true;dojo.provide(\"dijit.nls.loading.ROOT\");dijit.nls.loading.ROOT={\"loadingState\":\"Loading...\",\"errorState\":\"Sorry, an error occurred\"};dojo.provide(\"dijit.nls.common\");dijit.nls.common._built=true;dojo.provide(\"dijit.nls.common.ROOT\");dijit.nls.common.ROOT={\"buttonOk\":\"OK\",\"buttonCancel\":\"Cancel\",\"buttonSave\":\"Save\",\"itemClose\":\"Close\"};dojo.provide(\"dojo.nls.ambra_en\");dojo.provide(\"dijit.nls.loading\");dijit.nls.loading._built=true;dojo.provide(\"dijit.nls.loading.en\");dijit.nls.loading.en={\"loadingState\":\"Loading...\",\"errorState\":\"Sorry, an error occurred\"};dojo.provide(\"dijit.nls.common\");dijit.nls.common._built=true;dojo.provide(\"dijit.nls.common.en\");dijit.nls.common.en={\"buttonOk\":\"OK\",\"buttonCancel\":\"Cancel\",\"buttonSave\":\"Save\",\"itemClose\":\"Close\"};dojo.provide(\"dojo.nls.ambra_en-us\");dojo.provide(\"dijit.nls.loading\");dijit.nls.loading._built=true;dojo.provide(\"dijit.nls.loading.en_us\");dijit.nls.loading.en_us={\"loadingState\":\"Loading...\",\"errorState\":\"Sorry, an error occurred\"};dojo.provide(\"dijit.nls.common\");dijit.nls.common._built=true;dojo.provide(\"dijit.nls.common.en_us\");dijit.nls.common.en_us={\"buttonOk\":\"OK\",\"buttonCancel\":\"Cancel\",\"buttonSave\":\"Save\",\"itemClose\":\"Close\"};";

// NOTE: the gmaven plugin provides AntBuilder in the scripting context
if (!ant)
  ant = new AntBuilder()

def injectLocales = { String fpath ->
  File f = new File(fpath)
  String fbuf = f.getText()
  int index = fbuf.lastIndexOf('dojo.i18n._preloadLocalizations')
  if (index >= 0)
    f.write(fbuf.substring(0, index) + locales + fbuf.substring(index))
}

// check if we're already up-to-date
def artifact = new File(project.build.directory, "${project.build.finalName}-js.zip")

ant.uptodate(property: 'isUpToDate', targetfile: artifact) {
  srcfiles(dir: new File(project.basedir, project.build.scriptSourceDirectory), includes: "*")
}

if (ant.project.properties.'isUpToDate') {
  println 'dojo build is up to date'
  return
}

// dojo build settings 
// IMPT: paths in the following setting vars are relative to the 'dojo-release-xxx-src/util/buildscripts' dir
final String profileFile = '../../../../src/main/scripts/ambra.profile.js';
final String action = 'release';
final String releaseName = 'dojo';
final String releaseDir = '../../../';
final String localeList = 'en-us';
final String cssOptimize = 'comments';
final String cssImportIgnore = '';
final String optimize = ''
final String layerOptimize = 'shrinksafe'
final String copyTests = 'false'
final String version = project.version;

final String shrinkSafeJarPath = (project.build.directory + '/dojo-src/util/shrinksafe/js.jar:' + project.build.directory + '/dojo-src/util/shrinksafe/shrinksafe.jar:')
final String shrinkSafeWorkingDir = (project.build.directory + '/dojo-src/util/buildscripts')

//java -classpath ../shrinksafe/js.jar:../shrinksafe/shrinksafe.jar -classname org.mozilla.javascript.tools.shell.Main build.js  %*
println 'Invoking ambra dojo build (' + profileFile + ')...'
println 'Classpath: ' + shrinkSafeJarPath
println 'Working Folder: ' + shrinkSafeWorkingDir
ant.java(classpath: shrinkSafeJarPath, classname:'org.mozilla.javascript.tools.shell.Main', fork:true, dir: shrinkSafeWorkingDir, resultproperty:'dojoBuildResult') {
  arg(value: 'build.js')
  arg(value: 'profileFile=' + profileFile)
  arg(value: 'action=' + action)
  arg(value: 'releaseName=' + releaseName)
  arg(value: 'releaseDir=' + releaseDir)
  arg(value: 'localeList=' + localeList)
  arg(value: 'cssOptimize=' + cssOptimize)
  arg(value: 'cssImportIgnore=' + cssImportIgnore)
  arg(value: 'optimize=' + optimize)
  arg(value: 'layerOptimize=' + layerOptimize)
  arg(value: 'copyTests=' + copyTests)
  arg(value: 'version=' + version)
}
def dojoBuildResult = ant.project.properties.'dojoBuildResult';
if(dojoBuildResult != '0') {
  println 'dojo build error (exit code: ' + dojoBuildResult+ ').  Aborting!'
  System.exit(1)
}
println 'dojo build complete'

// inject the locales to the built files
println 'Injecting locale(s)...'
injectLocales(project.build.directory + '/dojo/dojo/ambra.js')
injectLocales(project.build.directory + '/dojo/dojo/ambra.js.uncompressed.js')
println 'Locale(s) injected'

