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
 * @see ContentWorker
 * @author Sebastian Mancke, tarent GmbH
 */
public class AnnotationWorkerFactory implements SpecialWorkerFactory {
	private static Logger logger = Logger.getLogger(AnnotationWorkerFactory.class.getName());

    /**
     * Liefert einen Worker entsprechend der workerDeclaration zurück.
     * Im Normalfall muss von der Factory nur die ImplementationSource berücksichtigt werden.
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
