package de.tarent.aa.veraweb.cucumber.data;

import de.tarent.aa.veraweb.cucumber.utils.NameUtil;

public enum LoginData {

    GUELTIGE_ANMELDEDATEN("administrator", "mySecret2$"),
    UNGUELTIGE_ANMELDEDATEN_FALSCHER_BENUTZER("falscherUser", "mySecret2$"),
    UNGUELTIGE_ANMELDEDATEN_FALSCHES_PASSWORT("administrator", "falschesPasswort");

    public final String user;

    public final String password;

    private LoginData(String user, String password) {
        this.user = user;
        this.password = password;
    }

    public static LoginData forName(String name) {
        return valueOf(NameUtil.nameToEnumName(name));
    }
}
