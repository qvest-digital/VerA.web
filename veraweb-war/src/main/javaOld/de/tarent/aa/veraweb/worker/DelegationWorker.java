package de.tarent.aa.veraweb.worker;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import de.tarent.aa.veraweb.beans.Delegation;
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


public class DelegationWorker {
	private static final String DELEGATION_TABLE_NAME = "veraweb.tdelegations";
	private Database database;
	
	public DelegationWorker(OctopusContext ctx) {
		this.database = new DatabaseVeraWeb(ctx);
	}
	
	/**
	 * Persist or Update the given "Delegation"-object
	 * @param delegation
	 * @throws SyntaxErrorException
	 * @throws SQLException
	 * @throws BeanException
	 */
	public void createOrUpdateDelegation(Delegation delegation) throws SyntaxErrorException, SQLException, BeanException {
		if(this.delegationExist(delegation)) {
			this.updateDelegation(delegation);
		} else {
			this.createDelegation(delegation);
		}
	}
	
	/**
	 * Persists the given "Delegation"-object
	 * @param delegation
	 * @throws SyntaxErrorException
	 * @throws SQLException
	 * @throws BeanException
	 */
	public void createDelegation(Delegation delegation) throws SyntaxErrorException, SQLException, BeanException {
		TransactionContext context = this.database.getTransactionContext();
		Insert insert = SQL.Insert(this.database);
		
		insert.table(DELEGATION_TABLE_NAME);
		insert.insert("fk_guest", delegation.getFkGuest());
		insert.insert("fk_delegation_field", delegation.getFkDelegationnField());
		insert.insert("value", delegation.getValue());

		DB.insert(context, insert.statementToString());
        context.commit();
	}

	/**
	 * Update the "value"-field of an existing "Delegation"-object 
	 * @param delegation
	 * @throws SyntaxErrorException
	 * @throws SQLException
	 * @throws BeanException
	 */
	public void updateDelegation(Delegation delegation) throws SyntaxErrorException, SQLException, BeanException {
		TransactionContext context = this.database.getTransactionContext();
		WhereList whereCriterias = new WhereList();
		Update update = SQL.Update(this.database);
		
		whereCriterias.addAnd(new Where("fk_guest", delegation.getFkGuest(), "="));
		whereCriterias.addAnd(new Where("fk_delegation_field", delegation.getFkDelegationnField(), "="));
		update.table(DELEGATION_TABLE_NAME);
		update.where(whereCriterias);
		update.update("value", delegation.getValue());

		DB.update(context, update.statementToString());
        context.commit();
	}

	/**
	 * Returns an "ArrayList<Delegation>" with all delegations who have the given guestId 
	 * @param guestId
	 * @return
	 * @throws BeanException
	 * @throws SQLException
	 */
	public ArrayList<Delegation> getDelegationsByGuest(int guestId) throws BeanException, SQLException {
		ArrayList<Delegation> result = new ArrayList<Delegation>();
		WhereList whereCriterias = new WhereList();
		Select select = SQL.Select(this.database);
		
		whereCriterias.addAnd(new Where("fk_guest", guestId, "="));
		select.from(DELEGATION_TABLE_NAME);
		select.select("fk_guest");
		select.select("fk_delegation_field");
		select.select("value");
		
		ResultSet resultSet = database.result(select);
		
        while(resultSet.next()) {
        	Delegation delegation = new Delegation(resultSet);
        	result.add(delegation);
        }
	
		return result;
	}
	
	/**
	 * Returns the "Delegation" with the given guestId and delegationFieldId 
	 * @param guestId
	 * @param delegationFieldId
	 * @return
	 * @throws BeanException
	 * @throws SQLException
	 */
	public Delegation getDelegationByGuestAndDelegationField(int guestId, int delegationFieldId) throws BeanException, SQLException {
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
        	return new Delegation(resultSet);
		}
		
		return null;
	}
	
	/**
	 * Check if the given "Delegation" exist
	 * @param delegation
	 * @return
	 * @throws BeanException
	 * @throws SQLException
	 */
	public boolean delegationExist(Delegation delegation) throws BeanException, SQLException {
		WhereList whereCriterias = new WhereList();
		Select select = SQL.Select(this.database);
		
		whereCriterias.addAnd(new Where("fk_guest", delegation.getFkGuest(), "="));
		whereCriterias.addAnd(new Where("fk_delegation_field", delegation.getFkDelegationnField(), "="));
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
