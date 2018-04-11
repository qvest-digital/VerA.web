package de.tarent.octopus.content;

/*-
 * VerA.web-Middleware, newly MIT licenced, is comprised of:
 * tarent-commons, a set of common components and solutions
 * tarent-contact, platform-independent webservice-based contact management
 * tarent-database, jdbc database library
 * tarent-doctor, Document Generation Platform
 * tarent-octopus, Webservice Data Integrator and Application Server
 *  © 2005, 2006, 2007 asteban (s.mancke@tarent.de)
 *  © 2007 David Goemans (d.goemans@tarent.de)
 *  © 2006, 2007, 2010 Hendrik Helwich (h.helwich@tarent.de)
 *  © 2005, 2006, 2007 Christoph Jerolimov (c.jerolimov@tarent.de)
 *  © 2006 Philipp Kirchner (p.kirchner@tarent.de)
 *  © 2010 Carsten Klein (c.klein@tarent.de)
 *  © 2006 Michael Kleinhenz (m.kleinhenz@tarent.de)
 *  © 2006 Michael Klink (m.klink@tarent.de)
 *  © 2007 Fabian Köster (f.koester@tarent.de)
 *  © 2006 Martin Ley (m.ley@tarent.de)
 *  © 2007 Alex Maier (a.maier@tarent.de)
 *  © 2007, 2015, 2017, 2018 mirabilos (t.glaser@tarent.de)
 *  © 2006, 2007 Jens Neumaier (j.neumaier@tarent.de)
 *  © 2006 Nils Neumaier (n.neumaier@tarent.de)
 *  © 2007, 2008 Martin Pelzer (m.pelzer@tarent.de)
 *  © 2008, 2009 Christian Preilowski (c.thiel@tarent.de)
 *  © 2006, 2008, 2009 Thomas Schmitz (t.schmitz@tarent.de)
 *  © 2007 Robert Schuster (r.schuster@tarent.de)
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

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;

import de.tarent.octopus.config.ContentWorkerDeclaration;
import de.tarent.octopus.config.TcModuleConfig;
import de.tarent.octopus.logging.LogFactory;
import de.tarent.octopus.resource.Resources;
import de.tarent.octopus.server.SpecialWorkerFactory;
import de.tarent.octopus.server.WorkerCreationException;

/**
 * Factory Klasse mit statischen Methoden zur Lieferung von WorkerInstanzen
 *
 * @author <a href="mailto:mancke@mancke-software.de">Sebastian Mancke</a>, <b>tarent GmbH</b>
 * @see TcContentWorker
 */
public class TcContentWorkerFactory {
    private static Log logger = LogFactory.getLog(TcContentWorkerFactory.class);

    /**
     * Map mit TcContentWorkern. Keys der Map sind die ClassLoader der Module. Values sind wiederum Maps mit
     * ("workername"=>Instance).
     * Dadurch werden die Worker gepuffert und müssen nicht für jede Anfrage neu erstellt werden.
     */
    protected static Map workers = new HashMap();

    /**
     * Map mit den Speziellen Factorys zur Instanziierung von Workern. Keys der Map sind die ClassLoader der Module. Values
     * sind wiederum Maps mit ("factoryClassName"=>Instance).
     */
    protected static Map factorys = new HashMap();

    /**
     * Liefert TcContentWorker.
     * Es wird ein bereits vorhandener geliefert, oder ein neuer geladen.
     *
     * Da der Worker dynamisch nach seinem Namen geladen wird kann eine
     * Exception auftreten, die nach obern weiter gegeben wird.
     *
     * @return Einen Worker mit dem entsprechenden Namen
     */
    public static TcContentWorker getContentWorker(TcModuleConfig config, String workerName, String requestID)
      throws WorkerCreationException {

        ContentWorkerDeclaration workerDeclaration = config.getContentWorkerDeclaration(workerName);
        if (null == workerDeclaration) {
            logger.error(
              Resources.getInstance().get("WORKERFACTORY_LOG_UNDECLARED_WORKER", requestID, workerName, config.getName()));
            throw new WorkerCreationException(
              Resources.getInstance().get("WORKERFACTORY_EXC_UNDECLARED_WORKER", workerName, config.getName()));
        }

        // Bei einem Singleton cachen wir die Instanz,
        if (workerDeclaration.isSingletonInstantiation()) {

            // Da jedes Modul einen eigenen Classloader besitzt müssen die
            // Worker unter diesem Classloader im Cache verwendet werden.
            ClassLoader moduleLoader = config.getClassLoader();
            Map moduleWorkers = (Map) workers.get(moduleLoader);
            if (null == moduleWorkers) {
                moduleWorkers = new HashMap();
                workers.put(moduleLoader, moduleWorkers);
            }

            if (!moduleWorkers.containsKey(workerName)) {
                TcContentWorker worker = getNewWorkerInstance(config, workerDeclaration);
                moduleWorkers.put(workerName, worker);
            }
            return (TcContentWorker) moduleWorkers.get(workerName);
        }
        // sonst erzeugen wir jedes mal eine neue.
        else {
            return getNewWorkerInstance(config, workerDeclaration);
        }
    }

    protected static TcContentWorker getNewWorkerInstance(TcModuleConfig config, ContentWorkerDeclaration workerDeclaration)
      throws WorkerCreationException {

        // Da jedes Modul einen eigenen Classloader besitzt müssen
        // auch die Factorys mit diesem Classloader geladen werden.
        // Nur so ist es möglich einem Modul eine eigene Factory hinzu zu fügen.
        ClassLoader moduleLoader = config.getClassLoader();

        Map moduleFactorys = (Map) factorys.get(moduleLoader);
        if (null == moduleFactorys) {
            moduleFactorys = new HashMap();
            factorys.put(moduleLoader, moduleFactorys);
        }

        SpecialWorkerFactory factory = (SpecialWorkerFactory) moduleFactorys.get(workerDeclaration.getFactory());
        if (null == factory) {
            try {
                logger.debug(Resources.getInstance().get("WORKERFACTORY_LOADING_FACTORY", workerDeclaration.getFactory()));
                factory = (SpecialWorkerFactory) moduleLoader.loadClass(workerDeclaration.getFactory()).newInstance();
            } catch (Exception reflectionException) {
                throw new WorkerCreationException(Resources.getInstance()
                  .get("WORKERFACTORY_EXC_LOADING_FACTORY", workerDeclaration.getFactory(),
                    workerDeclaration.getWorkerName()), reflectionException);
            }
            moduleFactorys.put(workerDeclaration.getFactory(), factory);
        }
        TcContentWorker worker = factory.createInstance(moduleLoader, workerDeclaration);
        worker.init(config);
        return worker;
    }
}
