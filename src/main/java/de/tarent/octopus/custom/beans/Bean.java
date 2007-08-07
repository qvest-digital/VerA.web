/*
 * Created on 08.02.2005
 */
package de.tarent.octopus.custom.beans;

import java.util.List;
import java.util.Set;

/**
 * @author Michael Klink, Alex Steeg, Christoph Jerolimov
 * @version 1.3
 */
public interface Bean {
	/**
	 * F�gt eine Nachricht der Bean-Fehlerliste hinzu.
	 * 
	 * @param message
	 */
	void addError(String message);

	/**
	 * Gibt ein nur-lese Liste mit Fehlern (Strings) zur�ck.
	 * 
	 * @return Fehlerliste
	 */
	List getErrors();

	/**
	 * Gibt true zur�ck wenn keine Fehler beim erzeugen der Bean
	 * aufgetreten sind, anderfalls false.
	 * 
	 * @return true wenn Bean fehlerfrei ist.
	 */
	boolean isCorrect();

	/**
	 * Gibt true zur�ck wenn das erzeugte der Bean
	 * ge�ndert wurde, anderfalls false.
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
	 * �berpr�ft das Bean auf innere Vollst�ndigkeit.
	 * 
	 * @throws BeanException
	 */
	void verify() throws BeanException;

	/**
	 * Gibt Liste mit den Field-Keys als String zur�ck. 
	 * 
	 * @return Liste mit Field-Keys als String
	 */
	Set getFields();

	/**
	 * Gibt den Inhalt eines Bean-Feldes zur�ck.
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
	 * Gibt den Typ eines Bean-Feldes zur�ck.
	 * 
	 * @param key
	 * @return Class des Bean-Fields
	 */
	Class getFieldClass(String key);
}
