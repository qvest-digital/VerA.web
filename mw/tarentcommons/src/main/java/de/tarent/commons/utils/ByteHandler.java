package de.tarent.commons.utils;

import java.text.DecimalFormat;

/**
 * <p>
 * A helper-class for converting bytes to kilobytes, megabytes, gigabytes and so on
 * </p>
 * <p><b>Caution:</b> This class is not compliant to the SI-unit-system. It is based on the naturalized (but false) use of the SI-prefixes.
 * <br />
 * 1024 byte (2^10) is not a "kilo" because according to the SI a kilo is always 1000 (10^3).
 * <br />
 * Try to avoid the use of this methods. Use the methods from ByteHandlerSI instead.
 * <br />
 * See also the appropriate <a href="http://en.wikipedia.org/wiki/Byte">Wikipedia-Article</a> on this.
 * </p>
 *
 *
 * @see de.tarent.commons.utils.ByteHandlerSI
 *
 * @author Fabian K&ouml;ster (f.koester@tarent.de), tarent GmbH Bonn
 *
 */
public class ByteHandler extends ByteHandlerSI
{
	public static double convertByteToKiloByte(long pBytes)
	{
		  return convertByteToKibiByte(pBytes);
	}

	public static double convertByteToMegaByte(long pBytes)
	{
		return convertByteToMebiByte(pBytes);
	}

	public static double convertByteToGigaByte(long pBytes)
	{
		return convertByteToGibiByte(pBytes);
	}

	public static double convertByteToTeraByte(long pBytes)
	{
		return convertByteToTebiByte(pBytes);
	}

	public static double convertByteToPetaByte(long pBytes)
	{
		return convertByteToPebiByte(pBytes);
	}

	public static double convertByteToExaByte(long pBytes)
	{
		return convertByteToExbiByte(pBytes);
	}

	/**
	 * Delivers a String-representation of a byte-number in an appropriate unit.<br />
	 * Example: A byte-size of 45029 will lead to the String "44 KB"
	 *
	 * @param pBytes a byte size
	 * @return A string-representation of the same size (only rounded) in the form <i>count unit</i>
	 */

	public static String getOptimalRepresentationForBytes(long pBytes)
	{
		DecimalFormat df = new DecimalFormat( "0.00" ); //$NON-NLS-1$

		if(pBytes > BYTES_PER_EXBI_BYTE)
			return df.format(convertByteToExaByte(pBytes))+ "EB"; //$NON-NLS-1$
		else if(pBytes > BYTES_PER_PEBI_BYTE)
			return df.format(convertByteToPetaByte(pBytes))+" PB"; //$NON-NLS-1$
		else if(pBytes > BYTES_PER_TEBI_BYTE)
			return df.format(convertByteToTeraByte(pBytes))+" TB"; //$NON-NLS-1$
		else if(pBytes > BYTES_PER_GIBI_BYTE)
			return df.format(convertByteToGigaByte(pBytes))+" GB"; //$NON-NLS-1$
		else if(pBytes > BYTES_PER_MEBI_BYTE)
			return df.format(convertByteToMegaByte(pBytes))+" MB"; //$NON-NLS-1$
		else if(pBytes > BYTES_PER_KIBI_BYTE)
			return df.format(convertByteToKiloByte(pBytes))+" KB"; //$NON-NLS-1$

		return pBytes + " Bytes"; //$NON-NLS-1$
	}
}
