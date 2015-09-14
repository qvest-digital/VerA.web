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

import java.util.Map;
/*
 * Language Helper was implemented to create a general message when deleting, creating or updating any entity
 * This utility is called from velocity through sending it to the OctopusContext
 */

public class LanguageHelper {

    public String createMessage(String entity,
                                String action,
                                String count,
                                Map<String, String> placeholderWithTranslation) {

        String message;
        // singular or plural
        String singularOrPluralOrNone;
        if (count.equals("0")) {
            singularOrPluralOrNone = "N";
        } else if (count.equals("1")) {
            singularOrPluralOrNone = "S";
        } else {
            singularOrPluralOrNone = "P";
        }

        String placeholdername = "GM_" + entity + "_" + action + "_" + singularOrPluralOrNone;
        if (singularOrPluralOrNone.equals("P")) {
            message = String.format(placeholderWithTranslation.get(placeholdername), count);
        } else {
            message = placeholderWithTranslation.get(placeholdername);
        }
        
        return message;
    }

    public String give(String enter) {
        return enter;
    }

}
