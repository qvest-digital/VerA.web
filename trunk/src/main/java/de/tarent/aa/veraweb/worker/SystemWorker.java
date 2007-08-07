/*
 * VerA.web,
 * Veranstaltungsmanagment VerA.web
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
 * interest in the program 'VerA.web'
 * Signature of Elmar Geese, 7 August 2007
 * Elmar Geese, CEO tarent GmbH.
 */

/*
 * $Id: SystemWorker.java,v 1.1 2007/06/20 11:56:51 christoph Exp $
 * Created on 21.02.2005
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
     * @throws IOException
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
