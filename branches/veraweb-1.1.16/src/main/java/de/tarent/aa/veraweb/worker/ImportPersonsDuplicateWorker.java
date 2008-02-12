/*
 * veraweb,
 * Veranstaltungsmanagment veraweb
 * Copyright (c) 2005-2007 tarent GmbH
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License,version 2
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 *
 * tarent GmbH., hereby disclaims all copyright
 * interest in the program 'veraweb'
 * Signature of Elmar Geese, 21 November 2007
 * Elmar Geese, CEO tarent GmbH.
 */

/*
 * $Id: ImportPersonsDuplicateWorker.java,v 1.1 2007/06/20 11:56:51 christoph Exp $
 * Created on 29.03.2005
 */
package de.tarent.aa.veraweb.worker;

import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import de.tarent.aa.veraweb.beans.ImportPerson;
import de.tarent.aa.veraweb.beans.Person;
import de.tarent.aa.veraweb.beans.facade.PersonConstants;
import de.tarent.dblayer.sql.SQL;
import de.tarent.dblayer.sql.clause.Expr;
import de.tarent.dblayer.sql.clause.Order;
import de.tarent.dblayer.sql.clause.Where;
import de.tarent.dblayer.sql.clause.WhereList;
import de.tarent.dblayer.sql.statement.Select;
import de.tarent.dblayer.sql.statement.Update;
import de.tarent.octopus.custom.beans.BeanException;
import de.tarent.octopus.custom.beans.Database;
import de.tarent.octopus.custom.beans.veraweb.ListWorkerVeraWeb;
import de.tarent.octopus.server.OctopusContext;

/**
 * Dieser Octopus-Worker bearbeitet Import-Personen-Duplikatslisten.
 * 
 * @author hendrik
 */
public class ImportPersonsDuplicateWorker extends ListWorkerVeraWeb {
	//
	// Konstruktoren
	//
	/**
	 * Der Konstruktor legt den Bean-Namen fest.
	 */
	public ImportPersonsDuplicateWorker() {
		super("ImportPerson");
	}

	//
	// Oberklasse BeanListWorker
	//
	/**
	 * @see de.tarent.octopus.custom.beans.BeanListWorker#showList(de.tarent.octopus.server.OctopusContext)
	 */
	public List showList(OctopusContext cntx) throws BeanException, IOException {
		Map importDuplicatesProperties = (Map) cntx.moduleConfig().getParams().get("importDuplicatesProperties");
		if (importDuplicatesProperties == null)
			ImportPersonsWorker.logger.warn("Konfiguration f�r die Duplikatbearbeitung beim Personen-Import wurde nicht gefunden.");
		if (cntx.sessionAsObject("limit" + BEANNAME) == null)
			cntx.setSession("limit" + BEANNAME, new Integer(Integer.parseInt((String) importDuplicatesProperties.get("dsCount"))));
		
		List beans = super.showList(cntx);
		
		// Zu den Duplikatdatens�tzen noch einige Beispiel-Duplikate hinzuf�gen.
		int dsCount = -1;
		if (importDuplicatesProperties != null)
			dsCount = Integer.parseInt((String) importDuplicatesProperties.get("dupCount"));
		
		Database database = getDatabase(cntx);
		if (beans != null) {
			for(Iterator it = beans.iterator(); it.hasNext(); ) {
				ImportPerson importPerson = (ImportPerson) it.next();
				importPerson.setMoreDuplicates(false);
				
				if (importPerson.getDuplicateList() == null) {
					List dups = null;
					StringTokenizer tokenizer = new StringTokenizer(
							importPerson.duplicates,
							Character.toString(ImportPerson.PK_SEPERATOR_CHAR));
					
					int count = 0;
					while (tokenizer.hasMoreTokens()) {
						if (dsCount != -1 && count >= dsCount) {
							importPerson.setMoreDuplicates(true);
							break;
						}
						Integer pk = new Integer(tokenizer.nextToken());
						
						Person person = new Person();
						person.setField("id", pk);
						Select select = database.getSelect(person);
						select.where(Where.and(
								Expr.equal("deleted", PersonConstants.DELETED_FALSE),
								database.getWhere(person)));
						person = (Person) database.getBean("Person", select);
						if (dups == null)
							dups = new LinkedList();
						dups.add(person);
						count++;
					}
					if (dups == null)
						dups = Collections.EMPTY_LIST;
					importPerson.setDuplicateList(dups);
				}
			}
		}
		return beans;
	}

	public void saveList(OctopusContext cntx) throws BeanException, IOException {
		if (cntx.requestContains(INPUT_BUTTON_SAVE)) {
			Database database = getDatabase(cntx);
			ImportPerson sample = new ImportPerson();
			Long importId = new Long(cntx.requestAsString("importId"));
			
			// Entfernt alle markierungen in der Datenbank.
			Update update = SQL.Update();
			update.table(database.getProperty(sample, "table"));
			update.update("dupcheckstatus", ImportPerson.FALSE);
			update.where(Where.and(
					Expr.equal("deleted", PersonConstants.DELETED_FALSE),
					Expr.equal("fk_import", importId)));
			database.execute(update);
			
			// Markierungen wieder setzten.
			List selection = getSelection(cntx, null);
			if (selection != null && selection.size() > 0) {
				update = SQL.Update();
				update.table(database.getProperty(sample, "table"));
				update.update("dupcheckstatus", ImportPerson.TRUE);
				update.where(Where.and(Where.and(
						Expr.equal("deleted", PersonConstants.DELETED_FALSE),
						Expr.equal("fk_import", importId)),
						Expr.in("pk", selection)));
				database.execute(update);
			}
			
			cntx.setContent("countUpdate",
					database.getCount(
					database.getCount(sample).where(Where.and(
							Expr.equal("deleted", PersonConstants.DELETED_FALSE),
							Expr.equal("fk_import", importId)))));
		}
	}

	/**
	 * Bedingung:
	 * Es existieren Duplikate zu dem Datensatz.
	 * Datensatz wurde noch nicht festgeschrieben.
	 * Nur Datens�tze von dem aktuellen Importvorgang.
	 */
	protected void extendWhere(OctopusContext cntx, Select select) throws BeanException {
		Database database = getDatabase(cntx);
		ImportPerson sample = new ImportPerson();
		Long importId = new Long(cntx.requestAsString("importId"));
		
		try {
			WhereList list = new WhereList();
			list.addAnd(Expr.isNotNull(database.getProperty(sample, "duplicates")));
			list.addAnd(Expr.equal(database.getProperty(sample, "dupcheckaction"), ImportPerson.FALSE));
			list.addAnd(Expr.equal(database.getProperty(sample, "fk_import"), importId));
			select.where(list);
		} catch (IOException e) {
			throw new BeanException("Fehler beim Lesen von Bean-Parametern", e);
		}
		
		cntx.setContent("importId", importId);
	}

	protected void extendColumns(OctopusContext cntx, Select select) throws BeanException {
		Database database = getDatabase(cntx);
		try {
			select.orderBy(Order.asc(database.getProperty(database.createBean(BEANNAME), "lastname_a_e1")));
		} catch (IOException e) {
			throw new BeanException("Fehler beim Lesen von Bean-Parametern", e);
		}
	}
}
