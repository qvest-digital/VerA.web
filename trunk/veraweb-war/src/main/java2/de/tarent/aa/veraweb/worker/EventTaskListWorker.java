package de.tarent.aa.veraweb.worker;

import java.util.ArrayList;
import java.util.List;

import de.tarent.octopus.server.OctopusContext;

/**
 * Functions needed by webapp to handle event tasks.
 */
public class EventTaskListWorker {
	
	
    public static final String[] INPUT_getTasks = {"id"};
	public static final boolean[] MANDATORY_getTasks = {true};
	public static final String OUTPUT_getTasks = "tasks";
	
	/**
	 * Load and return all tasks of the event with the given id.
	 * 
	 * @param oc
	 * @param eventId
	 * @return
	 */
	public List<Object> getTasks(OctopusContext oc, String eventId) {
		oc.setContent("eventId", eventId);
		// TODO load tasks for given event and return them
		return new ArrayList<Object>();
	}


}
