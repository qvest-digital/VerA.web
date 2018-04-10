package de.tarent.octopus.security;
import java.net.PasswordAuthentication;

import de.tarent.octopus.config.TcCommonConfig;
import de.tarent.octopus.request.TcRequest;
import de.tarent.octopus.server.PersonalConfig;

/**
 * Implementierung eines LoginManagers
 * <br><br>
 *
 * @author <a href="mailto:mancke@mancke-software.de">Sebastian Mancke</a>, <b>tarent GmbH</b>
 */
public class LoginManagerFilter extends AbstractLoginManager {

    protected void doLogin(TcCommonConfig commonConfig, PersonalConfig pConfig, TcRequest tcRequest)
        throws TcSecurityException {

        PasswordAuthentication pwdAuth = tcRequest.getPasswordAuthentication();
        if (pwdAuth == null || pwdAuth.getUserName() == null || pwdAuth.getUserName().length() == 0)
            throw new TcSecurityException(TcSecurityException.ERROR_AUTH_ERROR);

        pConfig.setUserGroups(new String[]{PersonalConfig.GROUP_USER});
        pConfig.userLoggedIn(pwdAuth.getUserName());
    }

    protected void doLogout(TcCommonConfig commonConfig, PersonalConfig pConfig, TcRequest tcRequest)
        throws TcSecurityException {
        pConfig.setUserGroups(new String[]{PersonalConfig.GROUP_LOGGED_OUT});
        pConfig.userLoggedOut();
    }

}
