package de.tarent.octopus.commons;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jws.WebMethod;

import de.tarent.commons.logging.LogManager;
import de.tarent.commons.messages.Message;
import de.tarent.commons.messages.MessageHelper;
import de.tarent.octopus.content.annotation.Name;
import de.tarent.octopus.content.annotation.Optional;
import de.tarent.octopus.server.OctopusContext;

/**
 * This octopus worker will be load the java.util.logging configuration
 * from a file with the tarent-commons {@link LogManager}.
 * 
 * @see LogManager for more information.
 * 
 * @author Christoph Jerolimov, tarent GmbH
 */
public class CommonLoggingWorker {
	/** Java util logger instance. */
	private static final Logger logger = Logger.getLogger(CommonLoggingWorker.class.getName());
	/** Will be syserr'ed if no parameter is available. */
	public static Message NO_PARAMETER_AVAILABLE;
	/** Will be syserr'ed if a new configuration file will be read. (1th param is full filename.) */
	public static Message READ_CONFIGURATION_FILE;
	/** Will be syserr'ed if the file does not exists. (First parameter will be the full filename.) */
	public static Message FILE_NOT_FOUND;
	/** Will be syserr'ed if an exception is thrown. (First parameter will be exception message.) */
	public static Message UNEXPECTED_EXCEPTION;

	static {
		MessageHelper.init(CommonLoggingWorker.class.getName(), "de.tarent.commons.messages.LogManager");
	}

	/**
	 * This octopus action re-configure the {@link LogManager} with the given
	 * parameters <code>logging.property.file</code>.
	 * 
	 * If <code>contentFilename</code> is set, the configuration file will be
	 * loaded from the content parameter <code>logging.property.file</code>.
	 * 
	 * Otherwise it tries to load the configuration file name from the octopus
	 * module configuration with the parameter <code>logging.property.file</code>.
	 * 
	 * @param contentFilename
	 * @param configFilename
	 */
	@WebMethod
	public void appendLoggingProperties(
			OctopusContext octopusContext,
			@Name("CONTENT:logging.property.file") @Optional(true) String contentFilename,
			@Name("CONFIG:logging.property.file") @Optional(true) String configFilename) {
		
		try {
			File modulePath = octopusContext.moduleRootPath();
			
			if (contentFilename != null) {
				if (!appendLoggingProperties(new File(modulePath, contentFilename))) {
					appendLoggingProperties(new File(contentFilename));
				}
			} else if (configFilename != null) {
				if (!appendLoggingProperties(new File(modulePath, configFilename))) {
					appendLoggingProperties(new File(configFilename));
				}
			} else {
				logger.log(Level.WARNING, NO_PARAMETER_AVAILABLE.getMessage());
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, UNEXPECTED_EXCEPTION.getMessage(e.getLocalizedMessage()), e);
		}
	}

	protected boolean appendLoggingProperties(File file) throws IOException {
		if (file.exists()) {
			if (logger.isLoggable(Level.INFO))
				logger.log(Level.INFO, READ_CONFIGURATION_FILE.getMessage(file.getAbsoluteFile()));
			
			InputStream is = new FileInputStream(file.getAbsoluteFile());
			
			// Do not call here java.util.logging.LogManager!
			// This will reset all other logging configurations and that
			// is really evil and can be have side effects to other
			// software producer that are running in the same vm.
			LogManager.getInstance().readConfiguration(is);
			
			return true;
		} else {
			if (logger.isLoggable(Level.INFO))
				logger.log(Level.INFO, FILE_NOT_FOUND.getMessage(file.getAbsoluteFile()));
			return false;
		}
	}

	@WebMethod
	public void appendLoggingHandler(
			OctopusContext octopusContext,
			@Name("CONFIG:logging.handler") @Optional(true) Map<String, Map<String, String>> configHandler,
			@Name("CONFIG:logging.logger") @Optional(true) Map<String, Map<String, String>> configLogger) {
		
		LogManager logManager = LogManager.getInstance();
		
		if (configHandler != null) {
			for (Map.Entry<String, Map<String, String>> entry : configHandler.entrySet()) {
				String handlername = entry.getKey();
				Properties properties = new Properties();
				properties.putAll(entry.getValue());
				
				logManager.setHandler(handlername, properties, null);
			}
		}
		
		if (configLogger != null) {
			for (Map.Entry<String, Map<String, String>> entry : configLogger.entrySet()) {
				String loggername = entry.getKey();
				
				Properties properties = new Properties();
				properties.setProperty(loggername + ".level", entry.getValue().get("level"));
				properties.setProperty(loggername + ".handlers", entry.getValue().get("handlers"));
				
				logManager.readConfiguration(properties);
			}
		}
	}
}
