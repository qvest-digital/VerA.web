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
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.tarent.aa.veraweb.beans.ChangeLogEntry;
import de.tarent.aa.veraweb.beans.ChangeLogReport;
import de.tarent.dblayer.sql.clause.Clause;
import de.tarent.dblayer.sql.clause.Expr;
import de.tarent.dblayer.sql.clause.Order;
import de.tarent.dblayer.sql.statement.Select;
import de.tarent.octopus.beans.Bean;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.beans.BeanFactory;
import de.tarent.octopus.beans.Database;
import de.tarent.octopus.beans.TransactionContext;
import de.tarent.octopus.beans.veraweb.ListWorkerVeraWeb;
import de.tarent.octopus.server.OctopusContext;

/**
 * The class ChangeLogReportsWorker is a concrete
 * worker for operations on the {@link ChangeLogReport}
 * bean in collaboration with the {@link ChangeLogEntry}
 * entity bean.
 * 
 * @author cklein
 * @since 1.2.0
 * @see de.tarent.aa.veraweb.beans.ChangeLogReport
 * @see de.tarent.octopus.customs.beans.ChangeLogEntry
 */
public class ChangeLogReportsWorker extends ListWorkerVeraWeb
{
	/**
	 * Constructs a new instance of this.
	 */
	public ChangeLogReportsWorker()
	{
		super( "ChangeLogEntry" );
	}

	/**
	 * Input parameters for action {@link #loadConfig( OctopusContext, String, String ) }
	 */
	public static final String INPUT_loadConfig[] = { "begin", "end" };
	/**
	 * Input parameter configuration for action {@link #loadReport( OctopusContext, String, String ) }
	 */
	public static final boolean MANDATORY_loadConfig[] = { false, false };

	/**
	 * Stores and loads the report configuration in / from the session.
	 * 
	 * @param cntx
	 * @param begin
	 * @param end
	 * @throws BeanException 
	 * @throws IOException 
	 */
	@SuppressWarnings( "unchecked" )
	public void loadConfig( OctopusContext cntx, String begin, String end ) throws BeanException, IOException
	{
		Map< String, Object > map = ( Map< String, Object > ) cntx.sessionAsObject( "changeLogReportSettings" );
		if ( map == null )
		{
			map = new HashMap< String, Object >();
			cntx.setSession( "changeLogReportSettings", map );
		}

		// fetch begin and end dates or sane defaults
		Date bd = ( Date ) BeanFactory.transform( begin, Date.class );
		if ( bd == null )
		{
			bd = ( Date ) map.get( "begin" );
			if ( bd == null )
			{
				bd = ( Date ) BeanFactory.transform( "01.01." + Calendar.getInstance().get( Calendar.YEAR ), Date.class );
			}
		}

		Date ed = ( Date ) BeanFactory.transform( end, Date.class );
		if ( ed == null )
		{
			ed = ( Date ) map.get( "end" );
			if ( ed == null )
			{
				ed = new Date( System.currentTimeMillis() );
			}
		}

		// make sure that end is always after begin
		if ( ed.after( bd ) )
		{
			map.put( "begin", bd );
			map.put( "end", ed );
		}
		else
		{
			map.put( "begin", ed );
			map.put( "end", bd );
		}

		DateFormat format = DateFormat.getDateInstance( DateFormat.DEFAULT );
		cntx.setContent( "begin", format.format( map.get( "begin" ) ) );
		cntx.setContent( "end", format.format( map.get( "end" ) ) );

		Database database = getDatabase(cntx);
		Integer count = getCount( cntx, database );
		if ( count.intValue() == 0 )
		{
			cntx.setContent( "noLogDataAvailableMessage", "Es stehen keine Protokolldaten zur Verfügung." );
		}
	}

	@Override
    public List showList(OctopusContext cntx) throws BeanException, IOException {
		Database database = getDatabase(cntx);
		Select select = getSelect( database );
		extendColumns( cntx, select );
		extendWhere( cntx, select );
		return getResultList( database, select );
	}

	@Override
    @SuppressWarnings("unchecked")
	protected void extendWhere( OctopusContext cntx, Select select )
		throws BeanException, IOException
	{
		Map< String, Object > map = ( Map< String, Object > ) cntx.sessionAsObject( "changeLogReportSettings" );
		Date begin = ( Date ) map.get( "begin" );
		Date end = ( Date ) map.get( "end" );
		Calendar calendar = Calendar.getInstance();
		// end date must point to day.month.year 23:59:59 in order to find todays change log entries
		calendar.setTime( end );
		calendar.add( Calendar.DAY_OF_MONTH, 1 );
		calendar.add( Calendar.SECOND, -1 );
		select.where( Expr.greaterOrEqual( "date", begin ) );
		select.whereAnd( Expr.lessOrEqual( "date", calendar.getTime() ) );
	}

	@Override
    protected void extendColumns( OctopusContext cntx, Select select )
		throws BeanException, IOException
	{
		if ( cntx.requestContains( "order" ) )
		{
			String order = cntx.requestAsString( "order" );
			if ( "name".equals( order ) )
			{
				select.orderBy( Order.asc( order ) );
				cntx.setContent( "order", order );
			}
			else if ( "flags".equals( order ) )
			{
				select.orderBy( Order.asc( order ).andAsc( "name" ) );
				cntx.setContent( "order", order );
			}
		}
	}

	@Override
    protected void extendAll( OctopusContext cntx, Select select )
		throws BeanException, IOException
	{
		Clause clause = getWhere( cntx );
		if ( clause != null )
		{
			select.where( clause );
		}
	}

	protected Clause getWhere( OctopusContext cntx )
		throws BeanException
	{
		Clause clause = null;
		return clause;
	}

	@Override
    protected void saveBean( OctopusContext cntx, Bean bean, TransactionContext context ) throws BeanException, IOException
	{
		throw new RuntimeException( "Change log entries cannot be modified. Not implemented." );
	}
}
