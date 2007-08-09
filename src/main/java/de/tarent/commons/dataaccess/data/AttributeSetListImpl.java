package de.tarent.commons.dataaccess.data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AttributeSetListImpl implements AttributeSetList {
	private final List content;

	public AttributeSetListImpl() {
		this.content = new ArrayList();
	}

	public AttributeSetListImpl(List content) {
		this.content = new ArrayList(content);
	}

	public int size() {
		return content.size();
	}

	public boolean isEmpty() {
		return content.isEmpty();
	}

	public AttributeSet get(int index) {
		return (AttributeSet) content.get(index);
	}

	public void add(AttributeSet attributeSet) {
		content.add(attributeSet);
	}

	public Iterator iterator() {
		return content.iterator();
	}

	public List asList() {
		return new ArrayList(content);
	}

	public String toString() {
		return content.toString();
	}
}
