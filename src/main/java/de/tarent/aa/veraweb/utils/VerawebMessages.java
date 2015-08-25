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
package de.tarent.aa.veraweb.utils;

import de.tarent.aa.veraweb.utils.i18n.LanguageProvider;
import de.tarent.aa.veraweb.utils.i18n.LanguageProviderHelper;
import de.tarent.octopus.server.OctopusContext;

/**
 * Created by Jon Nuñez, tarent solutions GmbH on 23.06.15.
 */
public class VerawebMessages {
    final LanguageProviderHelper languageProviderHelper;
    final LanguageProvider languageProvider;

    public VerawebMessages(OctopusContext octopusContext) {
        languageProviderHelper = new LanguageProviderHelper();
        languageProvider= languageProviderHelper.enableTranslation(octopusContext);
    }

    public String getMessageEndAndStartDate() {
        return languageProvider.getProperty("MESSAGE_END_AND_START_DATE");
    }

    public String getMessageEventMissingShortDescription() {
        return languageProvider.getProperty("MESSAGE_EVENT_MISSING_SHORT_DESCRIPTION");

    }

    public String getMessageEventWrongDateFormat() {
        return languageProvider.getProperty("MESSAGE_EVENT_WRING_DATE_FORMAT");
    }

    public String getMessageGenericMissingDescription() {
        return languageProvider.getProperty("MESSAGE_GENERIC_MISSING_DESCRIPTION");
    }

    public String getMessageColorMissingName() {
        return languageProvider.getProperty("MESSAGE_COLOR_MISSING_NAME");
    }

    public String getMessageEventcategoryWrong() {
        return languageProvider.getProperty("MESSAGE_EVENTCATEGORY_ASSIGNMENT_WRONG");
    }

    public String getMessageEventdoctypeWrong() {
        return languageProvider.getProperty("MESSAGE_EVENTDOCTYPE_ASSIGNMENT_WRONG");
    }

    public String getMessageEventfunctionWrong() {
        return languageProvider.getProperty("MESSAGE_EVENTFUNCTION_ASSIGNMENT_WRONG");
    }
}
