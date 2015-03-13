/**
 * veraweb, platform independent webservice-based event management
 * (Veranstaltungsmanagment VerA.web), is
 * Copyright © 2004–2008 tarent GmbH
 * Copyright © 2013–2015 tarent solutions GmbH
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
package de.tarent.aa.veraweb.worker;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.tarent.aa.veraweb.beans.Categorie;
import de.tarent.aa.veraweb.beans.OrgUnit;
import de.tarent.aa.veraweb.beans.Person;
import de.tarent.dblayer.sql.clause.Clause;
import de.tarent.dblayer.sql.clause.Expr;
import de.tarent.dblayer.sql.clause.RawClause;
import de.tarent.dblayer.sql.clause.Where;
import de.tarent.dblayer.sql.statement.Delete;
import de.tarent.dblayer.sql.statement.Insert;
import de.tarent.dblayer.sql.statement.Update;
import de.tarent.octopus.PersonalConfigAA;
import de.tarent.octopus.beans.Bean;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.beans.Database;
import de.tarent.octopus.beans.TransactionContext;
import de.tarent.octopus.beans.veraweb.BeanChangeLogger;
import de.tarent.octopus.beans.veraweb.DatabaseVeraWeb;
import de.tarent.octopus.beans.veraweb.ListWorkerVeraWeb;
import de.tarent.octopus.server.OctopusContext;

/**
 * Diese Octopus-Worker-Klasse stellt Operationen zur Anzeige
 * von Mandantenlisten zur Verf�gung. Details bitte dem
 * {@link de.tarent.octopus.beans.veraweb.ListWorkerVeraWeb}
 * entnehmen.
 *
 * @author mikel
 */
public class OrgUnitListWorker extends ListWorkerVeraWeb {
    //
    // Konstruktoren
    //
    /**
     * Dieser Konstruktor bestimmt den Basis-Bean-Typ des Workers.
     */
    public OrgUnitListWorker() {
        super("OrgUnit");
    }

    //
    // BeanListWorker
    //
    /**
     * Wird von {@link de.tarent.octopus.beans.BeanListWorker#saveList(OctopusContext)}
     * aufgerufen und soll das �bergebene Bean als neuen Eintrag speichern.
     * 
     * 2015-03-13 - We have one Press category for every Mandant.
     * 
     * @see #saveBean(OctopusContext, Bean)
     *
     * @param cntx Octopus-Kontext
     * @param errors kummulierte Fehlerliste
     * @param bean einzuf�gendes Bean
     * @throws BeanException
     * @throws IOException
     */
    @Override
    protected int insertBean(OctopusContext cntx, List errors, Bean bean, TransactionContext context) throws BeanException, IOException {
    	int count = 0;
        if (bean.isModified()) {
            if (bean.isCorrect()) {
                if (bean instanceof OrgUnit) {
                    OrgUnit orgunitBean = (OrgUnit) bean;
                    if (orgunitBean.id != null) {
                        errors.add("Einzuf\u00fcgender Mandant darf keine ID haben");
                        return count;
                    }
                    Database database = context.getDatabase();
                    OrgUnit dupBean = (OrgUnit) database.getBean("OrgUnit",
                            database.getSelect("OrgUnit").
                            where(Expr.equal("unitname", orgunitBean.name)), context);
                    if (dupBean != null) {
                        errors.add("Einzuf\u00fcgender Mandant '" + orgunitBean.name + "' existiert bereits.");
                        return count;
                    }
                }
                saveBean(cntx, bean, context);
                // Insert new Category Pressevertreter associated to every new Mandants
                initPressCategory(cntx,Integer.valueOf(bean.getField("id").toString()),context);
                count++;
            } else {
                errors.addAll(bean.getErrors());
            }
        }
        return count;
    }

    /**
     * Wird von {@link de.tarent.octopus.beans.BeanListWorker#saveList(OctopusContext)}
     * aufgerufen und soll die �bergebene Liste von Beans aktualisieren.
     *
     * @see #saveBean(OctopusContext, Bean)
     *
     * @param cntx Octopus-Kontext
     * @param errors kummulierte Fehlerliste
     * @param beanlist Liste von zu aktualisierenden Beans
     * @throws BeanException
     * @throws IOException
     */
    @Override
    protected int updateBeanList(OctopusContext cntx, List errors, List beanlist, TransactionContext context) throws BeanException, IOException {
    	int count = 0;
        for (Iterator it = beanlist.iterator(); it.hasNext(); ) {
            Bean bean = (Bean)it.next();
            if (bean.isModified()) {
                if (bean.isCorrect()) {
                    if (bean instanceof OrgUnit) {
                        OrgUnit orgunitBean = (OrgUnit) bean;
                        if (orgunitBean.id == null) {
                            errors.add("Zu aktualisierender Mandant " + orgunitBean.name + " muss eine ID haben");
                            continue;
                        }
                        Database database = context.getDatabase();
                        OrgUnit dupBean = (OrgUnit) database.getBean("OrgUnit",
                                database.getSelect("OrgUnit").
                                where(Where.and(
                                        Expr.equal("unitname", orgunitBean.name),
                                        Expr.notEqual("pk", orgunitBean.id))), context);
                        if (dupBean != null) {
                            errors.add("Ein Mandant mit Namen " + orgunitBean.name + " existiert bereits.");
                            continue;
                        }
                    }
                    saveBean(cntx, bean, context);
                    count++;
                } else {
                    errors.addAll(bean.getErrors());
                }
            }
        }
        return count;
    }


	//
	// weitere Octopus-Aktionen
	//

	/** Octopus-Eingabe-Parameter f�r {@link #cleanupDatabase(OctopusContext, Integer)} */
	public static final String INPUT_cleanupDatabase[] = { "orgunit" };
	/** Octopus-Eingabe-Parameter f�r {@link #cleanupDatabase(OctopusContext, Integer)} */
	public static final boolean MANDATORY_cleanupDatabase[] = { false };
	/** Octopus-Ausgabe-Parameter f�r {@link #cleanupDatabase(OctopusContext, Integer)} */
	public static final String OUTPUT_cleanupDatabase = "missingorgunit";
	/**
	 * Zeigt eine Statistik �ber 'verloren' gegangene Datens�tze an.
	 * Wenn der Parameter <code>orgunit</code> �bergeben wird werden
	 * alle Datens�tze ohne g�ltigen Mandanten diesem zugeordnet.
	 *
	 * @param cntx Octopus-Context-Instanz
	 * @param orgunit Neue Orgunit-ID
	 * @throws BeanException
	 * @throws IOException
	 */
	public Map cleanupDatabase(OctopusContext cntx, Integer orgunit) throws BeanException, IOException {
		Database database = getDatabase(cntx);
		Clause where = new RawClause("fk_orgunit IS NULL OR " +
				"fk_orgunit NOT IN (SELECT pk FROM veraweb.torgunit)");

		Map missingorgunit = new HashMap();

		if (orgunit != null) {
			database.execute(database.getUpdate("Person").
					update("fk_orgunit", orgunit).where(where));
			database.execute(database.getUpdate("Event").
					update("fk_orgunit", orgunit).where(where));
			database.execute(database.getUpdate("Import").
					update("fk_orgunit", orgunit).where(where));
			database.execute(database.getUpdate("ImportPerson").
					update("fk_orgunit", orgunit).where(where));
			database.execute(database.getUpdate("Location").
					update("fk_orgunit", orgunit).where(where));
			database.execute(database.getUpdate("Mailinglist").
					update("fk_orgunit", orgunit).where(where));
			database.execute(database.getUpdate("Categorie").
					update("fk_orgunit", orgunit).where(where));
			database.execute(database.getUpdate("User").
					update("fk_orgunit", orgunit).where(where));

			missingorgunit.put("result", "done");
		}

		missingorgunit.put("person", database.getCount(
				database.getCount("Person").where(where)));
		missingorgunit.put("event", database.getCount(
				database.getCount("Event").where(where)));
		missingorgunit.put("import", database.getCount(
				database.getCount("Import").where(where)));
		missingorgunit.put("importperson", database.getCount(
				database.getCount("ImportPerson").where(where)));
		missingorgunit.put("location", database.getCount(
				database.getCount("Location").where(where)));
		missingorgunit.put("mailinglist", database.getCount(
				database.getCount("Mailinglist").where(where)));
		missingorgunit.put("category", database.getCount(
				database.getCount("Categorie").where(where)));
		missingorgunit.put("user", database.getCount(
				database.getCount("User").where(where)));

		return missingorgunit;
	}

	/*
	 * 2009-05-12 cklein
	 *
	 * fixed as part of issue #1530 - deletion of orgunits and cascaded deletion of both workareas and person to workarea assignments
	 * 									note that in expectance of a major overhaul of the way that workareas are handled, the sql datamodel
	 * 									will not be changed now.
	 * 2015-03-13 - We have one Press category for every Mandant. That will be deleted when we want to delete one of these mandants 
	 */
	@Override
    protected boolean removeBean(OctopusContext cntx, Bean bean, TransactionContext context) throws BeanException, IOException
	{
		Database database = context.getDatabase();

		// first remove all workArea assignments from all persons
		WorkAreaWorker.removeAllWorkAreasFromOrgUnit( cntx, context, ( ( OrgUnit ) bean ).id );
		Delete stmt = database.getDelete( "OrgUnit" );
		stmt.byId( "pk",  ( ( OrgUnit ) bean ).id  );
		context.execute( stmt );
		
		// Remove category pressevertreter of the current mandant
		deletePressCategoryByOrgUnit(cntx,context,((OrgUnit)bean).id);
		
		return true;
	}
	
	/**
	 * Delete press category linked to the deleted mandant
	 * 
	 * @param cntx OctopusContext
	 * @param context TransactionContext
	 * @param orgUnitId Integer
	 * @throws BeanException
	 * @throws IOException
	 */
	private void deletePressCategoryByOrgUnit(OctopusContext cntx, TransactionContext context, Integer orgUnitId) throws BeanException, IOException {
		
		Database database = context.getDatabase();
		
		Delete delete = database.getDelete("Categorie");
		delete.where(Where.and(Expr.equal("fk_orgunit", orgUnitId),Expr.equal("catname", "Pressevertreter")));
		
		context.execute(delete);
	}
	/**
	 * Creating presse category to every new Mandants
	 * @param cntx OctopusContext
	 * @param orgUnitId Integer
	 * @param context TransactionContext
	 * @throws BeanException
	 * @throws IOException 
	 */
	private void initPressCategory(OctopusContext cntx, Integer orgUnitId, TransactionContext context) throws BeanException, IOException {
		// Implementieren
		Database database = new DatabaseVeraWeb(cntx);
		Categorie category = new Categorie();
		category.name = "Pressevertreter";
		category.flags = 0;
		category.orgunit = orgUnitId;
		
		Insert insert = database.getInsert(category);
		
		context.execute(insert);
	}

	public void updatePerson(OctopusContext cntx, Person person, Integer personId) throws BeanException, IOException {
		Database database = new DatabaseVeraWeb(cntx);

		if (person == null) {
			person = (Person)cntx.contentAsObject("person"); // ???
		}
		if (person == null || !person.id.equals(personId)) {
			person = (Person)database.getBean("Person", personId);
		}
		if (person != null && person.id != null) {
			person.updateHistoryFields(null, ((PersonalConfigAA)cntx.personalConfig()).getRoleWithProxy());
			Update update = database.getUpdate("Person");
			update.update(database.getProperty(person, "created"), person.created);
			update.update(database.getProperty(person, "createdby"), person.createdby);
			update.update(database.getProperty(person, "changed"), person.changed);
			update.update(database.getProperty(person, "changedby"), person.changedby);
			update.where(Expr.equal(database.getProperty(person, "id"), person.id));
			database.execute(update);

			// get the original version of the object for logging purposes
			Person personOld = ( Person ) database.getBean( "Person", personId );
			BeanChangeLogger clogger = new BeanChangeLogger( database );
			clogger.logUpdate( cntx.personalConfig().getLoginname(), personOld, person );
		}
	}

	
	
}
