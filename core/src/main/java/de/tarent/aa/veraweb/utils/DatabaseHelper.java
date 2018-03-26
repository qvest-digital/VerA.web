package de.tarent.aa.veraweb.utils;

/*-
 * Veranstaltungsmanagement VerA.web (platform-independent
 * webservice-based event management) is Copyright
 *  © 2014, 2015, 2016, 2017 Атанас Александров <a.alexandrov@tarent.de>
 *  © 2018 Атанас Александров <sirakov@gmail.com>
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
 *  © 2013, 2014, 2015, 2016, 2017, 2018 mirabilos <t.glaser@tarent.de>
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

import de.tarent.dblayer.sql.Format;
import de.tarent.dblayer.sql.clause.Clause;
import de.tarent.dblayer.sql.clause.Expr;
import de.tarent.dblayer.sql.clause.Order;
import de.tarent.dblayer.sql.clause.RawClause;
import de.tarent.dblayer.sql.clause.WhereList;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Datenbank-Hilfsklasse, erstellt u.a. Where-Bedingungen.
 *
 * @author Christoph Jerolimov
 */
public class DatabaseHelper {
    /**
     * Datenbankseitiges GROSS schreiben: MethodenName(
     */
    public static final String UPPER_PRE = "veraweb.upper_fix(";
    /**
     * Datenbankseitiges GROSS schreiben: )
     */
    public static final String UPPER_POST = ")";
    /**
     * Datenbankseitiges klein schreiben: MethodenName(
     */
    public static final String LOWER_PRE = "veraweb.upper_fix(";
    /**
     * Datenbankseitiges klein schreiben: )
     */
    public static final String LOWER_POST = ")";

    /**
     * Gibt eine Where-Clause zurück, die den übergebenen Suchbegriff
     * (<code>search</code>) in allen übergebenen Spalten (<code>column</code>)
     * sucht. Wenn im übergebenem Suchbegriff ein * oder ? vorkommt wird
     * ein entsprechendes SQL LIKE mit % und _ verwendet, sofern die Zeichen nicht
     * mit einem \ escaped wurden. Mehere Spalten werden mit ORs verknüpft.
     *
     * @param search Suchbegriff
     * @param column Liste mit Spaltennamen
     * @return Where-Clause
     */
    public static Clause getWhere(String search, String column[]) {
        WhereList list = new WhereList();
        if (search.indexOf('*') == -1 && search.indexOf('?') == -1) {
            for (int i = 0; i < column.length; i++) {
                list.addOr(Expr.equal(
                        UPPER_PRE + column[i] + UPPER_POST, new RawClause(
                                UPPER_PRE + Format.format(search) + UPPER_POST)));
            }
        } else {
            search = search.replaceAll("[*]", "%").replaceAll("[?]", "_");
            search = search.replaceAll("\\\\%", "*").replaceAll("\\\\_", "?");
            for (int i = 0; i < column.length; i++) {
                list.addOr(Expr.like(
                        UPPER_PRE + column[i] + UPPER_POST, new RawClause(
                                UPPER_PRE + Format.format(search) + UPPER_POST)));
            }
        }
        return list;
    }

    /**
     * Gibt eine Order-Clause zurück, schaut jeweils nach der Spalte ob
     * der Wert ASC oder DESC ist und wendet dieses Attribut entsprechend an.
     *
     * @param list FIXME
     * @return Order-Clause
     */
    public static Order getOrder(List list) {
        if (list != null && list.size() > 0) {
            int pos = 0;
            Order order = null;
            if (list.size() > 1) {
                if ("DESC".equalsIgnoreCase((String) list.get(1))) {
                    order = Order.desc((String) list.get(0));
                    pos += 2;
                } else if ("ASC".equalsIgnoreCase((String) list.get(1))) {
                    order = Order.asc((String) list.get(0));
                    pos += 2;
                } else {
                    order = Order.asc((String) list.get(0));
                    pos += 1;
                }
            } else {
                order = Order.asc((String) list.get(0));
                pos++;
            }
            while (pos < list.size()) {
                if (pos < list.size() - 1) {
                    if ("DESC".equalsIgnoreCase((String) list.get(pos + 1))) {
                        order = order.andDesc((String) list.get(pos));
                        pos += 2;
                    } else if ("ASC".equalsIgnoreCase((String) list.get(pos + 1))) {
                        order = order.andAsc((String) list.get(pos));
                        pos += 2;
                    } else {
                        order = order.andAsc((String) list.get(pos));
                        pos += 1;
                    }
                } else {
                    order = order.andAsc((String) list.get(pos));
                    pos++;
                }
            }
            return order;
        } else {
            return null;
        }
    }

    public static String listsToIdListString(List[] lists) {
        StringBuffer result = new StringBuffer();
        Set<Integer> coalesced = new HashSet<Integer>();

		/* coalesce all of main, partner and reserve into a single identity set
		   this fixes an issue where the user can select either partner or reserve
		   but not the main contact, which would result in the person not being
		   invited.
		 */
        for (int i = 0; i < lists.length; i++) {
            coalesced.addAll(lists[i]);
        }

        Iterator<Integer> i = coalesced.iterator();
        while (i.hasNext()) {
            result.append(i.next());
            result.append(',');
        }

        if (result.length() > 0) {
            result.setLength(result.length() - 1);
            return result.toString();
        }

        return "NULL";
    }
}
