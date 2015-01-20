/* $Id: AbstractLoginManager.java,v 1.3 2006/02/23 15:07:57 christoph Exp $
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

package de.tarent.octopus.security;

import java.net.PasswordAuthentication;
import java.util.HashMap;
import java.util.Map;

import de.tarent.octopus.config.TcCommonConfig;
import de.tarent.octopus.config.TcConfigException;
import de.tarent.octopus.request.TcRequest;
import de.tarent.octopus.request.TcSession;
import de.tarent.octopus.request.TcTask;
import de.tarent.octopus.request.TcTaskList;
import de.tarent.octopus.server.LoginManager;
import de.tarent.octopus.server.PersonalConfig;
import de.tarent.octopus.server.UserManager;

/** 
 * Abstrakte Implementierung eines LoginManagers unter verwendeung des Template-Method-Pattern
 * <br><br>
 * 
 * @author <a href="mailto:mancke@mancke-software.de">Sebastian Mancke</a>, <b>tarent GmbH</b>
 */
public abstract class AbstractLoginManager implements LoginManager {

    public static String PREFIX_PERSONAL_CONFIGS = "personalConfig-";
    
    protected Map configuration = new HashMap();

    public void setConfiguration(Map configuration) {
        assert configuration != null;
        this.configuration = configuration;
    }

    protected Object getConfigurationParam(Object key) {
        return (key != null) ? configuration.get(key) : null;
    }

    protected String getConfigurationString(Object key) {
        Object value = getConfigurationParam(key);
        return (value != null) ? value.toString() : null;
    }


    public void handleAuthentication(TcCommonConfig config, TcRequest tcRequest, TcSession theSession)
        throws TcSecurityException {
        
        PersonalConfig pConfig = getPersonalConfig(config, tcRequest, theSession);
        boolean wasNew = false;
        if (pConfig == null) {
            wasNew = true;
            try {
                pConfig = config.createNewPersonalConfig(tcRequest.getModule());
            } catch (TcConfigException e) {
                throw new TcSecurityException(e.getMessage(), e.getCause());
            }
            theSession.setAttribute(PREFIX_PERSONAL_CONFIGS+tcRequest.getModule(),
                                    pConfig);
        } 
        else if (pConfig.isUserInGroup(PersonalConfig.GROUP_LOGGED_OUT))
            pConfig.setUserGroups(new String[]{PersonalConfig.GROUP_ANONYMOUS});
                 
        String task = tcRequest.getTask();
        PasswordAuthentication pwdAuth = tcRequest.getPasswordAuthentication();
        if (TASK_LOGIN.equals(tcRequest.get(task))
            || TASK_LOGIN_SOAP.equals(task)
            || pwdAuth != null) {
            
            if (pwdAuth == null)                
                throw new TcSecurityException(TcSecurityException.ERROR_INCOMPLETE_USER_DATA);
            
            doLogin(config, pConfig, tcRequest);
        } 

        // Wenn nicht eingeloggt wird
        // und eine neue Session gestartet wurde
        // und das Task nicht für Anonymous freigegeben ist
        // ==> Meldung, dass die Session unültig ist
        else if (wasNew) {
            TcTaskList taskList = config.getTaskList(tcRequest.getModule());
            TcTask t = taskList.getTask(tcRequest.getTask());
            String[] taskGroups = t.getGroups();            
            
            if (!arrayContains(taskGroups, PersonalConfig.GROUP_ANONYMOUS)) {
                throw new TcSecurityException(TcSecurityException.ERROR_NO_VALID_SESSION);
            }
        }
        
        if (TASK_LOGOUT.equals(task) || TASK_LOGOUT_SOAP.equals(task)) {
            doLogout(config, pConfig, tcRequest);
        }
    }

    /**
     * Template-Method Pattern
     */
    protected abstract void doLogin(TcCommonConfig config, PersonalConfig pConfig, TcRequest tcRequest)
        throws TcSecurityException;

    /**
     * Template-Method Pattern
     */
    protected abstract void doLogout(TcCommonConfig config, PersonalConfig pConfig, TcRequest tcRequest)
        throws TcSecurityException;


    public PersonalConfig getPersonalConfig(TcCommonConfig config, TcRequest tcRequest, TcSession theSession) {
        return (PersonalConfig)theSession.getAttribute(PREFIX_PERSONAL_CONFIGS+tcRequest.getModule());
    }

	/* (non-Javadoc)
	 * @see de.tarent.octopus.server.LoginManager#isUserManagementSupported()
	 */
	public boolean isUserManagementSupported() {
		return false;
	}

	/* (non-Javadoc)
	 * @see de.tarent.octopus.server.LoginManager#getUserManager()
	 */
	public UserManager getUserManager() {
		return null;
	}

	protected boolean arrayContains(String[] array, String item) {
        for (int i = 0; i < array.length; i++)
            if (array[i].equals(item))
                return true;
        return false;
    }
    
}