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

/**
 *
 */
package de.tarent.commons.utils;

import java.text.DecimalFormat;

/**
 * <p>
 * A helper-class for converting bytes to kilobytes, megabytes, gigabytes and so on
 * </p>
 * <p>
 * This is the SI-compliant implementation of the byte-units-system. 1024 byte (2^10) is not a "kilo" because according to the SI a kilo is always 1000 (10^3).<br />
 * See also the appropriate <a href="http://en.wikipedia.org/wiki/Byte">Wikipedia-Article</a> on this.
 * </p>
 *
 * @see de.tarent.commons.utils.ByteHandler
 *
 * @author Fabian K&ouml;ster (f.koester@tarent.de), tarent GmbH Bonn
 *
 */
public class ByteHandlerSI
{
	/* SI prefixes */

	public final static double BYTES_PER_KILO_BYTE = Math.pow(10, 3);
	public final static double BYTES_PER_MEGA_BYTE = Math.pow(10,6);
	public final static double BYTES_PER_GIGA_BYTE = Math.pow(10,9);
	public final static double BYTES_PER_TERA_BYTE = Math.pow(10,12);
	public final static double BYTES_PER_PETA_BYTE = Math.pow(10,15);
	public final static double BYTES_PER_EXA_BYTE = Math.pow(10,18);

	/* Out of double-range (64-bit, 1-Math.pow(10,19)
	public final static double BYTES_PER_ZETTA_BYTE = Math.pow(10,21);
	public final static double BYTES_PER_YOTTA_BYTE = Math.pow(10,24);
	*/

	/* Binary prefixes */

	public final static double BYTES_PER_KIBI_BYTE = Math.pow(2, 10);
	public final static double BYTES_PER_MEBI_BYTE = Math.pow(2, 20);
	public final static double BYTES_PER_GIBI_BYTE = Math.pow(2, 30);
	public final static double BYTES_PER_TEBI_BYTE = Math.pow(2, 40);
	public final static double BYTES_PER_PEBI_BYTE = Math.pow(2, 50);
	public final static double BYTES_PER_EXBI_BYTE = Math.pow(2, 60);

	/* Out of double-range (64-bit, 1-Math.pow(10,19)
	public final static double BYTES_PER_ZEBI_BYTE = Math.pow(70);
	public final static double BYTES_PER_YOBI_BYTE = Math.pow(80);
	*/

	/* SI prefixes */

	public static double convertByteToKiloByte(long pBytes)
	{
		 return pBytes / BYTES_PER_KILO_BYTE;
	}

	public static double convertByteToMegaByte(long pBytes)
	{
		return pBytes / BYTES_PER_MEGA_BYTE;
	}

	public static double convertByteToGigaByte(long pBytes)
	{
		return pBytes / BYTES_PER_GIGA_BYTE;
	}

	public static double convertByteToTeraByte(long pBytes)
	{
		return pBytes / BYTES_PER_TERA_BYTE;
	}

	public static double convertByteToPetaByte(long pBytes)
	{
		return pBytes / BYTES_PER_PETA_BYTE;
	}

	public static double convertByteToExaByte(long pBytes)
	{
		return pBytes / BYTES_PER_EXA_BYTE;
	}

	/* binary prefixes */

	public static double convertByteToKibiByte(long pBytes)
	{
		return pBytes / BYTES_PER_KIBI_BYTE;
	}

	public static double convertByteToMebiByte(long pBytes)
	{
		return pBytes / BYTES_PER_MEBI_BYTE;
	}

	public static double convertByteToGibiByte(long pBytes)
	{
		return pBytes / BYTES_PER_GIBI_BYTE;
	}

	public static double convertByteToTebiByte(long pBytes)
	{
		return pBytes / BYTES_PER_TEBI_BYTE;
	}

	public static double convertByteToPebiByte(long pBytes)
	{
		return pBytes / BYTES_PER_PEBI_BYTE;
	}

	public static double convertByteToExbiByte(long pBytes)
	{
		return pBytes / BYTES_PER_EXBI_BYTE;
	}

	/* convenience method */

	/**
	 * Delivers a String-representation of a byte-number in an appropriate unit.<br />
	 * Example: A byte-size of 45029 will lead to the String "44 KiB"
	 *
	 * @param pBytes a byte size
	 * @return A string-representation of the same size (only rounded) in the form <i>count unit</i>
	 */

	public static String getOptimalRepresentationForBytes(long pBytes)
	{
		DecimalFormat df = new DecimalFormat( "0.00" ); //$NON-NLS-1$

		if(pBytes > BYTES_PER_EXBI_BYTE)
			return df.format(convertByteToExbiByte(pBytes))+ "EiB"; //$NON-NLS-1$
		else if(pBytes > BYTES_PER_PEBI_BYTE)
			return df.format(convertByteToPebiByte(pBytes))+" PiB"; //$NON-NLS-1$
		else if(pBytes > BYTES_PER_TEBI_BYTE)
			return df.format(convertByteToTebiByte(pBytes))+" TiB"; //$NON-NLS-1$
		else if(pBytes > BYTES_PER_GIBI_BYTE)
			return df.format(convertByteToGibiByte(pBytes))+" GiB"; //$NON-NLS-1$
		else if(pBytes > BYTES_PER_MEBI_BYTE)
			return df.format(convertByteToMebiByte(pBytes))+" MiB"; //$NON-NLS-1$
		else if(pBytes > BYTES_PER_KIBI_BYTE)
			return df.format(convertByteToKibiByte(pBytes))+" KiB"; //$NON-NLS-1$

		return pBytes + " Bytes"; //$NON-NLS-1$
	}
}
