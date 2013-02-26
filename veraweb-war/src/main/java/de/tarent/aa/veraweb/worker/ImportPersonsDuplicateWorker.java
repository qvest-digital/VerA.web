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
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.beans.Database;
import de.tarent.octopus.beans.TransactionContext;
import de.tarent.octopus.beans.veraweb.ListWorkerVeraWeb;
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
	 * @see de.tarent.octopus.beans.BeanListWorker#showList(de.tarent.octopus.server.OctopusContext)
	 */
	@Override
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

	@Override
    public void saveList(OctopusContext cntx) throws BeanException, IOException {
		if (cntx.requestContains(INPUT_BUTTON_SAVE)) {
			Database database = getDatabase(cntx);
			TransactionContext context = database.getTransactionContext();

			ImportPerson sample = new ImportPerson();
			Long importId = new Long(cntx.requestAsString("importId"));

			try
			{
				// Entfernt alle markierungen in der Datenbank.
				Update update = SQL.Update( context );
				update.table(database.getProperty(sample, "table"));
				update.update("dupcheckstatus", ImportPerson.FALSE);
				update.where(Where.and(
						Expr.equal("deleted", PersonConstants.DELETED_FALSE),
						Expr.equal("fk_import", importId)));
				context.execute(update);
				
				// Markierungen wieder setzten.
				List selection = getSelection(cntx, null);
				if (selection != null && selection.size() > 0) {
					update = SQL.Update( context );
					update.table(database.getProperty(sample, "table"));
					update.update("dupcheckstatus", ImportPerson.TRUE);
					update.where(Where.and(Where.and(
							Expr.equal("deleted", PersonConstants.DELETED_FALSE),
							Expr.equal("fk_import", importId)),
							Expr.in("pk", selection)));
					context.execute(update);
				}
				context.commit();

				cntx.setContent("countUpdate",
						database.getCount(
						database.getCount(sample).where(Where.and(
								Expr.equal("deleted", PersonConstants.DELETED_FALSE),
								Expr.equal("fk_import", importId)))));
			}
			catch(BeanException e)
			{
				// failed to commit
				context.rollBack();
				throw new BeanException("Die Änderungen an der Duplikatliste konnten nicht übernommen werden.", e);
			}
		}
	}

	/**
	 * Bedingung:
	 * Es existieren Duplikate zu dem Datensatz.
	 * Datensatz wurde noch nicht festgeschrieben.
	 * Nur Datens�tze von dem aktuellen Importvorgang.
	 */
	@Override
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

	@Override
    protected void extendColumns(OctopusContext cntx, Select select) throws BeanException {
		Database database = getDatabase(cntx);
		try {
			select.orderBy(Order.asc(database.getProperty(database.createBean(BEANNAME), "lastname_a_e1")));
		} catch (IOException e) {
			throw new BeanException("Fehler beim Lesen von Bean-Parametern", e);
		}
	}
}
