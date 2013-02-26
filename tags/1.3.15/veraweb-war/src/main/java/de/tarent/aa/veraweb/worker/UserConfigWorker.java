/**
 * veraweb, platform independent webservice-based event management
 * (Veranstaltungsmanagment VerA.web), is
 * Copyright © 2004-2008 tarent GmbH
 * Copyright © 2013 tarent solutions GmbH
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License, version 2, as
 * published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see: http://www.gnu.org/licenses/
 */
package de.tarent.aa.veraweb.worker;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import de.tarent.aa.veraweb.beans.OrgUnit;
import de.tarent.aa.veraweb.beans.User;
import de.tarent.aa.veraweb.beans.UserConfig;
import de.tarent.dblayer.sql.clause.Expr;
import de.tarent.dblayer.sql.clause.Where;
import de.tarent.dblayer.sql.statement.Select;
import de.tarent.octopus.PersonalConfigAA;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.beans.Database;
import de.tarent.octopus.beans.veraweb.DatabaseVeraWeb;
import de.tarent.octopus.server.OctopusContext;

/**
 * Dieser Octopus-Worker stellt Aktionen zum laden und speichern
 * von Benutzereinstellungen zur Verf�gung.
 * 
 * @author Christoph Jerolimov
 * @version $Revision: 1.1 $
 */
public class UserConfigWorker {
	private static final String PARAMS_STRING[] = {
		"personTab", "personMemberTab",
		"personAddresstypeTab", "personLocaleTab" };
	private static final String PARAMS_BOOLEAN[] = {
		"guestListFunction", "guestListCity", "guestListPhone", "personListState" };

	/** Octopus-Eingabe-Parameter f�r {@link #init(OctopusContext)} */
	public static final String INPUT_init[] = {};
	/** Octopus-Ausgabe-Parameter f�r {@link #init(OctopusContext)} */
	public static final String OUTPUT_init = "userConfig";
	/**
	 * L�dt die Konfiguration aus der Datenbank in die Session.
	 * 
	 * @param cntx
	 * @throws BeanException
	 * @throws IOException
	 */
	public Map init(OctopusContext cntx) throws BeanException, IOException {
		Database database = new DatabaseVeraWeb(cntx);
		Integer userId = ((PersonalConfigAA)cntx.personalConfig()).getVerawebId();
		if (userId == null) return null;
		
		Select select = database.getSelect("UserConfig");
		select.where(Expr.equal("fk_user", userId));
		
		Map result = new HashMap();
		for (Iterator it = database.getList(select, database).iterator(); it.hasNext(); ) {
			Map data = (Map)it.next();
			result.put(data.get("key"), data.get("value"));
		}
		
		/*
		 * modified to support display of orgunit as per change request for version 1.2.0
		 * 
		 * cklein
		 * 2008-02-15 
		 */
		User user = ( User ) database.getBean( "User", userId );
		OrgUnit orgUnit = new OrgUnit();
		
		if ( user != null && user.orgunit != null && user.orgunit.intValue() != 0 )
		{
			orgUnit = ( OrgUnit ) database.getBean( "OrgUnit", user.orgunit );
		}
		
		cntx.setContent( "orgUnit", orgUnit );
		cntx.setSession("userConfig", result);
		return result;
	}

	/** Octopus-Eingabe-Parameter f�r {@link #load(OctopusContext)} */
	public static final String INPUT_load[] = {};
	/** Octopus-Ausgabe-Parameter f�r {@link #load(OctopusContext)} */
	public static final String OUTPUT_load = "userConfig";
	/**
	 * L�dt die Konfiguration aus der Session in den Content.
	 * 
	 * @param cntx
	 * @throws BeanException
	 * @throws IOException
	 */
	public Map load(OctopusContext cntx) throws BeanException, IOException {
		Map result = (Map)cntx.sessionAsObject("userConfig");
		if (result == null) {
			return init(cntx);
		}
		return result;
	}

	/** Octopus-Eingabe-Parameter f�r {@link #save(OctopusContext)} */
	public static final String INPUT_save[] = {};
	/**
	 * Speichert die Benutzer Einstellungen in der Datenbank.
	 * 
	 * @param cntx
	 * @throws BeanException
	 * @throws IOException
	 */
	public void save(OctopusContext cntx) throws BeanException, IOException {
		if (!cntx.requestContains("save"))
			return;
		
		Database database = new DatabaseVeraWeb(cntx);
		Integer userId = ((PersonalConfigAA)cntx.personalConfig()).getVerawebId();
		Map config = (Map)cntx.contentAsObject("config");
		Map userConfig = (Map)cntx.contentAsObject("userConfig");
		
		for (int i = 0; i < PARAMS_STRING.length; i++) {
			String key = PARAMS_STRING[i];
			String value = cntx.requestAsString(key);
			String global = (String)config.get(key);
			
			if (value == null) continue;
			
			if (global == null) {
				// Global ist nichts gesetzt, Standard.
				if (value.equals("1")) {
					removeUserSetting(database, userId, userConfig, key);
				} else if (!value.equals("1")) {
					setUserSetting(database, userId, userConfig, key, value);
				}
			} else {
				// Global wurde dies gesetzt.
				if (global.equals(value)) {
					removeUserSetting(database, userId, userConfig, key);
				} else {
					setUserSetting(database, userId, userConfig, key, value);
				}
			}
		}
		
		for (int i = 0; i < PARAMS_BOOLEAN.length; i++) {
			String key = PARAMS_BOOLEAN[i];
			boolean value = cntx.requestAsBoolean(key).booleanValue();
			if (value) {
				setUserSetting(database, userId, userConfig, key, "true");
			} else {
				removeUserSetting(database, userId, userConfig, key);
			}
		}
	}

	protected void removeUserSetting(Database database, Integer userId, Map userConfig, String key) throws BeanException, IOException {
		String old = (String)userConfig.get(key);
		if (old != null) {
			database.execute(database.getDelete("UserConfig").
					where(Where.and(
							Expr.equal("fk_user", userId),
							Expr.equal("name", key))));
			userConfig.remove(key);
		}
	}

	protected void setUserSetting(Database database, Integer userId, Map userConfig, String key, String value) throws BeanException, IOException {
		String old = (String)userConfig.get(key);
		if (value == null) {
			removeUserSetting(database, userId, userConfig, key);
		} else if (old == null) {
			UserConfig config = new UserConfig();
			config.user = userId;
			config.key = key;
			config.value = value;
			database.saveBean(config);
			userConfig.put(key, value);
		} else if (!value.equals(old)) {
			database.execute(database.getUpdate("UserConfig").
					update("value", value).
					where(Where.and(
						Expr.equal("fk_user", userId),
						Expr.equal("name", key))));
			userConfig.put(key, value);
		}
	}
}
