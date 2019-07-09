package de.tarent.octopus.content;
/**
 * Interface für Wrapperklassen von Workern,
 * um eine einheitliche Schnittstelle für das Liefern des Delegate zu bieten.
 *
 * @author Sebastian Mancke
 */
public interface DelegatingWorker {
    /**
     * Returns the target Worker
     */
    public Object getWorkerDelegate();
}
