package de.tarent.aa.veraweb.worker;

import java.io.IOException;

import de.tarent.aa.veraweb.beans.Event;
import de.tarent.aa.veraweb.beans.Person;
import de.tarent.aa.veraweb.beans.Task;
import de.tarent.aa.veraweb.utils.DateHelper;
import de.tarent.dblayer.sql.statement.Insert;
import de.tarent.dblayer.sql.statement.Update;
import de.tarent.octopus.PersonalConfigAA;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.beans.Database;
import de.tarent.octopus.beans.Request;
import de.tarent.octopus.beans.TransactionContext;
import de.tarent.octopus.beans.veraweb.BeanChangeLogger;
import de.tarent.octopus.beans.veraweb.DatabaseVeraWeb;
import de.tarent.octopus.beans.veraweb.DatabaseVeraWebFactory;
import de.tarent.octopus.beans.veraweb.RequestVeraWeb;
import de.tarent.octopus.server.OctopusContext;

/**
 * Functions needed by webapp to handle event tasks.
 */
public class EventTaskDetailWorker {

	private final DatabaseVeraWebFactory databaseVeraWebFactory;

	public EventTaskDetailWorker(DatabaseVeraWebFactory databaseVeraWebFactory) {
		this.databaseVeraWebFactory = databaseVeraWebFactory;
	}
	
	public EventTaskDetailWorker() {
		this(new DatabaseVeraWebFactory());
	}

	public static final String[] INPUT_setEventTaskId = {"eventId"};
	public static final String OUTPUT_setEventTaskId = "eventId";
	/**
	 * Load and return task with the given id.
	 * 
	 * @param oc
	 * @param eventId
	 * @param id
	 * @return
	 */
    public Task getTask(OctopusContext oc, String eventId, String id) {
        oc.setContent("eventId", eventId);
        if (id == null) {
            return null;
        } else {
            // TODO load and return task with given id
            return new Task();
        }
    }
	
    

	public static final String[] INPUT_loadReferencePerson = { "refPersId" };
	public static final boolean[] MANDATORY_loadReferencePerson = { false };
	
    public void loadReferencePerson(OctopusContext cntx, String refPersId) throws BeanException, IOException {		
		if (refPersId == null || refPersId.trim().length() == 0) {
			return;
		};
		
		Integer pk = Integer.parseInt(refPersId);

		cntx.getContentObject().setField("refPerson", getPersonFromDB(cntx, pk));
    }

	public static final String[] INPUT_copyTaskAndEventId = { "eventId", "taskId" };
	public static final boolean[] MANDATORY_copyTaskAndEventId = { false, false };
	
    public void copyTaskAndEventId(OctopusContext oc, String eventId, String taskId) {
        oc.setContent("eventId", eventId);
        oc.setContent("taskId", taskId);
    }
    
	/**
	 * Assigns the eventId from the task list to a new created task to link
	 * the task to the event
	 * 
	 * @param cntx
	 * @param eventId
	 * @return eventId
	 */
	public String setEventTaskId(OctopusContext cntx, String eventId) {
	    cntx.setContent("eventId", eventId);
	    return eventId;
	}
	
	public static final String[] INPUT_showDetail = { "eventId", "id" };
	public static final boolean[] MANDATORY_showDetail = { false, false };
	public static final String OUTPUT_showDetail = "task";
	
	public Task showDetail(OctopusContext context, Integer eventId, Integer id) throws BeanException, IOException{
		if(eventId != null){
			setEventTaskId(context, String.valueOf(eventId));
		}
		Task task = getTaskFromDB(context, id);
		
		if (task != null && task.personId != null) {
			Person person = getPersonFromDB(context, task.personId);
			context.getContentObject().setField("refPerson", person);
		};
		
		return task;
	}


	public static final String INPUT_saveDetail[] = { "savetask" };

	public static final boolean MANDATORY_saveDetail[] = { false };


	public void saveDetail(OctopusContext cntx, Boolean savetask)
			throws BeanException, IOException {
		if (savetask == null || !savetask.booleanValue())
			return;
		   
		Request request = new RequestVeraWeb(cntx);
		Database database = databaseVeraWebFactory.createDatabaseVeraWeb(cntx);
		TransactionContext context = database.getTransactionContext();	  		

		try {
			Task task = (Task) cntx.contentAsObject("task");
			if (task == null) {
				task = (Task) request.getBean("Task", "task");
				DateHelper.addTimeToDate(task.startdate,
						cntx.requestAsString("task-starttime"),
						task.getErrors());
				DateHelper.addTimeToDate(task.enddate,
						cntx.requestAsString("task-endtime"), task.getErrors());
			}

			Task oldTask = (Task) database.getBean("Task", task.getId(),
					context);

			/** Veranstaltung speichern */
			if (task.isModified() && task.isCorrect()) {
				BeanChangeLogger clogger = new BeanChangeLogger(database,
						context);
				if (task.getId() == null) {
					cntx.setContent("countInsert", new Integer(1));
					database.getNextPk(task, context);

					task.updateHistoryFields(null, ((PersonalConfigAA)cntx.personalConfig()).getRoleWithProxy());
					
					Insert insert = database.getInsert(task);
					insert.insert("pk", task.getId());
					
					if (!((PersonalConfigAA) cntx.personalConfig()).getGrants()
							.mayReadRemarkFields()) {
						insert.remove("note");
					}
					context.execute(insert);

					clogger.logInsert(cntx.personalConfig().getLoginname(),
							task);
				} else {
					cntx.setContent("countUpdate", new Integer(1));
					Update update = database.getUpdate(task);
					if (!((PersonalConfigAA) cntx.personalConfig()).getGrants()
							.mayReadRemarkFields()) {
						update.remove("note");
					}
					context.execute(update);

					clogger.logUpdate(cntx.personalConfig().getLoginname(), oldTask, task);
				}
			} else {
				cntx.setStatus("notsaved");
			}
			cntx.setContent("task", task);
			cntx.setContent("task-starthastime",
					Boolean.valueOf(DateHelper.isTimeInDate(task.getStartdate())));
			cntx.setContent("task-endhastime",
					Boolean.valueOf(DateHelper.isTimeInDate(task.getEnddate())));
			
			if (task != null && task.personId != null) {
				Person person = getPersonFromDB(cntx, task.personId);
				cntx.getContentObject().setField("refPerson", person);
			};

			context.commit();
		} catch (BeanException e) {
			context.rollBack();
			// must report error to user
			throw new BeanException(
					"Die Taskdetails konnten nicht gespeichert werden.", e);
		}
	}

	static public Task getTaskFromDB(OctopusContext context, Integer id) throws BeanException, IOException {
		if(id == null){
			return null;
		}
		
		Database database = new DatabaseVeraWeb(context);
		Task task = (Task) database.getBean("Task", id);
		
		if(task != null){
			context.setContent("task-starthastime", Boolean.valueOf(DateHelper.isTimeInDate(task.startdate)));
			context.setContent("task-endhastime", Boolean.valueOf(DateHelper.isTimeInDate(task.enddate)));
		}
		
		return task;
	}

	static public Person getPersonFromDB(OctopusContext context, Integer id) throws BeanException, IOException {
		if(id == null){
			return null;
		}
		
		Database database = new DatabaseVeraWeb(context);
		Person person = (Person) database.getBean("Person", id);
		
		return person;
	}

}
