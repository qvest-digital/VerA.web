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

import de.tarent.aa.veraweb.beans.WorkArea;
import de.tarent.dblayer.sql.clause.Clause;
import de.tarent.dblayer.sql.clause.Expr;
import de.tarent.dblayer.sql.clause.Order;
import de.tarent.dblayer.sql.statement.Select;
import de.tarent.octopus.custom.beans.Bean;
import de.tarent.octopus.custom.beans.BeanException;
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

	protected void extendWhere( OctopusContext cntx, Select select )
		throws BeanException, IOException
	{
		Clause clause = getWhere( cntx );
		if ( clause != null )
		{
			select.where( clause );
		}
	}

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
		Clause clause = Expr.equal( "deleted", "f" );
		/*
        TcPersonalConfig pConfig = cntx.personalConfig();
        if ( pConfig instanceof PersonalConfigAA )
        {
            PersonalConfigAA aaConfig = ( PersonalConfigAA ) pConfig;
            String domain = cntx.contentAsString( PARAM_DOMAIN );
            if ( ! ( PARAM_DOMAIN_VALUE_ALL.equals( domain ) && pConfig.isUserInGroup( PersonalConfigAA.GROUP_ADMIN ) ) )
            {
        		Integer orgunit = ( ( PersonalConfigAA ) ( cntx.personalConfig() ) ).getOrgUnitId();
            	if (orgunit == null)
            		return Expr.isNull( "tcategorie.fk_orgunit" );
            	else
            		return Expr.equal( "tcategorie.fk_orgunit", aaConfig.getOrgUnitId() );
            }
            return null;
        } else
        {
            throw new BeanException( "Missing user information." );
        }
        */
		return clause;
	}

	protected void saveBean(OctopusContext cntx, Bean bean) throws BeanException, IOException
	{
		( ( WorkArea ) bean).deleted = "f";
		super.saveBean( cntx, bean );
	}
}
