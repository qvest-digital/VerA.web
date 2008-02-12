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
 * $Id: BeanInsertStatement.java,v 1.1 2007/06/20 11:56:51 christoph Exp $
 * 
 * Created on 25.01.2006
 */
package de.tarent.octopus.custom.beans;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import de.tarent.dblayer.sql.statement.Insert;

/**
 * Diese Klasse stellt ein INSERT-PreparedStatement dar, dass in einem
 * {@link ExecutionContext} arbeitet und seine Daten aus einer {@link Bean}
 * bezieht.<br>
 * TODO: ID-Pre- und -Post-Fetch �ber aggregierte BeanSelectStatements umsetzen.
 * 
 * @author mikel
 */
class BeanInsertStatement extends BeanBaseStatement implements BeanStatement {
    //
    // Konstruktor
    //
    /**
     * Dieser Konstruktor legt den �bergebenen Kontext ab und erzeugt
     * das PreparedStatement.
     * 
     * @param insert das dbLayer-Insert-Statement
     * @param fieldsInInsert Felder f�r die Platzhalter im Statement
     * @param context Ausf�hrungskontext des PreparedStatements
     */
    BeanInsertStatement(Insert insert, List fieldsInInsert, ExecutionContext context) throws BeanException {
        super(insert, context);
        this.fields = fieldsInInsert;
    }

    //
    // Schnittstelle BeanStatement
    //
    /**
     * Diese Methode f�hrt das Insert auf der �bergebenen Bean aus. 
     * 
     * @param bean Bean, bez�glich deren Daten das Insert ausgef�hrt werden soll.
     * @return Anzahl Inserts
     * @see de.tarent.octopus.custom.beans.BeanStatement#execute(de.tarent.octopus.custom.beans.Bean)
     */
    public int execute(Bean bean) throws BeanException {
        if (bean != null)
            try {
                preparedStatement.clearParameters();
                List params = new ArrayList(); 
                for (int index = 0; index < fields.size(); index++) {
                    String field = fields.get(index).toString();
                    Object value = bean.getField(field);
                    preparedStatement.setObject(index + 1, value);
                    params.add(value);
                }
                if (logger.isDebugEnabled())
                    logger.debug("PreparedStatement <" + sqlStatement + "> wird mit Parametern " + params + " aufgerufen.");
                return preparedStatement.executeUpdate();
            } catch (SQLException se) {
                throw new BeanException("Fehler beim Ausf�hren des PreparedInserts <" + sqlStatement + "> mit der Bean <" + bean + ">", se);
            }
        else
            logger.warn("Aufruf ohne Bean nicht erlaubt");
        return 0;
    }

    //
    // Hilfsmethoden
    //
    //
    // gesch�tzte Variablen
    //
    /** Hier sind die Felder zu den Platzhaltern im PreparedStatement aufgelistet. */
    final List fields;
    
    /** Logger dieser Klasse */
    final static Logger logger = Logger.getLogger(BeanUpdateStatement.class);
}
