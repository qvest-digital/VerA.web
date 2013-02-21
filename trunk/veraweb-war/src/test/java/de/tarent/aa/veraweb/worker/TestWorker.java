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
 * $Id$
 * 
 * Created on 24.11.2005
 */
package de.tarent.aa.veraweb.worker;

import java.io.IOException;

import de.tarent.aa.veraweb.beans.Person;
import de.tarent.dblayer.sql.clause.Expr;
import de.tarent.dblayer.sql.clause.Where;
import de.tarent.dblayer.sql.statement.Select;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.beans.Database;
import de.tarent.octopus.beans.TransactionContext;
import de.tarent.octopus.beans.veraweb.DatabaseVeraWeb;
import de.tarent.octopus.custom.beans.ProfileLogger;
import de.tarent.octopus.server.OctopusContext;

/**
 * Diese Klasse stellt einen Octopus-Worker dar, dessen Aktionen Hilfen bei
 * Units-Tests darstellen.
 * 
 * @author mikel
 */
public class TestWorker {
    /** Octopus-Eingabe-Parameter für {@link #createPersonStatements(OctopusContext, Person, Integer)} */
    public static final String[] INPUT_createPersonStatements = { "person", "count" };
    /** Octopus-Eingabe-Parameter-Pflicht für {@link #createPersonStatements(OctopusContext, Person, Integer)} */
    public static final boolean[] MANDATORY_createPersonStatements = { true, false };
    /**
     * Diese Octopus-Aktion erstellt Statements zu einer Person zu Profiling-Zwecken.
     * 
     * @param cntx Octopus-Kontext
     * @param person Person, zu der Statements erzeugt werden sollen.
     * @throws IOException 
     * @throws BeanException 
     */
    public void createPersonStatements(OctopusContext cntx, Person person, Integer count) throws BeanException, IOException {
        if (person == null)
            return;
        if (count == null)
            count = new Integer(10);
        ProfileLogger pLog = new ProfileLogger();
        Database database = new DatabaseVeraWeb(cntx);
        for (int i = 0; i < count.intValue(); i++) {
            database.getInsert(person);
        }
        pLog.log("Person-Insert erzeugen, " + count + " Durchläufe");
    }

    /** Octopus-Eingabe-Parameter für {@link #insertPerson(OctopusContext, Person, Integer)} */
    public static final String[] INPUT_insertPerson = { "person", "count" };
    /** Octopus-Eingabe-Parameter-Pflicht für {@link #insertPerson(OctopusContext, Person, Integer)} */
    public static final boolean[] MANDATORY_insertPerson = { true, false };
    /**
     * Diese Octopus-Aktion fügt eine Person in den Datenbestand zu Profiling-Zwecken ein.
     * 
     * @param cntx Octopus-Kontext
     * @param person Person, die eingefügt werden soll.
     * @throws IOException 
     * @throws BeanException 
     */
    public void insertPerson(OctopusContext cntx, Person person, Integer count) throws BeanException, IOException {
        if (person == null)
            return;
        if (count == null)
            count = new Integer(0);
        ProfileLogger pLog = new ProfileLogger();
        Database database = new DatabaseVeraWeb(cntx);
        for (int i = 0; i < count.intValue(); i++) {
            person.id = null;
            database.execute(database.getInsert(person));
        }
        pLog.log("Person einfügen, " + count + " Durchläufe");
    }

    /** Octopus-Eingabe-Parameter für {@link #insertPersonTransaction(OctopusContext, Person, Integer)} */
    public static final String[] INPUT_insertPersonTransaction = { "person", "count" };
    /** Octopus-Eingabe-Parameter-Pflicht für {@link #insertPersonTransaction(OctopusContext, Person, Integer)} */
    public static final boolean[] MANDATORY_insertPersonTransaction = { true, false };
    /**
     * Diese Octopus-Aktion fügt eine Person in den Datenbestand zu Profiling-Zwecken ein.
     * Diese Methode nutzt dafür einen Transaktionskontext.
     * 
     * @param cntx Octopus-Kontext
     * @param person Person, die eingefügt werden soll.
     * @throws IOException 
     * @throws BeanException 
     */
    public void insertPersonTransaction(OctopusContext cntx, Person person, Integer count) throws BeanException, IOException {
        if (person == null)
            return;
        if (count == null)
            count = new Integer(1000);
        ProfileLogger pLog = new ProfileLogger();
        Database database = new DatabaseVeraWeb(cntx);
        TransactionContext context = database.getTransactionContext();
        for (int i = 0; i < count.intValue(); i++) {
            person = PersonDetailWorker.getTestPerson(null);
            database.saveBean(person, context, false);
        }
        context.commit();
        pLog.log("Person transaktionell einfügen, " + count + " Durchläufe");
    }

    /** Octopus-Eingabe-Parameter für {@link #insertFullPerson(OctopusContext, Person, Integer)} */
    public static final String[] INPUT_insertFullPerson = { "person", "count" };
    /** Octopus-Eingabe-Parameter-Pflicht für {@link #insertFullPerson(OctopusContext, Person, Integer)} */
    public static final boolean[] MANDATORY_insertFullPerson = { true, false };
    /**
     * Diese Octopus-Aktion fügt eine Person in den Datenbestand zu Profiling-Zwecken ein.
     * 
     * @param cntx Octopus-Kontext
     * @throws IOException 
     * @throws BeanException 
     */
    public void insertFullPerson(OctopusContext cntx, Person person, Integer count) throws BeanException, IOException {
        if (person == null)
            return;
        if (count == null)
            count = new Integer(0);
        ProfileLogger pLog = new ProfileLogger();
        Database database = new DatabaseVeraWeb(cntx);
        for (int i = 0; i < count.intValue(); i++) {
        	person.id = null;
            database.saveBean(person);
            TransactionContext context = database.getTransactionContext();
            PersonDoctypeWorker.createPersonDoctype(cntx, database, context, person);
            context.commit();
        }
        pLog.log("Person einfügen und Dokumenttypen erzeugen, " + count + " Durchläufe");
    }

    /** Octopus-Eingabe-Parameter für {@link #insertFullPersonTransaction(OctopusContext, Person, Integer)} */
    public static final String[] INPUT_insertFullPersonTransaction = { "person", "count" };
    /** Octopus-Eingabe-Parameter-Pflicht für {@link #insertFullPersonTransaction(OctopusContext, Person, Integer)} */
    public static final boolean[] MANDATORY_insertFullPersonTransaction = { true, false };
    /**
     * Diese Octopus-Aktion fügt eine Person in den Datenbestand zu Profiling-Zwecken ein.
     * 
     * @param cntx Octopus-Kontext
     * @throws IOException 
     * @throws BeanException 
     */
    public void insertFullPersonTransaction(OctopusContext cntx, Person person, Integer count) throws BeanException, IOException {
        if (person == null)
            return;
        if (count == null)
            count = new Integer(0);
        ProfileLogger pLog = new ProfileLogger();
        Database database = new DatabaseVeraWeb(cntx);
        TransactionContext context = database.getTransactionContext();
        
        try {
	        for (int i = 0; i < count.intValue(); i++) {
	        	person.id = null;
	            database.saveBean(person);
	            PersonDoctypeWorker.createPersonDoctype(cntx, database, context, person);
	        }
	        context.commit();
        } finally {
        	context.rollBack();
        }
        pLog.log("Person einfügen und Dokumenttypen transaktionell erzeugen, " + count + " Durchläufe");
    }

    /** Octopus-Eingabe-Parameter für {@link #searchPerson(OctopusContext, nteger)} */
    public static final String[] INPUT_searchPerson = { "count" };
    /** Octopus-Eingabe-Parameter-Pflicht für {@link #searchPerson(OctopusContext, Integer)} */
    public static final boolean[] MANDATORY_searchPerson = { false };
    /**
     * Diese Octopus-Aktion sucht zu Profiling zwecken nach einer Person.
     * 
     * @param cntx Octopus-Kontext
     * @throws IOException 
     * @throws BeanException 
     */
    public void searchPerson(OctopusContext cntx, Integer count) throws BeanException, IOException {
        if (count == null)
            count = new Integer(10);
        ProfileLogger pLog = new ProfileLogger();
        Database database = new DatabaseVeraWeb(cntx);
        
        for (int i = 0; i < count.intValue(); i++) {
        	Person person = PersonDetailWorker.getTestPerson(null);
            Select select = database.getSelect(person);
            select.where(Where.and(
            		Expr.equal(database.getProperty(person, "firstname_a_e1"), person.firstname_a_e1), 
            		Expr.equal(database.getProperty(person, "lastname_a_e1"), person.lastname_a_e1)));
            database.getBean("Person", select);
        }
        pLog.log("Personen gesucht, " + count + " Durchläufe");
    }
}
