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

import org.apache.commons.logging.Log;

import de.tarent.commons.logging.LogFactory;

import jonelo.jacksum.JacksumAPI;
import jonelo.jacksum.algorithm.AbstractChecksum;

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
		try
		{
			AbstractChecksum checksum = JacksumAPI.getChecksumInstance("md5");
			checksum.readFile(pFileName);
			checksumString = checksum.getFormattedValue();
		}
		catch(Exception pExcp)
		{
			if(logger.isWarnEnabled()) logger.warn(pExcp.toString());
		}
		
		return checksumString;
	}
}
