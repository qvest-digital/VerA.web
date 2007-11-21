/*
 * veraweb,
 * Veranstaltungsmanagment veraweb
 * Copyright (c) 2005-2007 tarent GmbH
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License,version 2
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 *
 * tarent GmbH., hereby disclaims all copyright
 * interest in the program 'veraweb'
 * Signature of Elmar Geese, 21 November 2007
 * Elmar Geese, CEO tarent GmbH.
 */

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
