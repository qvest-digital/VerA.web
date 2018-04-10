package de.tarent.commons.action;

public class ActionDefinitionException extends Exception {

	private static final long	serialVersionUID	= -5748100286171926561L;

	private String actionUniqueName;

	public ActionDefinitionException(String msg) {
	super(msg);
    }

    public ActionDefinitionException(Throwable t) {
	super(t);
    }

    public ActionDefinitionException(String msg, Throwable t) {
	super(msg, t);
    }

    public void setActionUniqueName(String actionUniqueName) {
	this.actionUniqueName = actionUniqueName;
    }

    public String getActionUniqueName() {
	return actionUniqueName;
    }
}
