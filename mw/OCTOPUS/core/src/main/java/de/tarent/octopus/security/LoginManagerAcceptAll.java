package de.tarent.octopus.security;
import java.net.PasswordAuthentication;

import de.tarent.octopus.config.TcCommonConfig;
import de.tarent.octopus.request.TcRequest;
import de.tarent.octopus.server.PersonalConfig;

/**
 * Implementierung eines LoginManagers, die alle User immer akzeptiert, unabhängig davon,
 * ob sie existieren oder ihr Passwort gültig ist. Die User landen alle in der DEFAULT_GROUP.
 * Diese könnte später konfigurierbar sein.
 * <br><br>
 * a
 *
 * @author <a href="mailto:mancke@mancke-software.de">Sebastian Mancke</a>, <b>tarent GmbH</b>
 */
public class LoginManagerAcceptAll extends AbstractLoginManager {

    public static final String DEFAULT_GROUP = PersonalConfig.GROUP_USER;

    protected void doLogin(TcCommonConfig commonConfig, PersonalConfig pConfig, TcRequest tcRequest)
      throws TcSecurityException {
        PasswordAuthentication pwdAuth = tcRequest.getPasswordAuthentication();
        pConfig.setUserGroups(new String[] { DEFAULT_GROUP });
        pConfig.userLoggedIn(pwdAuth != null ? pwdAuth.getUserName() : "?");
    }

    protected void doLogout(TcCommonConfig commonConfig, PersonalConfig pConfig, TcRequest tcRequest)
      throws TcSecurityException {
        pConfig.setUserGroups(new String[] { PersonalConfig.GROUP_LOGGED_OUT });
        pConfig.userLoggedOut();
    }
}
