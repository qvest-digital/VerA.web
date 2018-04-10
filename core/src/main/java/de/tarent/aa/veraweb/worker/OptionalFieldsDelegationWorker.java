package de.tarent.aa.veraweb.worker;

/*-
 * Veranstaltungsmanagement VerA.web (platform-independent
 * webservice-based event management) is Copyright
 *  © 2014, 2015, 2016, 2017 Атанас Александров <a.alexandrov@tarent.de>
 *  © 2018 Атанас Александров <sirakov@gmail.com>
 *  © 2013 Иванка Александрова <i.alexandrova@tarent.de>
 *  © 2013 Patrick Apel <p.apel@tarent.de>
 *  © 2016 Eugen Auschew <e.auschew@tarent.de>
 *  © 2013 Andrei Boulgakov <a.boulgakov@tarent.de>
 *  © 2013 Valentin But <v.but@tarent.de>
 *  © 2016 Lukas Degener <l.degener@tarent.de>
 *  © 2017 Axel Dirla <a.dirla@tarent.de>
 *  © 2015 Julian Drawe <j.drawe@tarent.de>
 *  © 2014 Dominik George <d.george@tarent.de>
 *  © 2013 Sascha Girrulat <s.girrulat@tarent.de>
 *  © 2008 David Goemans <d.goemans@tarent.de>
 *  © 2015 Viktor Hamm <v.hamm@tarent.de>
 *  © 2013 Katja Hapke <k.hapke@tarent.de>
 *  © 2013 Hendrik Helwich <h.helwich@tarent.de>
 *  © 2005, 2006, 2007, 2008 Christoph Jerolimov <jerolimov@gmx.de>
 *  © 2008, 2009, 2010 Carsten Klein <c.klein@tarent.de>
 *  © 2014 Martin Ley <m.ley@tarent.de>
 *  © 2014, 2015 Max Marche <m.marche@tarent.de>
 *  © 2007 Jan Meyer <jan@evolvis.org>
 *  © 2013, 2014, 2015, 2016, 2017, 2018 mirabilos <t.glaser@tarent.de>
 *  © 2016 Cristian Molina <c.molina@tarent.de>
 *  © 2017 Michael Nienhaus <m.nienhaus@tarent.de>
 *  © 2013 Claudia Nuessle <c.nuessle@tarent.de>
 *  © 2014, 2015 Jon Nunez Alvarez <j.nunez-alvarez@tarent.de>
 *  © 2016 Jens Oberender <j.oberender@tarent.de>
 *  © 2016, 2017 Miluška Pech <m.pech@tarent.de>
 *  © 2009 Martin Pelzer <m.pelzer@tarent.de>
 *  © 2013 Marc Radel <m.radel@tarent.de>
 *  © 2013 Sebastian Reimers <s.reimers@tarent.de>
 *  © 2015 Charbel Saliba <c.saliba@tarent.de>
 *  © 2008, 2009, 2010 Thomas Schmitz <t.schmitz@tarent.de>
 *  © 2013 Volker Schmitz <v.schmitz@tarent.de>
 *  © 2014 Sven Schumann <s.schumann@tarent.de>
 *  © 2014 Sevilay Temiz <s.temiz@tarent.de>
 *  © 2013 Kevin Viola Schmitz <k.schmitz@tarent.de>
 *  © 2015 Stefan Walenda <s.walenda@tarent.de>
 *  © 2015, 2016, 2017 Max Weierstall <m.weierstall@tarent.de>
 *  © 2013 Rebecca Weinz <r.weinz@tarent.de>
 *  © 2015, 2016 Stefan Weiz <s.weiz@tarent.de>
 *  © 2015, 2016 Tim Zimmer <t.zimmer@tarent.de>
 * and older code, Copyright © 2004–2008 ⮡ tarent GmbH and contributors.
 * Licensor is tarent solutions GmbH, http://www.tarent.de/
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
import de.tarent.aa.veraweb.beans.OptionalDelegationField;
import de.tarent.aa.veraweb.beans.OptionalFieldTypeContent;
import de.tarent.dblayer.engine.DB;
import de.tarent.dblayer.helper.ResultList;
import de.tarent.dblayer.helper.ResultMap;
import de.tarent.dblayer.sql.SQL;
import de.tarent.dblayer.sql.clause.Clause;
import de.tarent.dblayer.sql.clause.Expr;
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
import java.util.Iterator;
import java.util.List;

/**
 * This class handles the optional fields for the delegation guests.
 *
 * @author Max Marche, tarent solutions GmbH
 * @author Atanas Alexandrov, tarent solutions GmbH
 */
public class OptionalFieldsDelegationWorker {

    private static final String DB_PREFIX = "veraweb.";
    private static final String OPTIONAL_FIELDS_DELEGATION_CONTENT_TABLE = DB_PREFIX +
            "toptional_fields_delegation_content";
    private static final Integer MULTIPLE_CHOICE_ID = 3;
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
     * @throws SQLException  TODO
     * @throws BeanException TODO
     */
    public void createOrUpdateOptionalDelegationField(OptionalDelegationField optionalDelegationField)
            throws SQLException, BeanException {
        if (this.checkOptionalDelegationFieldExist(optionalDelegationField)) {
            this.updateOptionalDelegationField(optionalDelegationField);
        } else {
            this.createOptionalDelegationField(optionalDelegationField);
        }
    }

    /**
     * Persists the given "OptionalDelegationField"-object.
     *
     * @param optionalDelegationField The {@link OptionalDelegationField}
     * @throws SQLException  TODO
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
     * @throws SQLException  TODO
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
     * @param eventId  event ID
     * @return List with all optional delegation fields for the current guest
     * @throws SQLException  FIXME
     * @throws BeanException FIXME
     */
    public List<OptionalDelegationField> getOptionalDelegationFieldsByGuestId(int guestId, int eventId)
            throws BeanException, SQLException {

        final String select = getStatementSelectOptionalDelegationField(guestId, eventId);
        final ResultSet resultSet = database.result(select);
        return getOptionalFieldsAsList(resultSet);
    }

    private List<OptionalDelegationField> getOptionalFieldsAsList(ResultSet resultSet)
            throws SQLException, BeanException {
        final List<OptionalDelegationField> optionalFieldsWithTypeContents = new ArrayList<OptionalDelegationField>();
        while (resultSet.next()) {
            getListWithTypeContents(resultSet, optionalFieldsWithTypeContents);
        }
        convertToSingleMultipleChoiceField(optionalFieldsWithTypeContents);

        return cleanUpMultipleOptionalFields(optionalFieldsWithTypeContents);
    }

    private void convertToSingleMultipleChoiceField(final List<OptionalDelegationField> optionalFieldsWithTypeContents)
            throws BeanException {
        final List<OptionalDelegationField> optionalFieldsAltList =
                new ArrayList<OptionalDelegationField>(optionalFieldsWithTypeContents);

        for (final Iterator<OptionalDelegationField> iterator = optionalFieldsWithTypeContents.iterator(); iterator.hasNext(); ) {
            final OptionalDelegationField optionalDelegationField = iterator.next();

            if (isMultipleChoiceField(optionalDelegationField.getFkDelegationField())) {
                // Unifying selected options in the same field
                unifySelectedOptions(optionalFieldsAltList, optionalDelegationField);
            }
        }
    }

    private void unifySelectedOptions(List<OptionalDelegationField> optionalFieldsAltList,
            OptionalDelegationField optionalDelegationField) {
        for (int i = 0; i < optionalFieldsAltList.size(); i++) {
            if ((optionalFieldsAltList.get(i).getFkDelegationField() == optionalDelegationField.getFkDelegationField())) {

                for (int j = 0; j < optionalFieldsAltList.get(i).getOptionalFieldTypeContents().size(); j++) {
                    if (optionalFieldsAltList.get(i).getOptionalFieldTypeContents().get(j).getIsSelected() &&
                            !optionalDelegationField.getOptionalFieldTypeContents().get(j).getIsSelected()) {
                        optionalDelegationField.getOptionalFieldTypeContents().get(j).setIsSelected(true);
                    }
                }
            }
        }
    }

    private List<OptionalDelegationField> cleanUpMultipleOptionalFields(List<OptionalDelegationField> optionalFields) {
        boolean removed = false;
        for (int i = 0; i < optionalFields.size(); i++) {
            for (int j = i + 1; j < optionalFields.size(); j++) {
                if (optionalFields.get(i).equals(optionalFields.get(j))) {
                    optionalFields.remove(j);
                    removed = true;
                } else {
                    removed = false;
                }

                if (removed) {
                    j--;
                }
            }
        }

        return optionalFields;
    }

    private boolean isMultipleChoiceField(final Integer optionalFieldId) throws BeanException {

        final Select select = SQL.Select(database).
                select("toptional_fields.*").
                from("veraweb.toptional_fields").
                whereAndEq("toptional_fields.pk", optionalFieldId);

        final ResultList resultList = database.getList(select, database);

        for (final Iterator<ResultMap> iterator = resultList.iterator(); iterator.hasNext(); ) {
            final ResultMap object = iterator.next();
            final Integer type = (Integer) object.get("fk_type");
            if (type.intValue() == MULTIPLE_CHOICE_ID) {
                return true;
            }
        }

        return false;
    }

    private void getListWithTypeContents(ResultSet resultSet, final List<OptionalDelegationField> optionalDelegationFields)
            throws SQLException, BeanException {
        final OptionalDelegationField optionalDelegationField = new OptionalDelegationField(resultSet);
        final ResultList resultListWithTypeContents = getFieldsAndTypeContentsFromDB(optionalDelegationField);

        final List<OptionalFieldTypeContent> typeContents =
                getFieldsWithTypeContentsAsList(resultListWithTypeContents, optionalDelegationField);

        optionalDelegationField.setOptionalFieldTypeContents(typeContents);
        optionalDelegationFields.add(optionalDelegationField);
    }

    private List<OptionalFieldTypeContent> getFieldsWithTypeContentsAsList(ResultList resultListWithTypeContents,
            OptionalDelegationField optionalDelegationField) {
        final List<OptionalFieldTypeContent> typeContents = new ArrayList<OptionalFieldTypeContent>();
        for (final Iterator<ResultMap> iterator = resultListWithTypeContents.iterator(); iterator.hasNext(); ) {
            final ResultMap object = iterator.next();
            final OptionalFieldTypeContent optionalFieldTypeContent = new OptionalFieldTypeContent();
            optionalFieldTypeContent.setContent((String) object.get("content"));
            optionalFieldTypeContent.setId((Integer) object.get("pk"));
            optionalFieldTypeContent.setFk_optional_field((Integer) object.get("fk_optional_field"));

            checkSelectedValues(optionalDelegationField, object, optionalFieldTypeContent);

            typeContents.add(optionalFieldTypeContent);
        }
        return typeContents;
    }

    private void checkSelectedValues(OptionalDelegationField optionalDelegationField, final ResultMap object,
            final OptionalFieldTypeContent optionalFieldTypeContent) {
        if (object.get("content").equals(optionalDelegationField.getContent())) {
            optionalFieldTypeContent.setIsSelected(true);
        } else {
            optionalFieldTypeContent.setIsSelected(false);
        }
    }

    private ResultList getFieldsAndTypeContentsFromDB(final OptionalDelegationField optionalDelegationField)
            throws BeanException {
        final Clause clauseToEmptyContent = Expr.notLike("toptional_field_type_content.content", "");
        final Clause clauseNotNullContent = Expr.isNotNull("toptional_field_type_content.content");

        final Integer fkDelegationField = optionalDelegationField.getFkDelegationField();
        final Select select = SQL.Select(database).
                select("toptional_field_type_content.pk").
                select("toptional_field_type_content.fk_optional_field").
                select("toptional_field_type_content.content").
                from("veraweb.toptional_field_type_content").
                whereAndEq("toptional_field_type_content.fk_optional_field", fkDelegationField).
                whereAnd(clauseToEmptyContent).
                whereAnd(clauseNotNullContent);

        final ResultList resultListWithTypeContents = database.getList(select, database);
        return resultListWithTypeContents;
    }

    /**
     * Check if the given {@link OptionalDelegationField} exists.
     *
     * @param optionalDelegationField The {@link OptionalDelegationField}
     * @return True if the field exists, otherwise false.
     * @throws SQLException  TODO
     * @throws BeanException TODO
     */
    public boolean checkOptionalDelegationFieldExist(OptionalDelegationField optionalDelegationField)
            throws BeanException, SQLException {

        final Select select = getStatementOptionalFieldExists(optionalDelegationField);
        final ResultSet resultSet = database.result(select);
        return resultSet.next();

    }

    private Select getStatementOptionalFieldExists(OptionalDelegationField optionalDelegationField) {
        final WhereList whereCriterias = new WhereList();
        whereCriterias.addAnd(new Where("fk_guest", optionalDelegationField.getFkGuest(), "="));
        whereCriterias.addAnd(new Where("fk_delegation_field", optionalDelegationField.getFkDelegationField(), "="));
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
        insert.insert("fk_delegation_field", optionalDelegationField.getFkDelegationField());
        insert.insert("fk_type", optionalDelegationField.getFkType());
        return insert;
    }

    private String getStatementSelectOptionalDelegationField(int guestId, int eventId) {
        return "WITH w_vorhandene_labels AS ( " +
                "SELECT DISTINCT dc.fk_guest as fk_guest, dc.fk_delegation_field as fk_delegation_field, f.fk_type as fk_type, " +
                "dc.value as value, " +
                "f.label as label " +
                "FROM veraweb.toptional_fields_delegation_content dc " +
                "RIGHT OUTER JOIN veraweb.toptional_fields f ON (dc.fk_delegation_field = f.pk) " +
                "LEFT OUTER JOIN veraweb.toptional_field_type t ON (t.pk = f.fk_type) " +
                "LEFT OUTER JOIN veraweb.toptional_field_type_content tc ON (tc.fk_optional_field = f.pk) " +
                "WHERE fk_guest = " + guestId +
                ") " +
                "SELECT * FROM w_vorhandene_labels wl " +
                "UNION " +
                "SELECT DISTINCT " + guestId +
                " as fk_guest, f.pk as fk_delegation_field, f.fk_type as fk_type, '' as value, f.label as label " +
                "FROM veraweb.toptional_fields f " +
                "WHERE f.fk_event = " + eventId +
                "AND label != '' " +
                "AND NOT EXISTS ( " +
                "SELECT 42 " +
                "FROM w_vorhandene_labels wl " +
                "WHERE f.label = wl.label " +
                ") " +
                "ORDER BY fk_delegation_field ASC;";
    }

    private Update getStatementUpdateOptionalDelegationField(OptionalDelegationField optionalDelegationField) {
        final WhereList whereCriterias = new WhereList();
        whereCriterias.addAnd(new Where("fk_guest", optionalDelegationField.getFkGuest(), "="));
        whereCriterias.addAnd(new Where("fk_delegation_field", optionalDelegationField.getFkDelegationField(), "="));
        final Update update = SQL.Update(this.database);
        update.table(OPTIONAL_FIELDS_DELEGATION_CONTENT_TABLE);
        update.where(whereCriterias);
        update.update("fk_type", optionalDelegationField.getFkType());
        return update;
    }
}
