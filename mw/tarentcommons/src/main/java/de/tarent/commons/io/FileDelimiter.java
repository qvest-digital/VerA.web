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
