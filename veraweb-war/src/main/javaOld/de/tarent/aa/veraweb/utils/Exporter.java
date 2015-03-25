/**
 * veraweb, platform independent webservice-based event management
 * (Veranstaltungsmanagment VerA.web), is
 * Copyright © 2004-2008 tarent GmbH
 * Copyright © 2013 tarent solutions GmbH
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
package de.tarent.aa.veraweb.utils;

import java.io.IOException;

import de.tarent.aa.veraweb.beans.Person;
import de.tarent.octopus.beans.BeanException;

/**
 * Diese Schnittstelle ist f�r jede grunds�tzliche Exportvariante umzusetzen.
 * 
 * @author mikel
 */
public interface Exporter
{
	/**
	 * Diese Methode wird zu jeder zu exportierenden Person aufgerufen, �bergeben wird die Person als Zusammenstellung von
	 * {@link Person}. Sie f�gt dem Export eine Beschreibung der �bergebenen VerA.web-Person hinzu.
	 * 
	 * @param person
	 *          {@link Person}-Bean
	 */
	public void exportPerson(Person person) throws BeanException, IOException;

	/**
	 * Diese Methode wird zu Beginn eines Exports aufgerufen. In ihr kann etwa das Dokument mit einem Kopf zu schreiben
	 * begonnen werden.
	 * 
	 * @throws IOException
	 */
	public void startExport() throws IOException;

	/**
	 * Diese Methode wird zum Ende eines Exports aufgerufen. In ihr kann etwa das bisher gesammelte Dokument
	 * festgeschrieben werden.
	 * 
	 * @throws IOException
	 */
	public void endExport() throws IOException;

	/**
	 * Obwohl ein Exporter die zu exportierenden Personen nicht selbst bestimmt (diese werden ihm durch
	 * <code>exportPerson(Person)</code> �bergeben), ben�tigt er evtl. schon Informationen zur Einschr�nkung auf einen
	 * Mandanten. Z.B. muss beim CSV-Exporter schon vor <code>startExport()</code> auf einen Mandanten eingeschr�nkt
	 * werden k�nnen, damit keine mandantenfremden Spalten�berschriften erzeugt werden (Kategorien, die nicht zum
	 * Mandanten geh�ren)
	 * 
	 * @param orgUnitId
	 *          die MandantenID, auf die der Exporter beschr�nkt wird
	 */
	public void setOrgUnitId(Integer orgUnitId);

	/**
	 * Obwohl ein Exporter die zu exportierenden Personen nicht selbst bestimmt (diese werden ihm durch
	 * <code>exportPerson(Person)</code>) �bergeben), ben�tigt er evtl. Informationen zur Einschr�nkung auf bestimmte
	 * Kategorien. Z.B. muss beim CSV-Exporter schon vor <code>startExport()</code> auf die vom Benutzer im GUI gew�hlte
	 * Kategorie eingeschr�nkt werden k�nnen, damit andere Kategorien nicht als Spalten�berschriften erzeugt werden.
	 * 
	 * @param categoryId
	 *          KategorieId, auf die der Exporter beschr�nkt wird. <code>Null</code> = alle Kategorien, 0 = keine
	 *          Kategorie
	 */
	public void setCategoryId(Integer categoryId);
}
