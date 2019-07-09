package de.tarent.octopus.server;
import de.tarent.octopus.config.ContentWorkerDeclaration;
import de.tarent.octopus.content.TcContentWorker;

/**
 * Schnittstelle für Factorys, die ContentWorker nach unterschiedlichen
 * Verfahren instantiieren.
 *
 * @author <a href="mailto:mancke@mancke-software.de">Sebastian Mancke</a>, <b>tarent GmbH</b>
 * @see TcContentWorker
 */
public interface SpecialWorkerFactory {
    /**
     * Liefert einen Worker entsprechend der workerDeclaration zurück.
     * Im Normalfall muss von der Factory nur die ImplementationSource berücksichtigt werden.
     *
     * @param classLoader       Octopus Classloader fuer Worker.
     * @param workerDeclaration Beschreibung zur Instanziierung des Workers.
     */
    public TcContentWorker createInstance(ClassLoader classLoader, ContentWorkerDeclaration workerDeclaration)
      throws WorkerCreationException;
}
