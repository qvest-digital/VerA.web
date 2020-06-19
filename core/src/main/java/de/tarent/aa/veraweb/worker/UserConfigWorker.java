package de.tarent.aa.veraweb.worker;

/*-
 * Veranstaltungsmanagement VerA.web, comprised of…
 * VerA.web, platform-independent webservice-based event management
 * tarent-commons, a set of common components and solutions
 * tarent-contact, platform-independent webservice-based contact management
 * tarent-database, jdbc database library
 * tarent-doctor, Document Generation Platform
 * tarent-octopus, Webservice Data Integrator and Application Server
 * … is newly MIT-licenced and Copyright
 *  © 2018 Атанас Александров (sirakov@gmail.com)
 *  © 2014, 2015, 2016, 2017 Атанас Александров (a.alexandrov@tarent.de)
 *  © 2013 Иванка Александрова (i.alexandrova@tarent.de)
 *  © 2005, 2006, 2007 asteban (s.mancke@tarent.de)
 *  © 2013 Patrick Apel (p.apel@tarent.de)
 *  © 2016 Eugen Auschew (e.auschew@tarent.de)
 *  © 2013 Andrei Boulgakov (a.boulgakov@tarent.de)
 *  © 2013 Valentin But (v.but@tarent.de)
 *  © 2016 Lukas Degener (l.degener@tarent.de)
 *  © 2017 Axel Dirla (a.dirla@tarent.de)
 *  © 2015 Julian Drawe (j.drawe@tarent.de)
 *  © 2009 Sven Frommeyer (s.frommeyer@tarent.de)
 *  © 2014, 2018 Dominik George (d.george@tarent.de)
 *  © 2013 Martin Gernhardt (m.gernhardt@tarent.de)
 *  © 2013 Sascha Girrulat (s.girrulat@tarent.de)
 *  © 2007, 2008 David Goemans (d.goemans@tarent.de)
 *  © 2018 Christian Gorski (c.gorski@tarent.de)
 *  © 2015 Viktor Hamm (v.hamm@tarent.de)
 *  © 2013 Katja Hapke (k.hapke@tarent.de)
 *  © 2006, 2007, 2010, 2013 Hendrik Helwich (h.helwich@tarent.de)
 *  © 2018 Thomas Hensel (t.hensel@tarent.de)
 *  © 2018, 2019 Benedict Hoeger (b.hoeger@tarent.de)
 *  © 2018, 2019 Titian Horvath (t.horvath@tarent.de)
 *  © 2005, 2006, 2007, 2008 Christoph Jerolimov (jerolimov@gmx.de)
 *  © 2018, 2019 Timo Kanera (t.kanera@tarent.de)
 *  © 2006 Philipp Kirchner (p.kirchner@tarent.de)
 *  © 2008, 2009, 2010 Carsten Klein (c.klein@tarent.de)
 *  © 2006 Michael Kleinhenz (m.kleinhenz@tarent.de)
 *  © 2006 Michael Klink (m.klink@tarent.de)
 *  © 2007 Fabian Köster (f.koester@tarent.de)
 *  © 2006, 2014 Martin Ley (m.ley@tarent.de)
 *  © 2007 Alex Maier (a.maier@tarent.de)
 *  © 2014, 2015 Max Marche (m.marche@tarent.de)
 *  © 2007 Jan Meyer (jan@evolvis.org)
 *  © 2007, 2013, 2014, 2015, 2016, 2017, 2018, 2019, 2020
 *     mirabilos (t.glaser@tarent.de)
 *  © 2016 Cristian Molina (c.molina@tarent.de)
 *  © 2006, 2007 Jens Neumaier (j.neumaier@tarent.de)
 *  © 2006 Nils Neumaier (n.neumaier@tarent.de)
 *  © 2018 Yorka Neumann (y.neumann@tarent.de)
 *  © 2017 Michael Nienhaus (m.nienhaus@tarent.de)
 *  © 2013 Claudia Nuessle (c.nuessle@tarent.de)
 *  © 2014, 2015 Jon Nuñez Alvarez (j.nunez-alvarez@tarent.de)
 *  © 2016 Jens Oberender (j.oberender@tarent.de)
 *  © 2016, 2017 Miluška Pech (m.pech@tarent.de)
 *  © 2007, 2008, 2009 Martin Pelzer (m.pelzer@tarent.de)
 *  © 2008, 2009 Christian Preilowski (c.thiel@tarent.de)
 *  © 2013 Marc Radel (m.radel@tarent.de)
 *  © 2013 Sebastian Reimers (s.reimers@tarent.de)
 *  © 2015 Charbel Saliba (c.saliba@tarent.de)
 *  © 2006, 2008, 2009, 2010 Thomas Schmitz (t.schmitz@tarent.de)
 *  © 2013 Volker Schmitz (v.schmitz@tarent.de)
 *  © 2014 Sven Schumann (s.schumann@tarent.de)
 *  © 2007 Robert Schuster (r.schuster@tarent.de)
 *  © 2014 Sevilay Temiz (s.temiz@tarent.de)
 *  © 2013 Kevin Viola Schmitz (k.schmitz@tarent.de)
 *  © 2008, 2015 Stefan Walenda (s.walenda@tarent.de)
 *  © 2015, 2016, 2017 Max Weierstall (m.weierstall@tarent.de)
 *  © 2013 Rebecca Weinz (r.weinz@tarent.de)
 *  © 2015, 2016 Stefan Weiz (s.weiz@tarent.de)
 *  © 2015, 2016 Tim Zimmer (t.zimmer@tarent.de)
 * and older code, Copyright © 2001–2008 ⮡ tarent GmbH and contributors.
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

import de.tarent.aa.veraweb.beans.OrgUnit;
import de.tarent.aa.veraweb.beans.User;
import de.tarent.aa.veraweb.beans.UserConfig;
import de.tarent.aa.veraweb.beans.ViewConfigKey;
import de.tarent.aa.veraweb.utils.LocaleMessage;
import de.tarent.aa.veraweb.utils.VworUtils;
import de.tarent.dblayer.sql.clause.Expr;
import de.tarent.dblayer.sql.clause.Where;
import de.tarent.dblayer.sql.statement.Select;
import de.tarent.octopus.PersonalConfigAA;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.beans.Database;
import de.tarent.octopus.beans.TransactionContext;
import de.tarent.octopus.beans.veraweb.DatabaseVeraWeb;
import de.tarent.octopus.server.OctopusContext;
import org.evolvis.veraweb.common.RestPaths;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Dieser Octopus-Worker stellt Aktionen zum laden und speichern
 * von Benutzereinstellungen zur Verfügung.
 *
 * @author Christoph Jerolimov
 * @version $Revision: 1.1 $
 */
public class UserConfigWorker {
    public static final String PARAMS_STRING[] = {
      "personTab", "personMemberTab",
      "personAddresstypeTab", "personLocaleTab" };

    /**
     * Octopus-Eingabe-Parameter für {@link #init(OctopusContext)}
     */
    public static final String INPUT_init[] = {};
    /**
     * Octopus-Ausgabe-Parameter für {@link #init(OctopusContext)}
     */
    public static final String OUTPUT_init = "userConfig";

    /**
     * Lädt die Konfiguration aus der Datenbank in die Session.
     *
     * @param octopusContext The {@link OctopusContext}
     * @throws BeanException BeanException
     * @throws IOException   IOException
     */
    public Map init(OctopusContext octopusContext) throws BeanException, IOException {
        Database database = new DatabaseVeraWeb(octopusContext);
        Integer userId = ((PersonalConfigAA) octopusContext.personalConfig()).getVerawebId();
        if (userId == null) {
            return null;
        }

        Select select = database.getSelect("UserConfig");
        select.where(Expr.equal("fk_user", userId));

        Map result = new HashMap();
        for (Iterator it = database.getList(select, database).iterator(); it.hasNext(); ) {
            Map data = (Map) it.next();
            result.put(data.get("key"), data.get("value"));
        }

        /* check if REST API is available and what version it reports to be */
        VworUtils vworUtils = new VworUtils();
        String vworAvailable;
        try {
            vworAvailable = vworUtils.readResource(vworUtils.path(RestPaths.REST_HEALTH_AVAILABLE));
            String vworVersion = null;
            if ("OK".equals(vworAvailable)) {
                try {
                    vworVersion = vworUtils.readResource(vworUtils.path(RestPaths.REST_INFO));
                } catch (Exception e) {
                    vworVersion = null;
                }
            }
            if (vworVersion != null) {
                vworAvailable = vworAvailable + " (" + vworVersion + ")";
            }
            vworAvailable = LocaleMessage.formatTextToHtmlString(vworAvailable);
        } catch (Exception e) {
            StringBuilder sb = new StringBuilder();
            final String emsg = e.getMessage();
            final String elmsg = e.getLocalizedMessage();

            if (emsg != null && !emsg.equals(elmsg)) {
                sb.append(LocaleMessage.formatTextToHtmlString(e.toString() + " (" + emsg + ")"));
            } else {
                sb.append(LocaleMessage.formatTextToHtmlString(e.toString()));
            }

            for (StackTraceElement element : e.getStackTrace()) {
                sb.append("<br />");
                sb.append(LocaleMessage.formatTextToHtmlString(element.toString()));
            }
            vworAvailable = sb.toString();
        }
        result.put("restApiAvail", vworAvailable);

        /*
         * modified to support display of orgunit as per change request for version 1.2.0
         *
         * cklein
         * 2008-02-15
         */
        User user = (User) database.getBean("User", userId);
        OrgUnit orgUnit = new OrgUnit();

        if (user != null && user.orgunit != null && user.orgunit.intValue() != 0) {
            orgUnit = (OrgUnit) database.getBean("OrgUnit", user.orgunit);
        }

        completeUserViewConfigEntries(database, userId, octopusContext, result);

        octopusContext.setContent("orgUnit", orgUnit);
        octopusContext.setSession("userConfig", result);
        return result;
    }

    /**
     * Checks view config property entries in user config. Settings of common config will be used for missing entries
     * in user config and persisted
     *
     * @param database       database
     * @param userId         id of current user
     * @param octopusContext the context
     * @param result         user
     * @throws IOException
     * @throws BeanException
     */
    private void completeUserViewConfigEntries(Database database, Integer userId, OctopusContext octopusContext, Map result)
      throws IOException, BeanException {
        Map config = (Map) octopusContext.contentAsObject("config");
        for (ViewConfigKey viewConfigKey : ViewConfigKey.values()) {
            if (!result.containsKey(viewConfigKey.key)) {
                String value;
                if (config.containsKey(viewConfigKey.key)) {
                    value = (String) config.get(viewConfigKey.key);
                } else {
                    value = viewConfigKey.defaultValue;
                }
                insertNewUserConfigEntry(database, userId, viewConfigKey.key, value);
                result.put(viewConfigKey.key, value);
            }
        }
    }

    /**
     * Octopus-Eingabe-Parameter für {@link #load(OctopusContext)}
     */
    public static final String INPUT_load[] = {};
    /**
     * Octopus-Ausgabe-Parameter für {@link #load(OctopusContext)}
     */
    public static final String OUTPUT_load = "userConfig";

    /**
     * Lädt die Konfiguration aus der Session in den Content.
     *
     * @param octopusContext The {@link OctopusContext}
     * @throws BeanException BeanException
     * @throws IOException   IOException
     */
    public Map load(OctopusContext octopusContext) throws BeanException, IOException {
        Map result = (Map) octopusContext.sessionAsObject("userConfig");
        if (result == null) {
            return init(octopusContext);
        }
        return result;
    }

    /**
     * Octopus-Eingabe-Parameter für {@link #save(OctopusContext)}
     */
    public static final String INPUT_save[] = {};

    /**
     * Speichert die Benutzer Einstellungen in der Datenbank.
     *
     * @param octopusContext The {@link OctopusContext}
     * @throws BeanException BeanException
     * @throws IOException   IOException
     */
    public void save(OctopusContext octopusContext) throws BeanException, IOException {
        // Bug 5
        if (octopusContext.requestContains("save-columns")) {
            saveColumnsConfig(octopusContext);
            return;
        }
        if (!octopusContext.requestContains("save")) {
            return;
        }

        Database database = new DatabaseVeraWeb(octopusContext);
        Integer userId = ((PersonalConfigAA) octopusContext.personalConfig()).getVerawebId();
        Map userConfig = (Map) octopusContext.contentAsObject("userConfig");
        boolean configChanged = false;

        for (int i = 0; i < PARAMS_STRING.length; i++) {
            String key = PARAMS_STRING[i];
            String value = octopusContext.requestAsString(key);

            if (value == null) {
                continue;
            }
            configChanged = setUserSetting(database, userId, userConfig, key, value) || configChanged;
        }

        // Bug 5
        for (ViewConfigKey viewConfigKey : ViewConfigKey.values()) {
            String key = viewConfigKey.key;
            boolean value = octopusContext.requestAsBoolean(key).booleanValue();
            String type = octopusContext.getRequestObject().getTask();

            if (type.equals("UserConfig")) {
                configChanged = setUserSetting(database, userId, userConfig, key, Boolean.toString(value)) || configChanged;
            }
        }

        if (configChanged) {
            octopusContext.setContent("saveSuccess", true);
        }
    }

    private void saveColumnsConfig(OctopusContext octopusContext) throws BeanException, IOException {
        Database database = new DatabaseVeraWeb(octopusContext);
        Integer userId = ((PersonalConfigAA) octopusContext.personalConfig()).getVerawebId();
        Map userConfig = (Map) octopusContext.contentAsObject("userConfig");
        boolean configChanged = false;

        for (ViewConfigKey viewConfigKey : ViewConfigKey.values()) {
            String key = viewConfigKey.key;
            boolean value = octopusContext.requestAsBoolean(key).booleanValue();
            String type = octopusContext.getRequestObject().getTask();
            switch (type) {
            case "SearchPerson":
            case "PersonDuplicateSearch":
                if (key.contains("person")) {
                    configChanged = setUserSetting(database, userId, userConfig, key, Boolean.toString(value)) || configChanged;
                }
                break;
            case "ShowGuestList":
                if (key.contains("guest")) {
                    configChanged = setUserSetting(database, userId, userConfig, key, Boolean.toString(value)) || configChanged;
                }
                break;
            }
        }
        if (configChanged) {
            octopusContext.setContent("saveSuccess", true);
        }
    }

    protected void removeUserSetting(Database database, Integer userId, Map userConfig, String key)
      throws BeanException, IOException {
        String old = (String) userConfig.get(key);
        if (old != null) {

            final TransactionContext transactionContext = database.getTransactionContext();
            transactionContext.execute(database.getDelete("UserConfig").
              where(Where.and(
                Expr.equal("fk_user", userId),
                Expr.equal("name", key))));
            transactionContext.commit();

            userConfig.remove(key);
        }
    }

    protected boolean setUserSetting(Database database, Integer userId, Map userConfig, String key, String value)
      throws BeanException, IOException {
        String old = (String) userConfig.get(key);
        if (value == null && old != null) {
            removeUserSetting(database, userId, userConfig, key);
            return true;
        } else if (value != null && old == null) {
            insertNewUserConfigEntry(database, userId, key, value);
            userConfig.put(key, value);
            return true;
        } else if (!value.equals(old)) {
            updateUserConfigEntry(database, userId, key, value);
            userConfig.put(key, value);
            return true;
        }
        return false;
    }

    private void updateUserConfigEntry(Database database, Integer userId, String key, String value)
      throws BeanException, IOException {
        final TransactionContext transactionContext = database.getTransactionContext();
        transactionContext.execute(database.getUpdate("UserConfig").
          update("value", value).
          where(Where.and(
            Expr.equal("fk_user", userId),
            Expr.equal("name", key))));
        transactionContext.commit();
    }

    private void insertNewUserConfigEntry(Database database, Integer userId, String key, String value)
      throws BeanException, IOException {
        final TransactionContext transactionContext = database.getTransactionContext();
        UserConfig config = new UserConfig();
        config.user = userId;
        config.key = key;
        config.value = value;
        database.saveBean(config, transactionContext, true);
        transactionContext.commit();
    }
}
