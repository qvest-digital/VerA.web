package de.tarent.octopus.beans;
import de.tarent.dblayer.sql.Statement;
import de.tarent.dblayer.sql.SyntaxErrorException;
import lombok.extern.log4j.Log4j2;

import java.sql.PreparedStatement;

/**
 * This class is a base class for {@link BeanStatement} implementations. It holds
 * the statement in db layer, SQL, and prepared form.
 *
 * @author Michael Klink
 */
@Log4j2
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
        if (logger.isDebugEnabled()) {
            logger.debug("Created PreparedStatement for SQL statament <" + sqlStatement + ">.");
        }
    }

    //
    // geschätzte Variablen
    //
    /**
     * The statement is to be executes inside this context.
     */
    final ExecutionContext context;

    /**
     * This is the prepared form of the statement.
     */
    final PreparedStatement preparedStatement;

    /**
     * This is the db layer form of the statement.
     */
    final Statement dblayerStatement;

    /**
     * This is the SQL form of the statement.
     */
    final String sqlStatement;

    /**
     * place holder object for variables inside the prepared statement.
     */
    final static Object PLACE_HOLDER = new Object() {
        public String toString() {
            return "?";
        }
    };
}
