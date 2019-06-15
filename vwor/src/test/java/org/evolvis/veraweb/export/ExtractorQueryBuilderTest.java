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
 *  © 2018, 2019 Benedict Hoeger (b.hoeger@tarent.de)
 *  © 2018, 2019 Titian Horvath (t.horvath@tarent.de)
 *  © 2005, 2006, 2007, 2008 Christoph Jerolimov (jerolimov@gmx.de)
 *  © 2018, 2019 Timo Kanera (t.kanera@tarent.de)
 *  © 2008, 2009, 2010 Carsten Klein (c.klein@tarent.de)
 *  © 2014 Martin Ley (m.ley@tarent.de)
 *  © 2014, 2015 Max Marche (m.marche@tarent.de)
 *  © 2007 Jan Meyer (jan@evolvis.org)
 *  © 2013, 2014, 2015, 2016, 2017, 2018, 2019
 *     mirabilos (t.glaser@tarent.de)
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

import de.tarent.extract.ExtractorQuery;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class ExtractorQueryBuilderTest {
    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Mock
    private ExtractorQuery extractorQuery;

    @Test
    public void canSubstituteSingleValues() {
        when(extractorQuery.getSql()).thenReturn("fnarz ${foo} fnarz ${bar}");
        ExtractorQueryBuilder queryTemplate = new ExtractorQueryBuilder(extractorQuery);
        ExtractorQuery query = queryTemplate.replace("foo", 42).replace("bar", "bang").build();
        assertEquals("fnarz 42 fnarz bang", query.getSql());
    }

    @Test
    public void canApplyASetOfSubstitutions() {
        when(extractorQuery.getSql()).thenReturn("fnarz ${foo} fnarz ${bar}");
        ExtractorQueryBuilder queryTemplate = new ExtractorQueryBuilder(extractorQuery);
        HashMap<String, String> substitutions = new HashMap<String, String>();
        substitutions.put("foo", "42");
        substitutions.put("bar", "bang");

        ExtractorQuery query = queryTemplate.replace(substitutions).build();
        assertEquals("fnarz 42 fnarz bang", query.getSql());
    }

    @Test
    public void applyingFiltersDontModifyBaseQuerySql() {
        //given:
        String basicQueryString = "SELECT * FROM tguest WHERE x=y";
        when(extractorQuery.getSql()).thenReturn(basicQueryString);
        ExtractorQueryBuilder queryTemplate = new ExtractorQueryBuilder(extractorQuery);
        HashMap<String, String> filterSettings = new HashMap<>();
        filterSettings.put(ValidExportFilter.CATEGORY_ID_FILTER.key, "0");
        filterSettings.put(ValidExportFilter.INVITATIONSTATUS_FILTER.key, "2");

        //when:
        ExtractorQuery query = queryTemplate.setFilters(filterSettings).build();

        //then:
        assertTrue(query.getSql().startsWith(basicQueryString));
    }

    @Test
    public void applyingFiltersExtendsBaseQuerySql() {
        //given:
        String basicQueryString = "SELECT * FROM tguest WHERE x=y";
        when(extractorQuery.getSql()).thenReturn(basicQueryString);
        ExtractorQueryBuilder queryTemplate = new ExtractorQueryBuilder(extractorQuery);
        HashMap<String, String> filterSettings = new HashMap<>();
        filterSettings.put(ValidExportFilter.CATEGORY_ID_FILTER.key, "0");
        filterSettings.put(ValidExportFilter.INVITATIONSTATUS_FILTER.key, "2");
        filterSettings.put("filterStupid", "0");
        filterSettings.put(ValidExportFilter.SEARCHWORD_FILTER.key, "2; DELETE FROM tconfig");

        //when:
        ExtractorQuery query = queryTemplate.setFilters(filterSettings).build();

        //then:
        String expectedStatusFilter = "((invitationtype = 1 AND (invitationstatus = 1 OR invitationstatus_p = 1)) OR (invitationtype = 2 AND invitationstatus = 1) OR (invitationtype = 3 AND invitationstatus_p = 1))";
        assertEquals(query.getSql(), basicQueryString + " AND " + expectedStatusFilter + " AND g.fk_category = 0");
    }
}