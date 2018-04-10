package de.tarent.octopus.content;

/*-
 * VerA.web-Middleware, newly MIT licenced, is comprised of:
 * tarent-commons, a set of common components and solutions
 * tarent-contact, platform-independent webservice-based contact management
 * tarent-database, jdbc database library
 * tarent-doctor, Document Generation Platform
 * tarent-octopus, Webservice Data Integrator and Application Server
 *  © 2005, 2006, 2007 asteban <s.mancke@tarent.de>
 *  © 2007 David Goemans <d.goemans@tarent.de>
 *  © 2006, 2007, 2010 Hendrik Helwich <h.helwich@tarent.de>
 *  © 2005, 2006, 2007 Christoph Jerolimov <c.jerolimov@tarent.de>
 *  © 2006 Philipp Kirchner <p.kirchner@tarent.de>
 *  © 2010 Carsten Klein <c.klein@tarent.de>
 *  © 2006 Michael Kleinhenz <m.kleinhenz@tarent.de>
 *  © 2006 Michael Klink <m.klink@tarent.de>
 *  © 2007 Fabian Köster <f.koester@tarent.de>
 *  © 2006 Martin Ley <m.ley@tarent.de>
 *  © 2007 Alex Maier <a.maier@tarent.de>
 *  © 2007, 2015, 2017, 2018 mirabilos <t.glaser@tarent.de>
 *  © 2006, 2007 Jens Neumaier <j.neumaier@tarent.de>
 *  © 2006 Nils Neumaier <n.neumaier@tarent.de>
 *  © 2007, 2008 Martin Pelzer <m.pelzer@tarent.de>
 *  © 2008, 2009 Christian Preilowski <c.thiel@tarent.de>
 *  © 2006, 2008, 2009 Thomas Schmitz <t.schmitz@tarent.de>
 *  © 2007 Robert Schuster <r.schuster@tarent.de>
 * and older code, Copyright © 2001–2007 ⮡ tarent GmbH and contributors.
 * Licensor is tarent solutions GmbH, http://www.tarent.de/
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

import de.tarent.octopus.config.*;

import java.util.HashMap;

public class AnnotationWorkerFactoryTest
        extends junit.framework.TestCase {

    TcModuleConfig config;
    AnnotationWorkerFactory factory;
    ContentWorkerDeclaration workerDeclaration;

    /**
     * Void Constructor for instantiation as worker
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
        Object workerDelegate = ((DelegatingWorker) worker).getWorkerDelegate();
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
