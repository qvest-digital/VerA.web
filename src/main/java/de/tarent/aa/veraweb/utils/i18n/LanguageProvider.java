/**
 * veraweb, platform independent webservice-based event management
 * (Veranstaltungsmanagment VerA.web), is
 * Copyright © 2004–2008 tarent GmbH
 * Copyright © 2013–2015 tarent solutions GmbH
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
package de.tarent.aa.veraweb.utils.i18n;

import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.beans.Request;
import de.tarent.octopus.beans.veraweb.RequestVeraWeb;
import de.tarent.octopus.server.OctopusContext;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

/**
 * @author jnunez Jon Nuñez, tarent solutions GmbH on 09.07.15.
 * @author sweiz
 */
public class LanguageProvider {
    /**
     *
     * Load standard language data (german)
     */
    public LanguageProvider() {
        this.properties = loadProperties(STANDARD_LANG_FILE);
    }

    /**
     *
     * Load language file into properties
     *
     * @param langFileName String name of language file
     */
    public LanguageProvider(final String langFileName) {
        this.properties = this.loadProperties(langFileName);
    }

    /** Octopus-Eingabeparameter für die Aktion {@link #load(OctopusContext)} */
    public static final String INPUT_load[] = {};
    /** Language file constants */
    public static final String STANDARD_LANG_FILE = "/etc/veraweb/l10n/de_DE.resource";

    public static final Logger LOGGER = Logger.getLogger(LanguageProvider.class.getName());
    //Path of all language files
    private static final String FILE_PATH = "/etc/veraweb/l10n/";

    /** Content of selected language file will be saved in Properties object */
    public Properties properties;
    //Map with all language names and their filename
    private Map<String, String> existingLanguagesAndFilenames = new TreeMap<String, String>();
    //Selected language will be persist, to load it on recall of controller
    private String lastSelectedLanguage = "";

    /**
     *
     * Getter for loaded properties
     *
     * @return loaded properties
     */
    public Properties getProperties() {
        return this.properties;
    }

    /**
     *
     * Get value from loaded property with key
     *
     * @param key String placeholder for translated text
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
     * @param cntx OctopusContext
     */
    public void load(OctopusContext cntx) {
        cntx.setContent("translatedNames", this.getLanguageOptions());
        this.insertAllValuesFromSelectedLanguageToContext(cntx);
    }

    /**
     * Getting translation by key
     *
     * @param langFileName String with full name of data with translations
     * @param key String with placeholder for translation
     * @return out String translated placeholder
     */
    public String getLocalizationValue(String langFileName, String key) {
        if (this.propertiesAreAvailable()) {
            return this.loadProperties(langFileName).getProperty(key);
        }

        return key;
    }

    //Load content of language file into properties
    private Properties loadProperties(final String langFileName) {
        final Properties properties = new Properties();

        FileInputStream inputStream = null;

        try {
            if(langFileName.equals(STANDARD_LANG_FILE)) {
                //Standard language file
                inputStream = new FileInputStream(langFileName);
            } else {
                //Language file from /etc/veraweb/l10n
                inputStream = new FileInputStream(FILE_PATH + langFileName);
            }

            properties.load(inputStream);
        } catch (IOException uniqueLangFileException) {
            LOGGER.warn(uniqueLangFileException);
            LOGGER.warn("Could not read language files!");
        }
        finally {
            try {
                inputStream.close();
            } catch (Exception closeFileException) {
                LOGGER.warn(closeFileException);
                LOGGER.warn("Could not close data! - " + inputStream);
            }
        }

        return properties;
    }

    private void insertAllValuesFromSelectedLanguageToContext(OctopusContext cntx) {
        Map<String, String> placeholderWithTranslation = new TreeMap<String, String>();
        final Request request = new RequestVeraWeb(cntx);

        try {
            //Will only be true on first load
            if (cntx.getContextField("languageSelector") == null && (lastSelectedLanguage.equals("") || lastSelectedLanguage == null)) {
                cntx.setContent("languageSelector", "Deutsch");
                lastSelectedLanguage = "de_DE.resource";
            } else if (request.getField("languageSelector") != null){
                lastSelectedLanguage = getFileNameByLangText(request.getField("languageSelector").toString());
            }
        } catch (BeanException e) {
            e.printStackTrace();
        }
            properties = this.loadProperties(lastSelectedLanguage);

        for(final String key : properties.stringPropertyNames()) {
            placeholderWithTranslation.put(key, properties.getProperty(key));
        }

        //Value of language name, which is read by language selector
        cntx.setContent("language", properties.getProperty("language"));
        //All keys with values from language data
        cntx.setContent("placeholderWithTranslation", placeholderWithTranslation);

        lastSelectedLanguage = getFileNameByLangText(cntx.getContextField("language").toString());
    }

    //Set language names (language parameter of language data) from all
    //language data of the given directory
    private List<String> getLanguageOptions() {
        existingLanguagesAndFilenames.clear();
        List<String> propertyNames = getLanguageFileNames();
        List<String> translatedNames = new ArrayList<String>();

        for(String langFileName : propertyNames) {
            String value = getLocalizationValue(langFileName, "language");

            translatedNames.add(value);
            existingLanguagesAndFilenames.put(value, langFileName);
        }

        return translatedNames;
    }

    /**
     * Get list of files (only file names)
     *
     * @return list of file names
     */
    private List<String> getLanguageFileNames() {
        File folder = new File(FILE_PATH);
        File[] listOfFiles = folder.listFiles();
        List<String> languageFileNames = new ArrayList<String>();

        try {
            for (File file : listOfFiles) {
                if (file.isFile() && file.getName().endsWith(".resource")) {
                    languageFileNames.add(file.getName());
                }
            }
        } catch (NullPointerException e) {
            LOGGER.warn(e);
            LOGGER.warn("Directory not found!");
        }

        return languageFileNames;
    }

    private String getFileNameByLangText(String langName) {
        return existingLanguagesAndFilenames.get(langName);
    }
}
