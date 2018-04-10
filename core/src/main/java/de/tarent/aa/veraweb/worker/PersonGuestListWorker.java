package de.tarent.aa.veraweb.worker;

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
 *  © 2005, 2006, 2007, 2008 Christoph Jerolimov <jerolimov@gmx.de>
 *  © 2008, 2009, 2010 Carsten Klein <c.klein@tarent.de>
 *  © 2014 Martin Ley <m.ley@tarent.de>
 *  © 2014, 2015 Max Marche <m.marche@tarent.de>
 *  © 2007 Jan Meyer <jan@evolvis.org>
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

import de.tarent.aa.veraweb.beans.Event;
import de.tarent.aa.veraweb.beans.PersonSearch;
import de.tarent.aa.veraweb.beans.facade.EventConstants;
import de.tarent.dblayer.sql.clause.Clause;
import de.tarent.dblayer.sql.clause.Expr;
import de.tarent.dblayer.sql.clause.Where;
import de.tarent.dblayer.sql.statement.Select;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.beans.BeanFactory;
import de.tarent.octopus.beans.Database;
import de.tarent.octopus.server.OctopusContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Dieser Octopus-Worker erweitert die Personen-Liste (vergleiche Worker
 * {@link PersonListWorker}) um Auswahllisten für den Partner und das
 * Reserve-Flag.
 *
 * @author Christoph Jerolimov
 * @version $Revision: 1.1 $
 */
public class PersonGuestListWorker extends PersonListWorker {
    //
    // Octopus-Aktionen
    //
    /**
     * Eingabe-PArameter für die Octopus-Aktion {@link #extendGuestSelection(OctopusContext)}
     */
    public static final String INPUT_extendGuestSelection[] = {};

    /**
     * Diese Octopus-Aktion erweitert die Listenselektionsfunktionalität des
     * {@link de.tarent.octopus.beans.BeanListWorker}s um weitere
     * Selektionsspalten.<br>
     * Hierzu wird zusätzlich auf die Daten im Octopus-Content unter "event", im Request
     * unter "list", "search", "selectAll", "selectNone", "{ID}-partner", "{ID}-reserve"
     * und "{ID}-category" und in der Session unter "addguest-invitepartner",
     * "addguest-selectreserve", "addguest-invitecategory" zurückgegriffen. Daten werden
     * dann im Octopus-Content unter "invitepartner", "selectreserve", "invitecategory",
     * "personCategorie" und "search" und in der Session unter "selectionPErson",
     * "addguest-invitepartner", "addguest-selectreserve" und "addguest-invitecategory"
     * abgelegt.
     *
     * @param octopusContext Octopus-Kontext
     */
    public void extendGuestSelection(OctopusContext octopusContext) throws BeanException, IOException {
        Database database = getDatabase(octopusContext);
        Event event = (Event) octopusContext.contentAsObject("event");
        PersonSearch search = getSearch(octopusContext);
        // IDs der sichtbaren Personen
        List ids = (List) BeanFactory.transform(octopusContext.requestAsObject(INPUT_LIST), List.class);
        // IDs der selektierten Personen
        List invitemain = (List) octopusContext.sessionAsObject("selectionPerson");
        // IDs der Personen mit Partner
        List invitepartner = (List) octopusContext.sessionAsObject("addguest-invitepartner");
        // IDs der Personen deren Reserve selektiert ist
        List selectreserve = (List) octopusContext.sessionAsObject("addguest-selectreserve");
        // IDs der Personen, welche als delegation selektiert sind
        List selectdelegation = (List) octopusContext.sessionAsObject("addguest-selectdelegation");

        Map invitecategory = (Map) octopusContext.sessionAsObject("addguest-invitecategory");

        octopusContext.getContentObject().setField("action", "guest");

        if ("reset".equals(octopusContext.requestAsString("search"))) {
            if (invitemain.size() == 1) {
                octopusContext.getRequestObject().setParam(INPUT_SELECTALL, Boolean.TRUE);
            }

            Select select = database.getSelectIds(database.createBean(BEANNAME));
            Clause clause = getPersonListFilter(octopusContext, false);
            if (search.categoriesSelection != null && search.categorie2 != null) {
                select.joinLeftOuter("veraweb.tperson_categorie AS cat1", "tperson.pk", "cat1.fk_person");
                select.joinLeftOuter("veraweb.tperson_categorie AS cat2", "tperson.pk", "cat2.fk_person");
                select.selectAs("cat1.fk_categorie", "category");
            } else if (search.categoriesSelection != null && search.categoriesSelection.size() > 0) {
                select.joinLeftOuter("veraweb.tperson_categorie AS cat1", "tperson.pk", "cat1.fk_person");
                select.selectAs("cat1.fk_categorie", "category");
            } else if (search.categorie2 != null) {
                select.joinLeftOuter("veraweb.tperson_categorie AS cat2", "tperson.pk", "cat2.fk_person");
                select.selectAs("cat2.fk_categorie", "category");
            } else {
                select.selectAs("NULL", "category");
            }
            select.where(clause);

            /*
             * modified to support searching for persons that have no categories assigned as per change request for version 1.2.0
             * cklein
             * 2008-02-26
             */
            if (search.categoriesSelection.size() == 1 && ((Integer) search.categoriesSelection.get(0)).intValue() == 0) {
                select.whereAnd(Expr.isNull("cat1.fk_person"));
            }

            // Kategorien berechnen
            invitecategory = new HashMap();
            if (search.categoriesSelection != null) {
                for (Iterator it = database.getList(select, database).iterator(); it.hasNext(); ) {
                    Map data = (Map) it.next();
                    invitecategory.put(data.get("id"), data.get("category") /*search.categorie*/);
                }
            } else if (search.categorie2 != null) {
                for (Iterator it = database.getList(select, database).iterator(); it.hasNext(); ) {
                    Map data = (Map) it.next();
                    invitecategory.put(data.get("id"), search.categorie2);
                }
            } else {
                boolean hasMore = false;
                Integer id = null, category = null, oldId = null, oldCategory = null;
                for (Iterator it = database.getList(select, database).iterator(); it.hasNext(); ) {
                    Map data = (Map) it.next();
                    id = (Integer) data.get("id");
                    category = (Integer) data.get("category");
                    if (id.equals(oldId)) {
                        hasMore = true;
                    } else {
                        if (!hasMore) {
                            invitecategory.put(oldId, oldCategory);
                        }
                        hasMore = false;
                    }
                    oldId = id;
                    oldCategory = category;
                }
            }
        } else {
            if (invitecategory == null) {
                invitecategory = new HashMap();
            }
        }

        if (octopusContext.requestAsBoolean(INPUT_SELECTNONE).booleanValue()) {
            // Leere Liste anlegen.
            invitemain = new ArrayList();
            invitepartner = new ArrayList();
            selectreserve = new ArrayList();
            selectdelegation = new ArrayList();
        } else if (octopusContext.requestAsBoolean(INPUT_SELECTALL).booleanValue()) {
            // Alle IDs aus der Datenbank in die Liste kopieren.
            invitepartner = new ArrayList();
            selectreserve = new ArrayList();
            selectdelegation = new ArrayList();

            Select select = database.getSelectIds(database.createBean(BEANNAME));
            if (search.categoriesSelection != null && search.categorie2 != null) {
                select.joinLeftOuter("veraweb.tperson_categorie AS cat1", "tperson.pk", "cat1.fk_person");
                select.joinLeftOuter("veraweb.tperson_categorie AS cat2", "tperson.pk", "cat2.fk_person");
                select.selectAs("cat1.fk_categorie", "category");
            } else if (search.categoriesSelection != null && search.categoriesSelection.size() > 0) {
                select.joinLeftOuter("veraweb.tperson_categorie AS cat1", "tperson.pk", "cat1.fk_person");
                select.selectAs("cat1.fk_categorie", "category");
            } else if (search.categorie2 != null) {
                select.joinLeftOuter("veraweb.tperson_categorie AS cat2", "tperson.pk", "cat2.fk_person");
                select.selectAs("cat2.fk_categorie", "category");
            } else {
                select.selectAs("NULL", "category");
            }
            Clause clause = getPersonListFilter(octopusContext, false);
            if (event.invitationtype.intValue() != EventConstants.TYPE_NURPARTNER) {
                select.where(Where.and(clause, Where.or(
                        Expr.greater("lastname_a_e1", ""),
                        Expr.greater("firstname_a_e1", ""))));
            }
            /*
             * modified to support searching for persons that have no categories assigned as per change request for version 1.2.0
             * cklein
             * 2008-02-26
             */
            if (search.categoriesSelection.size() == 1 && ((Integer) search.categoriesSelection.get(0)).intValue() == 0) {
                select.whereAnd(Expr.isNull("cat1.fk_person"));
            }
            if (event.invitationtype.intValue() != EventConstants.TYPE_OHNEPARTNER) {
                select.where(Where.and(clause, Where.or(
                        Expr.greater("lastname_b_e1", ""),
                        Expr.greater("firstname_b_e1", ""))));
                extendSelectByMultipleCategorySearch(octopusContext, search, select);
                for (Iterator it = database.getList(select, database).iterator(); it.hasNext(); ) {
                    invitepartner.add(((Map) it.next()).get("id"));
                }
            }
        } else if (invitepartner != null || selectreserve != null || selectdelegation != null) {
            // IDs zusammenführen.
            if (invitepartner == null) {
                invitepartner = new ArrayList();
            }
            if (selectreserve == null) {
                selectreserve = new ArrayList();
            }
            if (selectdelegation == null) {
                selectdelegation = new ArrayList();
            }

            for (Iterator it = ids.iterator(); it.hasNext(); ) {
                Integer id = new Integer((String) it.next());
                if (octopusContext.requestAsBoolean(id + "-partner").booleanValue()) {
                    if (invitepartner.indexOf(id) == -1) {
                        invitepartner.add(id);
                    }
                } else {
                    invitepartner.remove(id);
                }
                if (octopusContext.requestAsBoolean(id + "-reserve").booleanValue()) {
                    if (selectreserve.indexOf(id) == -1) {
                        selectreserve.add(id);
                    }
                } else {
                    selectreserve.remove(id);
                }
                if (octopusContext.requestAsBoolean(id + "-delegation").booleanValue()) {
                    if (selectdelegation.indexOf(id) == -1) {
                        selectdelegation.add(id);
                    }
                } else {
                    selectdelegation.remove(id);
                }

            }
        }

        for (Iterator it = ids.iterator(); it.hasNext(); ) {
            Integer id = new Integer((String) it.next());
            invitecategory.put(id, octopusContext.requestAsInteger(id + "-category"));
        }

        octopusContext.setSession("selection" + BEANNAME, invitemain);
        octopusContext.setSession("addguest-invitepartner", invitepartner);
        octopusContext.setSession("addguest-selectreserve", selectreserve);
        octopusContext.setSession("addguest-selectdelegation", selectdelegation);
        octopusContext.setSession("addguest-invitecategory", invitecategory);
        octopusContext.setContent("invitepartner", invitepartner);
        octopusContext.setContent("selectreserve", selectreserve);
        octopusContext.setContent("selectdelegation", selectdelegation);
        octopusContext.setContent("invitecategory", invitecategory);

        octopusContext.setContent("personCategorie", new PersonCategorie(database));
        octopusContext.setContent("search", search);
    }

    /**
     * Eingabe-PArameter für die Octopus-Aktion {@link #clearGuestSelection(OctopusContext)}
     */
    public static final String INPUT_clearGuestSelection[] = {};

    /**
     * Diese Octopus-Aktion setzt die zusätzlichen Personen-Selektionen in der
     * Session unter "addguest-selectreserve", "addguest-invitepartner" und
     * "addguest-invitecategory" zurück.
     *
     * @param cntx Octopus-Kontext.
     */
    public void clearGuestSelection(OctopusContext cntx) {
        cntx.setSession("addguest-selectreserve", null);
        cntx.setSession("addguest-selectdelegation", null);
        cntx.setSession("addguest-invitepartner", null);
        cntx.setSession("addguest-invitecategory", null);
    }

    //
    // innere Klassen
    //

    /**
     * Diese Hilfsklasse wird in der obigen Octopus-Aktion extendGuestSelection() benutzt,
     * um ein Objekt zur Verfügung zu stellen, das zu Personen die zugehörigen personalisierten Kategorien liefert.
     */
    static public class PersonCategorie {
        /**
         * Aus dieser DB sollen die personalisierten Kategorien gelesen werden.
         */
        private final Database database;

        /**
         * Dieser Konstruktor legt die übergeben DB lokal ab.
         *
         * @param database Aus dieser DB sollen die personalisierten Kategorien gelesen werden.
         */
        private PersonCategorie(Database database) {
            assert database != null;
            this.database = database;
        }

        /**
         * Diese Methode liefert die personalisierten Kategorien zu einer Personen-ID.
         *
         * @param personId ID der Person, deren personalisierte Kategorien gesucht sind.
         * @return Liste der personalisierten Kategorien der Person zu der übergebenen ID.
         */
        public List getList(Integer personId) {
            if (personId == null) {
                return Collections.EMPTY_LIST;
            }

            try {
                return
                        database.getList(
                                database.getSelect("PersonCategorie").
                                        joinLeftOuter("veraweb.tcategorie", "tperson_categorie.fk_categorie", "tcategorie.pk").
                                        selectAs("tcategorie.rank", "catrank").
                                        selectAs("tcategorie.catname", "name").
                                        selectAs("tcategorie.flags", "flags").
                                        where(Expr.equal("fk_person", personId)), database);
            } catch (BeanException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return Collections.EMPTY_LIST;
        }
    }
}
