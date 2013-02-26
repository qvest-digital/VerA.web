/**
 * veraweb, platform independent webservice-based event management
 * (Veranstaltungsmanagment VerA.web), is
 * Copyright © 2004-2008 tarent GmbH
 * Copyright © 2013 tarent solutions GmbH
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License, version 2, as
 * published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see: http://www.gnu.org/licenses/
 */
package de.tarent.octopus.custom.beans;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * Diese Klasse stellt Methoden zum Loggen von Profiling-Informationen
 * zur Verf�gung.
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
    // �ffentliche Methoden
    //
    /**
     * Diese Methode loggt die Millisekunden seit dem letzten Aufruf
     * mit dem �bergebenen String als Information.
     * 
     * @param string Log-Eintrag-Info
     */
    public void log(final String string) {
        final long now = System.currentTimeMillis();
        logger.log(level, "[" + (now - last) + "ms] " + string);
        last = now;
    }

    //
    // gesch�tzte Member
    //
    /** letzter Messpunkt */
    long last = 0;
    
    /** Log-Level */
    Level level = Level.INFO;
    
    /** Logger */
    Logger logger = Logger.getLogger(ProfileLogger.class.getName());
}
