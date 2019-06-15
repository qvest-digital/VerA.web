package org.apache.commons.logging;

public interface Log {
    void debug(java.lang.Object msg);

    void debug(java.lang.Object msg, java.lang.Throwable e);

    void error(java.lang.Object msg);

    void error(java.lang.Object msg, java.lang.Throwable e);

    void fatal(java.lang.Object msg);

    void fatal(java.lang.Object msg, java.lang.Throwable e);

    void info(java.lang.Object msg);

    void info(java.lang.Object msg, java.lang.Throwable e);

    void trace(java.lang.Object msg);

    void trace(java.lang.Object msg, java.lang.Throwable e);

    void warn(java.lang.Object msg);

    void warn(java.lang.Object msg, java.lang.Throwable e);

    boolean isDebugEnabled();

    boolean isErrorEnabled();

    boolean isFatalEnabled();

    boolean isInfoEnabled();

    boolean isTraceEnabled();

    boolean isWarnEnabled();
}
