package de.tarent.commons.datahandling.binding;

/**
 * Exception for binding related problems.
 */
public class BindingException extends RuntimeException {

    /** serialVersionUID */
	private static final long serialVersionUID = 2168903326018119682L;

	public BindingException(String msg) {
        super(msg);
    }

    public BindingException(String msg, Throwable t) {
        super(msg, t);
    }

}
