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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;

import org.apache.log4j.Category;
import org.apache.log4j.FileAppender;
import org.apache.log4j.xml.DOMConfigurator;

import de.tarent.dblayer.engine.DB;
import de.tarent.octopus.server.OctopusContext;

/**
 * System Worker initalisiert den Datenbank-Connection-Pool
 * und das Logging.
 * 
 * @author Christoph Jerolimov <c.jerolimov@tarent.de>
 * @version 1.1
 */
public class SystemWorker {
    //
    // Octopus-Aktionen
    //
    /** Eingabe-Parameter der Octopus-Aktion {@link #openPool(OctopusContext)} */
	public final static String INPUT_openPool[] = {};
    /**
     * Diese Octopus-Aktion initialisiert den dblayer-DB-Pool dieses Moduls.
     * 
     * @param cntx Octopus-Kontext
     */
	public void openPool(OctopusContext cntx) throws IOException {
		File file = new File(
				cntx.moduleConfig().getRealPath(),
				cntx.moduleConfig().getParam("dblayer"));
		
		Properties properties = new Properties();
		properties.load(new FileInputStream(file));
		
		DB.openPool(cntx.getModuleName(), new HashMap(properties));
	}

    /** Eingabe-Parameter der Octopus-Aktion {@link #closePool(OctopusContext)} */
	public final static String INPUT_closePool[] = {};
    /**
     * Diese Octopus-Aktion schlie�t den dblayer-DB-Pool dieses Moduls.
     * 
     * @param cntx Octopus-Kontext
     */
	public void closePool(OctopusContext cntx) {
		DB.closePool(cntx.getModuleName());
	}

    /** Eingabe-Parameter der Octopus-Aktion {@link #initLogging(OctopusContext)} */
	public final static String INPUT_initLogging[] = {};
    /**
     * Diese Octopus-Aktion initialisiert das Log4J-Logging dieses Moduls.
     * 
     * @param cntx Octopus-Kontext
     */
	public void initLogging(OctopusContext cntx) {
		DOMConfigurator.configure(cntx.moduleConfig().getOtherNode("log4j:configuration"));
		
		String path = cntx.moduleConfig().getRealPath() + "/log/";
		new File(path).mkdirs();
		changeLogDir(Category.getInstance("SQL"), path);
		changeLogDir(Category.getInstance("WORKER"), path);
		changeLogDir(Category.getInstance("ERROR"), path);
		changeLogDir(Category.getInstance("de.tarent.dblayer"), path);
		changeLogDir(Category.getInstance("de.tarent.aa.veraweb"), path);
	}

    //
    // Hilfsmethoden
    //
    /**
     * Diese Methode �ndert den Pfad der Datei der �bergebenen Log4J-Kategorie
     * ab, indem der �bergebene Pfad vorangestellt wird, wenn der bisherige Pfad
     * keinen Verzeichnisanteil hat.
     * 
     * @param category abzu�ndernde Log4J-Kategorie
     * @param path zu benutzender Dateipfad
     */
	protected void changeLogDir(Category category, String path) {
		if (category == null) return;
		for (Enumeration e = category.getAllAppenders(); e.hasMoreElements(); ) {
			Object o = e.nextElement();
			if (o != null && o instanceof FileAppender) {
				FileAppender a = (FileAppender)o;
				if (a.getFile() != null && a.getFile().indexOf('/') == -1 && a.getFile().indexOf('\\') == -1) {
					a.setFile(path + a.getFile());
					a.activateOptions();
				}
			}
		}
	}
}
