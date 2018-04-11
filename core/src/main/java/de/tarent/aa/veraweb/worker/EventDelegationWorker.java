package de.tarent.aa.veraweb.worker;

/*-
 * Veranstaltungsmanagement VerA.web (platform-independent
 * webservice-based event management) is Copyright
 *  © 2014, 2015, 2016, 2017 Атанас Александров (a.alexandrov@tarent.de)
 *  © 2018 Атанас Александров (sirakov@gmail.com)
 *  © 2013 Иванка Александрова (i.alexandrova@tarent.de)
 *  © 2013 Patrick Apel (p.apel@tarent.de)
 *  © 2016 Eugen Auschew (e.auschew@tarent.de)
 *  © 2013 Andrei Boulgakov (a.boulgakov@tarent.de)
 *  © 2013 Valentin But (v.but@tarent.de)
 *  © 2016 Lukas Degener (l.degener@tarent.de)
 *  © 2017 Axel Dirla (a.dirla@tarent.de)
 *  © 2015 Julian Drawe (j.drawe@tarent.de)
 *  © 2014 Dominik George (d.george@tarent.de)
 *  © 2013 Sascha Girrulat (s.girrulat@tarent.de)
 *  © 2008 David Goemans (d.goemans@tarent.de)
 *  © 2015 Viktor Hamm (v.hamm@tarent.de)
 *  © 2013 Katja Hapke (k.hapke@tarent.de)
 *  © 2013 Hendrik Helwich (h.helwich@tarent.de)
 *  © 2018 Thomas Hensel (t.hensel@tarent.de)
 *  © 2005, 2006, 2007, 2008 Christoph Jerolimov (jerolimov@gmx.de)
 *  © 2008, 2009, 2010 Carsten Klein (c.klein@tarent.de)
 *  © 2014 Martin Ley (m.ley@tarent.de)
 *  © 2014, 2015 Max Marche (m.marche@tarent.de)
 *  © 2007 Jan Meyer (jan@evolvis.org)
 *  © 2013, 2014, 2015, 2016, 2017, 2018 mirabilos (t.glaser@tarent.de)
 *  © 2016 Cristian Molina (c.molina@tarent.de)
 *  © 2017 Michael Nienhaus (m.nienhaus@tarent.de)
 *  © 2013 Claudia Nuessle (c.nuessle@tarent.de)
 *  © 2014, 2015 Jon Nunez Alvarez (j.nunez-alvarez@tarent.de)
 *  © 2016 Jens Oberender (j.oberender@tarent.de)
 *  © 2016, 2017 Miluška Pech (m.pech@tarent.de)
 *  © 2009 Martin Pelzer (m.pelzer@tarent.de)
 *  © 2013 Marc Radel (m.radel@tarent.de)
 *  © 2013 Sebastian Reimers (s.reimers@tarent.de)
 *  © 2015 Charbel Saliba (c.saliba@tarent.de)
 *  © 2008, 2009, 2010 Thomas Schmitz (t.schmitz@tarent.de)
 *  © 2013 Volker Schmitz (v.schmitz@tarent.de)
 *  © 2014 Sven Schumann (s.schumann@tarent.de)
 *  © 2014 Sevilay Temiz (s.temiz@tarent.de)
 *  © 2013 Kevin Viola Schmitz (k.schmitz@tarent.de)
 *  © 2015 Stefan Walenda (s.walenda@tarent.de)
 *  © 2015, 2016, 2017 Max Weierstall (m.weierstall@tarent.de)
 *  © 2013 Rebecca Weinz (r.weinz@tarent.de)
 *  © 2015, 2016 Stefan Weiz (s.weiz@tarent.de)
 *  © 2015, 2016 Tim Zimmer (t.zimmer@tarent.de)
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

import de.tarent.aa.veraweb.beans.Event;
import de.tarent.aa.veraweb.beans.OptionalDelegationField;
import de.tarent.aa.veraweb.beans.OptionalField;
import de.tarent.aa.veraweb.beans.OptionalFieldType;
import de.tarent.aa.veraweb.beans.OptionalFieldTypeContent;
import de.tarent.aa.veraweb.utils.DateHelper;
import de.tarent.aa.veraweb.utils.EventURLHandler;
import de.tarent.aa.veraweb.utils.MediaRepresentativesUtilities;
import de.tarent.aa.veraweb.utils.OnlineRegistrationHelper;
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

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Atanas Alexandrov, tarent solutions GmbH
 */
public class EventDelegationWorker {

    public static final String[] INPUT_showDelegationFields = {};

    public static final String OUTPUT_showDelegationFields = "delegationFields";

    public static final String[] INPUT_getDelegationFieldsLabels = { "eventId" };

    public static final String OUTPUT_getDelegationFieldsLabels = "delegationFieldsLabels";

    public static final String[] INPUT_getOptionalFieldTypesFromEvent = { "eventId" };

    public static final String[] INPUT_saveDelegationFieldLabels = { "eventId" };

    /**
     * Show the optional delegation fields in the guest detail view.
     *
     * @param octopusContext The {@link OctopusContext}
     * @return Map with field label as key and field content as value
     * @throws IOException   If fetching event failed
     * @throws BeanException If fetching event failed
     * @throws SQLException  FIXME
     */
    public List<OptionalDelegationField> showDelegationFields(OctopusContext octopusContext)
            throws IOException, BeanException, SQLException {
        final Integer guestId = Integer.valueOf(octopusContext.getContextField("guest.id").toString());
        final Integer eventId = Integer.valueOf(octopusContext.getContextField("event.id").toString());

        if (guestId == null && eventId == null) {
            return null;
        }
        setEventAndMediaRepresantativeURLInContext(octopusContext, eventId);

        return getOptionalFieldsForGuest(octopusContext, guestId, eventId);
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
     * @param eventId        Event id
     * @throws SQLException  FIXME
     * @throws BeanException FIXME
     * @throws IOException   FIXME
     */
    public void getOptionalFieldTypesFromEvent(final OctopusContext octopusContext, Integer eventId)
            throws SQLException, BeanException, IOException {
        setEventAndMediaRepresantativeURLInContext(octopusContext, eventId);

        final DatabaseVeraWeb database = new DatabaseVeraWeb(octopusContext);
        final Select selectTypesStatement = SQL.Select(database);

        selectTypesStatement.select("fk_type");
        selectTypesStatement.from("veraweb.toptional_fields");
        selectTypesStatement.whereAndEq("veraweb.toptional_fields.fk_event", eventId);

        final ResultList result = database.getList(selectTypesStatement, database);
        final List<Integer> OptionalFieldTypesIds = new ArrayList<Integer>();

        for (final Iterator<ResultMap> iterator = result.iterator(); iterator.hasNext(); ) {
            final ResultMap object = iterator.next();
            OptionalFieldTypesIds.add((Integer) object.get("fk_event"));
        }

        octopusContext.setContent("OptionalFieldTypesIds", OptionalFieldTypesIds);
    }

    private List<OptionalFieldType> getAllTypesAsList(Select selectTypesStatement) throws SQLException {
        final List<OptionalFieldType> allTypesAsList = new ArrayList<OptionalFieldType>();
        final ResultSet types = ((Result) selectTypesStatement.execute()).resultSet();
        while (types.next()) {
            final OptionalFieldType field = new OptionalFieldType();
            field.setId(types.getInt("pk"));
            field.setDescription(types.getString("description"));
            allTypesAsList.add(field);
        }
        return allTypesAsList;
    }

    private List<OptionalDelegationField> getOptionalFieldsForGuest(OctopusContext octopusContext, Integer guestId,
            Integer eventId)
            throws BeanException, SQLException {
        final OptionalFieldsDelegationWorker optionalFieldsDelegationWorker =
                new OptionalFieldsDelegationWorker(octopusContext);
        return optionalFieldsDelegationWorker.getOptionalDelegationFieldsByGuestId(guestId, eventId);
    }

    /**
     * FIXME
     *
     * @param octopusContext The {@link OctopusContext}
     * @param eventId        Event id
     * @return FIXME
     * @throws IOException   FIXME
     * @throws BeanException FIXME
     * @throws SQLException  FIXME
     */
    public Map<String, String> showDelegationFieldsOnlyByEventId(OctopusContext octopusContext, Integer eventId)
            throws IOException, BeanException, SQLException {
        setEventAndMediaRepresantativeURLInContext(octopusContext, eventId);
        return null;
    }

    private void setEventAndMediaRepresantativeURLInContext(OctopusContext oc, Integer eventId)
            throws BeanException, IOException {
        oc.setContent("eventId", eventId);
        final Event event = getEvent(oc, eventId);
        if (event != null) {
            oc.setContent("event", event);
        }

        if (OnlineRegistrationHelper.isOnlineregActive(oc)) {
            final EventURLHandler eventURLHandler = new EventURLHandler();
            eventURLHandler.setEventUrl(oc, event.hash);
            final MediaRepresentativesUtilities mediaRepresentativesUtilities = new MediaRepresentativesUtilities(oc, event);
            mediaRepresentativesUtilities.setUrlForMediaRepresentatives();
        }
    }

    /**
     * Get the labels for the delegation fields.
     *
     * @param octopusContext The {@link OctopusContext}
     * @param eventId        Event id
     * @return FIXME
     * @throws IOException   FIXME
     * @throws BeanException FIXME
     * @throws SQLException  FIXME
     */
    public List<OptionalField> getDelegationFieldsLabels(OctopusContext octopusContext, Integer eventId)
            throws IOException, BeanException, SQLException {

        setEventAndMediaRepresantativeURLInContext(octopusContext, eventId);

        final List<OptionalFieldType> fieldTypes = getOptionalFieldTypes(octopusContext);
        octopusContext.setContent("fieldTypes", fieldTypes);

        final List<OptionalField> delegationFieldsLabels = getLabelsFromDB(octopusContext, eventId);

        final List<OptionalDelegationField> delegationFieldsWithTypeContents = new ArrayList<OptionalDelegationField>();
        getFieldsLabelsWithTypeContents(octopusContext, delegationFieldsLabels, delegationFieldsWithTypeContents);

        return delegationFieldsLabels;
    }

    private void getFieldsLabelsWithTypeContents(OctopusContext octopusContext,
            final List<OptionalField> delegationFieldsLabels,
            final List<OptionalDelegationField> delegationFieldsWithTypeContents) throws BeanException, IOException {
        for (final Iterator iterator = delegationFieldsLabels.iterator(); iterator
                .hasNext(); ) {
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

        for (final Iterator<ResultMap> iterator = result.iterator(); iterator.hasNext(); ) {
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
     * @param eventId        Event id
     * @throws BeanException FIXME
     * @throws SQLException  FIXME
     * @throws IOException   FIXME
     */
    public void saveDelegationFieldLabels(OctopusContext octopusContext, Integer eventId)
            throws BeanException, SQLException, IOException {
        final OptionalFieldSummary optionalFieldSummary = new OptionalFieldSummary();
        final OptionalFieldsWorker optionalFieldsWorker = new OptionalFieldsWorker(octopusContext);
        final Map<String, String> allRequestParams = octopusContext.getRequestObject().getRequestParameters();

        final List<OptionalField> oldOptionalFields = optionalFieldsWorker.getTotalEventsFromEvent(eventId);

        for (String key : allRequestParams.keySet()) {
            saveFieldLabels(eventId, optionalFieldsWorker, allRequestParams, key, optionalFieldSummary, oldOptionalFields);
            saveFieldTypeContent(octopusContext, allRequestParams, key, optionalFieldSummary);
        }

        octopusContext.setContent("showSuccessMessage", true);
        octopusContext.setContent("optionalFieldsSummary", optionalFieldSummary);
    }

    private void saveFieldLabels(Integer eventId, OptionalFieldsWorker optionalFieldsWorker,
            Map<String, String> allRequestParams, String key,
            OptionalFieldSummary optionalFieldSummary, List<OptionalField> oldOptionalFields)
            throws SQLException, BeanException, IOException {
        if (key.startsWith("optionalField-")) {
            saveField(eventId, optionalFieldsWorker, allRequestParams, key, optionalFieldSummary, oldOptionalFields);
        }
    }

    private void saveFieldTypeContent(OctopusContext octopusContext, Map<String, String> allRequestParams, String key,
            OptionalFieldSummary optionalFieldSummary)
            throws BeanException, IOException, SQLException {
        if (key.startsWith("optionalFieldtype-") && key.contains("_value")) {
            saveSingleFieldTypeContent(octopusContext, allRequestParams, key, optionalFieldSummary);
        }
    }

    private void saveSingleFieldTypeContent(OctopusContext octopusContext, Map<String, String> allRequestParams,
            String key, OptionalFieldSummary optionalFieldSummary)
            throws BeanException, IOException, SQLException {

        final Database database = new DatabaseVeraWeb(octopusContext);
        final TransactionContext transactionalContext = database.getTransactionContext();
        final String[] keyParts = key.split("_");
        final String[] labelParts = keyParts[0].split("-");
        final String[] valueParts = keyParts[1].split("-");
        final Integer typeContentId = new Integer(valueParts[1]);
        final OptionalFieldTypeContent optionalFieldTypeContent =
                handleUpdateOptionalFieldTypeContent(octopusContext, allRequestParams, key, labelParts, typeContentId);
        final List<OptionalField> changedFields = optionalFieldSummary.getChangedFields();
        final List<OptionalField> deletedFields = optionalFieldSummary.getDeletedFields();
        final OptionalField changedField =
                checkChangedOptionsToSupportSummary(octopusContext, deletedFields, changedFields, typeContentId,
                        optionalFieldTypeContent);
        if (changedField != null) {
            optionalFieldSummary.addChangedOptionalField(changedField);
        }

        final Update updateStatement = optionalFieldUpdateStatement(database, typeContentId, optionalFieldTypeContent);
        transactionalContext.execute(updateStatement);
        transactionalContext.commit();
    }

    private Update optionalFieldUpdateStatement(Database database, Integer valuePart,
            OptionalFieldTypeContent optionalFieldTypeContent) {
        final Update update = SQL.Update(database);
        update.table("veraweb.toptional_field_type_content");
        update.update("content", optionalFieldTypeContent.getContent());
        update.where(Expr.equal("pk", valuePart));
        return update;
    }

    private OptionalField checkChangedOptionsToSupportSummary(OctopusContext octopusContext,
            List<OptionalField> deletedFields,
            List<OptionalField> changedFields,
            Integer typeContentId,
            OptionalFieldTypeContent optionalFieldTypeContent)
            throws BeanException, IOException, SQLException {

        final OptionalFieldTypeContent oldOptionalFieldTypeContent = getExistingTypeContent(octopusContext, typeContentId);
        final String optionalFieldContent = oldOptionalFieldTypeContent.getContent();
        if (optionalFieldContent != null && !optionalFieldContent.equals(optionalFieldTypeContent.getContent())) {

            final Integer optionalFieldId = optionalFieldTypeContent.getFk_optional_field();
            final Boolean changed = getFieldChangedStatus(changedFields, optionalFieldId);

            if (!changed) {
                return addChangedOptionalFieldToSummary(octopusContext, deletedFields, optionalFieldId);
            }
        }
        return null;
    }

    private Boolean getFieldChangedStatus(List<OptionalField> changedFields, Integer optionalFieldId) {
        Boolean changed = false;
        if (changedFields != null) {
            for (OptionalField changedField : changedFields) {
                if (optionalFieldId.equals(changedField.getId())) {
                    changed = true;
                    break;
                }
            }
        }
        return changed;
    }

    private OptionalField addChangedOptionalFieldToSummary(OctopusContext octopusContext,
            List<OptionalField> deletedFields,
            Integer optionalFieldId) throws BeanException, SQLException {
        final OptionalField optionalField = getRelatedOptionalFieldByTypeContent(octopusContext, optionalFieldId);
        if (deletedFields == null || !deletedFields.contains(optionalField)) {
            return optionalField;
        }
        return null;
    }

    private OptionalFieldTypeContent handleUpdateOptionalFieldTypeContent(
            OctopusContext octopusContext,
            Map<String, String> allRequestParams, String key,
            String[] labelParts, Integer typeContentId) throws BeanException, IOException, SQLException {
        final OptionalFieldTypeContent optionalFieldTypeContent;
        optionalFieldTypeContent = getExistingTypeContent(octopusContext, typeContentId);
        optionalFieldTypeContent.setFk_optional_field(new Integer(labelParts[1]));
        optionalFieldTypeContent.setContent(allRequestParams.get(key).toString());
        optionalFieldTypeContent.setId(typeContentId);

        return optionalFieldTypeContent;
    }

    private OptionalFieldTypeContent getExistingTypeContent(OctopusContext octopusContext, Integer typeContentId)
            throws BeanException, IOException, SQLException {
        final Database database = new DatabaseVeraWeb(octopusContext);
        final Select select = SQL.Select(database);
        select.select("*");
        select.from("veraweb.toptional_field_type_content");
        select.where(Expr.equal("veraweb.toptional_field_type_content.pk", typeContentId));
        ResultSet resultSet = database.result(select);

        OptionalFieldTypeContent optionalFieldTypeContent = null;

        while (resultSet.next()) {
            Integer pk = resultSet.getInt("pk");
            Integer fkOptionalField = resultSet.getInt("fk_optional_field");
            String content = resultSet.getString("content");

            optionalFieldTypeContent = new OptionalFieldTypeContent(pk, fkOptionalField, content);
        }

        return optionalFieldTypeContent;
    }

    private OptionalField getRelatedOptionalFieldByTypeContent(OctopusContext octopusContext, Integer optionalFieldId)
            throws BeanException, SQLException {
        final Database database = new DatabaseVeraWeb(octopusContext);
        final Select select = SQL.Select(database);
        select.select("*");
        select.from("veraweb.toptional_fields");
        select.where(Expr.equal("toptional_fields.pk", optionalFieldId));

        ResultSet resultSet = database.result(select);
        OptionalField optionalField = null;

        while (resultSet.next()) {
            optionalField = new OptionalField(resultSet);
        }

        return optionalField;
    }

    private void saveField(Integer eventId, OptionalFieldsWorker optionalFieldsWorker,
            Map<String, String> allRequestParams, String key,
            OptionalFieldSummary optionalFieldSummary, List<OptionalField> oldOptionalFields)
            throws SQLException, BeanException, IOException {
        final String[] splitted = key.split("-");
        final OptionalField optionalField = new OptionalField();
        optionalField.setFkEvent(eventId);
        optionalField.setLabel(allRequestParams.get(key).toString());
        optionalField.setId(Integer.parseInt(splitted[1]));
        final String typeValue = allRequestParams.get("optionalFieldType-" + splitted[1]);

        if (typeValue.equals(OptionalFieldTypeFacade.inputfield.getValue().toString())) {
            optionalField.setFkType(OptionalFieldTypeFacade.inputfield.getValue());
        } else if (typeValue.equals(OptionalFieldTypeFacade.simple_combobox.getValue().toString())) {
            optionalField.setFkType(OptionalFieldTypeFacade.simple_combobox.getValue());
        } else if (typeValue.equals(OptionalFieldTypeFacade.multiple_combobox.getValue().toString())) {
            optionalField.setFkType(OptionalFieldTypeFacade.multiple_combobox.getValue());
        }

        generateOptionalFieldSummary(optionalFieldSummary, oldOptionalFields, optionalField);
        optionalFieldsWorker.updateOptionalField(optionalField);
    }

    private void generateOptionalFieldSummary(OptionalFieldSummary optionalFieldSummary, List<OptionalField> oldOptionalFields,
            OptionalField optionalField) {
        for (OptionalField oldField : oldOptionalFields) {
            if (oldField.getId() == optionalField.getId()) {
                if ("".equals(oldField.getLabel()) && !"".equals(optionalField.getLabel())) {
                    optionalFieldSummary.addCreatedOptionalField(optionalField);
                } else if (!"".equals(oldField.getLabel()) && "".equals(optionalField.getLabel())) {
                    optionalFieldSummary.addDeletedOptionalField(optionalField);
                    optionalField.setFkType(OptionalFieldTypeFacade.inputfield.getValue());
                } else if (!oldField.getLabel().equals(optionalField.getLabel()) ||
                        !oldField.getFkType().equals(optionalField.getFkType())) {
                    optionalFieldSummary.addChangedOptionalField(optionalField);
                }
            }
        }
    }

    /**
     * Duplicate optional fields in the database exam (database).
     *
     * @param octopusContext The {@link OctopusContext}
     * @param of             FIXME
     * @param eventId        Event id
     * @return FIXME
     * @throws BeanException FIXME
     * @throws IOException   FIXME
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
        return i != null && i > 0;
    }

    private static Event getEvent(OctopusContext cntx, Integer id) throws BeanException, IOException {
        if (id == null) {
            return null;
        }

        final Database database = new DatabaseVeraWeb(cntx);
        final Event event = (Event) database.getBean("Event", id);

        if (event != null) {
            cntx.setContent("event-beginhastime", Boolean.valueOf(DateHelper.isTimeInDate(event.begin)));
            cntx.setContent("event-endhastime", Boolean.valueOf(DateHelper.isTimeInDate(event.end)));
        }
        return event;
    }
}
