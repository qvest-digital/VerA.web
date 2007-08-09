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

/*
 * Copyright (c) tarent GmbH
 * Bahnhofstrasse 13 . 53123 Bonn
 * www.tarent.de . info@tarent.de
 * 
 * Created on 28.04.2005
 */
package de.tarent.commons.utils;

/**
 * This class provides simple access to version and package information.
 * There are two ways to use this class:
 *  
 * 1. To use Version in your project copy this class to the main directory of your 
 * project and change the package declaration accordingly.
 * 2.Keep the Version- class in de.tarent.commons.util and call the init-Method 
 * passing an object from your package:
 * <pre> 
 * 
 * package de.tarent.mypackage;
 * class MyMainClass {
 *  public MyMainClass() {
 * 	  de.tarent.commons.util.Version.init(this);
 *  }
 * }
 * </pre>
 * 
 * Information like version-numbers, titles and vendors are read from the .jars manifest
 * file, so this information appears only, if compiled als Jar-File with a proper setup 
 * build system (Maven works).
 * 
 * @author Martin Ley
 * 
 */
public class Version {

	private static String name;

	private static String specificationTitle;

	private static String specificationVendor;

	private static String specificationVersion;

	private static String implementationTitle;

	private static String implementationVendor;

	private static String implementationVersion;

	/**
	 * initializes Version with package information from itself
	 */
	static {
		gatherPackageInfo(Version.class.getPackage());

	}
	
	/**
	 * Initialize Version with Object o. All package information is retrieved from the passed
	 * object.
	 * @param o object to retrieve package information from
	 */
	public static void init(Object o) {
		gatherPackageInfo(o.getClass().getPackage());
	}

	/**
	 * Retrieves package information from passed Package p
	 * 
	 * If some version, title or vendor information is not available, the corresponding
	 * String is set to ""
	 * @param p
	 */
	private static void gatherPackageInfo(Package p) {
		// name of this package.
		name = p.getName();
		
		
		// title of the specification of this package.
		String s = p.getSpecificationTitle();
		specificationTitle = s != null ? s : "";

		// version of the specification of this package.
		s = p.getSpecificationVersion();
		specificationVersion = s != null ? s : "";

		// vendor of the specification of this package.
		s = p.getSpecificationVendor();
		specificationVendor = s != null ? s : "";

		// title of the implementation of this package.
		s = p.getImplementationTitle();
		implementationTitle = s != null ? s : "";

		// version of the implementation of this package.
		s = p.getImplementationVersion();
		implementationVersion = s != null ? s : "";

		// vendor of the implementation of this package.
		s = p.getImplementationVendor();
		implementationVendor = s != null ? s : "";

	}

	public static String getName() {
		return name;
	}

	public static String getVersion() {
		return implementationTitle + " " + implementationVersion;
	}

	public static String getSpecification() {
		return "Package " + name + " " + specificationTitle + " "
				+ specificationVersion + " " + specificationVendor;
	}

	public static String getImplementation() {
		return "Package " + name + " " + implementationTitle + " "
				+ implementationVersion + " " + implementationVendor;
	}

	/** Not implemated yet. */
	public static String getCopyright() {
		return "not available";
	}

	/** Not implemated yet. */
	public static String getExtModules() {
		return "not available";
	}
}
