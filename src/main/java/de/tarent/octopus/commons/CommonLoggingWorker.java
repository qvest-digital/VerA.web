package de.tarent.octopus.commons;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.logging.SocketHandler;

import javax.jws.WebMethod;

import de.tarent.commons.messages.Message;
import de.tarent.octopus.content.annotation.Name;
import de.tarent.octopus.content.annotation.Optional;
import de.tarent.octopus.server.Context;

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
					file = new File(Context.getActive().moduleRootPath(), contentFilename);
					if (file.exists()) {
						logInternal(READ_CONFIGURATION_FILE.getMessage(file.getAbsoluteFile()));
						LogManager.getLogManager().readConfiguration(new FileInputStream(file.getAbsoluteFile()));
					} else {
						logInternal(FILE_NOT_FOUND.getMessage(file.getAbsoluteFile()));
					}
				}
			} else if (configFilename != null) {
				File file = new File(configFilename);
				if (file.exists()) {
					logInternal(READ_CONFIGURATION_FILE.getMessage(file.getAbsoluteFile()));
					LogManager.getLogManager().readConfiguration(new FileInputStream(file.getAbsoluteFile()));
				} else {
					file = new File(Context.getActive().moduleRootPath(), configFilename);
					if (file.exists()) {
						logInternal(READ_CONFIGURATION_FILE.getMessage(file.getAbsoluteFile()));
						LogManager.getLogManager().readConfiguration(new FileInputStream(file.getAbsoluteFile()));
					} else {
						logInternal(FILE_NOT_FOUND.getMessage(file.getAbsoluteFile()));
					}
				}
			} else {
				logInternal(NO_PARAMETER_AVAILABLE.getMessage());
			}
		} catch (Exception e) {
			logInternal(UNEXPECTED_EXCEPTION.getMessage(e.getLocalizedMessage()), e);
		}
	}

	@WebMethod
	public void setAdditionalConfiguration(
			@Name("CONFIG:logging.handler") @Optional(true) Map<String, Map<String, String>> configHandler,
			@Name("CONFIG:logging.level") @Optional(true) Map<String, Map<String, String>> configLevel) {
		
		Map<String, Handler> handler = new HashMap<String, Handler>();
		Map<String, Logger> logger = new HashMap<String, Logger>();
		
		for (Map.Entry<String, Map<String, String>> entry : configHandler.entrySet()) {
			handler.put(entry.getKey(), getHandler(entry.getValue()));
		}
		
		for (Map.Entry<String, Map<String, String>> entry : configLevel.entrySet()) {
			logger.put(entry.getKey(), getLogger(entry.getValue(), handler));
		}
	}

	protected Handler getHandler(Map<String, String> configuration) {
		try {
			String clazz = configuration.get("class");
			if (clazz.equals("java.util.logging.ConsoleHandler")) {
				return new ConsoleHandler();
			} else if (clazz.equals("java.util.logging.FileHandler")) {
				String pattern = configuration.get("pattern");
				int limit = getParameter(configuration, "limit", 0);
				int count = getParameter(configuration, "limit", 1);
				boolean a = getParameter(configuration, "limit", false);
				return new FileHandler(pattern, limit, count, a);
			} else if (clazz.equals("java.util.logging.SocketHandler")) {
				String host = getParameter(configuration, "host", "127.0.0.1");
				int port = getParameter(configuration, "port", 0);
				return new SocketHandler(host, port);
			} else {
				throw new RuntimeException("Unknown handler class " + clazz);
			}
		} catch (Exception e) {
			logInternal(UNEXPECTED_EXCEPTION.getMessage(e.getLocalizedMessage()), e);
			return null;
		}
	}

	protected Logger getLogger(Map<String, String> configuration, Map<String, Handler> handler) {
		return null;
	}

	protected String getParameter(Map<String, String> configuration, String key, String def) {
		String result = configuration.get(key);
		if (result != null && result.trim().length() != 0)
			return result;
		else
			return def;
	}

	protected int getParameter(Map<String, String> configuration, String key, int def) {
		try {
			return Integer.parseInt(configuration.get(key));
		} catch (Exception e) {
			return def;
		}
	}

	protected boolean getParameter(Map<String, String> configuration, String key, boolean def) {
		try {
			return Boolean.parseBoolean(configuration.get(key));
		} catch (Exception e) {
			return def;
		}
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
	protected void logInternal(String message, Exception e) {
		logInternal(message);
		e.printStackTrace();
	}
}
