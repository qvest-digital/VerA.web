package de.tarent.aa.veraweb.utils;

/*-
 * Veranstaltungsmanagement VerA.web (platform-independent
 * webservice-based event management) is Copyright
 *  © 2014, 2015, 2016, 2017 Атанас Александров (a.alexandrov@tarent.de)
 *  © 2018 Атанас Александров (sirakov@gmail.com)
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

import de.tarent.aa.veraweb.beans.Categorie;
import de.tarent.aa.veraweb.beans.Event;
import de.tarent.aa.veraweb.beans.facade.EventConstants;
import de.tarent.dblayer.sql.SQL;
import de.tarent.dblayer.sql.clause.Expr;
import de.tarent.dblayer.sql.clause.Order;
import de.tarent.dblayer.sql.clause.RawClause;
import de.tarent.dblayer.sql.clause.WhereList;
import de.tarent.dblayer.sql.statement.Select;
import de.tarent.dblayer.sql.statement.Update;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.beans.ExecutionContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Diese Klasse sammelt Hilfsklassen zum Ermitteln laufender Nummern für Gäste.
 *
 * @author christoph
 */
public class GuestSerialNumber {
    /**
     * Diese Klasse ist Basisklasse für Hilfsklassen zum Ermitteln laufender
     * Nummern für Gäste.
     */
    static public abstract class CalcSerialNumber {
        protected int orderNo = 0;
        protected ExecutionContext executionContext;
        protected Event event;

        /**
         * Dieser Konstruktor übernimmt Datenbank und Veranstaltung zur
         * Benutzung bei der späteren Berechnung laufender Gästenummern.
         *
         * @param event   FIXME
         * @param context FIXME
         */
        public CalcSerialNumber(ExecutionContext context, Event event) {
            this.executionContext = context;
            this.event = event;
        }

        /**
         * Diese abstrakte Methode berechnet die tatsächlichen laufenden
         * Nummern der Gäste der im Konstruktor übergebenen Veranstaltung
         * in der ebenda übergebenen Datenbank.
         *
         * @throws BeanException FIXME
         * @throws IOException   FIXME
         */
        public abstract void calcSerialNumber() throws BeanException, IOException;

        protected void clearSerialNumber() throws BeanException {
            Update update = SQL.Update(executionContext).
              table("veraweb.tguest").
              update("orderno", null).
              update("orderno_p", null).
              where(Expr.equal("fk_event", event.id));
            executionContext.execute(update);
        }

        protected void setSerialNumber(Map guest) throws BeanException, IOException {
            Integer invitationtype = (Integer) guest.get("invitationtype");
            Integer invitationstatus_a = (Integer) guest.get("invitationstatus_a");
            Integer invitationstatus_b = (Integer) guest.get("invitationstatus_b");
            Integer orderno_a;
            Integer orderno_b;

            if (invitationtype == null || invitationtype.intValue() == EventConstants.TYPE_MITPARTNER) {
                if (invitationstatus_a != null && invitationstatus_a.intValue() == 2) {
                    orderno_a = null;
                } else {
                    orderno_a = new Integer(++orderNo);
                }
                if (invitationstatus_b != null && invitationstatus_b.intValue() == 2) {
                    orderno_b = null;
                } else {
                    orderno_b = new Integer(++orderNo);
                }
            } else if (invitationtype.intValue() == EventConstants.TYPE_OHNEPARTNER) {
                if (invitationstatus_a != null && invitationstatus_a.intValue() == 2) {
                    orderno_a = null;
                } else {
                    orderno_a = new Integer(++orderNo);
                }
                orderno_b = null;
            } else if (invitationtype.intValue() == EventConstants.TYPE_NURPARTNER) {
                orderno_a = null;
                if (invitationstatus_b != null && invitationstatus_b.intValue() == 2) {
                    orderno_b = null;
                } else {
                    orderno_b = new Integer(++orderNo);
                }
            } else {
                throw new IOException("wrong invitationtype");
            }

            executionContext.execute(SQL.Update(executionContext).
              table("veraweb.tguest").
              update("tguest.orderno", orderno_a).
              update("tguest.orderno_p", orderno_b).
              where(Expr.equal("pk", guest.get("id"))));
        }

        protected void setSerialNumber(Select select) throws BeanException, IOException {
            for (Iterator it = executionContext.getDatabase().getList(select, executionContext).iterator(); it.hasNext(); ) {
                setSerialNumber((Map) it.next());
            }
        }

        protected Select getSelect() {
            return SQL.Select(executionContext).
              from("veraweb.tguest").
              selectAs("tguest.pk", "id").
              selectAs("tguest.invitationtype", "invitationtype").
              selectAs("invitationstatus", "invitationstatus_a").
              selectAs("invitationstatus_p", "invitationstatus_b").
              selectAs("CASE WHEN tcategorie.flags = 99 THEN diplodate_a_e1 ELSE NULL END", "diplodate").
              joinLeftOuter("veraweb.tperson", "tguest.fk_person", "tperson.pk").
              joinLeftOuter("veraweb.tcategorie", "tguest.fk_category", "tcategorie.pk");
        }
    }

    /**
     * Berechnet die 'Laufende Nummer' einer Gästeliste nach folgendem Schema:
     * <ul>
     * <li>Sortiert Gäste mit Rang ein.</li>
     * <li>Sortiert Gäste anhand ihrer Kategorie ein.</li>
     * <li>Sortiert alle anderen anhand ihres Namens ein.</li>
     * </ul>
     */
    static public class CalcSerialNumberImpl2 extends CalcSerialNumber {
        /**
         * Dieser Konstruktor übernimmt Datenbank und Veranstaltung zur
         * Benutzung bei der späteren Berechnung laufender Gästenummern.
         *
         * @param context FIXME
         * @param event   FIXME
         */
        public CalcSerialNumberImpl2(ExecutionContext context, Event event) {
            super(context, event);
        }

        /**
         * Diese Methode berechnet die tatsächlichen laufenden Nummern der
         * Gäste der im Konstruktor übergebenen Veranstaltung in der ebenda
         * übergebenen Datenbank nach folgendem Schema:
         * <ul>
         * <li>Sortiert Gäste mit Rang ein.</li>
         * <li>Sortiert Gäste anhand ihrer Kategorie ein.</li>
         * <li>Sortiert alle anderen anhand ihres Namens ein.</li>
         * </ul>
         */
        @Override
        public void calcSerialNumber() throws BeanException, IOException {
            clearSerialNumber();
            calcSerialNumberForGuestRank();

            Select select = SQL.Select(executionContext).
              from("veraweb.tcategorie").
              selectAs("pk", "id").
              selectAs("flags", "flag").
              orderBy(Order.asc("rank").andAsc("catname"));
            for (Iterator it = executionContext.getDatabase().getList(select, executionContext).iterator(); it.hasNext(); ) {
                Map map = (Map) it.next();
                if (((Integer) map.get("flag")).intValue() == Categorie.FLAG_DIPLO_CORPS) {
                    calcSerialNumberForDiploCorp((Integer) map.get("id"));
                } else {
                    calcSerialNumberForCategorie((Integer) map.get("id"));
                }
            }

            calcSerialNumberForGuestName();
        }

        protected void calcSerialNumberForGuestRank() throws BeanException, IOException {
            WhereList where = getGuestSerialNumberWhere(event);
            where.addAnd(Expr.isNotNull("tguest.rank"));

            Select select = getSelect();
            select.where(where);
            select.orderBy(Order.asc("tguest.rank").andAsc("lastname_a_e1").andAsc("firstname_a_e1"));

            setSerialNumber(select);
        }

        protected void calcSerialNumberForGuestName() throws BeanException, IOException {
            WhereList where = getGuestSerialNumberWhere(event);

            Select select = getSelect();
            select.where(where);
            select.orderBy(Order.asc("lastname_a_e1").andAsc("firstname_a_e1"));

            setSerialNumber(select);
        }

        protected void calcSerialNumberForCategorie(Integer categorieId) throws BeanException, IOException {
            WhereList where = getGuestSerialNumberWhere(event);
            where.addAnd(Expr.equal("tperson_categorie.fk_categorie", categorieId));

            Select select = getSelect();
            select.join("veraweb.tperson_categorie", "tperson_categorie.fk_person", "tguest.fk_person");
            select.where(where);
            select.orderBy(Order.asc("tperson_categorie.rank").andAsc("lastname_a_e1").andAsc("firstname_a_e1"));

            setSerialNumber(select);
        }

        protected void calcSerialNumberForDiploCorp(Integer categorieId) throws BeanException, IOException {
            WhereList where = getGuestSerialNumberWhere(event);
            where.addAnd(Expr.equal("tperson_categorie.fk_categorie", categorieId));

            Select select = getSelect();
            select.join("veraweb.tperson_categorie", "tperson_categorie.fk_person", "tguest.fk_person");
            select.where(where);
            select.orderBy(Order.asc("tperson.diplodate").andAsc("lastname_a_e1").andAsc("firstname_a_e1"));

            setSerialNumber(select);
        }

        /**
         * @param event Event
         * @return Where-Liste mit Einschränkung auf die übergebene Veranstaltung
         * und nur nocht nicht einsortierte Gäste.
         */
        private WhereList getGuestSerialNumberWhere(Event event) {
            WhereList where = new WhereList();
            where.addAnd(Expr.equal("tguest.fk_event", event.id));
            where.addAnd(Expr.equal("ishost", new Integer(0)));
            where.addAnd(Expr.isNull("orderno"));
            where.addAnd(Expr.isNull("orderno_p"));
            return where;
        }
    }

    /**
     * Berechnet die 'Laufende Nummer' einer Gästeliste nach folgendem Schema:
     * <ul>
     * <li>Sortiert Gäste anhand ihrer Kategorie ein.</li>
     * <li>Sortiert Gäste mit Rang ein.</li>
     * <li>Sortiert Gäste nach Akkreditierungsdatum ein.</li>
     * <li>Sortiert alle anderen anhand ihres Namens ein.</li>
     * </ul>
     */
    static public class CalcSerialNumberImpl3 extends CalcSerialNumber {
        /**
         * Dieser Konstruktor übernimmt Datenbank und Veranstaltung zur
         * Benutzung bei der späteren Berechnung laufender Gästenummern.
         *
         * @param context FIXME
         * @param event   FIXME
         */
        public CalcSerialNumberImpl3(ExecutionContext context, Event event) {
            super(context, event);
        }

        /**
         * Diese Methode berechnet die tatsächlichen laufenden Nummern der
         * Gäste der im Konstruktor übergebenen Veranstaltung in der ebenda
         * übergebenen Datenbank nach folgendem Schema:
         * <ul>
         * <li>Sortiert Gäste anhand ihrer Kategorie ein.</li>
         * <li>Sortiert Gäste mit Rang ein.</li>
         * <li>Sortiert Gäste nach Akkreditierungsdatum ein.</li>
         * <li>Sortiert alle anderen anhand ihres Namens ein.</li>
         * </ul>
         */
        @Override
        public void calcSerialNumber() throws BeanException, IOException {
            clearSerialNumber();

            WhereList where = new WhereList();
            // Nur diese Veranstaltung
            where.addAnd(Expr.equal("tguest.fk_event", event.id));
            // Kein Gastgeber
            where.addAnd(Expr.equal("ishost", new Integer(0)));
            // Auf Platz
            where.addAnd(Expr.notEqual("reserve", new Integer(1)));
            where.addAnd(new RawClause("(NOT(" +
              "(invitationtype = 1" +
              " AND invitationstatus IS NOT NULL" +
              " AND invitationstatus = 2" +
              " AND invitationstatus_p IS NOT NULL" +
              " AND invitationstatus_p = 2) OR " +
              "(invitationtype = 2" +
              " AND invitationstatus IS NOT NULL" +
              " AND invitationstatus = 2) OR " +
              "(invitationtype = 3" +
              " AND invitationstatus_p IS NOT NULL" +
              " AND invitationstatus_p = 2)))"));

            List order = new ArrayList();
            order.add("tcategorie.rank");
            order.add("tcategorie.catname");
            order.add("tcategorie.pk");
            order.add("tguest.rank");
            order.add("diplodate");
            order.add("lastname_a_e1");
            order.add("firstname_a_e1");

            Select select = getSelect();
            select.where(where);
            select.orderBy(DatabaseHelper.getOrder(order));

            setSerialNumber(select);
        }
    }
}
