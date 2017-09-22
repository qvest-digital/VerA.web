package de.tarent.aa.veraweb.utils.i18n;

/*-
 * veraweb, platform independent webservice-based event management
 * (Veranstaltungsmanagment VerA.web), is
 * Copyright © 2004–2008 tarent GmbH
 * Copyright © 2013–2016 tarent solutions GmbH
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
