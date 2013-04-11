package de.tarent.aa.veraweb.worker;

import java.io.IOException;

import de.tarent.aa.veraweb.beans.Event;
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
import de.tarent.octopus.beans.veraweb.RequestVeraWeb;
import de.tarent.octopus.server.OctopusContext;

/**
 * Functions needed by webapp to handle event tasks.
 */
public class EventTaskDetailWorker {

	public static final String[] INPUT_getTask = { "eventId", "id" };
	public static final boolean[] MANDATORY_getTask = { true, false };
	public static final String OUTPUT_getTask = "task";

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


	public static final String INPUT_saveDetail[] = { "savetask" };

	public static final boolean MANDATORY_saveDetail[] = { false };


	public void saveDetail(OctopusContext cntx, Boolean savetask)
			throws BeanException, IOException {
		if (savetask == null || !savetask.booleanValue())
			return;

		Request request = new RequestVeraWeb(cntx);
		Database database = new DatabaseVeraWeb(cntx);
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

			Event oldEvent = (Event) database.getBean("Task", task.getId(),
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

					clogger.logUpdate(cntx.personalConfig().getLoginname(),
							oldEvent, task);
				}
			} else {
				cntx.setStatus("notsaved");
			}
			cntx.setContent("task", task);
			cntx.setContent("task-starthastime",
					Boolean.valueOf(DateHelper.isTimeInDate(task.getStartdate())));
			cntx.setContent("task-endhastime",
					Boolean.valueOf(DateHelper.isTimeInDate(task.getEnddate())));

			context.commit();
		} catch (BeanException e) {
			context.rollBack();
			// must report error to user
			throw new BeanException(
					"Die Taskdetails konnten nicht gespeichert werden.", e);
		}
	}

}
