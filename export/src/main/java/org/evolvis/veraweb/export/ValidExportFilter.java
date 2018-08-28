package org.evolvis.veraweb.export;

/*-
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

import org.springframework.util.StringUtils;

public enum ValidExportFilter {

    CATEGORY_ID_FILTER("filterCategoryId", "[0-9]{1,20}", "g.fk_category = ?"),
    SEARCHWORD_FILTER("filterWord", "[0-9a-zA-Z]{1,50}", "(p.firstname_a_e1 = ? OR p.lastname_a_e1 = ?)"),//TODO expand and modify to expected search word filtering
    INVITATIONSTATUS_FILTER("filterInvStatus", "[0-9]", null),
    RESERVE_FILTER("filterReserve", "[0-1]", "g.reserve = ?");

    public final String key;
    public final String pattern;
    public final String dbPath;

    ValidExportFilter(String key, String pattern, String dbPath) {
        this.key = key;
        this.pattern = pattern;
        this.dbPath = dbPath;
    }

    public static ValidExportFilter valueOfKey(String key) {
        if (key != null) {
            for (ValidExportFilter validExportFilter : ValidExportFilter.values()) {
                if (validExportFilter.key.equals(key)) {
                    return validExportFilter;
                }
            }
        }
        return null;
    }

    public static boolean isValidFilterSetting(String key, String value) {
        if (!(StringUtils.isEmpty(key) || StringUtils.isEmpty(value))) {
            ValidExportFilter validExportFilter = valueOfKey(key);
            if (validExportFilter != null) {
                return value.matches(validExportFilter.pattern);
            }
        }
        return false;
    }

    public static String buildDBPathPartial(String key, String value) {
        if (!(StringUtils.isEmpty(key) || StringUtils.isEmpty(value))) {
            ValidExportFilter validExportFilter = valueOfKey(key);
            if (validExportFilter.dbPath != null) {
                return validExportFilter.dbPath.replaceAll("\\?", value);
            }
            if (ValidExportFilter.INVITATIONSTATUS_FILTER.equals(validExportFilter))  {
                return getInvitationStatusFilter(value);
            }
        }
        return null;
    }

    /**
     * Creates a filter based on the status selected in the UI.
     *
     * See also {@link de.tarent.aa.veraweb.beans.GuestSearch#addGuestListFilter(de.tarent.dblayer.sql.clause.WhereList)}
     *
     * @param value the value selected in the UI status dropdown
     * @return the query filter
     */
    private static String getInvitationStatusFilter(String value) {
        if (StringUtils.isEmpty(value)) {
            return null;
        }
        switch (Integer.parseInt(value)) {
            case 1:
                // nur Offen
                return  // Mit Partner
                        "((invitationtype = 1 AND (invitationstatus IS NULL OR invitationstatus=0 OR " +
                                "invitationstatus_p IS NULL OR invitationstatus_p=0)) OR " +
                                // Ohne Partner
                                "(invitationtype = 2 AND (invitationstatus IS NULL OR invitationstatus=0)) OR " +
                                // Nur Partner
                                "(invitationtype = 3 AND (invitationstatus_p IS NULL OR invitationstatus_p=0)))";
            case 2:
                // nur Zusagen
                return  // Mit Partner
                        "((invitationtype = 1 AND (invitationstatus = 1 OR invitationstatus_p = 1)) OR " +
                                // Ohne Partner
                                "(invitationtype = 2 AND invitationstatus = 1) OR " +
                                // Nur Partner
                                "(invitationtype = 3 AND invitationstatus_p = 1))";
            case 3:
                // nur Absagen
                return  // Mit Partner
                        "((invitationtype = 1 AND (invitationstatus = 2 OR invitationstatus_p = 2)) OR " +
                                // Ohne Partner
                                "(invitationtype = 2 AND invitationstatus = 2) OR " +
                                // Nur Partner
                                "(invitationtype = 3 AND invitationstatus_p = 2))";
            case 4:
                // nur Teilnahmen
                return  // Mit Partner
                        "((invitationtype = 1 AND (invitationstatus = 3 OR invitationstatus_p = 3)) OR " +
                                // Ohne Partner
                                "(invitationtype = 2 AND invitationstatus = 3) OR " +
                                // Nur Partner
                                "(invitationtype = 3 AND invitationstatus_p = 3))";
        }
        return null;
    }
}
