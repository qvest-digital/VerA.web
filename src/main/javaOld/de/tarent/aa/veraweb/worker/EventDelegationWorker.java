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
import java.util.List;
import java.util.Map;

import de.tarent.aa.veraweb.beans.Event;
import de.tarent.aa.veraweb.beans.OptionalDelegationField;
import de.tarent.aa.veraweb.beans.OptionalField;
import de.tarent.aa.veraweb.beans.OptionalFieldType;
import de.tarent.aa.veraweb.beans.OptionalFieldTypeContent;
import de.tarent.aa.veraweb.utils.DateHelper;
import de.tarent.aa.veraweb.utils.OptionalFieldSummary;
import de.tarent.aa.veraweb.utils.OptionalFieldTypeFacade;
import de.tarent.dblayer.engine.Result;
import de.tarent.dblayer.helper.ResultList;
import de.tarent.dblayer.helper.ResultMap;
import de.tarent.dblayer.sql.SQL;
import de.tarent.dblayer.sql.clause.Expr;
import de.tarent.dblayer.sql.statement.Select;
import de.tarent.dblayer.sql.statement.Update;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.beans.Database;
import de.tarent.octopus.beans.TransactionContext;
import de.tarent.octopus.beans.veraweb.DatabaseVeraWeb;
import de.tarent.octopus.server.OctopusContext;

/**
 * @author Atanas Alexandrov, tarent solutions GmbH
 */
public class EventDelegationWorker {

    public static final String[] INPUT_showDelegationFields = {};

    public static final String OUTPUT_showDelegationFields = "delegationFields";

    public static final String[] INPUT_getDelegationFieldsLabels = {"eventId"};

    public static final String OUTPUT_getDelegationFieldsLabels = "delegationFieldsLabels";

    public static final String[] INPUT_getOptionalFieldTypesFromEvent = {"eventId"};

    public static final String[] INPUT_saveDelegationFieldLabels = {"eventId"};

    /**
     * Show the optional delegation fields in the guest detail view.
     *
     * @param octopusContext The {@link OctopusContext}
     *
     * @return Map with field label as key and field content as value
     * @throws IOException   If fetching event failed
     * @throws BeanException If fetching event failed
     * @throws SQLException FIXME
     */
    public List<OptionalDelegationField> showDelegationFields(OctopusContext octopusContext)
            throws IOException, BeanException, SQLException {
        final Integer guestId = Integer.valueOf(octopusContext.getContextField("guest.id").toString());
        final Integer eventId = Integer.valueOf(octopusContext.getContextField("event.id").toString());

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

    /**
     * FIXME
     * TODO set IDs as content for choosing saved field.type
     *
     * @param octopusContext The {@link OctopusContext}
     * @param eventId Event id
     *
     * @throws SQLException FIXME
     * @throws BeanException FIXME
     * @throws IOException FIXME
     */
    public void getOptionalFieldTypesFromEvent(final OctopusContext octopusContext, Integer eventId)
            throws SQLException, BeanException, IOException {
        setEventInContext(octopusContext, eventId);

        final DatabaseVeraWeb database = new DatabaseVeraWeb(octopusContext);
        final Select selectTypesStatement = SQL.Select(database);

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

    private List<OptionalDelegationField> getOptionalFieldsForGuest(OctopusContext octopusContext, Integer guestId)
            throws BeanException, SQLException {
        final OptionalFieldsDelegationWorker optionalFieldsDelegationWorker =
                new OptionalFieldsDelegationWorker(octopusContext);
        return optionalFieldsDelegationWorker.getOptionalDelegationFieldsByGuestId(guestId);
    }

    private List<OptionalField> getOptionalFieldDeclaredInEvent(OctopusContext octopusContext, Integer eventId)
            throws BeanException, SQLException {
        final OptionalFieldsWorker optionalFieldsWorker = new OptionalFieldsWorker(octopusContext);
        return optionalFieldsWorker.getOptionalFieldsByEvent(eventId);
    }

    private Integer getIntegerFromRequestParameter(OctopusContext oc, String parameter) {
        final Object parameterValue = oc.getRequestObject().getRequestParameters().get(parameter);
        if (parameterValue == null) {
            return null;
        }
        return new Integer(parameterValue.toString());
    }

    /**
     * FIXME
     * @param octopusContext The {@link OctopusContext}
     * @param eventId Event id
     *
     * @return FIXME
     *
     * @throws IOException FIXME
     * @throws BeanException FIXME
     * @throws SQLException FIXME
     */
    public Map<String, String> showDelegationFieldsOnlyByEventId(OctopusContext octopusContext, Integer eventId)
            throws IOException, BeanException, SQLException {
        setEventInContext(octopusContext, eventId);
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
     * @param octopusContext The {@link OctopusContext}
     * @param eventId Event id
     *
     * @return FIXME
     *
     * @throws IOException FIXME
     * @throws BeanException FIXME
     * @throws SQLException FIXME
     */
    public List<OptionalField> getDelegationFieldsLabels(OctopusContext octopusContext, Integer eventId)
            throws IOException, BeanException, SQLException {

        setEventInContext(octopusContext, eventId);

        final List<OptionalFieldType> fieldTypes = getOptionalFieldTypes(octopusContext);
        octopusContext.setContent("fieldTypes", fieldTypes);

        final List<OptionalField> delegationFieldsLabels = getLabelsFromDB(octopusContext, eventId);

        final List<OptionalDelegationField> delegationFieldsWithTypeContents = new ArrayList<OptionalDelegationField>();
        for (final Iterator iterator = delegationFieldsLabels.iterator(); iterator
                .hasNext();) {
            final OptionalField optionalField = (OptionalField) iterator.next();
            final List<OptionalFieldTypeContent> optionalFieldTypeContents =
                    getOptionalFieldTypeContentsFromLabel(octopusContext, optionalField.getId());

            final OptionalDelegationField optionalDelegationField = new OptionalDelegationField();
            optionalDelegationField.setOptionalFieldTypeContents(optionalFieldTypeContents);
            optionalDelegationField.setLabel(optionalField.getLabel());
            optionalDelegationField.setFkDelegationField(optionalField.getId());
            optionalDelegationField.setFkType(optionalField.getFkType());

            delegationFieldsWithTypeContents.add(optionalDelegationField);
        }

        octopusContext.setContent("delegationFieldsWithTypeContents", delegationFieldsWithTypeContents);

        return delegationFieldsLabels;
    }

    private List<OptionalFieldTypeContent> getOptionalFieldTypeContentsFromLabel(
            OctopusContext octopusContext,
            Integer optionalFieldId) throws BeanException, IOException {

        final Database database = new DatabaseVeraWeb(octopusContext);
        final Select select = getSelectStatementForTypeContents(optionalFieldId, database);
        final ResultList result = database.getList(select, database);
        final List<OptionalFieldTypeContent> oftc = getOptionalFieldsTypeContentsAsList(result);

        return oftc;
    }

    private Select getSelectStatementForTypeContents(Integer optionalFieldId, Database database) {
        final Select select = SQL.Select(database);
        select.select("toptional_field_type_content.pk");
        select.select("toptional_field_type_content.fk_optional_field");
        select.select("toptional_field_type_content.content");
        select.from("veraweb.toptional_field_type_content");
        select.where(Expr.equal("veraweb.toptional_field_type_content.fk_optional_field", optionalFieldId));
        return select;
    }

    private List<OptionalFieldTypeContent> getOptionalFieldsTypeContentsAsList(ResultList result) {
        final List<OptionalFieldTypeContent> oftc = new ArrayList<OptionalFieldTypeContent>();

        for (final Iterator<ResultMap> iterator = result.iterator(); iterator.hasNext();) {
            final ResultMap object = iterator.next();
            final OptionalFieldTypeContent optionalFieldTypeContent = new OptionalFieldTypeContent();
            optionalFieldTypeContent.setContent((String) object.get("content"));
            optionalFieldTypeContent.setId((Integer) object.get("pk"));
            optionalFieldTypeContent.setFk_optional_field((Integer) object.get("fk_optional_field"));
            oftc.add(optionalFieldTypeContent);
        }
        return oftc;
    }

    private List<OptionalField> getLabelsFromDB(OctopusContext octopusContext, Integer eventId)
            throws BeanException, SQLException {
        final OptionalFieldsWorker optionalFieldsWorker = new OptionalFieldsWorker(octopusContext);
        final List<OptionalField> optionalFieldsByEvent = optionalFieldsWorker.getOptionalFieldsByEvent(eventId);

        return optionalFieldsByEvent;
    }

    /**
     * FIXME.
     *
     * @param octopusContext The {@link OctopusContext}
     * @param eventId Event id
     *
     * @throws BeanException FIXME
     * @throws SQLException FIXME
     * @throws IOException FIXME
     */
    public void saveDelegationFieldLabels(OctopusContext octopusContext, Integer eventId)
            throws BeanException, SQLException, IOException {
        OptionalFieldSummary optionalFieldSummary = new OptionalFieldSummary();

        final OptionalFieldsWorker optionalFieldsWorker = new OptionalFieldsWorker(octopusContext);

        final Map<String, String> allRequestParams = octopusContext.getRequestObject().getRequestParameters();

        setDeletedOptionalFieldsIntoSummary(optionalFieldSummary,optionalFieldsWorker, allRequestParams, eventId);


        for (String key : allRequestParams.keySet()) {
            saveFieldLabels(eventId, optionalFieldsWorker, allRequestParams, key, optionalFieldSummary);
            saveFieldTypeContent(octopusContext, allRequestParams, key);
        }

        octopusContext.setContent("showSuccessMessage", true);
        octopusContext.setContent("optionalFieldsSummary", optionalFieldSummary);
    }

    private void setDeletedOptionalFieldsIntoSummary(OptionalFieldSummary optionalFieldSummary,
                                                     final OptionalFieldsWorker optionalFieldsWorker,
                                                     final Map<String, String> allRequestParams,
                                                     final Integer eventId) throws IOException, BeanException {

        Integer totalOldOptionalFields = optionalFieldsWorker.getTotalEventsFromEvent(eventId);
        Integer totalNewFields = 0;
        for (String key : allRequestParams.keySet()) {
            if (key.startsWith("optionalField-")) {
                String value = allRequestParams.get(key).toString();
                if (!value.equals("")) {
                    totalNewFields++;
                }
            }
        }

        Integer difference = totalOldOptionalFields - totalNewFields;

        if (difference > 0) {
            optionalFieldSummary.totalDeletedFields = difference;
        } else {
            optionalFieldSummary.totalCreatedFields = difference.intValue();
        }

    }

    private void saveFieldLabels(Integer eventId, OptionalFieldsWorker optionalFieldsWorker,
                                 Map<String, String> allRequestParams, String key,
                                 OptionalFieldSummary optionalFieldSummary) throws SQLException, BeanException, IOException {
        if (key.startsWith("optionalField-")) {
            saveField(eventId, optionalFieldsWorker, allRequestParams, key, optionalFieldSummary);
        }
    }

    private void saveFieldTypeContent(OctopusContext octopusContext, Map<String, String> allRequestParams, String key)
            throws BeanException, IOException, SQLException {
        if (key.startsWith("optionalFieldtype-") && key.contains("_value")) {
            saveSingleFieldTypeContent(octopusContext, allRequestParams, key);            
        }
    }

    private void saveSingleFieldTypeContent(OctopusContext octopusContext, Map<String, String> allRequestParams,
                                            String key) throws BeanException, IOException, SQLException {
        
        final Database database = new DatabaseVeraWeb(octopusContext);
        final TransactionContext transactionalContext = database.getTransactionContext();
        
        final String[] keyParts = key.split("_");
        final String[] labelParts = keyParts[0].split("-");
        final String[] valueParts = keyParts[1].split("-");
        
        final OptionalFieldTypeContent optionalFieldTypeContent =
            handleUpdateOptionalFieldTypeContent(
                    octopusContext,
                    allRequestParams,
                    key,
                    labelParts,
                    new Integer(valueParts[1])
            );

        final Update update = SQL.Update( database );
        update.table( "veraweb.toptional_field_type_content" );
        update.update( "content", optionalFieldTypeContent.getContent());
        update.where( Expr.equal( "pk", new Integer(valueParts[1]) ) );

        transactionalContext.execute(update);
        transactionalContext.commit();
    }

    private OptionalFieldTypeContent handleUpdateOptionalFieldTypeContent(
            OctopusContext octopusContext,
            Map<String, String> allRequestParams, String key,
            String[] labelParts, Integer typeContentId) throws BeanException, IOException {
        final OptionalFieldTypeContent optionalFieldTypeContent;
        optionalFieldTypeContent = getExistingTypeContent(octopusContext, typeContentId);
        optionalFieldTypeContent.setFk_optional_field(new Integer(labelParts[1]));
        optionalFieldTypeContent.setContent(allRequestParams.get(key).toString());
        optionalFieldTypeContent.setId(typeContentId);
        
        return optionalFieldTypeContent;
    }
    
    private OptionalFieldTypeContent getExistingTypeContent(OctopusContext octopusContext, Integer typeContentId)
            throws BeanException, IOException {
            final Database database = new DatabaseVeraWeb(octopusContext);
            final Select select = SQL.Select(database);
            select.select("toptional_field_type_content.*");
            select.from("veraweb.toptional_field_type_content");
            select.where(Expr.equal("veraweb.toptional_field_type_content.pk", typeContentId));

            return (OptionalFieldTypeContent) database.getBean("OptionalFieldTypeContent", select);
    }

    private void saveField(Integer eventId, OptionalFieldsWorker optionalFieldsWorker,
                           Map<String, String> allRequestParams, String key,
                           OptionalFieldSummary optionalFieldSummary) throws SQLException, BeanException, IOException {
        final String[] splitted = key.split("-");
        final OptionalField optionalField = new OptionalField();
        optionalField.setFkEvent(eventId);
        optionalField.setLabel(allRequestParams.get(key).toString());
        optionalField.setId(Integer.parseInt(splitted[1]));
        final String typeValue = allRequestParams.get("optionalFieldType-" + splitted[1]);
        if (typeValue.equals(OptionalFieldTypeFacade.inputfield.getText())) {
            optionalField.setFkType(OptionalFieldTypeFacade.inputfield.getValue());
        } else if (typeValue.equals(OptionalFieldTypeFacade.simple_combobox.getText())) {
            optionalField.setFkType(OptionalFieldTypeFacade.simple_combobox.getValue());
        } else if (typeValue.equals(OptionalFieldTypeFacade.multiple_combobox.getText())) {
            optionalField.setFkType(OptionalFieldTypeFacade.multiple_combobox.getValue());
        }

        optionalFieldsWorker.updateOptionalField(optionalField);
        /** Insertion into summary */
        // 1. Check if the field was stored -> into update list

//        optionalFieldSummary.setCreatedFields(new ArrayList<OptionalField>());
//        optionalFieldSummary.setChangedFields(new ArrayList<OptionalField>());
//        optionalFieldSummary.setDeletedFields(new ArrayList<OptionalField>());
//
//        if (!optionalFieldsWorker.isFieldRegisterForTheEvent(optionalField.getFkEvent(), optionalField.getId())) {
//            optionalFieldSummary.addCreatedOptionalField(optionalField);
//        }
//        else {
//            optionalFieldSummary.addChangedOptionalField(optionalField);
//
//        }
        // 2. Check if the field is new -> into create list
        // 3. Check if the field is deleted -> into delete list

        // TODO implement checks


//        optionalFieldSummary.addDeletedOptionalField(optionalField);
    }




    /**
     * Duplicate optional fields in the database exam (database).
     *
     * @param octopusContext The {@link OctopusContext}
     * @param of FIXME
     * @param eventId Event id
     *
     * @return FIXME
     *
     * @throws BeanException FIXME
     * @throws IOException FIXME
     */
    public Boolean checkExistingOptionalField(OctopusContext octopusContext, String of, Integer eventId)
            throws BeanException, IOException {
        final Database database = new DatabaseVeraWeb(octopusContext);
        final Select select = SQL.Select(database);
        select.select("count(pk)");
        select.from("veraweb.toptional_fields");
        select.where(Expr.equal("veraweb.toptional_fields.label", of.trim()));
        select.whereAnd(Expr.equal("veraweb.toptional_fields.fk_event", eventId));

        final Integer i = database.getCount(select);
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

