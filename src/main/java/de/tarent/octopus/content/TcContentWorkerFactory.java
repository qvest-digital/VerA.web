/* $Id: TcContentWorkerFactory.java,v 1.4 2007/01/02 09:51:19 christoph Exp $
 * 
 * tarent-octopus, Webservice Data Integrator and Applicationserver
 * Copyright (C) 2002 tarent GmbH
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 * tarent GmbH., hereby disclaims all copyright
 * interest in the program 'tarent-octopus'
 * (which makes passes at compilers) written
 * by Sebastian Mancke and Michael Klink.
 * signature of Elmar Geese, 1 June 2002
 * Elmar Geese, CEO tarent GmbH
 */

package de.tarent.octopus.content;

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
 * @see TcContentWorker
 * @author <a href="mailto:mancke@mancke-software.de">Sebastian Mancke</a>, <b>tarent GmbH</b>
 */
public class TcContentWorkerFactory {
    private static Log logger = LogFactory.getLog(TcContentWorkerFactory.class);

    /** Map mit TcContentWorkern. Keys der Map sind die ClassLoader der Module. Values sind wiederum Maps mit ("workername"=>Instance).
     *  Dadurch werden die Worker gepuffert und müssen nicht für jede Anfrage neu erstellt werden.
     */
    protected static Map workers = new HashMap();

    /** Map mit den Speziellen Factorys zur Instanziierung von Workern. Keys der Map sind die ClassLoader der Module. Values sind wiederum Maps mit ("factoryClassName"=>Instance).
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
            logger.error(Resources.getInstance().get("WORKERFACTORY_LOG_UNDECLARED_WORKER", requestID, workerName, config.getName()));
            throw new WorkerCreationException(Resources.getInstance().get("WORKERFACTORY_EXC_UNDECLARED_WORKER", workerName, config.getName()));
        }

        // Bei einem Singleton cachen wir die Instanz, 
        if (workerDeclaration.isSingletonInstantiation()) {

            // Da jedes Modul einen eigenen Classloader besitzt müssen die 
            // Worker unter diesem Classloader im Cache verwendet werden.
            ClassLoader moduleLoader = config.getClassLoader();
            Map moduleWorkers = (Map)workers.get(moduleLoader);
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

        Map moduleFactorys = (Map)factorys.get(moduleLoader);
        if (null == moduleFactorys) {
            moduleFactorys = new HashMap();
            factorys.put(moduleLoader, moduleFactorys);
        }        
        
        SpecialWorkerFactory factory = (SpecialWorkerFactory)moduleFactorys.get(workerDeclaration.getFactory());
        if (null == factory) {
            try {
                logger.debug(Resources.getInstance().get("WORKERFACTORY_LOADING_FACTORY", workerDeclaration.getFactory()));
                factory = (SpecialWorkerFactory)moduleLoader.loadClass(workerDeclaration.getFactory()).newInstance();
            } catch (Exception reflectionException) {
                throw new WorkerCreationException(Resources.getInstance().get("WORKERFACTORY_EXC_LOADING_FACTORY", workerDeclaration.getFactory(), workerDeclaration.getWorkerName()), reflectionException);
            }
            moduleFactorys.put(workerDeclaration.getFactory(), factory);
        }
        TcContentWorker worker = factory.createInstance(moduleLoader, workerDeclaration);
        worker.init(config);
        return worker;
    }
}
