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
 * $Id$
 * 
 * Created on 17.08.2005
 */
package de.tarent.aa.veraweb.worker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.tarent.aa.veraweb.beans.OrgUnit;
import de.tarent.aa.veraweb.beans.WorkArea;
import de.tarent.dblayer.sql.clause.Clause;
import de.tarent.dblayer.sql.clause.Expr;
import de.tarent.dblayer.sql.clause.RawClause;
import de.tarent.dblayer.sql.clause.Where;
import de.tarent.dblayer.sql.statement.Delete;
import de.tarent.octopus.beans.Bean;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.beans.Database;
import de.tarent.octopus.beans.TransactionContext;
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
        if (bean.isModified() && bean.isCorrect()) {
            if (bean instanceof OrgUnit) {
                OrgUnit orgunitBean = (OrgUnit) bean;
                if (orgunitBean.id != null) {
                    errors.add("Einzufügender Mandant darf keine ID haben");
                    return count;
                }
                Database database = context.getDatabase();
                OrgUnit dupBean = (OrgUnit) database.getBean("OrgUnit",
                        database.getSelect("OrgUnit").
                        where(Expr.equal("unitname", orgunitBean.name)), context);
                if (dupBean != null) {
                    errors.add("Einzufügender Mandant " + orgunitBean.name + " existiert bereits.");
                    return count;
                }
            }
            saveBean(cntx, bean, context);
            count++;
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
            if (bean.isModified() && bean.isCorrect()) {
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
            } else if (bean.isModified()) {
                errors.addAll(bean.getErrors());
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
		return true;
	}
}
