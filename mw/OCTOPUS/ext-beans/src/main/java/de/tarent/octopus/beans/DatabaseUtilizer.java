package de.tarent.octopus.beans;
/**
 * Diese Schnittstelle beschreibt allgemein Objekte, die eine
 * {@link Database}-Instanz benötigen.
 *
 * @author mikel
 */
public interface DatabaseUtilizer {
    /**
     * Die zu nutzende Datenbank<br>
     * TODO: Umstellen auf Nutzung eines {@link ExecutionContext}s
     */
    void setDatabase(Database database);

    /**
     * Die zu nutzende Datenbank<br>
     * TODO: Umstellen auf Nutzung eines {@link ExecutionContext}s
     */
    Database getDatabase();
}
