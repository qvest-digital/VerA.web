/*
 * Veranstaltungsmanagement VerA.web (platform-independent
 * webservice-based event management) is Copyright
 *  © 2014, 2015, 2016, 2017 Атанас Александров <a.alexandrov@tarent.de>
 *  © 2013 Иванка Александрова <i.alexandrova@tarent.de>
 *  © 2013 Patrick Apel <p.apel@tarent.de>
 *  © 2016 Eugen Auschew <e.auschew@tarent.de>
 *  © 2013 Andrei Boulgakov <a.boulgakov@tarent.de>
 *  © 2013 Valentin But <v.but@tarent.de>
 *  © 2016 Lukas Degener <l.degener@tarent.de>
 *  © 2017 Axel Dirla <a.dirla@tarent.de>
 *  © 2015 Julian Drawe <j.drawe@tarent.de>
 *  © 2014 Dominik George <d.george@tarent.de>
 *  © 2013 Sascha Girrulat <s.girrulat@tarent.de>
 *  © 2008 David Goemans <d.goemans@tarent.de>
 *  © 2015 Viktor Hamm <v.hamm@tarent.de>
 *  © 2013 Katja Hapke <k.hapke@tarent.de>
 *  © 2013 Hendrik Helwich <h.helwich@tarent.de>
 *  © 2007 jan <jan@evolvis.org>
 *  © 2005, 2006, 2007, 2008 Christoph Jerolimov <jerolimov@gmx.de>
 *  © 2008, 2009, 2010 Carsten Klein <c.klein@tarent.de>
 *  © 2014 Martin Ley <m.ley@tarent.de>
 *  © 2014, 2015 Max Marche <m.marche@tarent.de>
 *  © 2013, 2014, 2015, 2016, 2017 mirabilos <t.glaser@tarent.de>
 *  © 2016 Cristian Molina <c.molina@tarent.de>
 *  © 2017 Michael Nienhaus <m.nienhaus@tarent.de>
 *  © 2013 Claudia Nuessle <c.nuessle@tarent.de>
 *  © 2014, 2015 Jon Nunez Alvarez <j.nunez-alvarez@tarent.de>
 *  © 2016 Jens Oberender <j.oberender@tarent.de>
 *  © 2016, 2017 Miluška Pech <m.pech@tarent.de>
 *  © 2009 Martin Pelzer <m.pelzer@tarent.de>
 *  © 2013 Marc Radel <m.radel@tarent.de>
 *  © 2013 Sebastian Reimers <s.reimers@tarent.de>
 *  © 2015 Charbel Saliba <c.saliba@tarent.de>
 *  © 2008, 2009, 2010 Thomas Schmitz <t.schmitz@tarent.de>
 *  © 2013 Volker Schmitz <v.schmitz@tarent.de>
 *  © 2014 Sven Schumann <s.schumann@tarent.de>
 *  © 2014 Sevilay Temiz <s.temiz@tarent.de>
 *  © 2013 Kevin Viola Schmitz <k.schmitz@tarent.de>
 *  © 2015 Stefan Walenda <s.walenda@tarent.de>
 *  © 2015, 2016, 2017 Max Weierstall <m.weierstall@tarent.de>
 *  © 2013 Rebecca Weinz <r.weinz@tarent.de>
 *  © 2015, 2016 Stefan Weiz <s.weiz@tarent.de>
 *  © 2015, 2016 Tim Zimmer <t.zimmer@tarent.de>
 * and older code, Copyright © 2004–2008 ⮡ tarent GmbH and contributors.
 * Licensor is tarent solutions GmbH, http://www.tarent.de/
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see: http://www.gnu.org/licenses/
 */
(function(){
    var loadSalutationsUnusedList = function() {
        $.ajax({
            url: $("#salutations-unused-list").data("rest-path"),
            type: 'GET',
            success: function(data){
                $(".errormsg").remove();
                $(".successmsg").remove();
                if (data !== null ) {
                    if (data.length > 0) { /* show/hide + Button */
                        $("#salutationsAlternativeListContent").append("<div class='clearSalutation' style='margin-top: 10px'><img id='addSalutation' style='vertical-align: middle; margin-right: 10px;' src='../images/add.png'/><span>" + $("#salutations-placeholder-add").data("translation") + "</span></div>");
                        $("img#addSalutation").click(function(){
                            showDialog(data);
                        });
                    }
                } else {
                    //FIXME: REST API ERROR
                }
            },
            error: function(data) {
                $(".errormsg").remove();
                $(".successmsg").remove();
                var errorMsg = $("#pdftemplate-salutation-load-errormsg").data("errormsg");
                showWarning(errorMsg);
            }
        });
    };

    var showDialog = function(data) {
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

    var buildSelectTag = function(data) {
        var selectTag = "<select name='salutationId'>";
        data.forEach(function(entry){
            selectTag += "<option value='" + entry.pk + "'>" + entry.salutation + "</option>";
        });
        selectTag += "</select>";

        return selectTag;
    };

    var saveSalutationsAlternative = function(value) {
        $.ajax({
            url: $("#salutations-save-link").data("rest-path"),
            data: {
                salutationId: value.salutationId,
                content: value.salutationText.trim()
            },
            type: 'POST',
            success: function(response){
                $(".errormsg").remove();
                $(".successmsg").remove();
                reloadSalutations();
            },
            error: function(response) {
                $(".errormsg").remove();
                $(".successmsg").remove();
                var errorMsg = "";
                if (response.status == 400){ /* Bad Request */
                    errorMsg = $("#pdftemplate-salutation-empty-errormsg").data("errormsg");
                } else if (response.status == 420){ /* Policy Not Fulfilled */
                    errorMsg = $("#pdftemplate-salutation-length-errormsg").data("errormsg");
                } else {
                    errorMsg = $("#pdftemplate-salutation-save-errormsg").data("errormsg");
                }
                showWarning(errorMsg);
            }
        });
    };

    var loadSalutationsAlternativeList = function() {
        $.ajax({
            url: $("#salutations-alternative-list").data("rest-path"),
            type: 'GET',
            success: function(data){
                $(".errormsg").remove();
                $(".successmsg").remove();
                if (data !== null) {
                    for (i = 0; i < data.length; i++) {
                        $("#salutationsAlternativeListContainer").append("<div class='clearSalutation' style='margin: 10px 10px 0 0'><label style='display:inline; margin-right: 10px; width: 40%;'>" + data[i][4] + "</label>" + $("#salutations-placeholder-to").data("translation") + "<label style='display:inline; margin-left: 10px; width: 40%;'>" + data[i][3] + "</label><img data-salutation-id=" + data[i][0] + " class='removeSalutation' style='vertical-align: middle; margin-left: 10px;' src='../images/remove.png'/></div>");
                    }
                    $("img.removeSalutation").click(function(){
                        deleteSalutationsAlternative($(this).data("salutation-id"));
                    });
                } else {
                    //FIXME: REST API ERROR
                }
            },
            error: function(data) {
                $(".errormsg").remove();
                $(".successmsg").remove();
                var errorMsg = $("#pdftemplate-salutation-load-errormsg").data("errormsg");
                showWarning(errorMsg);
            }
        });
    };

    var deleteSalutationsAlternative = function(salutationId) {
        $.ajax({
            url: $("#salutations-delete-link").data("rest-path") + salutationId,
            type: 'DELETE',
            success: function(data){
                $(".errormsg").remove();
                $(".successmsg").remove();
                reloadSalutations();
            },
            error: function(data) {
                $(".errormsg").remove();
                $(".successmsg").remove();
                var errorMsg = $("#pdftemplate-salutation-delete-errormsg").data("errormsg");
                showWarning(errorMsg);
            }
        });
    };

    var reloadSalutations = function(){
        $(".clearSalutation").remove();
        loadSalutations();
    };

    var loadSalutations = function(){
        loadSalutationsUnusedList();
        loadSalutationsAlternativeList();
    };

    $(document).ready(function() {
        loadSalutations();
    });
})();
