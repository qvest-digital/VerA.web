package de.tarent.commons.utils;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import de.tarent.commons.datahandling.BeanMapTransformator;

/**
 * Contains various helper methods
 * @author Tim Steffens
 *
 */
public class Tools {

	/**
	 * Sets all entries from {@code map} that contain the empty String, only whitespaces or null to null
	 */
	public static void nullEmptyEntries(Map map) {
		for (Iterator it = map.keySet().iterator(); it.hasNext(); ) {
			Object key = it.next();
			Object value = map.get(key);
			if (value == null || value.toString().trim().length() == 0) {
				map.put(key, null);
			}
		}
	}

	/**
	 * Removes all entries from {@code map} that contain the empty String, only whitespaces or null.
	 */
	public static void removeEmptyEntries(Map map) {
		for (Iterator it = map.keySet().iterator(); it.hasNext(); ) {
			Object key = it.next();
			Object value = map.get(key);
			if (value == null || value.toString().trim().length() == 0) {
				it.remove();
			}
		}
	}

	/**
	 * Removes all entries vrom {@code map} that are null.
	 */
	public static void removeNullEntries(Map map) {
		for (Iterator it = map.keySet().iterator(); it.hasNext(); ) {
			Object key = it.next();
			Object value = map.get(key);
			if (value == null) {
				it.remove();
			}
		}
	}

	/**
	 * Returns true, if all entrys of map contain the empty String, only whitespaces or null.
	 */
	public static boolean mapIsEmpty(Map map) {
		for (Iterator it = map.values().iterator(); it.hasNext(); ) {
			Object value = it.next();
			if (value != null && value.toString().trim().length() != 0)
				return false;
		}
		return true;
	}

	/**
	 * Returns true, if {@code a} contains {@code o} (comparison via equals).
	 * @param a the array to be searched
	 * @param o the object to be found
	 * @return true, if {@code a} contains {@code o}
	 */
	public static boolean arrayContains(Object[] a, Object o) {
		if (a != null) {
			for (int i = 0; i < a.length; i++) {
				if (a[i].equals(o)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * If {@code key} and {@code value} are both not null, they are put into {@code map}.
	 */
	public static void putIfNotNull(Map map, Object key, Object value) {
		if (key != null && value != null) {
			map.put(key, value);
		}
	}

	/**
	 * Returns a string containing an ordered concatenation of the
	 * string representations of all items in <code>iterator</code>.
	 *
	 * @param prefix is a string that is put in front of each item
	 * @param prefixBeforeFirst is false, iff the prefix should not be put before the first item
	 * @param suffix is a string that is put behind each item
	 * @param suffixBeforeFirst is false, iff the prefix should not be put behind the first item
	 *
	 */
	public static String iteratorToString(Iterator iterator,
			String prefix, boolean prefixBeforeFirst,
			String suffix, boolean suffixBehindLast) {
		boolean isFirst = true;
		StringBuffer buffer = new StringBuffer();
		while (iterator.hasNext()) {
			Object o = iterator.next();
			if ((isFirst && prefixBeforeFirst) || !isFirst) {
				buffer.append(prefix);
			}
			isFirst = false;
			buffer.append(o);
			if (iterator.hasNext() || (!iterator.hasNext() && suffixBehindLast)) {
				buffer.append(suffix);
			}
		}
		return buffer.toString();
	}

	/**
	 * Returns a string representation of {@code map}
	 *
	 * @param prefix string that is put in front of each item
	 * @param prefixBeforeFirst is false, iff the prefix should not be put before the first item
	 * @param infix string, that is put between key and value
	 * @param suffix string that is put behind each item
	 * @param suffixBeforeFirst is false, iff the prefix should not be put behind the first item
	 */
	public static String mapToString(Map map,
			String prefix, boolean prefixBeforeFirst,
			String infix,
			String suffix, boolean suffixBehindLast) {
		Iterator keysIt = map.keySet().iterator();
		LinkedList keyValueList = new LinkedList();
		while (keysIt.hasNext()) {
			Object currentKey = keysIt.next();
			keyValueList.add(currentKey + infix + map.get(currentKey));
		}
		return iteratorToString(keyValueList.iterator(), prefix, prefixBeforeFirst, suffix, suffixBehindLast);
	}

	/**
	 * Returns a string containing an ordered concatenation of the
	 * string representations of all items in <code>array</code>.
	 *
	 * @param prefix is a string that is put in front of each item
	 * @param prefixBeforeFirst is false, iff the prefix should not be put before the first item
	 * @param suffix is a string that is put behind each item
	 * @param suffixBeforeFirst is false, iff the prefix should not be put behind the first item
	 *
	 */
	public static String arrayToString(Object[] array,
			String prefix, boolean prefixBeforeFirst,
			String suffix, boolean suffixBehindLast) {
		return iteratorToString(new ArrayIterator(array), prefix, prefixBeforeFirst, suffix, suffixBehindLast);
	}

	/**
	 * Returns a string representation of {@code bean}
	 *
	 * @param prefix string that is put in front of each item
	 * @param prefixBeforeFirst is false, iff the prefix should not be put before the first item
	 * @param infix string, that is put between key and value
	 * @param suffix string that is put behind each item
	 * @param suffixBeforeFirst is false, iff the prefix should not be put behind the first item
	 */
	public static String beanToString(Object bean,
			String prefix, boolean prefixBeforeFirst,
			String infix,
			String suffix, boolean suffixBehindLast) {
		return mapToString((new BeanMapTransformator()).transformBeanToMap(bean), prefix, prefixBeforeFirst, infix, suffix, suffixBehindLast);
	}

}
