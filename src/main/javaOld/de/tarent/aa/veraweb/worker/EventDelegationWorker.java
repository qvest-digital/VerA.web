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

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import de.tarent.aa.veraweb.beans.Event;
import de.tarent.aa.veraweb.beans.OptionalDelegationField;
import de.tarent.aa.veraweb.beans.OptionalField;
import de.tarent.aa.veraweb.beans.OptionalFieldType;
import de.tarent.aa.veraweb.beans.OptionalFieldTypeContent;
import de.tarent.aa.veraweb.beans.Person;
import de.tarent.aa.veraweb.utils.DateHelper;
import de.tarent.aa.veraweb.utils.OptionalFieldTypeFacade;
import de.tarent.dblayer.engine.Result;
import de.tarent.dblayer.helper.ResultList;
import de.tarent.dblayer.helper.ResultMap;
import de.tarent.dblayer.sql.Join;
import de.tarent.dblayer.sql.SQL;
import de.tarent.dblayer.sql.clause.Expr;
import de.tarent.dblayer.sql.clause.RawClause;
import de.tarent.dblayer.sql.statement.Select;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.beans.Database;
import de.tarent.octopus.beans.veraweb.DatabaseVeraWeb;
import de.tarent.octopus.server.OctopusContext;

/**
 * @author Atanas Alexandrov, tarent solutions GmbH
 */
public class EventDelegationWorker {

    public static final String INPUT_showDelegationFields[] = {};

    public static final String OUTPUT_showDelegationFields = "delegationFields";

    public static final String INPUT_getDelegationFieldsLabels[] = {"eventId"};

    public static final String OUTPUT_getDelegationFieldsLabels = "delegationFieldsLabels";

    public static final String INPUT_getOptionalFieldTypesFromEvent[] = {"eventId"};

    public static final String INPUT_saveDelegationFieldLabels[] = {"eventId"};

    /**
     * Show the optional delegation fields in the guest detail view.
     *
     * @param octopusContext The {@link OctopusContext}
     *
     * @return Map with field label as key and field content as value
     * @throws IOException   If fetching event failed
     * @throws BeanException If fetching event failed
     */
    public List<OptionalDelegationField> showDelegationFields(OctopusContext octopusContext)
            throws IOException, BeanException, SQLException {
        Integer guestId = getIntegerFromRequestParameter(octopusContext, "id");
        Integer eventId = getIntegerFromRequestParameter(octopusContext, "eventId");
        if (guestId == null && eventId == null) {
            return null;
        }
        setEventInContext(octopusContext, eventId);

        return getOptionalFieldsForGuest(octopusContext, guestId);
    }

    private List<OptionalFieldType> getOptionalFieldTypes(final OctopusContext octopusContext) throws SQLException {
        final DatabaseVeraWeb database = new DatabaseVeraWeb(octopusContext);
        final Select selectTypesStatement = SQL.Select(database);
        selectTypesStatement.select("*");
        selectTypesStatement.from("toptional_field_type");

        return getAllTypesAsList(selectTypesStatement);
    }

    //TODO set IDs as content for choosing saved field.type
    public void getOptionalFieldTypesFromEvent(final OctopusContext octopusContext, Integer eventId) throws SQLException, BeanException, IOException {
        setEventInContext(octopusContext, eventId);

        final DatabaseVeraWeb database = new DatabaseVeraWeb(octopusContext);
        Select selectTypesStatement = SQL.Select(database);

        selectTypesStatement.select("fk_type");
        selectTypesStatement.from("veraweb.toptional_fields");
        selectTypesStatement.whereAndEq("veraweb.toptional_fields.fk_event", eventId);

        final ResultList result = database.getList(selectTypesStatement, database);
        final List<Integer> OptionalFieldTypesIds = new ArrayList<Integer>();

        for (final Iterator<ResultMap> iterator = result.iterator(); iterator.hasNext();) {
            final ResultMap object = iterator.next();
            OptionalFieldTypesIds.add((Integer)object.get("fk_event"));
        }

        octopusContext.setContent("OptionalFieldTypesIds", OptionalFieldTypesIds);
    }

    private  List<OptionalFieldType> getAllTypesAsList(Select selectTypesStatement) throws SQLException {
        final List<OptionalFieldType> allTypesAsList = new ArrayList<OptionalFieldType>();
        final ResultSet types = (ResultSet) ((Result) selectTypesStatement.execute()).resultSet();
        while (types.next()) {
            final OptionalFieldType field = new OptionalFieldType();
            field.setId(types.getInt("pk"));
            field.setDescription(types.getString("description"));
            allTypesAsList.add(field);
        }
        return allTypesAsList;
    }

    private List<OptionalDelegationField> getOptionalFieldsForGuest(OctopusContext octopusContext, Integer guestId) throws BeanException, SQLException {
        OptionalFieldsDelegationWorker optionalFieldsDelegationWorker = new OptionalFieldsDelegationWorker(octopusContext);
        return optionalFieldsDelegationWorker.getOptionalDelegationFieldsByGuestId(guestId);
    }

    private List<OptionalField> getOptionalFieldDeclaredInEvent(OctopusContext octopusContext, Integer eventId) throws BeanException, SQLException {
        OptionalFieldsWorker optionalFieldsWorker = new OptionalFieldsWorker(octopusContext);
        return optionalFieldsWorker.getOptionalFieldsByEvent(eventId);
    }

    private Integer getIntegerFromRequestParameter(OctopusContext oc, String parameter) {
        Object parameterValue = oc.getRequestObject().getRequestParameters().get(parameter);
        if (parameterValue == null) {
            return null;
        }
        return new Integer(parameterValue.toString());
    }

    public Map<String, String> showDelegationFieldsOnlyByEventId(OctopusContext oc, Integer eventId)
            throws IOException, BeanException, SQLException {
        setEventInContext(oc, eventId);
        return null;
    }

    private OptionalField findFieldById(List<OptionalField> fields, int id) {
        for (OptionalField field : fields) {
            if (field.getId() == id) {
                return field;
            }
        }

        return null;
    }

    private void setEventInContext(OctopusContext oc, Integer eventId) throws BeanException, IOException {
        oc.setContent("eventId", eventId);
        final Event event = getEvent(oc, eventId);
        if (event != null) {
            oc.setContent("event", event);
        }
    }

    /**
     * Get the labels for the delegation fields.
     *
     * @return Labels for the fields
     * @throws SQLException
     */
    public List<OptionalField> getDelegationFieldsLabels(OctopusContext octopusContext, Integer eventId)
            throws IOException, BeanException, SQLException {

        setEventInContext(octopusContext, eventId);

        final List<OptionalFieldType> fieldTypes = getOptionalFieldTypes(octopusContext);
        octopusContext.setContent("fieldTypes", fieldTypes);

        final List<OptionalField> delegationFieldsLabels = getLabelsFromDB(octopusContext, eventId);

        List<OptionalDelegationField> delegationFieldsWithTypeContents = new ArrayList<OptionalDelegationField>();
        for (Iterator iterator = delegationFieldsLabels.iterator(); iterator
                .hasNext();) {
            OptionalField optionalField = (OptionalField) iterator.next();
            List<OptionalFieldTypeContent> optionalFieldTypeContents = getOptionalFieldTypeContentsFromLabel(octopusContext, optionalField.getId());

            OptionalDelegationField optionalDelegationField = new OptionalDelegationField();
            optionalDelegationField.setOptionalFieldTypeContents(optionalFieldTypeContents);
            optionalDelegationField.setLabel(optionalField.getLabel());
            optionalDelegationField.setFkDelegationField(optionalField.getId());
            optionalDelegationField.setFkType(optionalField.getFkType());

            delegationFieldsWithTypeContents.add(optionalDelegationField);
        }

        octopusContext.setContent("delegationFieldsWithTypeContents", delegationFieldsWithTypeContents);

        return delegationFieldsLabels;
    }

    private List<OptionalFieldTypeContent> getOptionalFieldTypeContentsFromLabel(OctopusContext octopusContext, Integer optionalFieldId) throws BeanException, IOException {

        final Database database = new DatabaseVeraWeb(octopusContext);
        Select select = SQL.Select(database);
        select.selectAs("toptional_field_type_content.pk", "pk");
        select.selectAs("toptional_field_type_content.fk_optional_field", "fk_optional_field");
        select.selectAs("toptional_field_type_content.content", "content");
        select.from("veraweb.toptional_field_type_content");
        select.where(Expr.equal("veraweb.toptional_field_type_content.fk_optional_field", optionalFieldId));

        return database.getBeanList("OptionalFieldTypeContent", select);
    }

    private List<OptionalField> getLabelsFromDB(OctopusContext octopusContext, Integer eventId) throws BeanException, SQLException {
        final OptionalFieldsWorker optionalFieldsWorker = new OptionalFieldsWorker(octopusContext);
        final List<OptionalField> optionalFieldsByEvent = optionalFieldsWorker.getOptionalFieldsByEvent(eventId);

        return optionalFieldsByEvent;
    }

    public void saveDelegationFieldLabels(OctopusContext oc, Integer eventId) throws BeanException, SQLException, IOException {
        OptionalFieldsWorker optionalFieldsWorker = new OptionalFieldsWorker(oc);
        Map<String, String> allRequestParams = oc.getRequestObject().getRequestParameters();

        for (String key : allRequestParams.keySet()) {
            saveFieldLabels(eventId, optionalFieldsWorker, allRequestParams, key);
        }

        oc.setContent("showSuccessMessage", true);
    }

    private void saveFieldLabels(Integer eventId, OptionalFieldsWorker optionalFieldsWorker, Map<String, String> allRequestParams, String key) throws SQLException, BeanException {
        if (key.startsWith("optionalField-")) {
            saveField(eventId, optionalFieldsWorker, allRequestParams, key);
        }
    }


    private void saveField(Integer eventId, OptionalFieldsWorker optionalFieldsWorker, Map<String, String> allRequestParams, String key) throws SQLException, BeanException {
        OptionalField optionalField = new OptionalField();
        optionalField.setFkEvent(eventId);
        String[] splitted = key.split("-");
        optionalField.setLabel(allRequestParams.get(key).toString());
        optionalField.setId(Integer.parseInt(splitted[1]));
        String typeValue = allRequestParams.get("optionalFieldType-" + splitted[1]);
        if (typeValue.equals(OptionalFieldTypeFacade.inputfield.getText())) {
            optionalField.setFkType(OptionalFieldTypeFacade.inputfield.getValue());
        } else if (typeValue.equals(OptionalFieldTypeFacade.simple_combobox.getText())) {
            optionalField.setFkType(OptionalFieldTypeFacade.simple_combobox.getValue());
        } else if (typeValue.equals(OptionalFieldTypeFacade.multiple_combobox.getText())) {
            optionalField.setFkType(OptionalFieldTypeFacade.multiple_combobox.getValue());
        }

        optionalFieldsWorker.updateOptionalField(optionalField);
    }

    /**
     * Duplicate optional fields in the database exam (database)
     */
    public Boolean checkExistingOptionalField(OctopusContext oc, String of, Integer eventId) throws BeanException, IOException {
        final Database database = new DatabaseVeraWeb(oc);
        Select select = SQL.Select(database);
        select.select("count(pk)");
        select.from("veraweb.toptional_fields");
        select.where(Expr.equal("veraweb.toptional_fields.label", of.trim()));
        select.whereAnd(Expr.equal("veraweb.toptional_fields.fk_event", eventId));

        Integer i = database.getCount(select);
        if (i != null && i > 0) {
            return true;
        }
        return false;
    }

    private static Event getEvent(OctopusContext cntx, Integer id) throws BeanException, IOException {
        if (id == null) return null;

        final Database database = new DatabaseVeraWeb(cntx);
        final Event event = (Event) database.getBean("Event", id);

        if (event != null) {
            cntx.setContent("event-beginhastime", Boolean.valueOf(DateHelper.isTimeInDate(event.begin)));
            cntx.setContent("event-endhastime", Boolean.valueOf(DateHelper.isTimeInDate(event.end)));
        }
        return event;
    }
}

