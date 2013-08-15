function getPosX(e) {
	var x = 0;
	while (e) {
		x += e.offsetLeft;
		e = e.offsetParent;
	}
	return x;
}

function getPosY(e) {
	var x = 0;
	while (e) {
		x += e.offsetTop;
		e = e.offsetParent;
	}
	return x;
}

function isOver(layer1, layer2) {
	return (getPosY(layer1) + layer1.offsetHeight > getPosY(layer2));
}

function fixLayer() {
	var fm = document.getElementById('fixme')
	var sl = document.getElementsByTagName('select');
	for (var i = 0; i < sl.length; i++) {
		if (isOver(sl[i], fm)) {
			sl[i].style.visibility = 'hidden';
		} else {
			sl[i].style.visibility = 'visible';
		}
	}
	return 0;
}

function setModified(id) {
	id = (id == null) ? 'modified' : id + '-modified';
	document.getElementById(id).value = 'true';
}

function checkModified(id) {
	if (isModified(id)) {
		return confirm('Bearbeitung verwerfen?');
	} else {
		return true;
	}
}

function isModified(id) {
	id = (id == null) ? 'modified' : id + '-modified';
	if (document.getElementById(id).value == 'true') {
		return true;
	} else {
		return false;
	}
}

function navigateList(start) {
	$('input[name$="-select"]').prop('checked', false);
	var form = document.getElementById('formlist');
	if (form && form.elements['start']) {
		form.elements['start'].value = start;
		form.submit();
	} else {
		alert('form or field start NOT FOUND!');
	}
}

function navigateLimit(limit) {
	$('input[name$="-select"]').prop('checked', false);
	var form = document.getElementById('formlist');
	if (form && form.elements['start']) {
		form.elements['start'].value = 0;
	}
	if (form && form.elements['limit']) {
		form.elements['limit'].value = limit;
		form.submit();
	} else {
		alert('form or field limit NOT FOUND!');
	}
}

//Handler for SelectAll Toggling 
$(function(){
    $('#toggleAllSelect').change(function(){
        if ($(this).is(':checked')) {
        	$('input[name$="-select"]').prop('checked', true).each(function() {
        		this.onclick();
        	});
        } else {
        	$('input[name$="-select"]').prop('checked', false).each(function() {
        		this.onclick();
        	});
        	$('input[name$="-partner"]').prop('checked', false);
        }
    });
});

function smallResolution() {
	if (window.innerWidth) {
		return (window.innerWidth < 912);
	} else {
		return (document.body.offsetWidth < 912);
	}
}

function writeResolution(small, large) {
	document.writeln(smallResolution() ? small : large);
}

function selectAll(inp) {
	inp.focus();
	if (document.selection) {
		/*
		var sel = document.body.createRange();
		sel.select();
		*/
	} else {
		inp.selectionStart = 0;
		inp.selectionEnd = inp.value.length;
	}
}

$.fn.veraDisable = function() {
    this.filter('[vera-disabled]').focus(function (e) { // do not allow focus
        $(this).blur();
    }).change(function (e) { // reset form element to default value
            var $t = $(this);
            if ($t.is('select')) { // single selection box
                $t.val($t.find('option[selected]').val());
            } else if ($t.is('input[type=radio]')) { // radio group
                var group = $('input[type=radio][name=' + $t.attr('name') + ']');
                group.prop('checked', function () {
                    return $(this).is('[checked]');
                });
            } else if ($t.is('input[type=checkbox]')) { // checkbox
                $t.prop('checked', $t.is('[checked]'));
            } else { // text field / text area
                $t.val($t.attr('value'));
            }
        });
    return this;
}

/** disable all form elements of forms with attribute "vera-disabled".
 * This is because IE9 does not allow changing the text color of disabled input elements.
 * Also focus will be disabled (which is not possible with attribute "readonly").
 **/
$(function () {
    $(':input[vera-disabled]').veraDisable();
});

function disableForm(frm) {
	$(frm).find(':input').attr('vera-disabled', '').veraDisable();
}

function disableFormInput(fld) {
    $(fld).attr('vera-disabled', '').veraDisable();
}
function disableFormTextarea(fld) {
    $(fld).attr('vera-disabled', '').veraDisable();
}

function disableFormSelect(fld) {
    $(fld).attr('vera-disabled', '').veraDisable();
}

function disableFormRadiobox(fld) {
    $(fld).attr('vera-disabled', '').veraDisable();
}

function disableFormCheckbox(fld) {
    $(fld).attr('vera-disabled', '').veraDisable();
}

function enableFormSelect(fld) {
    $(fld).removeAttr('vera-disabled');
}

function enableFormInput(fld) {
    $(fld).removeAttr('vera-disabled');
}

function enableFormCheckbox(fld) {
    $(fld).removeAttr('vera-disabled');
}

function onMouseOverList(line) {
	line.style.backgroundColor = '#ffffc3';
}

function onMouseOutList(line) {
	line.style.backgroundColor = '';
}

function insertAtCursor(fld, text) {
	// IE
	if (document.selection) {
		fld.focus();
		sel = document.selection.createRange();
		sel.text = text;
	}
	// Mozilla, Firefox
	else if (fld.selectionStart || fld.selectionStart == '0') {
		var startPos = fld.selectionStart;
		var endPos = fld.selectionEnd;
		fld.value = fld.value.substring(0, startPos)
				+ text
				+ fld.value.substring(endPos, fld.value.length);
	// Alle anderen
	} else {
		fld.value += text;
	}
}

/**
 * Show a text below the page header line
 *
 * @param text
 */
var showInfo, showWarning, showSuccess, showConfirm, showConfirmYesNo;

(function () {

    /**
     * Create a formatted info text HTML div element with the given message text.
     *
     * @param htmlStr
     * @returns {*|jQuery|HTMLElement}
     */
    var createInfoHtml = function (htmlStr) {
        return $('<div class="hinweis grayBorder marginBottom20 notBold"><strong>Hinweis</strong><p>'
            + htmlStr + '</p></div>');
    };

    /**
     * Create a formatted warning text HTML div element with the given message text.
     *
     * @param htmlStr
     * @returns {*|jQuery|HTMLElement}
     */
    var createWarnHtml = function (htmlStr) {
        return $('<div class="msg errormsg">'
            + htmlStr + '</div>');
    };

    /**
     * Create a formatted success text HTML div element with the given message text.
     *
     * @param htmlStr
     * @returns {*|jQuery|HTMLElement}
     */
    var createSuccessHtml = function (htmlStr) {
        return $('<div class="msg successmsg">'
            + htmlStr + '</div>');
    };

    /**
     * Create a formatted confirm text HTML div element with the given message text.
     *
     * @param htmlStr
     * @returns {*|jQuery|HTMLElement}
     */
    var createConfirmHtml = function (htmlStr) {
        return $('<div class="msg errormsg">'
            + htmlStr + '</div>');
    };

    /**
     * Format all texts in <vera-info>, <vera-war>, ... tags on load of the current page
     */
    $(function () {
        var funcRemove = function () {
            $(this).remove();
        };
        /**
         * Format elements with given function and optionally make removable
         *
         * @param select CSS selector which defines the elements which are adapted
         * @param wrapFunc function to wrap the HTML in formatting container
         * @param removable true if elements should be removed on click
         */
        var formatElements = function (select, wrapFunc, removable) {
            $(select).each(function () {
                var $this = $(this);
                var info = wrapFunc($this.html());
                if (removable) {
                    info.click(funcRemove);
                }
                // do not move element if it has attribute "vera-stay" or contains a form input element (e.g. button)
                if ($this.is('[vera-stay]') || $this.find('input').length > 0) {
                    $this.replaceWith(info);
                } else {
                    $this.remove();
                    $('h1').after(info);
                }
            });
        };
        formatElements('vera-info', createInfoHtml, true);
        formatElements('vera-warn', createWarnHtml);
        formatElements('vera-success', createSuccessHtml);
        formatElements('vera-confirm', createConfirmHtml);
    });

    showInfo = (function () {
        var activeInfoDialogs = {};
        return function (htmlStr) {
            if (activeInfoDialogs.hasOwnProperty(htmlStr)) { // already open?
                return;
            }
            activeInfoDialogs[htmlStr] = null;
            var info = createInfoHtml(htmlStr);
            info.click(function () {
                info.remove();
                delete activeInfoDialogs[htmlStr];
            });
            $(function () {
                $('h1').after(info);
            });
        }
    }());

    showWarning = function (htmlStr) {
        $(function () {
            $('h1').after(createWarnHtml(htmlStr));
        });
    };

    showSuccess = function (htmlStr) {
        $(function () {
            $('h1').after(createSuccessHtml(htmlStr));
        });
    };

    showConfirm = function (htmlStr) {
        $(function () {
            $('h1').after(createConfirmHtml(htmlStr));
        });
    };

    showConfirmYesNo = (function () {
        var activeConfirmDialogs = {};
        return function (title, htmlContent, yesAction, nayAction) {
            if (activeConfirmDialogs.hasOwnProperty(title)) { // already open?
                return;
            }
            var btnYes = $('<input type="button" class="button floatRight" value="Löschen">');
            var btnNo = $('<input type="button" class="button floatRight" value="Abbrechen">');
            var msg = createConfirmHtml('<strong>' + title + '</strong><br>' + htmlContent + '<br>');
            btnYes.click(function () {
                yesAction();
                msg.remove();
                delete activeConfirmDialogs[title];
            });
            btnNo.click(function () {
                if (nayAction !== undefined) {
                    nayAction();
                }
                msg.remove();
                delete activeConfirmDialogs[title];
            });
            msg.append(btnYes).append('&nbsp;').append(btnNo);
            activeConfirmDialogs[title] = null;
            $(function () {
                $('h1').after(msg);
            });
        };
    }());

}());
