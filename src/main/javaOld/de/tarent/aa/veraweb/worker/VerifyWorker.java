package de.tarent.aa.veraweb.worker;

/*-
 * veraweb, platform independent webservice-based event management
 * (Veranstaltungsmanagment VerA.web), is
 * Copyright © 2004–2008 tarent GmbH
 * Copyright © 2013–2016 tarent solutions GmbH
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
import de.tarent.aa.veraweb.beans.Config;
import de.tarent.aa.veraweb.utils.i18n.LanguageProvider;
import de.tarent.aa.veraweb.utils.i18n.LanguageProviderHelper;
import de.tarent.dblayer.sql.clause.Expr;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.beans.Database;
import de.tarent.octopus.beans.veraweb.DatabaseVeraWeb;
import de.tarent.octopus.server.OctopusContext;

import javax.xml.transform.TransformerFactory;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ResourceBundle;

/**
 * Dieser Worker schreibt Fehlerberichte oder Warnungen
 * als Liste in den Content.
 *
 * @author Christoph
 */
public class VerifyWorker {
	/** Octopus-Eingabeparameter für die Aktion {@link #addError(OctopusContext, String)} */
	public static final String INPUT_addError[] = { "message" };
	/**
	 * Octopus-Aktion die einen Error in den Content stellt.
	 *
	 * @param cntx Octopus-Context
	 * @param message Fehlermeldung
	 */
	public void addError(OctopusContext cntx, String message) {
		if (message != null && message.length() != 0) {
			Collection errors = null;
			if (cntx.contentAsObject("errors") instanceof Collection)
				errors = (Collection)cntx.contentAsObject("errors");
			if (errors == null)
				errors = new ArrayList();
			errors.add(message);
			cntx.setContent("errors", errors);
		}
	}

	/** Octopus-Eingabeparameter für die Aktion {@link #addWarning(OctopusContext, String)} */
	public static final String INPUT_addWarning[] = { "message" };
	/**
	 * Octopus-Aktion die eine Warnung in den Content stellt.
	 *
	 * @param cntx Octopus-Context
	 * @param message Warnmeldung
	 */
	public void addWarning(OctopusContext cntx, String message) {
		if (message != null && message.length() != 0) {
			Collection warnings = null;
			if (cntx.contentAsObject("warnings") instanceof Collection)
				warnings = (Collection)cntx.contentAsObject("warnings");
			if (warnings == null)
				warnings = new ArrayList();
			warnings.add(message);
			cntx.setContent("warnings", warnings);
		}
	}

	/** Octopus-Eingabeparameter für die Action {@link #verifySchemaVersion(OctopusContext)} */
	public final static String INPUT_verifySchemaVersion[] = {};
	/**
	 * Testet ob die Version des Datenbank-Schemas mit der Version in der
	 * veraweb.properties übereinstimmt und erweitert ggf. die Liste mit
	 * Fehlern und stellt diese in den Content.
	 *
	 * @param cntx OctopusContext
	 * @throws BeanException
	 * @throws IOException
	 */
	public void verifySchemaVersion(OctopusContext cntx) throws BeanException, IOException {
		LanguageProviderHelper languageProviderHelper = new LanguageProviderHelper();
		LanguageProvider languageProvider = languageProviderHelper.enableTranslation(cntx);
		Database database = new DatabaseVeraWeb(cntx);

		Config config = (Config)
				database.getBean("Config",
				database.getSelect("Config").
				where(Expr.equal("cname", "SCHEMA_VERSION")));
		ResourceBundle properties = (ResourceBundle)cntx.contentAsObject("properties");

		if (config == null || config.value == null) {
			addError(cntx, languageProvider.getProperty("VERIFY_ERROR_DB_VERSION_NOT_DETERMINED"));
		} else if (!config.value.equals(properties.getString("schema-version"))) {
			addError(cntx, languageProvider.getProperty("VERIFY_ERROR_DB_VERSION_INCONSISTENCE_ONE") + config.value +
					languageProvider.getProperty("VERIFY_ERROR_DB_VERSION_INCONSISTENCE_TWO") +
					properties.getString("schema-version") +
					languageProvider.getProperty("VERIFY_ERROR_DB_VERSION_INCONSISTENCE_THREE"));
		}
	}

	/** Octopus-Eingabeparameter für die Action {@link #getDatabaseCharset(OctopusContext)} */
	public final static String INPUT_getDatabaseCharset[] = {};
	/**
	 * Testet ob der Zeichensatz der Datenbank richtig konfiguriert ist.
	 *
	 * @param cntx OctopusContext
	 */
	public void getDatabaseCharset(OctopusContext cntx) {
		DatabaseVeraWeb database = new DatabaseVeraWeb(cntx);

		String LC_CTYPE = getResult(database, "SHOW LC_CTYPE");
		String LC_COLLATE = getResult(database, "SHOW LC_COLLATE");
		cntx.setContent("LC_CTYPE", LC_CTYPE);
		cntx.setContent("LC_COLLATE", LC_COLLATE);
		database.close();
	}

	/** Octopus-Eingabeparameter für die Action {@link #verifyDatabaseCharset(OctopusContext)} */
	public final static String INPUT_verifyDatabaseCharset[] = {};// "LC_CTYPE", "LC_COLLATE" };
	/**
	 * Testet ob der Zeichensatz der Datenbank richtig konfiguriert ist.
	 *
	 * @param cntx OctopusContext
	 */
	public void verifyDatabaseCharset(OctopusContext cntx) {
		LanguageProviderHelper languageProviderHelper = new LanguageProviderHelper();
		LanguageProvider languageProvider = languageProviderHelper.enableTranslation(cntx);
		getDatabaseCharset(cntx);
		String LC_CTYPE = cntx.contentAsString("LC_CTYPE");
		String LC_COLLATE = cntx.contentAsString("LC_COLLATE");
		cntx.setContent("LC_CTYPE", LC_CTYPE);
		cntx.setContent("LC_COLLATE", LC_COLLATE);

		if (LC_CTYPE == null || LC_CTYPE.toUpperCase().indexOf("UTF-8") == -1) {
			addError(cntx, languageProvider.getProperty("VERIFY_ERROR_DB_LC_CTYPE_NOT_WANTED") + LC_CTYPE + ").");
		}
		if (LC_COLLATE == null || LC_COLLATE.toUpperCase().indexOf("UTF-8") == -1) {
			addError(cntx, languageProvider.getProperty("VERIFY_ERROR_DB_LC_COLLATE_NOT_WANTED") + LC_COLLATE + ").");
		}
	}

	protected String getResult(Database database, String statement) {
		ResultSet rs = null;
		try {
			rs = database.result(statement);
			return rs.next() ? rs.getString(1) : null;
		} catch (BeanException e) {
			return null;
		} catch (SQLException e) {
			return null;
		} finally {
			if (rs != null) {
	            try {
	                database.close(rs);
                } catch (BeanException e) {
                }
			}
		}
	}

	/** Octopus-Eingabeparameter für die Aktion {@link #verifyXMLTransformer(OctopusContext)} */
	public final static String INPUT_verifyXMLTransformer[] = {};
	/**
	 * Testet ob der XML Transformer (für OpenOffice) korrektes XML erzeugt.
	 *
	 * @param cntx OctopusContext
	 */
	public void verifyXMLTransformer(OctopusContext cntx) {
		try {
			TransformerFactory.newInstance().newTransformer();
		} catch (Throwable e) {
			LanguageProviderHelper languageProviderHelper = new LanguageProviderHelper();
			LanguageProvider languageProvider = languageProviderHelper.enableTranslation(cntx);
			addError(cntx, languageProvider.getProperty("VERIFY_ERROR_XML") + e.toString() + ")");
		}
	}

	/** Octopus-Eingabeparameter für die Aktion {@link #verifyJavaVersion(OctopusContext)} */
	public final static String INPUT_verifyJavaVersion[] = {};
	/**
	 * Testet ob die verwendete Java-Version der spezifizierten entspricht.
	 *
	 * @param cntx OctopusContext
	 */
	public void verifyJavaVersion(OctopusContext cntx) {
		if (!System.getProperty("java.version").startsWith("1.4")) {
			LanguageProviderHelper languageProviderHelper = new LanguageProviderHelper();
			LanguageProvider languageProvider = languageProviderHelper.enableTranslation(cntx);
			addError(cntx, languageProvider.getProperty("VERIFY_ERROR_JAVA_ONE") +
					       System.getProperty("java.version") +
						   languageProvider.getProperty("VERIFY_ERROR_JAVA_TWO"));
		}
	}
}
