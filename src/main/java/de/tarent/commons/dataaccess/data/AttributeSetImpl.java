/*
 * tarent commons,
 * a set of common components and solutions
 * Copyright (c) 2006-2007 tarent GmbH
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License,version 2
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 *
 * tarent GmbH., hereby disclaims all copyright
 * interest in the program 'tarent commons'
 * Signature of Elmar Geese, 14 June 2007
 * Elmar Geese, CEO tarent GmbH.
 */

package de.tarent.commons.dataaccess.data;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class AttributeSetImpl implements AttributeSet {
	private final Map content = new LinkedHashMap();
	private final List listener = new LinkedList();

	public List getAttributeNames() {
		return Arrays.asList(content.keySet().toArray());
	}

	public boolean containsAttribute(String attributeName) {
		return content.containsKey(attributeName);
	}

	public void setAttribute(String attributeName, Object attributeValue) {
		Object oldValue = content.put(attributeName, attributeValue);
		if (!listener.isEmpty())
			for (Iterator it = listener.iterator(); it.hasNext(); )
				((AttributeListener) it.next()).handleAttributeChange(
						attributeName,
						oldValue,
						attributeValue);
	}

	public Object getAttribute(String attributeName) {
		return content.get(attributeName);
	}

	public void addAttributeListener(AttributeListener attributeListener) {
		listener.add(attributeListener);
	}

	public void removeAttributeListener(AttributeListener attributeListener) {
		listener.remove(attributeListener);
	}

	public int compareTo(Object other) {
		if (other instanceof AttributeSet) {
			AttributeSet otherSet = (AttributeSet) other;
			
			if (content.size() < otherSet.getAttributeNames().size())
				return -1;
			else if (content.size() > otherSet.getAttributeNames().size())
				return 1;
			
			for (Iterator it = content.entrySet().iterator(); it.hasNext(); ) {
				Map.Entry entry = (Map.Entry) it.next();
				Object otherValue = otherSet.getAttribute((String) entry.getKey());
				if (entry.getValue() == null && otherValue == null)
					continue;
				else if (entry.getValue() == null && otherValue != null)
					return -1;
				else if (entry.getValue() != null && otherValue == null)
					return 1;
				
				if (entry.getValue() instanceof Comparable)
					return ((Comparable) entry.getValue()).compareTo(otherValue);
			}
		}
		return 0;
	}

	public boolean equals(Object other) {
		if (other instanceof AttributeSet) {
			AttributeSet otherSet = (AttributeSet) other;
			if (content.size() != otherSet.getAttributeNames().size())
				return false;
			
			for (Iterator it = content.entrySet().iterator(); it.hasNext(); ) {
				Map.Entry entry = (Map.Entry) it.next();
				Object otherValue = otherSet.getAttribute((String) entry.getKey());
				if (entry.getValue() == null && otherValue != null)
					return false;
				else if (entry.getValue() != null && otherValue == null)
					return false;
				else if (entry.getValue() != null && !entry.getValue().equals(otherValue))
					return false;
			}
		}
		return false;
	}

	public int hashCode() {
		return content.hashCode();
	}

	public String toString() {
		return content.toString();
	}
}
