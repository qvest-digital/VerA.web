package de.tarent.commons.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Logger;

import org.w3c.dom.Document;

import de.tarent.commons.config.ConfigManager.Scope;
import de.tarent.commons.utils.SystemInfo;

/**
 * {@link ConfigManager.Loader} implementation that reads configuration documents
 * from the local filesystem and provides its own configuration via system properties.
 *
 * <p>The <code>FileLoader</code> distinguishes between local, global and posix installation.
 * It decides which to use by evaluating the system property
 * <code>de.tarent.contact.config.file.style</code>.</p>
 *
 * <p>In case the property's value is <code>global</code> the property
 * <code>de.tarent.contact.config.file.dir</code> is taken into account. If
 * this is not set it tries a well-known location on Unix and Windows.
 * However the correct way to handle this is by specifying the complete
 * properties. This should be handled by the packaging system.</p>
 *
 * <p>For development purposes no property needs to be set as the implementation
 * can find the configuration files anyway.</p>
 *
 * @author Robert Schuster
 *
 */
class FileLoader extends ConfigManager.Loader
{
	private Logger logger = Logger.getLogger(FileLoader.class.getName());

	private static final String BASE = "de.tarent.commons.config.file";

	private static final String STYLE = BASE + ".style";

	private static final String DIR = BASE + ".dir";

	private static final String STYLE_GLOBAL = "global";

	private static final String STYLE_LOCAL = "local";

	private static final String STYLE_POSIX = "posix";

	private static final String DIR_WINDOWS = "c:\\Programme\\tarent-contact\\";

	private static final String DIR_UNIX = "/opt/tarent-contact/";

	String baseDirSite;

	String baseDirInstallation;

	String baseDirUser;

	Class applicationClass;

	FileLoader(String variant, Class applicationClass)
	{
		super(variant);

		this.applicationClass = applicationClass;

		String style = System.getProperty(STYLE, STYLE_LOCAL).intern();

		if (style == STYLE_GLOBAL)
			findDirectoriesForGlobalInstall();
		else if (style == STYLE_LOCAL)
			findDirectoriesForLocalInstall();
		else if (style == STYLE_POSIX)
			findDirectoriesForPosixInstall();
	}

	private void findDirectoriesForGlobalInstall()
	{
		String globalDir = System.getProperty(DIR, null);
		if (globalDir == null)
		{
			logger.warning("Missing system property '" + DIR + "', using a speculative default. Check install and packaging!");

			globalDir = (SystemInfo.isWindowsSystem() ? DIR_WINDOWS : DIR_UNIX);
		}
		else
			globalDir += File.separator;

		baseDirSite = globalDir + VARIANT + File.separator + "site";
		baseDirInstallation = globalDir + VARIANT + File.separator + "installation";
		baseDirUser = System.getProperty("user.home") + File.separator
		+ (SystemInfo.isWindowsSystem() ? "tarent-contact" : ".tarent-contact")
		+ File.separator + VARIANT;
	}

	private void findDirectoriesForLocalInstall()
	{
	    String localDir = System.getProperty(DIR, null);
	    if (localDir == null)
	      {
	        logger.warning("Missing system property '" + DIR + "', looking files up manually. Check install and packaging!");

	        // Assuming the application is installed locally (= not loaded via the net)
	        // and the installation has a fixed directory layout we can find out the application's
	        // base installation directory via the CodeSource of one of its classes.
	        try
	          {
	            // Don't use URL.toURI() for 1.4-compatibility.
	            URI uri = new URI(applicationClass.getProtectionDomain().getCodeSource().getLocation().toString());
	            if (uri.getAuthority() == null)
	            {
	              File f = new File(uri);
	              if (f.isDirectory())
	                {
	                  // If the class was loaded from a directory the application is in development mode and we can
	                  // assume the development layout
	                  File baseDevDir = f.getParentFile().getParentFile();
	                  localDir = baseDevDir + "/src/main/config/";
	                }
	              else
	                {
	                  // If the class was loaded from a file (= Jar) then we can assume installation layout.
	                  // Note: Installation layout depends on the settings in the applications installer
	                  // See src/main/izpack/installer.xml for details.
	                  File baseInstallDir = f.getParentFile().getParentFile();
	                  localDir = baseInstallDir + File.separator + "config" + File.separator;
	                }
	            }
	            else
	              {
	                logger.warning("application not locally installed. Cannot load configuration from files.");
	                throw new RuntimeException();
	              }
	          }
	        catch (URISyntaxException use)
	        {
	          logger.warning("Unable to create URI instance.");
	          throw new RuntimeException();
	        }
	      }
	    else
	      localDir += File.separator;

	    baseDirSite = localDir + VARIANT + File.separator + "site";
	    baseDirInstallation = localDir + VARIANT + File.separator + "installation";
	    baseDirUser = System.getProperty("user.home") + File.separator
	      + (SystemInfo.isWindowsSystem() ? "tarent" : ".tarent")
	      + File.separator + VARIANT;
	}

	private void findDirectoriesForPosixInstall()
	{
		baseDirSite = "/etc/tarent-contact/" + VARIANT + "/config/site";
		baseDirInstallation = "/etc/tarent-contact/" + VARIANT + "/config/installation";
		baseDirUser = System.getProperty("user.home") + "/.tarent-contact/" + VARIANT;
	}

	String getBaseDir(Scope scope)
	{
		if (scope == Scope.SITE)
			return baseDirSite;
		else if (scope == Scope.INSTALLATION)
			return baseDirInstallation;
		else if (scope == Scope.USER)
			return baseDirUser;
		else
			throw new RuntimeException("Unsupported configuration scope");
	}

	protected Document getDocument(Scope scope, String docName)
	throws ConfigManager.DocumentUnavailableException
	{
		FileInputStream fis = null;
		File resolveBase = new File(getBaseDir(scope));
		File doc = new File(resolveBase, docName);
		try
		{
			fis = new FileInputStream(doc);
		}
		catch (FileNotFoundException fnfe)
		{
			throw new ConfigManager.DocumentUnavailableException(doc.getAbsolutePath(), "file not found: " + doc.getAbsolutePath());
		}

		try
		{
			// Creating an URI instance is necessary for 1.4-compatibility.
			return XmlUtil.getParsedDocument(fis, resolveBase.toURI().toString());
		}
		catch (XmlUtil.Exception xmlue)
		{
			throw new ConfigManager.DocumentUnavailableException(doc.getAbsolutePath(), "XML parse error in file: " + doc.getAbsolutePath());
		}

	}

	protected boolean isStoringSupported()
	{
		return true;
	}

	protected void storeDocument(Scope scope, String docName, Document doc)
	throws ConfigManager.DocumentUnavailableException
	{
		FileOutputStream fos = null;
		File resolveBase = new File(getBaseDir(scope));
		File docFile = new File(resolveBase, docName);

		try
		{
			if (!resolveBase.exists())
				resolveBase.mkdirs();

			if (!docFile.exists())
				docFile.createNewFile();
		}
		catch (IOException ioe)
		{
			throw new ConfigManager.DocumentUnavailableException(docFile.getAbsolutePath(), "unable to create file");
		}

		try
		{
			fos = new FileOutputStream(docFile);
		}
		catch (FileNotFoundException fnfe)
		{
			throw new ConfigManager.DocumentUnavailableException(docFile.getAbsolutePath(), "file was missing");
		}

		try
		{
			XmlUtil.storeDocument(doc, fos);
		}
		catch (XmlUtil.Exception e)
		{
			throw new ConfigManager.DocumentUnavailableException(docFile.getAbsolutePath(), "XML transformation failed");
		}
		finally
		{
			try
			{
				fos.close();
			}
			catch (IOException e)
			{
				throw (IllegalStateException) new IllegalStateException().initCause(e);
			}
		}

	}

}
