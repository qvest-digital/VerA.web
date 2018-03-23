package de.tarent.octopus.security;

/*-
 * tarent-octopus, Webservice Data Integrator and Application Server
 * Copyright © 2002–2018 tarent solutions GmbH and its contributors
 * Copyright © 2015, 2017, 2018 mirabilos (t.glaser@tarent.de)
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

import de.tarent.octopus.config.TcCommonConfig;
import de.tarent.octopus.logging.LogFactory;
import de.tarent.octopus.request.TcEnv;
import de.tarent.octopus.request.TcRequest;
import de.tarent.octopus.security.ldap.TcSecurityLDAPManager;
import de.tarent.octopus.server.PersonalConfig;
import org.apache.commons.logging.Log;

import java.net.PasswordAuthentication;
import java.util.Map;

/**
 * Implementierung eines LoginManagers, über LDAP
 *
 * @author <a href="mailto:mancke@mancke-software.de">Sebastian Mancke</a>, <b>tarent GmbH</b>
 * @deprecated moved to LDAPLib, will be deleted as soon as 2005-06-01
 */
public class LoginManagerLDAP extends AbstractLoginManager {
	private Log logger = LogFactory.getLog(this.getClass());

	protected void doLogin(TcCommonConfig commonConfig, PersonalConfig pConfig, TcRequest tcRequest)
	    throws TcSecurityException {
		logger.warn("Die Klasse " + this.getClass().getName() +
		    " soll nicht mehr verwendet werden, sie wird nicht mehr gepflegt! Stattdessen muss das Jar LDAPLib.jar im Classpath liegen, dann kann die Klasse de.tarent.ldap.LoginManagerLDAP genutzt werden.");
		// TODO:
		// Eigentlich soll die Cofiguration
		// dieses Loginmanagers aus der ModuleConfig kommen
		//         TcSecurityLDAPManager tcslm =
		//             new TcSecurityLDAPManager(
		//                                       getConfigurationString(TcEnv.KEY_LDAP_URL),
		//                                       getConfigurationString(TcEnv.KEY_LDAP_BASE_DN),
		//                                       getConfigurationString(TcEnv.KEY_LDAP_RELATIVE));

		TcSecurityLDAPManager tcslm =
		    new TcSecurityLDAPManager(
			commonConfig.getConfigData(TcEnv.KEY_LDAP_URL),
			commonConfig.getConfigData(TcEnv.KEY_LDAP_BASE_DN),
			commonConfig.getConfigData(TcEnv.KEY_LDAP_RELATIVE));

		PasswordAuthentication pwdAuth = tcRequest.getPasswordAuthentication();
		if (pwdAuth == null)
			throw new TcSecurityException(TcSecurityException.ERROR_AUTH_ERROR);
		// TODO
		// Eigentlich soll die Cofiguration
		// dieses Loginmanagers aus der ModuleConfig kommen
		//tcslm.login(userName, true, tcRequest.get(PARAM_PASSWORD), getConfigurationString(TcEnv.KEY_LDAP_AUTHORIZATION));
		tcslm.login(pwdAuth.getUserName(), true, new String(pwdAuth.getPassword()),
		    commonConfig.getConfigData(TcEnv.KEY_LDAP_AUTHORIZATION));
		if (!tcslm.checkuid(pwdAuth.getUserName())) {
			throw new TcSecurityException(TcSecurityException.ERROR_AUTH_ERROR);
		}
		Map userdata = tcslm.getUserData(pwdAuth.getUserName());

		pConfig.setUserLastName((String)userdata.get("nachname"));
		pConfig.setUserGivenName((String)userdata.get("vorname"));
		pConfig.setUserEmail((String)userdata.get("mail"));

		// TODO: Erweiterung der LDAP Daten auf Groups
		String adminflag = (String)userdata.get("adminflag");
		if (adminflag == "TRUE")
			pConfig.setUserGroups(new String[] { PersonalConfig.GROUP_ADMINISTRATOR });
		else
			pConfig.setUserGroups(new String[] { PersonalConfig.GROUP_USER });

		pConfig.userLoggedIn(pwdAuth.getUserName());
	}

	protected void doLogout(TcCommonConfig commonConfig, PersonalConfig pConfig, TcRequest tcRequest)
	    throws TcSecurityException {
		pConfig.setUserGroups(new String[] { PersonalConfig.GROUP_LOGGED_OUT });
		pConfig.userLoggedOut();
	}
}
