package de.tarent.octopus.util;

import java.io.File;
import java.util.Map;
import java.util.prefs.Preferences;

import org.w3c.dom.Document;

import de.tarent.octopus.config.TcModuleConfig;
import de.tarent.octopus.resource.Resources;

/**
 * Tests the Parsing of List Params with nested Maps
 */
public class ConfigIncludeTest
    extends junit.framework.TestCase {

    /**
     *  Void Constructor for instantiation as worker
     */
    public ConfigIncludeTest() {
    }

    public ConfigIncludeTest(String init) {
        super(init);
    }

    public void setUp() {

    }

    public void testRecursiveIncludes() 
        throws Exception {
        String realPath = "src/tests/java/de/tarent/octopus/util";
        File modulePath = new File(realPath);
        
        File configFile;                        
        configFile = new File(modulePath, "config.xml");
        Document document = Xml.getParsedDocument(Resources.getInstance().get("REQUESTPROXY_URL_MODULE_CONFIG", configFile.getAbsolutePath()));
        Preferences modulePreferences = Preferences.systemRoot().node("/de/tarent/octopus");
        
        TcModuleConfig config = new TcModuleConfig("Testname", modulePath, document, modulePreferences);
        Map params = config.getParams();
        
        assertTrue("Falsche Anzahl an eingelesenen Parametern: " +params.size(), params.size() == 2);
        
       
    }

}