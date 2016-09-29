/**
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
package de.tarent.aa.veraweb.beans;

import de.tarent.octopus.beans.BeanException;

import java.util.Date;

/**
 * Diese Klasse erweitert die {@link de.tarent.octopus.beans.MapBean}
 * um eine Funktion zum Setzen von Historisierungsfeldern.
 *
 * @author mikel
 */
public class AbstractHistoryBean extends AbstractBean {
	//
	// Konstanten
	//
	/** <code>actor</code>-Vorgabewert bei {@link #updateHistoryFields(Date, String)} */
	public final static String HISTORY_DEFAULT_ACTOR = "SYSTEM";
	/** Erzeugungsdatum-Feldname bei {@link #updateHistoryFields(Date, String)} */
	public final static String HISTORY_FIELD_CREATED = "created";
	/** Erzeuger-Feldname bei {@link #updateHistoryFields(Date, String)} */
	public final static String HISTORY_FIELD_CREATED_BY = "createdby";
	/** Änderungsdatum-Feldname bei {@link #updateHistoryFields(Date, String)} */
	public final static String HISTORY_FIELD_CHANGED = "changed";
	/** Änderer-Feldname bei {@link #updateHistoryFields(Date, String)} */
	public final static String HISTORY_FIELD_CHANGED_BY = "changedby";

	/**
	 * Diese Methode aktualisiert Historienfelder der Bohne. Dies bedeutet, dass
	 * --- falls vorhanden aber noch nicht gesetzt --- Erzeuger und Erzeugungsdatum
	 * eingetragen und --- falls vorhanden --- Änderer und Änderungsdatum aktualisiert
	 * werden
	 *
	 * @param date Datum der Änderung --- Default ist <i>jetzt</i>
	 * @param actor Namen des Ändernden --- Default ist nicht vorgegeben
	 * @throws BeanException
	 */
	public void updateHistoryFields(Date date, String actor) throws BeanException {
		if (date == null)
			date = new Date();
		if (actor == null)
			actor = HISTORY_DEFAULT_ACTOR;
		if (containsKey(HISTORY_FIELD_CREATED))
			if (getField(HISTORY_FIELD_CREATED) == null)
				setField(HISTORY_FIELD_CREATED, date);
		if (containsKey(HISTORY_FIELD_CREATED_BY))
			if (getField(HISTORY_FIELD_CREATED_BY) == null || getField(HISTORY_FIELD_CREATED_BY).toString().trim().length() == 0)
				setField(HISTORY_FIELD_CREATED_BY, actor);
		if (containsKey(HISTORY_FIELD_CHANGED))
			setField(HISTORY_FIELD_CHANGED, date);
		if (containsKey(HISTORY_FIELD_CHANGED_BY))
			setField(HISTORY_FIELD_CHANGED_BY, actor);
    }
}
