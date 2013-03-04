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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.tarent.aa.veraweb.beans.Person;
import de.tarent.aa.veraweb.beans.PersonSearch;
import de.tarent.aa.veraweb.beans.facade.PersonConstants;
import de.tarent.dblayer.helper.ResultMap;
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
		
		/* FIXME remove this temporary fix ASAP
		 * cklein 2009-09-17
		 * Temporary workaround for NPE Exception in Conjunction with temporary Connection Pooling Fix in tarent-database
		 * Somehow the resultlist returned by getResultList or its underlying ResultSet will be NULL when entering the view
		 * although, upon exiting this method the first time that it is called, will return the correct resultlist with at most
		 * 10 entries in the underlying resultset as is defined by the query.
		 */
		ArrayList< Map > result = new ArrayList< Map >();
		List resultList = getResultList( database, select );
		for ( int i = 0; i < resultList.size(); i++ )
		{
			HashMap< String, Object > tmp = new HashMap< String, Object >();
			Set< String > keys = ( ( ResultMap ) resultList.get( i ) ).keySet();
			for ( String key : keys )
			{
				tmp.put( key, ( ( ResultMap ) resultList.get( i ) ).get( key ) );
			}
			result.add( ( Map ) tmp );
		}
		return result;
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
		subselect.whereAnd(
			Where.and(
				Expr.equal( "tperson.fk_orgunit", ( ( PersonalConfigAA ) cntx.personalConfig() ).getOrgUnitId() ),
				Expr.equal( "tperson.deleted", PersonConstants.DELETED_FALSE )
			)
		).whereAnd(
				Expr.equal( "person2.deleted", PersonConstants.DELETED_FALSE )
		).whereAnd(
			Where.and(
					new RawClause( "tperson.pk!=person2.pk" ),
					new RawClause( "tperson.fk_orgunit=person2.fk_orgunit" )
			)
		).whereAnd(
				Where.and(
						new RawClause( "tperson.firstname_a_e1=person2.firstname_a_e1" ),
						new RawClause( "tperson.lastname_a_e1=person2.lastname_a_e1" )
				)
		).whereAnd(
				Where.and(
						new RawClause( "tperson.lastname_a_e1<>''" ),
						new RawClause( "tperson.firstname_a_e1<>''" )
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
