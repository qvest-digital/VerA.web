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

package de.tarent.commons.io;

import java.io.File;

/**
 * <p>
 * This helper supports some simple normalize functions to transfer unix or
 * windows path information to the present system.
 * </p>
 *
 * @see File#separatorChar
 *
 * @author Christoph Jerolimov, tarent GmbH
 */
public class FileDelimiter {
	/** Extension delimiter of the present system. */
	public final static String EXTENSION_DELIMITER = ".";
	/** Path delimiter ot the present system. */
	public final static String PATH_DELIMITER = new String(new char[] { File.separatorChar });

	/**
	 * This function return a new normalized file instance which
	 * use the system path delimiter.
	 *
	 * @param filename Unix or windows filename.
	 * @return A normalized file instance, never null.
	 * @throws NullPointerException if the argument is null.
	 */
	public static File getNormalizeFile(String filename) {
		return new File(normalize(filename));
	}

	/**
	 * This function return a new normalized file instance which
	 * use the system path delimiter.
	 *
	 * @param file File which contains a unix or windows filename.
	 * @return A normalized file instance, never null.
	 * @throws NullPointerException if the argument is null.
	 */
	public static File getNormalizeFile(File file) {
		return new File(normalize(file.getPath()));
	}

	/**
	 * This function return a new normalized file instance which
	 * use the system path delimiter.
	 *
	 * @param filename Unix or windows filename.
	 * @return A normalized string, never null.
	 * @throws NullPointerException if the argument is null.
	 */
	public static String normalize(String filename) {
		return filename.replaceAll("/|\\\\", PATH_DELIMITER);
	}
}
