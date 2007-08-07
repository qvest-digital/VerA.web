/*
 * $Id: ConfigWorker.java,v 1.1 2007/06/20 11:56:51 christoph Exp $
 */
package de.tarent.aa.veraweb.worker;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import de.tarent.dblayer.sql.SQL;
import de.tarent.dblayer.sql.clause.Expr;
import de.tarent.octopus.PersonalConfigAA;
import de.tarent.octopus.custom.beans.BeanException;
import de.tarent.octopus.custom.beans.BeanFactory;
import de.tarent.octopus.custom.beans.Database;
import de.tarent.octopus.custom.beans.veraweb.ListWorkerVeraWeb;
import de.tarent.octopus.server.OctopusContext;

/**
 * Dieser Worker stellt Hilfsfunktionen zur Verfügung um Einstellungen
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
			"LABEL_ADDRESS_SUFFIX1", "LABEL_ADDRESS_SUFFIX2" };
	private static final String defaultTarget[] = {
			"main", "partner",
			"private", "business", "other",
			"latin", "extra1", "extra2",
			"suffix1", "suffix2" };
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
     * Diese Octopus-Aktion initialisiert die Map der Konfig-Einträge dieses
     * Workers aus der Datenbank (mittels der ererbten Aktion
     * {@link de.tarent.octopus.custom.beans.BeanListWorker#getAll(OctopusContext)})
     * gegebenenfalls ergänzt um einige Muss-Einträge.  
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
				result.put(defaultTarget[i], defaultBundle.getString(defaultSource[i]));
			}
		}
		
		config = result;
		loaded = true;
	}

    /** Input-Parameter der Octopus-Aktion {@link #load(OctopusContext)} */
    static public final String INPUT_load[] = {};
    /**
     * Diese Octopus-Aktion initialisiert die Map der Konfig-Einträge dieses
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
     * Diese Octopus-Aktion deinitialisiert die Map der Konfig-Einträge dieses
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
     * Diese Octopus-Aktion speichert eine Liste von Konfigurationseinträgen aus dem
     * Octopus-Request (unter "saveconfig-*") in der Datenbank.
     * 
     * @param cntx Octopus-Kontext
     * @throws BeanException
     * @throws IOException
     */
	public void save(OctopusContext cntx) throws BeanException, IOException {
		if (!cntx.configImpl().isUserInGroup(PersonalConfigAA.GROUP_ADMIN))
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

	private void saveValue(OctopusContext cntx, String key, String value) throws BeanException, IOException {
		// wenn standard, dann null
		for (int i = 0; i < defaultTarget.length; i++) {
			if (defaultTarget[i].equals(key)) {
				if (defaultBundle.getString(defaultSource[i]).equals(value)) {
					value = null;
				}
				break;
			}
		}
		
		// einstellung in datenbank speichern
		Database database = getDatabase(cntx);
		Integer count =
			database.getCount(
			database.getCount("Config").
			where(Expr.equal("cname", key)));
		
		if (count.intValue() == 0) {
			if (value != null && value.length() != 0) {
				database.execute(
						SQL.Insert().
						table("veraweb.tconfig").
						insert("cname", key).
						insert("cvalue", value));
				config.put(key, value);
			}
		} else {
			if (value != null && value.length() != 0) {
				database.execute(
						SQL.Update().
						table("veraweb.tconfig").
						update("cvalue", value).
						where(Expr.equal("cname", key)));
				config.put(key, value);
			} else {
				database.execute(
						SQL.Delete().
						from("veraweb.tconfig").
						where(Expr.equal("cname", key)));
				config.remove(key);
			}
		}
		
		// default aus properties laden
		if (value == null || value.length() == 0) {
			for (int i = 0; i < defaultTarget.length; i++) {
				if (defaultTarget[i].equals(key)) {
					config.put(key, defaultBundle.getString(defaultSource[i]));
				}
			}
		}
	}

	/**
	 * Gibt eine Config-Einstellung zurück.
	 * 
	 * @param cntx Octopus-Kontext
	 * @param key Name hinter dem die Einstellung hinterlegt sein soll.
	 * @return Der Wert der Einstellung oder null.
	 */
	public static String getString(OctopusContext cntx, String key) {
		return WorkerFactory.getConfigWorker(cntx).getValue(key);
	}

	/**
	 * Gibt eine Config-Einstellung zurück, falls dieser nicht zu einer
	 * Zahl transformiert werden kann wird null zurückgegeben.
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
