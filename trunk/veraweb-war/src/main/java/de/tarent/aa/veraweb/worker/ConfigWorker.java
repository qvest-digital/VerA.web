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
 * $Id$
 */
package de.tarent.aa.veraweb.worker;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import de.tarent.aa.veraweb.beans.Config;
import de.tarent.aa.veraweb.beans.Duration;
import de.tarent.dblayer.sql.SQL;
import de.tarent.dblayer.sql.clause.Expr;
import de.tarent.dblayer.sql.statement.Delete;
import de.tarent.dblayer.sql.statement.Insert;
import de.tarent.dblayer.sql.statement.Select;
import de.tarent.dblayer.sql.statement.Update;
import de.tarent.octopus.PersonalConfigAA;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.beans.BeanFactory;
import de.tarent.octopus.beans.Database;
import de.tarent.octopus.beans.TransactionContext;
import de.tarent.octopus.beans.veraweb.ListWorkerVeraWeb;
import de.tarent.octopus.server.OctopusContext;

/**
 * Dieser Worker stellt Hilfsfunktionen zur Verf�gung um Einstellungen
 * aus der Datenbank auszulesen.
 * 
 * @author Christoph Jerolimov
 * @version $Revision: 1.1 $
 */
public class ConfigWorker extends ListWorkerVeraWeb {
	private static final String defaultSource[] = {
			"LABEL_MEMBER_MAIN", "LABEL_MEMBER_PARTNER",
			"LABEL_MEMBER_PRIVATE", "LABEL_MEMBER_BUSINESS", "LABEL_MEMBER_OTHER",
			"LABEL_MEMBER_LATIN", "LABEL_MEMBER_EXTRA1", "LABEL_MEMBER_EXTRA2",
			"LABEL_ADDRESS_SUFFIX1", "LABEL_ADDRESS_SUFFIX2", "CHANGE_LOG_RETENTION_POLICY" };
	private static final String defaultTarget[] = {
			"main", "partner",
			"private", "business", "other",
			"latin", "extra1", "extra2",
			"suffix1", "suffix2", "changeLogRetentionPolicy" };
	private static final ResourceBundle defaultBundle =
			ResourceBundle.getBundle("de.tarent.aa.veraweb.config");

	protected Map config;
	protected boolean loaded = false;

    //
    // Konstruktoren
    //
    /**
     * Der Konstruktor legt den Bean-Namen fest.
     */
	public ConfigWorker() {
		super("Config");
	}

    //
    // Octopus-Aktionen
    //
    /** Input-Parameter der Octopus-Aktion {@link #init(OctopusContext)} */
	static public final String INPUT_init[] = {};
    /**
     * Diese Octopus-Aktion initialisiert die Map der Konfig-Eintr�ge dieses
     * Workers aus der Datenbank (mittels der ererbten Aktion
     * {@link de.tarent.octopus.beans.BeanListWorker#getAll(OctopusContext)})
     * gegebenenfalls erg�nzt um einige Muss-Eintr�ge.  
     * 
     * @param cntx Octopus-Kontext
     */
	public void init(OctopusContext cntx) throws BeanException, IOException {
		// config aus datenbank laden
		Map result = new HashMap();
		getAll(cntx);
		List list = (List)cntx.contentAsObject("all" + BEANNAME);
		for (Iterator it = list.iterator(); it.hasNext(); ) {
			Map entry = (Map)it.next();
			result.put(entry.get("key"), entry.get("value"));
		}
		
		// default config aus properties laden
		for (int i = 0; i < defaultTarget.length; i++) {
			String value = (String)result.get(defaultTarget[i]);
			if (value == null || value.length() == 0) {
				if ( "CHANGE_LOG_RETENTION_POLICY".compareTo( defaultSource[ i ] ) == 0 )
				{
					result.put( defaultTarget[ i ], Duration.fromString( defaultBundle.getString( defaultSource[ i ] ) ) );
				}
				else
				{
					result.put(defaultTarget[i], defaultBundle.getString(defaultSource[i]));
				}
			}
		}
		
		config = result;
		loaded = true;
	}

    /** Input-Parameter der Octopus-Aktion {@link #load(OctopusContext)} */
    static public final String INPUT_load[] = {};
    /**
     * Diese Octopus-Aktion initialisiert die Map der Konfig-Eintr�ge dieses
     * Workers mittels der Aktion {@link #init(OctopusContext)}, sofern dies
     * nicht schon zuvor geschehen ist, und setzt einen entsprechenden Eintrag
     * im Octopus-Content.
     * 
     * @param cntx Octopus-Kontext
     * @throws BeanException
     * @throws IOException
     */
	public void load(OctopusContext cntx) throws BeanException, IOException {
		if (!loaded) {
			init(cntx);
		}
		cntx.setContent("config", config);
	}

    /** Input-Parameter der Octopus-Aktion {@link #clean(OctopusContext)} */
    static public final String INPUT_clean[] = {};
    /**
     * Diese Octopus-Aktion deinitialisiert die Map der Konfig-Eintr�ge dieses
     * Workers.
     * 
     * @param cntx Octopus-Kontext
     */
	public void clean(OctopusContext cntx) {
		config = null;
		loaded = false;
	}

    /** Input-Parameter der Octopus-Aktion {@link #save(OctopusContext)} */
    static public final String INPUT_save[] = {};
    /**
     * Diese Octopus-Aktion speichert eine Liste von Konfigurationseintr�gen aus dem
     * Octopus-Request (unter "saveconfig-*") in der Datenbank.
     * 
     * @param cntx Octopus-Kontext
     * @throws BeanException
     * @throws IOException
     * @throws SQLException 
     */
	public void save(OctopusContext cntx) throws BeanException, IOException, SQLException {
		if (!cntx.personalConfig().isUserInGroup(PersonalConfigAA.GROUP_ADMIN))
			return;
		
		List list = (List)BeanFactory.transform(cntx.requestAsObject("saveconfig"), List.class);
		for (Iterator it = list.iterator(); it.hasNext(); ) {
			String key = (String)it.next();
			String value = cntx.requestAsString(key);
			saveValue(cntx, key, value);
		}
	}

    //
    // Hilfsmethoden
    //
	private String getValue(String key) {
		return (String)config.get(key);
	}

	/*
	 * modified to support duration handling for the new setting change log retention policy
	 * 
	 * refactored a bit to minimize processing overhead, namely removed redundant second loop and
	 * moved default/new/null value handling to the first loop
	 * 
	 * database query getCount() will now be executed only if there is a value to be stored
	 * 
	 * cklein
	 * 2008-02-27
	 */
	private void saveValue(OctopusContext cntx, String key, String value) throws BeanException, IOException, SQLException {
		// wenn standard, dann null und default aus properties laden, sonst neuen wert in config hinterlegen
		boolean found = false;
		for (int i = 0; i < defaultTarget.length; i++) 
		{
			if (defaultTarget[i].equals(key)) {
				found = true;
				if ( "CHANGE_LOG_RETENTION_POLICY".compareTo( defaultSource[ i ] ) == 0 )
				{
					Duration dnew = Duration.fromString( value );
					Duration dold = Duration.fromString( defaultBundle.getString( defaultSource[ i ] ) );
					if
					(
						( dold.toString().compareTo( dnew.toString() ) == 0 ) ||
						( dnew.toString().compareTo( "P0" ) == 0 )
					)
					{
						value = null;
						config.put( defaultTarget[ i ], Duration.fromString( defaultBundle.getString( defaultSource[ i ] ) ) );
					}
					else
					{
						config.put( defaultTarget[ i ], Duration.fromString( value ) );
					}
				}
				else
				{
					if (defaultBundle.getString(defaultSource[i]).equals(value))
					{
						value = null;
						config.put(key, defaultBundle.getString(defaultSource[i]));
					}
					else
					{
						config.put(key, value);
					}
				}
				break;
			}
		}
		if ( ! found )
		{
			// ist kein default konfigurationseintrag
			if ( "".compareTo( value ) == 0 )
			{
				value = null;
				config.remove( key );
			}
			else
			{
				config.put( key, value );
			}
		}
		
		// einstellung in datenbank speichern
		Database database = getDatabase(cntx);
		if ( value != null && value.length() != 0 )
		{
			Integer count =
				database.getCount(
				database.getCount("Config").
				where(Expr.equal("cname", key)));
		
			if (count.intValue() == 0) {
				Insert insert = SQL.Insert( database );
				insert.table( "veraweb.tconfig" );
				insert.insert( "cname", key );
				insert.insert( "cvalue", value );
				insert.execute();
			} else {
				Update update = SQL.Update( database );
				update.table( "veraweb.tconfig" );
				update.update( "cvalue", value );
				update.where( Expr.equal( "cname", key ) );
				update.execute();
			}
		} else {
			Delete delete = SQL.Delete( database );
			delete.from( "veraweb.tconfig" );
			delete.where( Expr.equal( "cname", key ) );
			delete.execute();
		}
	}

	/**
	 * Gibt eine Config-Einstellung zur�ck.
	 * 
	 * @param cntx Octopus-Kontext
	 * @param key Name hinter dem die Einstellung hinterlegt sein soll.
	 * @return Der Wert der Einstellung oder null.
	 */
	public static String getString(OctopusContext cntx, String key) {
		return WorkerFactory.getConfigWorker(cntx).getValue(key);
	}

	/**
	 * Gibt eine Config-Einstellung zur�ck, falls dieser nicht zu einer
	 * Zahl transformiert werden kann wird null zur�ckgegeben.
	 * 
	 * @param cntx Octopus-Kontext
	 * @param key Name hinter dem die Einstellung hinterlegt sein soll.
	 * @return Der Wert der Einstellung oder null.
	 */
	public static Integer getInteger(OctopusContext cntx, String key) {
		try {
			return new Integer(getString(cntx, key));
		} catch (NullPointerException e) {
			return null;
		} catch (NumberFormatException e) {
			return null;
		}
	}
}
