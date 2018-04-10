package de.tarent.octopus.util;
/**
 * Interface für Exceptions, die noch weitere beinhalten
 */
public interface RootCauseException {
    public Throwable getRootCause();
}
