package de.tarent.dblayer.engine;

import de.tarent.commons.logging.LogFactory;

public class Log {

	private static final org.apache.commons.logging.Log logger = LogFactory.getLog(Log.class);

	public static void logStatement(Object statement) {
		if (logger.isDebugEnabled())
			logger.debug(statement+"");
	}

	public static void logPool(String info) {
		if (logger.isDebugEnabled())
			logger.debug(info);
	}
}
