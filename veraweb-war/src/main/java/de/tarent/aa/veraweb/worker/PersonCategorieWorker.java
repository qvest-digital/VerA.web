/**
 * veraweb, platform independent webservice-based event management
 * (Veranstaltungsmanagment VerA.web), is
 * Copyright © 2004-2008 tarent GmbH
 * Copyright © 2013 tarent solutions GmbH
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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import de.tarent.aa.veraweb.beans.Categorie;
import de.tarent.aa.veraweb.beans.PersonCategorie;
import de.tarent.dblayer.engine.DB;
import de.tarent.dblayer.sql.SQL;
import de.tarent.dblayer.sql.clause.Expr;
import de.tarent.dblayer.sql.statement.Select;
import de.tarent.octopus.beans.Bean;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.beans.Database;
import de.tarent.octopus.beans.TransactionContext;
import de.tarent.octopus.beans.veraweb.ListWorkerVeraWeb;
import de.tarent.octopus.server.OctopusContext;

/**
 * Dieser Octopus-Worker bearbeitet Personen-Kategorien-Listen.
 */
public class PersonCategorieWorker extends ListWorkerVeraWeb {
    //
    // Konstruktoren
    //
    /**
     * Der Konstruktor legt den Bean-Namen fest.
     */
	public PersonCategorieWorker() {
		super("PersonCategorie");
	}

    //
    // Oberklasse BeanListWorker
    //
	@Override
    protected void extendWhere(OctopusContext cntx, Select select) throws BeanException, IOException {
		Bean bean = (Bean)cntx.contentAsObject("person");
		select.where(Expr.equal("fk_person", bean.getField("id")));
	}

	@Override
    protected void extendColumns(OctopusContext cntx, Select select) throws BeanException, IOException {
		select.join("veraweb.tcategorie", "fk_categorie", "tcategorie.pk");
		select.selectAs("tcategorie.rank", "catrank");
		select.selectAs("tcategorie.catname", "name");
		select.selectAs("tcategorie.flags", "flags");
	}

	@Override
    public void saveList(OctopusContext cntx) throws BeanException, IOException {
		super.saveList(cntx);
		
		String addRank = cntx.requestAsString("add-rank");
		String addCategorie = cntx.requestAsString("add-categorie");
		if (addRank != null && addRank.length() != 0 && (addCategorie == null || addCategorie.length() == 0)) {
			List errors = (List)cntx.contentAsObject(OUTPUT_saveListErrors);
			if (errors == null) {
				errors = new ArrayList();
				cntx.setContent(OUTPUT_saveListErrors, errors);
			}
			errors.add("Um eine neue Kategorie hinzuzufügen wählen" +
					" Sie bitte eine Kategorie aus. " +
					"(Sie haben nur einen Rang eingegeben.)");
		}
	}

	@Override
    protected void saveBean(OctopusContext cntx, Bean bean, TransactionContext context) throws BeanException, IOException {
		super.saveBean(cntx, bean, context);
		WorkerFactory.getPersonDetailWorker(cntx).updatePerson(cntx, null, ((PersonCategorie)bean).person);
	}

	public void addCategoryAssignment( OctopusContext cntx, Integer categoryId, Integer personId, Database database, TransactionContext context ) throws BeanException, IOException
	{
		addCategoryAssignment(cntx, categoryId, personId, database, context, true);
	}
	
	public PersonCategorie addCategoryAssignment( OctopusContext cntx, Integer categoryId, Integer personId, Database database, TransactionContext context, boolean save ) throws BeanException, IOException
	{
		Categorie category = ( Categorie ) database.getBean( "Categorie", categoryId, context == null ? database : context);
		if ( category != null )
		{
			Select select = database.getCount( "PersonCategorie" );
			select.where( Expr.equal( "fk_person", personId ) );
			select.whereAnd( Expr.equal( "fk_categorie", categoryId ) );
			Integer count = database.getCount( select );
			if ( count.intValue() == 0 )
			{
				PersonCategorie personCategory = ( PersonCategorie ) database.createBean( "PersonCategorie" );
				personCategory.categorie = categoryId;
				personCategory.person = personId;
				personCategory.rank = category.rank;
				if(save)
					this.saveBean( cntx, personCategory, context );
				return personCategory;
			}
		}
		return null;
	}

	public void removeAllCategoryAssignments( OctopusContext cntx, Integer personId, Database database, TransactionContext context ) throws BeanException, IOException
	{
		try
		{
			context.execute( 
				SQL.Delete( database ).from( "veraweb.tperson_categorie" ).where( Expr.equal( "fk_person", personId ) )
			);
		}
		catch( BeanException e )
		{
			throw new BeanException( "Die Kategoriezuweisungen konnte nicht aufgehoben werden.", e );
		}
	}

	public void removeCategoryAssignment( OctopusContext cntx, Integer categoryId, Integer personId, Database database, TransactionContext context ) throws BeanException, IOException
	{
		Categorie category = ( Categorie ) database.getBean( "Categorie", categoryId, context == null ? database : context );
		if ( category != null )
		{
			Select select = database.getSelect( "PersonCategorie" );
			select.where( Expr.equal( "fk_person", personId ) );
			select.whereAnd( Expr.equal( "fk_categorie", categoryId ) );
			PersonCategorie personCategory = ( PersonCategorie ) database.getBean( "PersonCategorie", select, context );
			if ( personCategory != null )
			{
				database.removeBean( personCategory, context );
			}
		}
	}
}
