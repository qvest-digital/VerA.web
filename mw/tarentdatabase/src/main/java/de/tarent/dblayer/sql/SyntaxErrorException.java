/*
 * $Id: SyntaxErrorException.java,v 1.3 2007/06/14 14:51:57 dgoema Exp $
 */
package de.tarent.dblayer.sql;

import java.sql.SQLException;

/**
 * This {@link Exception} class is a {@link SQLException} used for syntax
 * problems in a db layer model of a statement.
 */
public class SyntaxErrorException extends SQLException {
    /** The constructor stores an informational message. */
	public SyntaxErrorException(String msg) {
		super(msg);
	}

    /** serialization UID */
    private static final long serialVersionUID = 3257848787924955441L;
}
