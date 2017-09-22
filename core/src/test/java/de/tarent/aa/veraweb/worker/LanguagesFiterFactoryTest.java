package de.tarent.aa.veraweb.worker;

/*-
 * veraweb, platform independent webservice-based event management
 * (Veranstaltungsmanagment VerA.web), is
 * Copyright © 2004–2008 tarent GmbH
 * Copyright © 2013–2016 tarent solutions GmbH
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
import de.tarent.aa.veraweb.beans.PersonSearch;
import de.tarent.dblayer.sql.clause.Clause;
import de.tarent.dblayer.sql.clause.WhereList;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Atanas Alexandrov, tarent solutions GmbH
 */
public class LanguagesFiterFactoryTest {

    private LanguagesFilterFactory factory;

    @Before
    public void setUp() throws Exception {
        factory = new LanguagesFilterFactory();
    }

    @Test
    public void testGetLanguagesFilterWithOneLanguage() {
        PersonSearch personSearch = new PersonSearch();
        personSearch.languages="Deutsch";
        WhereList klaus = (WhereList) factory.createLanguagesFilter(personSearch);

        assertEquals("(veraweb.upper_fix(languages_a_e1) LIKE veraweb.upper_fix('%Deutsch%') OR veraweb.upper_fix(languages_b_e1) LIKE veraweb.upper_fix('%Deutsch%'))", klaus.clauseToString());
    }

    @Test
    public void testGetLanguagesFilterWithTwoLanguages() {
        PersonSearch personSearch = new PersonSearch();
        personSearch.languages="Deutsch, English";
        Clause klaus = factory.createLanguagesFilter(personSearch);

        assertEquals("(veraweb.upper_fix(languages_a_e1) LIKE veraweb.upper_fix('%Deutsch%') OR veraweb.upper_fix(languages_b_e1) LIKE veraweb.upper_fix('%Deutsch%')) OR (veraweb.upper_fix(languages_a_e1) LIKE veraweb.upper_fix('%English%') OR veraweb.upper_fix(languages_b_e1) LIKE veraweb.upper_fix('%English%'))", klaus.clauseToString());
    }
}