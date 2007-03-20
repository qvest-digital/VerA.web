/* $Id: AnnotationWorkerFactory.java,v 1.5 2007/03/20 14:46:36 amaier Exp $
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
 * by Sebastian Mancke
 * signature of Elmar Geese, 1 June 2002
 * Elmar Geese, CEO tarent GmbH
 */

package de.tarent.octopus.content;

import de.tarent.octopus.config.ContentWorkerDeclaration;
import de.tarent.octopus.resource.Resources;

import java.util.logging.Level;
import java.util.logging.Logger;
import de.tarent.octopus.server.WorkerCreationException;
import de.tarent.octopus.server.SpecialWorkerFactory;

import de.tarent.octopus.content.annotation.AnnotationWorkerWrapper;

/**
 * Instantiiert AnnotationWorker nach der ReflectedWorkerWrapper Konvention.
 * 
 * @see TcContentWorker
 * @author Sebastian Mancke, tarent GmbH
 */
public class AnnotationWorkerFactory implements SpecialWorkerFactory {
	private static Logger logger = Logger.getLogger(AnnotationWorkerFactory.class.getName());

    /**
     * Liefert einen Worker entsprechend der workerDeclaration zur�ck.
     * Im Normalfall muss von der Factory nur die ImplementationSource ber�cksichtigt werden.
     * 
     * @param classLoader Octopus Classloader fuer Worker.
     * @param workerDeclaration Beschreibung zur Instanziierung des Workers.
     */
	public ContentWorker createInstance(
			ClassLoader classLoader,
			ContentWorkerDeclaration workerDeclaration)
			throws WorkerCreationException {
		
		try {
			if (logger.isLoggable(Level.FINE))
				logger.fine(Resources.getInstance().get("WORKERFACTORY_LOADING_WORKER",
						getClass().getName(),
						workerDeclaration.getWorkerName(),
						workerDeclaration.getImplementationSource()));
			
			Class workerClass = classLoader.loadClass(workerDeclaration.getImplementationSource());
			return new AnnotationWorkerWrapper(workerClass.newInstance());
		} catch (Exception reflectionException) {
			throw new WorkerCreationException(Resources.getInstance().get(
					"WORKERFACTORY_EXC_LOADING_WORKER",
					getClass().getName(),
					workerDeclaration.getWorkerName(),
					workerDeclaration.getImplementationSource()),
					reflectionException);
		}
	}
}
