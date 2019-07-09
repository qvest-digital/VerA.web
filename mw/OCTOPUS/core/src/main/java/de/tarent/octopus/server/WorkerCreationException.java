package de.tarent.octopus.server;
/**
 * Exception, für Fehler beim Erstellen einer neuen Worker-Instatnz
 *
 * @author <a href="mailto:mancke@mancke-software.de">Sebastian Mancke</a>, <b>tarent GmbH</b>
 * @see de.tarent.octopus.content.TcContentWorker
 */
public class WorkerCreationException extends Exception {
    /**
     * serialVersionUID = 46639696610147426L;
     */
    private static final long serialVersionUID = 46639696610147426L;

    public WorkerCreationException() {
        super();
    }

    public WorkerCreationException(String msg) {
        super(msg);
    }

    public WorkerCreationException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public WorkerCreationException(Throwable cause) {
        super(cause);
    }
}
