package de.tarent.commons.utils;

import java.util.Iterator;

/**
 * Wraps an interator interface around an array
 *
 * @author Tim Steffens
 *
 */
public class ArrayIterator implements Iterator {

	private Object[] array;
	private int index;

	public ArrayIterator(Object[] array) {
		this.array = array;
		this.index = 0;
	}

	/**
	 * @see java.util.Iterator#hasNext()
	 */
	public boolean hasNext() {
		return this.index < this.array.length;
	}

	/**
	 * @see java.util.Iterator#next()
	 */
	public Object next() {
		Object retObj = array[index];
		index++;
		return retObj;
	}

	/**
	 * @see java.util.Iterator#remove()
	 */
	public void remove() {
		throw new UnsupportedOperationException();
	}

}
