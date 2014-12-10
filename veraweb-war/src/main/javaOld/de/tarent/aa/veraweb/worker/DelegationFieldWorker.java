package de.tarent.aa.veraweb.worker;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import de.tarent.aa.veraweb.beans.Delegation;
import de.tarent.aa.veraweb.beans.DelegationField;
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


public class DelegationFieldWorker {
	private static final String DELEGATON_FIELD_TABLE_NAME = "veraweb.tdelegation_fields";
	private Database database;
	
	public DelegationFieldWorker(OctopusContext ctx) {
		this.database = new DatabaseVeraWeb(ctx);
	}
	
	/**
	 * Persist or Update the given "DelegationField"-object
	 * @param delegationField
	 * @throws SyntaxErrorException
	 * @throws SQLException
	 * @throws BeanException
	 */
	public void createOrUpdateDelegationField(DelegationField delegationField) throws SyntaxErrorException, SQLException, BeanException {
		if(this.delegationfieldExist(delegationField)) {
			this.updateDelegationField(delegationField);
		} else {
			this.createDelegationField(delegationField);
		}
	}
	
	/**
	 * Persists the given "DelegationField"-object
	 * @param delegationField
	 * @throws SyntaxErrorException
	 * @throws SQLException
	 * @throws BeanException 
	 */
	public void createDelegationField(DelegationField delegationField) throws SyntaxErrorException, SQLException, BeanException {
		TransactionContext context = this.database.getTransactionContext();
		Insert insert = SQL.Insert(this.database);
		
		insert.table(DELEGATON_FIELD_TABLE_NAME);
		insert.insert("label", delegationField.getLabel());
		insert.insert("fk_event", delegationField.getFkEvent());

		DB.insert(context, insert.statementToString());
        context.commit();
	}
	
	/**
	 * Update the "label" and "fk_event"(if is set)-field of an existing "Delegation"-object 
	 * @param delegationField
	 * @throws SyntaxErrorException
	 * @throws SQLException
	 * @throws BeanException
	 */
	public void updateDelegationField(DelegationField delegationField) throws SyntaxErrorException, SQLException, BeanException {
		TransactionContext context = this.database.getTransactionContext();
		WhereList whereCriterias = new WhereList();
		Update update = SQL.Update(this.database);
		
		whereCriterias.addAnd(new Where("pk", delegationField.getPk(), "="));
		update.table(DELEGATON_FIELD_TABLE_NAME);
		update.where(whereCriterias);
		if(delegationField.getFkEvent() != -1) {
			update.update("fk_event", delegationField.getFkEvent());
		}
		update.update("label", delegationField.getLabel());

		DB.update(context, update.statementToString());
        context.commit();
	}

	/**
	 * Returns an "ArrayList<DelegationField>" with all delegationFields who have the given fk_event 
	 * @param guestId
	 * @return
	 * @throws BeanException
	 * @throws SQLException
	 */
	public ArrayList<DelegationField> getDelegationFieldsByEvent(int fk_event) throws BeanException, SQLException {
		ArrayList<DelegationField> result = new ArrayList<DelegationField>();
		WhereList whereCriterias = new WhereList();
		Select select = SQL.Select(this.database);
		
		whereCriterias.addAnd(new Where("fk_guest", fk_event, "="));
		select.from(DELEGATON_FIELD_TABLE_NAME);
		select.select("pk");
		select.select("label");
		select.select("fk_event");
		
		ResultSet resultSet = database.result(select);
		
        while(resultSet.next()) {
        	DelegationField delegation = new DelegationField(resultSet);
        	result.add(delegation);
        }
	
		return result;
	}

	/**
	 * Returns the "DelegationField" with the given pk
	 * @param pk
	 * @return
	 * @throws BeanException
	 * @throws SQLException
	 */
	public DelegationField getDelegationFieldByPk(int pk) throws BeanException, SQLException {
		WhereList whereCriterias = new WhereList();
		Select select = SQL.Select(this.database);
		
		whereCriterias.addAnd(new Where("pk", pk, "="));
		select.from(DELEGATON_FIELD_TABLE_NAME);
		select.select("pk");
		select.select("label");
		select.select("fk_event");
		
		ResultSet resultSet = database.result(select);

		if(resultSet.next()) {
        	return new DelegationField(resultSet);
		}
		
		return null;
	}
	
	/**
	 * Check if the given "DelegationField" exist
	 * @param delegationField
	 * @return
	 * @throws BeanException
	 * @throws SQLException
	 */
	public boolean delegationfieldExist(DelegationField delegationField) throws BeanException, SQLException {
		WhereList whereCriterias = new WhereList();
		Select select = SQL.Select(this.database);
		
		if(delegationField.getPk() == -1) {
			whereCriterias.addAnd(new Where("label", delegationField.getLabel(), "="));
			whereCriterias.addAnd(new Where("fk_event", delegationField.getLabel(), "="));
		} else {
			whereCriterias.addAnd(new Where("pk", delegationField.getPk(), "="));
		}
		select.from(DELEGATON_FIELD_TABLE_NAME);
		select.where(whereCriterias);
		select.select("pk");
		
		ResultSet resultSet = database.result(select);
	
		return resultSet.next();
	}

	public void removeDelegationField(DelegationField delegationField) throws SQLException, BeanException {
		TransactionContext context = this.database.getTransactionContext();
		WhereList whereCriterias = new WhereList();
		Delete delete = SQL.Delete(this.database);
		
		if(delegationField.getPk() == -1) {
			whereCriterias.addAnd(new Where("label", delegationField.getLabel(), "="));
			whereCriterias.addAnd(new Where("fk_event", delegationField.getLabel(), "="));
		} else {
			whereCriterias.addAnd(new Where("pk", delegationField.getPk(), "="));
		}
		delete.from(DELEGATON_FIELD_TABLE_NAME);
		delete.where(whereCriterias);
		
		delete.executeDelete(context);
        context.commit();
	}

}
