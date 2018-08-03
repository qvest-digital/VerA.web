/*
 * Veranstaltungsmanagement VerA.web (platform-independent
 * webservice-based event management) is Copyright
 *  © 2018 Атанас Александров (sirakov@gmail.com)
 *  © 2014, 2015, 2016, 2017 Атанас Александров (a.alexandrov@tarent.de)
 *  © 2013 Иванка Александрова (i.alexandrova@tarent.de)
 *  © 2013 Patrick Apel (p.apel@tarent.de)
 *  © 2016 Eugen Auschew (e.auschew@tarent.de)
 *  © 2013 Andrei Boulgakov (a.boulgakov@tarent.de)
 *  © 2013 Valentin But (v.but@tarent.de)
 *  © 2016 Lukas Degener (l.degener@tarent.de)
 *  © 2017 Axel Dirla (a.dirla@tarent.de)
 *  © 2015 Julian Drawe (j.drawe@tarent.de)
 *  © 2014 Dominik George (d.george@tarent.de)
 *  © 2013 Sascha Girrulat (s.girrulat@tarent.de)
 *  © 2008 David Goemans (d.goemans@tarent.de)
 *  © 2018 Christian Gorski (c.gorski@tarent.de)
 *  © 2015 Viktor Hamm (v.hamm@tarent.de)
 *  © 2013 Katja Hapke (k.hapke@tarent.de)
 *  © 2013 Hendrik Helwich (h.helwich@tarent.de)
 *  © 2018 Thomas Hensel (t.hensel@tarent.de)
 *  © 2018 Benedict Hoeger (b.hoeger@tarent.de)
 *  © 2018 Titian Horvath (t.horvath@tarent.de)
 *  © 2005, 2006, 2007, 2008 Christoph Jerolimov (jerolimov@gmx.de)
 *  © 2018 Timo Kanera (t.kanera@tarent.de)
 *  © 2008, 2009, 2010 Carsten Klein (c.klein@tarent.de)
 *  © 2014 Martin Ley (m.ley@tarent.de)
 *  © 2014, 2015 Max Marche (m.marche@tarent.de)
 *  © 2007 Jan Meyer (jan@evolvis.org)
 *  © 2013, 2014, 2015, 2016, 2017, 2018 mirabilos (t.glaser@tarent.de)
 *  © 2016 Cristian Molina (c.molina@tarent.de)
 *  © 2018 Yorka Neumann (y.neumann@tarent.de)
 *  © 2017 Michael Nienhaus (m.nienhaus@tarent.de)
 *  © 2013 Claudia Nuessle (c.nuessle@tarent.de)
 *  © 2014, 2015 Jon Nuñez Alvarez (j.nunez-alvarez@tarent.de)
 *  © 2016 Jens Oberender (j.oberender@tarent.de)
 *  © 2016, 2017 Miluška Pech (m.pech@tarent.de)
 *  © 2009 Martin Pelzer (m.pelzer@tarent.de)
 *  © 2013 Marc Radel (m.radel@tarent.de)
 *  © 2013 Sebastian Reimers (s.reimers@tarent.de)
 *  © 2015 Charbel Saliba (c.saliba@tarent.de)
 *  © 2008, 2009, 2010 Thomas Schmitz (t.schmitz@tarent.de)
 *  © 2013 Volker Schmitz (v.schmitz@tarent.de)
 *  © 2014 Sven Schumann (s.schumann@tarent.de)
 *  © 2014 Sevilay Temiz (s.temiz@tarent.de)
 *  © 2013 Kevin Viola Schmitz (k.schmitz@tarent.de)
 *  © 2015 Stefan Walenda (s.walenda@tarent.de)
 *  © 2015, 2016, 2017 Max Weierstall (m.weierstall@tarent.de)
 *  © 2013 Rebecca Weinz (r.weinz@tarent.de)
 *  © 2015, 2016 Stefan Weiz (s.weiz@tarent.de)
 *  © 2015, 2016 Tim Zimmer (t.zimmer@tarent.de)
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
/* global $ */

$(function () { // on DOM ready
    'use strict';

    /**
     * @type {string}
     * @const
     */
    var ATTR_TAB = 'vera-tab';

    /**
     * @type {string}
     * @const
     */
    var ATTR_SELECT = 'vera-select';

    /**
     * @type {string}
     * @const
     */
    var ATTR_TAB_GROUP = 'vera-tab-group';

    /**
     * @type {string}
     * @const
     */
    var ATTR_CONTENT = 'vera-content';

    /**
     * @type {string}
     * @const
     */
    var ATTR_TAB_DEFAULT = 'vera-tab-default';

    /**
     * Only the content which is referenced by all active tabs and selected drop down fields is shown.
     */
    var updateContent = function () {
        // update visibility of content
        var contentIds = []; // active content ids
        $('[vera-tab].active').each(function () { // iterate active tabs
            contentIds.push($(this).attr(ATTR_TAB)); // add id of current active tab
        });
        $('select[vera-select] option:selected').each(function () { // iterate selected options of all selects
            contentIds.push($(this).val());  // add id of current active selection
        });
        var activeTabContent = $();
        var all = $('[' + ATTR_CONTENT + ']');
        all.each(function () { // iterate all managed content
            var showContent = true; // true if all ids in the content field are active
            var ccids = $(this).attr(ATTR_CONTENT).split(' ');
            for (var i = 0; i < ccids.length; i += 1) { // do not use $.each here => IE9 jQuery bug
                if ($.inArray(ccids[i], contentIds) === -1) {  // content field contains an id which is not active
                    showContent = false;
                }
            };
            if (showContent) {
                activeTabContent = $(this).add(activeTabContent);
            }
        });

        // hide inactive content
        all.not(activeTabContent).hide();

        // show active content
        activeTabContent.show();
    };

    /**
     * Activate the given tab elements and deactivate the other tabs in the same tab group.
     * Also only the content which is referenced by all active tabs is shown.
     *
     * @param tabs
     */
    var setTabs = function (tabs) {
        // activate tab
        tabs.each(function (i, tab) { // iterate Tabs which should be activated
            tab = $(tab); // create wrapping jQuery object
            // get all tabs in the tab group of the current tab
            var tabGroup = tab.parent('[' + ATTR_TAB_GROUP + ']').children('[' + ATTR_TAB + ']');
            // activate only current tab in tab group
            tabGroup.not(tab).removeClass("active").addClass("inactive");
            tab.removeClass("inactive").addClass("active");
        });
        updateContent();
    };

    // add click handler to all tabs
    $('[' + ATTR_TAB + ']').click(function () {
        setTabs($(this));
    });

    // add change handler to all content selects
    $('[' + ATTR_SELECT + ']').change(function () {
        updateContent();
    });

    // activate default tabs
    setTabs($('[' + ATTR_TAB_DEFAULT + ']'));
});
