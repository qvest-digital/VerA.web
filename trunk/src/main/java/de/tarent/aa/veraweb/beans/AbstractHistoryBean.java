package de.tarent.aa.veraweb.beans;

import java.util.Date;

import de.tarent.octopus.custom.beans.BeanException;

/**
 * Diese Klasse erweitert die {@link de.tarent.octopus.custom.beans.MapBean}
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
