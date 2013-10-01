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
