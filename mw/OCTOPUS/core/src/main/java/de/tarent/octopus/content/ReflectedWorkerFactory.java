package de.tarent.octopus.content;
import org.apache.commons.logging.Log;

import de.tarent.octopus.config.ContentWorkerDeclaration;
import de.tarent.octopus.logging.LogFactory;
import de.tarent.octopus.resource.Resources;
import de.tarent.octopus.server.SpecialWorkerFactory;
import de.tarent.octopus.server.WorkerCreationException;

/**
 * Instantiiert Worker nach der ReflectedWorkerWrapper Konvention.
 *
 *
 * @see TcContentWorker
 * @author <a href="mailto:mancke@mancke-software.de">Sebastian Mancke</a>, <b>tarent GmbH</b>
 */
public class ReflectedWorkerFactory implements SpecialWorkerFactory {

    private static Log logger = LogFactory.getLog(ReflectedWorkerFactory.class);
    private static Class[] emptyClassArray = new Class[]{};
    private static Object[] emptyObjectArray = new Object[]{};

    /**
     * Läd die als ImplementationSource angegebene Klasse und Kapselt sie mit einem ReflectedWorkerWrapper.
     *
     * @param workerDeclaration Beschreibung zur Instanziierung des Workers.
     */
    public TcContentWorker createInstance(ClassLoader classLoader, ContentWorkerDeclaration workerDeclaration)
        throws WorkerCreationException {
        try {
            logger.debug(Resources.getInstance().get("WORKERFACTORY_LOADING_WORKER", getClass().getName(), workerDeclaration.getWorkerName(), workerDeclaration.getImplementationSource()));
            Class workerClass = classLoader.loadClass(workerDeclaration.getImplementationSource());
            return new TcReflectedWorkerWrapper(workerClass.getConstructor(emptyClassArray).newInstance(emptyObjectArray));
        } catch (Exception reflectionException) {
            throw new WorkerCreationException(Resources.getInstance().get("WORKERFACTORY_EXC_LOADING_WORKER", getClass().getName(), workerDeclaration.getWorkerName(), workerDeclaration.getImplementationSource()), reflectionException);
        }
    }
}
