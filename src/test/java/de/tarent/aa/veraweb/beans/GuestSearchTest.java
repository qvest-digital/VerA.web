package de.tarent.aa.veraweb.beans;

import static org.junit.Assert.*;

import org.junit.Test;

import de.tarent.dblayer.sql.Escaper;
import de.tarent.dblayer.sql.clause.WhereList;

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
