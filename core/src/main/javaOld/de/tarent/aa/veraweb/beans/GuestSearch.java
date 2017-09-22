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
import de.tarent.aa.veraweb.utils.VerawebUtils;
import de.tarent.dblayer.sql.Escaper;
import de.tarent.dblayer.sql.clause.Expr;
import de.tarent.dblayer.sql.clause.RawClause;
import de.tarent.dblayer.sql.clause.WhereList;

/**
 * Bean zum Filtern einer Gästeliste und navigieren innerhalb der Detailansicht.
 *
 * Entspricht keiner Datenbank-Tabelle und wird ausschließlich in der Session
 * gehalten oder aus dem Request geladen.
 */
public class GuestSearch extends AbstractBean {
    public Integer event;
    public Integer offset;
    public Integer count;
    /**
     * Gibt an ob nach Platz / Reserve gefiltert werden soll.
     */
    public Integer reserve;
    /**
     * Gibt an ob nach Offene / Zusagen / Absagen gefiltert werden soll.
     */
    public Integer invitationstatus;
    /**
     * Gibt an in welcher Sortierreihenfolge die Gästeliste angezeigt werden
     * soll.
     */
    public String listorder; // orderno, lastname, firstname, email
    public String lastlistorder;
    public String sortDirection; // ASC, DESC
    /**
     * Gibt an nach welcher kategorie gefiltert werden soll
     */
    public String category;
    public Boolean sortList;
    public String keywords;

    public void addGuestListFilter(WhereList where) {
        where.addAnd(Expr.equal("tguest.fk_event", this.event));

        if (this.category != null && !this.category.trim().equals("")) {
            where.addAnd(new RawClause("fk_category IN (SELECT pk FROM veraweb.tcategorie WHERE catname = '" + Escaper.escape(this.category) + "')"));
        }

        if (this.reserve != null) {
            switch (this.reserve.intValue()) {
            case 1:
                where.addAnd(Expr.nullOrInt0("reserve"));
                break;
            case 2:
                where.addAnd(Expr.equal("reserve", new Integer(1)));
                break;
            }
        }

        if (this.invitationstatus != null) {
            switch (this.invitationstatus.intValue()) {
            case 1:
                // nur Offen
                where.addAnd(new RawClause("(" +
                        // Mit Partner
                        "(invitationtype = 1 AND (invitationstatus IS NULL OR invitationstatus=0 OR "
                        + "invitationstatus_p IS NULL OR invitationstatus_p=0)) OR" +
                        // Ohne Partner
                        "(invitationtype = 2 AND (invitationstatus IS NULL OR invitationstatus=0)) OR" +
                        // Nur Partner
                        "(invitationtype = 3 AND (invitationstatus_p IS NULL OR invitationstatus_p=0))" + ")"));
                break;
            case 2:
                // nur Zusagen
                where.addAnd(new RawClause("(" +
                        // Mit Partner
                        "(invitationtype = 1 AND (invitationstatus=1 OR invitationstatus_p=1)) OR" +
                        // Ohne Partner
                        "(invitationtype = 2 AND invitationstatus=1) OR" +
                        // Nur Partner
                        "(invitationtype = 3 AND invitationstatus_p=1)" + ")"));
                break;
            case 3:
                // nur Absagen
                where.addAnd(new RawClause("(" +
                        // Mit Partner
                        "(invitationtype = 1 AND (invitationstatus=2 OR invitationstatus_p=2)) OR" +
                        // Ohne Partner
                        "(invitationtype = 2 AND invitationstatus=2) OR" +
                        // Nur Partner
                        "(invitationtype = 3 AND invitationstatus_p=2)" + ")"));
                break;
            case 4:
                // nur Teilnahmen
                where.addAnd(new RawClause("(" +
                        // Mit Partner
                        "(invitationtype = 1 AND (invitationstatus=3 OR invitationstatus_p=3)) OR" +
                        // Ohne Partner
                        "(invitationtype = 2 AND invitationstatus=3) OR" +
                        // Nur Partner
                        "(invitationtype = 3 AND invitationstatus_p=3)" + ")"));
                break;
            }
        }
        if(this.keywords!=null && !this.keywords.trim().isEmpty()){
            final String[] words = VerawebUtils.splitKeywords(keywords);
            for (String word : words) {
                where.addAnd(Expr.like("tguest.keywords", "%"+word+"%"));
            }
        }
        
    }
}
