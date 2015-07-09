package de.tarent.aa.veraweb.utils.i18n;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author jnunez Jon Nuñez, tarent solutions GmbH on 09.07.15.
 */
public class LanguageProvider {

    /** Language file constants */
    private static final String FILE_PATH = "/etc/veraweb/lang/";
    private static final String FILE_EXTENSION = ".properties";

    public Properties properties;

    public LanguageProvider(final String langFileName) {
        this.properties = this.loadProperties(langFileName);
    }

    public Properties getProperties() {
        return this.properties;
    }

    public String getProperty(String key) {
        return this.properties.getProperty(key);
    }

    public boolean propertiesAreAvailable() {
        return (this.properties == null) ? false : true;
    }

    /**
     * Getting translation by key
     *
     * @param key String with umlauts
     * @return out String without umlauts
     */
    public String translate(String key) {
        if (this.propertiesAreAvailable()) {
            return this.properties.getProperty(key);
        }
        return key;
    }

    /**
     * Get list of files (only names)
     *
     * @return list of file names
     */
    public List<String> getLanguageNames() {

        File folder = new File("your/path");
        File[] listOfFiles = folder.listFiles();
        List<String> languageNames = new ArrayList<String>();

        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                languageNames.add(listOfFiles[i].getName());
            }
        }

        return languageNames;
    }

    private Properties loadProperties(final String langFileName) {
        final Properties properties = new Properties();

        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(FILE_PATH + langFileName + FILE_EXTENSION);
            properties.load(inputStream);
        } catch (IOException e) {
            return null;
        } finally {
            try { inputStream.close(); } catch (Exception e) { }
        }

        return properties;
    }

}
