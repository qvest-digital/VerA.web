/*
 * tarent-octopus common,
 * an opensource webservice and webapplication framework (common part)
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
 * interest in the program 'tarent-octopus common'
 * Signature of Elmar Geese, 11 June 2007
 * Elmar Geese, CEO tarent GmbH.
 */

/*
 * tarent-octopus common worker,
 * tarent-octopus common worker
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
 * interest in the program 'tarent-octopus common worker'
 * Signature of Elmar Geese, 11 June 2007
 * Elmar Geese, CEO tarent GmbH.
 */

package de.tarent.octopus.commons;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jws.WebMethod;

import de.tarent.commons.logging.LogManager;
import de.tarent.commons.logging.ThreadLogger;
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
			getLogManager().readConfiguration(is);
			
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
		
		LogManager logManager = getLogManager();
		
		if (configHandler != null) {
			for (Map.Entry<String, Map<String, String>> entry : configHandler.entrySet()) {
				String handlername = entry.getKey();
				Properties properties = new Properties();
				
				for (Map.Entry<String, String> param : entry.getValue().entrySet()) {
					String key = param.getKey();
					String value = octopusContext.moduleConfig().substituteVars(param.getValue());
					properties.put(key, value);
				}
				
				logManager.setHandler(handlername, properties, null);
			}
		}
		
		if (configLogger != null) {
			for (Map.Entry<String, Map<String, String>> entry : configLogger.entrySet()) {
				String loggername = entry.getKey();
				String level = entry.getValue().get("level");
				String handlers = entry.getValue().get("handlers");
				
				Properties properties = new Properties();
				if (level != null && level.length() != 0)
					properties.setProperty(loggername + ".level", level);
				if (handlers != null && handlers.length() != 0)
					properties.setProperty(loggername + ".handlers", handlers);
				
				logManager.readConfiguration(properties);
			}
		}
	}

	@WebMethod
	public void changeHandlerLevels(
			@Name("CONFIG:logging.level") @Optional(true) Map<String, Map<String, String>> configLevels) {
		
		if (configLevels != null) {
			for (Map.Entry<String, Map<String, String>> entry : configLevels.entrySet()) {
				String loggername = entry.getKey();
				if (loggername.equals("root"))
					loggername = "";
				
				if (logger.isLoggable(Level.INFO))
					logger.log(Level.INFO, "Will change log level for handler of logger '" + loggername + "' with " + entry.getValue() + ".");
				
				List<Handler> handlers = Arrays.asList(Logger.getLogger(loggername).getHandlers());
				if (logger.isLoggable(Level.INFO))
					logger.log(Level.INFO, "Available handlers: " + handlers);
				
				for (Map.Entry<String, String> handlerToLevel : entry.getValue().entrySet()) {
					String handlerpattern = handlerToLevel.getKey();
					String level = handlerToLevel.getValue();
					
					if (level != null && level.length() != 0) {
						for (Handler handler : handlers) {
							if (handler.toString().contains(handlerpattern)) {
								if (logger.isLoggable(Level.INFO))
									logger.log(Level.INFO, "Change level of handler '" + handler + "' for logger '" + loggername + "' on '" + level + "'.");
								try {
									handler.setLevel(Level.parse(level));
								} catch (IllegalArgumentException e) {
									logger.log(Level.WARNING, "Bad level '" + level + "' for handler '" + handler + "' for logger '" + loggername + "'.");
								}
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Initialize a thread logger for the current request thread with
	 * the request id of the surroung webapplication server.
	 * 
	 * @param octopusContext
	 */
	@WebMethod
	public void initializeThreadLogger(OctopusContext octopusContext) {
		String threadId = octopusContext.getRequestObject().getRequestID();
		final ThreadLogger threadLogger = ThreadLogger.createInstance(threadId);
		
		octopusContext.addCleanupCode(new Runnable() {
			public void run() {
				threadLogger.clean();
			}
		});
	}

	/** {@link LogManager} instance for this common logging worker. */
	protected LogManager logManagerInstance = LogManager.getInstance();

	/**
	 * Return one {@link LogManager} instance for all octopus actions
	 * in this worker.
	 * 
	 * @return
	 */
	protected LogManager getLogManager() {
		return logManagerInstance;
	}
}
