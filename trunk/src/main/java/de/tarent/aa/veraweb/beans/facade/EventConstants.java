/*
 * $Id: EventConstants.java,v 1.1 2007/06/20 11:56:51 christoph Exp $
 */
package de.tarent.aa.veraweb.beans.facade;

/**
 * Diese Schnittstelle stellt Konstanten für {@link de.tarent.aa.veraweb.beans.Event}-Instanzen 
 * zur Verfügung.
 * 
 * @author christoph
 */
public interface EventConstants {
	/** Person mit Partner einladen */
	static public final int TYPE_MITPARTNER = 1;

	/** Person ohne Partner einladen */
	static public final int TYPE_OHNEPARTNER = 2;

	/** Nur den Partner der Person einladen */
	static public final int TYPE_NURPARTNER = 3;

	/** Status: Offen */
	static public final int STATUS_OPEN = 0;

	/** Status: Zusage */
	static public final int STATUS_ACCEPT = 1;

	/** Status: Absage */
	static public final int STATUS_REFUSE = 2;
}
