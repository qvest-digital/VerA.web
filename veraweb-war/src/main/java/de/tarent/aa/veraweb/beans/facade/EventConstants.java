/*
 * veraweb,
 * Veranstaltungsmanagment veraweb
 * Copyright (c) 2005-2007 tarent GmbH
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License,version 2
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 *
 * tarent GmbH., hereby disclaims all copyright
 * interest in the program 'veraweb'
 * Signature of Elmar Geese, 21 November 2007
 * Elmar Geese, CEO tarent GmbH.
 */

/*
 * $Id$
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
