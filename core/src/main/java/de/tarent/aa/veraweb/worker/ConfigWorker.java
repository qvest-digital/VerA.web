package de.tarent.aa.veraweb.worker;

/*-
 * Veranstaltungsmanagement VerA.web (platform-independent
 * webservice-based event management) is Copyright
 *  © 2014, 2015, 2016, 2017 Атанас Александров (a.alexandrov@tarent.de)
 *  © 2018 Атанас Александров (sirakov@gmail.com)
 *  © 2013 Иванка Александрова (i.alexandrova@tarent.de)
 *  © 2013 Patrick Apel (p.apel@tarent.de)
 *  © 2016 Eugen Auschew (e.auschew@tarent.de)
 *  © 2013 Andrei Boulgakov (a.boulgakov@tarent.de)
 *  © 2013 Valentin But (v.but@tarent.de)
 *  © 2016 Lukas Degener (l.degener@tarent.de)
 *  © 2017 Axel Dirla (a.dirla@tarent.de)
 *  © 2015 Julian Drawe (j.drawe@tarent.de)
 *  © 2014 Dominik George (d.george@tarent.de)
 *  © 2013 Sascha Girrulat (s.girrulat@tarent.de)
 *  © 2008 David Goemans (d.goemans@tarent.de)
 *  © 2015 Viktor Hamm (v.hamm@tarent.de)
 *  © 2013 Katja Hapke (k.hapke@tarent.de)
 *  © 2013 Hendrik Helwich (h.helwich@tarent.de)
 *  © 2018 Thomas Hensel (t.hensel@tarent.de)
 *  © 2005, 2006, 2007, 2008 Christoph Jerolimov (jerolimov@gmx.de)
 *  © 2008, 2009, 2010 Carsten Klein (c.klein@tarent.de)
 *  © 2014 Martin Ley (m.ley@tarent.de)
 *  © 2014, 2015 Max Marche (m.marche@tarent.de)
 *  © 2007 Jan Meyer (jan@evolvis.org)
 *  © 2013, 2014, 2015, 2016, 2017, 2018 mirabilos (t.glaser@tarent.de)
 *  © 2016 Cristian Molina (c.molina@tarent.de)
 *  © 2017 Michael Nienhaus (m.nienhaus@tarent.de)
 *  © 2013 Claudia Nuessle (c.nuessle@tarent.de)
 *  © 2014, 2015 Jon Nunez Alvarez (j.nunez-alvarez@tarent.de)
 *  © 2016 Jens Oberender (j.oberender@tarent.de)
 *  © 2016, 2017 Miluška Pech (m.pech@tarent.de)
 *  © 2009 Martin Pelzer (m.pelzer@tarent.de)
 *  © 2013 Marc Radel (m.radel@tarent.de)
 *  © 2013 Sebastian Reimers (s.reimers@tarent.de)
 *  © 2015 Charbel Saliba (c.saliba@tarent.de)
 *  © 2008, 2009, 2010 Thomas Schmitz (t.schmitz@tarent.de)
 *  © 2013 Volker Schmitz (v.schmitz@tarent.de)
 *  © 2014 Sven Schumann (s.schumann@tarent.de)
 *  © 2014 Sevilay Temiz (s.temiz@tarent.de)
 *  © 2013 Kevin Viola Schmitz (k.schmitz@tarent.de)
 *  © 2015 Stefan Walenda (s.walenda@tarent.de)
 *  © 2015, 2016, 2017 Max Weierstall (m.weierstall@tarent.de)
 *  © 2013 Rebecca Weinz (r.weinz@tarent.de)
 *  © 2015, 2016 Stefan Weiz (s.weiz@tarent.de)
 *  © 2015, 2016 Tim Zimmer (t.zimmer@tarent.de)
 * and older code, Copyright © 2004–2008 ⮡ tarent GmbH and contributors.
 * Licensor is tarent solutions GmbH, http://www.tarent.de/
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see: http://www.gnu.org/licenses/
 */

import de.tarent.aa.veraweb.beans.Duration;
import de.tarent.aa.veraweb.utils.PropertiesReader;
import de.tarent.aa.veraweb.utils.URLGenerator;
import de.tarent.dblayer.sql.SQL;
import de.tarent.dblayer.sql.clause.Expr;
import de.tarent.dblayer.sql.statement.Delete;
import de.tarent.dblayer.sql.statement.Insert;
import de.tarent.dblayer.sql.statement.Update;
import de.tarent.octopus.PersonalConfigAA;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.beans.BeanFactory;
import de.tarent.octopus.beans.Database;
import de.tarent.octopus.beans.TransactionContext;
import de.tarent.octopus.beans.veraweb.ListWorkerVeraWeb;
import de.tarent.octopus.server.OctopusContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Dieser Worker stellt Hilfsfunktionen zur Verfügung um Einstellungen
 * aus der Datenbank auszulesen.
 *
 * @author Christoph Jerolimov
 * @version $Revision: 1.1 $
 */
public class ConfigWorker extends ListWorkerVeraWeb {
    private static final String defaultSource[] = {
      "LABEL_MEMBER_PRIVATE", "LABEL_MEMBER_BUSINESS", "LABEL_MEMBER_OTHER",
      "LABEL_MEMBER_LATIN", "LABEL_MEMBER_EXTRA1", "LABEL_MEMBER_EXTRA2",
      "LABEL_ADDRESS_SUFFIX1", "LABEL_ADDRESS_SUFFIX2", "CHANGE_LOG_RETENTION_POLICY" };
    private static final String defaultTarget[] = {
      "private", "business", "other",
      "latin", "extra1", "extra2",
      "suffix1", "suffix2", "changeLogRetentionPolicy" };
    private static final ResourceBundle defaultBundle =
      ResourceBundle.getBundle("de.tarent.aa.veraweb.config");

    private Map config;
    private boolean loaded = false;
    private final PropertiesReader propertiesReader = new PropertiesReader();

    final Logger LOGGER = LogManager.getLogger(ConfigWorker.class.getCanonicalName());

    //
    // Konstruktoren
    //

    /**
     * Der Konstruktor legt den Bean-Namen fest.
     */
    public ConfigWorker() {
        super("Config");
    }

    //
    // Octopus-Aktionen
    //
    /**
     * Input-Parameter der Octopus-Aktion {@link #init(OctopusContext)}
     */
    static public final String INPUT_init[] = {};

    /**
     * Diese Octopus-Aktion initialisiert die Map der Konfig-Einträge dieses
     * Workers aus der Datenbank (mittels der ererbten Aktion
     * {@link de.tarent.octopus.beans.BeanListWorker#getAll(OctopusContext)})
     * gegebenenfalls ergänzt um einige Muss-Einträge.
     *
     * @param cntx Octopus-Kontext
     */
    @SuppressWarnings("unchecked")
    private void init(OctopusContext cntx) throws BeanException, IOException {
        // config aus datenbank laden
        Map result = new HashMap();
        getAll(cntx);
        List list = (List) cntx.contentAsObject("all" + BEANNAME);
        for (Object aList : list) {
            Map entry = (Map) aList;
            result.put(entry.get("key"), entry.get("value"));
        }

        // default config aus properties laden
        for (int i = 0; i < defaultTarget.length; i++) {
            String value = (String) result.get(defaultTarget[i]);
            if (value == null || value.length() == 0) {
                if ("CHANGE_LOG_RETENTION_POLICY".compareTo(defaultSource[i]) == 0) {
                    result.put(defaultTarget[i], Duration.fromString(defaultBundle.getString(defaultSource[i])));
                } else {
                    result.put(defaultTarget[i], defaultBundle.getString(defaultSource[i]));
                }
            }
        }

        config = result;
        loaded = true;
    }

    /**
     * Input-Parameter der Octopus-Aktion {@link #load(OctopusContext)}
     */
    static public final String INPUT_load[] = {};

    /**
     * Diese Octopus-Aktion initialisiert die Map der Konfig-Einträge dieses
     * Workers mittels der Aktion {@link #init(OctopusContext)}, sofern dies
     * nicht schon zuvor geschehen ist, und setzt einen entsprechenden Eintrag
     * im Octopus-Content.
     *
     * @param cntx Octopus-Kontext
     * @throws BeanException FIXME
     * @throws IOException   FIXME
     */
    public void load(OctopusContext cntx) throws BeanException, IOException {
        if (!loaded) {
            init(cntx);
        }
        cntx.setContent("config", config);

        //FIXME: here is as good as anywhere else, I guess?
        cntx.setContent("url", new URLGenerator(propertiesReader.getProperties()));
    }

    /**
     * Input-Parameter der Octopus-Aktion {@link #clean(OctopusContext)}
     */
    static public final String INPUT_clean[] = {};

    /**
     * Diese Octopus-Aktion deinitialisiert die Map der Konfig-Einträge dieses
     * Workers.
     *
     * @param cntx Octopus-Kontext
     */
    public void clean(OctopusContext cntx) {
        config = null;
        loaded = false;
    }

    /**
     * Input-Parameter der Octopus-Aktion {@link #save(OctopusContext)}
     */
    static public final String INPUT_save[] = {};

    /**
     * Diese Octopus-Aktion speichert eine Liste von Konfigurationseinträgen aus dem
     * Octopus-Request (unter "saveconfig-*") in der Datenbank.
     *
     * @param cntx Octopus-Kontext
     * @throws BeanException FIXME
     * @throws IOException   FIXME
     * @throws SQLException  FIXME
     */
    public void save(OctopusContext cntx) throws BeanException, IOException, SQLException {
        if (!cntx.personalConfig().isUserInGroup(PersonalConfigAA.GROUP_ADMIN) || !cntx.requestContains("save")) {
            return;
        }

        List list = (List) BeanFactory.transform(cntx.requestAsObject("saveconfig"), List.class);
        for (Object aList : list) {
            String key = (String) aList;
            String value = cntx.requestAsString(key);
            saveValue(cntx, key, value);
        }
        cntx.setContent("saveSuccess", true);
    }

    //
    // Hilfsmethoden
    //
    private String getValue(String key) {
        return (String) config.get(key);
    }

    /*
     * modified to support duration handling for the new setting change log retention policy
     *
     * refactored a bit to minimize processing overhead, namely removed redundant second loop and
     * moved default/new/null value handling to the first loop
     *
     * database query getCount() will now be executed only if there is a value to be stored
     *
     * cklein
     * 2008-02-27
     */
    @SuppressWarnings("unchecked")
    private void saveValue(OctopusContext octopusContext, String key, String value)
      throws BeanException, IOException, SQLException {
        // wenn standard, dann null und default aus properties laden, sonst neuen wert in config hinterlegen
        boolean found = false;
        for (int i = 0; i < defaultTarget.length; i++) {
            if (defaultTarget[i].equals(key)) {
                found = true;
                if ("CHANGE_LOG_RETENTION_POLICY".compareTo(defaultSource[i]) == 0) {
                    Duration dnew = Duration.fromString(value);
                    Duration dold = Duration.fromString(defaultBundle.getString(defaultSource[i]));
                    if ((dold.toString().compareTo(dnew.toString()) == 0) || (dnew.toString().compareTo("P0") == 0)) {
                        value = null;
                        config.put(defaultTarget[i], Duration.fromString(defaultBundle.getString(defaultSource[i])));
                    } else {
                        config.put(defaultTarget[i], Duration.fromString(value));
                    }
                } else {
                    if (defaultBundle.getString(defaultSource[i]).equals(value)) {
                        value = null;
                        config.put(key, defaultBundle.getString(defaultSource[i]));
                    } else {
                        config.put(key, value);
                    }
                }
                break;
            }
        }
        if (!found) {
            // ist kein default konfigurationseintrag
            if ("".compareTo(value) == 0) {
                value = null;
                config.remove(key);
            } else {
                config.put(key, value);
            }
        }

        executeSaveSettings(octopusContext, key, value);
    }

    private void executeSaveSettings(OctopusContext octopusContext, String key, String value) throws BeanException, IOException {
        // einstellung in datenbank speichern
        final Database database = getDatabase(octopusContext);
        final TransactionContext transactionContext = database.getTransactionContext();
        if (value != null && value.length() != 0) {
            Integer count = database.getCount(
              database.getCount("Config").where(Expr.equal("cname", key))
            );

            if (count == 0) {
                insertConfigSettings(key, value, database, transactionContext);
            } else {
                updateConfigSettings(key, value, database, transactionContext);
            }
        } else {
            deleteConfigSettings(key, value, database, transactionContext);
        }
    }

    private void deleteConfigSettings(String key, String value, Database database, TransactionContext transactionContext)
      throws BeanException {
        LOGGER.debug(" -----------------------> BEGIN DELETE CONFIG " + key + "/" + value + " <----------------------- ");
        Delete delete = SQL.Delete(database);
        delete.from("veraweb.tconfig");
        delete.where(Expr.equal("cname", key));
        transactionContext.execute(delete);
        transactionContext.commit();
        LOGGER.debug(" -----------------------> DONE DELETE CONFIG " + key + "/" + value + " <----------------------- ");
    }

    private void updateConfigSettings(String key, String value, Database database, TransactionContext transactionContext)
      throws BeanException {
        LOGGER.debug("-----------------------> BEGIN UPDATE CONFIG " + key + "/" + value + " <----------------------- ");
        Update update = SQL.Update(database);
        update.table("veraweb.tconfig");
        update.update("cvalue", value);
        update.where(Expr.equal("cname", key));
        transactionContext.execute(update);
        transactionContext.commit();
        LOGGER.debug(" -----------------------> DONE UPDATE CONFIG " + key + "/" + value + " <----------------------- ");
    }

    private void insertConfigSettings(String key, String value, Database database, TransactionContext transactionContext)
      throws BeanException {
        LOGGER.debug(" -----------------------> BEGIN INSERT CONFIG " + key + "/" + value + " <----------------------- ");
        Insert insert = SQL.Insert(database);
        insert.table("veraweb.tconfig");
        insert.insert("cname", key);
        insert.insert("cvalue", value);
        transactionContext.execute(insert);
        transactionContext.commit();
        LOGGER.debug(" -----------------------> DONE INSERT CONFIG " + key + "/" + value + " <----------------------- ");
    }

    /**
     * Gibt eine Config-Einstellung zurück.
     *
     * @param cntx Octopus-Kontext
     * @param key  Name hinter dem die Einstellung hinterlegt sein soll.
     * @return Der Wert der Einstellung oder null.
     */
    public static String getString(OctopusContext cntx, String key) {
        return WorkerFactory.getConfigWorker(cntx).getValue(key);
    }

    /**
     * Gibt eine Config-Einstellung zurück, falls dieser nicht zu einer
     * Zahl transformiert werden kann wird null zurückgegeben.
     *
     * @param cntx Octopus-Kontext
     * @param key  Name hinter dem die Einstellung hinterlegt sein soll.
     * @return Der Wert der Einstellung oder null.
     */
    public static Integer getInteger(OctopusContext cntx, String key) {
        try {
            return new Integer(getString(cntx, key));
        } catch (NullPointerException | NumberFormatException e) {
            return null;
        }
    }
}
