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
 * $Id: Database.java,v 1.20 2007/06/11 13:24:36 christoph Exp $
 *
 * Created on 21.02.2005
 */
package de.tarent.octopus.beans;

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
import java.util.logging.Level;
import java.util.logging.Logger;

import de.tarent.dblayer.engine.DB;
import de.tarent.dblayer.engine.Pool;
import de.tarent.dblayer.helper.ResultList;
import de.tarent.dblayer.sql.SQL;
import de.tarent.dblayer.sql.Statement;
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
    /** This boolean attribut define if a column is read only. */
    public final static String ATTRIBUTE_READ_ONLY = "ro";

    /** This boolean attribut define if a table need a distinct select. */
    public final static String ATTRIBUTE_SELECT_DISINCT = "distinct";

    /** This boolean attribut define if a table need a distinct select. */
    public final static String ATTRIBUTE_SELECT_NO_DISINCT = "nodistinct";

    /** relative bean property path inside an octopus module */
    public final static String OCTOPUS_BEAN_SUB_FOLDER = "beans";

    //
    // Konstruktoren
    //
    /**
     * This constructor initializes the {@link BeanFactory} using the given
     * bean package and sets the pool name and the bean property folder to the
     * octopus module name and the {@link #OCTOPUS_BEAN_SUB_FOLDER beans} sub
     * folder of the octopus module path respectively.
     *
     * @param cntx octopus context determining the pool name and bean properties
     *  path.
     * @param beanPackage Java package of the {@link Bean} implementations for this
     *  {@link BeanFactory}.
     */
	public Database(OctopusContext cntx, String beanPackage) {
	super(beanPackage);
	this.beanPropertyPath = new File(cntx.moduleRootPath(), OCTOPUS_BEAN_SUB_FOLDER);
		this.poolName = cntx.getModuleName();
	}

    /**
     * This constructor initializes the {@link BeanFactory} using the given
     * bean package, pool name, and bean property folder.
     *
     * @param poolName pool name for the db layer access.
     * @param beanPropertyPath bean property folder
     * @param beanPackage Java package of the {@link Bean} implementations for this
     *  {@link BeanFactory}.
     */
	public Database(String poolName, File beanPropertyPath, String beanPackage) {
	super(beanPackage);
		this.beanPropertyPath = beanPropertyPath;
		this.poolName = poolName;
	}

    /**
     * Returns the DBLayer pool identifier for this DBContext.
     *
     * @see de.tarent.dblayer.engine.DBContext#getPoolName()
     */
    public String getPoolName() {
	return this.poolName;
    }

    /**
     * Returns the DBLayer Pool Object for this DBContext
     *
     * @see de.tarent.dblayer.engine.DBContext#getPool()
     */
    public Pool getPool() {
	return DB.getPool(this.getPoolName());
    }

    //
    // Öffentliche Methoden
    //
    /**
     * Diese Methode liefert einen neuen {@link TransactionContext Transaktionskontext}
     * zu dieser Datenbank-Instanz.
     */
    public TransactionContext getTransactionContext() {
	return new TransactionContext(this);
    }

    // Öffentliche Bean-orientierte Methoden
    /**
     * Diese Methode liefert eine Bean vom übergebenen Typ zum übergebenen
     * Primärschlüssel aus der Datenbank.
     *
     * @param beanname Klasse der zu holenden Bean
     * @param pk Primärschlüssel des auszulesenden DB-Datensatzes.
     * @return die ausgelesene Bean oder <code>null</code>
     * @throws BeanException
     * @throws IOException bei Problemen beim Zugriff auf die Bean-Properties
     */
    public Bean getBean(String beanname, Integer pk) throws BeanException, IOException {
	return getBean(beanname, pk, this);
    }

    /**
     * Diese Methode liefert eine Bean vom übergebenen Typ zum übergebenen
     * Primärschlüssel aus dem übergebenen {@link ExecutionContext Ausführungskontext}
     * der Datenbank.
     *
     * @param beanname Klasse der zu holenden Bean
     * @param pk Primärschlüssel des auszulesenden DB-Datensatzes.
     * @param context {@link ExecutionContext Ausführungskontext}, in dem gelesen werden soll.
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
	    logger.info("Kann Schlüsselfeldnamen nicht den Bean-Eigenschaften von " + beanname + " entnehmen; gehe von 'pk' aus.");
	    pkColumn = "pk";
	}
	return getBean(beanname, getSelect(beanname).where(Expr.equal(pkColumn, pk)), context);
    }

    /**
     * Diese Methode liefert eine Bean vom übergebenen Typ zum übergebenen
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
     * Diese Methode liefert eine Bean vom übergebenen Typ zum übergebenen
     * Select-Statement aus dem übergebenen {@link ExecutionContext Ausführungskontext}
     * der Datenbank.
     *
     * @param beanname Klasse der zu holenden Bean
     * @param select Select-Statement zum Zugriff auf den DB-Datensatz.
     * @param context {@link ExecutionContext Ausführungskontext}, in dem gelesen werden soll.
     * @return die ausgelesene Bean oder <code>null</code>
     * @throws BeanException
     */
    public Bean getBean(String beanname, Select select, ExecutionContext context) throws BeanException {
	try {
	    executeSelect(select, context);
	    return hasNext() ? fillBean(createBean(beanname)) : null;
	} finally {
	    closeResultSet(context);
	}
    }

    /**
     * Diese Methode liefert eine Liste mit Bean-Instanzen vom übergebenen Typ zum
     * übergebenen Select-Statement aus der Datenbank.
     *
     * @param beanname Klasse der zu holenden Beans
     * @param select Select-Statement zum Zugriff auf die DB-Datensätze.
     * @return eine Liste ausgelesener Beans.
     * @throws BeanException
     */
    public List getBeanList(String beanname, Select select) throws BeanException {
	return getBeanList(beanname, select, this);
    }

    /**
     * Diese Methode liefert eine Liste mit Bean-Instanzen vom übergebenen Typ zum
     * übergebenen Select-Statement aus dem übergebenen {@link ExecutionContext Ausführungskontext}
     * der Datenbank.
     *
     * @param beanname Klasse der zu holenden Beans
     * @param select Select-Statement zum Zugriff auf die DB-Datensätze.
     * @param context {@link ExecutionContext Ausführungskontext}, in dem gelesen werden soll.
     * @return eine Liste ausgelesener Beans.
     * @throws BeanException
     */
    public List getBeanList(String beanname, Select select, ExecutionContext context) throws BeanException {
	// TODO: beans on the fly erstellen
	try {
	    executeSelect(select, context);
	    return fillBeanList(beanname);
	} finally {
	    closeResultSet(context);
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
     * Diese Methode speichert eine Bean im übergebenen {@link ExecutionContext Ausführungskontext}
     * der Datenbank. Wahlweise wird hierbei im Falle eines neu erzeugten DB-Datensatzes
     * der ID-Eintrag aktualisiert, sofern er entsprechend in den Properties konfiguriert wurde.
     *
     * @param bean zu speichernde Bean
     * @param context {@link ExecutionContext Ausführungskontext}, in dem gespeichert werden soll.
     * @param updateID Flag, ob der ID-Eintrag der Bean aktualisiert werden muss
     * @throws BeanException
     * @throws IOException
     */
    public void saveBean(Bean bean, ExecutionContext context, boolean updateID) throws BeanException, IOException {
	String id = getProperty(bean, "pk");
	if (bean.getField(id) == null) {
	    String nextval = getProperty(bean, "sequence.nextval");
	    if (nextval != null) {
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
     * Diese Methode löscht den DBDatensatz, der der übergebenen Bean zugrunde liegt
     *
     * @param bean
     * @throws BeanException
     * @throws IOException
     */
    public void removeBean(Bean bean) throws BeanException, IOException {
	removeBean(bean, this);
    }

    /**
     * Diese Methode löscht den DBDatensatz, der der übergebenen Bean zugrunde liegt,
     * im übergebenen {@link ExecutionContext Ausführungskontext} der Datenbank.
     *
     * @param bean Bean, die den zu löschenden Datensatz darstellt
     * @param context Ausführungskontext, innerhalb dessen gelöscht werden soll.
     * @throws BeanException
     * @throws IOException
     */
    public void removeBean(Bean bean, ExecutionContext context) throws BeanException, IOException {
	String field = getProperty(bean, "pk");
	if (bean.getField(field) != null) {
	    context.execute(getDelete(bean));
	} else {
	    // TODO: sensible delete operation even without primary key information
	    // This might be done using a WHERE clause including comparisons of all column values.
	    logger.warning("Instance of bean class " + bean.getClass().getName() + "could not be deleted as it has no primary key property.");
	}
    }

    // Öffentliche dblayer-orientierte Methoden
    /**
     * Diese Methode führt das übergebene Select-Statement in String-Form
     * aus und erwartet als Resultat ein {@link ResultSet}, das dann
     * zurückgegeben wird.
     *
     * @param statement auszuführendes Select-Statement
     * @return resultierendes {@link ResultSet}
     * @throws BeanException
     */
    public ResultSet result(String statement) throws BeanException {
	try {
	    return DB.getResultSet(getPoolName(), statement);
	} catch (SQLException e) {
	    throw new BeanException("Fehler beim Ausführen einer Datenbankabfrage.", e);
	}
    }

    /**
     * Diese Methode führt ein Zählstatement in der Datenbank aus
     *
     * @param select Select-Statement
     * @return Zahl aus der ersten Spalte des ersten Ergebnisdatensatzes
     * @throws BeanException
     */
    public Integer getCount(Select select) throws BeanException {
	return getCount(select, this);
    }

    /**
     * Diese Methode führt ein Zählstatement in dem übergebenen
     * {@link ExecutionContext Ausführungskontext} der Datenbank aus
     *
     * @param select Select-Statement
     * @param context {@link ExecutionContext Ausführungskontext}, in dem gezählt werden soll.
     * @return Zahl aus der ersten Spalte des ersten Ergebnisdatensatzes
     * @throws BeanException
     */
    public Integer getCount(Select select, ExecutionContext context) throws BeanException {
	ResultSet rs = null;
	try {
	    rs = context.result(select);
	    if (rs.next())
		return new Integer(rs.getInt(1));

	    throw new BeanException("Failed to read a count value from the database.");
	} catch (SQLException e) {
	    throw new BeanException("Failed to read a count value from the database.", e);
	} finally {
	    context.close(rs);
	}
    }

    /**
     * Diese Methode liefert eine Ergebnisliste zum übergebenen Select-Statement aus
     * dem übergebenen {@link ExecutionContext Ausführungskontext} der Datenbank.<br>
     * Die Einträge dieser Liste sind Maps, die jeweils einen Ergebnisdatensatz
     * darstellen. Diese Maps werden erst erstellt, wenn sie abgerufen werden.<br>
     * Beware the warnings concerning the returned {@link List} implementation given
     * {@link ResultList here}, it is highly volatile and shows erratic behaviour.<br>
     * And again beware! Intensive use of this method may lead to db layer pool drought,
     * as the database connection used here can not be closed (i.e. returned to the pool)
     * before the list is finalised. Especially when using an {@link ExecutionContext}
     * that uses a fresh connection from the pool for each executed statement (e.g. this
     * class {@link Database} itself) you might drain the pool in no time.<br>
     * TODO: Replace this method by something more safe.
     *
     * @param statement Select-Statement zum Zugriff auf die DB-Datensätze.
     * @param context {@link ExecutionContext Ausführungskontext}, in dem gelesen werden soll.
     * @return eine Liste von Maps.
     * @throws BeanException
     */
     public ResultList getList(Select statement, final ExecutionContext context) throws BeanException {
		try {
			final ResultSet rs = context.result(statement);
			return new ResultList(new Runnable() {
				public void run() {
					try {
						context.close(rs);
						logger.log(Level.FINER, "Closing ResultSet for ResultList");
					} catch (BeanException e) {
						logger.log(Level.SEVERE, "Error closing ResultSet of ResultList", e);
					}
				}
			}, rs);
		} catch (SQLException e) {
			throw new BeanException("Fehler beim Ausführen einer Datenbankabfrage.", e);
		}
	}

    /**
     * Diese Methode ruft den nächsten Primärschlüssel-Wert der der übergebenen Bean
     * zugrunde liegenden Tabelle im übergebenen {@link ExecutionContext Ausführungskontext}
     * ab und setzt das ID-Feld dieser Bean entsprechend.<br>
     * Dies erlaubt beim Einfügen eines neuen Datensatzes ein sicheres Wissen um die
     * zugeteilte ID.
     *
     * @param bean Bean, die die nächste freie ID bekommen soll.
     * @param context {@link ExecutionContext Ausführungskontext}, in dem gearbeitet werden soll.
     * @throws BeanException
     * @throws IOException
     */
    public void getNextPk(Bean bean, ExecutionContext context) throws BeanException, IOException {
	String id = getProperty(bean, "pk");
	String nextval = getProperty(bean, "sequence.nextval");
	String nextvalTable = getProperty(bean, "sequence.nextval.table");
	if (nextval == null) return;

	Select select = SQL.Select(this);
	select.selectAs(nextval, "pk");
	if (nextvalTable != null)
		select.from(nextvalTable);
	bean.setField(id, getCount(select, context));
    }

    /**
     * Diese Methode ruft den höchsten Primärschlüssel-Wert der der übergebenen Bean
     * zugrunde liegenden Tabelle im übergebenen {@link ExecutionContext Ausführungskontext}
     * ab und setzt das ID-Feld dieser Bean entsprechend.<br>
     * Dies erlaubt nach einem Einfügen eines neuen Datensatzes eine plausible Annahme
     * über die ID des neuen Datensatzes.
     *
     * @param bean Bean, die die nächste freie ID bekommen soll.
     * @param context {@link ExecutionContext Ausführungskontext}, in dem gearbeitet werden soll.
     * @throws BeanException
     * @throws IOException
     */
    public void getInsertedPk(Bean bean, ExecutionContext context) throws IOException, BeanException {
	String id = getProperty(bean, "pk");
	String pk = getProperty(bean, id);
	if (pk == null) return;

	Select select = SQL.Select(this).from(getProperty(bean, "table"));
	select.selectAs("MAX(" + pk + ')', "pk");
	bean.setField(id, getCount(select, context));
    }

    /**
     * Diese Methode liefert ein Statement zum Zählen von Datensätzen, die der
     * Bean des übergebenen Namens zugrunde liegen.
     *
     * @param beanname Name der Bean, zu der Datensätze gezählt werden sollen
     * @return Select-Statment zum Zählen von Datensätzen zum übergebenen Beantyp.
     * @throws BeanException
     * @throws IOException
     */
    public Select getCount(String beanname) throws BeanException, IOException {
	return getCount(createBean(beanname));
    }

    /**
     * Diese Methode liefert ein Statement zum Zählen von Datensätzen, die dem
     * Typ der übergebenen Bean zugrunde liegen.
     *
     * @param bean Bean, zu deren Typ Datensätze gezählt werden sollen
     * @return Select-Statment zum Zählen von Datensätzen zum Typ der übergebenen Bean.
     * @throws BeanException
     * @throws IOException
     */
    public Select getCount(Bean bean) throws BeanException, IOException {
	String pk = getProperty(bean, "pk");
	Select select = SQL.Select(this);
	select.from(getProperty(bean, "table"));
	select.select("COUNT(" + getProperty(bean, pk) + ")");
	return select;
    }

    /**
     * Diese Methode liefert ein Statement zum Abfragen von Datensätzen, die der
     * Bean des übergebenen Namens zugrunde liegen.
     *
     * @param beanname Name der Bean, zu der Datensätze abgefragt werden sollen
     * @return Select-Statment zum Abfragen von Datensätzen zum übergebenen Beantyp.
     * @throws BeanException
     * @throws IOException
     */
    public Select getSelect(String beanname) throws BeanException, IOException {
	return getSelect(createBean(beanname));
    }

    /**
     * Diese Methode liefert ein Statement zum Abfragen von Datensätzen, die dem
     * Typ der übergebenen Bean zugrunde liegen.
     *
     * @param bean Bean, zu deren Typ Datensätze abgefragt werden sollen
     * @return Select-Statment zum Abfragen von Datensätzen zum Typ der übergebenen Bean.
     * @throws BeanException
     * @throws IOException
     */
    public Select getSelect(Bean bean) throws BeanException, IOException {
	String field, column;
	Select select = getEmptySelect(bean);
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
     * Diese Methode liefert ein Statement zum Abfragen von IDs von Datensätzen, die dem
     * Typ der übergebenen Bean zugrunde liegen.
     *
     * @param bean Bean, zu deren Typ Datensatz-IDs abgefragt werden sollen
     * @return Select-Statment zum Abfragen von IDs von Datensätzen zum Typ der übergebenen Bean.
     * @throws BeanException
     * @throws IOException
     */
    public Select getSelectIds(Bean bean) throws BeanException, IOException {
	Select select = getEmptySelect(bean);
	select.selectAs(getProperty(bean, "id"), "id");
	return select;
    }

    /**
     * Return a new emtry select statement with only the given from-table.
     * It also heed the parameters {@value #ATTRIBUTE_SELECT_DISINCT}
     * and {@value #ATTRIBUTE_SELECT_NO_DISINCT}.
     *
     * @param bean
     * @return
     * @throws BeanException
     * @throws IOException
     */
    public Select getEmptySelect(Bean bean) throws BeanException, IOException {
	String attrNoDistinct = getPropertyAttribute(bean, "table", ATTRIBUTE_SELECT_NO_DISINCT);
	// If ATTRIBUTE_SELECT_NO_DISINCT true return SQL.Select, if false SQL.SelectDistinct.
	if (attrNoDistinct != null && attrNoDistinct.length() != 0) {
		return Boolean.valueOf(attrNoDistinct).booleanValue() ?
				SQL.Select(this).from(getProperty(bean, "table")) :
				SQL.SelectDistinct(this).from(getProperty(bean, "table"));
	// If ATTRIBUTE_SELECT_DISINCT is true return SQL.SelectDistinct, if false SQL.Select.
	} else {
		String attrDistinct = getPropertyAttribute(bean, "table", ATTRIBUTE_SELECT_DISINCT);
		if (attrDistinct != null && attrDistinct.length() != 0) {
			return Boolean.valueOf(attrDistinct).booleanValue() ?
					SQL.SelectDistinct(this).from(getProperty(bean, "table")) :
					SQL.Select(this).from(getProperty(bean, "table"));
		}
	}
		return SQL.SelectDistinct(this).from(getProperty(bean, "table"));
    }

    /**
     * Diese Methode liefert ein Statement zum Erzeugen eines Datensatzes
     * zu der übergebenen Bean.
     *
     * @param bean Bean, zu der ein Datensätze erzeugt werden sollen
     * @return Insert-Statment zum Erzeugen eines Datensatzes zu der übergebenen Bean
     * @throws BeanException
     * @throws IOException
     */
    public Insert getInsert(Bean bean) throws BeanException, IOException {
	String pk = getProperty(bean, "pk");
	String field, column;
	Insert insert = SQL.Insert(this);
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
     * zu einer Bean mit dem übergebenen Namen.
     *
     * @param beanname Name des Bean-Typs, zu dem ein Aktualisierungs-Statement erzeugt werden soll.
     * @return Update-Statment zum Aktualisieren eines Datensatzes zu einer Bean mit dem übergebenen Namen
     * @throws BeanException
     * @throws IOException
     */
    public Update getUpdate(String beanname) throws BeanException, IOException {
	return SQL.Update(this).table(getProperty(createBean(beanname), "table"));
    }

    /**
     * Diese Methode liefert ein Statement zum Aktualisieren des Datensatzes
     * zu der übergebenen Bean.
     *
     * @param bean Bean, deren Datensätze aktualisiert werden sollen
     * @return Update-Statment zum Aktualisieren des Datensatzes der übergebenen Bean
     * @throws BeanException
     * @throws IOException
     */
    public Update getUpdate(Bean bean) throws BeanException, IOException {
	String field, column;
	Update update = SQL.Update(this);
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
     * Diese Methode liefert ein Statement zum Löschen eines Datensatzes
     * zu einer Bean mit dem übergebenen Namen.
     *
     * @param beanname Name des Bean-Typs, zu dem ein Lösch-Statement erzeugt werden soll.
     * @return Delete-Statment zum Löschen eines Datensatzes zu einer Bean mit dem übergebenen Namen
     * @throws BeanException
     * @throws IOException
     */
    public Delete getDelete(String beanname) throws BeanException, IOException {
	return SQL.Delete(this).from(getProperty(createBean(beanname), "table"));
    }

    /**
     * Diese Methode liefert ein Statement zum Löschen des Datensatzes
     * zu der übergebenen Bean.
     *
     * @param bean Bean, deren Datensätze gelöscht werden sollen
     * @return Delete-Statment zum Löschen des Datensatzes der übergebenen Bean
     * @throws BeanException
     * @throws IOException
     */
    public Delete getDelete(Bean bean) throws BeanException, IOException {
	String field, column;
	Delete delete = SQL.Delete(this);
	delete.from(getProperty(bean, "table"));
	field = getProperty(bean, "pk");
	column = getProperty(bean, field);
	delete.where(Expr.equal(column, bean.getField(field)));
	return delete;
    }

    /**
     * Diese Methode liefert eine Where-Klausel zum Bestimmen des Datensatzes
     * zu der übergebenen Bean.
     *
     * @param bean Bean, zu der eine Where-Klausel erstellt werden soll
     * @return Where-Klausel zum Bestimmen des Datensatzes zu der übergebenen Bean.
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
     * Diese Methode liefert ein Attribut eines Properties zum übergebenen Bean-Typ.
     *
     * @param bean Bean, zu deren Typ ein Property-Attribut geliefert werden soll
     * @param key Schlüssel des zu betroffenen Properties
     * @param attributeKey Schlüssel des Attributs des betroffenen Properties
     * @return das gewünschte Property-Attribut zum Bean-Typ.
     * @throws IOException
     */
    public String getPropertyAttribute(Bean bean, String key, String attributeKey) throws IOException {
	return getProperty(bean, propertyAttributeKeyFormat.format(new Object[] { key, attributeKey }));
    }

    /**
     * Diese Methode liefert ein Property zum übergebenen Bean-Typ.
     *
     * @param bean Bean, zu deren Typ ein Property geliefert werden soll
     * @param key Schlüssel des zu liefernden Properties
     * @return das gewünschte Property zum Bean-Typ.
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
     * einer Anzahl Schlüssel- (für die WHERE-Klausel) und Update-Felder (für die
     * SET-Anweisung), die im übergebenen Kontext arbeitet.
     *
     * @param sample Beispiel-{@link Bean}
     * @param keyFields Sammlung von Schlüsselfeldern
     * @param updateFields Sammlung von Update-Feldern
     * @param context {@link ExecutionContext Ausführungskontext}
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
     * @param key Feld-Schlüssel
     * @return Feldinhalt oder <code>null</code> bei DB-Zugriffsfehlern.
     * @throws BeanException bei Datenzugriffsfehlern.
     * @see BeanFactory#getField(String)
     */
	public Object getField(String key) throws BeanException {
		try {
			return resultSet.getObject(key);
		} catch (SQLException e) {
	    if (logger.isLoggable(Level.FINE))
		logger.log(Level.FINE, "Feld " + key + " kann nicht ausgelesen werden.", e);
			return null;
		}
	}

    /**
     * Wird verwendet, um bei Bean-Listen zur nächsten Bohne zu springen,
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
			throw new BeanException("Fehler beim Lesen aus der Datenbank.", e);
		}
	}

    /**
     * Diese Methode setzt basierend auf dem Factory-Wissen das Bean-Feld
     * {@link Bean#isModified() Modified}.<br>
     * Im Datenbank-Kontext wird davon ausgegangen, dass eine Prüfung genauso
     * aufwändig wie ein Update ist, und also ist dies ein NOP.
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
     * Diese Methode führt das übergebene {@link Statement} aus.
     *
     * @param statement auszuführendes {@link Statement}
     * @throws BeanException
     * @see ExecutionContext#execute(Statement)
     */
    public void execute(Statement statement) throws BeanException {
	try {
	    statement.execute();
	} catch (SQLException e) {
	    throw new BeanException("Fehler beim Ausführen eines Statements in der Datenbank.", e);
	}
    }

    /**
     * Diese Methode führt das übergebene {@link Select}-{@link Statement}
     * aus und erwartet als Resultat ein {@link ResultSet}, das dann
     * zurückgegeben wird.
     *
     * @param statement auszuführendes {@link Select}-{@link Statement}
     * @return resultierendes {@link ResultSet}
     * @throws BeanException
     * @see ExecutionContext#result(Select)
     */
    public ResultSet result(Select statement) throws BeanException {
	try {
	    return statement.getResultSet(getPoolName());
	} catch (SQLException e) {
	    throw new BeanException("Fehler beim Erzeugen eines SQL-Strings zu einem Select-Statement", e);
	}
    }

    /**
     * This method closes a {@link ResultSet} returned by {@link #result(Select)}.
     * It may also close the {@link java.sql.Statement} and {@link java.sql.Connection}
     * used for creating the {@link ResultSet} if they were opened just for this
     * task.
     *
     * @param resultSet a {@link ResultSet} returned by {@link #result(Select)}.
     * @throws BeanException
     */
    public void close(ResultSet resultSet) throws BeanException {
	DB.closeAll(resultSet);
    }

    /**
     * Diese Methode bereitet das übergebene {@link Statement} vor.<br>
     * TODO: Die geholte Connection wird nicht geschlossen, es sei denn, es tritt einen SQLException auf!
     *
     * @param statement vorzubereitendes {@link Statement}
     * @return resultierendes {@link PreparedStatement}
     * @throws BeanException
     */
    public PreparedStatement prepare(Statement statement) throws BeanException {
	Connection con = null;
	try {
	    con = DB.getConnection(getPoolName());
	    con.setAutoCommit(true);
	    return con.prepareStatement(statement.statementToString());
	} catch (SQLException e) {
	    DB.close(con);
	    throw new BeanException("Fehler beim Erstellen eines PreparedStatements", e);
	}
    }

    /**
     * Diese Methode liefert die {@link Database}, in der dieser Kontext arbeitet.
     *
     * @return zugehörige {@link Database}
     */
    public Database getDatabase() {
	return this;
    }

    //
    // geschätzte Hilfsmethoden
    //
    /**
     * Diese Methode erzeugt das eigentliche dblayer-SQL-Statement für den Update
     * und fällt die übergebene Liste der Platzhalter-Felder.
     *
     * @param sample eine Beispiel-Bean zu der Update-Tabelle
     * @param keyFields eine Sammlung von Filterfeldern für die Abfrage
     * @param updateFields eine Sammlung Felder für den Update in dieser Abfrage
     * @param fieldsInStatement Liste, der die Felder zum Füllen der Platzhalter
     *  hinzugefügt werden.
     * @return dblayer-SQL-Statement
     */
    Update getPreparedUpdate(Bean sample, Collection keyFields, Collection updateFields, List fieldsInStatement) throws IOException {
	assert keyFields != null && !keyFields.isEmpty();
	assert updateFields != null && !updateFields.isEmpty();

	WhereList where = new WhereList();
	Collection keyFieldsFound = new ArrayList();
	String field, column;
	Update update = SQL.Update(this);
	update.table(getProperty(sample, "table"));
	for (Iterator it = sample.getFields().iterator(); it.hasNext(); ) {
	    field = (String)it.next();
	    column = getProperty(sample, field);
	    if (keyFields.contains(field)) {
		if (column == null || column.length() == 0)
		    logger.warning("Schlüsselfeld ohne zugeordnete Tabellenspalte: " + field);
		else {
		    keyFieldsFound.add(field);
		    where.addAnd(Expr.equal(column, BeanBaseStatement.PLACE_HOLDER));
		}
	    } else if (updateFields.contains(field)) {
		if (column == null || column.length() == 0)
		    logger.warning("Update-Feld ohne zugeordnete Tabellenspalte: " + field);
		else if (Boolean.valueOf(getPropertyAttribute(sample, field, Database.ATTRIBUTE_READ_ONLY)).booleanValue())
		    logger.warning("Update-Feld ohne zugeordnete Tabellenspalte: " + field);
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
     * Diese Methode führt im übergebenen {@link ExecutionContext Ausführungskontext}
     * das übergebene Select-Statement aus und merkt sich das erzeugte
     * {@link ResultSet}.
     *
     * @param select auszuführendes Select-Statement
     * @param context context {@link ExecutionContext Ausführungskontext},
     *  in dem gelesen werden soll.
     */
	void executeSelect(Select select, ExecutionContext context) throws BeanException {
		resultSet = context.result(select);
	}

    /**
     * Diese Methode schließt ein mit {@link #executeSelect(Select, ExecutionContext)}
     * geöffnetes {@link ResultSet}.
     */
	void closeResultSet(ExecutionContext context) {
		try {
			if (resultSet != null)
				context.close(resultSet);
		} catch (BeanException e) {
	    logger.log(Level.WARNING, "Fehler beim Schließen eines ResultSets", e);
		}
	}

    /**
     * Diese Methode liefert eine Map der Properties zum Typ der übergebenen Bean.
     *
     * @param bean Bean, zu deren Typ die Properties geholt werden sollen.
     * @return {@link Map} der Properties des Typs der übergebenen Bean.
     * @throws IOException
     */
	Map loadProperties(Bean bean) throws IOException {
		String beanname = bean.getClass().getName();
		beanname = beanname.substring(beanname.lastIndexOf('.') + 1);

		File file = new File(beanPropertyPath, beanname + ".properties");

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
     * an einen StringBuffer angehängt wird.
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
    // geschätzte Variablen
    //
    /** Aktuelles ResultSet */
    private ResultSet resultSet;

    /** Modulname für den dblayer-Datenbankzugriff */
    protected final String poolName;
    /** path of the property files describing the bean db mapping */
    protected final File beanPropertyPath;

    /** statischer Cache der Bean-Klassen-Properties */
    final static private Map beans = new HashMap();

    /** Dient zum Erstellen eines zusammengesetzten Property-Attribute-Schlüssels */
    final static MessageFormat propertyAttributeKeyFormat = new MessageFormat("{0}({1})");
    /** logger of this class. */
    final static Logger logger = Logger.getLogger(Database.class.getName());
}
