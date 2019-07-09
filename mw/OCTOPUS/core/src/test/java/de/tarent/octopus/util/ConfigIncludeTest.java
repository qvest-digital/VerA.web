package de.tarent.octopus.util;
import java.io.File;
import java.net.URL;

import junit.framework.TestCase;

import org.w3c.dom.Document;

import de.tarent.octopus.config.TcModuleConfig;
import de.tarent.octopus.resource.Resources;

/**
 * Tests the Parsing of List Params with nested Maps
 */
public class ConfigIncludeTest extends TestCase {
    public void testRecursiveIncludes() throws Exception {
        URL url = ConfigIncludeTest.class.getResource("config.xml");

        String resource = Resources.getInstance().get("REQUESTPROXY_URL_MODULE_CONFIG", url.getFile());

        Document document = Xml.getParsedDocument(resource);

        TcModuleConfig config = new TcModuleConfig("Testname", new File(url.getFile()).getParentFile(), document);

        assertEquals("Falsche Anzahl an eingelesenen Parametern: ", 2, config.getParams().size());
    }
}
