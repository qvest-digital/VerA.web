package de.tarent.aa.veraweb.worker;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import de.tarent.aa.veraweb.beans.OptionalField;
import de.tarent.dblayer.engine.DB;
import de.tarent.dblayer.sql.SQL;
import de.tarent.dblayer.sql.SyntaxErrorException;
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


public class OptionalFieldsWorker {
	private static final String DELEGATON_FIELD_TABLE_NAME = "veraweb.toptional_fields";
	private Database database;
	
	public OptionalFieldsWorker(OctopusContext ctx) {
		this.database = new DatabaseVeraWeb(ctx);
	}
	
	/**
	 * Persist or Update the given "OptionalField"-object
	 * @param optionalField
	 * @throws SyntaxErrorException
	 * @throws SQLException
	 * @throws BeanException
	 */
	public void createOrUpdateDelegationField(OptionalField optionalField) throws SyntaxErrorException, SQLException, BeanException {
		if(this.delegationfieldExist(optionalField)) {
			this.updateDelegationField(optionalField);
		} else {
			this.createDelegationField(optionalField);
		}
	}
	
	/**
	 * Persists the given "OptionalField"-object
	 * @param optionalField
	 * @throws SyntaxErrorException
	 * @throws SQLException
	 * @throws BeanException 
	 */
	public void createDelegationField(OptionalField optionalField) throws SyntaxErrorException, SQLException, BeanException {
		TransactionContext context = this.database.getTransactionContext();
		Insert insert = SQL.Insert(this.database);
		
		insert.table(DELEGATON_FIELD_TABLE_NAME);
		insert.insert("label", optionalField.getLabel());
		insert.insert("fk_event", optionalField.getFkEvent());

		DB.insert(context, insert.statementToString());
        context.commit();
	}
	
	/**
	 * Update the "label" and "fk_event"(if is set)-field of an existing "OptionalDelegationField"-object
	 * @param optionalField
	 * @throws SyntaxErrorException
	 * @throws SQLException
	 * @throws BeanException
	 */
	public void updateDelegationField(OptionalField optionalField) throws SyntaxErrorException, SQLException, BeanException {
		TransactionContext context = this.database.getTransactionContext();
		WhereList whereCriterias = new WhereList();
		Update update = SQL.Update(this.database);
		
		whereCriterias.addAnd(new Where("pk", optionalField.getPk(), "="));
		update.table(DELEGATON_FIELD_TABLE_NAME);
		update.where(whereCriterias);
		if(optionalField.getFkEvent() != -1) {
			update.update("fk_event", optionalField.getFkEvent());
		}
		update.update("label", optionalField.getLabel());

		DB.update(context, update.statementToString());
        context.commit();
	}

	/**
	 * Returns an "ArrayList<OptionalField>" with all delegationFields who have the given fk_event.
     *
	 * @param fk_event Event id
     *
	 * @return
	 * @throws BeanException
	 * @throws SQLException
	 */
	public ArrayList<OptionalField> getDelegationFieldsByEvent(int fk_event) throws BeanException, SQLException {
		ArrayList<OptionalField> result = new ArrayList<OptionalField>();
		WhereList whereCriterias = new WhereList();
		Select select = SQL.Select(this.database);
		
		whereCriterias.addAnd(new Where("fk_guest", fk_event, "="));
		select.from(DELEGATON_FIELD_TABLE_NAME);
		select.select("pk");
		select.select("label");
		select.select("fk_event");
		
		ResultSet resultSet = database.result(select);
		
        while(resultSet.next()) {
        	OptionalField delegation = new OptionalField(resultSet);
        	result.add(delegation);
        }
	
		return result;
	}

	/**
	 * Returns the "OptionalField" with the given pk
	 * @param pk
	 * @return
	 * @throws BeanException
	 * @throws SQLException
	 */
	public OptionalField getDelegationFieldByPk(int pk) throws BeanException, SQLException {
		WhereList whereCriterias = new WhereList();
		Select select = SQL.Select(this.database);
		
		whereCriterias.addAnd(new Where("pk", pk, "="));
		select.from(DELEGATON_FIELD_TABLE_NAME);
		select.select("pk");
		select.select("label");
		select.select("fk_event");
		
		ResultSet resultSet = database.result(select);

		if(resultSet.next()) {
        	return new OptionalField(resultSet);
		}
		
		return null;
	}
	
	/**
	 * Check if the given "OptionalField" exist
	 * @param optionalField
	 * @return
	 * @throws BeanException
	 * @throws SQLException
	 */
	public boolean delegationfieldExist(OptionalField optionalField) throws BeanException, SQLException {
		WhereList whereCriterias = new WhereList();
		Select select = SQL.Select(this.database);
		
		if(optionalField.getPk() == -1) {
			whereCriterias.addAnd(new Where("label", optionalField.getLabel(), "="));
			whereCriterias.addAnd(new Where("fk_event", optionalField.getLabel(), "="));
		} else {
			whereCriterias.addAnd(new Where("pk", optionalField.getPk(), "="));
		}
		select.from(DELEGATON_FIELD_TABLE_NAME);
		select.where(whereCriterias);
		select.select("pk");
		
		ResultSet resultSet = database.result(select);
	
		return resultSet.next();
	}

	public void removeDelegationField(OptionalField optionalField) throws SQLException, BeanException {
		TransactionContext context = this.database.getTransactionContext();
		WhereList whereCriterias = new WhereList();
		Delete delete = SQL.Delete(this.database);
		
		if(optionalField.getPk() == -1) {
			whereCriterias.addAnd(new Where("label", optionalField.getLabel(), "="));
			whereCriterias.addAnd(new Where("fk_event", optionalField.getLabel(), "="));
		} else {
			whereCriterias.addAnd(new Where("pk", optionalField.getPk(), "="));
		}
		delete.from(DELEGATON_FIELD_TABLE_NAME);
		delete.where(whereCriterias);
		
		delete.executeDelete(context);
        context.commit();
	}

}
