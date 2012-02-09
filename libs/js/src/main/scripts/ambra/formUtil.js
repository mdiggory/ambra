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
 * ambra.formUtil
 * 
 * @author		Joycelyn Chung			joycelyn@orangetowers.com
 **/
dojo.provide("ambra.formUtil");
dojo.require("ambra.general");
ambra.formUtil = {
  
  /**
   * ambra.formUtil.textCues
   * 
   * This turns the form field cues on and off.  It also resets them.  It takes the form object
   * and the text string for each state.
   * 
   * @param		 formEl			Form object			Form object
   * @param		 textCue		String					Text string to be displayed or removed.  Must match the
   * 																			string that's currently in the field.
   */
  textCues: {
  	on: function ( formEl, textCue ) {
    if (formEl.value == "")
    	formEl.value = textCue;
	  },
	  
	  off: function ( formEl, textCue ) {
	    if (formEl.value == textCue)
	    	formEl.value = "";
	  },
	  
	  reset: function ( formEl, textCue ){
	    formEl.value = textCue;
	  }
  },
  
  /**
   * ambra.formUtil.toggleFieldsByClassname(String toggleClassOn, String toggleClassOff)
   * 
   * Toggles all elements with a class attribute containing toggleClassOn/toggleClassOff on and off,
   * respectively.
   * 
   * @param			toggleClassOn			String				Text string of the class name that will be toggled on.
   * @param			toggleClassOff		String				Text string of the class name that will be toggled off.
   */
  toggleFieldsByClassname: function ( toggleClassOn, toggleClassOff ) {
    var targetElOn = document.getElementsByTagAndClassName(null, toggleClassOn);
    var targetElOff = document.getElementsByTagAndClassName(null, toggleClassOff);

    for (var i=0; i<targetElOn.length; i++) {
      targetElOn[i].style.display = "block";
    }

    for (var i=0; i<targetElOff.length; i++) {
      targetElOff[i].style.display = "none";
    }
  },
  
  /**
   * ambra.formUtil.checkFieldStrLength(Form field  fieldObj, Integer maxLength)
   * 
   * Checks the specified fieldObj value exceeds the maxLength.
   * 
   * @param			fieldObj		Form field object			A form input object.
   * @param			maxLength		Integer								Maximum length the value can be.
   * 
   * @return		-1					Integer								The field value did not exceed the maxLength.
   * @return		 0					Integer								The field value has exceeded the maxLength.
   */
  checkFieldStrLength: function ( fieldObj, maxLength ) {
    if(fieldObj.value && fieldObj.value.length > maxLength) {
      alert("Your comment exceeds the allowable limit of " + maxLength + " characters by " + (fieldObj.value.length - maxLength) + " characters.");
      fieldObj.focus();
      return 0;
    }
    else {
      return -1;
    }
  },
  
  /**
   * Enables or disables all found form fields found under a given dom node except hidden fields.
   */
  _tglFields: function(node, bEnable){
    var n = bEnable? 'false' : 'true';
    dojo.query("input[type='text'],input[type='textarea'],input[type='radio'],input[type='checkbox'],input[type='button'],select,textarea", node).forEach('item.disabled='+n+';item.locked='+n+';');
  },
  
  /**
   * ambra.formUtil.disableFormFields(node)
   * 
   * Disables all found child form fields under the given dom node.
   * 
   * @param     node     A dom node
   */
  disableFormFields: function (node) {
    ambra.formUtil._tglFields(node, false);
  },
  
  /**
   * ambra.formUtil.enableFormFields(node)
   * 
   * Enables all found child form fields under the given dom node.
   * 
   * @param     node     A dom node
   */
  enableFormFields: function (node) {
    ambra.formUtil._tglFields(node, true);
  },
  
  /**
   * ambra.formUtil.createHiddenFields(Form formObj)
   * 
   * Method goes through the form object formObj looking for elements other than hidden fields,
   * buttons and submits and where the names are not null.  The elements that does meet the 
   * criteria has a hidden counterpart created and attached at the end of the form.  
   */
/*  createHiddenFields: function (formObj) {
    for (var i=0; i<formObj.elements.length; i++) {
      if(formObj.elements[i].type != 'hidden' && 
         formObj.elements[i].type != 'button' && 
         formObj.elements[i].type != 'submit' && 
         formObj.elements[i].name != null) {
        if (formObj.elements["hdn" + formObj.elements[i].name] == null) {
          var newHdnEl = document.createElement("input");
          
          newHdnEl.type = "hidden";
          newHdnEl.name = "hdn" + formObj.elements[i].name;
          
          if (formObj.elements[i].type == "radio") {
            var radioName = formObj.elements[i].name;
            
            for (var n=0; n<formObj.elements[radioName].length; n++) {
              if (formObj.elements[radioName][n].checked) {
                newHdnEl.value = formObj.elements[radioName][n].value;

                break;
              }
            }
          }
          else if (formObj.elements[i].type == "checkbox") {
            var checkboxName = formObj.elements[i].name;
            
            for (var n=0; n<formObj.elements[checkboxName].length; n++) {
              if (formObj.elements[checkboxName][n].checked) {
                newHdnEl.value = (newHdnEl.value == "") ? formObj.elements[checkboxName].value : newHdnEl.value + "," + formObj.elements[checkboxName].value;
              }
            }
          }
          else if (formObj.elements[i].type == "select-one") {
            //alert("formObj.elements[" + i + "][" + formObj.elements[i].selectedIndex + "].value = " + formObj.elements[i][formObj.elements[i].selectedIndex].value);
            newHdnEl.value = formObj.elements[i][formObj.elements[i].selectedIndex].value; 
          }
          else {
            newHdnEl.value = formObj.elements[i].value;
          }
    
          formObj.appendChild(newHdnEl);
        }
      }
    }
    
  },
*/  
  
  /**
   * ambra.formUtil.createFormValueObject(Form formObj)
   * 
   * Method goes through the form object and looks for all fields that are not hidden,
   * buttons, or submits.  For all other fields, the field names are stored as keys while
   * their values are stored as values in an associative array.  Most of the values in 
   * the associative arrays are strings with the exception of checkboxes.  Since in a
   * checkbox, you can have more than one value selected.  The value from the checkboxes
   * are stored in an array.  That array is then stored as the value mapped to checkbox
   * fields.
   * 
   * @param			formObj						Form object					Form object
   * 
   * @return		formValueObject		Associative array		Map of all the field names and their values.
   */
  createFormValueObject: function (formObj) {
    var formValueObject = new Object();
    
    for (var i=0; i<formObj.elements.length; i++) {
      if(formObj.elements[i].type != 'hidden' && 
         formObj.elements[i].type != 'button' && 
         formObj.elements[i].type != 'submit' && 
         formObj.elements[i].name != null) {
        
        if (formObj.elements[i].type == "radio") {
          var radioName = formObj.elements[i].name;
          var radioObj = formObj.elements[radioName];
          
          for (var n=0; n<radioObj.length; n++) {
            if (radioObj[n].checked) {
              formValueObject[radioObj[n].name] = radioObj[n].value;
  
              break;
            }
          }
        }
        else if (formObj.elements[i].type == "checkbox") {
          var checkboxName = formObj.elements[i].name;
          var checkboxObj = formObj.elements[checkboxName];
          
          var cbArray = new Array();
          if (checkboxObj.length) {
            for (var n=0; n<checkboxObj.length; n++) {
              if (checkboxObj[n].checked) {
                 cbArray.push(checkboxObj[n].value);
              }
            }
            
            formValueObject[checkboxName] = cbArray;
          }
          else {
            formValueObject[checkboxObj.name] = checkboxObj.value;
          }
        }
        else if (formObj.elements[i].type == "select-one") {
          formValueObject[formObj.elements[i].name] = formObj.elements[i][formObj.elements[i].selectedIndex].value;
        }
        else {
          formValueObject[formObj.elements[i].name] = formObj.elements[i].value;
        }
      }
    }

    return formValueObject;
  },
  
  /**
   * ambra.formUtil.hasFieldChange(Form formObj)
   * 
   * Checks the fields in formObj that are not hidden, buttons, or submits, to see they have changed.
   */
/*  hasFieldChange: function (formObj) {
    var thisChanged = false;
    
    for (var i=0; i<formObj.elements.length; i++) {
      if(formObj.elements[i].type != 'hidden' && 
         formObj.elements[i].type != 'button' && 
         formObj.elements[i].type != 'submit' && 
         formObj.elements[i].name != null) {
        
        var hdnFieldName = "hdn" + formObj.elements[i].name;
        
        //alert("formObj.elements[" + hdnFieldName + "] = " + formObj.elements[hdnFieldName]);
        
        if (formObj.elements[hdnFieldName] != null) {
          
          //alert("formObj.elements[" + i + "].type = " + formObj.elements[i].type);
          if (formObj.elements[i].type == "radio") {
            var radioName = formObj.elements[i].name;
            
            for (var n=0; n<formObj.elements[radioName].length; n++) {
              if (formObj.elements[radioName][n].checked) {
                alert("formObj.elements[" + radioName + "][" + n + "].value = " + formObj.elements[radioName][n].value + "\n" +
                      "formObj.elements[" + hdnFieldName + "].value = " + formObj.elements[hdnFieldName].value);
                if (formObj.elements[radioName][n].value != formObj.elements[hdnFieldName].value) {
                  thisChanged = true;
                  break;
                }
              }
            }
          }
          else if (formObj.elements[i].type == "checkbox") {
            var checkboxName = formObj.elements[i].name;
            
            var hdnCheckboxList = formObj.elements[hdnFieldName].value.split(",");
            
            for (var n=0; n<formObj.elements[checkboxName].length; n++) {
              if (formObj.elements[checkboxName][n].checked) {
                var isCheckedPreviously = false;
                
                for (var p=0; p<hdnCheckboxList; p++) {
                  if (formObj.elements[checkboxName][n].value == hdnCheckboxList[p])
                    isCheckedPreviously = true;
                }
                
                alert("isCheckedPreviously = " + isCheckedPreviously);
                if (!isCheckedPreviously) {
                  thisChanged = true;
                  break;
                }
              }
            }
          }
          else if (formObj.elements[i].type == "select-one") {
            alert("formObj.elements[" + i + "][" + formObj.elements[i].selectedIndex + "].value = " + formObj.elements[i][formObj.elements[i].selectedIndex].value + "\n" +
                  "formObj.elements[" + hdnFieldName + "].value = " + formObj.elements[hdnFieldName].value);
            if (formObj.elements[hdnFieldName].value != formObj.elements[i][formObj.elements[i].selectedIndex].value) {
              thisChanged = true; 
              break;
            }
          }
          else {
            alert("formObj.elements[" + i + "].value = " + formObj.elements[i].value + "\n" +
                  "formObj.elements[" + hdnFieldName + "].value = " + formObj.elements[hdnFieldName].value);
            if (formObj.elements[hdnFieldName].value != formObj.elements[i].value) {
              thisChanged = true;
              break;
            }
          }
        }
      }
    }
    
    //alert("thisChanged = " + thisChanged);
    
    return thisChanged;
  },

  removeHiddenFields: function (formObj) {
    alert("removeHiddenFields");
    for (var i=0; i<formObj.elements.length; i++) {
      if (formObj.elements[i].type == 'hidden') {
        ambra.domUtil.removeNode(formObj.elements[i]);
      }
    }
  },

  addItemInArray: function (array, item) {
    var foundItem = false;
    for (var i=0; i<array.length; i++) {
      alert("array[" + i + "] = " + array[i] + "\n" +
            "item = " + item);
      if (array[i] == item) 
        foundItem = true;
    }
    
    alert("foundItem = " + foundItem);
    
    if (!foundItem)
      array.push(item);
  },
  
  isItemInArray: function (array, item) {
    var foundItem = false;
    for (var i=0; i<array.length; i++) {
      if (array[i] == item) 
        foundItem = true;
    }
    
    if (foundItem)
      return true;
    else
      return false;
  },
*/  
  
  /**
   * ambra.formUtil.selectAllCheckboxes(Form field  srcObj, Form field  targetCheckboxObj)
   * 
   * If the form field srcObj has been selected, all the checkboxes in the form field targetCheckboxObj
   * gets selected.  When srcObj is not selected, all the checkboxes in targetCheckboxObj gets
   * deselected.
   * 
   * @param			srcObj							Form field				Checkbox field.
   * @param			targetCheckboxObj		Form field				Checkbox field.
   */
  selectAllCheckboxes: function (srcObj, targetCheckboxObj) {
    if (srcObj.checked) {
      for (var i=0; i<targetCheckboxObj.length; i++) {
        targetCheckboxObj[i].checked = true;
      }
    }
    else {
      for (var i=0; i<targetCheckboxObj.length; i++) {
        targetCheckboxObj[i].checked = false;
      }
    }
  },
  
  /**
   * ambra.formUtil.selectCheckboxPerCollection(Form field  srcObj, Form field  collectionObj)
   * 
   * Checks to see if all of the checkboxes in the collectionObj are selected.  If it is, select srcObj
   * also.  If all of the checkboxes in collectionObj are not selected, deselect srcObj.
   * 
   * @param			srcObj							Form field				Checkbox field.
   * @param			targetCheckboxObj		Form field				Checkbox field.
   */
  selectCheckboxPerCollection: function (srcObj, collectionObj) {
    var count = 0;
    
    for (var i=0; i<collectionObj.length; i++) {
      if (collectionObj[i].checked)
        count++;
    }
    
    srcObj.checked = (count == collectionObj.length) ? true : false;
  }
}
