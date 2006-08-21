package de.tarent.octopus.commons;

import java.io.File;
import java.io.FileInputStream;
import java.util.Map;
import java.util.logging.LogManager;

import javax.jws.WebMethod;

import de.tarent.commons.messages.Message;
import de.tarent.octopus.content.annotation.Name;
import de.tarent.octopus.content.annotation.Optional;

/**
 * This octopus worker will be load the java.util.logging configuration
 * from a file ({@link #readConfiguration(Object, Object)} or from the
 * octopus module config ({@link #setAdditionalConfiguration(Map)}).
 * 
 * @see LogManager for more information.
 * 
 * @author Christoph Jerolimov, tarent GmbH
 */
public class CommonLoggingWorker {
	/** Will be syserr'ed if no parameter is available. */
	public static Message NO_PARAMETER_AVAILABLE;
	/** Will be syserr'ed if a new configuration file will be read. (1th param is full filename.) */
	public static Message READ_CONFIGURATION_FILE;
	/** Will be syserr'ed if the file does not exists. (First parameter will be the full filename.) */
	public static Message FILE_NOT_FOUND;
	/** Will be syserr'ed if an exception is thrown. (First parameter will be exception message.) */
	public static Message UNEXPECTED_EXCEPTION;

	/**
	 * This octopus action re-configure the {@link LogManager} with the given
	 * parameters <code>contentFilename</code> or <code>configFilename</code>.
	 * 
	 * If <code>contentFilename</code> is set, the configuration file will be
	 * loaded from the content parameter <code>loggingConfigurationFile</code>.
	 * 
	 * Otherwise it tries to load the configuration file name from the octopus
	 * module configuration with the parameter <code>loggingConfigurationFile</code>.
	 * 
	 * @param contentFilename
	 * @param configFilename
	 */
	@WebMethod
	public void readConfiguration(
			@Name("CONTENT:loggingConfigurationFile") @Optional(true) String contentFilename,
			@Name("CONFIG:loggingConfigurationFile") @Optional(true) String configFilename) {
		
		try {
			logInternal("#readConfiguration: Reload java.util.logging-Configuration.");
			if (contentFilename != null) {
				File file = new File(contentFilename);
				if (file.exists()) {
					logInternal(READ_CONFIGURATION_FILE.getMessage(file.getAbsoluteFile()));
					LogManager.getLogManager().readConfiguration(new FileInputStream(file.getAbsoluteFile()));
				} else {
					logInternal(FILE_NOT_FOUND.getMessage(file.getAbsoluteFile()));
				}
			} else if (configFilename != null) {
				File file = new File(configFilename);
				if (file.exists()) {
					logInternal(READ_CONFIGURATION_FILE.getMessage(file.getAbsoluteFile()));
					LogManager.getLogManager().readConfiguration(new FileInputStream(file.getAbsoluteFile()));
				} else {
					logInternal(FILE_NOT_FOUND.getMessage(file.getAbsoluteFile()));
				}
			} else {
				System.err.println(NO_PARAMETER_AVAILABLE);
			}
		} catch (Exception e) {
			logInternal(UNEXPECTED_EXCEPTION.getMessage(e.getLocalizedMessage(), contentFilename, configFilename));
		}
	}

	@WebMethod
	public void setAdditionalConfiguration(
			/*@Name("CONFIG:loggingConfiguration") @Optional(true) Map<String, Object> loggingConfiguration*/) {
		
	}

	/**
	 * This method log internal parameters of this configuration worker
	 * out of the error stream of your webapplication server.
	 * 
	 * @param message Message string
	 */
	protected void logInternal(String message) {
		System.err.println("[" + getClass().getSimpleName() + "] " + message);
	}

	/**
	 * This method log internal exceptions of this configuration worker
	 * out of the error stream of your webapplication server.
	 * 
	 * @param e Exception
	 */
	protected void logInternal(Exception e) {
		e.printStackTrace();
	}
}
