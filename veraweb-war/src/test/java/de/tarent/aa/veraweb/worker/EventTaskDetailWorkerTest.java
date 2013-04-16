package de.tarent.aa.veraweb.worker;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import junit.framework.TestCase;

import org.mockito.ArgumentMatcher;

import de.tarent.aa.veraweb.beans.Grants;
import de.tarent.aa.veraweb.beans.Task;
import de.tarent.dblayer.sql.statement.Insert;
import de.tarent.octopus.PersonalConfigAA;
import de.tarent.octopus.beans.Bean;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.beans.TransactionContext;
import de.tarent.octopus.beans.veraweb.DatabaseVeraWeb;
import de.tarent.octopus.beans.veraweb.DatabaseVeraWebFactoryMock;
import de.tarent.octopus.request.TcRequest;
import de.tarent.octopus.server.OctopusContext;

public class EventTaskDetailWorkerTest extends TestCase {

	public void testSaveDetail() throws BeanException, IOException, ParseException {
		// create mocked objects
		TransactionContext context = mock(TransactionContext.class);// ,, withSettings().verboseLogging());
		DatabaseVeraWeb dbv = mock(DatabaseVeraWeb.class);// , withSettings().verboseLogging());
		doReturn(context).when(dbv).getTransactionContext();
		Insert insert = mock(Insert.class);// ,, withSettings().verboseLogging());
		doReturn(insert).when(dbv).getInsert(isA(Task.class));
		EventTaskDetailWorker worker = new EventTaskDetailWorker(new DatabaseVeraWebFactoryMock(dbv));
		OctopusContext cntx = mock(OctopusContext.class);// , withSettings().verboseLogging());
		TcRequest request = mock(TcRequest.class);// , withSettings().verboseLogging());
		doReturn(request).when(cntx).getRequestObject();
		PersonalConfigAA personalConfig = mock(PersonalConfigAA.class);// , withSettings().verboseLogging());
		Grants grants = mock(Grants.class);// , withSettings().verboseLogging());
		doReturn(grants).when(personalConfig).getGrants();
		doReturn(personalConfig).when(cntx).personalConfig();

		// config
		doReturn(null).when(cntx).contentAsObject("task"); // assume no task is in content

		// no id
		putTaskInContextRequest(cntx, null, "Gäste einladen", "25.12.2011", "24.12.2011", "3",
				"Einladungen verschicken", "20", "4", "user123", "user111", "26.12.2911", "23.12.2011", "true",
				"10:23", "09:34");

		// call method which should be tested
		worker.saveDetail(cntx, true);

		Task t = new Task();
		t.setTitle("Gäste einladen");
		t.setStartdate(new Timestamp(DATE_FORMAT.parse("25.12.2011 10:23").getTime()));
		t.setCreated(new Timestamp(DATE_FORMAT.parse("24.12.2011 00:00").getTime()));
		t.setPriority(3);
		t.setDescription("Einladungen verschicken");
		t.setDegreeofcompletion(20);
		t.setEventId(4);
		t.setChangedby("SYSTEM");
		t.setCreatedby("user111");
		t.setEnddate(new Timestamp(DATE_FORMAT.parse("26.12.2911 09:34").getTime()));
		t.setChanged(new Timestamp(new Date().getTime()));
		t.setModified(true);

		// verify that exact one insert of the task has been executed
		verify(context).execute(insert);
		verify(dbv).getInsert(argThat(new TaskArgumentMarcher(t)));
	}

	private final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy HH:mm");

	private static void putTaskInContextRequest(OctopusContext cntx, String id, String title, String startdate,
			String created, String priority, String description, String degreeofcompletion, String fk_event,
			String changedby, String createdby, String enddate, String changed, String modified, String starttime,
			String endtime) {
		doReturn(id).when(cntx).requestAsObject("task-id");
		doReturn(title).when(cntx).requestAsObject("task-title");
		doReturn(startdate).when(cntx).requestAsObject("task-startdate");
		doReturn(created).when(cntx).requestAsObject("task-created");
		doReturn(priority).when(cntx).requestAsObject("task-priority");
		doReturn(description).when(cntx).requestAsObject("task-description");
		doReturn(degreeofcompletion).when(cntx).requestAsObject("task-degreeofcompletion");
		doReturn(fk_event).when(cntx).requestAsObject("task-fk_event");
		doReturn(changedby).when(cntx).requestAsObject("task-changedby");
		doReturn(createdby).when(cntx).requestAsObject("task-createdby");
		doReturn(enddate).when(cntx).requestAsObject("task-enddate");
		doReturn(changed).when(cntx).requestAsObject("task-changed");
		doReturn(modified).when(cntx).requestAsObject("task-modified");
		doReturn(starttime).when(cntx).requestAsString("task-starttime");
		doReturn(endtime).when(cntx).requestAsString("task-endtime");
	}

	class TaskArgumentMarcher extends ArgumentMatcher<Bean> {

		private final Task task;

		public TaskArgumentMarcher(Task task) {
			this.task = task;
		}

		@Override
		public boolean matches(Object taskObj) {
			if (!(taskObj instanceof Task)) {
				return false;
			}
			Task tsk = (Task) taskObj;
			// changed time is not equal but should be similar => assume max 1 second difference
			Timestamp b = task.getChanged();
			Timestamp a = tsk.getChanged();
			if (Math.abs(a.getTime() - b.getTime()) > 1000) {
				return false;
			}
			// temporary remove changed property for equality check
			tsk.setChanged(null);
			task.setChanged(null);
			boolean equal = tsk.equals(task);
			tsk.setChanged(a);
			task.setChanged(b);
			return equal;
		}
	}

}
