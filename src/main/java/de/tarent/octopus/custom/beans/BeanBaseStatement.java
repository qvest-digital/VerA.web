/*
 * $Id: BeanBaseStatement.java,v 1.1 2007/06/20 11:56:51 christoph Exp $
 * 
 * Created on 25.01.2006
 */
package de.tarent.octopus.custom.beans;

import java.sql.PreparedStatement;

import org.apache.log4j.Logger;

import de.tarent.dblayer.sql.Statement;
import de.tarent.dblayer.sql.SyntaxErrorException;

/**
 * Diese Klasse stellt eine Basisklasse für {@link BeanStatement}-Implementierungen
 * dar. Hier wird das zu Grunde liegende Statement in dblayer-, SQL- und vorbereiteter
 * Form gehalten. 
 * 
 * @author mikel
 */
class BeanBaseStatement {
    //
    // Konstruktor
    //
    /**
     * Dieser Konstruktor 
     */
    public BeanBaseStatement(Statement statement, ExecutionContext context) throws BeanException {
        this.context = context;
        this.dblayerStatement = statement;
        this.preparedStatement = context.prepare(dblayerStatement);
        try {
            this.sqlStatement = dblayerStatement.statementToString();
        } catch (SyntaxErrorException e) {
            throw new BeanException("Syntax-Fehler im SQL-Statement", e);
        }
        if (logger.isDebugEnabled())
            logger.debug("PreparedStatement mit SQL-Statament <" +sqlStatement + "> erstellt.");
    }

    //
    // geschützte Variablen
    //
    /** In diesem Ausführungskontext wird ge-UPDATE-t. */
    final ExecutionContext context;
    
    /** Dieses PreparedStatement update-t. */
    final PreparedStatement preparedStatement;

    /** dbLayer-Statement */
    final Statement dblayerStatement;
    
    /** SQL-Statament */
    final String sqlStatement; 
    
    /** Platzhalterobjekt zum Aufbau von PreparedStatements. */
    final static Object PLACE_HOLDER = new Object() {
        public String toString() {
            return "?";
        }
    };
    
    /** Logger dieser Klasse */
    final static Logger logger = Logger.getLogger(BeanBaseStatement.class);
}
