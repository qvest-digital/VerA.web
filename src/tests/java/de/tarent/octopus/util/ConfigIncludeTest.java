package de.tarent.octopus.util;

import java.util.*;
import java.util.logging.Level;
import java.util.prefs.Preferences;

import javax.xml.parsers.*;
import org.w3c.dom.Document;

import de.tarent.octopus.config.TcModuleConfig;
import de.tarent.octopus.request.TcEnv;
import de.tarent.octopus.resource.Resources;

import java.io.*;

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
        // C:\workspace\octopus-core\src\tests\java\de\tarent\octopus\\util
        String realPath = "c:\\workspace\\octopus-core\\src\\tests\\java\\de\\tarent\\octopus\\util";
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