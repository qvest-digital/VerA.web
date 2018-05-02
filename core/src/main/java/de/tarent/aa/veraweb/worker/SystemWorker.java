package de.tarent.aa.veraweb.worker;

/*-
 * Veranstaltungsmanagement VerA.web (platform-independent
 * webservice-based event management) is Copyright
 *  © 2014, 2015, 2016, 2017 Атанас Александров (a.alexandrov@tarent.de)
 *  © 2018 Атанас Александров (sirakov@gmail.com)
 *  © 2013 Иванка Александрова (i.alexandrova@tarent.de)
 *  © 2013 Patrick Apel (p.apel@tarent.de)
 *  © 2016 Eugen Auschew (e.auschew@tarent.de)
 *  © 2013 Andrei Boulgakov (a.boulgakov@tarent.de)
 *  © 2013 Valentin But (v.but@tarent.de)
 *  © 2016 Lukas Degener (l.degener@tarent.de)
 *  © 2017 Axel Dirla (a.dirla@tarent.de)
 *  © 2015 Julian Drawe (j.drawe@tarent.de)
 *  © 2014 Dominik George (d.george@tarent.de)
 *  © 2013 Sascha Girrulat (s.girrulat@tarent.de)
 *  © 2008 David Goemans (d.goemans@tarent.de)
 *  © 2015 Viktor Hamm (v.hamm@tarent.de)
 *  © 2013 Katja Hapke (k.hapke@tarent.de)
 *  © 2013 Hendrik Helwich (h.helwich@tarent.de)
 *  © 2018 Thomas Hensel (t.hensel@tarent.de)
 *  © 2005, 2006, 2007, 2008 Christoph Jerolimov (jerolimov@gmx.de)
 *  © 2008, 2009, 2010 Carsten Klein (c.klein@tarent.de)
 *  © 2014 Martin Ley (m.ley@tarent.de)
 *  © 2014, 2015 Max Marche (m.marche@tarent.de)
 *  © 2007 Jan Meyer (jan@evolvis.org)
 *  © 2013, 2014, 2015, 2016, 2017, 2018 mirabilos (t.glaser@tarent.de)
 *  © 2016 Cristian Molina (c.molina@tarent.de)
 *  © 2018 Yorka Neumann (y.neumann@tarent.de)
 *  © 2017 Michael Nienhaus (m.nienhaus@tarent.de)
 *  © 2013 Claudia Nuessle (c.nuessle@tarent.de)
 *  © 2014, 2015 Jon Nunez Alvarez (j.nunez-alvarez@tarent.de)
 *  © 2016 Jens Oberender (j.oberender@tarent.de)
 *  © 2016, 2017 Miluška Pech (m.pech@tarent.de)
 *  © 2009 Martin Pelzer (m.pelzer@tarent.de)
 *  © 2013 Marc Radel (m.radel@tarent.de)
 *  © 2013 Sebastian Reimers (s.reimers@tarent.de)
 *  © 2015 Charbel Saliba (c.saliba@tarent.de)
 *  © 2008, 2009, 2010 Thomas Schmitz (t.schmitz@tarent.de)
 *  © 2013 Volker Schmitz (v.schmitz@tarent.de)
 *  © 2014 Sven Schumann (s.schumann@tarent.de)
 *  © 2014 Sevilay Temiz (s.temiz@tarent.de)
 *  © 2013 Kevin Viola Schmitz (k.schmitz@tarent.de)
 *  © 2015 Stefan Walenda (s.walenda@tarent.de)
 *  © 2015, 2016, 2017 Max Weierstall (m.weierstall@tarent.de)
 *  © 2013 Rebecca Weinz (r.weinz@tarent.de)
 *  © 2015, 2016 Stefan Weiz (s.weiz@tarent.de)
 *  © 2015, 2016 Tim Zimmer (t.zimmer@tarent.de)
 * and older code, Copyright © 2004–2008 ⮡ tarent GmbH and contributors.
 * Licensor is tarent solutions GmbH, http://www.tarent.de/
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

import de.tarent.dblayer.engine.DB;
import de.tarent.octopus.server.OctopusContext;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

/**
 * System Worker initalisiert den Datenbank-Connection-Pool
 * und das Logging.
 *
 * @author Christoph Jerolimov c.jerolimov@tarent.de
 * @version 1.1
 */
public class SystemWorker {
    //
    // Octopus-Aktionen
    //
    /**
     * Eingabe-Parameter der Octopus-Aktion {@link #openPool(OctopusContext)}
     */
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

    /**
     * Eingabe-Parameter der Octopus-Aktion {@link #closePool(OctopusContext)}
     */
    public final static String INPUT_closePool[] = {};

    /**
     * Diese Octopus-Aktion schließt den dblayer-DB-Pool dieses Moduls.
     *
     * @param cntx Octopus-Kontext
     */
    public void closePool(OctopusContext cntx) {
        DB.closePool(cntx.getModuleName());
    }

    /**
     * Eingabe-Parameter der Octopus-Aktion
     */
    public final static String INPUT_initLogging[] = {};
    /**
     * Diese Octopus-Aktion initialisiert das Log4J-Logging dieses Moduls.
     *
     * @param cntx Octopus-Kontext
     */
    //	public void initLogging(OctopusContext cntx) {
    //		DOMConfigurator.configure(cntx.moduleConfig().getOtherNode("log4j:configuration"));
    //
    //		String path = cntx.moduleConfig().getRealPath() + "/log/";
    //		new File(path).mkdirs();
    //		changeLogDir(Category.getInstance("SQL"), path);
    //		changeLogDir(Category.getInstance("WORKER"), path);
    //		changeLogDir(Category.getInstance("ERROR"), path);
    //		changeLogDir(Category.getInstance("de.tarent.dblayer"), path);
    //		changeLogDir(Category.getInstance("de.tarent.aa.veraweb"), path);
    //	}

    //
    // Hilfsmethoden
    //
    /**
     * Diese Methode ändert den Pfad der Datei der übergebenen Log4J-Kategorie
     * ab, indem der übergebene Pfad vorangestellt wird, wenn der bisherige Pfad
     * keinen Verzeichnisanteil hat.
     *
     * @param category abzuändernde Log4J-Kategorie
     * @param path zu benutzender Dateipfad
     */
    //	protected void changeLogDir(Category category, String path) {
    //		if (category == null) return;
    //		for (Enumeration e = category.getAllAppenders(); e.hasMoreElements(); ) {
    //			Object o = e.nextElement();
    //			if (o != null && o instanceof FileAppender) {
    //				FileAppender a = (FileAppender)o;
    //				if (a.getFile() != null && a.getFile().indexOf('/') == -1 && a.getFile().indexOf('\\') == -1) {
    //					a.setFile(path + a.getFile());
    //					a.activateOptions();
    //				}
    //			}
    //		}
    //	}
}
