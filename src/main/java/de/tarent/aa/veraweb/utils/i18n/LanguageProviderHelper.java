package de.tarent.aa.veraweb.utils.i18n;

import de.tarent.octopus.server.OctopusContext;

/**
 * Class to allow translations over Java hardcoded texts
 *
 * @author Created by Jon Nuñez, tarent solutions GmbH on 21.08.15.
 */
public class LanguageProviderHelper {

    /**
     * Enabling translations to allow it over hardcoded text in java files
     *
     * @param cntx Octopus Context
     * @return LanguageProvider instance
     */
    public LanguageProvider enableTranslation(OctopusContext cntx) {
        String language = cntx.getContentObject().get("language").toString();

        LanguageProvider languageProvider = new LanguageProvider();

        languageProvider.setLastSelectedLanguage(language + ".resource");
        languageProvider.load(cntx);
        return languageProvider;
    }
}
