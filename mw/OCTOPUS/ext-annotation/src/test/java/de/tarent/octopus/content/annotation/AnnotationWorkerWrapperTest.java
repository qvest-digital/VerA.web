/*
 * tarent-octopus annotation extension,
 * an opensource webservice and webapplication framework (annotation extension)
 * Copyright (c) 2006-2007 tarent GmbH
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
 * interest in the program 'tarent-octopus annotation extension'
 * Signature of Elmar Geese, 11 June 2007
 * Elmar Geese, CEO tarent GmbH.
 */

package de.tarent.octopus.content.annotation;

import de.tarent.octopus.config.*;
import de.tarent.octopus.content.*;
import de.tarent.octopus.request.*;
import java.util.*;

public class AnnotationWorkerWrapperTest
    extends junit.framework.TestCase {

    static boolean calledFlag;

    TcModuleConfig moduleConfig;
    TcContentWorker sampleWorker1;
    TcContentWorker sampleWorker2;
    TcRequest request;
    TcContent content;
    TcConfig config;

    public AnnotationWorkerWrapperTest(String init) {
        super(init);
    }

    public void setUp()
        throws Exception {
        calledFlag = false;
        moduleConfig = TcModuleConfig.createMockupModuleConfig("/tmp", new HashMap());

        AnnotationWorkerFactory factory = new AnnotationWorkerFactory();
        ContentWorkerDeclaration workerDeclaration = new ContentWorkerDeclaration();
        workerDeclaration.setImplementationSource(SampleWorker1.class.getName());
        sampleWorker1 = factory.createInstance(getClass().getClassLoader(), workerDeclaration);

        workerDeclaration.setImplementationSource(SampleWorker2.class.getName());
        sampleWorker2 = factory.createInstance(getClass().getClassLoader(), workerDeclaration);

        request = new TcRequest();
        request.setRequestParameters(new HashMap());
        content = new TcContent();

        // this mockup can produce errors if parts of the config will be accecced
        config = new TcConfig(null, null, null);
    }

    public void testVersion()
        throws Exception {

        assertEquals("Given Version.", "0.4.2", sampleWorker1.getVersion() );
        assertEquals("Default Version.", "1.0", sampleWorker2.getVersion() );
    }

    public void testInitCall()
        throws Exception {

        sampleWorker1.init(moduleConfig);
        assertTrue("Init method was called.", ((SampleWorker1)((DelegatingWorker)sampleWorker1).getWorkerDelegate()).wasInitCalled);

        sampleWorker2.init(moduleConfig);
        // no init method, only assert, that there is no exception
    }

    public void testSimpleCall1()
        throws Exception {
        sampleWorker1.doAction(config, "helloWorld", request, content);
        assertEquals("getting result", "Hello World!", content.getAsString("helloWorldResult"));
    }

    public void testSimpleCall2()
        throws Exception {
        sampleWorker1.doAction(config, "helloWorldWithDefaultResult", request, content);
        assertEquals("getting result", "Hello World!", content.getAsString("return"));
    }

    public void testSimpleCall3()
        throws Exception {
        sampleWorker1.doAction(config, "otherName", request, content);
        assertEquals("getting result", "Hello World!", content.getAsString("return"));
    }

    public void testArgumentCall1()
        throws Exception {
        content.setField("firstname", "Frank");
        sampleWorker1.doAction(config, "helloWorldWithArgument", request, content);
        assertEquals("getting result", "Hello Frank", content.getAsString("return"));
    }

    public void testArgumentCall2()
        throws Exception {
        content.setField("firstname", "Frank");
        sampleWorker1.doAction(config, "nameAnnotation", request, content);
        assertEquals("getting result", "Hello Frank", content.getAsString("return"));
    }

    public void testMandatoryParams1()
        throws Exception {

        content.setField("mandatoryByDefault", "Frank");
        content.setField("mandatory", "Frank");
        content.setField("optional", (String)null);
        sampleWorker1.doAction(config, "optionalArguments", request, content);
        // only assert, that there is no exception
    }

    public void testMandatoryParams2()
        throws Exception {

        try {
            content.setField("mandatoryByDefault", "Frank");
            content.setField("mandatory", (String)null);
            content.setField("optional", (String)null);
            sampleWorker1.doAction(config, "optionalArguments", request, content);
        } catch (TcActionInvocationException aie) {
            // success
            return;
        }
        fail("no exception on missing mandatory param");
    }

    public void testMandatoryParams3()
        throws Exception {

        try {
            content.setField("mandatoryByDefault", (String)null);
            content.setField("mandatory", "Frank");
            content.setField("optional", (String)null);
            sampleWorker1.doAction(config, "optionalArguments", request, content);
        } catch (TcActionInvocationException aie) {
            // success
            return;
        }
        fail("no exception on missing mandatory param");
    }

    public void testTypesSimple()
        throws Exception {

        content.setField("testCase", this);
        content.setField("int", 42);
        content.setField("long", 42l);
        content.setField("float", 42f);
        content.setField("double", 42d);
        content.setField("boolean", true);
        content.setField("list", Collections.singletonList("test"));
        content.setField("map", Collections.singletonMap("test", "test"));

        sampleWorker1.doAction(config, "testTypesSimple", request, content);
    }

    public void testParameterConversions()
        throws Exception {

        content.setField("testCase", this);

        content.setField("int1", "42");
        content.setField("long1", "42");
        content.setField("float1", "42");
        content.setField("double1", "42");
        content.setField("boolean1", "True");
        // fields int2, long2, float2, double2, boolean2 are undefined

        content.setField("list", new String[]{"test"});
        Map map = new HashMap();
        map.put("name", "Frank");
        map.put("city", "Prüm");
        content.setField("map", map);

        sampleWorker1.doAction(config, "testParameterConversions", request, content);
    }

    public void testInOuts()
        throws Exception {

        content.setField("p1", "test");
        content.setField("p2", 40);
        content.setField("p3", false);

        sampleWorker1.doAction(config, "testInOuts", request, content);

        assertEquals("String InOut", "test.suffix", content.get("p1"));
        assertEquals("Integer InOut", 42, content.get("p2"));
        assertEquals("String InOut", true, content.get("p3"));
    }
}
