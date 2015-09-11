/**
 * veraweb, platform independent webservice-based event management
 * (Veranstaltungsmanagment VerA.web), is
 * Copyright © 2004–2008 tarent GmbH
 * Copyright © 2013–2015 tarent solutions GmbH
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see: http://www.gnu.org/licenses/
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

	static public final String EVENT_TYPE_OPEN_EVENT = "Offene Veranstaltung";
}
