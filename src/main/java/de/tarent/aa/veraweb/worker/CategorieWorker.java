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
 * Created on 10.03.2005
 */
package de.tarent.aa.veraweb.worker;

import java.io.IOException;
import java.util.List;

import de.tarent.aa.veraweb.beans.Categorie;
import de.tarent.aa.veraweb.beans.Event;
import de.tarent.aa.veraweb.beans.Person;
import de.tarent.dblayer.sql.SQL;
import de.tarent.dblayer.sql.clause.Clause;
import de.tarent.dblayer.sql.clause.Expr;
import de.tarent.dblayer.sql.clause.Order;
import de.tarent.dblayer.sql.clause.RawClause;
import de.tarent.dblayer.sql.clause.Where;
import de.tarent.dblayer.sql.statement.Select;
import de.tarent.octopus.PersonalConfigAA;
import de.tarent.octopus.config.TcPersonalConfig;
import de.tarent.octopus.custom.beans.Bean;
import de.tarent.octopus.custom.beans.BeanException;
import de.tarent.octopus.custom.beans.Database;
import de.tarent.octopus.server.OctopusContext;

/**
 * <p>
 * Diese Octopus-Worker-Klasse stellt Operationen für Dokumenttypen
 * zur Verfügung. Details bitte dem BeanListWorker entnehmen.
 * </p>
 * <p>
 * Wenn eine <em>person</em> im Octopus-Content steht, wird bei der
 * <em>getAll</em>-Aktion die Ergebnis-Liste auf die Kategorien
 * eingeschränkt die NICHT dieser Person zugeordnet sind.
 * </p>
 * <p>
 * Wenn ein <em>event</em> im Octopus-Content steht, wird bei der
 * <em>getAll</em>-Aktion die Ergebnis-Liste auf die Kategorien
 * eingeschränkt die entweder ALLEN oder DIESEM Event zugeordnet sind.
 * </p>
 * 
 * @see de.tarent.octopus.custom.beans.BeanListWorker
 * 
 * @author Christoph
 */
public class CategorieWorker extends StammdatenWorker {
    //
    // öffentliche Konstanten
    //
    /** Parameter: Wessen Kategorien? */
    public final static String PARAM_DOMAIN = "domain";
    
    /** Parameterwert: beliebige Kategorien */
    public final static String PARAM_DOMAIN_VALUE_ALL = "all";
    /** Parameterwert: Kategorien des gleichen Mandanten */
    public final static String PARAM_DOMAIN_VALUE_OU = "ou";

    //
    // Konstruktoren
    //
    /**
     * Der Konstruktor legt den Bean-Namen fest.
     */
    public CategorieWorker() {
		super("Categorie");
	}

    //
    // Basisklasse BeanListWorker
    //
	protected void extendWhere(OctopusContext cntx, Select select) throws BeanException, IOException {
		Clause orgUnitTest = getWhere(cntx);
		if (orgUnitTest != null)
			select.where(orgUnitTest);
	}

	protected void extendColumns(OctopusContext cntx, Select select) throws BeanException, IOException {
		if (cntx.requestContains("order")) {
			String order = cntx.requestAsString("order");
			if ("name".equals(order)) {
				select.orderBy(Order.asc(order));
				cntx.setContent("order", order);
			} else if ("flags".equals(order)) {
				select.orderBy(Order.asc(order).andAsc("name"));
				cntx.setContent("order", order);
			}
		}
	}

	protected void extendAll(OctopusContext cntx, Select select) throws BeanException, IOException {
		Clause clause = null;
		Person person = (Person)cntx.contentAsObject("person");
		if (person != null && person.id != null) {
			clause = Where.and(
					Expr.isNull("fk_event"),
					new RawClause("pk NOT IN (SELECT fk_categorie FROM veraweb.tperson_categorie WHERE fk_person = " + person.id + ")"));
		}
		Event event = (Event)cntx.contentAsObject("event");
		if (event != null && event.id != null) {
			clause = Where.or(
					Expr.isNull("fk_event"),
					Expr.equal("fk_event", event.id));
		}
        
		Clause orgUnitTest = getWhere(cntx);
        if (orgUnitTest != null)
        	clause = clause == null ? orgUnitTest : Where.and(orgUnitTest, clause);
        
        if (clause != null)
            select.where(clause);
	}

	protected Clause getWhere(OctopusContext cntx) throws BeanException {
        TcPersonalConfig pConfig = cntx.personalConfig();
        if (pConfig instanceof PersonalConfigAA) {
            PersonalConfigAA aaConfig = (PersonalConfigAA) pConfig;
            String domain = cntx.contentAsString(PARAM_DOMAIN);
            if (!(PARAM_DOMAIN_VALUE_ALL.equals(domain) && pConfig.isUserInGroup(PersonalConfigAA.GROUP_ADMIN))) {
        		Integer orgunit = ((PersonalConfigAA)(cntx.personalConfig())).getOrgUnitId();
            	if (orgunit == null)
            		return Expr.isNull("tcategorie.fk_orgunit");
            	else
            		return Expr.equal("tcategorie.fk_orgunit", aaConfig.getOrgUnitId());
            }
            return null;
        } else {
            throw new BeanException("Missing user information");
        }
	}

	protected int insertBean(OctopusContext cntx, List errors, Bean bean) throws BeanException, IOException
	{
		int count = 0;
		if (bean.isModified() && bean.isCorrect())
		{
			Database database = getDatabase(cntx);

			Clause sameOrgunit = getWhere(cntx);
			Clause sameName = Expr.equal(database.getProperty(bean, "name"), bean.getField("name"));
			Clause sameCategorie = sameOrgunit == null ? sameName : Where.and(sameOrgunit, sameName);

			Integer exist = database.getCount(database.getCount(bean).where(sameCategorie));

			if (exist.intValue() != 0)
			{
				errors.add("Es existiert bereits ein Stammdaten-Eintrag unter dem Namen '" + bean.getField("name") + "'.");
			} else
			{
				saveBean(cntx, bean);
				count++;
			}
		} else if (bean.isModified() && !bean.isCorrect())
		{

			errors.addAll(bean.getErrors());

		}
		return count;
	}

	/**
	 * Die Kategorie bean innerhalb der bestehenden Kategorieen einsortieren. Dazu werden alle bestehenden Kategorien mit
	 * Rang >= bean.rank in ihrem Rang um eins erhoeht.
	 * 
	 * @param cntx
	 * @param bean
	 * @throws BeanException
	 */
	protected void incorporateBean(OctopusContext cntx, Categorie bean) throws BeanException
	{
		assert bean != null; 
		assert cntx != null;
		
		if (bean.rank != null)
		{
			Database database = getDatabase(cntx);
			database.execute(SQL.Update().
				table("veraweb.tcategorie").
				update("rank", new RawClause("rank + 1")).
				where(Expr.greaterOrEqual("rank", bean.rank)));
		}
	}
	
	
	protected void saveBean(OctopusContext cntx, Bean bean) throws BeanException, IOException
	{
		((Categorie) bean).orgunit = ((PersonalConfigAA) (cntx.personalConfig())).getOrgUnitId();
		if (bean.isModified() && bean.isCorrect())
		{
			if (((Categorie) bean).rank != null && cntx.requestAsBoolean("resort").booleanValue())
			{
				incorporateBean(cntx, (Categorie) bean);
			}
		}
		super.saveBean(cntx, bean);
	}

	protected int removeSelection(OctopusContext cntx, List errors, List selection) throws BeanException, IOException {
		int count = super.removeSelection(cntx, errors, selection);
		
		getDatabase(cntx).execute(
				SQL.Delete().
				from("veraweb.tperson_categorie").
				where(new RawClause("fk_categorie NOT IN (" +
				"SELECT pk FROM veraweb.tcategorie)")));
		
		return count;
	}
}
