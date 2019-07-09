package de.tarent.octopus.beans;
import de.tarent.dblayer.sql.statement.Update;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Diese Klasse stellt ein UPDATE-PreparedStatement dar, dass in einem
 * {@link ExecutionContext} arbeitet und seine Daten aus einer {@link Bean}
 * bezieht.
 *
 * @author mikel
 */
@Log4j2
class BeanUpdateStatement extends BeanBaseStatement implements BeanStatement {
    //
    // Konstruktor
    //

    /**
     * Dieser Konstruktor legt den übergebenen Kontext ab und erzeugt
     * das PreparedStatement.
     *
     * @param update         das dbLayer-Update-Statement
     * @param fieldsInUpdate Felder für die Platzhalter im Statement
     * @param context        Ausführungskontext des PreparedStatements
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
        if (bean != null) {
            try {
                preparedStatement.clearParameters();
                List params = new ArrayList();
                for (int index = 0; index < fields.size(); index++) {
                    String field = fields.get(index).toString();
                    Object value = bean.getField(field);
                    preparedStatement.setObject(index + 1, value);
                    params.add(value);
                }
                if (logger.isDebugEnabled()) {
                    logger.debug("PreparedStatement <" + sqlStatement + "> wird mit Parametern " + params + " aufgerufen.");
                }
                return preparedStatement.executeUpdate();
            } catch (SQLException se) {
                throw new BeanException(
                  "Fehler beim Ausführen des PreparedUpdates <" + sqlStatement + "> mit der Bean <" + bean + ">", se);
            }
        } else {
            logger.warn("Aufruf ohne Bean nicht erlaubt");
        }
        return 0;
    }

    //
    // geschätzte Variablen
    //
    /**
     * Hier sind die Felder zu den Platzhaltern im PreparedStatement aufgelistet.
     */
    final List fields;
}
