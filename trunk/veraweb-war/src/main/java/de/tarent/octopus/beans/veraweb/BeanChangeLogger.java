/*
 * veraweb,
 * Veranstaltungsmanagment veraweb
 * Copyright (c) 2005-2008 tarent GmbH
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
 * $Id: BeanChangeLogger.java,v 1.2 2008/02/12 12:00:00 cklein Exp $
 * 
 * Created on 12.02.2008
 */
package de.tarent.octopus.beans.veraweb;

import java.io.IOException;
import java.sql.Date;
import java.util.Iterator;

import de.tarent.aa.veraweb.beans.ChangeLogEntry;
import de.tarent.aa.veraweb.beans.Event;
import de.tarent.aa.veraweb.beans.Guest;
import de.tarent.aa.veraweb.beans.Person;
import de.tarent.dblayer.sql.statement.Insert;
import de.tarent.octopus.beans.Bean;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.beans.Database;
import de.tarent.octopus.beans.ExecutionContext;
import de.tarent.octopus.beans.TransactionContext;

/**
 * The class BeanChangeLogger represents a facility
 * for logging changes to individual beans.
 * 
 * The changes logged will be written to the same
 * database. For this to work, the database schema
 * must include the following schema extensions:
 * 
 * CREATE TABLE tchangelog
 * (
 * 		pk serial NOT NULL,
 * 		username varchar(100) NOT NULL,
 * 		objtype varchar(30) NOT NULL,		-- the type of the object
 * 		objid int4 NOT NULL,				-- the id of the object
 * 		op varchar(6) NOT NULL,				-- the sql operation: insert|delete|update
 * 		attributes text NOT NULL,			-- a list of comma separated attribute names
 * 		date timestamptz,
 * 		CONSTRAINT tchangelog_pkey PRIMARY KEY (pk)
 * );
 * 
 * In order to create an actual entry in the log, the ChangeLogEntry bean will
 * be used for storing the information to and for retrieving the informatiom
 * from the database.
 * 
 * @author cklein
 * @since 1.2
 */
public class BeanChangeLogger {

	private Database database;
	private TransactionContext context;

	/**
	 * Creates a new instance of the logger.
	 * 
	 * @param database	the database to which we will be logging changes to
	 * @param context the current transaction context
	 */
	public BeanChangeLogger( Database database, TransactionContext context )
	{
		this.database = database;
		this.context = context;
	}

	public BeanChangeLogger( Database database )
	{
		this.database = database;
	}

	/**
	 * Logs the update change to the database. if any.
	 * For this, the method will compare the old version o and the new
	 * version n of a bean of the same class.
	 * 
	 * @param username	the username who is committing the change
	 * @param o			the bean's old state from the database
	 * @param n			the bean's new state from the request
	 */
	@SuppressWarnings("unchecked")
	public void logUpdate( String username, Bean o, Bean n ) throws BeanException, IOException
	{
		if ( ! o.getClass().equals( n.getClass() ) )
		{
			throw new IllegalArgumentException( "Beans o and n must be of the same type." );
		}

		// compile a comma separated list of attributes that were changed
		// from version o to version n of the bean.
		StringBuffer changedAttributes = new StringBuffer();
		Iterator< String > i = o.getFields().iterator();
		while( i.hasNext() )
		{
			String k = i.next();

			if ( k.compareTo( "id" ) == 0 )
			{
				// we skip the primary key a/o id field
				continue;
			}
			
			Comparable nv = ( Comparable ) n.getField( k );
			Comparable ov = ( Comparable ) o.getField( k );
			try
			{
				if 
				( 
					( ( ov != null ) && ( ov.compareTo( nv ) != 0 ) ) 
					|| ( ( ov == null ) && ( nv != null ) )
				)
				{
					if ( changedAttributes.length() > 0 )
					{
						changedAttributes.append( ',' );
					}
					changedAttributes.append( k );
				}
			}
			catch( NullPointerException e )
			{
				;; // just catch
			}
		}

		// are there any differences from o to n?
		if ( changedAttributes.length() > 0 )
		{
			ChangeLogEntry entry = this.createNewChangeLogEntryInstance( "update", username, determineObjectName( o ), o.getClass().getName(), ( Integer ) o.getField( "id" ), changedAttributes.toString() );
			this.insertLogEntry( entry );
		}
	}

	/**
	 * Logs the insert change to the database. if any.
	 * 
	 * @param username	the username who is committing the change
	 * @param o			the bean's old state from the database
	 */
	public void logInsert( String username, Bean o ) throws BeanException, IOException
	{
		ChangeLogEntry entry = this.createNewChangeLogEntryInstance( "insert", username, determineObjectName( o ), o.getClass().getName(), ( Integer ) o.getField( "id" ), "*" );
		this.insertLogEntry( entry );
	}

	/**
	 * Logs the delete change to the database.
	 * 
	 * @param username	the username who is committing the change
	 * @param o			the bean's old state from the database
	 */
	public void logDelete( String username, Bean o ) throws BeanException, IOException
	{
		ChangeLogEntry entry = this.createNewChangeLogEntryInstance( "delete", username, determineObjectName( o ), o.getClass().getName(), ( Integer ) o.getField( "id" ), "*" );
		this.insertLogEntry( entry );
	}

	private String determineObjectName( Bean o ) throws BeanException, IOException
	{
		String result = "";

		if ( o instanceof Event )
		{
			result = ( ( Event ) o ).shortname; // shortname is mandatory
		}
		else
		{
			if ( o instanceof Guest )
			{
				o = this.database.getBean( "Person", ( ( Guest ) o ).person );
			}
			Person p = ( Person ) o;
			if ( p != null )
			{
				result = p.lastname_a_e1; // lastname is mandatory, even for companies
				if ( p.firstname_a_e1 != null )
				{
					result += ", " + p.firstname_a_e1; 
				}
			}
		}
		if ( result == null || result.length() == 0 )
		{
			result = "Kein Name vergeben";
		}

		return result;
	}
	
	private void insertLogEntry( ChangeLogEntry entry )
		throws BeanException, IOException
	{
		entry.verify();
		if ( entry.isCorrect() )
		{
			ExecutionContext c = this.database;
			if( this.context != null )
			{
				c = this.context;
			}

			this.database.getNextPk( entry, c );
			Insert insert = this.database.getInsert( entry );
			c.execute( insert );
		}
	}

	private ChangeLogEntry createNewChangeLogEntryInstance( String op, String username, String oname, String otype, Integer oid, String attributes )
		throws BeanException, IOException
	{
		ChangeLogEntry result = new ChangeLogEntry();
		result.username = username;
		result.objectname = oname;
		result.objecttype = otype;
		result.objectid = oid;
		result.op = op;
		result.attributes = attributes;
		result.created = new Date( System.currentTimeMillis() );
		return result;
	}
}
