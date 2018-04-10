package de.tarent.commons.utils;

import de.tarent.commons.ui.UnsupportedVersionComplain;

/**
 *
 * Provides system-information
 *
 * @author Fabian K&ouml;ster (f.koester@tarent.de), tarent GmbH Bonn
 *
 */
public class SystemInfo
{
	/**
	 * Checks if the host-system consists of crappy code from Redmond
	 *
	 * @return true if we are running in an evil-environment
	 */

	public static boolean isWindowsSystem()
	{
		return getOSName().toLowerCase().startsWith("windows");
	}

	/**
	 *
	 * Checks if the host-system is a Linux-derivate
	 *
	 * @return true if we are at home
	 */

	public static boolean isLinuxSystem()
	{
		return getOSName().toLowerCase().startsWith("linux");
	}

	/**
	 *
	 * Returns the System-Property-Value os.name. (Just for convenience)
	 *
	 * @return the name of the host-operating-system
	 */

	public static String getOSName()
	{
		return System.getProperty("os.name");
	}

	/**
	 * Checks if the host-system is a x86-based platform
	 * @return true if it is a x86-host
	 */

	public static boolean isX86System()
	{
		return getArch().toLowerCase().equals("i386");
	}

	/**
	 * Checks if the host-system is a AMD64-based platform
	 * @return true if it is a AMD64-host
	 */

	public static boolean isAMD64System()
	{
		return getArch().toLowerCase().equals("amd64");
	}

	/**
	 * Get the architecture-information-String for the host-platform
	 * @return the architecture name
	 */

	public static String getArch()
	{
		return System.getProperty("os.arch");
	}

	/**
	 * checks the runtime for compatibility.
	 *
	 */
	public static boolean checkForCompatibility(String appName, double minVersion, double maxVersion)
	{
		double d = new Double(System.getProperty("java.specification.version", "0.0")).doubleValue();
		if (d < minVersion|| d > maxVersion || System.getProperty("java.vendor", "none").indexOf("Sun Microsystems") < 0)
			return UnsupportedVersionComplain.showUnsupportedVersionComplain(appName);

		return true;
	}
}
