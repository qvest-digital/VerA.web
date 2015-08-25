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
import de.tarent.aa.veraweb.utils.i18n.LanguageProvider;
import de.tarent.aa.veraweb.utils.i18n.LanguageProviderHelper;
import de.tarent.dblayer.sql.clause.Clause;
import de.tarent.dblayer.sql.clause.Expr;
import de.tarent.dblayer.sql.clause.RawClause;
import de.tarent.dblayer.sql.clause.Where;
import de.tarent.dblayer.sql.statement.Delete;
import de.tarent.dblayer.sql.statement.Insert;
import de.tarent.octopus.beans.Bean;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.beans.Database;
import de.tarent.octopus.beans.TransactionContext;
import de.tarent.octopus.beans.veraweb.DatabaseVeraWeb;
import de.tarent.octopus.beans.veraweb.ListWorkerVeraWeb;
import de.tarent.octopus.server.OctopusContext;

/**
 * Diese Octopus-Worker-Klasse stellt Operationen zur Anzeige
 * von Mandantenlisten zur Verfügung. Details bitte dem
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
     * aufgerufen und soll das übergebene Bean als neuen Eintrag speichern.
     *
     * 2015-03-13 - We have one Press category for every Mandant.
     *
     * @see #saveBean(OctopusContext, Bean)
     *
     * @param cntx Octopus-Kontext
     * @param errors kummulierte Fehlerliste
     * @param bean einzufügendes Bean
     * @throws BeanException
     * @throws IOException
     */
    @Override
    protected int insertBean(OctopusContext cntx, List errors, Bean bean, TransactionContext context) throws BeanException, IOException {

    	int count = 0;
        if (bean.isModified()) {
            if (bean.isCorrect()) {
                if (bean instanceof OrgUnit) {
					LanguageProviderHelper languageProviderHelper = new LanguageProviderHelper();
					LanguageProvider languageProvider = languageProviderHelper.enableTranslation(cntx);
                    OrgUnit orgunitBean = (OrgUnit) bean;
                    if (orgunitBean.id != null) {
                        errors.add(languageProvider.getProperty("ORG_UNIT_NO_MANDANT_ID"));
                        return count;
                    }
                    Database database = context.getDatabase();
                    OrgUnit dupBean = (OrgUnit) database.getBean("OrgUnit",
                            database.getSelect("OrgUnit").
                            where(Expr.equal("unitname", orgunitBean.name)), context);
                    if (dupBean != null) {
                        errors.add(languageProvider.getProperty("ORG_UNIT_NO_MANDANT_ALREADY_EXISTS_ONE") +
								   orgunitBean.name +
								   languageProvider.getProperty("ORG_UNIT_NO_MANDANT_ALREADY_EXISTS_TWO"));
                        return count;
                    }
                }
                saveBean(cntx, bean, context);
                count++;
            } else {
                errors.addAll(bean.getErrors());
            }
        }
        return count;
    }

    /**
     * Wird von {@link de.tarent.octopus.beans.BeanListWorker#saveList(OctopusContext)}
     * aufgerufen und soll die übergebene Liste von Beans aktualisieren.
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
    protected int updateBeanList(OctopusContext cntx, List errors, List beanlist, TransactionContext context)
			throws BeanException, IOException {
    	int count = 0;
        for (Iterator it = beanlist.iterator(); it.hasNext(); ) {
            Bean bean = (Bean)it.next();
            if (bean.isModified()) {
                if (bean.isCorrect()) {
                    if (bean instanceof OrgUnit) {
                        OrgUnit orgunitBean = (OrgUnit) bean;

						LanguageProviderHelper languageProviderHelper = new LanguageProviderHelper();
						LanguageProvider languageProvider = languageProviderHelper.enableTranslation(cntx);

                        if (orgunitBean.id == null) {
                            errors.add(languageProvider.getProperty("ORG_UNIT_MANDANT_ID_COMPULSORY_ONE") +
									   orgunitBean.name +
									   languageProvider.getProperty("ORG_UNIT_MANDANT_ID_COMPULSORY_TWO"));
                            continue;
                        }
                        Database database = context.getDatabase();
                        OrgUnit dupBean = (OrgUnit) database.getBean("OrgUnit",
                                database.getSelect("OrgUnit").
                                where(Where.and(
                                        Expr.equal("unitname", orgunitBean.name),
                                        Expr.notEqual("pk", orgunitBean.id))), context);
                        if (dupBean != null) {
                            errors.add(languageProvider.getProperty("ORG_UNIT_MANDANT_EXISTS_ONE") +
									   orgunitBean.name +
									   languageProvider.getProperty("ORG_UNIT_MANDANT_EXISTS_TWO"));
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

	/** Octopus-Eingabe-Parameter für {@link #cleanupDatabase(OctopusContext, Integer)} */
	public static final String INPUT_cleanupDatabase[] = { "orgunit" };
	/** Octopus-Eingabe-Parameter für {@link #cleanupDatabase(OctopusContext, Integer)} */
	public static final boolean MANDATORY_cleanupDatabase[] = { false };
	/** Octopus-Ausgabe-Parameter für {@link #cleanupDatabase(OctopusContext, Integer)} */
	public static final String OUTPUT_cleanupDatabase = "missingorgunit";
	/**
	 * Zeigt eine Statistik über 'verloren' gegangene Datensätze an.
	 * Wenn der Parameter <code>orgunit</code> übergeben wird werden
	 * alle Datensätze ohne gültigen Mandanten diesem zugeordnet.
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
		final Delete deleteStatement = database.getDelete( "OrgUnit" );
		deleteStatement.byId("pk", ((OrgUnit) bean).id);
		context.execute( deleteStatement );

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

		final Database database = context.getDatabase();

		final Delete deleteStatement = database.getDelete("Categorie");
		deleteStatement.where(Where.and(Expr.equal("fk_orgunit", orgUnitId), Expr.equal("catname", "Pressevertreter")));

		context.execute(deleteStatement);
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
		final Database database = new DatabaseVeraWeb(cntx);
		final Categorie category = new Categorie();
		category.name = "Pressevertreter";
		category.flags = 0;
		category.orgunit = orgUnitId;

		final Insert insertStatement = database.getInsert(category);

		context.execute(insertStatement);
	}

}
