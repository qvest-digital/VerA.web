/*
 * $Id: BeanBaseStatement.java,v 1.5 2007/06/11 13:24:36 christoph Exp $
 *
 * Created on 25.01.2006
 */
package de.tarent.octopus.beans;

import java.sql.PreparedStatement;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.tarent.dblayer.sql.Statement;
import de.tarent.dblayer.sql.SyntaxErrorException;

/**
 * This class is a base class for {@link BeanStatement} implementations. It holds
 * the statement in db layer, SQL, and prepared form.
 *
 * @author Michael Klink
 */
class BeanBaseStatement {
    //
    // Konstruktor
    //
    /**
     * This constructor prepares the given statement inside the given context.
     */
    public BeanBaseStatement(Statement statement, ExecutionContext context) throws BeanException {
        this.context = context;
        this.dblayerStatement = statement;
        this.preparedStatement = context.prepare(dblayerStatement);
        try {
            this.sqlStatement = dblayerStatement.statementToString();
        } catch (SyntaxErrorException e) {
            throw new BeanException("Syntax error in SQL statement", e);
        }
        if (logger.isLoggable(Level.FINE))
            logger.fine("Created PreparedStatement for SQL statament <" +sqlStatement + ">.");
    }

    //
    // geschätzte Variablen
    //
    /** The statement is to be executes inside this context. */
    final ExecutionContext context;

    /** This is the prepared form of the statement. */
    final PreparedStatement preparedStatement;

    /** This is the db layer form of the statement. */
    final Statement dblayerStatement;

    /** This is the SQL form of the statement. */
    final String sqlStatement;

    /** place holder object for variables inside the prepared statement. */
    final static Object PLACE_HOLDER = new Object() {
        public String toString() {
            return "?";
        }
    };

    /** logger of this class. */
    final static Logger logger = Logger.getLogger(BeanBaseStatement.class.getName());
}
