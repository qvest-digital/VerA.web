package de.tarent.octopus.beans;

/*-
 * VerA.web-Middleware, newly MIT licenced, is comprised of:
 * tarent-commons, a set of common components and solutions
 * tarent-contact, platform-independent webservice-based contact management
 * tarent-database, jdbc database library
 * tarent-doctor, Document Generation Platform
 * tarent-octopus, Webservice Data Integrator and Application Server
 *  © 2018 Атанас Александров (sirakov@gmail.com)
 *  © 2005, 2006, 2007 asteban (s.mancke@tarent.de)
 *  © 2018 Dominik George (d.george@tarent.de)
 *  © 2007 David Goemans (d.goemans@tarent.de)
 *  © 2018 Christian Gorski (c.gorski@tarent.de)
 *  © 2006, 2007, 2010 Hendrik Helwich (h.helwich@tarent.de)
 *  © 2018 Benedict Hoeger (b.hoeger@tarent.de)
 *  © 2005, 2006, 2007 Christoph Jerolimov (c.jerolimov@tarent.de)
 *  © 2018 Timo Kanera (t.kanera@tarent.de)
 *  © 2006 Philipp Kirchner (p.kirchner@tarent.de)
 *  © 2010 Carsten Klein (c.klein@tarent.de)
 *  © 2006 Michael Kleinhenz (m.kleinhenz@tarent.de)
 *  © 2006 Michael Klink (m.klink@tarent.de)
 *  © 2007 Fabian Köster (f.koester@tarent.de)
 *  © 2006 Martin Ley (m.ley@tarent.de)
 *  © 2007 Alex Maier (a.maier@tarent.de)
 *  © 2007, 2015, 2017, 2018 mirabilos (t.glaser@tarent.de)
 *  © 2006, 2007 Jens Neumaier (j.neumaier@tarent.de)
 *  © 2006 Nils Neumaier (n.neumaier@tarent.de)
 *  © 2007, 2008 Martin Pelzer (m.pelzer@tarent.de)
 *  © 2008, 2009 Christian Preilowski (c.thiel@tarent.de)
 *  © 2006, 2008, 2009 Thomas Schmitz (t.schmitz@tarent.de)
 *  © 2007 Robert Schuster (r.schuster@tarent.de)
 * and older code, Copyright © 2001–2007 ⮡ tarent GmbH and contributors.
 * Licensor is tarent solutions GmbH, http://www.tarent.de/
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

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
                if (logger.isLoggable(Level.FINE)) {
                    logger.fine("PreparedStatement <" + sqlStatement + "> wird mit Parametern " + params + " aufgerufen.");
                }
                return preparedStatement.executeUpdate();
            } catch (SQLException se) {
                throw new BeanException(
                  "Fehler beim Ausführen des PreparedUpdates <" + sqlStatement + "> mit der Bean <" + bean + ">", se);
            }
        } else {
            logger.warning("Aufruf ohne Bean nicht erlaubt");
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

    /**
     * logger of this class.
     */
    final static Logger logger = Logger.getLogger(BeanUpdateStatement.class.getName());
}
