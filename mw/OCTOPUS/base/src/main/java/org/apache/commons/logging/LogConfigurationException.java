package org.apache.commons.logging;
/**
 * Helper class for {@link LogFactory}
 */
public class LogConfigurationException extends RuntimeException {
    private static final long serialVersionUID = -2697289265806698357L;

    protected final Throwable cause;

    public LogConfigurationException() {
        super();
        cause = null;
    }

    public LogConfigurationException(final String msg) {
        super(msg);
        cause = null;
    }

    public LogConfigurationException(final Throwable e) {
        super(e);
        cause = e;
    }

    public LogConfigurationException(final String msg, final Throwable e) {
        super(msg, e);
        cause = e;
    }

    @Override
    public Throwable getCause() {
        return cause;
    }
}                                                                                                                
