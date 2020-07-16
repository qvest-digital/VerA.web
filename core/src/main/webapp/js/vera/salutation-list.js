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

(function () {
    var buildSelectTag = function (data) {
	var selectTag = "<select name='salutationId'>";
	data.forEach(function (entry) {
	    selectTag += "<option value='" + entry.pk + "'>" + entry.salutation + "</option>";
	});
	selectTag += "</select>";

	return selectTag;
    };

    var showDialog = function (data) {
	var selectOptions = buildSelectTag(data);

	vex.dialog.open({
	    message: $("#salutations-dialog-text").data("translation"),
	    input: [
		selectOptions,
		"<span> " + $("#salutations-placeholder-to").data("translation") + "</span>",
		"<input name='salutationText' type='text' placeholder=" + $("#salutations-dialog-field").data("translation") + " required />"
	    ].join(""),
	    buttons: [
		$.extend({}, vex.dialog.buttons.YES, { text: $("#salutations-dialog-yes").data("translation") }),
		$.extend({}, vex.dialog.buttons.NO, { text: $("#salutations-dialog-no").data("translation") })
	    ],
	    callback: function (value) {
		if (value) {
		    saveSalutationsAlternative(value);
		} else {
		    // FIXME: ERROR
		    console.log("ERROR or CLOSED dialog popup");
		}
	    }
	});
    };

    var loadSalutationsUnusedList = function () {
	$.ajax({
	    url: $("#salutations-unused-list").data("rest-path"),
	    type: 'GET',
	    success: function (data) {
		$(".errormsg").remove();
		$(".successmsg").remove();
		if (data !== null ) {
		    if (data.length > 0) { /* show/hide + Button */
			$("#salutationsAlternativeListContent").append("<div class='clearSalutation' style='margin-top: 10px'><img id='addSalutation' style='vertical-align: middle; margin-right: 10px;' src='../images/add.png'/><span>" + $("#salutations-placeholder-add").data("translation") + "</span></div>");
			$("img#addSalutation").click(function () {
			    showDialog(data);
			});
		    }
		} else {
		    //FIXME: REST API ERROR
		}
	    },
	    error: function (data) {
		$(".errormsg").remove();
		$(".successmsg").remove();
		var errorMsg = $("#pdftemplate-salutation-load-errormsg").data("errormsg");
		showWarning(errorMsg);
	    }
	});
    };

    var loadSalutations = function () {
	loadSalutationsUnusedList();
	loadSalutationsAlternativeList();
    };

    var reloadSalutations = function () {
	$(".clearSalutation").remove();
	loadSalutations();
    };

    var saveSalutationsAlternative = function (value) {
	$.ajax({
	    url: $("#salutations-save-link").data("rest-path"),
	    data: {
		salutationId: value.salutationId,
		content: value.salutationText.trim()
	    },
	    type: 'POST',
	    success: function (response) {
		$(".errormsg").remove();
		$(".successmsg").remove();
		reloadSalutations();
	    },
	    error: function (response) {
		$(".errormsg").remove();
		$(".successmsg").remove();
		var errorMsg = "";
		if (response.status == 400) { /* Bad Request */
		    errorMsg = $("#pdftemplate-salutation-empty-errormsg").data("errormsg");
		} else if (response.status == 428) { /* Precondition Required */
		    errorMsg = $("#pdftemplate-salutation-length-errormsg").data("errormsg");
		} else {
		    errorMsg = $("#pdftemplate-salutation-save-errormsg").data("errormsg");
		}
		showWarning(errorMsg);
	    }
	});
    };

    var deleteSalutationsAlternative = function (salutationId) {
	$.ajax({
	    url: $("#salutations-delete-link").data("rest-path") + salutationId,
	    type: 'DELETE',
	    success: function (data) {
		$(".errormsg").remove();
		$(".successmsg").remove();
		reloadSalutations();
	    },
	    error: function (data) {
		$(".errormsg").remove();
		$(".successmsg").remove();
		var errorMsg = $("#pdftemplate-salutation-delete-errormsg").data("errormsg");
		showWarning(errorMsg);
	    }
	});
    };

    var loadSalutationsAlternativeList = function () {
	$.ajax({
	    url: $("#salutations-alternative-list").data("rest-path"),
	    type: 'GET',
	    success: function (data) {
		$(".errormsg").remove();
		$(".successmsg").remove();
		if (data !== null) {
		    for (i = 0; i < data.length; i++) {
			$("#salutationsAlternativeListContainer").append("<div class='clearSalutation' style='margin: 10px 10px 0 0'><label style='display:inline; margin-right: 10px; width: 40%;'>" + data[i][4] + "</label>" + $("#salutations-placeholder-to").data("translation") + "<label style='display:inline; margin-left: 10px; width: 40%;'>" + data[i][3] + "</label><img data-salutation-id=" + data[i][0] + " class='removeSalutation' style='vertical-align: middle; margin-left: 10px;' src='../images/remove.png'/></div>");
		    }
		    $("img.removeSalutation").click(function () {
			deleteSalutationsAlternative($(this).data("salutation-id"));
		    });
		} else {
		    //FIXME: REST API ERROR
		}
	    },
	    error: function (data) {
		$(".errormsg").remove();
		$(".successmsg").remove();
		var errorMsg = $("#pdftemplate-salutation-load-errormsg").data("errormsg");
		showWarning(errorMsg);
	    }
	});
    };

    $(document).ready(function () {
	loadSalutations();
    });
})();
