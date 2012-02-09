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
 * This file is not a dojo module rather simply contains general utility methods.
 */
 
dojo.provide("ambra.general");

document.getElementsByTagAndClassName = function(tagName, className) {
  if ( tagName == null )
    tagName = '*';
   
  var children = document.getElementsByTagName(tagName);
  var elements = new Array();
  
  if ( className == null )
    return children;
  
  for (var i = 0; i < children.length; i++) {
    var child = children[i];
    var classNames = child.className.split(' ');
    for (var j = 0; j < classNames.length; j++) {
      if (classNames[j] == className) {
        elements.push(child);
        break;
      }
    }
  }

  return elements;
}

document.getElementsByTagAndAttributeName = function(tagName, attributeName) {
  if ( tagName == null )
    tagName = '*';
   
  var children = document.getElementsByTagName(tagName);
  var elements = new Array();
  
  if ( attributeName == null )
    return children;
  
  for (var i = 0; i < children.length; i++) {
    var child = children[i];
    if (child.getAttributeNode(attributeName) != null) {
      elements.push(child);
    }
  }

  return elements;
}

document.getElementsByAttributeValue = function(tagName, attributeName, attributeValue) {
  if ( tagName == null )
    tagName = '*';
  else if ( attributeName == null )
    return "[getElementsByAttributeValue] attributeName is required.";
  else if ( attributeValue == null )
    return "[getElementsByAttributeValue] attributeValue is required.";

  var elements = document.getElementsByTagAndAttributeName(tagName, attributeName);
  var elValue = new Array();
  
  for (var i = 0; i < elements.length; i++) {
    var element = elements[i];
    if (element.getAttributeNode(attributeName).nodeValue == attributeValue) {
      elValue.push(element);
    }
  }

  return elValue;
}


/**
 * Extending the String object
 *
 **/
String.prototype.trim = function() {
  return this.replace(/(?:(?:^|\n)\s+|\s+(?:$|\n))/g,"");
}

String.prototype.rtrim = function() {
  return this.replace(/\s+$/,"");
}

String.prototype.ltrim = function() {
  return this.replace(/^\s+/, "");
}

String.prototype.isEmpty = function() {
  return (this == null || this == "");
}

String.prototype.trimOnWord = function(newLength) {
  var splitString = this.split(" ");
  var newString = "";

  if (this.length <= newLength)
    return this;

  for (var i = 0; i < splitString.length; i++)
  {
    if ((newString + " " + splitString[i]).length < newLength)
    {
      if (newString.length > 0)
        newString = newString + " ";

      newString = newString + splitString[i];

    } else {
      return newString;
    }
  }

  return newString;
}

String.prototype.replaceStringArray = function(delimiter, strMatch, newStr) {
  if (!strMatch || !newStr) {
    return "Missing required value";
  }
  
  var strArr = (delimiter) ? this.split(delimiter) : this.split(" ");
  var matchIndexStart = -1;
  var matchIndexEnd = -1;
  for (var i=0; i<strArr.length; i++) {
    if (strArr[i].match(strMatch) != null) {
      if (matchIndexStart < 0)
        matchIndexStart = i;
      
      matchIndexEnd = i;
    }
  }
  
  if (matchIndexEnd >= 0) {
    var diff = matchIndexEnd - matchIndexStart + 1;
    strArr.splice(matchIndexStart, diff, newStr);
  }
  
  var newStr = strArr.join(" ");
  
  return newStr;
}

/**
 * One stop shopping for handling dojo xhr errors.
 * This method is intended to be called from within dojo.xhr 'handle' or 'error' callback methods.
 */
function handleXhrError(response, ioArgs) {
  if(response instanceof Error){
    if(_ldc) _ldc.hide();
    if(response.dojoType == "cancel"){
      //The request was canceled by some other JavaScript code.
      console.debug("Request canceled.");
    }else if(response.dojoType == "timeout"){
      //The request took over 5 seconds to complete.
      console.debug("Request timed out.");
    }else{
      //Some other error happened.
      console.error(response);
      if(djConfig.isDebug) alert(response.toSource());
    }
  }
}

function jumpToElement(elNode) {
  if (elNode) {
    elLocation = ambra.domUtil.getCurrentOffset(elNode);
    window.scrollTo(0, elLocation.top);
  }
}

Date.getMonthShortName = function(mon) {
  switch(mon) {
    case 0:
      return "Jan";
      break;
    case 1:
      return "Feb";
      break;
    case 2:
      return "Mar";
      break;
    case 3:
      return "Apr";
      break;
    case 4:
      return "May";
      break;
    case 5:
      return "Jun";
      break;
    case 6:
      return "Jul";
      break;
    case 7:
      return "Aug";
      break;
    case 8:
      return "Sep";
      break;
    case 9:
      return "Oct";
      break;
    case 10:
      return "Nov";
      break;
    case 11:
      return "Dec";
      break;
  }
}

Date.formatDate = function(timestamp) {
  var dt = new Date(timestamp);
  return Date.getMonthShortName(dt.getMonth()) + " " + dt.getDate() + ", " + " " + dt.getFullYear();
}

Date.getDateFromGMT = function(timestamp) {
  //We need to turn the returned date from GMT to local.
  //getTimezoneOffset returns minutes, so we turn this into milliseconds
  var offset = (new Date()).getTimezoneOffset() * 60000;
  return new Date(timestamp + offset);
}

// Array Remove - By John Resig (MIT Licensed)
//http://ejohn.org/blog/javascript-array-remove/
Array.prototype.remove = function(from, to) {
  var rest = this.slice((to || from) + 1 || this.length);
  this.length = from < 0 ? this.length + from : from;
  return this.push.apply(this, rest);
};


//Stolen from:
//http://stackoverflow.com/questions/149055/how-can-i-format-numbers-as-money-in-javascript
//There are some really nice number formatting routines included with DOJO, but I can't get the
//Internationalization to work correctly with our custom build process.  We should revisit this later
Number.prototype.format = function(c, d, t){
  var n = this, c = isNaN(c = Math.abs(c)) ? 2 : c, d = d == undefined ? "," : d, t = t == undefined ? "." :
    t, s = n < 0 ? "-" : "", i = parseInt(n = Math.abs(+n || 0).toFixed(c)) + "", j = (j = i.length) > 3 ? j % 3 : 0;
  return s + (j ? i.substr(0, j) + t : "") + i.substr(j).replace(/(\d{3})(?=\d)/g, "$1" + t) + (c ? d +
    Math.abs(n - i).toFixed(c).slice(2) : "");
};

//  Cross-browser compatibility.  This patch allows the graph to be displayed on older browsers.
//  Adapted from a code fragment copied on January 23, 2012 from:
//  https://developer.mozilla.org/en/JavaScript/Reference/Global_Objects/Object/keys
if (!Object.keys) {
  Object.keys = function(o) {
    if (o !== Object(o)) {
      throw new TypeError('Object.keys called on non-object');
    }

    var ret=[],p;
    for(p in o) {
      if(Object.prototype.hasOwnProperty.call(o,p)) {
        ret.push(p);
      }
    }
    return ret;
  }
}