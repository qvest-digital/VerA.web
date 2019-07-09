package de.tarent.octopus.content;

/*-
 * Veranstaltungsmanagement VerA.web, comprised of…
 * VerA.web, platform-independent webservice-based event management
 * tarent-commons, a set of common components and solutions
 * tarent-contact, platform-independent webservice-based contact management
 * tarent-database, jdbc database library
 * tarent-doctor, Document Generation Platform
 * tarent-octopus, Webservice Data Integrator and Application Server
 * … is newly MIT-licenced and Copyright
 *  © 2018 Атанас Александров (sirakov@gmail.com)
 *  © 2014, 2015, 2016, 2017 Атанас Александров (a.alexandrov@tarent.de)
 *  © 2013 Иванка Александрова (i.alexandrova@tarent.de)
 *  © 2005, 2006, 2007 asteban (s.mancke@tarent.de)
 *  © 2013 Patrick Apel (p.apel@tarent.de)
 *  © 2016 Eugen Auschew (e.auschew@tarent.de)
 *  © 2013 Andrei Boulgakov (a.boulgakov@tarent.de)
 *  © 2013 Valentin But (v.but@tarent.de)
 *  © 2016 Lukas Degener (l.degener@tarent.de)
 *  © 2017 Axel Dirla (a.dirla@tarent.de)
 *  © 2015 Julian Drawe (j.drawe@tarent.de)
 *  © 2009 Sven Frommeyer (s.frommeyer@tarent.de)
 *  © 2014, 2018 Dominik George (d.george@tarent.de)
 *  © 2013 Martin Gernhardt (m.gernhardt@tarent.de)
 *  © 2013 Sascha Girrulat (s.girrulat@tarent.de)
 *  © 2007, 2008 David Goemans (d.goemans@tarent.de)
 *  © 2018 Christian Gorski (c.gorski@tarent.de)
 *  © 2015 Viktor Hamm (v.hamm@tarent.de)
 *  © 2013 Katja Hapke (k.hapke@tarent.de)
 *  © 2006, 2007, 2010, 2013 Hendrik Helwich (h.helwich@tarent.de)
 *  © 2018 Thomas Hensel (t.hensel@tarent.de)
 *  © 2018, 2019 Benedict Hoeger (b.hoeger@tarent.de)
 *  © 2018, 2019 Titian Horvath (t.horvath@tarent.de)
 *  © 2005, 2006, 2007, 2008 Christoph Jerolimov (jerolimov@gmx.de)
 *  © 2018, 2019 Timo Kanera (t.kanera@tarent.de)
 *  © 2006 Philipp Kirchner (p.kirchner@tarent.de)
 *  © 2008, 2009, 2010 Carsten Klein (c.klein@tarent.de)
 *  © 2006 Michael Kleinhenz (m.kleinhenz@tarent.de)
 *  © 2006 Michael Klink (m.klink@tarent.de)
 *  © 2007 Fabian Köster (f.koester@tarent.de)
 *  © 2006, 2014 Martin Ley (m.ley@tarent.de)
 *  © 2007 Alex Maier (a.maier@tarent.de)
 *  © 2014, 2015 Max Marche (m.marche@tarent.de)
 *  © 2007 Jan Meyer (jan@evolvis.org)
 *  © 2007, 2013, 2014, 2015, 2016, 2017, 2018, 2019
 *     mirabilos (t.glaser@tarent.de)
 *  © 2016 Cristian Molina (c.molina@tarent.de)
 *  © 2006, 2007 Jens Neumaier (j.neumaier@tarent.de)
 *  © 2006 Nils Neumaier (n.neumaier@tarent.de)
 *  © 2018 Yorka Neumann (y.neumann@tarent.de)
 *  © 2017 Michael Nienhaus (m.nienhaus@tarent.de)
 *  © 2013 Claudia Nuessle (c.nuessle@tarent.de)
 *  © 2014, 2015 Jon Nuñez Alvarez (j.nunez-alvarez@tarent.de)
 *  © 2016 Jens Oberender (j.oberender@tarent.de)
 *  © 2016, 2017 Miluška Pech (m.pech@tarent.de)
 *  © 2007, 2008, 2009 Martin Pelzer (m.pelzer@tarent.de)
 *  © 2008, 2009 Christian Preilowski (c.thiel@tarent.de)
 *  © 2013 Marc Radel (m.radel@tarent.de)
 *  © 2013 Sebastian Reimers (s.reimers@tarent.de)
 *  © 2015 Charbel Saliba (c.saliba@tarent.de)
 *  © 2006, 2008, 2009, 2010 Thomas Schmitz (t.schmitz@tarent.de)
 *  © 2013 Volker Schmitz (v.schmitz@tarent.de)
 *  © 2014 Sven Schumann (s.schumann@tarent.de)
 *  © 2007 Robert Schuster (r.schuster@tarent.de)
 *  © 2014 Sevilay Temiz (s.temiz@tarent.de)
 *  © 2013 Kevin Viola Schmitz (k.schmitz@tarent.de)
 *  © 2008, 2015 Stefan Walenda (s.walenda@tarent.de)
 *  © 2015, 2016, 2017 Max Weierstall (m.weierstall@tarent.de)
 *  © 2013 Rebecca Weinz (r.weinz@tarent.de)
 *  © 2015, 2016 Stefan Weiz (s.weiz@tarent.de)
 *  © 2015, 2016 Tim Zimmer (t.zimmer@tarent.de)
 * and older code, Copyright © 2001–2008 ⮡ tarent GmbH and contributors.
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

import de.tarent.octopus.config.ContentWorkerDeclaration;
import de.tarent.octopus.config.TcModuleConfig;
import de.tarent.octopus.resource.Resources;
import de.tarent.octopus.server.SpecialWorkerFactory;
import de.tarent.octopus.server.WorkerCreationException;
import lombok.extern.log4j.Log4j2;

import java.util.HashMap;
import java.util.Map;

/**
 * Factory Klasse mit statischen Methoden zur Lieferung von WorkerInstanzen
 *
 * @author <a href="mailto:mancke@mancke-software.de">Sebastian Mancke</a>, <b>tarent GmbH</b>
 * @see TcContentWorker
 */
@Log4j2
public class TcContentWorkerFactory {
    /**
     * Map mit TcContentWorkern. Keys der Map sind die ClassLoader der Module. Values sind wiederum Maps mit
     * („workername“ ⇒ Instance).
     * Dadurch werden die Worker gepuffert und müssen nicht für jede Anfrage neu erstellt werden.
     */
    protected static Map workers = new HashMap();

    /**
     * Map mit den Speziellen Factorys zur Instanziierung von Workern. Keys der Map sind die ClassLoader der Module.
     * Values sind wiederum Maps mit („factoryClassName“ ⇒ Instance).
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
