package de.tarent.octopus.custom.beans;
import de.tarent.octopus.client.OctopusConnection;
import de.tarent.octopus.client.OctopusResult;
import junit.framework.TestCase;
import lombok.extern.log4j.Log4j2;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * Diese Testklasse dient dem Profilen des Zeitverhaltens beim Erstellen
 * von Insert-Statements, das deutlich länger braucht als das Erstellen von
 * analogen Where-Klauseln.
 *
 * @author mikel
 */
@Log4j2
public class ProfileInsertTest extends TestCase {
    private OctopusConnection con;

    public void testProfile() {
        if (con == null) {
            return;
        }

        Map taskParamsTestProfile = new TreeMap();
        taskParamsTestProfile.put("module", "veraweb");
        taskParamsTestProfile.put("username", "pol-2");
        taskParamsTestProfile.put("password", "Benutzer99");
        taskParamsTestProfile.put("count", "10");

        ProfileLogger pLog = new ProfileLogger();
        OctopusResult res = con.callTask("testProfile", taskParamsTestProfile);
        pLog.log("testProfile");
        printResult(res, "testProfile-Result:");
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        con = null;
        super.tearDown();
    }

    static void printResult(OctopusResult res, String title) {
        if (res == null) {
            return;
        }
        System.out.println(title);
        for (Iterator iter = res.getDataKeys(); iter.hasNext(); ) {
            String key = (String) iter.next();
            System.out.println("  " + key + "=" + res.getData(key));
        }
        System.out.println();
    }
}
