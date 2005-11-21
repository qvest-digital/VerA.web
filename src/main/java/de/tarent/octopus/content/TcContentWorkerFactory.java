/* $Id: TcContentWorkerFactory.java,v 1.1.1.1 2005/11/21 13:33:37 asteban Exp $
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

import de.tarent.octopus.config.TcModuleConfig;
import de.tarent.octopus.resource.Resources;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/** 
 * Factory Klasse mit statischen Methoden zur Lieferung von WorkerInstanzen
 * 
 * @see TcContentWorker
 * @author <a href="mailto:mancke@mancke-software.de">Sebastian Mancke</a>, <b>tarent GmbH</b>
 */
public class TcContentWorkerFactory {
    private static Logger logger = Logger.getLogger(TcContentWorkerFactory.class.getName());

    /** Map mit TcContentWorkern.
     *  Dadurch werden die Worker gepuffert und müssen nicht für jede Anfrage neu erstellt werden.
     */
    private static Map workers = new HashMap();

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
        throws
            ClassNotFoundException,
            InstantiationException,
            IllegalAccessException,
            ClassCastException,
            TcContentProzessException {
        ClassLoader moduleLoader = config.getClassLoader();
        if (!workers.containsKey(moduleLoader))
            workers.put(moduleLoader, new HashMap());
        Map moduleWorkers = (Map) workers.get(moduleLoader);
        if (!moduleWorkers.containsKey(workerName)) {
            String workerClassName = null;
            if ("TcPutParams".equals(workerName))
                workerClassName = "de.tarent.octopus.content.TcPutParams";
            else if (config.getDeclaredContentWorkers().containsKey(workerName))
                workerClassName = config.getDeclaredContentWorkers().get(workerName).toString();
            else if (config.getDeclaredContentWorkers().containsValue(workerName)) {
                workerClassName = workerName;
                logger.warning(Resources.getInstance().get("WORKERFACTORY_LOG_DEPRECATED_USE", requestID, workerName, config.getName()));
            } else
                logger.severe(Resources.getInstance().get("WORKERFACTORY_LOG_UNDECLARED_WORKER", requestID, workerName, config.getName()));
            if (workerClassName == null)
                throw new TcContentProzessException(Resources.getInstance().get("WORKERFACTORY_EXC_UNDECLARED_WORKER", workerName, config.getName()));
            Object workerClass = moduleLoader.loadClass(workerClassName).newInstance();
            TcContentWorker worker;
            if (workerClass instanceof TcContentWorker)
                worker = (TcContentWorker)workerClass;
            else
                worker = new TcReflectedWorkerWrapper(workerClass);

            worker.init(config);
            moduleWorkers.put(workerName, worker);
        }
        return (TcContentWorker) moduleWorkers.get(workerName);
    }
}