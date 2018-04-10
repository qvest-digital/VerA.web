package de.tarent.dblayer.persistence;

/*-
 * VerA.web-Middleware, newly MIT licenced, is comprised of:
 * tarent-commons, a set of common components and solutions
 * tarent-contact, platform-independent webservice-based contact management
 * tarent-database, jdbc database library
 * tarent-doctor, Document Generation Platform
 * tarent-octopus, Webservice Data Integrator and Application Server
 *  © 2005, 2006, 2007 asteban <s.mancke@tarent.de>
 *  © 2007 David Goemans <d.goemans@tarent.de>
 *  © 2006, 2007, 2010 Hendrik Helwich <h.helwich@tarent.de>
 *  © 2005, 2006, 2007 Christoph Jerolimov <c.jerolimov@tarent.de>
 *  © 2006 Philipp Kirchner <p.kirchner@tarent.de>
 *  © 2010 Carsten Klein <c.klein@tarent.de>
 *  © 2006 Michael Kleinhenz <m.kleinhenz@tarent.de>
 *  © 2006 Michael Klink <m.klink@tarent.de>
 *  © 2007 Fabian Köster <f.koester@tarent.de>
 *  © 2006 Martin Ley <m.ley@tarent.de>
 *  © 2007 Alex Maier <a.maier@tarent.de>
 *  © 2007, 2015, 2017, 2018 mirabilos <t.glaser@tarent.de>
 *  © 2006, 2007 Jens Neumaier <j.neumaier@tarent.de>
 *  © 2006 Nils Neumaier <n.neumaier@tarent.de>
 *  © 2007, 2008 Martin Pelzer <m.pelzer@tarent.de>
 *  © 2008, 2009 Christian Preilowski <c.thiel@tarent.de>
 *  © 2006, 2008, 2009 Thomas Schmitz <t.schmitz@tarent.de>
 *  © 2007 Robert Schuster <r.schuster@tarent.de>
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

import de.tarent.commons.datahandling.entity.EntityProperty;
import de.tarent.dblayer.sql.statement.Insert;
import de.tarent.dblayer.engine.*;
import de.tarent.dblayer.sql.statement.*;
import de.tarent.dblayer.sql.ParamValue;
import de.tarent.dblayer.sql.SQL;
import java.util.*;

/**
 * Simple implementation of the AbstractDBMapping interface for configuration in Java.
 *
 */
public abstract class AbstractDBMapping implements DBMapping {

	public static final String PROPERTY_SEPARATOR = ".";

	// the following definitions are deprecated and should no longer be used
	@Deprecated
    protected static final int EMPTY_FIELD_SET = 0;
	@Deprecated
    protected static final int PRIMARY_KEY_FIELDS = 1;
	@Deprecated
    protected static final int COMMON_FIELDS = 2;
	@Deprecated
    protected static final int MINIMAL_FIELDS = 4;
	@Deprecated
    protected static final int WRITEABLE_FIELDS = 8;
	@Deprecated
    protected static final int ALL_FIELDS = 0xFFFF;

	@Deprecated
    protected static final int DEFAULT_FIELD_SET = COMMON_FIELDS | WRITEABLE_FIELDS;

    /** Map for storing the fields; some standard fields
     * are already defined.
     * The key defines the name, the value the value.
     *
     */
    protected Map<String, Integer> fields = new HashMap<String, Integer>();
    private int currentNewFieldsNumber;	// contains the number of the the next custom field that will be added

    /**
     * Maximal length of an identifier bevore we use an alias.
     * For example the Oracle allows only 30 characters.
     */
    protected int maxIdentifierLength = 30;
    protected int aliasCounter = 1;
    protected Map aliasMap = null;

    protected List fieldList = new ArrayList();
    protected List statementList = new ArrayList();

    protected Insert insert;
    protected Update update;
    protected Delete delete;

    protected Class associatedBean;

    /**
     * This is a context, whoch should not be used for sql execution,
     * but may be used to obtain configuration information of the underlaying database;
     */
    DBContext contextWithPoolInformation = null;

    /** Default constructor doing nothing. The DBContext has to be set seperately
     * and the configure and init method has to be called from outside.
     *
     */
    public AbstractDBMapping() {
	this.initFields();
    }

    /**
     * Default constructor.
     * Calles the abstract configureMapping() and starts the initialization.
     *
     * @param dbc A database context, which will be used for configuration information and not for connecting to the database.
     */
    public AbstractDBMapping(DBContext dbc) {
	this.contextWithPoolInformation = dbc;
	this.initFields();
	configureMapping();
	init();
    }

    /**
     * Default constructor.
     * Calles the abstract configureMapping() and starts the initialization.
     *
     * @param dbc A database context, which will be used for configuration information and not for connecting to the database.
     */
    public AbstractDBMapping(DBContext dbc, Class associatedBean) {
	this.contextWithPoolInformation = dbc;
	this.associatedBean = associatedBean;
	this.initFields();
	configureMapping();
	init();
    }

    /**
     * Default constructor with a custom max Identifier length.
     * Calles the abstract configureMapping() and starts the initialization.
     *
     * @param maxIdentifierLength the maximal length of identifiers for the database system.
     * @param dbc A database context, which will be used for configuration information and not for connecting to the database.
     */
    public AbstractDBMapping(int maxIdentifierLength, DBContext dbc) {
	this.maxIdentifierLength = maxIdentifierLength;
	this.contextWithPoolInformation = dbc;
	this.initFields();
	configureMapping();
	init();
    }

    /** fills the field list with standard fields
     *
     *
     */
    private void initFields() {
	this.fields.put("emptyFieldSet", new Integer(0));
	this.fields.put("primaryKeyFields", new Integer(1));
	this.fields.put("commonFields", new Integer(2));
	this.fields.put("minimalFields", new Integer(4));
	this.fields.put("writeableFields", new Integer(8));
	this.fields.put("allFields", new Integer(0xFFFF));
	this.fields.put("defaultFieldSet", new Integer(2 | 8));

	// set starting point of custom fields to 7 so that
	// the next field will get value 2^7 = 128
	this.currentNewFieldsNumber = 7;
    }

    /** adds a custom field definition to the list. The n
     * is chosen internally.
     *
     * @param name the name of the new field definition
     */
    public void addCustomFieldDefinition(String name) {
	if (!this.fields.containsKey(name)) {
		this.fields.put(name, this.pow(2, this.currentNewFieldsNumber));
		this.currentNewFieldsNumber++;
	}
    }

    private int pow(int a, int b) {
	int result = 1;
	for (int i = 0; i < b; i++)
		result *= a;
	return result;
    }

    /** returns the integer value for a given field definition name;
     * if the name is not defined yet, it will be defined
     *
     * @param name
     * @return
     */
    protected int getFieldDefinitionValue(String name) {
	Integer value = this.fields.get(name);
	if (value != null)
		return value.intValue();
	else {
		// field not defined. Define now.
		this.addCustomFieldDefinition(name);
		return this.fields.get(name).intValue();
	}
    }

    /** returns the combined integer value for a given
     * set of field set names.
     *
     * @param names the names of the field sets
     * @return the combined integer value for all given fields
     */
    protected int getFieldDefinitionValue(String... names) {
	int value = 0;
	for (int i = 0; i < names.length; i++)
		value |= this.getFieldDefinitionValue(names[i]);
	return value;
    }

    public void setBeanName(Class associatedBean) {
	this.associatedBean = associatedBean;
    }

    //
    // Abstract methods
    //
    /**
     * Abstract template method for initialisation
     */
    public abstract void configureMapping();

    /**
     * Returns the target table of this mapping
     */
    public abstract String getTargetTable();

    //
    // helper methods for configuration
    //
    public void setDBContext(DBContext dbc) {
	this.contextWithPoolInformation = dbc;
    }

    /**
     * Returns a basic Select for the target table
     */
    protected Select createBasicSelectAll() {
	return SQL.Select(contextWithPoolInformation).from(getTargetTable());
    }

    /**
     * Returns a basic select including a filter on the primary key fields
     */
    protected Select createBasicSelectOne() {
	Select select = SQL.Select(contextWithPoolInformation).from(getTargetTable());
	for (Iterator iter = fieldList.iterator(); iter.hasNext();) {
	    FieldMapping field = (FieldMapping)iter.next();
	    // add all primary key fields as where clause
	    if (field.containedInFieldSet(this.getFieldDefinitionValue("primaryKeyFields")))
		select.whereAndEq(field.getColumnName(), new ParamValue(field.getPropertyName()));
	}
	return select;
    }

    /**
     * Returns a basic insert statement
     */
    protected Insert createBasicInsert() {
	Insert insert = SQL.Insert(contextWithPoolInformation).table(getTargetTable());
	for (Iterator iter = fieldList.iterator(); iter.hasNext();) {
	    FieldMapping field = (FieldMapping)iter.next();
	    // add all primary key fields as generated keys
	    if (field.containedInFieldSet(this.getFieldDefinitionValue("primaryKeyFields"))) {
		insert.addReturnKeyColumn(field.getColumnName());
	    }
	}
	return insert;
    }

    /**
     * Returns a basic update statement for one record, including a filter in the primary key fields
     *
     * @package allowEmptyWhere should be set to false, to prevent for dangerous statements
     * @throws IllegalStateException if no primary key fields exist and an emptyWhere is not allowed
     */
    protected Update createBasicUpdate(boolean allowEmptyWhere) {
	Update update = SQL.Update(contextWithPoolInformation).table(getTargetTable());
	boolean filterExist = false;
	for (Iterator iter = fieldList.iterator(); iter.hasNext();) {
	    FieldMapping field = (FieldMapping)iter.next();
	    // add all primary key fields as where clause
	    if (field.containedInFieldSet(this.getFieldDefinitionValue("primaryKeyFields"))) {
		update.whereAndEq(field.getColumnName(), new ParamValue(field.getPropertyName()));
		filterExist = true;
	    }
	}
	if (!filterExist && !allowEmptyWhere)
	    throw new IllegalStateException("creating unsave update statement without filter rule");
	return update;
    }

    /**
     * Returns a basic delete statement for one record, including a filter in the primary key fields
     *
     * @package allowEmptyWhere should be set to false, to prevent for dangerous statements
     * @throws IllegalStateException if no primary key fields exist and an emptyWhere is not allowed
     */
    protected Delete createBasicDelete(boolean allowEmptyWhere) {
	Delete delete = SQL.Delete(contextWithPoolInformation).from(getTargetTable());
	boolean filterExist = false;
	for (Iterator iter = fieldList.iterator(); iter.hasNext();) {
	    FieldMapping field = (FieldMapping)iter.next();
	    // add all primary key fields as where clause
	    if (field.containedInFieldSet(this.getFieldDefinitionValue("primaryKeyFields"))){
		delete.whereAndEq(field.getColumnName(), new ParamValue(field.getPropertyName()));
		filterExist = true;
	    }
	}
	if (!filterExist && !allowEmptyWhere)
	    throw new IllegalStateException("creating unsave delete statement without filter rule");
	return delete;
    }

    /**
     * Adds an insert Statement
     */
    protected void add(Insert newInsert) {
	this.insert = newInsert;
    }

    /**
     * Adds an update Statement
     */
    protected void add(Update newUpdate) {
	this.update = newUpdate;
    }

    /**
     * Adds an delete Statement
     */
    protected void add(Delete newDelete) {
	this.delete = newDelete;
    }

    /**
     * Adds a field to the list of field mappings.
     *
     * If the propertyName of the field is longer than maxIdentifierLength,
     * we cut it off and append a unique id to fit in the length.
     *
     */
    protected void add(FieldMapping field) {
	if (field.propertyName.length() > maxIdentifierLength) {
	    String property = field.propertyName;
	    field.propertyName = property.substring(0, maxIdentifierLength-4)  +"-"+ (aliasCounter++);
	    field.originalPropertyName = property;
	    if (aliasMap == null)
		aliasMap = new HashMap();
	    aliasMap.put(field.propertyName, field.originalPropertyName);
	}
	fieldList.add(field);
    }

    /**
     * Creates a new Fieldmapping and appends it to the list of field mappings.
     *
     */
    protected void addField(String columnName, String propertyName, int fieldSetBitmask) {
	add(new FieldMapping(columnName, propertyName, fieldSetBitmask));
    }

    /**
     * Creates a new Fieldmapping and appends it to the list of field mappings, using the default bitmask.
     */
    protected void addField(String columnName, String propertyName) {
	add(new FieldMapping(columnName, propertyName));
    }

    /**
     * Creates a new Fieldmapping and appends it to the list of field mappings.
     *
     * <p>Works on <code>EntityProperty</code> objects which have been introduced in tarent-commons 1.1.6 .</p>
     */
    protected void addField(String columnName, EntityProperty property, int fieldSetBitmask) {
	add(new FieldMapping(columnName, property.getKey(), fieldSetBitmask));
    }

    /**
     * Creates a new Fieldmapping and appends it to the list of field mappings, using the default bitmask.
     *
     * <p>Works on <code>EntityProperty</code> objects which have been introduced in tarent-commons 1.1.6 .</p>
     */
    protected void addField(String columnName, EntityProperty property) {
	add(new FieldMapping(columnName, property.getKey()));
    }

    /**
     * Add the fields of another entity represented by an AbstractDBMapping.
     *
     * @param refMapping the DBMapping of the joined entity
     * @param propertyNamePrefix prefix to prepend before the included propertyNames
     * @param includeBitmask set of fields to include from the joined mapping
     * @param fieldSetBitmask field set under which the fields are added to this mapping
     */
    protected void addFields(AbstractDBMapping refMapping, String propertyNamePrefix, int includeBitmask, int fieldSetBitmask) {
	for (Iterator iter = refMapping.getFieldList().iterator(); iter.hasNext();) {
	    FieldMapping field = (FieldMapping)iter.next();
	    if (field.containedInFieldSet(includeBitmask))
		addField(field.getColumnName(), concatPropCol(propertyNamePrefix,field.getPropertyName()), fieldSetBitmask);
	}
    }

    /**
     * Returns the list of field mappings
     * @return List of FieldMapping objects
     */
    protected List getFieldList() {
	return fieldList;
    }

    /**
     * Appends a new Query to the Query list
     */
    protected void addQuery(String queryID, Select query, int fieldSetBitmask) {
	statementList.add(new Query(queryID, query, fieldSetBitmask));
    }

    /**
     * Concatenate the given property name
     * with the {@link #PROPERTY_SEPARATOR} and column name
     * Returns the concatenated string back.
     * @param propertyName
     * @param columnName
     * @return concatenated string
     */
    protected String concatPropCol(String propertyName, String columnName){
	return propertyName.concat(PROPERTY_SEPARATOR).concat(columnName);
    }

    //
    // interface DBMapping implementation
    //
    /**
     * {@see DBMapping#getQuery()}
     */
    public Select getQuery(String statementID) {
	for (Iterator iter = statementList.iterator(); iter.hasNext();) {
	    Query q = (Query)iter.next();
	    if (statementID.equals(q.getQueryID()))
		return q.getQuery();
	}
	return null;
    }

    /**
     * Retuns the Fields, contained in the query for the supplied ID
     */
    public Field[] getQueryFields(String statementID) {
	for (Iterator iter = statementList.iterator(); iter.hasNext();) {
	    Query q = (Query)iter.next();
	    if (statementID.equals(q.getQueryID()))
		return q.getQueryFields();
	}
	return null;
    }

    /**
     * Returns an insert statement for this business object. The Fields of the
     * insert should be set over the ParamValue mechanism.
     *
     * <p>Be carefull, this Select instance may be the same for multiple running threads.
     * If you need to modify it, you should create a clone first.</p>
     */
    public Insert getInsert() {
	return insert;
    }

    /**
     * Returns an insert update for this business object. The Fields of the
     * insert should be set over the ParamValue mechanism.
     *
     * <p>Be carefull, this Select instance may be the same for multiple running threads.
     * If you need to modify it, you should create a clone first.</p>
     */
    public Update getUpdate() {
	return update;
    }

    /**
     * Returns an delete update for this business object. The Fields of the
     * insert should be set over the ParamValue mechanism.
     *
     * <p>Be carefull, this Select instance may be the same for multiple running threads.
     * If you need to modify it, you should create a clone first.</p>
     */
    public Delete getDelete() {
	return delete;
    }

    /**
     * Returns the original (full) property name of a property.
     * If the propertyName was not to long, we return the supplied propertyName.
     *
     * @param propertyName the property name as contained in the result set.
     */
    public String getOriginalPropertyName(String propertyName) {
	if (aliasMap == null || ! (aliasMap.containsKey(propertyName)))
	    return propertyName;
	return (String)aliasMap.get(propertyName);
    }

    public String getColumnNameByProperty(String propertyName) {
	for (Iterator iter = fieldList.iterator(); iter.hasNext();) {
	    FieldMapping field = (FieldMapping)iter.next();
	    if (field.getPropertyName().equals(propertyName))
		return field.getColumnName();
	}
	return null;
    }

    /**
     * Returns the primaryKeys field.
     * If this mapping has more than one pk, the first one is returned.
     */
    public Field getPkField() {
	for (Iterator iter = fieldList.iterator(); iter.hasNext();) {
	    FieldMapping field = (FieldMapping)iter.next();
	    // add all primary key fields as generated keys
	    if (field.containedInFieldSet(this.getFieldDefinitionValue("primaryKeyFields")))
		return field;
	}
	return null;
    }

    //
    // private initialisation methods
    //

    /**
     * Enhances the existing statements with the configured fields.
     * If they do not exist, the default statements for this mapping will be created.
     */
    public void init() {
	// add default statement for selection multiple records
	if (getQuery(STMT_SELECT_ALL) == null)
	    addQuery(STMT_SELECT_ALL, createBasicSelectAll(), this.getFieldDefinitionValue("commonFields"));

	// add default statement for selection of one record
	if (getQuery(STMT_SELECT_ONE) == null)
	    addQuery(STMT_SELECT_ONE, createBasicSelectOne(), this.getFieldDefinitionValue("commonFields"));

	for (Iterator iter = statementList.iterator(); iter.hasNext();)
	    completeSelect((Query)iter.next());

	if (insert == null)
	    insert = createBasicInsert();
	completeInsert(insert);

	if (update == null)
	    update = createBasicUpdate(false);
	completeUpdate(update);

	if (delete == null)
	    delete = createBasicDelete(false);
	// nothing to complete for "delete"
    }

    private void completeSelect(Query query) {
	FieldMapping[] fields = query.getQueryFields();
	Select select = query.getQuery();
	HashMap addedProperties = new HashMap();
	for (int i = 0; i < fields.length; i++) {
		// only add a column for one property-name once
		if (!addedProperties.containsKey(fields[i].getPropertyName())) {
			select.selectAs(fields[i].getColumnName(), fields[i].getPropertyName());
			addedProperties.put(fields[i].getPropertyName(), fields[i].getPropertyName());
		}
	}
    }

    private void completeInsert(Insert insert) {
	for (Iterator iter = fieldList.iterator(); iter.hasNext();) {
	    FieldMapping field = (FieldMapping)iter.next();
	    // only add, if the bitmask of the field contains the bitmask of the statement
	    if (field.containedInFieldSet(this.getFieldDefinitionValue("writeableFields")))
		insert.insert(field.getColumnName(), new ParamValue(field.getPropertyName()));
	}
    }

    private void completeUpdate(Update update) {
	for (Iterator iter = fieldList.iterator(); iter.hasNext();) {
	    FieldMapping field = (FieldMapping)iter.next();
	    // only add, if the bitmask of the field contains the bitmask of the statement
	    if (field.containedInFieldSet(this.getFieldDefinitionValue("writeableFields")))
		update.update(field.getColumnName(), new ParamValue(field.getPropertyName()));
	}
    }

    //
    // inner classes
    //
    public class Query {
	String queryID;
	Select query;
	int fieldSetBitmask;
	FieldMapping[] queryFields;

	public Query(String queryID, Select query, int fieldSetBitmask) {
	    this.queryID = queryID;
	    this.query = query;
	    this.fieldSetBitmask = fieldSetBitmask;
	}

	public FieldMapping[] getQueryFields() {
	    if (queryFields == null) {
		ArrayList fields = new ArrayList();
		for (Iterator iter = fieldList.iterator(); iter.hasNext();) {
		    FieldMapping field = (FieldMapping)iter.next();
		    // only add, if the bitmask of the field contains the bitmask of the statement
		    if (field.containedInFieldSet(getFieldSetBitmask()))
			fields.add(field);
		}
		queryFields = (FieldMapping[])fields.toArray(new FieldMapping[fields.size()]);
	    }
	    return queryFields;
	}

	public String getQueryID() {
	    return queryID;
	}

	public Select getQuery() {
	    return query;
	}

	public int getFieldSetBitmask() {
	    return fieldSetBitmask;
	}

	public boolean equals(Object o) {
	    return ((o instanceof Query) || ((Query)o).getQueryID().equals(queryID));
	}
    }

    public class FieldMapping implements Field {

	String columnName;
	String propertyName;
	String originalPropertyName;
	int fieldSetBitmask;

	public FieldMapping(String columnName, String propertyName, int fieldSetBitmask) {
	    this.columnName = columnName;
	    this.propertyName = propertyName;
	    this.originalPropertyName = propertyName;
	    this.fieldSetBitmask = fieldSetBitmask;
	}

	public FieldMapping(String columnName, String propertyName) {
	    this.columnName = columnName;
	    this.propertyName = propertyName;
	    this.originalPropertyName = propertyName;
	    this.fieldSetBitmask = getFieldDefinitionValue("defaultFieldSet");
	}

	/**
	 * Returns true, if the field is contained in the field set
	 * represented by the supplied bit mask.
	 */
	public boolean containedInFieldSet(int fieldSet) {
	    return 0 < (fieldSet & fieldSetBitmask);
	}

	public int getFieldSetBitmask() {
	    return fieldSetBitmask;
	}

	/**
	 * Returns the table column, this field is mapped to
	 */
	public String getColumnName() {
	    return columnName;
	}

	/**
	 * Returns objects property name for this field
	 */
	public String getPropertyName() {
	    return propertyName;
	}
    }
}
