/**
 * veraweb, platform independent webservice-based event management
 * (Veranstaltungsmanagment VerA.web), is
 * Copyright © 2004-2008 tarent GmbH
 * Copyright © 2013 tarent solutions GmbH
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License, version 2, as
 * published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see: http://www.gnu.org/licenses/
 */
package de.tarent.aa.veraweb.worker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
    /** Eingabe-PArameter für die Octopus-Aktion {@link #extendGuestSelection(OctopusContext)} */
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
     * @param cntx Octopus-Kontext
	 */
	public void extendGuestSelection(OctopusContext cntx) throws BeanException, IOException {
		Database database = getDatabase(cntx);
		Event event = (Event)cntx.contentAsObject("event");
		PersonSearch search = getSearch(cntx);
		// IDs der sichtbaren Personen
		List ids = (List)BeanFactory.transform(cntx.requestAsObject(INPUT_LIST), List.class);
		// IDs der selektierten Personen
		List invitemain = (List)cntx.sessionAsObject("selectionPerson");
		// IDs der Personen mit Partner
		List invitepartner = (List)cntx.sessionAsObject("addguest-invitepartner");
		// IDs der Personen deren Reserve selektiert ist
		List selectreserve = (List)cntx.sessionAsObject("addguest-selectreserve");
		Map invitecategory = (Map)cntx.sessionAsObject("addguest-invitecategory");
		
		
		if ("reset".equals(cntx.requestAsString("search"))) {
			if (invitemain.size() == 1) {
				cntx.getRequestObject().setParam(INPUT_SELECTALL, Boolean.TRUE);
			}
			
			Select select = database.getSelectIds(database.createBean(BEANNAME));
			Clause clause = getPersonListFilter(cntx);
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
			if ( search.categoriesSelection.size() == 1 && ( ( Integer ) search.categoriesSelection.get( 0 ) ).intValue() == 0 )
			{
				select.whereAnd( Expr.isNull( "cat1.fk_person" ) );
			}
			
			// Kategorien berechnen
			invitecategory = new HashMap();
			if (search.categoriesSelection!= null) {
				for (Iterator it = database.getList(select, database).iterator(); it.hasNext(); ) {
					Map data = (Map)it.next();
					invitecategory.put(data.get("id"), data.get( "category" ) /*search.categorie*/ );
				}
			} else if (search.categorie2 != null) {
				for (Iterator it = database.getList(select, database).iterator(); it.hasNext(); ) {
					Map data = (Map)it.next();
					invitecategory.put(data.get("id"), search.categorie2);
				}
			} else {
				boolean hasMore = false;
				Integer id = null, category = null, oldId = null, oldCategory = null;
				for (Iterator it = database.getList(select, database).iterator(); it.hasNext(); ) {
					Map data = (Map)it.next();
					id = (Integer)data.get("id");
					category = (Integer)data.get("category");
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
			if (invitecategory == null) invitecategory = new HashMap();
		}
		
		if (cntx.requestAsBoolean(INPUT_SELECTNONE).booleanValue()) {
			// Leere Liste anlegen.
			invitemain = new ArrayList();
			invitepartner = new ArrayList();
			selectreserve = new ArrayList();
		} else if (cntx.requestAsBoolean(INPUT_SELECTALL).booleanValue()) {
			// Alle IDs aus der Datenbank in die Liste kopieren.
			invitepartner = new ArrayList();
			selectreserve = new ArrayList();
			
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
			Clause clause = getPersonListFilter(cntx);
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
			if ( search.categoriesSelection.size() == 1 && ( ( Integer ) search.categoriesSelection.get( 0 ) ).intValue() == 0 )
			{
				select.whereAnd( Expr.isNull( "cat1.fk_person" ) );
			}
			if (event.invitationtype.intValue() != EventConstants.TYPE_OHNEPARTNER) {
				select.where(Where.and(clause, Where.or(
						Expr.greater("lastname_b_e1", ""),
						Expr.greater("firstname_b_e1", ""))));
				extendSelectByMultipleCategorySearch( cntx, search, select );
				for (Iterator it = database.getList(select, database).iterator(); it.hasNext(); ) {
					invitepartner.add(((Map)it.next()).get("id"));
				}
			}
		} else {
			// IDs zusammenführen.
			if (invitepartner == null) invitepartner = new ArrayList();
			if (selectreserve == null) selectreserve = new ArrayList();
			
			for (Iterator it = ids.iterator(); it.hasNext(); ) {
				Integer id = new Integer((String)it.next());
				if (cntx.requestAsBoolean(id + "-partner").booleanValue()) {
					if (invitepartner.indexOf(id) == -1)
						invitepartner.add(id);
				} else {
					invitepartner.remove(id);
				}
				if (cntx.requestAsBoolean(id + "-reserve").booleanValue()) {
					if (selectreserve.indexOf(id) == -1)
						selectreserve.add(id);
				} else {
					selectreserve.remove(id);
				}
			}
		}
		
		for (Iterator it = ids.iterator(); it.hasNext(); ) {
			Integer id = new Integer((String)it.next());
			invitecategory.put(id, cntx.requestAsInteger(id + "-category"));
		}
		
		cntx.setSession("selection" + BEANNAME, invitemain);
		cntx.setSession("addguest-invitepartner", invitepartner);
		cntx.setSession("addguest-selectreserve", selectreserve);
		cntx.setSession("addguest-invitecategory", invitecategory);
		cntx.setContent("invitepartner", invitepartner);
		cntx.setContent("selectreserve", selectreserve);
		cntx.setContent("invitecategory", invitecategory);
		
		cntx.setContent("personCategorie", new PersonCategorie(database));
		cntx.setContent("search", search);
	}

    /** Eingabe-PArameter für die Octopus-Aktion {@link #clearGuestSelection(OctopusContext)} */
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
		cntx.setSession("addguest-invitepartner", null);
		cntx.setSession("addguest-invitecategory", null);
	}

    //
    // innere Klassen
    //
    /**
     * Diese Hilfsklasse wird in der obigen Octopus-Aktion
     * {@link CopyOfPersonGuestListWorker#extendGuestSelection(OctopusContext)}
     * benutzt, um ein Objekt zur Verfügung zu stellen, das zu Personen
     * die zugehörigen personalisierten Kategorien liefert. 
     */
	static public class PersonCategorie {
        /** Aus dieser DB sollen die personalisierten Kategorien gelesen werden. */
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
			if (personId == null)
				return Collections.EMPTY_LIST;
			
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
