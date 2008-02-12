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
 * $Id: Database.java,v 1.1 2007/06/20 11:56:51 christoph Exp $
 * 
 * Created on 21.02.2005
 */
package de.tarent.octopus.custom.beans;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import de.tarent.dblayer.engine.DB;
import de.tarent.dblayer.helper.ResultList;
import de.tarent.dblayer.sql.SQL;
import de.tarent.dblayer.sql.Statement;
import de.tarent.dblayer.sql.SyntaxErrorException;
import de.tarent.dblayer.sql.clause.Clause;
import de.tarent.dblayer.sql.clause.Expr;
import de.tarent.dblayer.sql.clause.Order;
import de.tarent.dblayer.sql.clause.WhereList;
import de.tarent.dblayer.sql.statement.Delete;
import de.tarent.dblayer.sql.statement.Insert;
import de.tarent.dblayer.sql.statement.Select;
import de.tarent.dblayer.sql.statement.Update;
import de.tarent.octopus.server.OctopusContext;

/**
 * Konkrete {@link BeanFactory}, die Beans aus einer Datenbank ausliest. 
 * 
 * @author Michael Klink, Alex Steeg, Christoph Jerolimov
 * @version 1.3
 */
public abstract class Database extends BeanFactory implements ExecutionContext {
    //
    // Konstanten
    //
    /** Dieses Boolean-Attribut legt fest, ob eine Spalte Read-Only ist. */
    public final static String ATTRIBUTE_READ_ONLY = "ro";
    
    //
    // Konstruktoren
    //
    /**
     * Dieser Konstruktor initialisiert die {@link BeanFactory} mit dem
     * �bergebenen Bean-Package und nimmt als Modulnamen f�r den DB-Zugriff
     * das Modul des �bergebenen Octopus-Kontexts. Der Kontext wird sich
     * weiterhin gemerkt, um Pfade zugeh�riger Bean-Properties zu finden.
     * 
     * @param cntx Octopus-Kontext, der DB-Modulnamen und
     *  Bean-Property-Dateipfade bestimmt.
     * @param beanPackage Java-Package, in dem sich die Bean-Klassen
     *  befinden. 
     */
	public Database(OctopusContext cntx, String beanPackage) {
        super(beanPackage);
		this.cntx = cntx;
		this.moduleName = cntx.getModuleName();
	}

    /**
     * Dieser Konstruktor initialisiert die {@link BeanFactory} mit dem
     * �bergebenen Bean-Package und nimmt als Modulnamen f�r den DB-Zugriff
     * den �bergebenen Namen.<br>
     * Achtung: in diesem Kontext gibt es keine sinnvolle Vorgabe f�r Pfade
     * von Bean-Property-Dateien.<br>
     * TODO: sinnvolle Property-Dateipfade einf�hren 
     * 
     * @param moduleName Modulnamen f�r den DB-Zugriff.
     * @param beanPackage Java-Package, in dem sich die Bean-Klassen
     *  befinden. 
     */
	public Database(String moduleName, String beanPackage) {
        super(beanPackage);
		this.cntx = null;
		this.moduleName = moduleName;
	}

    //
    // �ffentliche Methoden
    //
    /**
     * Diese Methode liefert einen neuen {@link TransactionContext Transaktionskontext}
     * zu dieser Datenbank-Instanz.
     */
    public TransactionContext getTransactionContext() {
        return new TransactionContext(this);
    }
    
    /**
     * Diese Methode liefert den zugrundeliegenden Modulnamen. Dieser wird beim Erstellen
     * unabh�ngiger SQL-Statements ben�tigt, sollte aber nicht unn�tig genutzt und erst
     * recht nicht misbraucht werden. 
     * 
     * @return zugrundeliegender Modulname
     */
    public final String getModuleName() {
        return moduleName;
    }

    // �ffentliche Bean-orientierte Methoden
    /**
     * Diese Methode liefert eine Bean vom �bergebenen Typ zum �bergebenen
     * Prim�rschl�ssel aus der Datenbank.
     * 
     * @param beanname Klasse der zu holenden Bean
     * @param pk Prim�rschl�ssel des auszulesenden DB-Datensatzes.
     * @return die ausgelesene Bean oder <code>null</code>
     * @throws BeanException
     * @throws IOException bei Problemen beim Zugriff auf die Bean-Properties
     */
    public Bean getBean(String beanname, Integer pk) throws BeanException, IOException {
        return getBean(beanname, pk, this);
    }
    
    /**
     * Diese Methode liefert eine Bean vom �bergebenen Typ zum �bergebenen
     * Prim�rschl�ssel aus dem �bergebenen {@link ExecutionContext Ausf�hrungskontext}
     * der Datenbank.
     * 
     * @param beanname Klasse der zu holenden Bean
     * @param pk Prim�rschl�ssel des auszulesenden DB-Datensatzes.
     * @param context {@link ExecutionContext Ausf�hrungskontext}, in dem gelesen werden soll.
     * @return die ausgelesene Bean oder <code>null</code>
     * @throws BeanException
     * @throws IOException bei Problemen beim Zugriff auf die Bean-Properties
     */
    public Bean getBean(String beanname, Integer pk, ExecutionContext context) throws BeanException, IOException {
        if (pk == null)
            return null;
        Bean sample = createBean(beanname);
        String pkField = getProperty(sample, "pk");
        String pkColumn = null;
        if (pkField != null)
            pkColumn = getProperty(sample, pkField);
        if (pkColumn == null) {
            logger.info("Kann Schl�sselfeldnamen nicht den Bean-Eigenschaften von " + beanname + " entnehmen; gehe von 'pk' aus.");
            pkColumn = "pk";
        }
        return getBean(beanname, getSelect(beanname).where(Expr.equal(pkColumn, pk)), context);
    }

    /**
     * Diese Methode liefert eine Bean vom �bergebenen Typ zum �bergebenen
     * Select-Statement aus der Datenbank.
     * 
     * @param beanname Klasse der zu holenden Bean
     * @param select Select-Statement zum Zugriff auf den DB-Datensatz.
     * @return die ausgelesene Bean oder <code>null</code>
     * @throws BeanException
     */
    public Bean getBean(String beanname, Select select) throws BeanException {
        return getBean(beanname, select, this);
    }
    
    /**
     * Diese Methode liefert eine Bean vom �bergebenen Typ zum �bergebenen
     * Select-Statement aus dem �bergebenen {@link ExecutionContext Ausf�hrungskontext}
     * der Datenbank.
     * 
     * @param beanname Klasse der zu holenden Bean
     * @param select Select-Statement zum Zugriff auf den DB-Datensatz.
     * @param context {@link ExecutionContext Ausf�hrungskontext}, in dem gelesen werden soll.
     * @return die ausgelesene Bean oder <code>null</code>
     * @throws BeanException
     */
    public Bean getBean(String beanname, Select select, ExecutionContext context) throws BeanException {
        try {
            executeSelect(select, context);
            return hasNext() ? fillBean(createBean(beanname)) : null;
        } finally {
            closeResultSet();
        }
    }

    /**
     * Diese Methode liefert eine Liste mit Bean-Instanzen vom �bergebenen Typ zum
     * �bergebenen Select-Statement aus der Datenbank.
     * 
     * @param beanname Klasse der zu holenden Beans
     * @param select Select-Statement zum Zugriff auf die DB-Datens�tze.
     * @return eine Liste ausgelesener Beans.
     * @throws BeanException
     */
    public List getBeanList(String beanname, Select select) throws BeanException {
        return getBeanList(beanname, select, this);
    }
    
    /**
     * Diese Methode liefert eine Liste mit Bean-Instanzen vom �bergebenen Typ zum
     * �bergebenen Select-Statement aus dem �bergebenen {@link ExecutionContext Ausf�hrungskontext}
     * der Datenbank.
     * 
     * @param beanname Klasse der zu holenden Beans
     * @param select Select-Statement zum Zugriff auf die DB-Datens�tze.
     * @param context {@link ExecutionContext Ausf�hrungskontext}, in dem gelesen werden soll.
     * @return eine Liste ausgelesener Beans.
     * @throws BeanException
     */
    public List getBeanList(String beanname, Select select, ExecutionContext context) throws BeanException {
        // TODO: beans on the fly erstellen
        try {
            executeSelect(select, context);
            return fillBeanList(beanname);
        } finally {
            closeResultSet();
        }
    }

    /**
     * Diese Methode speichert eine Bean in der Datenbank. Hierbei wird im
     * Falle eines neu erzeugten DB-Datensatzes der ID-Eintrag aktualisiert,
     * sofern er entsprechend in den Properties konfiguriert wurde.  
     * 
     * @param bean zu speichernde Bean
     * @throws BeanException
     * @throws IOException
     */
    public void saveBean(Bean bean) throws BeanException, IOException {
        saveBean(bean, this, true);
    }

    /**
     * Diese Methode speichert eine Bean im �bergebenen {@link ExecutionContext Ausf�hrungskontext}
     * der Datenbank. Wahlweise wird hierbei im Falle eines neu erzeugten DB-Datensatzes
     * der ID-Eintrag aktualisiert, sofern er entsprechend in den Properties konfiguriert wurde. 
     * 
     * @param bean zu speichernde Bean
     * @param context {@link ExecutionContext Ausf�hrungskontext}, in dem gespeichert werden soll.
     * @param updateID Flag, ob der ID-Eintrag der Bean aktualisiert werden soll
     * @throws BeanException
     * @throws IOException
     */
    public void saveBean(Bean bean, ExecutionContext context, boolean updateID) throws BeanException, IOException {
        String id = getProperty(bean, "pk");
        if (bean.getField(id) == null) {
            String nextval = getProperty(bean, "sequence.nextval");
            if (updateID && nextval != null) {
                getNextPk(bean, context);
                Insert insert = getInsert(bean);
                insert.insert(getProperty(bean, id), bean.getField(id));
                context.execute(insert);
            } else {
                context.execute(getInsert(bean));
                if (updateID)
                    getInsertedPk(bean, context);
            }
        } else {
            context.execute(getUpdate(bean));
        }
    }
    
    /**
     * Diese Methode l�scht den DBDatensatz, der der �bergebenen Bean zugrunde liegt  
     * 
     * @param bean
     * @throws BeanException
     * @throws IOException
     */
    public void removeBean(Bean bean) throws BeanException, IOException {
        String field = getProperty(bean, "pk");
        if (bean.getField(field) != null) {
            execute(getDelete(bean));
        }
    }

    // �ffentliche dblayer-orientierte Methoden
    /**
     * Diese Methode f�hrt das �bergebene Select-Statement in String-Form
     * aus und erwartet als Resultat ein {@link ResultSet}, das dann
     * zur�ckgegeben wird. 
     * 
     * @param statement auszuf�hrendes Select-Statement
     * @return resultierendes {@link ResultSet}
     * @throws BeanException
     */
    public ResultSet result(String statement) throws BeanException {
        try {
            return DB.result(getModuleName(), statement).resultSet();
        } catch (SQLException e) {
            throw new BeanException("Fehler beim Ausf�hren einer Datenbankabfrage.", e);
        }
    }

    /**
     * Diese Methode f�hrt ein Z�hlstatement in der Datenbank aus 
     * 
     * @param select Select-Statement
     * @return Zahl aus der ersten Spalte des ersten Ergebnisdatensatzes
     * @throws BeanException
     */
    public Integer getCount(Select select) throws BeanException {
        return getCount(select, this);
    }

    /**
     * Diese Methode f�hrt ein Z�hlstatement in dem �bergebenen
     * {@link ExecutionContext Ausf�hrungskontext} der Datenbank aus 
     * 
     * @param select Select-Statement
     * @param context {@link ExecutionContext Ausf�hrungskontext}, in dem gez�hlt werden soll.
     * @return Zahl aus der ersten Spalte des ersten Ergebnisdatensatzes
     * @throws BeanException
     */
    public Integer getCount(Select select, ExecutionContext context) throws BeanException {
        ResultSet rs = null;
        try {
            rs = context.result(select);
            if (rs.next())
                return new Integer(rs.getInt(1));
            
            throw new BeanException("Fehler beim Laden des Beans aus der Datenbank.");
        } catch (SQLException e) {
            throw new BeanException("Fehler beim Laden des Beans aus der Datenbank.", e);
        } finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (SQLException e) {
            }
        }
    }

    /**
     * Diese Methode liefert eine Ergebnisliste zum �bergebenen Select-Statement aus
     * dem �bergebenen {@link ExecutionContext Ausf�hrungskontext} der Datenbank.<br>
     * Die Eintr�ge dieser Liste sind Maps, die jeweils einen Ergebnisdatensatz
     * darstellen. Diese Maps werden erst erstellt, wenn sie abgerufen werden.
     * 
     * @param statement Select-Statement zum Zugriff auf die DB-Datens�tze.
     * @param context {@link ExecutionContext Ausf�hrungskontext}, in dem gelesen werden soll.
     * @return eine Liste von Maps.
     * @throws BeanException
     */
    public List getList(Select statement, ExecutionContext context) throws BeanException {
        try {
            return new ResultList(context.result(statement));
        } catch (SQLException e) {
            throw new BeanException("Fehler beim Ausf�hren einer Datenbankabfrage.", e);
        }
    }

    /**
     * Diese Methode liefert eine Ergebnisliste zum �bergebenen Select-Statement aus
     * der Datenbank.<br>
     * Die Eintr�ge dieser Liste sind Maps, die jeweils einen Ergebnisdatensatz
     * darstellen. Diese Maps werden erst erstellt, wenn sie abgerufen werden.
     * 
     * @param statement Select-Statement zum Zugriff auf die DB-Datens�tze.
     * @return eine Liste von Maps.
     * @throws BeanException
     */
    public List getList(Statement statement) throws BeanException {
        try {
            return new ResultList(DB.result(getModuleName(), statement).resultSet());
        } catch (SQLException e) {
            throw new BeanException("Fehler beim bearbeiten eines Beans in der Datenbank.", e);
        }
    }

    /**
     * Diese Methode ruft den n�chsten Prim�rschl�ssel-Wert der der �bergebenen Bean
     * zugrunde liegenden Tabelle im �bergebenen {@link ExecutionContext Ausf�hrungskontext}
     * ab und setzt das ID-Feld dieser Bean entsprechend.<br>
     * Dies erlaubt beim Einf�gen eines neuen Datensatzes ein sicheres Wissen um die
     * zugeteilte ID. 
     * 
     * @param bean Bean, die die n�chste freie ID bekommen soll. 
     * @param context {@link ExecutionContext Ausf�hrungskontext}, in dem gearbeitet werden soll.
     * @throws BeanException
     * @throws IOException
     */
    public void getNextPk(Bean bean, ExecutionContext context) throws BeanException, IOException {
        String id = getProperty(bean, "pk");
        String nextval = getProperty(bean, "sequence.nextval");
        if (nextval == null) return;
        
        Select select = SQL.Select()/*.from(getProperty(bean, "table"))*/;
        select.selectAs(nextval, "pk");
        bean.setField(id, getCount(select, context));
    }

    /**
     * Diese Methode ruft den h�chsten Prim�rschl�ssel-Wert der der �bergebenen Bean
     * zugrunde liegenden Tabelle im �bergebenen {@link ExecutionContext Ausf�hrungskontext}
     * ab und setzt das ID-Feld dieser Bean entsprechend.<br>
     * Dies erlaubt nach einem Einf�gen eines neuen Datensatzes eine plausible Annahme
     * �ber die ID des neuen Datensatzes. 
     * 
     * @param bean Bean, die die n�chste freie ID bekommen soll. 
     * @param context {@link ExecutionContext Ausf�hrungskontext}, in dem gearbeitet werden soll.
     * @throws BeanException
     * @throws IOException
     */
    public void getInsertedPk(Bean bean, ExecutionContext context) throws IOException, BeanException {
        String id = getProperty(bean, "pk");
        String pk = getProperty(bean, id);
        if (pk == null) return;
        
        Select select = SQL.Select().from(getProperty(bean, "table"));
        select.selectAs("MAX(" + pk + ')', "pk");
        bean.setField(id, getCount(select, context));
    }
    
    /**
     * Diese Methode liefert ein Statement zum Z�hlen von Datens�tzen, die der
     * Bean des �bergebenen Namens zugrunde liegen. 
     * 
     * @param beanname Name der Bean, zu der Datens�tze gez�hlt werden sollen
     * @return Select-Statment zum Z�hlen von Datens�tzen zum �bergebenen Beantyp.
     * @throws BeanException
     * @throws IOException
     */
    public Select getCount(String beanname) throws BeanException, IOException {
        return getCount(createBean(beanname));
    }

    /**
     * Diese Methode liefert ein Statement zum Z�hlen von Datens�tzen, die dem
     * Typ der �bergebenen Bean zugrunde liegen. 
     * 
     * @param bean Bean, zu deren Typ Datens�tze gez�hlt werden sollen
     * @return Select-Statment zum Z�hlen von Datens�tzen zum Typ der �bergebenen Bean.
     * @throws BeanException
     * @throws IOException
     */
    public Select getCount(Bean bean) throws BeanException, IOException {
        String pk = getProperty(bean, "pk");
        Select select = SQL.SelectDistinct();
        select.from(getProperty(bean, "table"));
        select.select("COUNT(DISTINCT(" + getProperty(bean, pk) + "))");
        return select;
    }

    /**
     * Diese Methode liefert ein Statement zum Abfragen von Datens�tzen, die der
     * Bean des �bergebenen Namens zugrunde liegen. 
     * 
     * @param beanname Name der Bean, zu der Datens�tze abgefragt werden sollen
     * @return Select-Statment zum Abfragen von Datens�tzen zum �bergebenen Beantyp.
     * @throws BeanException
     * @throws IOException
     */
    public Select getSelect(String beanname) throws BeanException, IOException {
        return getSelect(createBean(beanname));
    }

    /**
     * Diese Methode liefert ein Statement zum Abfragen von Datens�tzen, die dem
     * Typ der �bergebenen Bean zugrunde liegen. 
     * 
     * @param bean Bean, zu deren Typ Datens�tze abgefragt werden sollen
     * @return Select-Statment zum Abfragen von Datens�tzen zum Typ der �bergebenen Bean.
     * @throws BeanException
     * @throws IOException
     */
    public Select getSelect(Bean bean) throws BeanException, IOException {
        String field, column;
        Select select = SQL.SelectDistinct();
        select.from(getProperty(bean, "table"));
        if (getProperty(bean, "order") != null) {
            select.orderBy(new StringOrder(getProperty(bean, "order")));
        }
        for (Iterator it = bean.getFields().iterator(); it.hasNext(); ) {
            field = (String)it.next();
            column = getProperty(bean, field);
            if (column != null)
                select.selectAs(column, field);
        }
        return select;
    }

    /**
     * Diese Methode liefert ein Statement zum Abfragen von IDs von Datens�tzen, die dem
     * Typ der �bergebenen Bean zugrunde liegen. 
     * 
     * @param bean Bean, zu deren Typ Datensatz-IDs abgefragt werden sollen
     * @return Select-Statment zum Abfragen von IDs von Datens�tzen zum Typ der �bergebenen Bean.
     * @throws BeanException
     * @throws IOException
     */
    public Select getSelectIds(Bean bean) throws BeanException, IOException {
        Select select = SQL.SelectDistinct();
        select.from(getProperty(bean, "table"));
        select.selectAs(getProperty(bean, "id"), "id");
        return select;
    }

    /**
     * Diese Methode liefert ein Statement zum Erzeugen eines Datensatzes
     * zu der �bergebenen Bean. 
     * 
     * @param bean Bean, zu der ein Datens�tze erzeugt werden sollen
     * @return Insert-Statment zum Erzeugen eines Datensatzes zu der �bergebenen Bean
     * @throws BeanException
     * @throws IOException
     */
    public Insert getInsert(Bean bean) throws BeanException, IOException {
        String pk = getProperty(bean, "pk");
        String field, column;
        Insert insert = new Insert(bean.getFields().size());
        insert.table(getProperty(bean, "table"));
        for (Iterator it = bean.getFields().iterator(); it.hasNext(); ) {
            field = (String)it.next();
            if (!(field.equals(pk) || Boolean.valueOf(getPropertyAttribute(bean, field, ATTRIBUTE_READ_ONLY)).booleanValue())) {
                column = getProperty(bean, field);
                if (column != null) {
                    insert.insert(column, bean.getField(field));
                }
            }
        }
        return insert;
    }

    /**
     * Diese Methode liefert ein Statement zum Aktualisieren eines Datensatzes
     * zu einer Bean mit dem �bergebenen Namen.  
     * 
     * @param beanname Name des Bean-Typs, zu dem ein Aktualisierungs-Statement erzeugt werden soll.
     * @return Update-Statment zum Aktualisieren eines Datensatzes zu einer Bean mit dem �bergebenen Namen
     * @throws BeanException
     * @throws IOException
     */
    public Update getUpdate(String beanname) throws BeanException, IOException {
        return SQL.Update().table(getProperty(createBean(beanname), "table"));
    }

    /**
     * Diese Methode liefert ein Statement zum Aktualisieren des Datensatzes
     * zu der �bergebenen Bean. 
     * 
     * @param bean Bean, deren Datens�tze aktualisiert werden sollen
     * @return Update-Statment zum Aktualisieren des Datensatzes der �bergebenen Bean
     * @throws BeanException
     * @throws IOException
     */
    public Update getUpdate(Bean bean) throws BeanException, IOException {
        String field, column;
        Update update = SQL.Update();
        update.table(getProperty(bean, "table"));
        for (Iterator it = bean.getFields().iterator(); it.hasNext(); ) {
            field = (String)it.next();
            column = getProperty(bean, field);
            if (column != null  && !Boolean.valueOf(getPropertyAttribute(bean, field, ATTRIBUTE_READ_ONLY)).booleanValue())
                update.update(column, bean.getField(field));
        }
        field = getProperty(bean, "pk");
        column = getProperty(bean, field);
        update.where(Expr.equal(column, bean.getField(field)));
        return update;
    }

    /**
     * Diese Methode liefert ein Statement zum L�schen eines Datensatzes
     * zu einer Bean mit dem �bergebenen Namen.  
     * 
     * @param beanname Name des Bean-Typs, zu dem ein L�sch-Statement erzeugt werden soll.
     * @return Delete-Statment zum L�schen eines Datensatzes zu einer Bean mit dem �bergebenen Namen
     * @throws BeanException
     * @throws IOException
     */
    public Delete getDelete(String beanname) throws BeanException, IOException {
        return SQL.Delete().from(getProperty(createBean(beanname), "table"));
    }

    /**
     * Diese Methode liefert ein Statement zum L�schen des Datensatzes
     * zu der �bergebenen Bean. 
     * 
     * @param bean Bean, deren Datens�tze gel�scht werden sollen
     * @return Delete-Statment zum L�schen des Datensatzes der �bergebenen Bean
     * @throws BeanException
     * @throws IOException
     */
    public Delete getDelete(Bean bean) throws BeanException, IOException {
        String field, column;
        Delete delete = SQL.Delete();
        delete.from(getProperty(bean, "table"));
        field = getProperty(bean, "pk");
        column = getProperty(bean, field);
        delete.where(Expr.equal(column, bean.getField(field)));
        return delete;
    }

    /**
     * Diese Methode liefert eine Where-Klausel zum Bestimmen des Datensatzes
     * zu der �bergebenen Bean.
     * 
     * @param bean Bean, zu der eine Where-Klausel erstellt werden soll
     * @return Where-Klausel zum Bestimmen des Datensatzes zu der �bergebenen Bean.
     * @throws BeanException
     * @throws IOException
     */
    public Clause getWhere(Bean bean) throws BeanException, IOException {
        WhereList where = new WhereList();
        for (Iterator it = bean.getFields().iterator(); it.hasNext(); ) {
            String field = (String)it.next();
            Object object = bean.getField(field);
            if (object != null && !Boolean.valueOf(getPropertyAttribute(bean, field, ATTRIBUTE_READ_ONLY)).booleanValue()) {
                where.addAnd(Expr.equal(getProperty(bean, field), object));
            }
        }
        return where;
    }

    /**
     * Diese Methode liefert ein Attribut eines Properties zum �bergebenen Bean-Typ. 
     * 
     * @param bean Bean, zu deren Typ ein Property-Attribut geliefert werden soll
     * @param key Schl�ssel des zu betroffenen Properties
     * @param attributeKey Schl�ssel des Attributs des betroffenen Properties
     * @return das gew�nschte Property-Attribut zum Bean-Typ.
     * @throws IOException
     */
    public String getPropertyAttribute(Bean bean, String key, String attributeKey) throws IOException {
        return getProperty(bean, propertyAttributeKeyFormat.format(new Object[] { key, attributeKey }));
    }

    /**
     * Diese Methode liefert ein Property zum �bergebenen Bean-Typ. 
     * 
     * @param bean Bean, zu deren Typ ein Property geliefert werden soll
     * @param key Schl�ssel des zu liefernden Properties
     * @return das gew�nschte Property zum Bean-Typ.
     * @throws IOException
     */
    public String getProperty(Bean bean, String key) throws IOException {
        Map map = (Map)beans.get(bean.getClass().getName());
        if (map == null) {
            map = loadProperties(bean);
            beans.put(bean.getClass().getName(), map);
        }
        return (String)map.get(key);
    }
    
    /**
     * Diese Methode erzeugt ein UPDATE-PreparedStatement zu einer {@link Bean} mit
     * einer Anzahl Schl�ssel- (f�r die WHERE-Klausel) und Update-Felder (f�r die
     * SET-Anweisung), die im �bergebenen Kontext arbeitet.
     * 
     * @param sample Beispiel-{@link Bean}
     * @param keyFields Sammlung von Schl�sselfeldern
     * @param updateFields Sammlung von Update-Feldern
     * @param context {@link ExecutionContext Ausf�hrungskontext}
     * @return ein UPDATE-PreparedStatement
     */
    public BeanStatement prepareUpdate(Bean sample, Collection keyFields, Collection updateFields, ExecutionContext context) throws BeanException, IOException {
        List fieldsInUpdate = new ArrayList();
        Update update = getPreparedUpdate(sample, keyFields, updateFields, fieldsInUpdate);
        return new BeanUpdateStatement(update, fieldsInUpdate, context);
    }
    
    //
    // Basisklassen BeanFactory
    //
    /**
     * Liefert ein Objekt, das in ein bestimmtes Feld der "aktuellen" Bean
     * gesetzt werden soll.
     * 
     * @param key Feld-Schl�ssel
     * @return Feldinhalt oder <code>null</code> bei DB-Zugriffsfehlern.
     * @throws BeanException bei Datenzugriffsfehlern.
     * @see BeanFactory#getField(String)
     */
	public Object getField(String key) throws BeanException {
		try {
			return resultSet.getObject(key);
		} catch (SQLException e) {
            if (logger.isDebugEnabled())
                logger.debug("Feld " + key + " kann nicht ausgelesen werden.", e);
			return null;
		}
	}

    /**
     * Wird verwendet, um bei Bean-Listen zur n�chsten Bohne zu springen,
     * wird vor dem Einlesen einer Bean aufgerufen.
     * 
     * @return <code>true</code>, wenn weitere Beans vorhanden sind, ansonsten
     *  <code>false</code>.
     * @throws BeanException bei Datenzugriffsfehlern.
     * @see BeanFactory#hasNext()
     */
	public boolean hasNext() throws BeanException {
		try {
			return resultSet.next();
		} catch (SQLException e) {
			throw new BeanException("Fehler beim lesen aus der Datenbank.", e);
		}
	}
    
    /**
     * Diese Methode setzt basierend auf dem Factory-Wissen das Bean-Feld
     * {@link Bean#isModified() Modified}.<br>
     * Im Datenbank-Kontext wird davon ausgegangen, dass eine Pr�fung genauso
     * aufw�ndig wie ein Update ist, und also ist dies ein NOP.
     * 
     * @param bean Bohne, deren {@link Bean#isModified() Modified}-Feld aktualisiert werden soll.
     * @throws BeanException
     * @see BeanFactory#checkModified(Bean)
     */
    protected void checkModified(Bean bean) throws BeanException {
        assert bean != null;
    }

    //
    // Schnittstelle ExecutionContext
    //
    /**
     * Diese Methode f�hrt das �bergebene {@link Statement} aus.
     * 
     * @param statement auszuf�hrendes {@link Statement}
     * @throws BeanException
     * @see ExecutionContext#execute(Statement)
     */
    public void execute(Statement statement) throws BeanException {
        try {
            DB.update(getModuleName(), statement);
        } catch (SQLException e) {
            throw new BeanException("Fehler beim Ausf�hren eines Statements in der Datenbank.", e);
        }
    }
    
    /**
     * Diese Methode f�hrt das �bergebene {@link Select}-{@link Statement}
     * aus und erwartet als Resultat ein {@link ResultSet}, das dann
     * zur�ckgegeben wird. 
     * 
     * @param statement auszuf�hrendes {@link Select}-{@link Statement}
     * @return resultierendes {@link ResultSet}
     * @throws BeanException
     * @see ExecutionContext#result(Select)
     */
    public ResultSet result(Select statement) throws BeanException {
        try {
            return result(statement.statementToString());
        } catch (SyntaxErrorException e) {
            throw new BeanException("Fehler beim Erzeugen eines SQL-Strings zu einem Select-Statement", e);
        }
    }
    
    /**
     * Diese Methode bereitet das �bergebene {@link Statement} vor.<br>
     * TODO: Die geholte Connection wird nicht geschlossen. �ndern! 
     * 
     * @param statement vorzubereitendes {@link Statement}
     * @return resultierendes {@link PreparedStatement}
     * @throws BeanException
     */
    public PreparedStatement prepare(Statement statement) throws BeanException {
        Connection con = null;
        try {
            con = DB.getConnection(getModuleName());
            con.setAutoCommit(true);
            return con.prepareStatement(statement.statementToString());
        } catch (SQLException e) {
            throw new BeanException("Fehler beim Erstellen eines PreparedStatements", e);
        }
    }
    
    /**
     * Diese Methode liefert die {@link Database}, in der dieser Kontext arbeitet. 
     * 
     * @return zugeh�rige {@link Database}
     */
    public Database getDatabase() {
        return this;
    }
    
    //
    // gesch�tzte Hilfsmethoden
    //
    /**
     * Diese Methode erzeugt das eigentliche dblayer-SQL-Statement f�r den Update
     * und f�llt die �bergebene Liste der Platzhalter-Felder.
     * 
     * @param sample eine Beispiel-Bean zu der Update-Tabelle
     * @param keyFields eine Sammlung von Filterfeldern f�r die Abfrage
     * @param updateFields eine Sammlung Felder f�r den Update in dieser Abfrage
     * @param fieldsInStatement Liste, der die Felder zum F�llen der Platzhalter
     *  hinzugef�gt werden.
     * @return dblayer-SQL-Statement
     */
    Update getPreparedUpdate(Bean sample, Collection keyFields, Collection updateFields, List fieldsInStatement) throws IOException {
        assert keyFields != null && !keyFields.isEmpty();
        assert updateFields != null && !updateFields.isEmpty();

        WhereList where = new WhereList();
        Collection keyFieldsFound = new ArrayList();
        String field, column;
        Update update = SQL.Update();
        update.table(getProperty(sample, "table"));
        for (Iterator it = sample.getFields().iterator(); it.hasNext(); ) {
            field = (String)it.next();
            column = getProperty(sample, field);
            if (keyFields.contains(field)) {
                if (column == null || column.length() == 0)
                    logger.warn("Schl�sselfeld ohne zugeordnete Tabellenspalte: " + field);
                else {
                    keyFieldsFound.add(field);
                    where.addAnd(Expr.equal(column, BeanBaseStatement.PLACE_HOLDER));
                }
            } else if (updateFields.contains(field)) {
                if (column == null || column.length() == 0)
                    logger.warn("Update-Feld ohne zugeordnete Tabellenspalte: " + field);
                else if (Boolean.valueOf(getPropertyAttribute(sample, field, Database.ATTRIBUTE_READ_ONLY)).booleanValue())
                    logger.warn("Update-Feld ohne zugeordnete Tabellenspalte: " + field);
                else {
                    fieldsInStatement.add(field);
                    update.update(column, BeanBaseStatement.PLACE_HOLDER);
                }
            }
        }
        fieldsInStatement.addAll(keyFieldsFound);
        update.where(where);
        return update;
    }

    
    
    
    /**
     * Diese Methode f�hrt im �bergebenen {@link ExecutionContext Ausf�hrungskontext}
     * das �bergebene Select-Statement aus und merkt sich das erzeugte
     * {@link ResultSet}.
     * 
     * @param select auszuf�hrendes Select-Statement
     * @param context context {@link ExecutionContext Ausf�hrungskontext},
     *  in dem gelesen werden soll.
     */
	void executeSelect(Select select, ExecutionContext context) throws BeanException {
		resultSet = context.result(select);
	}

    /**
     * Diese Methode schlie�t ein mit {@link #executeSelect(Select, ExecutionContext)}
     * ge�ffnetes {@link ResultSet}.
     */
	void closeResultSet() {
		try {
			if (resultSet != null)
				resultSet.close();
		} catch (SQLException e) {
            if (logger.isDebugEnabled())
                logger.debug("Fehler beim Schlie�en eines ResultSets", e);
		}
	}

    /**
     * Diese Methode liefert eine Map der Properties zum Typ der �bergebenen Bean. 
     * 
     * @param bean Bean, zu deren Typ die Properties geholt werden sollen.
     * @return {@link Map} der Properties des Typs der �bergebenen Bean.
     * @throws IOException
     */
	Map loadProperties(Bean bean) throws IOException {
		String beanname = bean.getClass().getName();
		beanname = beanname.substring(beanname.lastIndexOf('.') + 1);
		
		File file = null;
		if (cntx != null)
			file = new File(cntx.moduleRootPath() + "/beans/" + beanname + ".properties");
		else
			file = new File("webapp/OCTOPUS/beans/" + beanname + ".properties");
		
		Properties properties = new Properties();
		properties.load(new FileInputStream(file));
		
		Map map = new HashMap(properties.size());
		for (Iterator it = properties.entrySet().iterator(); it.hasNext(); ) {
			Entry entry = (Entry)it.next();
			map.put(entry.getKey(), entry.getValue());
		}
		return map;
	}

    //
    // private Hilfsklassen
    //
    /**
     * Diese Klasse stellt eine Sortier-Klausel dar, deren Inhalt roh gesetzt wird.<br>
     * Achtung: Hierbei handelt es sich um einen Hack, der andere Methoden von {@link Order}
     * unterwandert. Nur benutzen, wenn klar ist, dass diese Klausel nur erzeugt oder
     * an einen StringBuffer angeh�ngt wird.
     */
	static private class StringOrder extends Order {
		private final String order;
		
		private StringOrder(String order) {
			this.order = order;
		}
		
		protected void appendClause(StringBuffer sb) {
			sb.append(order);
		}
	}
    
    //
    // gesch�tzte Variablen
    //
    /** Aktuelles ResultSet */
    private ResultSet resultSet;

    /** Octopus-Kontext, in dem hier gearbeitet wird */
    protected final OctopusContext cntx;
    /** Modulname f�r den dblayer-Datenbankzugriff */
    protected final String moduleName;
    
    /** statischer Cache der Bean-Klassen-Properties */
    final static private Map beans = new HashMap();

    /** Dient zum Erstellen eines zusammengesetzten Property-Attribute-Schl�ssels */
    final static MessageFormat propertyAttributeKeyFormat = new MessageFormat("{0}({1})");
    /** Logger dieser Klasse */
    final static Logger logger = Logger.getLogger(Database.class);
}
