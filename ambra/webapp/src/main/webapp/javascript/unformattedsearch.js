/*
 * $HeadURL$
 * $Id$
 *
 * Copyright (c) 2006-2010 by Public Library of Science
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
 * ambra.unformattedsearch
 *
 * Methods used by the Query Builder page to generate the "unformattedQuery" which is submitted
 *   to a query engine.
 **/
ambra.unformattedsearch = {

  Config: {
    // Query Builder elements
    idFormUnformattedSearch:'unformattedSearchFormId', // The Query Builder HTML form

    idFieldUnformattedQuery:'unformattedQueryId',
    idFieldQueryField:'queryFieldId',
    idFieldQueryTerm:'queryTermId',
    idFieldStartDateAsString:'startDateAsStringId',
    idFieldEndDateAsString:'endDateAsStringId',

    idButtonAnd:'queryConjunctionAndId',
    idButtonOr:'queryConjunctionOrId',
    idButtonNot:'queryConjunctionNotId',
    idButtonSearch:'buttonSearchId',
    idButtonSearch2:'buttonSearchId2',

    idJournalsAll:'journalsOpt_all',
    idJournalsSlct:'journalsOpt_slct',
    idFsJournalsOpt:'fsJournalOpt',

    idSubjectsAll:'subjectOption_all',
    idSubjectsSome:'subjectOption_some',
    idFsSubjectOpt:'fsSubjectOpt',

    idArticleTypeAll:'articleType_all',
    idArticleTypeOne:'articleType_one',
    idFsArticleTypeOpt:'fsarticleTypOpt',

    // Find An Article elements
    idFormFindAnArticleSearch:'quickFind', // The Find An Article HTML form

    idfilterJournalsPicklist:'filterJournalsPicklistId',
    idVolume:'volumeId',
    idELocationId:'eLocationIdId',
    idId:'idId',

    idButtonGo:'buttonGoId', // Find An Article form "submit" button.

    // Display control elements
    idQueryTermDivBlock:'queryTermDivBlockId',
    idStartAndEndDateDivBlock:'startAndEndDateDivBlockId',

    idClearUnformattedQueryButtonId:'clearUnformattedQueryButtonId',
    idClearFiltersButtons: [ 'clearFiltersButtonId1', 'clearFiltersButtonId2' ]
  },

  unformattedQuery:null, // The String which is submitted to the query engine
  queryField:null,       // The picklist of searchable fields in the query engine
  queryTerm:null,        // The value which "queryField"

  // Do NOT put Date variables (e.g., startDateAsString) up here in global scope
  //   because variables by the same name are used for Advanced Search, meaning that there could be
  //   name-space collision with the Javascript for the Advanced Search page.
  
  init: function() {
    unformattedQuery = dojo.byId(ambra.unformattedsearch.Config.idFieldUnformattedQuery);
    queryField = dojo.byId(ambra.unformattedsearch.Config.idFieldQueryField);
    queryTerm = dojo.byId(ambra.unformattedsearch.Config.idFieldQueryTerm);

    Calendar.setup({
      inputField     :    ambra.unformattedsearch.Config.idFieldStartDateAsString, // id of the input field
      ifFormat       :    "%Y-%m-%d",                                              // format of the input field
      eventName      :    "focus",
      step           :    1       // show all years in drop-down boxes (instead of every other year as default)
    });
    Calendar.setup({
      inputField     :    ambra.unformattedsearch.Config.idFieldEndDateAsString,  // id of the input field
      ifFormat       :    "%Y-%m-%d",                                             // format of the input field
      eventName      :    "focus",
      step           :    1      // show all years in drop-down boxes (instead of every other year as default)
    });

    dojo.connect(dojo.byId(ambra.unformattedsearch.Config.idFieldQueryField), "onchange", this.enableQueryTermAndDateFormFields);

    dojo.connect(dojo.byId(ambra.unformattedsearch.Config.idButtonAnd), "onclick", ambra.unformattedsearch.addToUnformattedQuery);
    dojo.connect(dojo.byId(ambra.unformattedsearch.Config.idButtonOr), "onclick", ambra.unformattedsearch.addToUnformattedQuery);
    dojo.connect(dojo.byId(ambra.unformattedsearch.Config.idButtonNot), "onclick", ambra.unformattedsearch.addToUnformattedQuery);
    // hijack form submission for validation...
    dojo.connect(dojo.byId(ambra.unformattedsearch.Config.idButtonSearch), "onclick", ambra.unformattedsearch.onSubmitHandler);
    dojo.connect(dojo.byId(ambra.unformattedsearch.Config.idButtonSearch2), "onclick", ambra.unformattedsearch.onSubmitHandler);
    // submit the form when the user clicks "enter"
    dojo.connect(dojo.byId(ambra.unformattedsearch.Config.idFieldUnformattedQuery), "onkeypress", ambra.unformattedsearch.submitFormOnEnter);
    // add ( queryTerm or ( startDate and endDate ) ) and submit the form when the user clicks "enter"
    dojo.connect(dojo.byId(ambra.unformattedsearch.Config.idFieldQueryTerm), "onkeypress", ambra.unformattedsearch.addQueryClauseAndSubmitFormOnEnter);
    dojo.connect(dojo.byId(ambra.unformattedsearch.Config.idFieldStartDateAsString), "onkeypress", ambra.unformattedsearch.addQueryClauseAndSubmitFormOnEnter);
    dojo.connect(dojo.byId(ambra.unformattedsearch.Config.idFieldEndDateAsString), "onkeypress", ambra.unformattedsearch.addQueryClauseAndSubmitFormOnEnter);

    // date part comment cue event bindings...
    dojo.connect(dojo.byId(ambra.unformattedsearch.Config.idFieldStartDateAsString), "onfocus", ambra.unformattedsearch.onFocusCommentCueInputHandler);
    dojo.connect(dojo.byId(ambra.unformattedsearch.Config.idFieldEndDateAsString), "onfocus", ambra.unformattedsearch.onFocusCommentCueInputHandler);

    // journals section...
    if(document.selection) {
      // IE
      dojo.connect(dojo.byId(ambra.unformattedsearch.Config.idJournalsAll), "onclick", ambra.unformattedsearch.onChangeJournals);
      dojo.connect(dojo.byId(ambra.unformattedsearch.Config.idJournalsSlct), "onclick", ambra.unformattedsearch.onChangeJournals);
    } else {
      // gecko et al.
      dojo.connect(dojo.byId(ambra.unformattedsearch.Config.idJournalsAll), "onchange", ambra.unformattedsearch.onChangeJournals);
      dojo.connect(dojo.byId(ambra.unformattedsearch.Config.idJournalsSlct), "onchange", ambra.unformattedsearch.onChangeJournals);
    }

    // subject categories section...
    if(document.selection) {
      // IE
      dojo.connect(dojo.byId(ambra.unformattedsearch.Config.idSubjectsAll), "onclick", ambra.unformattedsearch.onChangeSubjects);
      dojo.connect(dojo.byId(ambra.unformattedsearch.Config.idSubjectsSome), "onclick", ambra.unformattedsearch.onChangeSubjects);
    } else {
      // gecko et al.
      dojo.connect(dojo.byId(ambra.unformattedsearch.Config.idSubjectsAll), "onchange", ambra.unformattedsearch.onChangeSubjects);
      dojo.connect(dojo.byId(ambra.unformattedsearch.Config.idSubjectsSome), "onchange", ambra.unformattedsearch.onChangeSubjects);
    }

    //Article types section...
    if(document.selection) {
      // IE
      dojo.connect(dojo.byId(ambra.unformattedsearch.Config.idArticleTypeAll), "onclick", ambra.unformattedsearch.onChangeArticleType);
      dojo.connect(dojo.byId(ambra.unformattedsearch.Config.idArticleTypeOne), "onclick", ambra.unformattedsearch.onChangeArticleType);
    } else {
      // gecko et al.
      dojo.connect(dojo.byId(ambra.unformattedsearch.Config.idArticleTypeAll), "onchange", ambra.unformattedsearch.onChangeArticleType);
      dojo.connect(dojo.byId(ambra.unformattedsearch.Config.idArticleTypeOne), "onchange", ambra.unformattedsearch.onChangeArticleType);
    }

    ambra.unformattedsearch.tglSubjects();
    ambra.unformattedsearch.tglJournals();
    ambra.unformattedsearch.tglArticleType();

    //  Find An Article form
    dojo.connect(dojo.byId(ambra.unformattedsearch.Config.idButtonGo), "onclick", ambra.unformattedsearch.onSubmitFindAnArticleHandler);

    //Clear query buttons
    dojo.connect(dojo.byId(ambra.unformattedsearch.Config.idClearUnformattedQueryButtonId), "onclick", ambra.unformattedsearch.onClickClearQueryHandler);

    //Clear filter buttons
    for(var a = 0; a < ambra.unformattedsearch.Config.idClearFiltersButtons.length; a++) {
      var button = dojo.byId(ambra.unformattedsearch.Config.idClearFiltersButtons[a]);

      if(button != null) {
        dojo.connect(button, "onclick", ambra.unformattedsearch.onClickClearFilterHandler);
      }
    }

    dojo.connect(dojo.byId(ambra.unformattedsearch.Config.idClearUnformattedQueryButtonId), "onclick", ambra.unformattedsearch.onClickClearQueryHandler);
  },

  onClickClearFilterHandler: function(e) {
    console.log('onClickClearFilterHandler');
    dojo.byId(ambra.unformattedsearch.Config.idArticleTypeAll).checked = true;
    dojo.byId(ambra.unformattedsearch.Config.idSubjectsAll).checked = true;
    dojo.byId(ambra.unformattedsearch.Config.idJournalsAll).checked = true;

    ambra.unformattedsearch.tglSubjects();
    ambra.unformattedsearch.tglJournals();
    ambra.unformattedsearch.tglArticleType();
  },

  onClickClearQueryHandler: function(e) {
    dojo.byId(ambra.unformattedsearch.Config.idFieldUnformattedQuery).value = '';
    return true;
  },

  addToUnformattedQuery: function(e) {
      queryTerm.value = ambra.unformattedsearch.trim(queryTerm.value);

      // If no input, then complain to the user
      if (queryField.value.length < 1) {
        alert('Please select a Search Field from the picklist');
        queryField.focus();
        return false;
      } else if ( ( ! ambra.unformattedsearch.isQueryFieldADate() ) 
          && ( queryTerm.value.length < 1 || queryTerm.value == 'Enter search terms')) {
        alert('Please enter a Search Term in the text field next to the picklist');
        queryTerm.focus();
        return false;
      } else if (ambra.unformattedsearch.isQueryFieldADate() && ! ambra.unformattedsearch.validateDateRange()) {
        return false;
      }

      unformattedQuery.value = ambra.unformattedsearch.trim(unformattedQuery.value);

      if (unformattedQuery.value.length > 0) {
        // Wrap the preceding statement in parenthesis to group it apart from the new statement
        unformattedQuery.value = '(' + unformattedQuery.value + ') ' + e.target.value + ' ';
      } else if (e.target.value == 'NOT') { // Solr syntax allows a query to start with a NOT statement
        unformattedQuery.value = e.target.value + ' ';
      }

      // Date input is formatted differently from text input
      if (ambra.unformattedsearch.isQueryFieldADate()) {
        return ambra.unformattedsearch.addDateToUnformattedQuery();
      }

      unformattedQuery.value = unformattedQuery.value + queryField.value + ':';

      // If queryTerm.value has multiple terms, then wrap queryTerm.value in double quotes
      if (queryTerm.value.search(/\s/) > -1) {
        unformattedQuery.value = unformattedQuery.value
            + '"' + ambra.unformattedsearch.escapeSpecialWordsAndCharacters(queryTerm.value) + '" ';
      } else {
        unformattedQuery.value = unformattedQuery.value
            + ambra.unformattedsearch.escapeSpecialWordsAndCharacters(queryTerm.value) + ' ';
      }

      queryTerm.value = '';
      queryTerm.focus();
  },

  addDateToUnformattedQuery: function() {
    var startDateAsString = dojo.byId(ambra.unformattedsearch.Config.idFieldStartDateAsString);
    var endDateAsString = dojo.byId(ambra.unformattedsearch.Config.idFieldEndDateAsString);

    unformattedQuery.value = unformattedQuery.value + queryField.value
        + ':[' + startDateAsString.value + 'T00:00:00Z TO ' + endDateAsString.value + 'T23:59:59Z] ';
    startDateAsString.value = '';
    endDateAsString.value = '';
    queryField.focus();
    return true;
  },

  // Determines if the Solr field defined by queryField takes date-formatted input
  isQueryFieldADate: function() {
    if (queryField.value == 'publication_date' || queryField.value == 'received_date' || queryField.value == 'accepted_date') {
      return true;
    } else {
      return false;
    }
  },

  enableQueryTermAndDateFormFields: function() {
    if (ambra.unformattedsearch.isQueryFieldADate()) {
      dojo.byId(ambra.unformattedsearch.Config.idQueryTermDivBlock).style.display = 'none';
      queryTerm.disabled = true;

      dojo.byId(ambra.unformattedsearch.Config.idStartAndEndDateDivBlock).style.display = 'inline';
      dojo.byId(ambra.unformattedsearch.Config.idFieldStartDateAsString).disabled = false;
      dojo.byId(ambra.unformattedsearch.Config.idFieldEndDateAsString).disabled = false;
      dojo.byId(ambra.unformattedsearch.Config.idFieldStartDateAsString).focus();
    } else {
      dojo.byId(ambra.unformattedsearch.Config.idStartAndEndDateDivBlock).style.display = 'none';
      dojo.byId(ambra.unformattedsearch.Config.idFieldStartDateAsString).disabled = true;
      dojo.byId(ambra.unformattedsearch.Config.idFieldEndDateAsString).disabled = true;

      dojo.byId(ambra.unformattedsearch.Config.idQueryTermDivBlock).style.display = 'inline';
      queryTerm.disabled = false;
      queryTerm.focus();
    }
  },

  // Trim whitespace from left and right sides of the "stringToBeTrimmed" String.
  trim: function (stringToBeTrimmed) {
    return stringToBeTrimmed.replace( /^\s*/, "" ).replace( /\s*$/, "" );
  },

  //  TODO: Check that these are all of the characters that should be escaped.
  //  Escape all characters that are special to Solr, except * and ? which are wildcards
  //  Also change to lower-case all special Solr words: upper-case AND, OR, NOT, and TO
  escapeSpecialWordsAndCharacters: function (stringToBeEscaped) {
    var stringToBeReturned = stringToBeEscaped.replace(/([:!&"'\^\+\-\|\(\)\[\]\{\}\\])/g, "\\$1");
    stringToBeReturned = stringToBeReturned.replace(/^TO$/g, "to").replace(/^TO /g, "to ").replace(/ TO$/g, " to").replace(/ TO /g, " to ");
    stringToBeReturned = stringToBeReturned.replace(/^AND$/g, "and").replace(/^AND /g, "and ").replace(/ AND$/g, " and").replace(/ AND /g, " and ");
    stringToBeReturned = stringToBeReturned.replace(/^OR$/g, "or").replace(/^OR /g, "or ").replace(/ OR$/g, " or").replace(/ OR /g, " or ");
    stringToBeReturned = stringToBeReturned.replace(/^NOT$/g, "not").replace(/^NOT /g, "not ").replace(/ NOT$/g, " not").replace(/ NOT /g, " not ");
    return stringToBeReturned;
  },

  // Test whether the event "e" was was the "enter" key being pressed, if so, then submit the form.
  // If the form was submitted, then return "true".
  // If the form was not submitted, then return false.
  submitFormOnEnter: function (e) {
    if (e.keyCode == 13) {
      if (ambra.unformattedsearch.trim(unformattedQuery.value).length > 0) {
        ambra.unformattedsearch.onSubmitHandler(e);
        return true;
      }
    }
    return false;
  },

  // Test whether the event "e" was was the "enter" key being pressed.
  // If so and if there is a valid content for creating a new query clause, then add
  // a new "AND" query clause to "unformattedQuery", then submit the form.
  // If the form was submitted, then return "true".
  // If the form was not submitted, then return false.
  addQueryClauseAndSubmitFormOnEnter: function (e) {
    if (e.keyCode == 13) {
      var isQueryClauseValid = false;
      if ((( ! ambra.unformattedsearch.isQueryFieldADate()) && queryTerm.value.length > 0 && queryTerm.value != 'Enter search terms' )
          || (ambra.unformattedsearch.isQueryFieldADate() && ambra.unformattedsearch.isValidDateRange())) {
        isQueryClauseValid = true;
      }
      dojo.byId(ambra.unformattedsearch.Config.idButtonAnd).click();

      if (isQueryClauseValid) {
        ambra.unformattedsearch.submitFormOnEnter(e);
      }
    }
    return false;
  },

  //  Validation functions

  /**
   * Ensures that startDateAsString and endDateAsString are dates and that they form a useful range.
   * Returns an error message if there is a problem.
   * Returns an empty String if startDateAsString and endDateAsString both pass all the tests.
   */
  validateDateRange: function() {
    // validate published date range (if applicable)
    var startDateAsString = dojo.byId(ambra.unformattedsearch.Config.idFieldStartDateAsString);
    var endDateAsString = dojo.byId(ambra.unformattedsearch.Config.idFieldEndDateAsString);
    // Convenient to do this trimming here.
    startDateAsString.value = ambra.unformattedsearch.trim(startDateAsString.value);
    endDateAsString.value = ambra.unformattedsearch.trim(endDateAsString.value);

    if (startDateAsString.value.length < 1 && endDateAsString.value.length < 1) {
      alert('Please enter a Start Date and an End Date');
      startDateAsString.focus();
      return false;
    } else if (startDateAsString.value.length < 1) {
      alert('Please enter a Start Date in the left-hand date field');
      startDateAsString.focus();
      return false;
    } else if (endDateAsString.value.length < 1) {
      alert('Please enter an End Date in the right-hand date field');
      endDateAsString.focus();
      return false;
    }

    var startDateAsDate = new Date(startDateAsString.value.replace(/-/g, "/"));
    var endDateAsDate = new Date(endDateAsString.value.replace(/-/g, "/"));

    //  Make sure there is a Start date and an End date.
    if (isNaN(startDateAsDate.getMilliseconds()) && isNaN(endDateAsDate.getMilliseconds())) {
      alert('Please choose valid Start and End Dates');
      startDateAsString.focus();
      return false;
    } else if (isNaN(startDateAsDate.getMilliseconds())) {
      alert('Please choose a valid Start Date');
      startDateAsString.focus();
      return false;
    } else if (isNaN(endDateAsDate.getMilliseconds())) {
      alert('Please choose a valid End Date');
      endDateAsString.focus();
      return false;
    } else if (startDateAsDate.getTime() > endDateAsDate.getTime()) {
      alert('The Start Date must occur before the End Date');
      startDateAsString.focus();
      return false;
    }
    return true;
  },

  /**
   * Just like the "validateDateRange" function, but does not popup "alert" windows, nor does it change focus.
   */
  isValidDateRange: function() {
    // validate published date range (if applicable)
    // Convenient to do this trimming here.
    var startDateAsString = ambra.unformattedsearch.trim(dojo.byId(ambra.unformattedsearch.Config.idFieldStartDateAsString).value);
    var endDateAsString = ambra.unformattedsearch.trim(dojo.byId(ambra.unformattedsearch.Config.idFieldEndDateAsString).value);

    if (startDateAsString.length < 1 || endDateAsString.length < 1) {
      return false;
    }
    
    var startDateAsDate = new Date(startDateAsString.replace(/-/g, "/"));
    var endDateAsDate = new Date(endDateAsString.replace(/-/g, "/"));

    //  Make sure there is a Start date and an End date.
    if (isNaN(startDateAsDate.getMilliseconds()) || isNaN(endDateAsDate.getMilliseconds())) {
      return false;
    } else if (startDateAsDate.getTime() > endDateAsDate.getTime()) {
      return false;
    }
    return true;
  },

 onSubmitHandler: function(e) {
   dojo.stopEvent(e);
   // If there is valid content in the queryTerm or Date fields, then add this content to
   // the unformattedQuery field before submitting the form.
   if ( (( ! ambra.unformattedsearch.isQueryFieldADate() ) && queryTerm.value.length > 0 && queryTerm.value != 'Enter search terms')
       || (ambra.unformattedsearch.isQueryFieldADate() && ambra.unformattedsearch.isValidDateRange() )) {
     ambra.unformattedsearch.addToUnformattedQuery(e);
   }
   if (ambra.unformattedsearch.validateFormBeforeSubmit()) {
     dojo.byId(ambra.unformattedsearch.Config.idFormUnformattedSearch).submit();
     return true;
   } else {
     return false;
   }
 },

  validateFormBeforeSubmit: function() {
    if (ambra.unformattedsearch.trim(unformattedQuery.value).length < 1) {
      alert('Please create a query');
      return false;
    } else {
      return true;
    }
  },

  onSubmitFindAnArticleHandler: function(e) {
    dojo.stopEvent(e);
    if (ambra.unformattedsearch.validateFindAnArticleFormBeforeSubmit()) {
      dojo.byId(ambra.unformattedsearch.Config.idFormFindAnArticleSearch).submit();
      return true;
    } else {
      return false;
    }
  },

  validateFindAnArticleFormBeforeSubmit: function() {
    if (ambra.unformattedsearch.trim(dojo.byId(ambra.unformattedsearch.Config.idVolume).value).length < 1
        && ambra.unformattedsearch.trim(dojo.byId(ambra.unformattedsearch.Config.idELocationId).value).length < 1
        && ambra.unformattedsearch.trim(dojo.byId(ambra.unformattedsearch.Config.idId).value).length < 1) {
      alert('Please enter appropriate values in one or more of the fields.');
      return false;
    } else {
      return true;
    }
  },

  // The rest of this code is used by the Subject Category and Journal radio button and check-boxes.

  getCueText: function(inptId) {
    if(inptId.indexOf(ambra.unformattedsearch.Config.idMonthPart) > 0) {
      return ambra.unformattedsearch.Config.monthCue;
    }
    else if(inptId.indexOf(ambra.unformattedsearch.Config.idDayPart) > 0) {
      return ambra.unformattedsearch.Config.dayCue;
    }
    else {
      return ambra.unformattedsearch.Config.yearCue;
    }
  },

  onFocusCommentCueInputHandler: function(e) {
    ambra.unformattedsearch.onFocusCommentCueInput(e.target);
    dojo.stopEvent(e);
    return false;
  },

  onBlurCommentCueInputHandler: function(e) {
    ambra.unformattedsearch.onBlurCommentCueInput(e.target);
    dojo.stopEvent(e);
    return false;
  },

  onBlurCommentCueInput: function(inpt) {
    if(inpt.value == '') inpt.value = this.getCueText(inpt.id);
  },

  onFocusCommentCueInput: function(inpt) {
    if(inpt.value == this.getCueText(inpt.id)) inpt.value = '';
  },

  onChangeJournals: function(e) {
    console.log("onChangeJournals");
    ambra.unformattedsearch.tglJournals();
    return true;
  },

  tglJournals: function() {
    console.log("tglJournals");
    var rbAll = dojo.byId(ambra.unformattedsearch.Config.idJournalsAll);
    var rbSlct = dojo.byId(ambra.unformattedsearch.Config.idJournalsSlct);
    var enable = rbSlct.checked;
    var fs = dojo.byId(ambra.unformattedsearch.Config.idFsJournalsOpt);
    if(enable) ambra.formUtil.enableFormFields(fs); else ambra.formUtil.disableFormFields(fs);
  },

  onChangeSubjects: function(e) {
    ambra.unformattedsearch.tglSubjects();
    return true;
  },

  tglArticleType: function() {
    var rbAll = dojo.byId(ambra.unformattedsearch.Config.idArticleTypeAll);
    var rbOne = dojo.byId(ambra.unformattedsearch.Config.idArticleTypeOne);
    var enable = rbOne.checked;
    var fs = dojo.byId(ambra.unformattedsearch.Config.idFsArticleTypeOpt);
    if(enable) ambra.formUtil.enableFormFields(fs); else ambra.formUtil.disableFormFields(fs);
  },

  onChangeArticleType: function(e) {
    ambra.unformattedsearch.tglArticleType();
    return true;
  },

  tglSubjects: function() {
    var rbAll = dojo.byId(ambra.unformattedsearch.Config.idSubjectsAll);
    var rbSlct = dojo.byId(ambra.unformattedsearch.Config.idSubjectsSome);
    var enable = rbSlct.checked;
    var fs = dojo.byId(ambra.unformattedsearch.Config.idFsSubjectOpt);
    if (enable) ambra.formUtil.enableFormFields(fs); else ambra.formUtil.disableFormFields(fs);
  }
};
dojo.addOnLoad(function() { ambra.unformattedsearch.init(); });
