package de.tarent.commons.datahandling;

/**
 * Example class that inherits BeanMap for BeanMapTest
 * @author tim
 *
 */
public class ConcreteBeanMap extends BeanMap {

	private String anAttribute;
	private String aReadonlyAttribute;
	private String aWriteonlyAttribute;

	public ConcreteBeanMap() {
		anAttribute = "anAttributeValue";
		aReadonlyAttribute = "aReadonlyAttributeValue";
		aWriteonlyAttribute = "aWriteonlyAttributeValue";
	}

	public String getAnAttribute() {
		return anAttribute;
	}

	public void setAnAttribute(String anAttribute) {
		this.anAttribute = anAttribute;
	}

	public String getAReadonlyAttribute() {
		return aReadonlyAttribute;
	}

	public void setAWriteonlyAttribute(String writeonlyAttribute) {
		aWriteonlyAttribute = writeonlyAttribute;
	}
}
