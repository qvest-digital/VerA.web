package org.apache.commons.logging;
import org.apache.commons.logging.impl.LogBridge;
import org.apache.commons.logging.impl.LogFactoryImpl;

/**
 * Subset of commons-logging 1.2 LogFactory interface
 *
 * @author mirabilos (t.glaser@tarent.de)
 */
public abstract class LogFactory {
    /**
     * Empty default constructor, so we can instantiate the subclass.
     */
    protected LogFactory() {
    }

    private static LogFactory implementation = null;

    /**
     * Returns the singleton actual factory instance.
     *
     * @return {@link LogFactory}
     */
    public static LogFactory getFactory() throws LogConfigurationException {
        if (implementation == null) {
            implementation = new LogFactoryImpl();
        }
        return implementation;
    }

    /**
     * Throws away all internal references to any {@link LogFactory} instances
     * in the specified classloader, after calling {@link #release()} on them.
     *
     * In this implementation, identical to {@link #releaseAll()}.
     */
    public static void release(@SuppressWarnings("unused") final ClassLoader classLoader) {
        releaseAll();
    }

    /**
     * Throws away all internal references to any {@link LogFactory} instances
     * after calling {@link #release()} (see there) on them.
     */
    public static void releaseAll() {
        implementation = null;
    }

    /**
     * Returns a logger for the requested class.
     *
     * @param clazz {@link Class} to log for
     * @return {@link Log} instance ({@link LogBridge} to Log4j2)
     */
    public static Log getLog(final Class clazz) throws LogConfigurationException {
        return getFactory().getInstance(clazz);
    }

    /**
     * Returns a logger for the requested class name.
     *
     * @param clazzName {@link String} name of class to log for
     * @return {@link Log} instance ({@link LogBridge} to Log4j2)
     */
    public static Log getLog(final String clazzName) throws LogConfigurationException {
        return getFactory().getInstance(clazzName);
    }

    /**
     * Returns a logger for the requested class.
     *
     * @param clazz {@link Class} to log for
     * @return {@link Log} instance ({@link LogBridge} to Log4j2)
     */
    public abstract Log getInstance(final Class clazz) throws LogConfigurationException;

    /**
     * Returns a logger for the requested class name.
     *
     * @param clazzName {@link String} name of class to log for
     * @return {@link Log} instance ({@link LogBridge} to Log4j2)
     */
    public abstract Log getInstance(final String clazzName) throws LogConfigurationException;

    /**
     * Throws away all internal references to any {@link Log} instances.
     *
     * Some application server implementations handle reload by throwing
     * the classloader away, so it’s prudent to keep no references to
     * objects in those alive.
     */
    public abstract void release();

    /**
     * In this implementation, does nothing.
     *
     * @return null
     */
    public abstract Object getAttribute(final String name);

    /**
     * In this implementation, does nothing.
     *
     * @return zero-length array
     */
    public abstract String[] getAttributeNames();

    /**
     * In this implementation, does nothing.
     */
    public abstract void removeAttribute(final String name);

    /**
     * In this implementation, does nothing.
     */
    public abstract void setAttribute(final String name, final Object value);
}
