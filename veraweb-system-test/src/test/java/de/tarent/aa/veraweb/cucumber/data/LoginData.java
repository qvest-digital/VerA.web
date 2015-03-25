/**
 * veraweb, platform independent webservice-based event management
 * (Veranstaltungsmanagment VerA.web), is
 * Copyright © 2004-2008 tarent GmbH
 * Copyright © 2013 tarent solutions GmbH
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
package de.tarent.aa.veraweb.cucumber.data;

import java.util.HashMap;
import java.util.Map;

import de.tarent.aa.veraweb.cucumber.pagedefinitions.ElementDefinition;
import de.tarent.aa.veraweb.cucumber.pagedefinitions.PageDefinition;
import de.tarent.aa.veraweb.cucumber.utils.NameUtil;

public enum LoginData {

    GUELTIGE_ANMELDEDATEN("administrator", "mySecret2$"),
    UNGUELTIGE_ANMELDEDATEN_MIT_FALSCHEM_BENUTZER("falscherUser", "mySecret2$"),
    UNGUELTIGE_ANMELDEDATEN_MIT_FALSCHEM_PASSWORT("administrator", "falschesPasswort");

    public final String user;
    public final String password;
    
    public final Map<String, ElementDefinition> valuesForPageFields;

    private LoginData(String user, String password) {
        this.user = user;
        this.password = password;
        
        valuesForPageFields = new HashMap<String, ElementDefinition>();
        valuesForPageFields.put(user, PageDefinition.ANMELDUNGSSEITE.elementForName("Benutzername-Feld"));
        valuesForPageFields.put(password, PageDefinition.ANMELDUNGSSEITE.elementForName("Passwort-Feld"));
    }

    public static LoginData forName(String name) {
        return valueOf(NameUtil.nameToEnumName(name));
    }
}
