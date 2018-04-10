package de.tarent.commons.logging;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.impl.Jdk14Logger;
import org.apache.commons.logging.impl.Log4JLogger;
import org.apache.commons.logging.impl.SimpleLog;

/**
 * A simple factory which should be used to get a log instance in tarent projects.
 * This implementations use an on small configuration in a ressource properties file /tarent-logging.properties
 * to decide which underlaying logging system should be used.
 *
 * <p>This way makes it possible to use apache.commmons.logging in a shared environment (i.e. J2EE) an
 * without having to use the same underlying logging system.<p/>
 *
 * <p>For configuration, there has to be a file /tarent-logging.properties in the classpath with the single property <code>logging.api</code>.
 * Possible values for this Property are: jdk (Java util logging backend), log4j (Log4j backend), commons (apache commons default behavior), simple (apache commons simple logger)<p/>
 */
public class LogFactory {

    public static final String TARENT_LOGGING_PROPERTIES = "/tarent-logging.properties";

    public static final String LOGGING_API = "logging.api";

    public static final String LOGGING_API_JDK14 = "jdk";
    public static final String LOGGING_API_LOG4J = "log4j";
    public static final String LOGGING_API_COMMONS = "commons";
    public static final String LOGGING_API_SIMPLE = "simple";

    protected static final int JDK14_LOGGER = 1;
    protected static final int LOG4J_LOGGER = 2;
    protected static final int COMMONS_LOGGER = 3;
    protected static final int SIMPLE_LOGGER = 4;

    static int logger = JDK14_LOGGER;

	static {
		loadProperties();
	}

	public static Log getLog(Class clazz) {
		if (useJdkLogger())
			return new Jdk14Logger(clazz.getName());
		else if (useLog4jLogger())
			return new Log4JLogger(clazz.getName());
		else if (useCommonsLogger())
			return org.apache.commons.logging.LogFactory.getLog(clazz);
		else if (useSimpleLog())
			return new SimpleLog(clazz.getName());
		else
			return org.apache.commons.logging.LogFactory.getLog(clazz);
    }

	public static void loadProperties() {
        InputStream in = LogFactory.class.getResourceAsStream(TARENT_LOGGING_PROPERTIES);
        if (in != null) {
            Properties properties = new Properties();
            try {
                properties.load(in);
                Object value = properties.get(LOGGING_API);
                if (LOGGING_API_JDK14.equals(value))
                    logger = JDK14_LOGGER;
                else if (LOGGING_API_LOG4J.equals(value))
                    logger = LOG4J_LOGGER;
                else if (LOGGING_API_COMMONS.equals(value))
                    logger = COMMONS_LOGGER;
                else if (LOGGING_API_SIMPLE.equals(value))
                    logger = SIMPLE_LOGGER;
            } catch (IOException e) {
                log("FATAL: Error while reading logging configuration from ressource: "+TARENT_LOGGING_PROPERTIES, e);
            }
        }
    }

	static boolean useJdkLogger() {
		return (JDK14_LOGGER  == logger);
	}

	static boolean useLog4jLogger() {
		return (LOG4J_LOGGER  == logger);
	}

	static boolean useCommonsLogger() {
		return (COMMONS_LOGGER  == logger);
	}

	static boolean useSimpleLog() {
		return (SIMPLE_LOGGER  == logger);
	}

    public static void log(String message, Exception e) {
        System.err.println(message);
        if (e != null)
            e.printStackTrace(System.err);
    }
}
