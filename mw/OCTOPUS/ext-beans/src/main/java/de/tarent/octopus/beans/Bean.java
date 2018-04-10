package de.tarent.octopus.beans;

import java.util.List;
import java.util.Set;

/**
 * @author Michael Klink, Alex Steeg, Christoph Jerolimov
 * @version 1.3
 */
public interface Bean {
	/**
	 * Fügt eine Nachricht der Bean-Fehlerliste hinzu.
	 *
	 * @param message
	 */
	void addError(String message);

	/**
	 * Gibt ein nur-lese Liste mit Fehlern (Strings) zurück.
	 *
	 * @return Fehlerliste
	 */
	List getErrors();

	/**
	 * Gibt true zurück wenn keine Fehler beim erzeugen der Bean
	 * aufgetreten sind, anderfalls false.
	 *
	 * @return true wenn Bean fehlerfrei ist.
	 */
	boolean isCorrect();

	/**
	 * Gibt true zurück wenn das erzeugte der Bean
	 * geändert wurde, anderfalls false.
	 *
	 * @return true wenn Bean fehlerfrei ist.
	 */
	boolean isModified();

	/**
	 * Setzt das Modified-Flag.
	 *
	 * @param modified
	 */
	void setModified(boolean modified);

	/**
	 * Überprüft das Bean auf innere Vollständigkeit.
	 *
	 * @throws BeanException
	 */
	void verify() throws BeanException;

	/**
	 * Gibt Liste mit den Field-Keys als String zurück.
	 *
	 * @return Liste mit Field-Keys als String
	 */
	Set getFields();

	/**
	 * Gibt den Inhalt eines Bean-Feldes zurück.
	 *
	 * @param key
	 * @return Inhalt
	 */
	Object getField(String key) throws BeanException;

	/**
	 * Setzt den Inhalt eines Bean-Feldes.
	 *
	 * @param key
	 * @param value
	 */
	void setField(String key, Object value) throws BeanException;

	/**
	 * Gibt den Typ eines Bean-Feldes zurück.
	 *
	 * @param key
	 * @return Class des Bean-Fields
	 */
	Class getFieldClass(String key);
}
