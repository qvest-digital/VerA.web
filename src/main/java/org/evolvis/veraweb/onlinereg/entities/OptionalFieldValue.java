package org.evolvis.veraweb.onlinereg.entities;

import org.evolvis.veraweb.onlinereg.entities.OptionalField;

/**
 * This class represents a union of {@link OptionalField} and the typed value of an guest.
 *
 * @author Sven Schumann <s.schumann@tarent.de>
 */
public class OptionalFieldValue extends OptionalField {

	public OptionalFieldValue() {

	}

	public OptionalFieldValue(OptionalField field, String value) {
		this.setPk(field.getPk());
		this.setFk_event(field.getFk_event());
		this.setLabel(field.getLabel());
		this.setValue(value);
	}

	private String value;

	public void setValue(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
