/*
 * tarent-octopus bean extension,
 * an opensource webservice and webapplication framework (bean extension)
 * Copyright (c) 2006-2007 tarent GmbH
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
 * interest in the program 'tarent-octopus bean extension'
 * Signature of Elmar Geese, 11 June 2007
 * Elmar Geese, CEO tarent GmbH.
 */

/*
 * $Id: BeanUpdateStatement.java,v 1.4 2007/06/11 13:24:36 christoph Exp $
 * 
 * Created on 20.01.2006
 */
package de.tarent.octopus.beans;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.tarent.dblayer.sql.statement.Update;

/**
 * Diese Klasse stellt ein UPDATE-PreparedStatement dar, dass in einem
 * {@link ExecutionContext} arbeitet und seine Daten aus einer {@link Bean}
 * bezieht.
 * 
 * @author mikel
 */
class BeanUpdateStatement extends BeanBaseStatement implements BeanStatement {
    //
    // Konstruktor
    //
    /**
     * Dieser Konstruktor legt den übergebenen Kontext ab und erzeugt
     * das PreparedStatement.
     * 
     * @param update das dbLayer-Update-Statement
     * @param fieldsInUpdate Felder für die Platzhalter im Statement
     * @param context Ausführungskontext des PreparedStatements
     */
    BeanUpdateStatement(Update update, List fieldsInUpdate, ExecutionContext context) throws BeanException, IOException {
        super(update, context);
        this.fields = fieldsInUpdate;
    }

    //
    // Schnittstelle BeanStatement
    //
    /**
     * Diese Methode führt das Update auf der übergebenen Bean aus.<br>
     * TODO: Falls Feld <code>null</code>, so muss (?) mit setNull, nicht setObject gearbeitet werden
     * 
     * @param bean Bean, bezüglich deren Daten das Update ausgeführt werden soll.
     * @return Anzahl Updates
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
                if (logger.isLoggable(Level.FINE))
                    logger.fine("PreparedStatement <" + sqlStatement + "> wird mit Parametern " + params + " aufgerufen.");
                return preparedStatement.executeUpdate();
            } catch (SQLException se) {
                throw new BeanException("Fehler beim Ausführen des PreparedUpdates <" + sqlStatement + "> mit der Bean <" + bean + ">", se);
            }
        else
            logger.warning("Aufruf ohne Bean nicht erlaubt");
        return 0;
    }
    
    //
    // geschätzte Variablen
    //
    /** Hier sind die Felder zu den Platzhaltern im PreparedStatement aufgelistet. */
    final List fields;
    
    /** logger of this class. */
    final static Logger logger = Logger.getLogger(BeanUpdateStatement.class.getName());
}
