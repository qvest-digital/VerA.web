package de.tarent.octopus.client.remote;

/*-
 * VerA.web-Middleware, newly MIT licenced, is comprised of:
 * tarent-commons, a set of common components and solutions
 * tarent-contact, platform-independent webservice-based contact management
 * tarent-database, jdbc database library
 * tarent-doctor, Document Generation Platform
 * tarent-octopus, Webservice Data Integrator and Application Server
 *  © 2018 Атанас Александров (sirakov@gmail.com)
 *  © 2005, 2006, 2007 asteban (s.mancke@tarent.de)
 *  © 2018 Dominik George (d.george@tarent.de)
 *  © 2007 David Goemans (d.goemans@tarent.de)
 *  © 2018 Christian Gorski (c.gorski@tarent.de)
 *  © 2006, 2007, 2010 Hendrik Helwich (h.helwich@tarent.de)
 *  © 2018 Benedict Hoeger (b.hoeger@tarent.de)
 *  © 2005, 2006, 2007 Christoph Jerolimov (c.jerolimov@tarent.de)
 *  © 2018 Timo Kanera (t.kanera@tarent.de)
 *  © 2006 Philipp Kirchner (p.kirchner@tarent.de)
 *  © 2010 Carsten Klein (c.klein@tarent.de)
 *  © 2006 Michael Kleinhenz (m.kleinhenz@tarent.de)
 *  © 2006 Michael Klink (m.klink@tarent.de)
 *  © 2007 Fabian Köster (f.koester@tarent.de)
 *  © 2006 Martin Ley (m.ley@tarent.de)
 *  © 2007 Alex Maier (a.maier@tarent.de)
 *  © 2007, 2015, 2017, 2018 mirabilos (t.glaser@tarent.de)
 *  © 2006, 2007 Jens Neumaier (j.neumaier@tarent.de)
 *  © 2006 Nils Neumaier (n.neumaier@tarent.de)
 *  © 2007, 2008 Martin Pelzer (m.pelzer@tarent.de)
 *  © 2008, 2009 Christian Preilowski (c.thiel@tarent.de)
 *  © 2006, 2008, 2009 Thomas Schmitz (t.schmitz@tarent.de)
 *  © 2007 Robert Schuster (r.schuster@tarent.de)
 * and older code, Copyright © 2001–2007 ⮡ tarent GmbH and contributors.
 * Licensor is tarent solutions GmbH, http://www.tarent.de/
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.xml.rpc.ServiceException;

import org.apache.commons.logging.Log;

import de.tarent.octopus.client.OctopusCallException;
import de.tarent.octopus.client.OctopusConnection;
import de.tarent.octopus.client.OctopusConstants;
import de.tarent.octopus.client.OctopusResult;
import de.tarent.octopus.client.OctopusTask;
import de.tarent.octopus.client.UserDataProvider;

/**
 * Implementierung einer OctopusConnection zu einem entfernt liegenden Octopus.
 *
 * @author <a href="mailto:sebastian@tarent.de">Sebastian Mancke</a>, <b>tarent GmbH</b>
 */
import lombok.extern.log4j.Log4j2;@Log4j2
public class OctopusRemoteConnection implements OctopusConnection {
    public static final String AUTH_TYPE = "authType";
    public static final String AUTH_TYPE_CALL_PARAM = "callParam";
    public static final String AUTH_TYPE_SESSION = "session";
    public static final String AUTH_TYPE_NONE = "none";

    // Default = true
    public static final String AUTO_LOGIN = "autoLogin";
    public static final String CONNECTION_TRACKING = "connectionTracking";
    public static final String USE_SESSION_COOKIE = "useSessionCookie";
    public static final String KEEP_SESSION_ALIVE = "keepSessionAlive";

    public static String PARAM_USERNAME = "username";
    public static String PARAM_PASSWORD = "password";
    public static String PARAM_SERVICE_URL = "serviceURL";

    public static String TASK_LOGIN = "login_SOAP";
    public static String TASK_LOGOUT = "logout_SOAP";
    public static String TASK_TEST_SESSION_STATUS = "testSessionStatus";

    String startServiceURL;
    String serviceURL;
    String moduleName;
    String username;
    String password;
    String authType;
    boolean autoLogin;
    boolean useSessionCookie;
    boolean connectionTracking = false;
    String sessionCookieFile;
    Integer keepSessionAlive;

    KeepAliveTimer keepAliveTimer;
    boolean isDoingLogin;
    UserDataProvider userDataProvider;

    /**
     * Liefert ein CallObject, dass fr den Aufruf dieses Task verwendet werden kann.
     */
    public OctopusTask getTask(String taskName)
      throws OctopusCallException {
        try {
            OctopusTask task = new OctopusRemoteTask(getModuleName(), taskName, this);
            if (AUTH_TYPE_CALL_PARAM.equals(getAuthType())) {
                task.add(PARAM_USERNAME, getUsername());
                task.add(PARAM_PASSWORD, getPassword());
            }

            return task;
        } catch (ServiceException se) {
            throw new OctopusCallException("Error on creating OctopusRemoteTask", se);
        }
    }

    public OctopusResult callTask(String taskName, Map paramMap)
      throws OctopusCallException {

        OctopusTask task = getTask(taskName);
        for (Iterator iter = paramMap.keySet().iterator(); iter.hasNext(); ) {
            String key = (String) iter.next();
            task.add(key, paramMap.get(key));
        }
        return task.invoke();
    }

    public void login()
      throws OctopusCallException {

        if (userDataProvider != null) {
            loginWithUserDataProvider();
        } else {
            setIsDoingLogin(true);
            OctopusResult res = getTask(TASK_LOGIN)
              .add(PARAM_USERNAME, getUsername())
              .add(PARAM_PASSWORD, getPassword())
              .invoke();

            Object newServiceURL = res.getData("url");
            if (!(newServiceURL instanceof String)) {
                setIsDoingLogin(false);
                throw new OctopusCallException(OctopusConstants.SERVER_ERROR_PREFIX, "No service url returned by login task.",
                  null);
            }
            serviceURL = (String) newServiceURL;
            if (useSessionCookie) {
                storeSessionCookie();
            }
            startKeepAliveTimer();
            setIsDoingLogin(false);
        }
    }

    public void loginWithUserDataProvider() {
        boolean hasValidLogin = false;
        String username = getUsername();
        String password = getPassword();
        boolean askForUserData = (null == username || null == password);

        OctopusResult res = null;
        while (!hasValidLogin) {

            if (askForUserData) {
                if (userDataProvider.requestUserData("Bitte Authentifizieren Sie sich.", username)) {
                    username = userDataProvider.getUsername();
                    password = userDataProvider.getPassword();
                } else {
                    throw new OctopusCallException(OctopusConstants.AUTHENTICATION_CANCELED, "No user data provided.", null);
                }
            }

            hasValidLogin = true;
            try {
                res = getTask(TASK_LOGIN)
                  .add(PARAM_USERNAME, username)
                  .add(PARAM_PASSWORD, password)
                  .invoke();
            } catch (OctopusCallException oce) {
                hasValidLogin = false;
                askForUserData = true;
                logger.info("Exception during login: " + oce.getErrorCode(), oce);
                // Hier wird im Moment immer weiter gemacht.
                // Wenn der Server aber einen sauberen Statuscode mitgibt,
                // muss nur bei Loginfehlern durch fehlerhalte Eingaben weiter gemacht werden.
            }
        }

        if (res == null) //kann eigentlich nicht vorkomen.
        {
            throw new OctopusCallException(OctopusConstants.SERVER_ERROR_PREFIX, "No result returned by login task.", null);
        }

        Object newServiceURL = res.getData("url");
        if (!(newServiceURL instanceof String)) {
            setIsDoingLogin(false);
            throw new OctopusCallException(OctopusConstants.SERVER_ERROR_PREFIX, "No service url returned by login task.", null);
        }
        setUsername(username);
        setPassword(password);
        serviceURL = (String) newServiceURL;
        if (useSessionCookie) {
            storeSessionCookie();
        }
        startKeepAliveTimer();
        setIsDoingLogin(false);
    }

    /**
     * Nimmt eine Session, die in einem Session-Cookie gespeichert war wieder auf.
     *
     * @return true, wenn eine gltige Session aufgenommen wurde, false sonst.
     */
    public boolean continueSession()
      throws OctopusCallException {

        if (!useSessionCookie) {
            return false;
        }

        setIsDoingLogin(true);
        String oldUsername = username;
        loadSessionCookie();

        if (serviceURL != null) {
            try {
                getTask(TASK_TEST_SESSION_STATUS).invoke();
                startKeepAliveTimer();
                setIsDoingLogin(false);

                return true;
            } catch (OctopusCallException e) {
            }
        }

        username = oldUsername;
        serviceURL = startServiceURL;
        setIsDoingLogin(false);
        return false;
    }

    protected void loadSessionCookie() {
        BufferedReader in = null;

        // If session cookie could not be loaded the caller expects username and
        // serviceURL to be null. However getSessionCookieFile() needs a non-null
        // serviceURL to work correctly.
        String tmpServiceURL = null;
        String tmpUsername = null;

        if (getSessionCookieFile() != null) {
            try {
                in = new BufferedReader(new FileReader(getSessionCookieFile()));
                tmpUsername = in.readLine();
                tmpServiceURL = in.readLine();
                in.close();
            } catch (FileNotFoundException fne) {
                logger.debug("Keine Octopus Sessiondatei gefunden unter <" + getSessionCookieFile() + ">", fne);
            } catch (Exception e) {
                logger.warn("Fehler beim Lesen eines Octopus Session Cookies aus <" + getSessionCookieFile() + ">", e);
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
        }

        username = tmpUsername;
        serviceURL = tmpServiceURL;
    }

    protected void storeSessionCookie() {
        PrintWriter out = null;
        try {
            out = new PrintWriter(new BufferedWriter(new FileWriter(getSessionCookieFile())));
            out.println(getUsername());
            out.println(getServiceURL());
            out.flush();
            out.close();
        } catch (IOException e) {
            logger.warn("Fehler beim Speichern eines Octopus Session Cookies in <" + getSessionCookieFile() + ">", e);
        } catch (RuntimeException e) {
            logger.warn("Fehler beim Speichern eines Octopus Session Cookies in <" + getSessionCookieFile() + ">", e);
            if (out != null) {
                out.close();
            }
        }
    }

    public void logout()
      throws OctopusCallException {

        getTask(TASK_LOGOUT)
          .invoke();
        serviceURL = startServiceURL;
    }

    protected void startKeepAliveTimer() {
        if (null != keepSessionAlive
          && keepSessionAlive.intValue() != 0
          && keepAliveTimer == null) {

            keepAliveTimer = new KeepAliveTimer(this, keepSessionAlive.intValue());
        }
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String newPassword) {
        this.password = newPassword;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String newUsername) {
        this.username = newUsername;
    }

    public void setUserDataProvider(UserDataProvider provider) {
        userDataProvider = provider;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String newModuleName) {
        this.moduleName = newModuleName;
    }

    public String getServiceURL() {
        return serviceURL;
    }

    public void setServiceURL(String newServiceURL) {
        this.serviceURL = newServiceURL;
        this.startServiceURL = newServiceURL;
    }

    public String getAuthType() {
        return authType;
    }

    public void setAuthType(String newAuthType) {
        this.authType = newAuthType;
    }

    public boolean isAutoLogin() {
        return autoLogin;
    }

    public void setAutoLogin(boolean newAutoLogin) {
        this.autoLogin = newAutoLogin;
    }

    public boolean isIsDoingLogin() {
        return isDoingLogin;
    }

    public void setIsDoingLogin(boolean newIsdoinglogin) {
        this.isDoingLogin = newIsdoinglogin;
    }

    public String getSessionCookieFile() {
        if (sessionCookieFile == null) {
            // Translates service URL into a proper file name by
            // replacing illegal characters with underscores.
            sessionCookieFile = System.getProperty("user.home")
              + File.separator
              + ".octopus_sessioncookie_"
              + startServiceURL.replaceAll("\\\\|:|/", "_");
        }

        return sessionCookieFile;
    }

    public boolean isUseSessionCookie() {
        return useSessionCookie;
    }

    public void setUseSessionCookie(boolean newUseSessionCookie) {
        this.useSessionCookie = newUseSessionCookie;
    }

    /**
     * @return Returns the connectionTracking.
     */
    public boolean isConnectionTracking() {
        return connectionTracking;
    }

    /**
     * @param connectionTracking The connectionTracking to set.
     */
    public void setConnectionTracking(boolean connectionTracking) {
        this.connectionTracking = connectionTracking;
    }

    public Integer getKeepSessionAlive() {
        return keepSessionAlive;
    }

    public void setKeepSessionAlive(Integer newKeepSessionAlive) {
        this.keepSessionAlive = newKeepSessionAlive;
    }

    /**
     * Klasse, die regelmig den Octopus anfragen kann, um die Session 'am Leben' zu halten.
     */
    class KeepAliveTimer extends TimerTask {
        OctopusRemoteConnection con;

        public KeepAliveTimer(OctopusRemoteConnection con, int delayMinutes) {
            this.con = con;
            Timer t = new Timer();
            int minutes = (60 * 1000 * delayMinutes);
            t.schedule(this, minutes, minutes);
        }

        public void run() {
            try {
                con.getTask(OctopusRemoteConnection.TASK_TEST_SESSION_STATUS).invoke();
            } catch (OctopusCallException e) {
                logger.warn("Error on keep alive requesr. Maybe the delay is to large.", e);
            }
        }
    }
}
