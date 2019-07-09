package org.apache.commons.logging;
/**
 * commons-logging 1.2 Log interface
 */
@SuppressWarnings("unused")
public interface Log {
    void trace(Object msg);

    void trace(Object msg, Throwable e);

    void debug(Object msg);

    void debug(Object msg, Throwable e);

    void info(Object msg);

    void info(Object msg, Throwable e);

    void warn(Object msg);

    void warn(Object msg, Throwable e);

    void error(Object msg);

    void error(Object msg, Throwable e);

    void fatal(Object msg);

    void fatal(Object msg, Throwable e);

    boolean isTraceEnabled();

    boolean isDebugEnabled();

    boolean isInfoEnabled();

    boolean isWarnEnabled();

    boolean isErrorEnabled();

    boolean isFatalEnabled();
}
