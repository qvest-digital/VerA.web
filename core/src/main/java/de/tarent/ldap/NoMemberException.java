package de.tarent.ldap;
public class NoMemberException extends LDAPException {
    private static final long serialVersionUID = 6191825942458988785L;

    public NoMemberException(String message) {
        super(message);
    }
}
