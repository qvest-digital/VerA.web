package de.tarent.aa.veraweb.beans.facade;

/*-
 * veraweb, platform independent webservice-based event management
 * (Veranstaltungsmanagment VerA.web), is
 * Copyright © 2004–2008 tarent GmbH
 * Copyright © 2013–2016 tarent solutions GmbH
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
/**
 * Diese Schnittstelle stellt Konstanten für {@link de.tarent.aa.veraweb.beans.Event}-Instanzen
 * zur Verfügung.
 *
 * @author christoph
 */
public interface EventConstants {
	/** Person mit Partner einladen */
	int TYPE_MITPARTNER = 1;

	/** Person ohne Partner einladen */
	int TYPE_OHNEPARTNER = 2;

	/** Nur den Partner der Person einladen */
	int TYPE_NURPARTNER = 3;

	/** Status: Offen */
	int STATUS_OPEN = 0;

	/** Status: Zusage */
	int STATUS_ACCEPT = 1;

	/** Status: Absage */
	int STATUS_REFUSE = 2;

	String EVENT_TYPE_OPEN_EVENT = "Offene Veranstaltung";
}
