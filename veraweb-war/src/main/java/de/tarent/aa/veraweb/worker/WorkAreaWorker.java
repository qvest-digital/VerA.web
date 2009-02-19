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

/* $Id: WorkAreaWorker.java,v 1.0 2008/02/14 13:00:00 cklein Exp $ */
package de.tarent.aa.veraweb.worker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.tarent.aa.veraweb.beans.WorkArea;
import de.tarent.dblayer.sql.clause.Expr;
import de.tarent.dblayer.sql.clause.Order;
import de.tarent.dblayer.sql.statement.Select;
import de.tarent.octopus.PersonalConfigAA;
import de.tarent.octopus.beans.Bean;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.beans.Database;
import de.tarent.octopus.server.OctopusContext;

/**
 * The class WorkAreaWorker is a concrete worker
 * for operations on the {@link WorkArea} entity
 * bean.
 * 
 * @author cklein
 * @since 1.2.0
 * @see de.tarent.aa.veraweb.beans.WorkArea
 */
public class WorkAreaWorker extends StammdatenWorker
{
	/**
	 * Constructs a new instance of this.
	 */
	public WorkAreaWorker()
	{
		super( "WorkArea" );
	}

	@Override
	protected void saveBean( OctopusContext cntx, Bean bean ) throws BeanException, IOException
	{
		WorkArea workArea = ( WorkArea ) bean;

		if ( workArea.orgunit == null )
		{
			workArea.orgunit = ( ( PersonalConfigAA ) cntx.personalConfig() ).getOrgUnitId();
		}
		if ( workArea.orgunit == -1)
		{
			List< String > errors = ( List< String > ) cntx.getContextField( OUTPUT_saveListErrors );
			if  ( errors == null )
			{
				errors = new ArrayList< String >();
			}
			errors.add( "Der Arbeitsbereich mit dem Namen '" + ( ( WorkArea ) bean ).name + "' konnte nicht angelegt werden. Bitte weisen Sie sich zuerst einen Mandanten zu." );
			cntx.setContent( OUTPUT_saveListErrors, errors );
		}
		else
		{
			super.saveBean(cntx, bean);
		}
	}

	@Override
    protected void extendWhere(OctopusContext cntx, Select select) throws BeanException, IOException {
		// hide default entry with pk=0 from user, the workarea "Kein" with pk ::= 0
		// is only used internally in order to be able to use foreign key constraints
		// with individual workareas being assigned to one or multiple users.
		select.where( Expr.greater( "pk", 0 ) );
		select.where( Expr.equal( "fk_orgunit", ( ( PersonalConfigAA ) cntx.personalConfig() ).getOrgUnitId() ) );
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
			} else if ( "flags".equals( order ) )
			{
				select.orderBy( Order.asc( order ).andAsc( "name" ) );
				cntx.setContent( "order", order );
			}
		}
	}

	@Override
    protected boolean removeBean(OctopusContext cntx, Bean bean) throws BeanException, IOException
	{
		try
		{
			super.removeBean( cntx, bean );
		}
		catch( BeanException e )
		{
			if ( e.getCause().getMessage().indexOf( "Fremdschlüssel-Constraint" ) > 0 )
			{
				List< String > errors = ( List< String > ) cntx.getContextField( OUTPUT_saveListErrors );
				if  ( errors == null )
				{
					errors = new ArrayList< String >();
				}
				Database database = getDatabase(cntx);
				bean = database.getBean( "WorkArea", ( ( WorkArea ) bean ).id ); 
				errors.add( "Der ausgewählte Arbeitsbereich mit dem Namen '" + ( ( WorkArea ) bean ).name + "' ist noch einzelnen Personen zugeordnet und kann nicht gelöscht werden." );
				cntx.setContent( OUTPUT_saveListErrors, errors );
			}
			else
			{
				throw e;
			}
		}
		return true;
	}

	@Override
    protected void extendAll( OctopusContext cntx, Select select )
		throws BeanException, IOException
	{
		// hide default entry with pk=0 from user, the workarea "Kein" with pk ::= 0
		// is only used internally in order to be able to use foreign key constraints
		// with individual workareas being assigned to one or multiple users.
		select.where( Expr.greater( "pk", 0 ) );
		select.where( Expr.equal( "fk_orgunit", ( ( PersonalConfigAA ) cntx.personalConfig() ).getOrgUnitId() ) );
	}
	
	@Override
	public void getAll(OctopusContext cntx) throws BeanException, IOException 
	{
		super.getAll(cntx);
		
		Integer count = cntx.requestAsInteger( "count" );
		if ( count != null )
		{
			cntx.setContent( "count", count );
		}
	}
}
