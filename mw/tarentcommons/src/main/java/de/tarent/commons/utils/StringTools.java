package de.tarent.commons.utils;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Contains methods for string processing.
 * @author tim
 *
 */
public class StringTools {

	/**
	 * OS-specific character sequence for line break
	 */
	public final static String LINE_SEPARATOR = System.getProperty("line.separator");

	/**
	 * Returns {@code s} with the first letter in upper case
	 *
	 * @param s the string to be processed
	 * @return {@code s} with the first letter in upper case
	 */
	public static String capitalizeFirstLetter(String s) {
		if (s == null) {
			return null;
		}
		else if (s.length() == 0) {
			return s;
		}
		else {
			return Character.toUpperCase(s.charAt(0)) +
				s.substring(1);
		}
	}

	/**
	 * Returns {@code s} with the first letter in lower case
	 *
	 * @param s the string to be processed
	 * @return {@code s} with the first letter in lower case
	 */
	 public static String minusculizeFirstLetter(String s) {
		if (s == null) {
			return null;
		}
		else if (s.length() == 0) {
			return s;
		}
		else {
			return Character.toLowerCase(s.charAt(0)) +
				s.substring(1);
		}
	}

	/**
	 * Calculates the digest from input
	 * @param input String to be hashed
	 * @param hashfunction Hash-Function to use
	 * @return hashed version of input
	 * @throws NoSuchAlgorithmException
	 */
	public static String digest(String input, String hashfunction) throws NoSuchAlgorithmException{
		String md5 = null;
		if (input == null)
			return null;
		Charset cs = Charset.forName("UTF8"); //$NON-NLS-1$
		ByteBuffer bb = cs.encode(CharBuffer.wrap(input));
		MessageDigest md = MessageDigest.getInstance( hashfunction ); //$NON-NLS-1$
		md.update(bb.array());
		byte[] digest = md.digest();
		StringBuffer buffer = new StringBuffer();
		for(int i = 0; i < digest.length; i++)
			buffer.append(Integer.toHexString(digest[i] & 0xff));
		md5 = buffer.toString();
		return md5;
	}

	/**
	 * Calculates the md5-hash of input
	 * @param input String to be hashed
	 * @return MD5-Hash from input
	 * @throws NoSuchAlgorithmException
	 */
	public static String md5(String input) throws NoSuchAlgorithmException{
		return digest(input, "MD5");
	}

}
