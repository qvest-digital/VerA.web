/*
 * $Id: BeanUpdateStatement.java,v 1.1 2007/06/20 11:56:51 christoph Exp $
 * 
 * Created on 20.01.2006
 */
package de.tarent.octopus.custom.beans;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

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
    BeanUpdateStatement(Update update, List fieldsInUpdate, ExecutionContext context) throws BeanException {
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
                if (logger.isDebugEnabled())
                    logger.debug("PreparedStatement <" + sqlStatement + "> wird mit Parametern " + params + " aufgerufen.");
                return preparedStatement.executeUpdate();
            } catch (SQLException se) {
                throw new BeanException("Fehler beim Ausführen des PreparedUpdates <" + sqlStatement + "> mit der Bean <" + bean + ">", se);
            }
        else
            logger.warn("Aufruf ohne Bean nicht erlaubt");
        return 0;
    }
    
    //
    // geschützte Variablen
    //
    /** Hier sind die Felder zu den Platzhaltern im PreparedStatement aufgelistet. */
    final List fields;
    
    /** Logger dieser Klasse */
    final static Logger logger = Logger.getLogger("de.tarent.dblayer.sql.BeanUpdateStatement");
}
