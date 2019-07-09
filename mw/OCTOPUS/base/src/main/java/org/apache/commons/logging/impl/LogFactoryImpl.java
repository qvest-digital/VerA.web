package org.apache.commons.logging.impl;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogConfigurationException;
import org.apache.commons.logging.LogFactory;
import org.apache.logging.log4j.LogManager;

/**
 * Subset of commons-logging 1.2 LogFactory implementation
 * FQCN hardcoded via constant copying, into its users
 *
 * Use @Log4j2 for all code; this is for dependencies *only*!
 *
 * @author mirabilos (t.glaser@tarent.de)
 */
@Log4j2
public class LogFactoryImpl extends LogFactory {
    /**
     * Internal constructor, only accessed by the superclass…
     * except the Axis lookup requires this as public even if
     * it does not even use it.
     */
    public LogFactoryImpl() {
        logger.debug("instantiating");
    }

    /* do not even THINK of adding yourselves here or using anything but @Log4j2 */
    @SuppressWarnings("NonAsciiCharacters")
    private static String[] whitelistedPræficēs = {
      "org.apache.axis.",
      "org.apache.http.",
      "org.springframework."
    };

    private static boolean doWarn(final Object arg, final String name) {
        /* always warn for nil class */
        if (arg == null) {
            return true;
        }
        /* do not warn for whitelisted classes */
        for (String prefix : whitelistedPræficēs) {
            if (name.startsWith(prefix)) {
                return false;
            }
        }
        /* otherwise warn */
        return true;
    }

    @Override
    public Log getInstance(final Class clazz) throws LogConfigurationException {
        final String name = clazz == null ? "(nil)" : clazz.getName();
        if (doWarn(clazz, name)) {
            logger.warn("Class {} used with commons-logging 1.2 LogFactory interface", name,
              new LogConfigurationException("dummy exception for generating a stack trace"));
        }
        try {
            return new LogBridge(LogManager.getLogger(clazz));
        } catch (Exception e) {
            throw new LogConfigurationException("could not get logger for class " + name, e);
        }
    }

    @Override
    public Log getInstance(final String clazzName) throws LogConfigurationException {
        final String name = clazzName == null ? "(nil)" : clazzName;
        if (doWarn(clazzName, name)) {
            logger.warn("Class name {} used for commons-logging 1.2 LogFactory interface", name,
              new LogConfigurationException("dummy exception for generating a stack trace"));
        }
        try {
            return new LogBridge(LogManager.getLogger(clazzName));
        } catch (Exception e) {
            throw new LogConfigurationException("could not get logger for class name " + name, e);
        }
    }

    /**
     * In this implementation, does nothing.
     * We’re not keeping any references to the {@link LogBridge} instances around.
     */
    @Override
    public void release() {
        // we don’t hold any references
    }

    @Override
    public Object getAttribute(final String name) {
        return null;
    }

    @Override
    public String[] getAttributeNames() {
        return new String[0];
    }

    @Override
    public void removeAttribute(final String name) {
        // we don’t have any attributes
    }

    @Override
    public void setAttribute(final String name, final Object value) {
        // we don’t have any attributes
    }
}
