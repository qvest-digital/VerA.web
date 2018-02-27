package de.tarent.aa.veraweb.utils.i18n;

/*-
 * Veranstaltungsmanagement VerA.web (platform-independent
 * webservice-based event management) is Copyright
 *  © 2014, 2015, 2016, 2017 Атанас Александров <a.alexandrov@tarent.de>
 *  © 2018 Атанас Александров <sirakov@gmail.com>
 *  © 2013 Иванка Александрова <i.alexandrova@tarent.de>
 *  © 2013 Patrick Apel <p.apel@tarent.de>
 *  © 2016 Eugen Auschew <e.auschew@tarent.de>
 *  © 2013 Andrei Boulgakov <a.boulgakov@tarent.de>
 *  © 2013 Valentin But <v.but@tarent.de>
 *  © 2016 Lukas Degener <l.degener@tarent.de>
 *  © 2017 Axel Dirla <a.dirla@tarent.de>
 *  © 2015 Julian Drawe <j.drawe@tarent.de>
 *  © 2014 Dominik George <d.george@tarent.de>
 *  © 2013 Sascha Girrulat <s.girrulat@tarent.de>
 *  © 2008 David Goemans <d.goemans@tarent.de>
 *  © 2015 Viktor Hamm <v.hamm@tarent.de>
 *  © 2013 Katja Hapke <k.hapke@tarent.de>
 *  © 2013 Hendrik Helwich <h.helwich@tarent.de>
 *  © 2007 jan <jan@evolvis.org>
 *  © 2005, 2006, 2007, 2008 Christoph Jerolimov <jerolimov@gmx.de>
 *  © 2008, 2009, 2010 Carsten Klein <c.klein@tarent.de>
 *  © 2014 Martin Ley <m.ley@tarent.de>
 *  © 2014, 2015 Max Marche <m.marche@tarent.de>
 *  © 2013, 2014, 2015, 2016, 2017, 2018 mirabilos <t.glaser@tarent.de>
 *  © 2016 Cristian Molina <c.molina@tarent.de>
 *  © 2017 Michael Nienhaus <m.nienhaus@tarent.de>
 *  © 2013 Claudia Nuessle <c.nuessle@tarent.de>
 *  © 2014, 2015 Jon Nunez Alvarez <j.nunez-alvarez@tarent.de>
 *  © 2016 Jens Oberender <j.oberender@tarent.de>
 *  © 2016, 2017 Miluška Pech <m.pech@tarent.de>
 *  © 2009 Martin Pelzer <m.pelzer@tarent.de>
 *  © 2013 Marc Radel <m.radel@tarent.de>
 *  © 2013 Sebastian Reimers <s.reimers@tarent.de>
 *  © 2015 Charbel Saliba <c.saliba@tarent.de>
 *  © 2008, 2009, 2010 Thomas Schmitz <t.schmitz@tarent.de>
 *  © 2013 Volker Schmitz <v.schmitz@tarent.de>
 *  © 2014 Sven Schumann <s.schumann@tarent.de>
 *  © 2014 Sevilay Temiz <s.temiz@tarent.de>
 *  © 2013 Kevin Viola Schmitz <k.schmitz@tarent.de>
 *  © 2015 Stefan Walenda <s.walenda@tarent.de>
 *  © 2015, 2016, 2017 Max Weierstall <m.weierstall@tarent.de>
 *  © 2013 Rebecca Weinz <r.weinz@tarent.de>
 *  © 2015, 2016 Stefan Weiz <s.weiz@tarent.de>
 *  © 2015, 2016 Tim Zimmer <t.zimmer@tarent.de>
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
import de.tarent.aa.veraweb.utils.LanguageHelper;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.beans.Request;
import de.tarent.octopus.beans.veraweb.RequestVeraWeb;
import de.tarent.octopus.server.OctopusContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

/**
 * @author jnunez Jon Nuñez, tarent solutions GmbH on 09.07.15.
 * @author sweiz
 */
public class LanguageProvider {
    /**
     * Load standard language data (german)
     */
    public LanguageProvider() {
        this.properties = loadProperties(STANDARD_LANG_FILE);
    }

    /**
     * Load language file into properties
     *
     * @param langFileName
     *            String name of language file
     */
    public LanguageProvider(final String langFileName) {
        this.properties = this.loadProperties(langFileName);
    }

    /**
     * Octopus-Eingabeparameter für die Aktion {@link #load(OctopusContext)}
     */
    public static final String INPUT_load[] = {};
    /**
     * Language file constants
     */
    public static final String STANDARD_LANG_FILE = "/etc/veraweb/l10n/de_DE.resource";

    public static final Logger LOGGER = LogManager.getLogger(LanguageProvider.class.getName());
    // Path of all language files
    private static final String FILE_PATH = "/etc/veraweb/l10n/";

    /**
     * Content of selected language file will be saved in Properties object
     */
    public Properties properties;
    // Map with all language names and their filename
    private Map<String, String> existingLanguagesAndFilenames = new TreeMap<String, String>();
    // Selected language will be persist, to load it on recall of controller
    private String lastSelectedLanguage = "";

    /**
     * Getter for loaded properties
     *
     * @return loaded properties
     */
    public Properties getProperties() {
        return this.properties;
    }

    /**
     * Get value from loaded property with key
     *
     * @param key
     *            String placeholder for translated text
     * @return value from key
     */
    public String getProperty(String key) {
        return this.properties.getProperty(key);
    }

    /**
     * Checks, if the properties object is null
     *
     * @return false if properties object is null, else true
     */
    public boolean propertiesAreAvailable() {
        return (this.properties != null);
    }

    /**
     * Load language names into Octopus Context
     *
     * @param octopusContext
     *            OctopusContext
     */
    public void load(OctopusContext octopusContext) {
        final Map<String, String> languageOptions = this.getLanguageOptions(octopusContext);
        octopusContext.setContent("translatedNames", languageOptions);

        final Request request = new RequestVeraWeb(octopusContext);

        try {
            if (request.getField("languageSelector") != null) {
                octopusContext.setSession("sessionLanguage", request.getField("languageSelector"));
            } else if (request.getField("languageSelector") == null && octopusContext.sessionAsString("sessionLanguage") == null) {
                octopusContext.setSession("sessionLanguage", "de_DE");
            }
        } catch (BeanException e) {
            // TODO NEVER
            e.printStackTrace();
        }

        this.loadTranslations(octopusContext);
    }

    /**
     * Getting translation by key
     *
     * @param langFileName
     *            String with full name of data with translations
     * @param key
     *            String with placeholder for translation
     * @return out String translated placeholder
     */
    public String getLocalizationValue(String langFileName, String key) {
        if (this.propertiesAreAvailable()) {
            return this.loadProperties(langFileName).getProperty(key);
        }

        return key;
    }

    // Load content of language file into properties
    private Properties loadProperties(final String langFileName0) {

        final Properties properties = new Properties();

        // work around some potential legacy weirdness: we make sure that
        // langFileName is actually *always* the locale-name + suffix.
        // No absolute path like /etc/yaddayadda or whatever
        final String langFileName = langFileName0.substring(langFileName0.lastIndexOf(File.separatorChar) + 1);

        final String resource = "/l10n/" + langFileName;
        final File file = new File(FILE_PATH, langFileName);

        // First, load defaults from classpath.
        Reader reader = null;
        try {
            reader = new InputStreamReader(getClass().getClassLoader().getResourceAsStream(resource), "utf-8");
            properties.load(reader);
        } catch (IOException uniqueLangFileException) {
            LOGGER.warn(uniqueLangFileException);
            LOGGER.warn("Could not read default language files!");
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (Exception closeFileException) {
                LOGGER.warn(closeFileException);
                LOGGER.warn("Could not close data!");
            }
        }
        // next, if override exists in /etc/veraweb/i10n, merge its contents
        if (file.canRead()) {
            Reader fileReader = null;
            try {
                fileReader = new InputStreamReader(new FileInputStream(file), "utf-8");
                properties.load(fileReader);
            } catch (IOException uniqueLangFileException) {
                LOGGER.warn(uniqueLangFileException);
                LOGGER.warn("Could not read language files! - " + file);
            } finally {
                try {
                    if (fileReader != null) {
                        fileReader.close();
                    }
                } catch (Exception closeFileException) {
                    LOGGER.warn(closeFileException);
                    LOGGER.warn("Could not close data! - " + file);
                }
            }
        }

        return properties;
    }

    private void loadTranslations(OctopusContext octopusContext) {

        String filename = octopusContext.sessionAsString("sessionLanguage") + ".resource";

        properties = new Properties();
        properties = this.loadProperties(filename);

        Map<String, String> placeholderWithTranslation = new HashMap<String, String>();
        for (final String key : properties.stringPropertyNames()) {
            placeholderWithTranslation.put(key, properties.getProperty(key));
        }
        octopusContext.setContent("language", octopusContext.sessionAsString("sessionLanguage"));
        octopusContext.setContent("placeholderWithTranslation", placeholderWithTranslation);
        octopusContext.setContent("helper", new LanguageHelper(placeholderWithTranslation));
        
    }

    // Set language names (language parameter of language data) from all
    // language data of the given directory
    private Map<String, String> getLanguageOptions(OctopusContext octopusContext) {
        existingLanguagesAndFilenames.clear();
        final List<String> propertyNames = getLanguageFileNames(octopusContext);

        final Map<String, String> languages = new HashMap<String, String>();
        for (String langFileName : propertyNames) {
            final String value = getLocalizationValue(langFileName, "language");
            languages.put(langFileName.substring(0, 5), value);
            existingLanguagesAndFilenames.put(value, langFileName);
        }

        return languages;
    }

    /**
     * Get list of files (only file names)
     * @param octopusContext  The {@link OctopusContext}
     *
     * @return list of file names
     */
    private List<String> getLanguageFileNames(OctopusContext octopusContext) {
        final List<String> localeNames = (List<String>) octopusContext.getConfigObject().getModuleConfig().getParams().get("availableTranslations");
        final List<String> languageFileNames = new ArrayList<String>();
        for(String localeName:localeNames){
            languageFileNames.add(localeName+".resource");
        }
        
        return languageFileNames;
    }

    public String getLastSelectedLanguage() {
        return lastSelectedLanguage;
    }

    public void setLastSelectedLanguage(String lastSelectedLanguage) {
        this.lastSelectedLanguage = lastSelectedLanguage;
    }
}
