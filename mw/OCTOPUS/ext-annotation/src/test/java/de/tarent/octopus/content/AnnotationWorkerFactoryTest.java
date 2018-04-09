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

package de.tarent.octopus.content;

import de.tarent.octopus.config.*;
import java.util.HashMap;

public class AnnotationWorkerFactoryTest
    extends junit.framework.TestCase {

    TcModuleConfig config;
    AnnotationWorkerFactory factory;
    ContentWorkerDeclaration workerDeclaration;

    /**
     *  Void Constructor for instantiation as worker
     */
    public AnnotationWorkerFactoryTest() {
    }

    public AnnotationWorkerFactoryTest(String init) {
        super(init);
    }

    public void setUp() {
        config = TcModuleConfig.createMockupModuleConfig("/tmp", new HashMap());
        factory = new AnnotationWorkerFactory();
        workerDeclaration = new ContentWorkerDeclaration();

    }

    public void testCreation()
        throws Exception {
        workerDeclaration.setImplementationSource(getClass().getName());

        TcContentWorker worker = factory.createInstance(getClass().getClassLoader(), workerDeclaration);
        Object workerDelegate = ((DelegatingWorker)worker).getWorkerDelegate();
        assertEquals("Worker is instance of the class.", workerDelegate.getClass(), getClass());
    }

    public void testErrorHandling_noSource()
        throws Exception {

        try {
            factory.createInstance(getClass().getClassLoader(), workerDeclaration);
        } catch (Exception e) {
            // Success
            return;
        }

        fail("No exception on worker creation with missing source");
    }

    public void testErrorHandling_wrongSource()
        throws Exception {

        try {
            workerDeclaration.setImplementationSource("xxx.yyy.zzzz");
            factory.createInstance(getClass().getClassLoader(), workerDeclaration);
        } catch (Exception e) {
            // Success
            return;
        }

        fail("No exception on worker creation with wrong source");
    }
}
