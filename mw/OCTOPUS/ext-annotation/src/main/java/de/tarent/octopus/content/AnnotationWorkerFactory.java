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
	public TcContentWorker createInstance(
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
