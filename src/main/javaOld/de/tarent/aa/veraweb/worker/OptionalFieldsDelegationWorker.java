/**
 * veraweb, platform independent webservice-based event management
 * (Veranstaltungsmanagment VerA.web), is
 * Copyright © 2004–2008 tarent GmbH
 * Copyright © 2013–2015 tarent solutions GmbH
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see: http://www.gnu.org/licenses/
 */
package de.tarent.aa.veraweb.worker;

import de.tarent.aa.veraweb.beans.OptionalDelegationField;
import de.tarent.dblayer.engine.DB;
import de.tarent.dblayer.sql.SQL;
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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class handles the optional fields for the delegation guests.
 *
 * @author Max Marche, tarent solutions GmbH
 * @author Atanas Alexandrov, tarent solutions GmbH
 */
public class OptionalFieldsDelegationWorker {

    private static final String OPTIONAL_FIELDS_DELEGATION_CONTENT_TABLE = "veraweb.toptional_fields_delegation_content";
    private static final String OPTIONAL_FIELDS_TABLE = "veraweb.toptional_fields";
    private Database database;

    /**
     * Constructor.
     *
     * @param ctx The {@link OctopusContext}
     */
	public OptionalFieldsDelegationWorker(OctopusContext ctx) {
		this.database = new DatabaseVeraWeb(ctx);
	}

	/**
	 * Persist or update the given {@link OptionalDelegationField}.
     *
	 * @param optionalDelegationField Label for the optional delegation field
     *
     * @throws SQLException TODO
     * @throws BeanException TODO
	 */
	public void createOrUpdateOptionalDelegationField(OptionalDelegationField optionalDelegationField)
            throws SQLException, BeanException {
		if(this.checkOptionalDelegationFieldExist(optionalDelegationField)) {
			this.updateOptionalDelegationField(optionalDelegationField);
		} else {
			this.createOptionalDelegationField(optionalDelegationField);
		}
	}

	/**
	 * Persists the given "OptionalDelegationField"-object.
     *
	 * @param optionalDelegationField The {@link OptionalDelegationField}
     *
     * @throws SQLException TODO
     * @throws BeanException TODO
	 */
	public void createOptionalDelegationField(OptionalDelegationField optionalDelegationField)
            throws SQLException, BeanException {
        final TransactionContext context = this.database.getTransactionContext();
        final Insert insert = getStatementInsertOptionalField(optionalDelegationField);

		DB.insert(context, insert.statementToString());
        context.commit();
	}

    /**
	 * Update the "value"-field of an existing "OptionalDelegationField"-object
     *
	 * @param optionalDelegationField The {@link OptionalDelegationField}
     *
     * @throws SQLException TODO
	 * @throws BeanException TODO
	 */
	public void updateOptionalDelegationField(OptionalDelegationField optionalDelegationField)
            throws BeanException, SQLException {
        final TransactionContext context = this.database.getTransactionContext();

        final Update update = getStatementUpdateOptionalDelegationField(optionalDelegationField);

		DB.update(context, update.statementToString());
        context.commit();
	}


    /**
	 * Get the optional delegation fields by guest id.
     *
	 * @param guestId Guest id
     *
	 * @return List with all optional delegation fields for the current guest
     *
     * @throws SQLException TODO
     * @throws BeanException TODO
	 */
	public List<OptionalDelegationField> getOptionalDelegationFieldsByGuestId(int guestId)
            throws BeanException, SQLException {

        final Select select = getStatementSelectOptionalDelegationField(guestId);
        final ResultSet resultSet = database.result(select);
        return getOptionalFieldsAsList(resultSet);
	}

    /**
	 * Get the optional delegation fields by guest id.
     *
	 * @param guestId Guest id
     *
	 * @return List with all optional delegation fields for the current guest
     *
     * @throws SQLException TODO
     * @throws BeanException TODO
	 */
	public OptionalDelegationField getOptionalDelegationFieldByGuestIdAndOptionalField(int guestId, int optionalField)
            throws BeanException, SQLException {

        final Select select = getStatementSelectOptionalDelegationFieldByGuestAndOptionalField(guestId, optionalField);
        final ResultSet resultSet = database.result(select);
        return new OptionalDelegationField(resultSet);
	}

    private Select getStatementSelectOptionalDelegationFieldByGuestAndOptionalField(
			int guestId, int optionalField) {
        final WhereList whereCriterias = new WhereList();
        final Select select = SQL.Select(this.database);

        whereCriterias.addAnd(new Where("fk_guest", guestId, "="));
        whereCriterias.addAnd(new Where("fk_delegation_field", optionalField, "="));

        select.where(whereCriterias);
        select.from(OPTIONAL_FIELDS_DELEGATION_CONTENT_TABLE);
        select.joinLeftOuter(OPTIONAL_FIELDS_TABLE,
                OPTIONAL_FIELDS_DELEGATION_CONTENT_TABLE + ".fk_delegation_field", OPTIONAL_FIELDS_TABLE + ".pk");
        select.select("fk_guest");
        select.select("fk_delegation_field");
        select.select("value");
        select.select(OPTIONAL_FIELDS_TABLE + ".label as label");

		return null;
	}

	private List<OptionalDelegationField> getOptionalFieldsAsList(ResultSet resultSet) throws SQLException {
        final List<OptionalDelegationField> optionalDelegationFields = new ArrayList<OptionalDelegationField>();
        while(resultSet.next()) {
            final OptionalDelegationField optionalDelegationField = new OptionalDelegationField(resultSet);
            optionalDelegationFields.add(optionalDelegationField);
        }

        return optionalDelegationFields;
    }


    /**
	 * Check if the given {@link }OptionalDelegationField} exists.
     *
	 * @param optionalDelegationField The {@link OptionalDelegationField}
     *
	 * @return True if the field exists, otherwise false.
     *
     * @throws SQLException TODO
     * @throws BeanException TODO
	 */
	public boolean checkOptionalDelegationFieldExist(OptionalDelegationField optionalDelegationField)
            throws BeanException, SQLException {

        final Select select = getStatementOptionalFieldExists(optionalDelegationField);
        final ResultSet resultSet = database.result(select);
		if(resultSet.next()) {
        	return true;
		}

		return false;
	}

    private Select getStatementOptionalFieldExists(OptionalDelegationField optionalDelegationField) {
        final WhereList whereCriterias = new WhereList();
        whereCriterias.addAnd(new Where("fk_guest", optionalDelegationField.getFkGuest(), "="));
        whereCriterias.addAnd(new Where("fk_delegation_field", optionalDelegationField.getFkDelegationnField(), "="));
        final Select select = SQL.Select(this.database);
        select.from(OPTIONAL_FIELDS_DELEGATION_CONTENT_TABLE);
        select.select("fk_guest");
        select.select("fk_delegation_field");
        return select;
    }

    private Insert getStatementInsertOptionalField(OptionalDelegationField optionalDelegationField) {
        final Insert insert = SQL.Insert(this.database);

        insert.table(OPTIONAL_FIELDS_DELEGATION_CONTENT_TABLE);
        insert.insert("fk_guest", optionalDelegationField.getFkGuest());
        insert.insert("fk_delegation_field", optionalDelegationField.getFkDelegationnField());
        insert.insert("value", optionalDelegationField.getValue());
        return insert;
    }

    private Select getStatementSelectOptionalDelegationField(int guestId) {
        final WhereList whereCriterias = new WhereList();
        whereCriterias.addAnd(new Where("fk_guest", guestId, "="));

        final Select select = SQL.Select(this.database);
        select.where(whereCriterias);
        select.from(OPTIONAL_FIELDS_DELEGATION_CONTENT_TABLE);
        select.joinLeftOuter(OPTIONAL_FIELDS_TABLE,
                OPTIONAL_FIELDS_DELEGATION_CONTENT_TABLE + ".fk_delegation_field", OPTIONAL_FIELDS_TABLE + ".pk");
        select.select("fk_guest");
        select.select("fk_delegation_field");
        select.select("value");
        select.select(OPTIONAL_FIELDS_TABLE + ".label as label");
        return select;
    }

    private Update getStatementUpdateOptionalDelegationField(OptionalDelegationField optionalDelegationField) {
        final WhereList whereCriterias = new WhereList();
        whereCriterias.addAnd(new Where("fk_guest", optionalDelegationField.getFkGuest(), "="));
        whereCriterias.addAnd(new Where("fk_delegation_field", optionalDelegationField.getFkDelegationnField(), "="));
        final Update update = SQL.Update(this.database);
        update.table(OPTIONAL_FIELDS_DELEGATION_CONTENT_TABLE);
        update.where(whereCriterias);
        update.update("value", optionalDelegationField.getValue());
        return update;
    }
}