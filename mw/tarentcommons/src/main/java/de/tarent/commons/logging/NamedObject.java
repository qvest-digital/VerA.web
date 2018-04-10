package de.tarent.commons.logging;

/**
 * Contains an object associated with a name.
 * Used for parameters and variables in class {@link MethodCall}.
 *
 * @author Tim Steffens
 */
public class NamedObject {
	private String name;
	private Object value;

	public NamedObject(String name, Object value) {
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public String toString() {
		return this.name + " = " + this.value;
	}
}
