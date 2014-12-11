package de.tarent.aa.veraweb.worker;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import de.tarent.aa.veraweb.beans.OptionalDelegationField;
import de.tarent.dblayer.engine.DB;
import de.tarent.dblayer.sql.SQL;
import de.tarent.dblayer.sql.SyntaxErrorException;
import de.tarent.dblayer.sql.clause.Where;
import de.tarent.dblayer.sql.clause.WhereList;
import de.tarent.dblayer.sql.statement.Insert;
import de.tarent.dblayer.sql.statement.Select;
import de.tarent.dblayer.sql.statement.Update;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.beans.Database;
import de.tarent.octopus.beans.TransactionContext;
import de.tarent.octopus.beans.veraweb.DatabaseVeraWeb;
import de.tarent.octopus.server.OctopusContext;


public class OptionalFieldsDelegationWorker {
	private static final String DELEGATION_TABLE_NAME = "veraweb.toptional_fields_delegation_content";
    private static final String DELEGATION_FIELDS_TABLE_NAME = "veraweb.toptional_fields";
    private Database database;
	
	public OptionalFieldsDelegationWorker(OctopusContext ctx) {
		this.database = new DatabaseVeraWeb(ctx);
	}
	
	/**
	 * Persist or Update the given "OptionalDelegationField"-object.
     *
	 * @param optionalDelegationField
     *
	 * @throws SyntaxErrorException
	 * @throws SQLException
	 * @throws BeanException
	 */
	public void createOrUpdateDelegation(OptionalDelegationField optionalDelegationField) throws SQLException, BeanException {
		if(this.delegationExist(optionalDelegationField)) {
			this.updateDelegation(optionalDelegationField);
		} else {
			this.createDelegation(optionalDelegationField);
		}
	}
	
	/**
	 * Persists the given "OptionalDelegationField"-object.
     *
	 * @param optionalDelegationField
     *
	 * @throws SyntaxErrorException
	 * @throws SQLException
	 * @throws BeanException
	 */
	public void createDelegation(OptionalDelegationField optionalDelegationField) throws SQLException, BeanException {
		TransactionContext context = this.database.getTransactionContext();
		Insert insert = SQL.Insert(this.database);
		
		insert.table(DELEGATION_TABLE_NAME);
		insert.insert("fk_guest", optionalDelegationField.getFkGuest());
		insert.insert("fk_delegation_field", optionalDelegationField.getFkDelegationnField());
		insert.insert("value", optionalDelegationField.getValue());

		DB.insert(context, insert.statementToString());
        context.commit();
	}

	/**
	 * Update the "value"-field of an existing "OptionalDelegationField"-object
     *
	 * @param optionalDelegationField
     *
	 * @throws SyntaxErrorException
	 * @throws SQLException
	 * @throws BeanException
	 */
	public void updateDelegation(OptionalDelegationField optionalDelegationField) throws SQLException, BeanException {
		TransactionContext context = this.database.getTransactionContext();
		WhereList whereCriterias = new WhereList();
		Update update = SQL.Update(this.database);
		
		whereCriterias.addAnd(new Where("fk_guest", optionalDelegationField.getFkGuest(), "="));
		whereCriterias.addAnd(new Where("fk_delegation_field", optionalDelegationField.getFkDelegationnField(), "="));
		update.table(DELEGATION_TABLE_NAME);
		update.where(whereCriterias);
		update.update("value", optionalDelegationField.getValue());

		DB.update(context, update.statementToString());
        context.commit();
	}

	/**
	 * Returns an "ArrayList<OptionalDelegationField>" with all delegations who have the given guestId
	 * @param guestId
	 * @return
	 * @throws BeanException
	 * @throws SQLException
	 */
	public List<OptionalDelegationField> getDelegationsByGuest(int guestId) throws BeanException, SQLException {
		List<OptionalDelegationField> result = new ArrayList<OptionalDelegationField>();
		WhereList whereCriterias = new WhereList();
		Select select = SQL.Select(this.database);
		
		whereCriterias.addAnd(new Where("fk_guest", guestId, "="));
		select.from(DELEGATION_TABLE_NAME);
        select.joinLeftOuter(DELEGATION_FIELDS_TABLE_NAME, DELEGATION_TABLE_NAME + ".fk_delegation_field", DELEGATION_FIELDS_TABLE_NAME + ".pk");
		select.select("fk_guest");
		select.select("fk_delegation_field");
		select.select("value");
        select.select(DELEGATION_FIELDS_TABLE_NAME + ".label as label");
		
		ResultSet resultSet = database.result(select);
		
        while(resultSet.next()) {
        	OptionalDelegationField optionalDelegationField = new OptionalDelegationField(resultSet);
        	result.add(optionalDelegationField);
        }
	
		return result;
	}
	
	/**
	 * Returns the "OptionalDelegationField" with the given guestId and delegationFieldId
	 * @param guestId
	 * @param delegationFieldId
	 * @return
	 * @throws BeanException
	 * @throws SQLException
	 */
	public OptionalDelegationField getDelegationByGuestAndDelegationField(int guestId, int delegationFieldId) throws BeanException, SQLException {
		WhereList whereCriterias = new WhereList();
		Select select = SQL.Select(this.database);
		
		whereCriterias.addAnd(new Where("fk_guest", guestId, "="));
		whereCriterias.addAnd(new Where("fk_delegation_field", guestId, "="));
		select.from(DELEGATION_TABLE_NAME);
		select.select("fk_guest");
		select.select("fk_delegation_field");
		select.select("value");
		
		ResultSet resultSet = database.result(select);

		if(resultSet.next()) {
        	return new OptionalDelegationField(resultSet);
		}
		
		return null;
	}
	
	/**
	 * Check if the given "OptionalDelegationField" exist
	 * @param optionalDelegationField
	 * @return
	 * @throws BeanException
	 * @throws SQLException
	 */
	public boolean delegationExist(OptionalDelegationField optionalDelegationField) throws BeanException, SQLException {
		WhereList whereCriterias = new WhereList();
		Select select = SQL.Select(this.database);
		
		whereCriterias.addAnd(new Where("fk_guest", optionalDelegationField.getFkGuest(), "="));
		whereCriterias.addAnd(new Where("fk_delegation_field", optionalDelegationField.getFkDelegationnField(), "="));
		select.from(DELEGATION_TABLE_NAME);
		select.select("fk_guest");
		select.select("fk_delegation_field");
		
		ResultSet resultSet = database.result(select);

		if(resultSet.next()) {
        	return true;
        	
		}
		
		return false;
	}
}
