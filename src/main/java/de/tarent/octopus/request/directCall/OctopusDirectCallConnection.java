
/*
 * tarent-octopus, Webservice Data Integrator and Application Server
 * Copyright © 2002–2017 tarent solutions GmbH and its contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package de.tarent.octopus.request.directCall;

import de.tarent.octopus.client.*;
import de.tarent.octopus.request.internal.OctopusStarter;

import java.util.*;

/** 
 * Liefert eine Verbindung zu einem Octopus im lokalen Prozessraum.
 *
 * @author <a href="mailto:sebastian@tarent.de">Sebastian Mancke</a>, <b>tarent GmbH</b>
 */
public class OctopusDirectCallConnection 
    implements OctopusConnection {


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
        for (Iterator iter = paramMap.keySet().iterator(); iter.hasNext();) {
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