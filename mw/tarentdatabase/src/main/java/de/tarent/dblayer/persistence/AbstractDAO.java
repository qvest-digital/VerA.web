package de.tarent.dblayer.persistence;

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

import de.tarent.commons.datahandling.ListFilter;
import de.tarent.commons.datahandling.entity.*;
import de.tarent.dblayer.engine.DB;
import de.tarent.dblayer.engine.DBContext;
import de.tarent.dblayer.engine.InsertKeys;
import de.tarent.dblayer.persistence.annotations.ForeignKey;
import de.tarent.dblayer.persistence.annotations.Id;
import de.tarent.dblayer.persistence.annotations.Reference;
import de.tarent.dblayer.sql.statement.ExtPreparedStatement;
import de.tarent.dblayer.sql.statement.Select;

import java.lang.reflect.Method;
import java.sql.*;
import java.util.*;

import org.apache.commons.logging.Log;

import de.tarent.dblayer.sql.clause.Where;
import de.tarent.dblayer.sql.clause.Limit;
import de.tarent.dblayer.sql.clause.Clause;
import de.tarent.commons.datahandling.ListFilterOperator;
import de.tarent.commons.logging.LogFactory;
import de.tarent.dblayer.sql.ParamValue;
import de.tarent.dblayer.sql.clause.Order;
import de.tarent.commons.datahandling.PrimaryKeyList;
import de.tarent.dblayer.sql.clause.Expr;
import de.tarent.commons.logging.TimeMeasureTool;
import de.tarent.commons.utils.Pojo;

public abstract class AbstractDAO {

    private static final Log logger = LogFactory.getLog(AbstractDAO.class);

    /**
     * the String "<code>desc</code>" for descending orders
     */
    public static final String DESC = "desc";
    /**
     * the String "<code>asc</code>" for ascending orders
     */
    public static final String ASC = "asc";

    DBMapping dbMapping;
    EntityFactory entityFactory;
    private Class bean;

    public AbstractDAO() {

    }

    public AbstractDAO(Class bean) {
        // register this DAO in the global DAO registry
        DAORegistry.registerDAO(this, bean);
        this.bean = bean;
    }

    public AbstractDAO(DBMapping dbMapping, EntityFactory entityFactory) {
        this.dbMapping = dbMapping;
        this.entityFactory = entityFactory;
    }

    /**
     * has to be overwritten if bean is not set
     *
     * @param keys
     * @param entity
     */
    public void setEntityKeys(InsertKeys keys, Object entity) {
        if (this.bean == null) {
            return;
        }

        // if bean is set we can search for the method marked with @Id. The
        // related setter method has to be used to set the id.
        Method[] methods = this.bean.getMethods();
        for (int i = 0; i < methods.length; i++) {
            if (methods[i].getAnnotation(Id.class) != null) {
                // We found the method marked with @Id. Get related setter method and set pk.
                Pojo.set(entity, methods[i].getName().substring(3).toLowerCase(), keys.getPk());
            }
        }
    }

    /**
     * Helper method for retrieving a single Entity identified by a single-field primary key.
     */
    protected Object getEntityByIdFilter(DBContext dbc, String queryID, String idPropertyName, Object idValue)
      throws SQLException {
        ExtPreparedStatement eps = getDbMapping().getQuery(queryID).prepare(dbc);
        ;
        try {
            eps.setAttribute(idPropertyName, idValue);
            return getEntityByPreparedStatement(dbc, eps.getPreparedStatement());
        } finally {
            eps.close();
        }
    }

    /**
     * Helper method for retrieving a single entity identified by a key
     * consisting of more than one parameter.
     *
     * @param dbc     The database context
     * @param queryID The id of the used query
     * @param params  The list of identifying parameters
     * @return The retrieved entity
     * @throws SQLException If a SQL Exception occures
     * @see ParamList
     */
    protected Object getEntityByIdFilterList(DBContext dbc, String queryID, ParamList params) throws SQLException {
        ExtPreparedStatement eps = getDbMapping().getQuery(queryID).prepare(dbc);
        ;
        try {
            List pList = params.getParams();
            for (int i = 0; i < pList.size(); i++) {
                Object[] paramNameValue = (Object[]) pList.get(i);
                eps.setAttribute((String) paramNameValue[0], paramNameValue[1]);
            }
            return getEntityByPreparedStatement(dbc, eps.getPreparedStatement());
        } finally {
            eps.close();
        }
    }

    /**
     * Helper method for retrieving a single Entity by its select
     * TO DO: MODIFY AS PER getEntityByPreparedStatement to avoid returning duplicate entities
     */
    protected Object getEntityBySelect(DBContext dbc, Select select) throws SQLException {
        ResultSet rs = select.getResultSet(dbc);
        try {
            if (!rs.next()) {
                return null;
            }
            AttributeSourceRS as = new AttributeSourceRS(rs, getDbMapping());
            return entityFactory.getEntity(as, null);
        } finally {
            DB.close(rs);
        }
    }

    /**
     * Helper method for retrieving a single Entity by its select in a PreparedStatement
     * ATTENTION: The prepared statement will not be closed by this method.
     */
    protected Object getEntityByPreparedStatement(DBContext dbc, PreparedStatement ps) throws SQLException {
        ResultSet rs = ps.executeQuery();
        try {
            LookupContext lc = new LookupContextImpl();
            Object entity = null;
            AttributeSourceRS as = new AttributeSourceRS(rs, getDbMapping());
            while (rs.next()) {
                if (entity == null) {
                    if (logger.isTraceEnabled()) {
                        logAs(as);
                    }
                    entity = entityFactory.getEntity(as, lc);
                } else {
                    if (logger.isTraceEnabled()) {
                        logAs(as);
                    }
                    entityFactory.getEntity(as, lc);
                }
            }
            return entity;
        } finally {
            DB.close(rs);
        }
    }

    private void logAs(AttributeSourceRS as) {
        List names = as.getAttributeNames();
        StringBuffer sb = new StringBuffer("attribute source: {");
        for (Iterator iter = names.iterator(); iter.hasNext(); ) {
            String name = (String) iter.next();
            sb.append(name + " => '" + as.getAttribute(name) + "'");
            if (iter.hasNext()) {
                sb.append(", ");
            }
        }
        sb.append("}");
        logger.trace(sb.toString());
    }

    public List getAll(DBContext dbc) throws SQLException {
        return getEntityList(dbc, getDbMapping().STMT_SELECT_ALL);
    }

    public List getAll(DBContext dbc, ListFilter listFilterParams) throws SQLException {
        return getEntityList(dbc, getDbMapping().STMT_SELECT_ALL, listFilterParams);
    }

    /**
     * Helper method for retrieving a list of Entities by a statement identified by the supplied id
     */
    protected List getEntityList(DBContext dbc, String queryID) throws SQLException {
        ExtPreparedStatement eps = null;
        try {
            eps = getDbMapping().getQuery(queryID).prepare(dbc);
            return getEntityList(dbc, eps.getPreparedStatement());
        } finally {
            if (eps != null) {
                eps.close();
            }
        }
    }

    /**
     * Helper method for retrieving a list of Entities by a statement identified by the supplied id
     */
    protected List getEntityList(DBContext dbc, String queryID, String idPropertyName, Object idValue) throws SQLException {
        ExtPreparedStatement eps = null;
        try {
            eps = getDbMapping().getQuery(queryID).prepare(dbc);
            eps.setAttribute(idPropertyName, idValue);
            return getEntityList(dbc, eps.getPreparedStatement());
        } finally {
            if (eps != null) {
                eps.close();
            }
        }
    }

    /**
     * Helper method for retrieving a list of Entities by a statement identified by the supplied parameters in the paramList
     */
    protected List getEntityList(DBContext dbc, String queryID, ParamList params) throws SQLException {
        ExtPreparedStatement eps = null;
        try {
            eps = getDbMapping().getQuery(queryID).prepare(dbc);
            List pList = params.getParams();
            for (int i = 0; i < pList.size(); i++) {
                Object[] paramNameValue = (Object[]) pList.get(i);
                eps.setAttribute((String) paramNameValue[0], paramNameValue[1]);
            }
            return getEntityList(dbc, eps.getPreparedStatement());
        } finally {
            if (eps != null) {
                eps.close();
            }
        }
    }

    protected List getEntityList(DBContext dbc, String queryID, ListFilter listFilterParams, ParamList params)
      throws SQLException {
        return getEntityList(dbc, (Select) getDbMapping().getQuery(queryID).clone(), listFilterParams, params);
    }

    protected List getEntityList(DBContext dbc, String queryID, ListFilter listFilterParams) throws SQLException {
        return getEntityList(dbc, (Select) getDbMapping().getQuery(queryID).clone(), listFilterParams, null);
    }

    /**
     * Helper method for retrieving a list of Entities by a applying the supplied filter parameters.
     * This method normally calls getEntityList_LimitImplementation, but may be overidden to call
     * getEntityList_CursorImplementation
     *
     * Attention: As a side affect of this method, the select will be modified. Use Select.clone() to supply a copy of the
     * object, if needed.
     */
    protected List getEntityList(DBContext dbc, Select select, ListFilter listFilterParams, ParamList params)
      throws SQLException {
        return getEntityList_LimitImplementation(dbc, select, listFilterParams, params);
    }

    protected List getEntityList(DBContext dbc, Select select, ListFilter listFilterParams) throws SQLException {
        return getEntityList_LimitImplementation(dbc, select, listFilterParams, null);
    }

    /**
     * Helper method for retrieving a list of Entities by a applying the supplied filter parameters.
     * If the ListFilter is configured to use use the limit, the select will be performed to times (with and without the limit)
     * and the count property in the ListFilter will be set the the total count of the result.
     * Attention: As a side affect of this method, the select will be modified. Use Select.clone() to supply a copy of the
     * object, if needed.
     */
    protected List getEntityList_LimitImplementation(DBContext dbc, Select select, ListFilter listFilterParams, ParamList params)
      throws SQLException {

        if (select.getUniqueColumn() == null && (select.getLimit() != null || listFilterParams.useLimit())) {
            select.setUniqueColumn(getDbMapping().getPkField().getColumnName());
        }

        List values = new ArrayList();
        Clause where = getWhereRPNList(dbc, listFilterParams.getFilterList(), values);

        if (listFilterParams.useLimit()) {

            // calculate count
            Select countSelect = (Select) select.clone();
            countSelect.clearColumnSelection();
            // reset the expression in the distinct on, the distinct is still active
            countSelect.setDistinctOn(null);
            countSelect.Limit(null);
            countSelect.orderBy(null);
            countSelect.select("COUNT(*)");
            if (where != null) {
                countSelect.whereAnd(where);
            }

            ExtPreparedStatement epsCount = null;
            try {
                epsCount = countSelect.prepare(dbc);
                for (Iterator iter = values.iterator(); iter.hasNext(); ) {
                    epsCount.setAttribute((String) iter.next(), iter.next());
                }

                if (params != null) {
                    List pList = params.getParams();
                    for (int i = 0; i < pList.size(); i++) {
                        Object[] paramNameValue = (Object[]) pList.get(i);
                        epsCount.setAttribute((String) paramNameValue[0], paramNameValue[1]);
                    }
                }

                ResultSet rs = epsCount.getPreparedStatement().executeQuery();
                if (rs.next()) {
                    listFilterParams.setCount(rs.getInt(1));
                } else {
                    listFilterParams.setCount(0);
                }
            } finally {
                if (epsCount != null) {
                    epsCount.close();
                }
            }
            //check if limit+start > count
            // ->then set limit to lower value
            int count = listFilterParams.getCount();
            int start = listFilterParams.getStart();
            int limit = listFilterParams.getLimit();
            if (count < (start + limit)) {
                if (count < start) {
                    limit = 0;
                } else {
                    int diff = (start + limit) - count;
                    limit = limit - diff;
                }
            }
            // set limit
            select.Limit(new Limit(limit, start));
        }

        if (where != null) {
            select.whereAnd(where);
        }

        if (listFilterParams.getSortField() != null) {
            String orderByColumn = getDbMapping().getColumnNameByProperty(listFilterParams.getSortField()) != null ?
              getDbMapping().getColumnNameByProperty(listFilterParams.getSortField()) : listFilterParams.getSortField();
            if (listFilterParams.getSortDirection() == ListFilter.DIRECTION_ASC) {
                select.orderBy(Order.asc(orderByColumn));
            } else {
                select.orderBy(Order.desc(orderByColumn));
            }
        }

        select.addOrderBy(Order.asc(getDbMapping().getPkField().getColumnName()));

        ExtPreparedStatement eps = null;
        try {
            eps = select.prepare(dbc);
            for (Iterator iter = values.iterator(); iter.hasNext(); ) {
                eps.setAttribute((String) iter.next(), iter.next());
            }

            if (params != null) {
                List pList = params.getParams();
                for (int i = 0; i < pList.size(); i++) {
                    Object[] paramNameValue = (Object[]) pList.get(i);
                    eps.setAttribute((String) paramNameValue[0], paramNameValue[1]);
                }
            }

            return getEntityList(dbc, eps.getPreparedStatement());
        } finally {
            if (eps != null) {
                eps.close();
            }
        }
    }

    /**
     * This method does the same as getEntityList_LimitImplementation,
     * but does not use the Limt,offset features of the database.
     * Here this is achieved by a scrollable result set, which has the effect,
     * that no additional count() select is needed.
     */
    protected List getEntityList_CursorImplementation(DBContext dbc, Select select, ListFilter listFilterParams)
      throws SQLException {

        if (select.getUniqueColumn() == null && (select.getLimit() != null || listFilterParams.useLimit())) {
            select.setUniqueColumn(getDbMapping().getPkField().getColumnName());
        }

        List values = new ArrayList();
        Clause where = getWhereRPNList(dbc, listFilterParams.getFilterList(), values);

        if (where != null) {
            select.whereAnd(where);
        }

        if (listFilterParams.getSortField() != null) {

            String orderByColumn = getDbMapping().getColumnNameByProperty(listFilterParams.getSortField()) != null ?
              getDbMapping().getColumnNameByProperty(listFilterParams.getSortField()) : listFilterParams.getSortField();

            if (listFilterParams.getSortDirection() == ListFilter.DIRECTION_ASC) {
                select.orderBy(Order.asc(orderByColumn));
            } else {
                select.orderBy(Order.desc(orderByColumn));
            }
        }

        select.addOrderBy(Order.asc(getDbMapping().getPkField().getColumnName()));

        ExtPreparedStatement eps = null;
        ResultSet rs = null;
        try {
            eps = new ExtPreparedStatement(select);
            eps.prepareScrollable(dbc);

            for (Iterator iter = values.iterator(); iter.hasNext(); ) {
                eps.setAttribute((String) iter.next(), iter.next());
            }

            rs = eps.getPreparedStatement().executeQuery();

            LookupContext lc = new LookupContextImpl();
            List entityList = new ArrayList(30);

            // the AttributeSourceRS will automaticly scroll with the ReslutSet
            AttributeSourceRS as = new AttributeSourceRS(rs, getDbMapping());
            int counter = 0;
            int limit = Integer.MAX_VALUE;

            if (listFilterParams.useLimit()) {
                rs.absolute(listFilterParams.getStart());
                limit = listFilterParams.getLimit();
            }
            while (rs.next() && counter++ < limit) {
                Object entity = entityFactory.getEntity(as, lc);
                if (!entityList.contains(entity)) {
                    entityList.add(entity);
                }
            }

            // find the total count of entries
            rs.last();
            listFilterParams.setCount(rs.getRow());

            return entityList;
        } finally {
            DB.close(rs);
            if (eps != null) {
                eps.close();
            }
        }
    }

    /**
     * Returns a List of the Primary Keys from the select.
     * Attention: As a side affect of this method, the select will be modified. Use Select.clone() to supply a copy of the
     * object, if needed.
     */
    protected PrimaryKeyList getPrimaryKeyList(DBContext dbc, Select select, ListFilter listFilterParams) throws SQLException {
        List values = new ArrayList();
        Clause where = getWhereRPNList(dbc, listFilterParams.getFilterList(), values);

        if (where != null) {
            select.whereAnd(where);
        }

        Field field = getDbMapping().getPkField();
        select.clearColumnSelection();
        select.selectAs(field.getColumnName(), field.getPropertyName());

        if (listFilterParams.getSortField() != null) {

            String orderByColumn = getDbMapping().getColumnNameByProperty(listFilterParams.getSortField()) != null ?
              getDbMapping().getColumnNameByProperty(listFilterParams.getSortField()) : listFilterParams.getSortField();

            if (listFilterParams.getSortDirection() == ListFilter.DIRECTION_ASC) {
                select.orderBy(Order.asc(orderByColumn));
            } else {
                select.orderBy(Order.desc(orderByColumn));
            }
        }

        ExtPreparedStatement eps = null;
        ResultSet rs = null;
        try {
            eps = select.prepare(dbc);
            for (Iterator iter = values.iterator(); iter.hasNext(); ) {
                eps.setAttribute((String) iter.next(), iter.next());
            }

            PrimaryKeyList primaryKeys = new PrimaryKeyList();
            rs = eps.getPreparedStatement().executeQuery();
            while (rs.next()) {
                primaryKeys.add(rs.getObject(1));
            }
            return primaryKeys;
        } finally {
            DB.close(rs);
            if (eps != null) {
                eps.close();
            }
        }
    }

    /**
     * Creates a Where Tree based on a list in Reverse Polish Notation (Umgekehrte Polnische Notation UPN).
     * The first (left) operand of the list is allways the String property name, the right operand may be of any type.
     * The operators are from the constants of {@link de.tarent.commons.datahandling.ListFilter}.
     *
     * <p>All column names may be in the terminology of the business object, and will be translates to database column names,
     * using the assotiated DBMapping.
     * <p>The values of the expressions will not be set, but defined as ParamValue Objects, using the property names as keys.
     *
     * @param outValues Output parameters filled with the values key-value pairs (String-Object) for filling the ParamValues.
     */
    public Clause getWhereRPNList(DBContext dbc, List rpnList, List outValues) throws SQLException {
        if (rpnList.size() == 0) {
            return null;
        }
        int paramCounter = 0;
        try {
            LinkedList stack = new LinkedList();
            boolean valueExpected = false;
            String lastColumnName = null;

            // we iterate over all tokens
            for (Iterator iter = rpnList.iterator(); iter.hasNext(); ) {
                Object tokenObject = iter.next();

                // the token is an Operator
                // get the consuming args from the stack,
                // concatenate them and push it back to the stack
                if (tokenObject instanceof ListFilterOperator) {
                    valueExpected = false;
                    ListFilterOperator op = (ListFilterOperator) tokenObject;

                    if (!op.isConnectionOperator()) {
                        String columnName = null;
                        ParamValue value = null;
                        if (2 == op.getConsumingArsg()) {
                            value = (ParamValue) stack.removeLast();
                            columnName = (String) stack.removeLast();
                        } else {
                            columnName = (String) stack.removeLast();
                        }
                        if (ListFilterOperator.IN.equals(op)) {
                            // remove the variables for the prepared statement
                            Collection collectionValue = (Collection) outValues.remove(outValues.size() - 1);
                            outValues.remove(outValues.size() - 1);
                            stack.add(Expr.optimizedIn(dbc, columnName, collectionValue));
                        } else {
                            stack.add(new Where(columnName, value, " " + op.toString() + " "));
                        }
                    } else {
                        Clause left = null;
                        Clause right = null;
                        if (2 == op.getConsumingArsg()) {
                            right = (Clause) stack.removeLast();
                            left = (Clause) stack.removeLast();
                        } else {
                            right = (Clause) stack.removeLast();
                        }
                        stack.add(new Where(left, right, " " + op.toString() + " "));
                    }

                    //operand
                } else {
                    if (valueExpected) {
                        // value
                        // make the preparedStatementKey unique, but readeable
                        String preparedStatementKey = lastColumnName + "-" + (paramCounter++);
                        outValues.add(preparedStatementKey);
                        outValues.add(tokenObject);
                        stack.add(new ParamValue(preparedStatementKey));
                        valueExpected = false;
                    } else {
                        // column
                        String dbColumnName = getDbMapping().getColumnNameByProperty(tokenObject.toString());
                        if (dbColumnName == null) {
                            throw new RuntimeException("no db column for property '" + tokenObject + "'");
                        }
                        stack.add(dbColumnName);
                        valueExpected = true;
                        lastColumnName = tokenObject.toString();
                    }
                }
            }
            return (Clause) stack.removeLast();
        } catch (Exception e) {
            SQLException se = new SQLException("Error while creating where tree from RPN list: " + rpnList);
            se.initCause(e);
            throw se;
        }
    }

    /**
     * Helper method for retrieving a list of Entities by a PreparedStatement
     * ATTENTION: The prepared statement will not be closed by this method.
     * TO DO: MODIFY to avoid returning duplicate entities
     */
    protected List getEntityList(DBContext dbc, PreparedStatement ps) throws SQLException {
        ResultSet rs = ps.executeQuery();
        //tm.step("getEntityList-execute select");
        TimeMeasureTool tm = TimeMeasureTool.getMeasureTool(logger);

        try {
            LookupContext lc = new LookupContextImpl();
            List entityList = new ArrayList(30);
            // the AttributeSourceRS will automaticly scroll with the ReslutSet
            AttributeSourceRS as = new AttributeSourceRS(rs, getDbMapping());
            while (rs.next()) {
                Object entity = entityFactory.getEntity(as, lc);
                if (!entityList.contains(entity)) {
                    entityList.add(entity);
                }
                tm.step("getEntityList-filling one");
            }
            tm.total("getEntityList-total");
            return entityList;
        } finally {
            DB.close(rs);
        }
    }

    /**
     * Insert a single entity.
     */
    public void insert(DBContext dbc, Object entity) throws SQLException {
        ExtPreparedStatement eps = getDbMapping().getInsert().prepare(dbc);
        entityFactory.writeTo(eps, entity);
        PreparedStatement ps = eps.getPreparedStatement();
        try {
            ps.executeUpdate();
            setEntityKeys(eps.returnGeneratedKeys(dbc), entity);
        } finally {
            DB.close(ps);
        }
    }

    /**
     * Inserts a single entity and all its child entities
     * Note: does only work with annotated beans!
     *
     * @param dbc    the database context
     * @param entity the entity to insert
     * @throws SQLException thrown if there are problems with the database
     */
    public void insertDeep(DBContext dbc, Object entity) throws SQLException {
        boolean inTransaction = !dbc.getDefaultConnection().getAutoCommit();
        if (!inTransaction) {
            dbc.getDefaultConnection().setAutoCommit(false);
        }

        try {
            // first insert entity itself
            insert(dbc, entity);

            // We can only insert deep if the bean is annotated. Otherwise
            // we just insert as insert() would have been used.
            if (entity.getClass().getAnnotation(de.tarent.dblayer.persistence.annotations.Entity.class) != null) {
                int pkEntity = 0;
                // get primary key of entity
                Method[] entityMethods = entity.getClass().getMethods();
                for (int i = 0; i < entityMethods.length; i++) {
                    if (entityMethods[i].getAnnotation(Id.class) != null) {
                        pkEntity = (Integer) Pojo.get(entity, entityMethods[i]);
                    }
                }

                // now iterate over attributes and insert each
                // attribute that has annotation @Reference
                // (Note: special treatments for collections)
                Method[] methods = entity.getClass().getMethods();
                for (int i = 0; i < methods.length; i++) {
                    if (methods[i].getAnnotation(Reference.class) != null) {
                        // there is a reference. get the referenced object and
                        // call its insertDeep method in its DAO
                        Object referencedEntity = Pojo.get(entity, methods[i]);
                        Class referencedBean = methods[i].getAnnotation(Reference.class).bean();

                        if (referencedEntity instanceof Collection) {
                            // Referenced entity is a collection of beans. Iterate
                            // over collection and call insertDeep for each one
                            Iterator iter = ((Collection) referencedEntity).iterator();
                            while (iter.hasNext()) {
                                Object refEntity = iter.next();

                                // insert primary key of entity in referenced entity
                                // To do that we have to find the foreign key in the referenced entity
                                Method[] refMethods = refEntity.getClass().getMethods();
                                for (int j = 0; j < refMethods.length; j++) {
                                    if (refMethods[j].getAnnotation(ForeignKey.class) != null) {
                                        // found foreign key attribute; set primary key of entity
                                        Pojo.set(refEntity, refMethods[j].getName().substring(3), pkEntity);
                                    }
                                }

                                DAORegistry.getDAOForBean(referencedBean).insertDeep(dbc, refEntity);
                            }
                        } else if (referencedEntity != null) {
                            // referencend entity is a single bean

                            // insert primary key of entity in referenced entity
                            // To do that we have to find the foreign key in the referenced entity
                            Method[] refMethods = referencedEntity.getClass().getMethods();
                            for (int j = 0; j < refMethods.length; j++) {
                                if (refMethods[j].getAnnotation(ForeignKey.class) != null) {
                                    // found foreign key attribute; set primary key of entity
                                    Pojo.set(referencedEntity, refMethods[j].getName().substring(3), pkEntity);
                                }
                            }

                            DAORegistry.getDAOForBean(referencedBean).insertDeep(dbc, referencedEntity);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            dbc.getDefaultConnection().rollback();
            throw e;
        } finally {
            if (!inTransaction) {
                dbc.getDefaultConnection().commit();
                dbc.getDefaultConnection().setAutoCommit(true);
            }
        }
    }

    /**
     * Update a single entity.
     */
    public void update(DBContext dbc, Object entity) throws SQLException, EntityOperationException {
        ExtPreparedStatement eps = getDbMapping().getUpdate().prepare(dbc);
        entityFactory.writeTo(eps, entity);
        PreparedStatement ps = eps.getPreparedStatement();
        try {
            int updatedRows = ps.executeUpdate();
            if (updatedRows == 0) {
                throw new EntityOperationException("entity does not exist (" + entity.getClass().getName() + ")");
            }
            if (updatedRows > 1) {
                throw new EntityOperationException("several entities with this primary key exist");
            }
        } finally {
            DB.close(ps);
        }
    }

    /**
     * Updates a single entity and all its child entities.
     * If the primary key of a child entity is not set tarent-database
     * assumes that this child is new and inserts in into the db
     * (instead of updating it)
     * Note: does only work with annotated beans!
     *
     * @param dbc    the database context
     * @param entity the entity to update
     * @throws SQLException thrown if there are problems with the database
     */
    public void updateDeep(DBContext dbc, Object entity) throws SQLException, EntityOperationException {
        boolean inTransaction = !dbc.getDefaultConnection().getAutoCommit();
        if (!inTransaction) {
            dbc.getDefaultConnection().setAutoCommit(false);
        }

        try {
            // first update entity itself
            update(dbc, entity);

            // We can only update deep if the bean is annotated. Otherwise
            // we just update as update() would have been used.
            if (entity.getClass().getAnnotation(de.tarent.dblayer.persistence.annotations.Entity.class) != null) {
                int pkEntity = 0;
                // get primary key of entity
                Method[] entityMethods = entity.getClass().getMethods();
                for (int i = 0; i < entityMethods.length; i++) {
                    if (entityMethods[i].getAnnotation(Id.class) != null) {
                        pkEntity = (Integer) Pojo.get(entity, entityMethods[i]);
                    }
                }

                // now iterate over attributes and insert each
                // attribute that has annotation @Reference
                // (Note: special treatments for collections)
                Method[] methods = entity.getClass().getMethods();
                for (int i = 0; i < methods.length; i++) {
                    if (methods[i].getAnnotation(Reference.class) != null) {
                        // there is a reference. get the referenced object and
                        // call its updateDeep method in its DAO
                        Object referencedEntity = Pojo.get(entity, methods[i]);
                        Class referencedBean = methods[i].getAnnotation(Reference.class).bean();

                        if (referencedEntity instanceof Collection) {
                            // Referenced entity is a collection of beans. Iterate
                            // over collection and call updateDeep for each one
                            Iterator iter = ((Collection) referencedEntity).iterator();
                            while (iter.hasNext()) {
                                Object refEntity = iter.next();
                                boolean insert = false;

                                // insert primary key of entity in referenced entity
                                // To do that we have to find the foreign key in the referenced entity
                                Method[] refMethods = refEntity.getClass().getMethods();
                                for (int j = 0; j < refMethods.length; j++) {
                                    if (refMethods[j].getAnnotation(ForeignKey.class) != null) {
                                        // found foreign key attribute; set primary key of entity
                                        Pojo.set(refEntity, refMethods[j].getName().substring(3), pkEntity);
                                    }

                                    // check if primary key is set. if so update else insert
                                    if (refMethods[j].getAnnotation(Id.class) != null) {
                                        if (((Integer) Pojo.get(refEntity, refMethods[j])).intValue() == 0) {
                                            // the primary key of the referenced entity is 0. So it is new and has
                                            // to be inserted instead of updated.
                                            insert = true;
                                        }
                                    }
                                }

                                if (insert) {
                                    DAORegistry.getDAOForBean(referencedBean).insertDeep(dbc, refEntity);
                                } else {
                                    DAORegistry.getDAOForBean(referencedBean).updateDeep(dbc, refEntity);
                                }
                            }
                        } else if (referencedEntity != null) {
                            // referencend entity is a single bean
                            boolean insert = false;

                            // insert primary key of entity in referenced entity
                            // To do that we have to find the foreign key in the referenced entity
                            Method[] refMethods = referencedEntity.getClass().getMethods();
                            for (int j = 0; j < refMethods.length; j++) {
                                if (refMethods[j].getAnnotation(ForeignKey.class) != null) {
                                    // found foreign key attribute; set primary key of entity
                                    Pojo.set(referencedEntity, refMethods[j].getName().substring(3), pkEntity);
                                }

                                // check if primary key is set. if so update else insert
                                if (refMethods[j].getAnnotation(Id.class) != null) {
                                    if (((Integer) Pojo.get(referencedEntity, refMethods[j])).intValue() == 0) {
                                        // the primary key of the referenced entity is 0. So it is new and has
                                        // to be inserted instead of updated.
                                        insert = true;
                                    }
                                }
                            }

                            if (insert) {
                                DAORegistry.getDAOForBean(referencedBean).insertDeep(dbc, referencedEntity);
                            } else {
                                DAORegistry.getDAOForBean(referencedBean).updateDeep(dbc, referencedEntity);
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            dbc.getDefaultConnection().rollback();
            throw e;
        } finally {
            if (!inTransaction) {
                dbc.getDefaultConnection().commit();
                dbc.getDefaultConnection().setAutoCommit(true);
            }
        }
    }

    /**
     * Delete a single entity.
     *
     * @throws EntityOperationException
     */
    public void delete(DBContext dbc, Object entity) throws SQLException, EntityOperationException {
        ExtPreparedStatement eps = getDbMapping().getDelete().prepare(dbc);
        entityFactory.writeTo(eps, entity);
        PreparedStatement ps = eps.getPreparedStatement();
        try {
            int updatedRows = ps.executeUpdate();
            if (updatedRows == 0) {
                throw new EntityOperationException("entity does not exist (" + entity.getClass().getName() + ")");
            }
            if (updatedRows > 1) {
                throw new EntityOperationException("several entities with this primary key exist");
            }
        } finally {
            DB.close(ps);
        }
    }

    /**
     * deletes a single entity and all its child entities
     * Note: does only work with annotated beans!
     *
     * @param dbc    the database context
     * @param entity the entity to delete
     * @throws SQLException             thrown if there are problems with the database
     * @throws EntityOperationException
     */
    public void deleteDeep(DBContext dbc, Object entity) throws SQLException, EntityOperationException {
        boolean inTransaction = !dbc.getDefaultConnection().getAutoCommit();
        if (!inTransaction) {
            dbc.getDefaultConnection().setAutoCommit(false);
        }

        try {
            // first delete child entities, then parent entity (because of foreign key constraints)

            // We can only delete deep if the bean is annotated. Otherwise
            // we just delete as delete() would have been used.
            if (entity.getClass().getAnnotation(de.tarent.dblayer.persistence.annotations.Entity.class) != null) {
                int pkEntity = 0;
                // get primary key of entity
                Method[] entityMethods = entity.getClass().getMethods();
                for (int i = 0; i < entityMethods.length; i++) {
                    if (entityMethods[i].getAnnotation(Id.class) != null) {
                        pkEntity = (Integer) Pojo.get(entity, entityMethods[i]);
                    }
                }

                // now iterate over attributes and delete each
                // attribute that has annotation @Reference
                // (Note: special treatments for collections)
                Method[] methods = entity.getClass().getMethods();
                for (int i = 0; i < methods.length; i++) {
                    if (methods[i].getAnnotation(Reference.class) != null) {
                        // there is a reference. get the referenced object and
                        // call its deleteDeep method in its DAO
                        Object referencedEntity = Pojo.get(entity, methods[i]);
                        Class referencedBean = methods[i].getAnnotation(Reference.class).bean();

                        if (referencedEntity instanceof Collection) {
                            // Referenced entity is a collection of beans. Iterate
                            // over collection and call insertDeep for each one
                            Iterator iter = ((Collection) referencedEntity).iterator();
                            while (iter.hasNext()) {
                                Object refEntity = iter.next();

                                // insert primary key of entity in referenced entity
                                // To do that we have to find the foreign key in the referenced entity
                                Method[] refMethods = refEntity.getClass().getMethods();
                                for (int j = 0; j < refMethods.length; j++) {
                                    if (refMethods[j].getAnnotation(ForeignKey.class) != null) {
                                        // found foreign key attribute; set primary key of entity
                                        Pojo.set(refEntity, refMethods[j].getName().substring(3), pkEntity);
                                    }
                                }

                                DAORegistry.getDAOForBean(referencedBean).deleteDeep(dbc, refEntity);
                            }
                        } else if (referencedEntity != null) {
                            // referencend entity is a single bean

                            // insert primary key of entity in referenced entity
                            // To do that we have to find the foreign key in the referenced entity
                            Method[] refMethods = referencedEntity.getClass().getMethods();
                            for (int j = 0; j < refMethods.length; j++) {
                                if (refMethods[j].getAnnotation(ForeignKey.class) != null) {
                                    // found foreign key attribute; set primary key of entity
                                    Pojo.set(referencedEntity, refMethods[j].getName().substring(3), pkEntity);
                                }
                            }

                            DAORegistry.getDAOForBean(referencedBean).deleteDeep(dbc, referencedEntity);
                        }
                    }
                }
            }

            delete(dbc, entity);
        } catch (SQLException e) {
            dbc.getDefaultConnection().rollback();
            throw e;
        } finally {
            if (!inTransaction) {
                dbc.getDefaultConnection().commit();
                dbc.getDefaultConnection().setAutoCommit(true);
            }
        }
    }

    /**
     * Delete a single entity only identified by its primary key
     *
     * @param dbc        the database context
     * @param primaryKey the primary key of the entity to delete
     * @throws SQLException thrown when there are problems with the database
     */
    public void delete(DBContext dbc, int primaryKey) throws SQLException {
        ExtPreparedStatement eps = getDbMapping().getDelete().prepare(dbc);

        Field pkField = getDbMapping().getPkField();
        eps.setAttribute(pkField.getPropertyName(), new Integer(primaryKey));

        PreparedStatement ps = eps.getPreparedStatement();
        try {
            ps.executeUpdate();
        } finally {
            DB.close(ps);
        }
    }

    public EntityFactory getEntityFactory() {
        return entityFactory;
    }

    public void setEntityFactory(EntityFactory newEntityFactory) {
        this.entityFactory = newEntityFactory;
        if (this.bean != null) {
            EntityFactoryRegistry.registerEntityFactory(this.bean, newEntityFactory);
        }
    }

    public DBMapping getDbMapping() {
        return dbMapping;
    }

    public void setDbMapping(DBMapping newDbMapping) {
        this.dbMapping = newDbMapping;
    }
}
