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
     * @param octopusContext Octopus Context
     * @return LanguageProvider instance
     */
    public LanguageProvider enableTranslation(OctopusContext octopusContext) {
        final String language = octopusContext.sessionAsObject("sessionLanguage").toString();
        //        final String language = octopusContext.getContentObject().get("language").toString();
        final LanguageProvider languageProvider = new LanguageProvider();

        languageProvider.setLastSelectedLanguage(language + ".resource");
        languageProvider.load(octopusContext);
        return languageProvider;
    }
}
