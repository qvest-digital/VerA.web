package de.tarent.aa.veraweb.worker;

import de.tarent.aa.veraweb.beans.Event;
import de.tarent.aa.veraweb.utils.DateHelper;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.beans.Database;
import de.tarent.octopus.beans.veraweb.DatabaseVeraWeb;
import de.tarent.octopus.server.OctopusContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Atanas Alexandrov, tarent solutions GmbH
 */
public class EventDelegationWorker {

    public static final String INPUT_showDelegationFields[] = {"eventId"};

    public static final String OUTPUT_showDelegationFields = "delegationFieldsContent";

    public static final String INPUT_getDelegationFieldsLabels[] = {};

    public static final String OUTPUT_getDelegationFieldsLabels = "delegationFieldsLabels";

    public List<String> showDelegationFields(OctopusContext oc, Integer eventId) throws IOException, BeanException {
        oc.setContent("eventId", eventId);
        Event event = getEvent(oc, eventId);
        if (event != null) {
            oc.setContent("event", event);
        }

        // TODO Zusätzliche Felder anzeigen

        return new ArrayList<String>();
    }

    public List<String> getDelegationFieldsLabels() throws IOException, BeanException {
        List<String> delegationFieldsLabelds = new ArrayList<String>();
        delegationFieldsLabelds.add("Bezeichnung Feld 01");
        delegationFieldsLabelds.add("Bezeichnung Feld 02");
        delegationFieldsLabelds.add("Bezeichnung Feld 03");
        delegationFieldsLabelds.add("Bezeichnung Feld 04");
        delegationFieldsLabelds.add("Bezeichnung Feld 05");
        delegationFieldsLabelds.add("Bezeichnung Feld 06");
        delegationFieldsLabelds.add("Bezeichnung Feld 07");
        delegationFieldsLabelds.add("Bezeichnung Feld 08");
        delegationFieldsLabelds.add("Bezeichnung Feld 09");
        delegationFieldsLabelds.add("Bezeichnung Feld 10");
        delegationFieldsLabelds.add("Bezeichnung Feld 11");
        delegationFieldsLabelds.add("Bezeichnung Feld 12");
        delegationFieldsLabelds.add("Bezeichnung Feld 13");
        delegationFieldsLabelds.add("Bezeichnung Feld 14");
        delegationFieldsLabelds.add("Bezeichnung Feld 15");

        return delegationFieldsLabelds;
    }

    static public Event getEvent(OctopusContext cntx, Integer id) throws BeanException, IOException {
        if (id == null) return null;

        Database database = new DatabaseVeraWeb(cntx);
        Event event = (Event)database.getBean("Event", id);

        if (event != null) {
            cntx.setContent("event-beginhastime", Boolean.valueOf(DateHelper.isTimeInDate(event.begin)));
            cntx.setContent("event-endhastime", Boolean.valueOf(DateHelper.isTimeInDate(event.end)));
        }
        return event;
    }
}
