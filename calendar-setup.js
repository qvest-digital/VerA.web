/*  Copyright Mihai Bazon, 2002, 2003  |  http://students.infoiasi.ro/~mishoo
 * ---------------------------------------------------------------------------
 *
 * The DHTML Calendar
 *
 * Details and latest version at:
 * http://students.infoiasi.ro/~mishoo/site/calendar.epl
 *
 * Feel free to use this script under the terms of the GNU Lesser General
 * Public License, as long as you do not remove or alter this notice.
 *
 * This file defines helper functions for setting up the calendar.  They are
 * intended to help non-programmers get a working calendar on their site
 * quickly.
 */

// $Id: calendar-setup.js,v 1.1 2003/07/03 18:29:11 mishoo Exp $

/**
 *  This function "patches" an input field (or other element) to use a calendar
 *  widget for date selection.
 *
 *  The "params" is a single object that can have the following properties:
 *
 *    prop. name   | description
 *  -------------------------------------------------------------------------------------------------
 *   inputField    | the ID of an input field to store the date
 *   displayArea   | the ID of a DIV or other element to show the date
 *   button        | ID of a button or other element that will trigger the calendar
 *   eventName     | event that will trigger the calendar, without the "on" prefix (default: "click")
 *   ifFormat      | date format that will be stored in the input field
 *   daFormat      | the date format that will be used to display the date in displayArea
 *   singleClick   | (true/false) wether the calendar is in single click mode or not (default: true)
 *   mondayFirst   | (true/false) if true Monday is the first day of week, Sunday otherwise (default: false)
 *   align         | alignment (default: "Bl"); if you don't know what's this see the calendar documentation
 *   range         | array with 2 elements.  Default: [1900, 2999] -- the range of years available
 *
 *  None of them is required, they all have default values.  However, if you
 *  pass none of "inputField", "displayArea" or "button" you'll get a warning
 *  saying "nothing to setup".
 */
Calendar.setup = function (params) {
	function param_default(pname, def) { if (typeof params[pname] == "undefined") { params[pname] = def; } };

	param_default("inputField",    null);
	param_default("displayArea",   null);
	param_default("button",        null);
	param_default("eventName",     "click");
	param_default("ifFormat",      "y/mm/dd");
	param_default("daFormat",      "y/mm/dd");
	param_default("singleClick",   true);
	param_default("mondayFirst",   false);
	param_default("align",         "Bl");
	param_default("range",         [1900, 2999]);

	var tmp = ["inputField", "displayArea", "button"];
	for (var i in tmp) {
		if (typeof params[tmp[i]] == "string") {
			params[tmp[i]] = document.getElementById(params[tmp[i]]);
		}
	}
	if (!(params.inputField || params.displayArea || params.button)) {
		alert("Calendar.setup:\n  Nothing to setup (no fields found).  Please check your code");
		return false;
	}

	function onSelect(cal) {
		if (cal.params.inputField) {
			cal.params.inputField.value = cal.date.print(cal.params.ifFormat);
		}
		if (cal.params.displayArea) {
			cal.params.displayArea.innerHTML = cal.date.print(cal.params.daFormat);
		}
		if (cal.params.singleClick && cal.dateClicked) {
			cal.callCloseHandler();
		}
	};

	var triggerEl = params.button || params.displayArea || params.inputField;
	triggerEl["on" + params.eventName] = function() {
		var dateEl = params.inputField || params.displayArea;
		var dateFmt = params.inputField ? params.ifFormat : params.daFormat;
		if (!window.calendar) {
			window.calendar = new Calendar(params.mondayFirst, null, onSelect, function(cal) { cal.hide(); });
			window.calendar.create();
		} else {
			window.calendar.hide();
		}
		window.calendar.setRange(params.range[0], params.range[1]);
		window.calendar.params = params;
		window.calendar.setDateFormat(dateFmt);
		window.calendar.parseDate(dateEl.value || dateEl.innerHTML);
		window.calendar.showAtElement(params.displayArea || params.inputField, params.align);
		return false;
	};
};
