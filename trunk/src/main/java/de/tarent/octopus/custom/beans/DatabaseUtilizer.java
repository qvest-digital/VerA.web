/*
 * Created on 22.08.2005
 */
package de.tarent.octopus.custom.beans;

/**
 * Diese Schnittstelle beschreibt allgemein Objekte, die eine
 * {@link Database}-Instanz ben�tigen.
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
