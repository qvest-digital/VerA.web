package de.tarent.aa.veraweb.worker;

import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import de.tarent.aa.veraweb.beans.Event;
import de.tarent.aa.veraweb.beans.OptionalDelegationField;
import de.tarent.aa.veraweb.beans.OptionalField;
import de.tarent.aa.veraweb.utils.DateHelper;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.beans.Database;
import de.tarent.octopus.beans.veraweb.DatabaseVeraWeb;
import de.tarent.octopus.server.OctopusContext;

/**
 * @author Atanas Alexandrov, tarent solutions GmbH
 */
public class EventDelegationWorker {

    public static final String INPUT_showDelegationFields[] = {"id", "eventId"};

    public static final String OUTPUT_showDelegationFields = "delegationFields";

    public static final String INPUT_getDelegationFieldsLabels[] = {"eventId"};

    public static final String OUTPUT_getDelegationFieldsLabels = "delegationFieldsLabels";
    
    public static final String INPUT_saveDelegationFieldLabels[] = {"eventId"};

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

        OptionalFieldsDelegationWorker optionalFieldsDelegationWorker = new OptionalFieldsDelegationWorker(oc);
        List<OptionalDelegationField> optionalDelegationFieldFieldsForGuest = optionalFieldsDelegationWorker.getOptionalDelegationFieldsByGuestId(guestId);
        Integer counter = 1;
        for (OptionalDelegationField optionalDelegationField : optionalDelegationFieldFieldsForGuest) {
            delegationFields.put(optionalDelegationField.getLabel(), optionalDelegationField.getValue());
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
    public Map<String, String> getDelegationFieldsLabels(OctopusContext oc, Integer eventId)
            throws IOException, BeanException, SQLException {

        setEventInContext(oc, eventId);

        final Map<String, String> delegationFieldsLabelds = new LinkedHashMap<String, String>();
        final OptionalFieldsWorker optionalFieldsWorker = new OptionalFieldsWorker(oc);
        
        List<OptionalField> optionalFieldsByEvent = optionalFieldsWorker.getOptionalFieldsByEvent(eventId);
        
        for (OptionalField optionalField : optionalFieldsByEvent) {
        	delegationFieldsLabelds.put(String.valueOf(optionalField.getPk()), optionalField.getLabel());
		}

        return delegationFieldsLabelds;
    }

    public void saveDelegationFieldLabels(OctopusContext oc, Integer eventId) throws BeanException, SQLException {
    	OptionalFieldsWorker optionalFieldsWorker = new OptionalFieldsWorker(oc);
    	Map<String, String> allRequestParams = oc.getRequestObject().getRequestParameters();
    	
    	for (String key : allRequestParams.keySet()) {
    		
    		if(key.startsWith("optionalField-")) {
        		OptionalField optionalField = new OptionalField();
        		optionalField.setFkEvent(eventId);
    			String[] splitted = key.split("-");
    			optionalField.setLabel(allRequestParams.get(key).toString());
    			optionalField.setPk(Integer.parseInt(splitted[1]));
    			
    			updateOrDeleteOptionalField(optionalFieldsWorker, optionalField);
    		}else if(key.startsWith("optionalField")) {
    			Object tempParam = allRequestParams.get(key);
    			
    			if(tempParam instanceof String[]) {
    				String[] tempParamA = (String[]) tempParam;
    				
    				for (String string : tempParamA) {
        				createOptionalField(optionalFieldsWorker, eventId, allRequestParams.get(key));
					}
    				
    			} else {
    				createOptionalField(optionalFieldsWorker, eventId, allRequestParams.get(key));
    			}
    		}
    	}
    }
    
    private void createOptionalField(OptionalFieldsWorker optionalFieldsWorker, int eventId, String label) throws SQLException, BeanException {
    	OptionalField optionalField = new OptionalField();
		optionalField.setFkEvent(eventId);
		optionalField.setLabel(label);
		optionalFieldsWorker.createOptionalField(optionalField);
    }
    
    private void updateOrDeleteOptionalField(OptionalFieldsWorker optionalFieldsWorker, OptionalField optionalField) throws SQLException, BeanException {
    	if(optionalField.getLabel().trim().isEmpty()) {
    		optionalFieldsWorker.removeOptionalField(optionalField);
    	} else {
    		optionalFieldsWorker.updateOptionalField(optionalField);
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

