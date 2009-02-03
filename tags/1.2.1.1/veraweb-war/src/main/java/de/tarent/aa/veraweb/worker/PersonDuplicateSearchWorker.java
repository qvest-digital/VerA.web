/**
 * 
 */
package de.tarent.aa.veraweb.worker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.tarent.aa.veraweb.beans.Person;
import de.tarent.aa.veraweb.beans.PersonSearch;
import de.tarent.aa.veraweb.beans.facade.PersonConstants;
import de.tarent.dblayer.sql.Escaper;
import de.tarent.dblayer.sql.SyntaxErrorException;
import de.tarent.dblayer.sql.clause.Expr;
import de.tarent.dblayer.sql.clause.Limit;
import de.tarent.dblayer.sql.clause.Order;
import de.tarent.dblayer.sql.clause.RawClause;
import de.tarent.dblayer.sql.clause.Where;
import de.tarent.dblayer.sql.statement.Select;
import de.tarent.octopus.PersonalConfigAA;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.beans.Database;
import de.tarent.octopus.beans.veraweb.DatabaseVeraWeb;
import de.tarent.octopus.server.OctopusContext;

/**
 * This worker implements change request 2.3 for version 1.2.0.
 * 
 * It enables the user to run a duplicate search over all persons
 * in the database. Duplicates are found by searching for equal
 * firstname and lastname names.
 * 
 * @author cklein
 */
public class PersonDuplicateSearchWorker extends PersonListWorker
{
	public PersonDuplicateSearchWorker()
	{
		super();
	}

	@Override
	public List showList(OctopusContext cntx) throws BeanException, IOException
	{
		// code in part duplicated from PersonListWorker
		Database database = getDatabase(cntx);

		Integer start = getStart(cntx);
		Integer limit = getLimit(cntx);
		Integer count = getCount(cntx, database);
		Map param = getParamMap(start, limit, count);
		cntx.setContent(OUTPUT_showListParams, param);
		cntx.setContent(OUTPUT_getSelection, getSelection(cntx, count));
		cntx.setContent( "action", "duplicateSearch" );

		Select select = getSelect(database);
		this.extendColumns(cntx, select);
		this.extendWhere(cntx, select);
		this.extendLimit( cntx, select );
		select.orderBy( Order.asc( "lastname_a_e1" ).andAsc( "firstname_a_e1" ) );

		return getResultList(database, select);
	}

	@Override
	protected Select getSelect( PersonSearch search, Database database ) throws BeanException, IOException
	{
		Person template = new Person();
		Select result = database.getSelectIds( template );

		// replicated from PersonListWorker.
		return result.
				select("firstname_a_e1").
				select("lastname_a_e1").
				select("firstname_b_e1").
				select("lastname_b_e1").
				select("function_a_e1").
				select("company_a_e1").
				select("street_a_e1").
				select("zipcode_a_e1").
				select("city_a_e1");
	}

	@Override
	protected Integer getAlphaStart(OctopusContext cntx, String start) throws BeanException, IOException
	{
		// code duplicated from PersonListWorker.getAlphaStart()
		Database database = getDatabase(cntx);
		Select select = database.getEmptySelect( new Person() );
		select.select( "COUNT(DISTINCT(tperson.pk))" );
		select.from( "veraweb.tperson person2" );
		select.setDistinct( false );

		this.extendWhere( cntx, select );
		if ( start != null && start.length() > 0 )
		{
			select.whereAnd( Expr.less( "tperson.lastname_a_e1", Escaper.escape( start ) ) );
		}

		Integer i = database.getCount(select);
		return new Integer(i.intValue() - (i.intValue() % getLimit(cntx).intValue()));
	}

	@Override
	public List getSelection(OctopusContext cntx, Integer count) throws BeanException, IOException
	{
		List result = null;

		boolean doSelectAll = cntx.requestAsBoolean( this.INPUT_SELECTALL ).booleanValue();
		if ( doSelectAll )
		{
			// code in part duplicated from BeanListWorker.getSelection()
			result = new ArrayList( count != null ? count.intValue() : 10 );
			Database database = getDatabase( cntx );
			Select select = this.getSelect( database );
			Person template = new Person();
	        select.selectAs( database.getProperty( template, "id" ), "id" );
			this.extendWhere( cntx, select );
			this.extendLimit( cntx, select );
			for ( Iterator it = database.getList( select, database ).iterator(); it.hasNext(); )
			{
				result.add( ( ( Map ) it.next() ).get( "id" ) );
			}
		}
		else
		{
			result = super.getSelection( cntx, count );
		}

		return result;
	}

	@Override
	protected Integer getCount(OctopusContext cntx, Database database) throws BeanException, IOException
	{
		Select select = database.getEmptySelect( new Person() );
		select.select( "COUNT(DISTINCT(tperson.pk))" );
		select.setDistinct( false );
		this.extendSubselect( cntx, database, select );

		return database.getCount( select );
	}

	protected void extendSubselect( OctopusContext cntx, Database database, Select subselect )
	{
		subselect.from( "veraweb.tperson person2" );
//		subselect.from( "veraweb.tperson_cross_product" );
		subselect.whereAnd(
			Where.and(
				Expr.equal( "tperson.fk_orgunit", ( ( PersonalConfigAA ) cntx.personalConfig() ).getOrgUnitId() ),
				Expr.equal( "tperson.deleted", PersonConstants.DELETED_FALSE )
			)
		).whereAnd(
			new RawClause( "tperson.pk!=person2.pk" )
		).whereAnd(
			Where.or(
				Where.or(
					Where.and(
						Where.and(
							new RawClause( "tperson.firstname_a_e1=person2.firstname_a_e1" ),
							new RawClause( "tperson.lastname_a_e1=person2.lastname_a_e1" )
						),
						Where.and(
							new RawClause( "tperson.lastname_a_e1<>''" ),
							new RawClause( "tperson.firstname_a_e1<>''" )
						)
					),
					Where.and(
						Where.and(
							new RawClause( "tperson.firstname_a_e2=person2.firstname_a_e2" ),
							new RawClause( "tperson.lastname_a_e2=person2.lastname_a_e2" )
						),
						Where.and(
							new RawClause( "tperson.lastname_a_e2<>''" ),
							new RawClause( "tperson.firstname_a_e2<>''" )
						)
					)
				),
				Where.and(
					Where.and(
						new RawClause( "tperson.firstname_a_e2=person2.firstname_a_e2" ),
						new RawClause( "tperson.lastname_a_e2=person2.lastname_a_e2" )
					),
					Where.and(
						new RawClause( "tperson.lastname_a_e2<>''" ),
						new RawClause( "tperson.firstname_a_e2<>''" )
					)
				)
			)
		);
	}

	protected void extendLimit( OctopusContext cntx, Select select ) throws BeanException, IOException
	{
		Integer start = this.getStart( cntx );
		Integer limit = this.getLimit( cntx );
		select.Limit( new Limit( limit, start ) );
	}
	
	@Override
	protected void extendWhere( OctopusContext cntx, Select select )
	{
		Database database = new DatabaseVeraWeb( cntx );
		Select subselect = null;
		try
		{
			Person template = new Person();
			subselect = database.getSelectIds( template );
			this.extendSubselect( cntx, database, subselect );
			subselect.setDistinct( false );
			subselect.orderBy( Order.asc( "tperson.lastname_a_e1" ).andAsc( "tperson.firstname_a_e1" ) );
		}
		catch( Exception e )
		{
			;; // just catch, should never happen
		}

		try
		{
			select.whereAnd(
				new RawClause( "tperson.pk IN (" + subselect.statementToString() + ")" )
			);
		}
		catch( SyntaxErrorException e )
		{
			;; // should never happen
		}
	}
}
