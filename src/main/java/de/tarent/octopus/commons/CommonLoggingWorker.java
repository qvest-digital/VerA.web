package de.tarent.octopus.commons;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.logging.SocketHandler;

import javax.jws.WebMethod;

import de.tarent.commons.messages.Message;
import de.tarent.commons.messages.MessageHelper;
import de.tarent.octopus.content.annotation.Name;
import de.tarent.octopus.content.annotation.Optional;
import de.tarent.octopus.server.OctopusContext;

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
			logInternal("Run appendLoggingProperties: Wlll reload java.util.logging configuration.");
			logInternal("    contentFilename: " + contentFilename);
			logInternal("     configFilename: " + configFilename);
			
			File modulePath = octopusContext.moduleRootPath();
			
			if (contentFilename != null) {
				File file = new File(contentFilename);
				if (file.exists()) {
					logInternal(READ_CONFIGURATION_FILE.getMessage(file.getAbsoluteFile()));
					LogManager.getLogManager().readConfiguration(new FileInputStream(file.getAbsoluteFile()));
				} else {
					file = new File(modulePath, contentFilename);
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
					file = new File(modulePath, configFilename);
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
	public void appendLoggingHandler(
			@Name("CONFIG:logging.handler") @Optional(true) Map<String, Map<String, String>> configHandler,
			@Name("CONFIG:logging.logger") @Optional(true) Map<String, Map<String, String>> configLogger) {
		logInternal("Run appendLoggingHandler: Wlll append java.util.logging configuration.");
		logInternal("    configHandler: " + configHandler);
		logInternal("    configLogger : " + configLogger);
		
		Map<String, Handler> handlerPool = new HashMap<String, Handler>();
		
		if (configHandler != null) {
			for (Map.Entry<String, Map<String, String>> entry : configHandler.entrySet()) {
				handlerPool.put(entry.getKey(), getHandler(entry.getValue()));
			}
		}
		
		if (configLogger != null) {
			for (Map.Entry<String, Map<String, String>> entry : configLogger.entrySet()) {
				setLogger(entry.getKey(), entry.getValue(), handlerPool);
			}
		}
	}

	protected Handler getHandler(Map<String, String> configuration) {
		try {
			String classname = configuration.get("class");
			String level = configuration.get("level");
			
			Handler handler = null;
			
			if (classname.equals("java.util.logging.ConsoleHandler")) {
				handler = new ConsoleHandler();
			} else if (classname.equals("java.util.logging.FileHandler")) {
				String pattern = configuration.get("pattern");
				int limit = getParameter(configuration, "limit", 0);
				int count = getParameter(configuration, "limit", 1);
				boolean a = getParameter(configuration, "limit", false);
				handler = new FileHandler(pattern, limit, count, a);
			} else if (classname.equals("java.util.logging.SocketHandler")) {
				String host = getParameter(configuration, "host", "127.0.0.1");
				int port = getParameter(configuration, "port", 0);
				handler =  new SocketHandler(host, port);
			} else {
				handler = (Handler)Class.forName(classname).newInstance();
			}
			
			if (level != null && level.length() != 0)
				handler.setLevel(Level.parse(level));
			
			return handler;
			
		} catch (Exception e) {
			logInternal(UNEXPECTED_EXCEPTION.getMessage(e.getLocalizedMessage()), e);
			return null;
		}
	}

	protected void setLogger(String name, Map<String, String> configuration, Map<String, Handler> handlerPool) {
		Logger logger = Logger.getLogger(name);
		
		String level = configuration.get("level");
		String handlers = configuration.get("handler");
		
		if (level != null && level.length() != 0)
			logger.setLevel(Level.parse(level));
		
		if (handlers != null) {
			for (String h : handlers.split("[,;]")) {
				Handler handler = handlerPool.get(h.trim());
				if (handler != null) {
					logger.addHandler(handler);
				}
			}
		}
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
		logger.log(Level.CONFIG, message);
	}

	/**
	 * This method log internal exceptions of this configuration worker
	 * out of the error stream of your webapplication server.
	 * 
	 * @param e Exception
	 */
	protected void logInternal(String message, Exception e) {
		logger.log(Level.SEVERE, message, e);
	}

	static {
		MessageHelper.init();
	}
}
