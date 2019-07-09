package de.tarent.aa.veraweb.worker;
import de.tarent.aa.veraweb.beans.OptionalField;
import de.tarent.aa.veraweb.utils.OptionalFieldTypeFacade;
import de.tarent.dblayer.engine.DB;
import de.tarent.dblayer.sql.SQL;
import de.tarent.dblayer.sql.clause.Clause;
import de.tarent.dblayer.sql.clause.Expr;
import de.tarent.dblayer.sql.clause.Order;
import de.tarent.dblayer.sql.clause.Where;
import de.tarent.dblayer.sql.clause.WhereList;
import de.tarent.dblayer.sql.statement.Delete;
import de.tarent.dblayer.sql.statement.Insert;
import de.tarent.dblayer.sql.statement.Select;
import de.tarent.dblayer.sql.statement.Update;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.beans.Database;
import de.tarent.octopus.beans.TransactionContext;
import de.tarent.octopus.beans.veraweb.DatabaseVeraWeb;
import de.tarent.octopus.server.OctopusContext;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class handles the optional fields labels.
 *
 * @author Max Marche, tarent solutions GmbH
 * @author Atanas Alexandrov, tarent solutions GmbH
 */
public class OptionalFieldsWorker {
    private static final String DELEGATON_FIELD_TABLE_NAME = "veraweb.toptional_fields";
    private static final String DELEGATON_FIELD_VALUE_TABLE_NAME = "veraweb.toptional_fields_delegation_content";
    private static final String OPTIONAL_FIELD_TYPE_CONTENT = "veraweb.toptional_field_type_content";

    private Database database;

    /**
     * Constructor.
     *
     * @param cntx The {@link OctopusContext}
     */
    public OptionalFieldsWorker(OctopusContext cntx) {
        this.database = new DatabaseVeraWeb(cntx);
    }

    /**
     * Create ot update optional field.
     *
     * @param optionalField The {@link OptionalField}
     * @throws SQLException  TODO
     * @throws BeanException TODO
     */
    public void createOrUpdateOptionalField(OptionalField optionalField) throws SQLException, BeanException {
        if (this.checkOptionFieldExists(optionalField)) {
            this.updateOptionalField(optionalField);
        } else {
            this.createOptionalField(optionalField);
        }
    }

    /**
     * Create optional field.
     *
     * @param optionalField The {@link OptionalField}
     * @throws SQLException  TODO
     * @throws BeanException TODO
     */
    public void createOptionalField(OptionalField optionalField) throws SQLException, BeanException {
        final TransactionContext transactionContext = this.database.getTransactionContext();
        final Insert insert = getStatementInsertOptionalField(optionalField);

        DB.insert(transactionContext, insert.statementToString());
        transactionContext.commit();
    }

    /**
     * Update optional field.
     *
     * @param optionalField The {@link OptionalField}
     * @throws SQLException  TODO
     * @throws BeanException TODO
     */
    public void updateOptionalField(OptionalField optionalField) throws SQLException, BeanException {
        final TransactionContext context = this.database.getTransactionContext();
        final Update updateStatement = getStatementUpdateOptionalField(optionalField);
        DB.update(context, updateStatement.statementToString());

        // check if field already has type contents

        createOptionalFieldTypeContents(optionalField, context);

        context.commit();
    }

    private void createOptionalFieldTypeContents(OptionalField optionalField, TransactionContext context)
      throws SQLException, BeanException {
        final Boolean selectStatement = getStatementCheckOptionalFieldTypeContentExists(optionalField);

        if ((optionalField.getFkType() == OptionalFieldTypeFacade.simple_combobox.getValue()
          || optionalField.getFkType() == OptionalFieldTypeFacade.multiple_combobox.getValue())
          && !selectStatement) {
            initTypeContents(optionalField, context);
        }
    }

    private void initTypeContents(OptionalField optionalField, TransactionContext context) throws SQLException {
        final Insert insertStatement = getInsertStatementForTypeContents(optionalField.getId());
        final Insert insertStatement1 = getInsertStatementForTypeContents(optionalField.getId());
        final Insert insertStatement2 = getInsertStatementForTypeContents(optionalField.getId());
        final Insert insertStatement3 = getInsertStatementForTypeContents(optionalField.getId());
        DB.insert(context, insertStatement);
        DB.insert(context, insertStatement1);
        DB.insert(context, insertStatement2);
        DB.insert(context, insertStatement3);
    }

    /**
     * Get all optional fields by event id.
     *
     * @param eventId Event id
     * @return List with labels for the optional fields.
     * @throws SQLException  TODO
     * @throws BeanException TODO
     */
    public List<OptionalField> getOptionalFieldsByEvent(Integer eventId) throws BeanException, SQLException {
        final Select select = getStatementSelectOptionalField(eventId);
        final ResultSet resultSet = database.result(select);

        return getOptionalFieldsAsList(resultSet);
    }

    /**
     * Check if the given {@link OptionalField} exist.
     *
     * @param optionalField The {@link OptionalField}
     * @return True if the field exists, otherwise false.
     * @throws SQLException  TODO
     * @throws BeanException TODO
     */
    public boolean checkOptionFieldExists(OptionalField optionalField) throws BeanException, SQLException {
        final Select select = getStatementCheckOptionalFieldExists(optionalField);
        final ResultSet resultSet = database.result(select);

        return resultSet.next();
    }

    /**
     * Delete optional field.
     *
     * @param optionalField The {@link OptionalField}
     * @throws SQLException  TODO
     * @throws BeanException TODO
     */
    public void removeOptionalField(OptionalField optionalField) throws SQLException, BeanException {
        final TransactionContext context = this.database.getTransactionContext();
        deleteOptionalFieldDependencies(context, optionalField);
        deleteOptionalFieldFromDB(optionalField, context);
    }

    private void deleteOptionalFieldDependencies(TransactionContext context,
      OptionalField optionalField) throws BeanException, SQLException {
        final Delete deleteStatement = getDeleteDependenciesStatement(optionalField);
        deleteStatement.executeDelete(context);
        context.commit();
    }

    private Delete getDeleteDependenciesStatement(OptionalField optionalField) {
        final Delete deleteStatement = SQL.Delete(this.database);
        final WhereList whereCriterias = new WhereList();

        whereCriterias.add(new Where("fk_delegation_field", optionalField.getId(), "="));

        deleteStatement.from(DELEGATON_FIELD_VALUE_TABLE_NAME);
        deleteStatement.where(whereCriterias);
        return deleteStatement;
    }

    private void deleteOptionalFieldFromDB(OptionalField optionalField, TransactionContext context)
      throws SQLException, BeanException {
        final Delete deleteStatement = getDeleteStatement(optionalField);
        deleteStatement.executeDelete(context);
        context.commit();
    }

    private Delete getDeleteStatement(OptionalField optionalField) {
        final Delete deleteStatement = SQL.Delete(this.database);

        final WhereList whereCriterias = getWhereCriterialsForOptionalField(optionalField);
        deleteStatement.from(DELEGATON_FIELD_TABLE_NAME);
        deleteStatement.where(whereCriterias);
        return deleteStatement;
    }

    private WhereList getWhereCriterialsForOptionalField(OptionalField optionalField) {
        final WhereList whereCriterias = new WhereList();
        if (optionalField.getId() == -1) {
            whereCriterias.addAnd(new Where("label", optionalField.getLabel(), "="));
            whereCriterias.addAnd(new Where("fk_event", optionalField.getLabel(), "="));
        } else {
            whereCriterias.addAnd(new Where("pk", optionalField.getId(), "="));
        }
        return whereCriterias;
    }

    private List<OptionalField> getOptionalFieldsAsList(ResultSet resultSet) throws SQLException {
        final List<OptionalField> result = new ArrayList<OptionalField>();
        while (resultSet.next()) {
            final OptionalField delegation = new OptionalField(resultSet);
            result.add(delegation);
        }

        return result;
    }

    private Select getStatementCheckOptionalFieldExists(OptionalField optionalField) {
        final WhereList whereCriterias = getWhereCriterialsForOptionalField(optionalField);
        final Select select = SQL.Select(this.database);
        select.from(DELEGATON_FIELD_TABLE_NAME);
        select.where(whereCriterias);
        select.select("pk");
        return select;
    }

    private boolean getStatementCheckOptionalFieldTypeContentExists(OptionalField optionalField) throws BeanException {
        final WhereList whereCriterias = new WhereList();
        whereCriterias.addAnd(new Where("fk_optional_field", optionalField.getId(), "="));

        final Select select = SQL.Select(this.database);
        select.from(OPTIONAL_FIELD_TYPE_CONTENT);
        select.where(whereCriterias);
        select.select("count(*)");

        return (database.getCount(select) > 0);
    }

    private Select getStatementSelectOptionalField(Integer eventId) {
        final WhereList whereCriterias = new WhereList();
        whereCriterias.addAnd(new Where("fk_event", eventId, "="));

        final Select select = SQL.Select(this.database);
        select.from(DELEGATON_FIELD_TABLE_NAME);
        select.select("pk");
        select.select("label");
        select.select("fk_event");
        select.select("fk_type");
        select.where(whereCriterias);
        select.orderBy(Order.asc("pk"));
        return select;
    }

    private Insert getStatementInsertOptionalField(OptionalField optionalField) {
        final Insert insert = SQL.Insert(this.database);

        insert.table(DELEGATON_FIELD_TABLE_NAME);
        insert.insert("label", optionalField.getLabel());
        insert.insert("fk_event", optionalField.getFkEvent());
        return insert;
    }

    private Update getStatementUpdateOptionalField(OptionalField optionalField) {
        final WhereList whereCriterias = new WhereList();
        whereCriterias.addAnd(new Where("pk", optionalField.getId(), "="));

        final Update updateStatement = SQL.Update(this.database);
        updateStatement.table(DELEGATON_FIELD_TABLE_NAME);
        updateStatement.where(whereCriterias);
        if (optionalField.getFkEvent() != -1) {
            updateStatement.update("fk_event", optionalField.getFkEvent());
        }
        updateStatement.update("label", optionalField.getLabel());
        updateStatement.update("fk_type", optionalField.getFkType());

        return updateStatement;
    }

    private Insert getInsertStatementForTypeContents(final Integer fieldId) {
        final Insert insertStatement = SQL.Insert(this.database);
        insertStatement.table(OPTIONAL_FIELD_TYPE_CONTENT);
        insertStatement.insert("fk_optional_field", fieldId);
        insertStatement.insert("content", "");

        return insertStatement;
    }

    public Boolean isFieldRegisterForTheEvent(final Integer eventId, final Integer fieldId)
      throws IOException, BeanException {
        Clause labelNotNullClause = Expr.notEqual("label", "");

        Integer counter = this.database.getCount(this.database.getCount("OptionalField").
          whereAndEq("fk_event", eventId).
          whereAndEq("pk", fieldId).
          whereAnd(labelNotNullClause)).intValue();
        return counter > 0;
    }

    public List<OptionalField> getTotalEventsFromEvent(final Integer eventId)
      throws IOException, BeanException, SQLException {
        Select selectStatement = SQL.Select(database).from("veraweb.toptional_fields").select("*").
          whereAndEq("fk_event", eventId);

        final ResultSet resultSet = database.result(selectStatement);

        return getOptionalFieldsAsList(resultSet);
    }
}
