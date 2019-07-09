package de.tarent.octopus.content;
import de.tarent.octopus.config.ContentWorkerDeclaration;
import de.tarent.octopus.resource.Resources;
import de.tarent.octopus.server.SpecialWorkerFactory;
import de.tarent.octopus.server.WorkerCreationException;
import lombok.extern.log4j.Log4j2;

/**
 * Instantiiert Worker nach der ReflectedWorkerWrapper Konvention.
 *
 * @author <a href="mailto:mancke@mancke-software.de">Sebastian Mancke</a>, <b>tarent GmbH</b>
 * @see TcContentWorker
 */
@Log4j2
public class ReflectedWorkerFactory implements SpecialWorkerFactory {
    private static Class[] emptyClassArray = new Class[] {};
    private static Object[] emptyObjectArray = new Object[] {};

    /**
     * Läd die als ImplementationSource angegebene Klasse und Kapselt sie mit einem ReflectedWorkerWrapper.
     *
     * @param workerDeclaration Beschreibung zur Instanziierung des Workers.
     */
    public TcContentWorker createInstance(ClassLoader classLoader, ContentWorkerDeclaration workerDeclaration)
      throws WorkerCreationException {
        try {
            logger.debug(Resources.getInstance()
              .get("WORKERFACTORY_LOADING_WORKER", getClass().getName(), workerDeclaration.getWorkerName(),
                workerDeclaration.getImplementationSource()));
            Class workerClass = classLoader.loadClass(workerDeclaration.getImplementationSource());
            return new TcReflectedWorkerWrapper(workerClass.getConstructor(emptyClassArray).newInstance(emptyObjectArray));
        } catch (Exception reflectionException) {
            throw new WorkerCreationException(Resources.getInstance()
              .get("WORKERFACTORY_EXC_LOADING_WORKER", getClass().getName(), workerDeclaration.getWorkerName(),
                workerDeclaration.getImplementationSource()), reflectionException);
        }
    }
}
