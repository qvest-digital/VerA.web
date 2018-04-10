package de.tarent.octopus.content;
import de.tarent.octopus.config.ContentWorkerDeclaration;
import de.tarent.octopus.resource.Resources;
import de.tarent.octopus.server.SpecialWorkerFactory;
import de.tarent.octopus.server.WorkerCreationException;

/**
 * Instantiiert eine Groovy-Klasse, die nach dem ReflectedWorkerWrapper Prinzip arbeitet
 *
 *
 * @see TcContentWorker
 * @author <a href="mailto:mancke@mancke-software.de">Sebastian Mancke</a>, <b>tarent GmbH</b>
 */
public class GroovyWorkerFactory implements SpecialWorkerFactory {
    /**
     * Läd die als ImplementationSource angegebene Klasse und gibt sie gecastet TcContentWorker zurück.
     *
     * @param workerDeclaration Beschreibung zur Instanziierung des Workers.
     */
    public TcContentWorker createInstance(ClassLoader classLoader, ContentWorkerDeclaration workerDeclaration)
        throws WorkerCreationException {
        try {
            //GroovyClassLoader loader = new GroovyClassLoader(config.getClassLoader());
            //Object workerObject = loader.parseClass(new java.io.File("/home/asteban/tarent/octopus/modules/octopustest/webapp/OCTOPUS/classes/de/tarent/octopustest/GroovyTest.groovy")).newInstance();
            return null;
            //return new TcGroovyWorkerWrapper(workerObject);
        } catch (ClassCastException castException) {
            throw new WorkerCreationException(Resources.getInstance().get("WORKERFACTORY_CAST_EXC_LOADING_WORKER", getClass().getName(), workerDeclaration.getWorkerName(), workerDeclaration.getImplementationSource()));
        } catch (Exception reflectionException) {
            throw new WorkerCreationException(Resources.getInstance().get("WORKERFACTORY_EXC_LOADING_WORKER", getClass().getName(), workerDeclaration.getWorkerName(), workerDeclaration.getImplementationSource()), reflectionException);
        }
    }
}
