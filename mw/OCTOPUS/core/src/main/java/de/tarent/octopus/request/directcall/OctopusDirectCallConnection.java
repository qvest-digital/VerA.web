package de.tarent.octopus.request.directcall;
import de.tarent.octopus.client.*;
import de.tarent.octopus.request.internal.OctopusStarter;

import java.util.Iterator;
import java.util.Map;

/**
 * Liefert eine Verbindung zu einem Octopus im lokalen Prozessraum.
 *
 * @author <a href="mailto:sebastian@tarent.de">Sebastian Mancke</a>, <b>tarent GmbH</b>
 */
public class OctopusDirectCallConnection implements OctopusConnection {
	public static final String PARAM_MODULE = "module";
	public static final String PARAM_TASK = "task";

	OctopusStarter octopusStarter;
	String moduleName;

	/**
	 * Liefert ein CallObject, dass für den Aufruf dieses Task verwendet werden kann.
	 */
	public OctopusTask getTask(String taskName)
	    throws OctopusCallException {
		OctopusTask task = new DirectCallTask(getOctopusStarter());
		task.add(PARAM_MODULE, getModuleName());
		task.add(PARAM_TASK, taskName);
		return task;
	}

	public OctopusResult callTask(String taskName, Map paramMap)
	    throws OctopusCallException {

		OctopusTask task = getTask(taskName);
		if (paramMap != null)
			for (Iterator iter = paramMap.keySet().iterator(); iter.hasNext(); ) {
				String key = (String)iter.next();
				task.add(key, paramMap.get(key));
			}
		return task.invoke();
	}

	public OctopusStarter getOctopusStarter() {
		return octopusStarter;
	}

	public void setOctopusStarter(OctopusStarter newOctopusStarter) {
		this.octopusStarter = newOctopusStarter;
	}

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String newModuleName) {
		this.moduleName = newModuleName;
	}

	public void setPassword(String newPassword) {
		// Do Nothing at the Moment
	}

	public void setUsername(String newUsername) {
		// Do Nothing at the Moment
	}

	public void login() throws OctopusCallException {
		// Do Nothing at the Moment
	}

	public void logout() throws OctopusCallException {
		// Do Nothing at the Moment
	}

	public void setUserDataProvider(UserDataProvider provider) {
		// Do Nothing at the Moment
	}
}
