package de.tarent.aa.veraweb.worker;

import de.tarent.aa.veraweb.beans.Delegation;
import de.tarent.aa.veraweb.beans.DelegationField;
import de.tarent.aa.veraweb.beans.Event;
import de.tarent.aa.veraweb.utils.DateHelper;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.beans.Database;
import de.tarent.octopus.beans.veraweb.DatabaseVeraWeb;
import de.tarent.octopus.server.OctopusContext;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Atanas Alexandrov, tarent solutions GmbH
 */
public class EventDelegationWorker {

    public static final String INPUT_showDelegationFields[] = {"id", "eventId"};

    public static final String OUTPUT_showDelegationFields = "delegationFields";

    public static final String INPUT_getDelegationFieldsLabels[] = {"eventId"};

    public static final String OUTPUT_getDelegationFieldsLabels = "delegationFieldsLabels";
    
    public static final String INPUT_saveDelegationFieldLabels[] = {"eventId", "delegationFields", };

//    public static final String OUTPUT_saveDelegationFieldLabels = "";

    /**
     * Show the optional delegation fields in the guest detail view.
     *
     * @param oc The {@link OctopusContext}
     * @param eventId The event id
     *
     * @return Map with field label as key and field content as value
     *
     * @throws IOException If fetching event failed
     * @throws BeanException If fetching event failed
     */
    public Map<String, String> showDelegationFields(OctopusContext oc, Integer eventId, Integer guestId)
            throws IOException, BeanException, SQLException {
        setEventInContext(oc, eventId);

        Map<String, String> delegationFields = new LinkedHashMap<String, String>();

        DelegationWorker delegationWorker = new DelegationWorker(oc);
        List<Delegation> delegationFieldsForGuest = delegationWorker.getDelegationsByGuest(guestId);
        Integer counter = 1;
        for (Delegation delegation : delegationFieldsForGuest) {
            delegationFields.put(delegation.getLabel(), delegation.getValue());
            counter++;
        };

        return delegationFields;
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
    public List<String> getDelegationFieldsLabels(OctopusContext oc, Integer eventId)
            throws IOException, BeanException, SQLException {

        setEventInContext(oc, eventId);

        final List<String> delegationFieldsLabelds = new ArrayList<String>();
        final DelegationFieldWorker delegationFieldWorker = new DelegationFieldWorker(oc);
        
        List<DelegationField> delegationFieldsByEvent = delegationFieldWorker.getDelegationFieldsByEvent(eventId);
        
        for (DelegationField delegationField : delegationFieldsByEvent) {
        	delegationFieldsLabelds.add(delegationField.getLabel());
		}

        return delegationFieldsLabelds;
    }

    public void saveDelegationFieldLabels(OctopusContext oc, Integer eventId, List<String> delegationFieldLables) throws BeanException, SQLException {
    	DelegationFieldWorker delegationFieldWorker = new DelegationFieldWorker(oc);
    	List<String> createdOrUpdatedLabels = new ArrayList<String>();
    	
		for(String label : delegationFieldLables) {
			DelegationField delegationField = new DelegationField();
			delegationField.setFkEvent(eventId);
			delegationField.setLabel(label);
			
			delegationFieldWorker.createOrUpdateDelegationField(delegationField);
			
			createdOrUpdatedLabels.add(label);
		}
		
		delegationFieldLables.removeAll(createdOrUpdatedLabels);
		
		for (String label : createdOrUpdatedLabels) {
			DelegationField delegationField = new DelegationField();
			delegationField.setFkEvent(eventId);
			delegationField.setLabel(label);
			
			delegationFieldWorker.removeDelegationField(delegationField);
		}
    	
    	
    }
    
    private static Event getEvent(OctopusContext cntx, Integer id) throws BeanException, IOException {
        if (id == null) return null;

        final Database database = new DatabaseVeraWeb(cntx);
        final Event event = (Event)database.getBean("Event", id);

        if (event != null) {
            cntx.setContent("event-beginhastime", Boolean.valueOf(DateHelper.isTimeInDate(event.begin)));
            cntx.setContent("event-endhastime", Boolean.valueOf(DateHelper.isTimeInDate(event.end)));
        }
        return event;
    }
}
