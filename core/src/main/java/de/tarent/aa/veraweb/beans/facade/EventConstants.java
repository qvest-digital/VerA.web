package de.tarent.aa.veraweb.beans.facade;
/**
 * Diese Schnittstelle stellt Konstanten für {@link de.tarent.aa.veraweb.beans.Event}-Instanzen
 * zur Verfügung.
 *
 * @author christoph
 */
public interface EventConstants {
    /**
     * Person mit Partner einladen
     */
    int TYPE_MITPARTNER = 1;

    /**
     * Person ohne Partner einladen
     */
    int TYPE_OHNEPARTNER = 2;

    /**
     * Nur den Partner der Person einladen
     */
    int TYPE_NURPARTNER = 3;

    /**
     * Status: Offen
     */
    int STATUS_OPEN = 0;

    /**
     * Status: Zusage
     */
    int STATUS_ACCEPT = 1;

    /**
     * Status: Absage
     */
    int STATUS_REFUSE = 2;

    String EVENT_TYPE_OPEN_EVENT = "Offene Veranstaltung";
}
