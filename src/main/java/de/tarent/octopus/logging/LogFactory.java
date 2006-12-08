package de.tarent.octopus.logging;

import de.tarent.octopus.request.TcEnv;
import de.tarent.octopus.resource.Resources;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.SocketHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.impl.Jdk14Logger;
import org.apache.commons.logging.impl.Log4JLogger;
import org.apache.commons.logging.impl.SimpleLog;
import org.apache.log4j.xml.DOMConfigurator;


/**
 * A simple factory which can be used to get a log instance.
 * This is due to the automatic log choosing in
 * {@link org.apache.commons.logging.LogFactory}
 * 
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

    private static Logger baseLogger = null;
    private static FileHandler fileLogHandler = null;
    private static SocketHandler portLogHandler = null;

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

    /**
     * Init. the logging.
     * 
     * @throws IOException
     */
	public static void initOctopusLogging(TcEnv env) throws IOException {
        if (useJdkLogger())
            initJdkOctopusLogging(env);
        else if (useLog4jLogger())
            initLog4jOctopusLogging(env);
    }
    
    public static void initLog4jOctopusLogging(TcEnv env) throws IOException {
        String rootPath = env.getValueAsString(TcEnv.KEY_PATHS_ROOT);
        File f = new File(rootPath, "log4j_properties.xml");
        if (!f.exists())
            log("WARNING: log4j configuration file '"+f.getAbsolutePath()+"' does not exist", null);
        else
            DOMConfigurator.configure(f.getAbsolutePath());
    }    

    public static void initJdkOctopusLogging(TcEnv env) throws IOException {
        if (baseLogger != null)
            return;

        String baseLoggerPackage = "de.tarent.octopus";
        if (env.getValue(TcEnv.KEY_LOGGING_BASELOGGER) != null)
            baseLoggerPackage = env.getValueAsString(TcEnv.KEY_LOGGING_BASELOGGER);
        baseLogger = Logger.getLogger(baseLoggerPackage);
        baseLogger.setLevel(Level.ALL);
        

		String pattern = env.getValueAsString(TcEnv.KEY_LOGGING_PATTERN);
		
		// Get absolut pattern file.
		if (pattern == null || pattern.trim().length() == 0) {
			// if no pattern defined, fallback to default filename.
			String rootPath = env.getValueAsString(TcEnv.KEY_PATHS_ROOT);
			String logPath = env.getValueAsString(TcEnv.KEY_PATHS_LOGFILE);
			if (logPath == null || logPath.trim().length() == 0) {
				pattern = new File(rootPath, "log/octopus-%g_%u.log").getAbsolutePath();
			} else {
                logPath = expandSystemProperties(logPath);
                if (new File(logPath).isAbsolute())
                    pattern = new File(logPath).getAbsolutePath();
                else
                    pattern = new File(rootPath, logPath).getAbsolutePath();
			}
		} else {
			// if pattern defined use this with root and log path (optional).
			String rootPath = env.getValueAsString(TcEnv.KEY_PATHS_ROOT);
			String logPath = env.getValueAsString(TcEnv.KEY_PATHS_LOGFILE);
			if (logPath == null || logPath.trim().length() == 0) {
				pattern = new File(rootPath, pattern).getAbsolutePath();
			} else {
                logPath = expandSystemProperties(logPath);
                if (new File(logPath).isAbsolute())
                    pattern = new File(logPath, pattern).getAbsolutePath();
                else
                    pattern = new File(new File(rootPath, logPath), pattern).getAbsolutePath();
			}
		}
		log(Resources.getInstance().get("REQUESTPROXY_LOG_START_LOGGING_TO", pattern), null);
		
		// Get maximum size of logging file.
		int loggingLimit = 4 * 1024 * 1024; // default 4 MB
		try {
			String param = env.getValueAsString(TcEnv.KEY_LOGGING_LIMIT);
			if (param != null && param.trim().equals("disabled")) {
				loggingLimit = 0;
			} else if (param != null && param.trim().length() != 0 && !param.trim().equals("default")) {
				loggingLimit = Integer.parseInt(param.trim());
			}
		} catch (NumberFormatException e) {
			log(Resources.getInstance().get("REQUESTPROXY_LOG_PARSEERROR_LIMIT", "4 MB"), e);
		}
		
		// Get maximum logfile count.
		int loggingCount = 10; // default 10 files
		try {
			String param = env.getValueAsString(TcEnv.KEY_LOGGING_COUNT);
			if (param != null && param.trim().equals("disabled")) {
				loggingCount = 1;
			} else if (param != null && param.trim().length() != 0 && !param.trim().equals("default")) {
				loggingCount = Integer.parseInt(param.trim());
			}
		} catch (NumberFormatException e) {
			log(Resources.getInstance().get("REQUESTPROXY_LOG_PARSEERROR_LIMIT", "10"), e);
		}
		
		// Init. FileHandle
		fileLogHandler = new FileHandler(pattern, loggingLimit, loggingCount, true);
		fileLogHandler.setEncoding("UTF-8");
		fileLogHandler.setFormatter(new SimpleFormatter());
		baseLogger.addHandler(fileLogHandler);
		
		// Append optional socket logger.
		int loggingPort = -1;
		try {
			String param = env.get("logging.port");
			if (param != null && param.trim().length() != 0) {
				loggingPort = Integer.decode(param).intValue();
			}
		} catch (NumberFormatException e) {
            log("Fehler beim Parsen des Log-Ports; benutze Default-Wert", e);
            loggingPort = 0;
		}
		if (loggingPort >= 0) {
			try {
				// TODO remove this 'aeon' hack!?
				portLogHandler = loggingPort == 0 ?
						new SocketHandler() :
						new SocketHandler("aeon", loggingPort);
				baseLogger.addHandler(portLogHandler);
			} catch (Exception e) {
				log("Konnte SocketHandler nicht initialisieren", e);
			}
		}
		
		baseLogger.info(Resources.getInstance().get("REQUESTPROXY_LOG_START_LOGGING"));
		
		// Set logging.level
		String loggingLevel = env.getValueAsString(TcEnv.KEY_LOGGING_LEVEL);
		try {
			Level level = Level.parse(loggingLevel);
			baseLogger.config(Resources.getInstance().get("REQUESTPROXY_LOG_NEW_LOG_LEVEL", level));
			baseLogger.setLevel(level);
		} catch (IllegalArgumentException e) {
			baseLogger.log(Level.WARNING, Resources.getInstance().get("REQUESTPROXY_LOG_INVALID_LOG_LEVEL", loggingLevel), e);
		}
	}

	public static void deInitOctopusLogging()  {
        if (useJdkLogger())
            deInitJdkOctopusLogging();
        else if (useLog4jLogger())
            deInitLog4jOctopusLogging();
    }
    
	public static void deInitLog4jOctopusLogging()  {
        org.apache.log4j.LogManager.shutdown();
    }
    
	public static void deInitJdkOctopusLogging() {
        if (baseLogger != null)
            baseLogger.removeHandler(fileLogHandler);
        if (portLogHandler != null)
            baseLogger.removeHandler(portLogHandler);
        if (fileLogHandler != null)
            fileLogHandler.close();
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
	

    /**
     * Replaces all Variables ${key} with the corresponding system property
     */
    protected static String expandSystemProperties(String string) {
        int startPos = string.indexOf("${");
        if (startPos == -1)
            return string;

        StringBuffer sb = new StringBuffer(string); 
        while (-1 != startPos) {
            int endPos = sb.indexOf("}", startPos);
            String propertyName = sb.substring(startPos+2, endPos);
            if (System.getProperty(propertyName) != null)
                sb.replace(startPos, endPos+1, System.getProperty(propertyName));
            startPos = sb.indexOf("${", endPos);
        }
        return sb.toString();
    }

}
