/*
 * Veranstaltungsmanagement VerA.web, comprised of…
 * VerA.web, platform-independent webservice-based event management
 * tarent-commons, a set of common components and solutions
 * tarent-contact, platform-independent webservice-based contact management
 * tarent-database, jdbc database library
 * tarent-doctor, Document Generation Platform
 * tarent-octopus, Webservice Data Integrator and Application Server
 * … is newly MIT-licenced and Copyright
 *  © 2018 Атанас Александров (sirakov@gmail.com)
 *  © 2014, 2015, 2016, 2017 Атанас Александров (a.alexandrov@tarent.de)
 *  © 2013 Иванка Александрова (i.alexandrova@tarent.de)
 *  © 2005, 2006, 2007 asteban (s.mancke@tarent.de)
 *  © 2013 Patrick Apel (p.apel@tarent.de)
 *  © 2016 Eugen Auschew (e.auschew@tarent.de)
 *  © 2013 Andrei Boulgakov (a.boulgakov@tarent.de)
 *  © 2013 Valentin But (v.but@tarent.de)
 *  © 2016 Lukas Degener (l.degener@tarent.de)
 *  © 2017 Axel Dirla (a.dirla@tarent.de)
 *  © 2015 Julian Drawe (j.drawe@tarent.de)
 *  © 2009 Sven Frommeyer (s.frommeyer@tarent.de)
 *  © 2014, 2018 Dominik George (d.george@tarent.de)
 *  © 2013 Martin Gernhardt (m.gernhardt@tarent.de)
 *  © 2013 Sascha Girrulat (s.girrulat@tarent.de)
 *  © 2007, 2008 David Goemans (d.goemans@tarent.de)
 *  © 2018 Christian Gorski (c.gorski@tarent.de)
 *  © 2015 Viktor Hamm (v.hamm@tarent.de)
 *  © 2013 Katja Hapke (k.hapke@tarent.de)
 *  © 2006, 2007, 2010, 2013 Hendrik Helwich (h.helwich@tarent.de)
 *  © 2018 Thomas Hensel (t.hensel@tarent.de)
 *  © 2018, 2019 Benedict Hoeger (b.hoeger@tarent.de)
 *  © 2018, 2019 Titian Horvath (t.horvath@tarent.de)
 *  © 2005, 2006, 2007, 2008 Christoph Jerolimov (jerolimov@gmx.de)
 *  © 2018, 2019 Timo Kanera (t.kanera@tarent.de)
 *  © 2006 Philipp Kirchner (p.kirchner@tarent.de)
 *  © 2008, 2009, 2010 Carsten Klein (c.klein@tarent.de)
 *  © 2006 Michael Kleinhenz (m.kleinhenz@tarent.de)
 *  © 2006 Michael Klink (m.klink@tarent.de)
 *  © 2007 Fabian Köster (f.koester@tarent.de)
 *  © 2006, 2014 Martin Ley (m.ley@tarent.de)
 *  © 2007 Alex Maier (a.maier@tarent.de)
 *  © 2014, 2015 Max Marche (m.marche@tarent.de)
 *  © 2007 Jan Meyer (jan@evolvis.org)
 *  © 2007, 2013, 2014, 2015, 2016, 2017, 2018, 2019, 2020
 *     mirabilos (t.glaser@tarent.de)
 *  © 2016 Cristian Molina (c.molina@tarent.de)
 *  © 2006, 2007 Jens Neumaier (j.neumaier@tarent.de)
 *  © 2006 Nils Neumaier (n.neumaier@tarent.de)
 *  © 2018 Yorka Neumann (y.neumann@tarent.de)
 *  © 2017 Michael Nienhaus (m.nienhaus@tarent.de)
 *  © 2013 Claudia Nuessle (c.nuessle@tarent.de)
 *  © 2014, 2015 Jon Nuñez Alvarez (j.nunez-alvarez@tarent.de)
 *  © 2016 Jens Oberender (j.oberender@tarent.de)
 *  © 2016, 2017 Miluška Pech (m.pech@tarent.de)
 *  © 2007, 2008, 2009 Martin Pelzer (m.pelzer@tarent.de)
 *  © 2008, 2009 Christian Preilowski (c.thiel@tarent.de)
 *  © 2013 Marc Radel (m.radel@tarent.de)
 *  © 2013 Sebastian Reimers (s.reimers@tarent.de)
 *  © 2015 Charbel Saliba (c.saliba@tarent.de)
 *  © 2006, 2008, 2009, 2010 Thomas Schmitz (t.schmitz@tarent.de)
 *  © 2013 Volker Schmitz (v.schmitz@tarent.de)
 *  © 2014 Sven Schumann (s.schumann@tarent.de)
 *  © 2007 Robert Schuster (r.schuster@tarent.de)
 *  © 2014 Sevilay Temiz (s.temiz@tarent.de)
 *  © 2013 Kevin Viola Schmitz (k.schmitz@tarent.de)
 *  © 2008, 2015 Stefan Walenda (s.walenda@tarent.de)
 *  © 2015, 2016, 2017 Max Weierstall (m.weierstall@tarent.de)
 *  © 2013 Rebecca Weinz (r.weinz@tarent.de)
 *  © 2015, 2016 Stefan Weiz (s.weiz@tarent.de)
 *  © 2015, 2016 Tim Zimmer (t.zimmer@tarent.de)
 * and older code, Copyright © 2001–2008 ⮡ tarent GmbH and contributors.
 * Licensor is tarent solutions GmbH, http://www.tarent.de/
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

$('#guest-keywords').tagsInput({width:'auto'});
    $(function() {
      $('#guest-keywords').tagsInput();
    }
);

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
        var fm = document.getElementById('fixme');
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
        id = (id === null || typeof id === "undefined" ) ? 'modified' : id + '-modified';
        document.getElementById(id).value = 'true';
}

function checkModified(id, message) {
        if (isModified(id)) {
                return confirm(message);
        } else {
                return true;
        }
}

function isModified(id) {
        id = (id === null || typeof id === "undefined" ) ? 'modified' : id + '-modified';
        if (document.getElementById(id).value == 'true') {
                return true;
        } else {
                return false;
        }
}

function filterList(letter, page) {
        $('input[name$="-select"]').prop('checked', false);
        var form = document.getElementById('formlist');
        if (form && form.elements.filter) {
                form.elements.filter.value = letter;
                if (page === null || page === undefined) {
                    form.start.value = 0;
                } else {
                    form.start.value = page;
                }
                form.submit();
        } else if (form && !form.elements.filter) {
        if (page === null || page === undefined) {
            form.start.value = 0;
        } else {
            form.start.value = page;
        }
        form.submit();
    } else {
                alert('form or field filter NOT FOUND!');
        }
}

function navigateList(start) {
        $('input[name$="-select"]').prop('checked', false);
        var form = document.getElementById('formlist');
        if (form && form.elements.start) {
                form.elements.start.value = start;
                form.submit();
        } else {
                alert('form or field start NOT FOUND!');
        }
}

function navigateLimit(limit) {
        $('input[name$="-select"]').prop('checked', false);
        var form = document.getElementById('formlist');
        if (form && form.elements.start) {
                form.elements.start.value = 0;
        }
        if (form && form.elements.limit) {
                form.elements.limit.value = limit;
                form.submit();
        } else {
                alert('form or field limit NOT FOUND!');
        }
}

//Handler for SelectAll Toggling
$(function(){
    $('#toggleAllSelect').change(function(){
        if ($(this).is(':checked')) {
                $('input[name$="-select"]:not(:checked)').click();
        } else {
                $('input[name$="-select"]:checked').click();
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
};

/** disable all form elements of forms with attribute "vera-disabled".
 * This is because IE9 does not allow changing the text color of disabled input elements.
 * Also focus will be disabled (which is not possible with attribute "readonly").
 **/
$(function () {
  //  $(':input[vera-disabled]').veraDisable();
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

/**
 * Show a text below the page header line
 *
 * @param text
 */
var showInfo, showWarning, showSuccess, showConfirm, showConfirmYesNo;

(function () {
    var displayMsg = function displayMsg(msg) {
        var h1 = $('h1')[0];
        $(h1).after(msg);
        h1.scrollIntoView();
    };

    /**
     * Create a formatted info text HTML div element with the given message text.
     *
     * @param htmlStr
     * @returns {*|jQuery|HTMLElement}
     */
    var createInfoHtml = function (toolTip, htmlStr) {
        if (typeof htmlStr === "undefined") {
            return $('<div class="hinweis grayBorder marginBottom20 notBold">' + toolTip + '</div>');
        } else {
            return $('<div class="hinweis grayBorder marginBottom20 notBold"><strong>' + toolTip + '</strong><p>' +
            htmlStr + '</p></div>');
        }
    };

    /**
     * Create a formatted warning text HTML div element with the given message text.
     *
     * @param htmlStr
     * @returns {*|jQuery|HTMLElement}
     */
    var createWarnHtml = function (htmlStr) {
        return $('<div class="msg errormsg"><span>' +
        htmlStr + '</span></div>');
    };

    /**
     * Create a formatted success text HTML div element with the given message text.
     *
     * @param htmlStr
     * @returns {*|jQuery|HTMLElement}
     */
    var createSuccessHtml = function (htmlStr) {
        return $('<div class="msg successmsg"><span>' +
        htmlStr + '</span></div>');
    };

    /**
     * Create a formatted confirm text HTML div element with the given message text.
     *
     * @param htmlStr
     * @returns {*|jQuery|HTMLElement}
     */
    var createConfirmHtml = function (htmlStr) {
        return $('<div class="msg errormsg errormsgButton"><span>' +
        htmlStr + '</span></div>');
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
                    displayMsg(info);
                }
            });
        };
        formatElements('vera-info', createInfoHtml, false);
        formatElements('vera-warn', createWarnHtml);
        formatElements('vera-success', createSuccessHtml);
        formatElements('vera-confirm', createConfirmHtml);
    });

    showInfo = (function () {
        var activeInfoDialogs = {};
        return function (toolTip, htmlStr) {
            if (activeInfoDialogs.hasOwnProperty(htmlStr)) { // already open?
                return;
            }
            activeInfoDialogs[htmlStr] = null;
            var info = createInfoHtml(toolTip, htmlStr);
            info.click(function () {
                info.remove();
                delete activeInfoDialogs[htmlStr];
            });
            $(function () {
                displayMsg(info);
            });
        };
    }());

    showWarning = function (htmlStr) {
        $(function () {
            displayMsg(createWarnHtml(htmlStr));
        });
    };

    showSuccess = function (htmlStr) {
        $(function () {
            displayMsg(createSuccessHtml(htmlStr));
        });
    };

    showConfirm = function (htmlStr) {
        $(function () {
            displayMsg(createConfirmHtml(htmlStr));
        });
    };

    showConfirmYesNo = (function () {
        var activeConfirmDialogs = {};
        return function (title, htmlContent, yesAction, nayAction, textDelete, textCancel) {
            if (activeConfirmDialogs.hasOwnProperty(title)) { // already open?
                return;
            }
            var btnYes = $('<input type="button" class="button marginRight5" value="'+ textDelete +'">');
            var btnNo = $('<input type="button" class="button" value="'+ textCancel+'"/>');
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
            var divBtn = $('<div class="floatRight"/>');
            divBtn.append(btnYes).append(btnNo);
            msg.append(divBtn);
            activeConfirmDialogs[title] = null;
            $(function () {
                displayMsg(msg);
            });
        };
    }());

    showAllocateAssign = (function () {
         var activeConfirmDialogs = {};
         return function (title, yesAction, nayAction, textAssign, textCancel) {
             if (activeConfirmDialogs.hasOwnProperty(title)) { // already open?
                 return;
             }
             var btnYes = $('<input type="button" class="button marginRight5" value="'+ textAssign +'">');
             var btnNo = $('<input type="button" class="button" value="'+ textCancel +'">');
             var msg = createConfirmHtml('<strong>' + title + '</strong><br>' + '<br>');
             btnYes.click(function () {
                 yesAction();
                 msg.remove();
                 delete activeConfirmDialogs[title];
                closePopup();
             });
             btnNo.click(function () {
                 if (nayAction !== undefined) {
                     nayAction();
                 }
                 msg.remove();
                 delete activeConfirmDialogs[title];
             });
             var divBtn = $('<div class="floatRight"/>');
             divBtn.append(btnYes).append(btnNo);
             msg.append(divBtn);
             activeConfirmDialogs[title] = null;
             $(function () {
                 displayMsg(msg);
             });
        };
    }());

    showAllocateRemove = (function () {
         var activeConfirmDialogs = {};
        return function (title, yesAction, nayAction, textDelete, textCancel) {
            if (activeConfirmDialogs.hasOwnProperty(title)) { // already open?
                return;
            }
            var btnYes = $('<input type="button" class="button marginRight5" value="'+ textDelete +'">');
            var btnNo = $('<input type="button" class="button" value="'+ textCancel +'">');
            var msg = createConfirmHtml('<strong>' + title + '</strong><br>' + '<br>');
            btnYes.click(function () {
                  yesAction();
                 msg.remove();
                 delete activeConfirmDialogs[title];
                 closePopup();
            });
            btnNo.click(function () {
                 if (nayAction !== undefined) {
                    nayAction();
                }
                msg.remove();
                delete activeConfirmDialogs[title];
            });
            var divBtn = $('<div class="floatRight"/>');
            divBtn.append(btnYes).append(btnNo);
            msg.append(divBtn);
            activeConfirmDialogs[title] = null;
            $(function () {
                displayMsg(msg);
            });
       };
   }());
}());
