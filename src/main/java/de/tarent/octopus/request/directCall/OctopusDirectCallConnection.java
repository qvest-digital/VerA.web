/* $Id: OctopusDirectCallConnection.java,v 1.1.1.1 2005/11/21 13:33:37 asteban Exp $
 * tarent-octopus, Webservice Data Integrator and Applicationserver
 * Copyright (C) 2002 tarent GmbH
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 * tarent GmbH., hereby disclaims all copyright
 * interest in the program 'tarent-octopus'
 * (which makes passes at compilers) written
 * by Sebastian Mancke and Michael Klink.
 * signature of Elmar Geese, 1 June 2002
 * Elmar Geese, CEO tarent GmbH
 */

package de.tarent.octopus.request.directCall;

import de.tarent.octopus.client.*;
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
        OctopusTask task = new OctopusDirectCallTask(getOctopusStarter());
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