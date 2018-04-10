package de.tarent.octopus.logging;

/*-
 * VerA.web-Middleware, newly MIT licenced, is comprised of:
 * tarent-commons, a set of common components and solutions
 * tarent-contact, platform-independent webservice-based contact management
 * tarent-database, jdbc database library
 * tarent-doctor, Document Generation Platform
 * tarent-octopus, Webservice Data Integrator and Application Server
 *  © 2005, 2006, 2007 asteban <s.mancke@tarent.de>
 *  © 2007 David Goemans <d.goemans@tarent.de>
 *  © 2006, 2007, 2010 Hendrik Helwich <h.helwich@tarent.de>
 *  © 2005, 2006, 2007 Christoph Jerolimov <c.jerolimov@tarent.de>
 *  © 2006 Philipp Kirchner <p.kirchner@tarent.de>
 *  © 2010 Carsten Klein <c.klein@tarent.de>
 *  © 2006 Michael Kleinhenz <m.kleinhenz@tarent.de>
 *  © 2006 Michael Klink <m.klink@tarent.de>
 *  © 2007 Fabian Köster <f.koester@tarent.de>
 *  © 2006 Martin Ley <m.ley@tarent.de>
 *  © 2007 Alex Maier <a.maier@tarent.de>
 *  © 2007, 2015, 2017, 2018 mirabilos <t.glaser@tarent.de>
 *  © 2006, 2007 Jens Neumaier <j.neumaier@tarent.de>
 *  © 2006 Nils Neumaier <n.neumaier@tarent.de>
 *  © 2007, 2008 Martin Pelzer <m.pelzer@tarent.de>
 *  © 2008, 2009 Christian Preilowski <c.thiel@tarent.de>
 *  © 2006, 2008, 2009 Thomas Schmitz <t.schmitz@tarent.de>
 *  © 2007 Robert Schuster <r.schuster@tarent.de>
 * and older code, Copyright © 2001–2007 ⮡ tarent GmbH and contributors.
 * Licensor is tarent solutions GmbH, http://www.tarent.de/
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
import de.tarent.octopus.request.TcEnv;
import de.tarent.octopus.resource.Resources;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.impl.Jdk14Logger;
import org.apache.commons.logging.impl.Log4JLogger;
import org.apache.commons.logging.impl.SimpleLog;
import org.apache.log4j.xml.DOMConfigurator;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.Properties;
import java.util.logging.*;

/**
 * A simple factory which should be used to get a log instance in tarent projects.
 * This implementations use an on small configuration in a resource properties file
 * /tarent-logging.properties to decide which underlaying logging system should be used.
 *
 * This way makes it possible to use apache.commmons.logging in a shared environment
 * (i.e. J2EE) and without having to use the same underlying logging system.
 *
 * For configuration, there has to be a file /tarent-logging.properties in the
 * classpath with the single property <code>logging.api</code>.
 * Possible values for this Property are:
 * jdk (Java util logging backend), log4j (Log4j backend),
 * commons (apache commons default behavior), simple (apache commons simple logger)
 */
public class LogFactory {
    private static final String TARENT_LOGGING_PROPERTIES = "/tarent-logging.properties";

    private static final String LOGGING_API = "logging.api";

    private static final String LOGGING_API_JDK14 = "jdk";
    private static final String LOGGING_API_LOG4J = "log4j";
    private static final String LOGGING_API_COMMONS = "commons";
    private static final String LOGGING_API_SIMPLE = "simple";

    private static final int JDK14_LOGGER = 1;
    private static final int LOG4J_LOGGER = 2;
    private static final int COMMONS_LOGGER = 3;
    private static final int SIMPLE_LOGGER = 4;

    private static int CHOSEN_LOGGER = JDK14_LOGGER;

    private static Logger baseLogger = null;
    private static FileHandler fileLogHandler = null;
    private static SocketHandler portLogHandler = null;

    private static final String CONFIG = "CONFIG";
    private static final String ERROR = "ERROR";
    private static final String FATAL = "FATAL";
    private static final String WARNING = "WARNING";

    static {
        loadProperties();
    }

    public static Log getLog(Class clazz) {
        if (useJdkLogger()) {
            return new Jdk14Logger(clazz.getName());
        } else if (useLog4jLogger()) {
            return new Log4JLogger(clazz.getName());
        } else if (useCommonsLogger()) {
            return org.apache.commons.logging.LogFactory.getLog(clazz);
        } else if (useSimpleLog()) {
            return new SimpleLog(clazz.getName());
        } else {
            return org.apache.commons.logging.LogFactory.getLog(clazz);
        }
    }

    private static void loadProperties() {
        InputStream in = LogFactory.class.getResourceAsStream(TARENT_LOGGING_PROPERTIES);
        if (in != null) {
            Properties properties = new Properties();
            try {
                properties.load(in);
                Object value = properties.get(LOGGING_API);
                if (LOGGING_API_JDK14.equals(value)) {
                    CHOSEN_LOGGER = JDK14_LOGGER;
                } else if (LOGGING_API_LOG4J.equals(value)) {
                    CHOSEN_LOGGER = LOG4J_LOGGER;
                } else if (LOGGING_API_COMMONS.equals(value)) {
                    CHOSEN_LOGGER = COMMONS_LOGGER;
                } else if (LOGGING_API_SIMPLE.equals(value)) {
                    CHOSEN_LOGGER = SIMPLE_LOGGER;
                }
            } catch (IOException e) {
                log(FATAL, "Error while reading logging configuration from resource: " +
                        TARENT_LOGGING_PROPERTIES, e);
            }
        }
    }

    /**
     * initialise the logging.
     */
    public static void initOctopusLogging(TcEnv env) throws IOException {
        if (useJdkLogger()) {
            initJdkOctopusLogging(env);
        } else if (useLog4jLogger()) {
            initLog4jOctopusLogging(env);
        }
    }

    private static void initLog4jOctopusLogging(TcEnv env) {
        String rootPath = env.getValueAsString(TcEnv.KEY_PATHS_ROOT);
        File f = new File(rootPath, "log4j_properties.xml");
        if (!f.exists()) {
            log(WARNING, "log4j configuration file '" + f.getAbsolutePath() + "' does not exist", null);
        } else {
            DOMConfigurator.configure(f.getAbsolutePath());
        }
    }

    private static void initJdkOctopusLogging(TcEnv env) throws IOException {
        if (baseLogger != null) {
            return;
        }

        String baseLoggerPackage = "de.tarent.octopus";
        if (env.getValue(TcEnv.KEY_LOGGING_BASELOGGER) != null) {
            baseLoggerPackage = env.getValueAsString(TcEnv.KEY_LOGGING_BASELOGGER);
        }
        baseLogger = Logger.getLogger(baseLoggerPackage);
        baseLogger.setLevel(Level.ALL);

        String pattern = env.getValueAsString(TcEnv.KEY_LOGGING_PATTERN);

        // get absolute pattern file
        if (pattern == null || pattern.trim().length() == 0) {
            // if no pattern defined, fall back to default filename
            String rootPath = env.getValueAsString(TcEnv.KEY_PATHS_ROOT);
            String logPath = env.getValueAsString(TcEnv.KEY_PATHS_LOGFILE);
            if (logPath == null || logPath.trim().length() == 0) {
                pattern = new File(rootPath, "log/octopus-%g_%u.log").getAbsolutePath();
            } else {
                logPath = expandSystemProperties(logPath);
                if (new File(logPath).isAbsolute()) {
                    pattern = new File(logPath).getAbsolutePath();
                } else {
                    pattern = new File(rootPath, logPath).getAbsolutePath();
                }
            }
        } else {
            // if pattern defined, use this with root and log path (optional)
            String rootPath = env.getValueAsString(TcEnv.KEY_PATHS_ROOT);
            String logPath = env.getValueAsString(TcEnv.KEY_PATHS_LOGFILE);
            if (logPath == null || logPath.trim().length() == 0) {
                pattern = new File(rootPath, pattern).getAbsolutePath();
            } else {
                logPath = expandSystemProperties(logPath);
                if (new File(logPath).isAbsolute()) {
                    pattern = new File(logPath, pattern).getAbsolutePath();
                } else {
                    pattern = new File(new File(rootPath, logPath), pattern).getAbsolutePath();
                }
            }
        }
        log(CONFIG, Resources.getInstance().get("REQUESTPROXY_LOG_START_LOGGING_TO", pattern), null);

        // get maximum size of logging file
        int loggingLimit = 4 * 1024 * 1024; // default 4 MiB
        try {
            String param = env.getValueAsString(TcEnv.KEY_LOGGING_LIMIT);
            if (param != null && param.trim().equals("disabled")) {
                loggingLimit = 0;
            } else if (param != null && param.trim().length() != 0 && !param.trim().equals("default")) {
                loggingLimit = Integer.parseInt(param.trim());
            }
        } catch (NumberFormatException e) {
            log(ERROR, Resources.getInstance().get("REQUESTPROXY_LOG_PARSEERROR_LIMIT", "4 MiB"), e);
        }

        // get maximum logfile count
        int loggingCount = 10; // default 10 files
        try {
            String param = env.getValueAsString(TcEnv.KEY_LOGGING_COUNT);
            if (param != null && param.trim().equals("disabled")) {
                loggingCount = 1;
            } else if (param != null && param.trim().length() != 0 && !param.trim().equals("default")) {
                loggingCount = Integer.parseInt(param.trim());
            }
        } catch (NumberFormatException e) {
            log(ERROR, Resources.getInstance().get("REQUESTPROXY_LOG_PARSEERROR_LIMIT", "10"), e);
        }

        // initialise FileHandle
        fileLogHandler = new FileHandler(pattern, loggingLimit, loggingCount, true);
        fileLogHandler.setEncoding("UTF-8");
        fileLogHandler.setFormatter(new SimpleFormatter());
        baseLogger.addHandler(fileLogHandler);

        // append optional socket logger
        int loggingPort = -1;
        try {
            String param = env.get("logging.port");
            if (param != null && param.trim().length() != 0) {
                loggingPort = Integer.decode(param);
            }
        } catch (NumberFormatException e) {
            log(ERROR, "Fehler beim Parsen des Log-Ports; benutze Default-Wert", e);
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
                log(ERROR, "Konnte SocketHandler nicht initialisieren", e);
            }
        }

        Logger xlogger = baseLogger;
        int xlogger_parent = 0;
        while (xlogger != null) {
            final Handler[] loggerHandlers = xlogger.getHandlers();
            int xlogger_element = 0;
            for (Handler handler : loggerHandlers) {
                if (handler != fileLogHandler &&
                        (portLogHandler == null || handler != portLogHandler)) {
                    String enc = handler.getEncoding();
                    if (enc == null) {
                        enc = Charset.defaultCharset().name();
						/*if (enc == null) {
							baseLogger.severe("Logger parent(" + xlogger_parent +
							    ") element(" + xlogger_element + ") name(" + handler +
							    ") encoding is nonexistent");
						} else*/
                        if (enc.equals("US-ASCII")) {
                            baseLogger.warning("Logger parent(" + xlogger_parent +
                                    ") element(" + xlogger_element +
                                    ") name(" + handler +
                                    ") encoding is 7-bit ASCII (platform encoding, LC_CTYPE=C; consider export LC_CTYPE=C.UTF-8" +
                                     " or starting the JVM with -Dfile.encoding=UTF-8 to fix this; umlauts *will* be broken)");
                        } else if (!enc.equals("UTF-8")) {
                            baseLogger.info("Logger parent(" + xlogger_parent +
                                    ") element(" + xlogger_element + ") name(" + handler +
                                    ") encoding is '" + enc +
                                    "' (platform encoding; consider export LC_CTYPE=C.UTF-8 or starting the JVM with -Dfile" +
                                     ".encoding=UTF-8 to fix this)");
                        }
                    } else if (enc.equals("US-ASCII")) {
                        baseLogger.warning("Logger parent(" + xlogger_parent +
                                ") element(" + xlogger_element + ") name(" + handler +
                                ") encoding is '" + enc + "' (manually configured)");
                    } else if (!enc.equals("UTF-8")) {
                        baseLogger.info("Logger parent(" + xlogger_parent +
                                ") element(" + xlogger_element + ") name(" + handler +
                                ") encoding is '" + enc + "' (manually configured)");
                    }
                }
                xlogger_element++;
            }

            final boolean useParentHdls = xlogger.getUseParentHandlers();
            if (!useParentHdls) {
                break;
            }

            xlogger = xlogger.getParent();
            xlogger_parent++;
        }

        baseLogger.info(Resources.getInstance().get("REQUESTPROXY_LOG_START_LOGGING"));

        // set logging.level
        String loggingLevel = env.getValueAsString(TcEnv.KEY_LOGGING_LEVEL);
        try {
            Level level = Level.parse(loggingLevel);
            baseLogger.config(Resources.getInstance().get("REQUESTPROXY_LOG_NEW_LOG_LEVEL", level));
            baseLogger.setLevel(level);
        } catch (IllegalArgumentException e) {
            baseLogger.log(Level.WARNING,
                    Resources.getInstance().get("REQUESTPROXY_LOG_INVALID_LOG_LEVEL", loggingLevel), e);
        }
    }

    public static void deInitOctopusLogging() {
        if (useJdkLogger()) {
            deInitJdkOctopusLogging();
        } else if (useLog4jLogger()) {
            deInitLog4jOctopusLogging();
        }
    }

    private static void deInitLog4jOctopusLogging() {
        org.apache.log4j.LogManager.shutdown();
    }

    private static void deInitJdkOctopusLogging() {
        if (baseLogger != null) {
            baseLogger.removeHandler(fileLogHandler);
            if (portLogHandler != null) {
                baseLogger.removeHandler(portLogHandler);
            }
        }
        if (fileLogHandler != null) {
            fileLogHandler.close();
        }
    }

    private static boolean useJdkLogger() {
        return (JDK14_LOGGER == CHOSEN_LOGGER);
    }

    private static boolean useLog4jLogger() {
        return (LOG4J_LOGGER == CHOSEN_LOGGER);
    }

    private static boolean useCommonsLogger() {
        return (COMMONS_LOGGER == CHOSEN_LOGGER);
    }

    private static boolean useSimpleLog() {
        return (SIMPLE_LOGGER == CHOSEN_LOGGER);
    }

    private static void log(String level, String message, Exception e) {
        final Date dat = new Date();

        System.err.println(String.format("%1$tF %1$tT.%1$tL %2$7s (de.tarent.octopus.logging.LogFactory) [OCTOPUS] %3$s",
                dat, level, message));
        if (e != null) {
            e.printStackTrace(System.err);
        }
    }

    /**
     * Replaces all Variables ${key} with the corresponding system property
     */
    private static String expandSystemProperties(String string) {
        int startPos = string.indexOf("${");
        if (startPos == -1) {
            return string;
        }

        StringBuilder sb = new StringBuilder(string);
        while (-1 != startPos) {
            int endPos = sb.indexOf("}", startPos);
            String propertyName = sb.substring(startPos + 2, endPos);
            if (System.getProperty(propertyName) != null) {
                sb.replace(startPos, endPos + 1, System.getProperty(propertyName));
            }
            startPos = sb.indexOf("${", endPos);
        }
        return sb.toString();
    }
}
