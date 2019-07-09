package de.tarent.octopus.server;
/**
 * A Closeable is a Resource, which may be closed if not needed any longer.
 * This interace ist semanticaly the same as {@link java.io.Closeable} introduced with JDK 1.5.
 * <br><br>
 * Within the octopus, this interface may be used to safely close resources after the processing of a request.
 * This is done by a List of Closeable-Object within the OctopusContext. After the generation of the
 * octopus response, the close method is called for each object in this list. This will be done even if the request processing
 * will throw an exception.
 * Use the {@link OctopusContext#addCleanupCode(Closeable)} to add an Object to the list of closeable objects.
 *
 * @author <a href="mailto:sebastian@tarent.de">Sebastian Mancke</a>, <b>tarent GmbH</b>
 * @version 1.0
 */
public interface Closeable {
    /**
     * Closes the resource.
     *
     * @throws Exception This method may throw any exception, but should not rely on an proper handling by the calling code.
     */
    public void close();
}
