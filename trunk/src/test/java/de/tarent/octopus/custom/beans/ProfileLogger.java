/*
 * $Id: ProfileLogger.java,v 1.1 2007/06/20 11:56:52 christoph Exp $
 * 
 * Created on 18.11.2005
 */
package de.tarent.octopus.custom.beans;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * Diese Klasse stellt Methoden zum Loggen von Profiling-Informationen
 * zur Verfügung.
 * 
 * @author mikel
 */
public final class ProfileLogger {
    //
    // Konstruktoren
    //
    /**
     * Dieser Konstruktor setzt den Startpunkt auf jetzt.
     */
    public ProfileLogger() {
        super();
        last = System.currentTimeMillis();
    }
    
    //
    // öffentliche Methoden
    //
    /**
     * Diese Methode loggt die Millisekunden seit dem letzten Aufruf
     * mit dem übergebenen String als Information.
     * 
     * @param string Log-Eintrag-Info
     */
    public void log(final String string) {
        final long now = System.currentTimeMillis();
        logger.log(level, "[" + (now - last) + "ms] " + string);
        last = now;
    }

    //
    // geschützte Member
    //
    /** letzter Messpunkt */
    long last = 0;
    
    /** Log-Level */
    Level level = Level.INFO;
    
    /** Logger */
    Logger logger = Logger.getLogger(ProfileLogger.class.getName());
}
