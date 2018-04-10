/**
 *
 */
package de.tarent.commons.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.logging.Log;

import de.tarent.commons.logging.LogFactory;

/**
 *
 * This class provides common checksum-methods based on the MD5-Algorithm
 *
 * @author Fabian K&ouml;ster (f.koester@tarent.de), tarent GmbH Bonn
 *
 */
public class ChecksumTool
{
	private static Log logger = LogFactory.getLog(ChecksumTool.class);

	/**
	 * Determines whether the given file and the given checksum are equal
	 *
	 * @param pFileName The filename of the file to checksum
	 * @param pChecksum The MD5-checksum to compare with
	 * @return true if equal
	 */

	public static boolean validateFile(String pFileName, String pChecksum)
	{
		return pChecksum.equals(createChecksum(pFileName));
	}

	/**
	 * Returns the MD5-checksum of the given file as a <code>String</code>
	 *
	 * @param pFileName The file to checksum
	 * @return The MD5-checksum of the file as a <code>String</code> or null if file not found
	 */

	public static String createChecksum(String pFileName)
	{
		String checksumString = null;

		MessageDigest md;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			if(logger.isWarnEnabled()) logger.warn(e.toString());
			return null;
		}

		FileInputStream fis;
		try {
			fis = new FileInputStream(pFileName);
		} catch (FileNotFoundException e) {
			return null;
		}

		DigestInputStream dis = new DigestInputStream(fis, md);

		// The dis.read method reads one byte of fis and updates the
		// MessageDigest. The following code reads the whole file.
		try {
			while(dis.read() != -1);
			dis.close();
		} catch (IOException e) {
			if(logger.isWarnEnabled()) logger.warn(e.toString());
			return null;
		}

		// Now we can compute the checksum.
		byte [] digest = md.digest();
		StringBuffer buffer = new StringBuffer();
		for(int i = 0; i < digest.length; i++) {
			String s = Integer.toHexString(digest[i] & 0xff);
			s = (s.length() == 1) ?	"0" + s : s;
			buffer.append(s);
		}
		checksumString = buffer.toString();

		return checksumString;
	}
}
