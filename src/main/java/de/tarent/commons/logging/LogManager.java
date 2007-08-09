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
 * License GPL.
 * 
 * Part of this software are copied from the GNU Classpath project.
 * Visit http://developer.classpath.org/doc/ for more information.
 */
package de.tarent.commons.logging;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SocketHandler;

import org.apache.commons.logging.Log;

import de.tarent.commons.messages.Message;
import de.tarent.commons.messages.MessageHelper;

/**
 * Better ;-) implementation of the {@link java.util.logging.LogManager},
 * because you <strong>can extend</strong> already loaded {@link Properties}
 * information about the logging. The default implementation will
 * remove all old informations.
 * 
 * TODO License information (GPL)
 * 
 * @author Christoph Jerolimov, tarent GmbH
 * @author Sascha Brawer (brawer@acm.org)
 */
public class LogManager {
	/** Java util logger instance. */
	private static final Log logger = LogFactory.getLog(LogManager.class);
	
	/** Root logger property name */
	private static final String ROOT_LOGGER_NAME = "root";
	/** Handler property const */
	private static final String HANDLERS = "handlers";
	/** Handler property const */
	private static final String HANDLERS_SUFFIX = ".handlers";
	/** Level property const */
	private static final String LEVEL = "level";
	/** Level property const */
	private static final String LEVEL_SUFFIX = ".level";
	/** Use parent property const */
	private static final String USE_PARENT = "useParentHandlers";
	/** Use parent property const */
	private static final String USE_PARENT_SUFFIX = ".useParentHandlers";
	
	/** Will be syserr'ed if no parameter is available. */
	public static Message NO_PARAMETER_AVAILABLE;
	/** Will be syserr'ed if a new configuration file will be read. (1th param is full filename.) */
	public static Message READ_CONFIGURATION_FILE;
	/** Will be syserr'ed if the file does not exists. (First parameter will be the full filename.) */
	public static Message FILE_NOT_FOUND;
	/** Will be syserr'ed if an exception is thrown. (First parameter will be exception message.) */
	public static Message UNEXPECTED_EXCEPTION;

	static {
		MessageHelper.init(LogManager.class.getName(), "de.tarent.commons.messages.LogManager");
	}

	/**
	 * Handler pool contains key-value pairs of handlername as {@link String}
	 * and instances of {@link Handler Handlers}.
	 */
	Map handlerPool = new HashMap();

	/** @see #getInstance() */
	private LogManager() {
		// Use getInstance()
	}

	/** Return a new instance of a logmanager. */
	public static LogManager getInstance() {
		return new LogManager();
	}

	/**
	 * Read the logging.properties from the given {@link InputStream} instance.
	 * 
	 * @param inputStream
	 * @throws IOException
	 */
	public void readConfiguration(InputStream inputStream) throws IOException {
		if (logger.isInfoEnabled())
			logger.info("Reading logging properties from an inputstream, will append this now.");
		
		Properties properties = new Properties();
		properties.load(inputStream);
		
		appendHandlers(properties);
		configureLoggers(properties);
	}

	/**
	 * Read the logging.properties from the given {@link Properties} instance.
	 * 
	 * @param properties
	 */
	public void readConfiguration(Properties properties) {
		if (logger.isInfoEnabled())
			logger.info("Reading logging properties from a property object, will append this now.");
		
		appendHandlers(properties);
		configureLoggers(properties);
	}

	/**
	 * Append {@link Handler log handlers} to a logger.
	 * 
	 * Properties <code>handlers</code> and <code>.handlers</code>
	 * define the handlers of the root logger.
	 * 
	 * Properties like <code>abc.my.logger.handlers</code> will append
	 * the handlers to the {@link Logger logger} "abc.my.logger".
	 * 
	 * @param properties
	 */
	public void appendHandlers(Properties properties) {
		Enumeration keys = properties.keys();
		while (keys.hasMoreElements()) {
			String key = (String)keys.nextElement();
			String value = properties.getProperty(key);
			
			if (key == null || (key = key.trim()).length() == 0)
				continue;
			if (value == null || (value = value.trim()).length() == 0)
				continue;
			
			// Append handler to loggers.
			if (key.equals(HANDLERS) || key.endsWith(HANDLERS_SUFFIX)) {
				String loggername;
				if (key.length() == HANDLERS.length())
					loggername = "";
				else
					loggername = key.substring(0, key.length() - HANDLERS_SUFFIX.length());
				if (loggername.equals(ROOT_LOGGER_NAME))
					loggername = "";
				
				if (logger.isInfoEnabled()) {
					if (loggername.length() == 0)
						logger.info("Found new handlers for root logger: '" + value + "'");
					else
						logger.info("Found new handlers for logger '" + loggername + "': '" + value + "'");
				}
				
				StringTokenizer tokenizer = new StringTokenizer(value);
				while (tokenizer.hasMoreTokens()) {
					String handlername = tokenizer.nextToken();
					setHandler(handlername, properties, handlername);
					Handler handler = getHandler(handlername);
					if (handler != null) {
						if (logger.isInfoEnabled()) {
							if (loggername.length() == 0)
								logger.info("Add handler for root logger: '" + handler + "'");
							else
								logger.info("Add handler for logger '" + loggername + "': '" + handler + "'");
						}
						Logger.getLogger(loggername).addHandler(handler);
					}
				}
			}
		}
	}

	/**
	 * Set the {@link Level log level} of the loggers.
	 * 
	 * Properties <code>level</code> and <code>.level</code> define
	 * the log level of the root logger.
	 * 
	 * Properties like <code>abc.my.logger.level</code> will set
	 * the level of the {@link Logger logger} "abc.my.logger".
	 * 
	 * @param properties
	 */
	protected void configureLoggers(Properties properties) {
		Enumeration keys = properties.keys();
		while (keys.hasMoreElements()) {
			String key = (String)keys.nextElement();
			String value = properties.getProperty(key);
			
			if (key == null || (key = key.trim()).length() == 0)
				continue;
			if (value == null || (value = value.trim()).length() == 0)
				continue;
			
			// Set logger level
			if (key.equals(LEVEL) || key.endsWith(LEVEL_SUFFIX)) {
				String loggername;
				if (key.length() == LEVEL.length())
					loggername = "";
				else
					loggername = key.substring(0, key.length() - LEVEL_SUFFIX.length());
				if (loggername.equals(ROOT_LOGGER_NAME))
					loggername = "";
				
				try {
					Logger.getLogger(loggername).setLevel(Level.parse(value));
				} catch (IllegalArgumentException e) {
					logger.warn("Bad level '" + value + "' for '" + key + "' logger.", e);
				}
			}
			
			if (key.equals(USE_PARENT) || key.endsWith(USE_PARENT_SUFFIX)) {
				String loggername;
				if (key.length() == USE_PARENT.length())
					loggername = "";
				else
					loggername = key.substring(0, key.length() - USE_PARENT_SUFFIX.length());
				if (loggername.equals(ROOT_LOGGER_NAME))
					loggername = "";
				
				Logger.getLogger(loggername).setUseParentHandlers(Boolean.valueOf(value).booleanValue());
			}
		}
	}

	/**
	 * Return true if the {@link Handler handler} behind the name
	 * <code>handlername</code> is available. False otherwise.
	 * 
	 * @param handlername
	 * @return
	 */
	public boolean isHandlerAvailable(String handlername) {
		return handlerPool.containsKey(handlername);
	}

	/**
	 * Get an {@link Handler handler} instance from the internal
	 * logmanager pool. Null if no handler for this name is available.
	 * 
	 * @param handlername
	 * @return
	 */
	public Handler getHandler(String handlername) {
		return (Handler)handlerPool.get(handlername);
	}

	/**
	 * Return a new instance of a {@link Handler handler} for the given handlername.
	 * 
	 * @param handlerPool
	 * @param handlername
	 * @param properties
	 * @return
	 */
	public void setHandler(String handlername, Properties properties, String propertyPrefix) {
		try {
			// Return if already a handler available.
			if (isHandlerAvailable(handlername))
				return;
			
			// Otherwise load the class.
			String classname = getParameter(properties, propertyPrefix, "class", null);
			String level = getParameter(properties, propertyPrefix, "level", null);
			Handler handler;
			
			// No classname found.
			if (classname == null || classname.length() == 0) {
				logger.warn("No classname for handler '" + handlername + "' found.");
				return;
			} else
			
			// ConsoleHandler
			if (classname.equals("java.util.logging.ConsoleHandler")) {
				handler = new ConsoleHandler();
			} else
			
			// FileHandler
			if (classname.equals("java.util.logging.FileHandler")) {
				handler = new FileHandler(
						getParameter(properties, propertyPrefix, "pattern", null),
						getParameter(properties, propertyPrefix, "limit", 0),
						getParameter(properties, propertyPrefix, "limit", 1),
						getParameter(properties, propertyPrefix, "append", false));
			} else
			
			// SocketHandler
			if (classname.equals("java.util.logging.SocketHandler")) {
				handler =  new SocketHandler(
						getParameter(properties, propertyPrefix, "host", "127.0.0.1"),
						getParameter(properties, propertyPrefix, "port", 0));
			} else
			
			// All other classes
			if (true) {
				Class clazz = Class.forName(classname);
				Method setter = null;
				handler = (Handler)clazz.newInstance();
				
				if (propertyPrefix == null)
					propertyPrefix = "";
				
				Enumeration keys = properties.keys();
				while (keys.hasMoreElements()) {
					String key = (String)keys.nextElement();
					if (!key.startsWith(propertyPrefix))
						continue;
					
					key = key.substring(propertyPrefix.length());
					String value = getParameter(properties, propertyPrefix, key, null);
					
					System.out.println(" -> " + key + " -> " + value);
					
					// TODO Set attributes of a handler can also be maked over an common function.
					if (value != null && !(key.equals("class") || key.equals("level"))) {
						setter = clazz.getMethod("set" +
								key.substring(0, 1).toUpperCase() +
								key.substring(1),
								new Class[] { String.class });
						if (setter != null) {
							setter.invoke(handler, new Object[] { value });
						} else {
							logger.warn("No setter for " + key + " on '" + handler + "' found.");
						}
					}
				}
			}
			
			if (level != null && level.length() != 0) {
				try {
					handler.setLevel(Level.parse(level));
				} catch (IllegalArgumentException e) {
					logger.warn("Bad level '" + level + "' for '" + handler + "'.", e);
				}
			}
			
			// TODO externalize string
			if (logger.isInfoEnabled())
				logger.info("Add new handler '" + handlername + "' (" + handler + ").");
			handlerPool.put(handlername, handler);
			
		} catch (Exception e) {
			logger.error(UNEXPECTED_EXCEPTION.getMessage(e.getLocalizedMessage()), e);
		}
	}

	protected String getParameter(Properties properties, String propertyPrefix, String key, String def) {
		String value = properties.getProperty(getPropertyName(propertyPrefix, key));
		if (value == null || (value = value.trim()).length() == 0)
			return def;
		else
			return value;
	}

	protected int getParameter(Properties properties, String propertyPrefix, String key, int def) {
		try {
			String value = properties.getProperty(getPropertyName(propertyPrefix, key));
			return Integer.parseInt(value);
		} catch (Exception e) {
			return def;
		}
	}

	protected boolean getParameter(Properties properties, String propertyPrefix, String key, boolean def) {
		try {
			String value = properties.getProperty(getPropertyName(propertyPrefix, key));
			return Boolean.valueOf(value).booleanValue();
		} catch (Exception e) {
			return def;
		}
	}

	protected String getPropertyName(String propertyPrefix, String key) {
		if (propertyPrefix == null || propertyPrefix.length() == 0)
			return key;
		else
			return propertyPrefix + "." + key;
	}
}
