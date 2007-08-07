/*
 * $Id: ProfileInsert.java,v 1.1 2007/06/20 11:56:52 christoph Exp $
 * 
 * Created on 18.11.2005
 */
package de.tarent.octopus.custom.beans;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import junit.framework.TestCase;
import de.tarent.octopus.client.OctopusConnection;
import de.tarent.octopus.client.OctopusConnectionFactory;
import de.tarent.octopus.client.OctopusResult;

/**
 * Diese Testklasse dient dem Profilen des Zeitverhaltens beim Erstellen
 * von Insert-Statements, das deutlich länger braucht als das Erstellen von
 * analogen Where-Klauseln.
 * 
 * @author mikel
 */
public class ProfileInsert extends TestCase {
    OctopusConnection con = OctopusConnectionFactory.getInstance().getConnection("test");
    private static Logger baseLogger = null;
    private FileHandler fileLogHandler = null;
    
    final static Map taskParamsTestProfile = new TreeMap();
    static {
        taskParamsTestProfile.put("module", "veraweb");
        taskParamsTestProfile.put("username", "pol-2");
        taskParamsTestProfile.put("password", "Benutzer99");
        taskParamsTestProfile.put("count", "10");
    }
    
    public void testProfile() {
        ProfileLogger pLog = new ProfileLogger();
        OctopusResult res = con.callTask("testProfile", taskParamsTestProfile);
        pLog.log("testProfile");
        printResult(res, "testProfile-Result:" );
    }

    protected void setUp() throws Exception {
        super.setUp();
        baseLogger = Logger.getLogger("de.tarent");
        baseLogger.setLevel(Level.ALL);
        fileLogHandler = new FileHandler("test/log/octopus-%g_%u.log", 1000000, 10, true);
        fileLogHandler.setEncoding("UTF-8");
        fileLogHandler.setFormatter(new SimpleFormatter());
        baseLogger.addHandler(fileLogHandler);
    }

    protected void tearDown() throws Exception {
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
