package de.tarent.octopus.extensions;
/**
 * This implements a simple extension framework for
 * the Octopus application server. An extension has to be
 * implement this interface and can then be initialized
 * and called without relying on reflections.
 *
 * @author Michael Kleinhenz (m.kleinhenz@tarent.de)
 */
public interface OctopusExtension {
    /**
     * Initializes the extension. Runtime parameters are
     * encoded in the parameter.
     *
     * @param param Runtime parameters for this extension.
     */
    public void initialize(Object param);

    /**
     * Starts the extension. This should be called on
     * startup of regular server operation.
     */
    public void start();

    /**
     * Stops the extension. This should be called before
     * the server shuts down.
     */
    public void stop();
}
