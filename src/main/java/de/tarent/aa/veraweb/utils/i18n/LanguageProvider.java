package de.tarent.aa.veraweb.utils.i18n;

import de.tarent.octopus.server.OctopusContext;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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

    /** Octopus-Eingabeparameter für die Aktion {@link #load(OctopusContext)} */
    public static final String INPUT_load[] = {};
    /** Language file constants */
    public static final String STANDARD_LANG_FILE =  InputStream.class.getResource("LanguageProvider") System.getProperty("catalina.base") +
            "/webapps/veraweb/OCTOPUS/templates/velocity/l10n/de_DE.resource";
    LanguageProvider.class.getResource("/l10n/de_DE.resource")



    public static final Logger LOGGER = Logger.getLogger(LanguageProvider.class.getName());
    private static final String FILE_PATH = "/etc/veraweb/l10n/";

    public Properties properties;
    private Map<String, String> existingLanguagesAndFilenames = new TreeMap();

    /**
     *
     * Load language file into properties
     *
     * @param langFileName String name of language file
     */
    public LanguageProvider(final String langFileName) {
        this.properties = this.loadProperties(langFileName);
    }

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
     * @return
     */
    public String getProperty(String key) {
        return this.properties.getProperty(key);
    }

    /**
     * Checks, if the properties is null
     *
     * @return false if properties null, else true
     */
    public boolean propertiesAreAvailable() {
        return (this.properties == null) ? false : true;
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

    /**
     * Get list of files (only names)
     *
     * @return list of file names
     */
    public List<String> getLanguageFileNames() {
        File folder = new File(FILE_PATH);
        File[] listOfFiles = folder.listFiles();
        List<String> languageFileNames = new ArrayList<String>();

        try {
            for (int i = 0; i < listOfFiles.length; i++) {
                if (listOfFiles[i].isFile()) {
                    languageFileNames.add(listOfFiles[i].getName());
                }
            }
        } catch (NullPointerException e) {
            LOGGER.warn(e);
            LOGGER.warn("Directory not found!");
        }

        return languageFileNames;
    }

    //Load content of language file into properties
    private Properties loadProperties(final String langFileName) {
        final Properties properties = new Properties();

        FileInputStream inputStream = null;

        try {
            if(langFileName == STANDARD_LANG_FILE) {
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
            try { inputStream.close(); } catch (Exception e) { }
        }

        return properties;
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

    private void insertAllValuesFromSelectedLanguageToContext(OctopusContext cntx) {
        Map<String, String> placeholderWithTranslation = new TreeMap<String, String>();

        LanguageProvider.class.getResource("/de/tarent/aa/veraweb/config.propersties");

        properties = this.loadProperties("en_EN.resource");
        //TODO mach
        //properties = this.loadProperties(getFileNameByLangText(cntx));

        for(final String key : properties.stringPropertyNames()) {
            placeholderWithTranslation.put(key, properties.getProperty(key));
        }

        cntx.setContent("placeholderWithTranslation", placeholderWithTranslation);
    }

    private String getFileNameByLangText(OctopusContext cntx) {
        String selectedLanguage = cntx.getContextField("selectedLanguage").toString();

        return existingLanguagesAndFilenames.get(selectedLanguage);
    }
}
