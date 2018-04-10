package de.tarent.commons.utils;

import de.tarent.commons.logging.LogFactory;

/**
 * This class is a static wrapper around the tarent-commons LogFactory, based on Apache Commons Logging.
 *
 * @author Michael Kleinhenz (m.kleinhenz@tarent.de)
 */
public class Log
{
    public static void info(Class source, Object message)
    {
        org.apache.commons.logging.Log log = LogFactory.getLog(source);
        log.info(message);
    }

    public static void debug(Class source, Object message)
    {
        org.apache.commons.logging.Log log = LogFactory.getLog(source);
        log.debug(message);
    }

    public static void debug(Class source, Throwable exception)
    {
        org.apache.commons.logging.Log log = LogFactory.getLog(source);
        log.debug("Exception thrown.", exception);
    }

    public static void fatal(Class source, Object message)
    {
        org.apache.commons.logging.Log log = LogFactory.getLog(source);
        log.fatal(message);
    }

    public static void error(Class source, Object message)
    {
        org.apache.commons.logging.Log log = LogFactory.getLog(source);
        log.error(message);
    }

    public static void fatal(Class source, Object message, Throwable throwable)
    {
        org.apache.commons.logging.Log log = LogFactory.getLog(source);
        log.fatal(message, throwable);
    }

    public static void error(Class source, Object message, Throwable throwable)
    {
        org.apache.commons.logging.Log log = LogFactory.getLog(source);
        log.error(message, throwable);
    }
}
