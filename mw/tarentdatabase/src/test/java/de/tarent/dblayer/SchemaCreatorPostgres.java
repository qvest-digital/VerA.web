package de.tarent.dblayer;

/*-
 * VerA.web-Middleware, newly MIT licenced, is comprised of:
 * tarent-commons, a set of common components and solutions
 * tarent-contact, platform-independent webservice-based contact management
 * tarent-database, jdbc database library
 * tarent-doctor, Document Generation Platform
 * tarent-octopus, Webservice Data Integrator and Application Server
 *  © 2018 Атанас Александров (sirakov@gmail.com)
 *  © 2005, 2006, 2007 asteban (s.mancke@tarent.de)
 *  © 2007 David Goemans (d.goemans@tarent.de)
 *  © 2006, 2007, 2010 Hendrik Helwich (h.helwich@tarent.de)
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

import java.sql.SQLException;
import java.text.DateFormat;
import java.util.Locale;

import de.tarent.dblayer.engine.DB;
import de.tarent.dblayer.sql.SQL;

/**
 * Creates the initial Schema for the tests.
 */
public class SchemaCreatorPostgres extends SchemaCreator {

    protected void dropSchema()
      throws SQLException {

        try {
            DB.update(dbx, "DROP TABLE person");
        } catch (SQLException e) {
            logger.trace("Error on droping table person", e);
            // ignore, because table may not exist
        }

        try {
            DB.update(dbx, "DROP TABLE firma CASCADE");
        } catch (SQLException e) {
            logger.trace("Error on droping table firma", e);
            // ignore, because table may not exist
        }

        try {
            DB.update(dbx, "DROP TABLE insert_test");
        } catch (SQLException e) {
            logger.trace("Error on droping table key_test", e);
            // ignore, because table may not exist
        }

        try {
            DB.update(dbx, "DROP FUNCTION public.unit_test(param1 varchar)");
        } catch (SQLException e) {
            logger.trace("Error on dropping function unit_test", e);
            // ignore, because table may not exist
        }

        try {
            DB.update(dbx, "DROP FUNCTION public.unit_test2()");
        } catch (SQLException e) {
            logger.trace("Error on dropping function unit_test2", e);
            // ignore, because table may not exist
        }
    }

    protected void createSchema()
      throws SQLException {
        DB.update(dbx,
          "CREATE TABLE firma ("
            + " pk_firma serial PRIMARY KEY,"
            + " name varchar(50),"
            + " umsatz integer"
            + ")");

        DB.update(dbx,
          "CREATE TABLE person ("
            + " pk_person serial PRIMARY KEY,"
            + " fk_firma integer,"
            + " vorname varchar(50),"
            + " nachname varchar(50),"
            + " geburtstag date"
            + ")");

        DB.update(dbx,
          "CREATE TABLE insert_test ("
            + " pk serial PRIMARY KEY,"
            + " data varchar(50)"
            + ")");

        DB.update(dbx,
          "CREATE OR REPLACE FUNCTION public.unit_test(param1_in varchar, OUT param1 varchar) "
            + " AS\n" +
            "'\n"
            + "BEGIN "
            + "	param1:=param1_in; "
            + "END;\n" +
            "'\n"
            + "LANGUAGE 'plpgsql' VOLATILE; "
            + "ALTER FUNCTION public.unit_test(varchar, OUT varchar) OWNER TO postgres;");

        DB.update(dbx,
          "CREATE OR REPLACE FUNCTION public.unit_test2() RETURNS void"
            + " AS '\n"
            + "BEGIN "
            + "END;'\n"
            + "LANGUAGE 'plpgsql' VOLATILE; "
            + "ALTER FUNCTION public.unit_test2() OWNER TO postgres;");
    }

    protected void doInserts()
      throws SQLException {

        try {
            SQL.Insert(dbx).table("firma")
              // SERIAL: .insert("pk_firma", new Integer(1))
              .insert("name", "Dagoberts Geldspeicher")
              .insert("umsatz", new Integer(100000))
              .execute();

            SQL.Insert(dbx).table("firma")
              // SERIAL: .insert("pk_firma", new Integer(2))
              .insert("name", "Donalds Frittenbude")
              .insert("umsatz", new Integer(30))
              .execute();

            SQL.Insert(dbx).table("firma")
              // SERIAL: .insert("pk_firma", new Integer(3))
              .insert("name", "Duesentriebs Werkstatt")
              .insert("umsatz", new Integer(3000))
              .execute();

            SQL.Insert(dbx).table("person")
              // SERIAL: .insert("pk_person", new Integer(1))
              .insert("fk_firma", new Integer(1))
              .insert("vorname", "Dagobert")
              .insert("nachname", "Duck")
              .insert("geburtstag", DateFormat.getDateInstance(DateFormat.SHORT, Locale.GERMANY).parse("31.03.80"))
              .execute();

            SQL.Insert(dbx).table("person")
              // SERIAL: .insert("pk_person", new Integer(2))
              .insert("fk_firma", new Integer(1))
              .insert("vorname", "Daisy")
              .insert("nachname", "Duck")
              .insert("geburtstag", DateFormat.getDateInstance(DateFormat.SHORT, Locale.GERMANY).parse("11.03.80"))
              .execute();

            SQL.Insert(dbx).table("person")
              // SERIAL: .insert("pk_person", new Integer(3))
              .insert("fk_firma", new Integer(2))
              .insert("vorname", "Donald")
              .insert("nachname", "Duck")
              .insert("geburtstag", DateFormat.getDateInstance(DateFormat.SHORT, Locale.GERMANY).parse("28.01.80"))
              .execute();

            SQL.Insert(dbx).table("person")
              // SERIAL: .insert("pk_person", new Integer(4))
              .insert("fk_firma", null)
              .insert("vorname", "Gustav")
              .insert("nachname", "Gans")
              .insert("geburtstag", DateFormat.getDateInstance(DateFormat.SHORT, Locale.GERMANY).parse("26.11.80"))
              .execute();

            //        } catch (java.text.ParseException e) {
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error on reading date strings", e);
        }
    }
}
