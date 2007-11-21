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
 * TODO: ID-Pre- und -Post-Fetch über aggregierte BeanSelectStatements umsetzen.
 * 
 * @author mikel
 */
class BeanInsertStatement extends BeanBaseStatement implements BeanStatement {
    //
    // Konstruktor
    //
    /**
     * Dieser Konstruktor legt den übergebenen Kontext ab und erzeugt
     * das PreparedStatement.
     * 
     * @param insert das dbLayer-Insert-Statement
     * @param fieldsInInsert Felder für die Platzhalter im Statement
     * @param context Ausführungskontext des PreparedStatements
     */
    BeanInsertStatement(Insert insert, List fieldsInInsert, ExecutionContext context) throws BeanException {
        super(insert, context);
        this.fields = fieldsInInsert;
    }

    //
    // Schnittstelle BeanStatement
    //
    /**
     * Diese Methode führt das Insert auf der übergebenen Bean aus. 
     * 
     * @param bean Bean, bezüglich deren Daten das Insert ausgeführt werden soll.
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
                throw new BeanException("Fehler beim Ausführen des PreparedInserts <" + sqlStatement + "> mit der Bean <" + bean + ">", se);
            }
        else
            logger.warn("Aufruf ohne Bean nicht erlaubt");
        return 0;
    }

    //
    // Hilfsmethoden
    //
    //
    // geschützte Variablen
    //
    /** Hier sind die Felder zu den Platzhaltern im PreparedStatement aufgelistet. */
    final List fields;
    
    /** Logger dieser Klasse */
    final static Logger logger = Logger.getLogger(BeanUpdateStatement.class);
}
