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
 * $Id: ChangeLogMaintenanceWorker.java,v 1.0 2008/2/22 14:40:00 cklein Exp $
 */
package de.tarent.aa.veraweb.worker;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Calendar;

import org.apache.log4j.Logger;

import de.tarent.aa.veraweb.beans.Duration;
import de.tarent.dblayer.engine.DB;
import de.tarent.dblayer.sql.SQL;
import de.tarent.dblayer.sql.clause.Expr;
import de.tarent.octopus.custom.beans.BeanException;
import de.tarent.octopus.server.OctopusContext;

/**
 * Worker that runs constantly purges old entries from the changelog.
 * 
 * @see ChangeLogMaintenanceWorker
 * 
 * @author cklein
 * @version $Revision: 1.1 $
 */
public class ChangeLogMaintenanceWorker implements Runnable {
	/** Log4J Logger Instanz */
	private final Logger logger = Logger.getLogger(ChangeLogMaintenanceWorker.class);
	/** Actual worker thread */
	protected Thread thread;
	protected boolean keeprunning = false;
	protected boolean isworking = false;
	/** Gibt die Wartezeit zwischen zwei Dispatch aufrufen an. */
	protected int waitMillis = 0;
	protected OctopusContext cntx = null;

	protected Duration retentionPolicy;

	/** Octopus-Eingabe-Parameter für {@link #load(OctopusContext)} */
	public static final String INPUT_load[] = {};

	/**
	 * Starts the background maintenance service.
	 * 
	 * @param cntx Octopus-Context
	 */
	public void load( OctopusContext cntx )
	{
		this.logger.info( "ChangeLogMaintenanceWorker wird im Hintergrund gestartet." );
		this.cntx = cntx;
		this.retentionPolicy = Duration.fromString( cntx.moduleConfig().getParam( "changeLogRetentionPolicy" ) );
		if ( this.retentionPolicy.toString().equals( "P0" ) )
		{
			// log invalid setting and use 1yr. default
			this.logger.warn( "changeLogRetentionPolicy Konfigurationseinstellung ist fehlerhaft. Die Einstellung muß einer gültigen Zeitdauerangabe im Format P[0-9]+Y[0-9]+M[0-9]+D entsprechen. Stattdessen wird die Vorgabe P1Y (1 Jahr) verwendet." );
			this.retentionPolicy.years = 1;
		}

		// this setting is unconfigurable, by default, the maintenance routine
		// will be run once every two hours
		this.waitMillis = 7200000;

		// Server status
		if ( ! keeprunning )
		{
			this.keeprunning = true;
			this.thread = new Thread( this );
			this.thread.start();
		}
		else
		{
			this.unload( cntx );
		}
	}

	/** Octopus-Eingabe-Parameter f�r {@link #unload(OctopusContext)} */
	public static final String INPUT_unload[] = {};
	/**
	 * Stops the background service.
	 * 
	 * @param cntx Octopus-Context
	 */
	public void unload(OctopusContext cntx)
	{
		this.logger.info( "ChangeLogMaintenanceWorker wird gestoppt." );
		this.keeprunning = false;
	}

	/** @see Runnable#run() */
	public void run()
	{
		try
		{
			this.isworking = true;
			while ( this.keeprunning )
			{
				try
				{
					this.purgeChangeLog();
					throw new Exception();
				}
				catch ( Exception e )
				{
					this.logger.error( "ChangeLogMaintenanceWorker: allgemeiner Fehler während der Wartungsarbeit.", e );
				}
				try
				{
					// TODO reactivate
					Thread.sleep( this.waitMillis < 1000 ? 1000 : this.waitMillis );
				}
				catch (InterruptedException e)
				{
					;; // just catch
				}
			}
		}
		finally
		{
			this.isworking = false;
			this.thread = null;
		}
	}

	/**
	 * Purges the change log from old information.
	 * @throws IOException 
	 * @throws BeanException 
	 */
	public void purgeChangeLog() throws SQLException, BeanException, IOException
	{
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis( System.currentTimeMillis() );
		c.add( Calendar.YEAR, -1 * this.retentionPolicy.years );
		c.add( Calendar.MONTH, -1 * this.retentionPolicy.months );
		c.add( Calendar.DAY_OF_MONTH, -1 * this.retentionPolicy.days );
		Date d = new Date( c.getTimeInMillis() );
		DB.update(
			this.cntx.getModuleName(),
			SQL.Delete().from( "veraweb.tchangelog" ).where( Expr.lessOrEqual( "date", d.toString() ) )
		);
	}
}
