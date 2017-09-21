package de.tarent.aa.veraweb.beans;

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
import de.tarent.dblayer.sql.clause.WhereList;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class GuestSearchTest {

    private GuestSearch search;

    @Test
    public void test() {
        // GIVEN
        search = new GuestSearch();
        search.event=42;
        search.keywords="gröfatz, knørßt   , baz , ";
        WhereList where = new WhereList();

        // WHEN
        search.addGuestListFilter(where);

        // THEN
        assertEquals("(tguest.fk_event=42 AND tguest.keywords LIKE '%gröfatz%' AND tguest.keywords LIKE '%knørßt%' AND tguest.keywords LIKE '%baz%')",where.clauseToString());
    }

    
    
}
