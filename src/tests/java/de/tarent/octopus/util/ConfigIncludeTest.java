package de.tarent.octopus.util;

import java.io.File;
import java.util.prefs.Preferences;

import junit.framework.TestCase;

import org.w3c.dom.Document;

import de.tarent.octopus.config.TcModuleConfig;
import de.tarent.octopus.resource.Resources;

/**
 * Tests the Parsing of List Params with nested Maps
 */
public class ConfigIncludeTest extends TestCase {
    public void testRecursiveIncludes() throws Exception {
        File modulePath = new File("src/tests/java/de/tarent/octopus/util");
        
        File configFile = new File(modulePath, "config.xml");

        String resource = Resources.getInstance().get("REQUESTPROXY_URL_MODULE_CONFIG", configFile.getAbsolutePath());
        
        Document document = Xml.getParsedDocument(resource);
        
        Preferences modulePreferences = Preferences.systemRoot().node("/de/tarent/octopus");
        
        TcModuleConfig config = new TcModuleConfig("Testname", modulePath, document, modulePreferences);
        
        assertEquals("Falsche Anzahl an eingelesenen Parametern: ", 2, config.getParams().size());
    }
}
