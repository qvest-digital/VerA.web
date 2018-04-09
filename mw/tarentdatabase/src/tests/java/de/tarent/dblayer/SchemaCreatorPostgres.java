/*
 * tarent-database,
 * jdbc database library
 * Copyright (c) 2005-2006 tarent GmbH
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
 * interest in the program 'tarent-database'
 * Signature of Elmar Geese, 14 June 2007
 * Elmar Geese, CEO tarent GmbH.
 */

package de.tarent.dblayer;

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
                  +" pk_firma serial PRIMARY KEY,"
                  +" name varchar(50),"
                  +" umsatz integer"
                  +")");
        
        DB.update(dbx, 
                  "CREATE TABLE person ("
                  +" pk_person serial PRIMARY KEY,"
                  +" fk_firma integer,"
                  +" vorname varchar(50),"
                  +" nachname varchar(50),"
                  +" geburtstag date"
                  +")");        

        DB.update(dbx, 
                  "CREATE TABLE insert_test ("
                  +" pk serial PRIMARY KEY,"
                  +" data varchar(50)"
                  +")");

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

