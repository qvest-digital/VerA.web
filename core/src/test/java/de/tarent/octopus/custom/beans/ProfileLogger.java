package de.tarent.octopus.custom.beans;
import lombok.extern.log4j.Log4j2;

/**
 * Diese Klasse stellt Methoden zum Loggen von Profiling-Informationen
 * zur Verfügung.
 *
 * @author mikel
 */
@Log4j2
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
    // Öffentliche Methoden
    //

    /**
     * Diese Methode loggt die Millisekunden seit dem letzten Aufruf
     * mit dem übergebenen String als Information.
     *
     * @param string Log-Eintrag-Info
     */
    public void log(final String string) {
        final long now = System.currentTimeMillis();
        logger.info("[" + (now - last) + "ms] " + string);
        last = now;
    }

    //
    // geschützte Member
    //
    /**
     * letzter Messpunkt
     */
    long last = 0;
}
