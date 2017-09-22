package de.tarent.octopus.custom.beans;

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
import de.tarent.octopus.client.OctopusConnection;
import de.tarent.octopus.client.OctopusResult;
import junit.framework.TestCase;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Diese Testklasse dient dem Profilen des Zeitverhaltens beim Erstellen
 * von Insert-Statements, das deutlich länger braucht als das Erstellen von
 * analogen Where-Klauseln.
 *
 * @author mikel
 */
public class ProfileInsert extends TestCase {
    private OctopusConnection con;
    private static Logger baseLogger = null;
    private FileHandler fileLogHandler = null;

    public void testProfile() {
    	if (con == null)
    		return;

    	Map taskParamsTestProfile = new TreeMap();
    	taskParamsTestProfile.put("module", "veraweb");
        taskParamsTestProfile.put("username", "pol-2");
        taskParamsTestProfile.put("password", "Benutzer99");
        taskParamsTestProfile.put("count", "10");

        ProfileLogger pLog = new ProfileLogger();
        OctopusResult res = con.callTask("testProfile", taskParamsTestProfile);
        pLog.log("testProfile");
        printResult(res, "testProfile-Result:" );
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        baseLogger = Logger.getLogger("de.tarent");
        baseLogger.setLevel(Level.ALL);
        baseLogger.addHandler(new ConsoleHandler());
//        con = OctopusConnectionFactory.getInstance().getConnection("test");
    }

    @Override
    protected void tearDown() throws Exception {
    	con = null;
        super.tearDown();
    }



    static void printResult(OctopusResult res, String title) {
        if (res == null)
            return;
        System.out.println(title);
        for (Iterator iter = res.getDataKeys(); iter.hasNext();) {
            String key = (String)iter.next();
            System.out.println("  "+key+ "="+res.getData(key));
        }
        System.out.println();
    }
}
