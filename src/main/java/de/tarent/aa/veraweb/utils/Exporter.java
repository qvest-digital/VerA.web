/*
 * $Id: Exporter.java,v 1.1 2007/06/20 11:56:52 christoph Exp $
 * 
 * Created on 14.06.2005
 */
package de.tarent.aa.veraweb.utils;

import java.io.IOException;

import de.tarent.aa.veraweb.beans.Person;
import de.tarent.octopus.custom.beans.BeanException;

/**
 * Diese Schnittstelle ist für jede grundsätzliche Exportvariante umzusetzen.
 * 
 * @author mikel
 */
public interface Exporter
{
	/**
	 * Diese Methode wird zu jeder zu exportierenden Person aufgerufen, übergeben wird die Person als Zusammenstellung von
	 * {@link Person}. Sie fügt dem Export eine Beschreibung der übergebenen VerA.web-Person hinzu.
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
	 * <code>exportPerson(Person)</code> übergeben), benötigt er evtl. schon Informationen zur Einschränkung auf einen
	 * Mandanten. Z.B. muss beim CSV-Exporter schon vor <code>startExport()</code> auf einen Mandanten eingeschränkt
	 * werden können, damit keine mandantenfremden Spaltenüberschriften erzeugt werden (Kategorien, die nicht zum
	 * Mandanten gehören)
	 * 
	 * @param orgUnitId
	 *          die MandantenID, auf die der Exporter beschränkt wird
	 */
	public void setOrgUnitId(Integer orgUnitId);

	/**
	 * Obwohl ein Exporter die zu exportierenden Personen nicht selbst bestimmt (diese werden ihm durch
	 * <code>exportPerson(Person)</code>) übergeben), benötigt er evtl. Informationen zur Einschränkung auf bestimmte
	 * Kategorien. Z.B. muss beim CSV-Exporter schon vor <code>startExport()</code> auf die vom Benutzer im GUI gewählte
	 * Kategorie eingeschränkt werden können, damit andere Kategorien nicht als Spaltenüberschriften erzeugt werden.
	 * 
	 * @param categoryId
	 *          KategorieId, auf die der Exporter beschränkt wird. <code>Null</code> = alle Kategorien, 0 = keine
	 *          Kategorie
	 */
	public void setCategoryId(Integer categoryId);
}
