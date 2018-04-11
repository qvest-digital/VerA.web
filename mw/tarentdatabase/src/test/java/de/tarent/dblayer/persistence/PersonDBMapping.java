package de.tarent.dblayer.persistence;

/*-
 * VerA.web-Middleware, newly MIT licenced, is comprised of:
 * tarent-commons, a set of common components and solutions
 * tarent-contact, platform-independent webservice-based contact management
 * tarent-database, jdbc database library
 * tarent-doctor, Document Generation Platform
 * tarent-octopus, Webservice Data Integrator and Application Server
 *  © 2005, 2006, 2007 asteban (s.mancke@tarent.de)
 *  © 2007 David Goemans (d.goemans@tarent.de)
 *  © 2006, 2007, 2010 Hendrik Helwich (h.helwich@tarent.de)
 *  © 2005, 2006, 2007 Christoph Jerolimov (c.jerolimov@tarent.de)
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

import de.tarent.dblayer.sql.clause.Order;
import de.tarent.dblayer.engine.DBContext;

public class PersonDBMapping extends AbstractDBMapping {

    protected static final int PERSON_FIRMA_FIELDS = 128;

    public static final String STMT_PERSON_FIRMA = "stmtPersonFirmaOne";

    public PersonDBMapping(DBContext cntx) {
        super(cntx);
    }

    public String getTargetTable() {
        return "person";
    }

    public void configureMapping() {
        addField("person.pk_person", "id", PRIMARY_KEY_FIELDS | MINIMAL_FIELDS | COMMON_FIELDS);
        addField("person.fk_firma", "firmaFk");
        addField("person.vorname", "givenName", DEFAULT_FIELD_SET | MINIMAL_FIELDS);
        addField("person.nachname", "lastName", DEFAULT_FIELD_SET | MINIMAL_FIELDS);
        addField("person.geburtstag", "birthday");

        addField("firma.pk_firma", concatPropCol("firma", "id"), PERSON_FIRMA_FIELDS);
        addField("firma.name", concatPropCol("firma", "name"), PERSON_FIRMA_FIELDS);
        addField("firma.umsatz", concatPropCol("firma", "turnover"), PERSON_FIRMA_FIELDS);

        addQuery(STMT_SELECT_ALL, createBasicSelectAll().orderBy(Order.asc("person.vorname")), DEFAULT_FIELD_SET);
        addQuery(STMT_PERSON_FIRMA, createBasicSelectOne().join("firma", "person.fk_firma", "firma.pk_firma"),
                PERSON_FIRMA_FIELDS | COMMON_FIELDS);
    }
}
