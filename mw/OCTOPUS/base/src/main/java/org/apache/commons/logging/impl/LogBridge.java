package org.apache.commons.logging.impl;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogConfigurationException;
import org.apache.logging.log4j.Logger;

/**
 * commons-logging-compatible wrapper for log4j 2
 * (kinda like log4j-jcl except without requiring
 * commons-logging itself on the classpath)
 *
 * @author mirabilos (t.glaser@tarent.de)
 */
public final class LogBridge implements Log {
    private final Logger l;

    LogBridge(final Logger loggerToUse) {
        if (loggerToUse == null) {
            throw new LogConfigurationException();
        }
        l = loggerToUse;
    }

    @Override
    public void trace(final Object msg) {
        l.trace(msg);
    }

    @Override
    public void trace(final Object msg, final Throwable e) {
        l.trace(msg, e);
    }

    @Override
    public void debug(final Object msg) {
        l.debug(msg);
    }

    @Override
    public void debug(final Object msg, final Throwable e) {
        l.debug(msg, e);
    }

    @Override
    public void info(final Object msg) {
        l.info(msg);
    }

    @Override
    public void info(final Object msg, final Throwable e) {
        l.info(msg, e);
    }

    @Override
    public void warn(final Object msg) {
        l.warn(msg);
    }

    @Override
    public void warn(final Object msg, final Throwable e) {
        l.warn(msg, e);
    }

    @Override
    public void error(final Object msg) {
        l.error(msg);
    }

    @Override
    public void error(final Object msg, final Throwable e) {
        l.error(msg, e);
    }

    @Override
    public void fatal(final Object msg) {
        l.fatal(msg);
    }

    @Override
    public void fatal(final Object msg, final Throwable e) {
        l.fatal(msg, e);
    }

    @Override
    public boolean isTraceEnabled() {
        return l.isTraceEnabled();
    }

    @Override
    public boolean isDebugEnabled() {
        return l.isDebugEnabled();
    }

    @Override
    public boolean isInfoEnabled() {
        return l.isInfoEnabled();
    }

    @Override
    public boolean isWarnEnabled() {
        return l.isWarnEnabled();
    }

    @Override
    public boolean isErrorEnabled() {
        return l.isErrorEnabled();
    }

    @Override
    public boolean isFatalEnabled() {
        return l.isFatalEnabled();
    }
}
