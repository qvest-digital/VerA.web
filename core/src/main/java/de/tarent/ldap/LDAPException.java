package de.tarent.ldap;
/**
 * Kapselt Exceptions aus dem LDAP
 *
 * @author philipp
 */
public class LDAPException extends Exception {
    private static final long serialVersionUID = -13354750513745321L;

    /**
     *
     */
    public LDAPException() {
        super();
    }

    /**
     * @param arg0 FIXME
     */
    public LDAPException(String arg0) {
        super(arg0);
    }

    /**
     * @param arg0 FIXME
     * @param arg1 FIXME
     */
    public LDAPException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    /**
     * @param arg0 FIXME
     */
    public LDAPException(Throwable arg0) {
        super(arg0);
    }
}
