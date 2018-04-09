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

import java.lang.reflect.Field;
import java.util.logging.Logger;

/**
 * 
 * <p>This class provides convenient methods for setting environment-variables like PATH(Windows)/LD_LIBRARY_PATH(UNIX) or CLASSPATH</p>
 * 
 * <p><b>Note that the init()-method which makes the variables writable is based on a assumption which is not part of the Java-Standard and therefore is no
 * guarantee that it works with all Java-Environments. It has been tested with official Sun-JREs 1.4 and 1.5 on Windows and Linux</b></p>
 * 
 * <p>A better solution is maybe a custom ClassLoader which is loaded at application start by setting -Dbootstrap.classloader=my-class-loader at JVM-startup.</p>
 * 
 * @author Fabian K&ouml;ster (f.koester@tarent.de), tarent GmbH Bonn
 *
 */
public abstract class EnvironmentVariableTool
{
	/**
	 * The java-property-name for the LIBRARY_PATH-Environment-Variable
	 */
	public final static String LIBRARY_PATH = "java.library.path";
	/**
	 * The java-property-name for the CLASSPATH-Environment-Variable
	 */
	public final static String CLASSPATH = "java.class.path";
	
	private final static Logger logger = Logger.getLogger(EnvironmentVariableTool.class.getName());
	
	// This class cannot be instantiated
	private EnvironmentVariableTool() {}
	
	private static void init()
	{
		// Workaround needed for beeing able to set java.library.path
		// TODO replace this hack by better method
		
		Class clazz = ClassLoader.class;
		try
		{
			Field field = clazz.getDeclaredField("sys_paths");
			boolean accessible = field.isAccessible();
			if (!accessible)
				field.setAccessible(true);
			
			// Reset it to null so that whenever "System.loadLibrary" is called, it will be reconstructed with the changed value.
			field.set(clazz, null);
		}
		catch(Exception pExcp)
		{
			logger.warning("Setting sys_paths accessible failed");
		}	
	}
	
	private static boolean addEntryToVariable(String pEntry, String pVariable)
	{
		init();
		
		// Check if it is already in there
		if(System.getProperty(pVariable) != null && System.getProperty(pVariable).indexOf(pEntry) == -1)
		{
			// Add entry
			System.setProperty(pVariable, System.getProperty(pVariable)+System.getProperty("path.separator")+pEntry);
			return false;
		}
		else
		{
			return true;
		}
	}
	
	private static boolean addEntriesToVariable(String[] pEntries, String pVariable)
	{
		boolean alreadyPresent = false;
		
		if(pEntries != null)
		{
			for(int i=0; i < pEntries.length; i++)
			{
				if(addEntryToVariable(pEntries[i], pVariable)) alreadyPresent = true;
			}
		}
		
		return alreadyPresent;
	}
	
	/**
	 * Adds an array of entries to the CLASSPATH-Environment-Variable
	 * 
	 * @param pEntries an array of entries to be added to the CLASSPATH-Environment-Variable
	 * @return if one of the entries already existed in the CLASSPATH-Environment-Variable
	 */
	
	public static boolean addEntriesToClassPath(String[] pEntries)
	{
		return addEntriesToVariable(pEntries, CLASSPATH);
	}
	
	/**
	 * Adds an entry to the CLASSPATH-Environment-Variable
	 * 
	 * @param pEntry the entry to be added to the CLASSPATH-Environment-Variable
	 * @return if the entry already existed in the CLASSPATH-Environment-Variable
	 */
	
	public static boolean addEntryToClassPath(String pEntry)
	{
		return addEntryToVariable(pEntry, CLASSPATH);
	}
	
	/**
	 * Adds an array of entries to the PATH(Windows)- or LD_LIBRARY_PATH(Unix)-Environment-Variable
	 * 
	 * @param pEntries an array of entries to be added to the PATH(Windows)- or LD_LIBRARY_PATH(Unix)-Environment-Variable
	 * @return if one of the entries already existed in the PATH(Windows)- or LD_LIBRARY_PATH(Unix)-Environment-Variable
	 */
	
	public static boolean addEntriesToLibraryPath(String [] pEntries)
	{
		return addEntriesToVariable(pEntries, LIBRARY_PATH);
	}
	
	/**
	 * Adds an entry to the PATH(Windows)- or LD_LIBRARY_PATH(Unix)-Environment-Variable
	 * 
	 * @param pEntry the entry to be added to the PATH(Windows)- or LD_LIBRARY_PATH(Unix)-Environment-Variable
	 * @return if the entry already existed in the PATH(Windows)- or LD_LIBRARY_PATH(Unix)-Environment-Variable
	 */
	
	public static boolean addEntryToLibraryPath(String pEntry)
	{
		return addEntryToVariable(pEntry, LIBRARY_PATH);
	}
}
