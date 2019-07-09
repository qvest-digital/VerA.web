package de.tarent.octopus.content;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Session-Manager
 *
 * Sollte beim Laden eines Worker initalisiert werden
 * und in der pre (und post) Methode-Verwendet werden.
 *
 * Durchsucht Content und Request nach neuen den
 * Parametern und speichert diese in der Session ab.
 * Sollte der Parameter nicht übergeben werden, wird
 * der Wert aus der Session geladen bzw. der Default-Wert
 * genommen.
 *
 * @author Heiko Ferger
 * @see #add(String, Object)
 * @see #sync(TcAll)
 */
public class SessionManager {
    private final Map _enabledVars = new HashMap();

    /**
     * @deprecated Die Liste der erlauben variablen sollte nicht gelöscht werden
     */
    public void clear() {
        _enabledVars.clear();
    }

    /**
     * Funktion zum registrieren der erlaubten Session-Variablen
     * es ist nicht möglich den Wert (Defaultwert) der erlaubten Variable
     * ein zweites mal zu ändern.
     *
     * Erlaubte Variable ist dann also nach definition "final"
     *
     * @param name Key für den Request, Content und die Session.
     * @param def  Default-Wert.
     */
    public void add(String name, Object def) {
        if (!_enabledVars.containsKey(name)) {
            _enabledVars.put(name, def);
        }
    }

    public void sync(TcAll all) {
        Iterator it = _enabledVars.keySet().iterator();

        while (it.hasNext()) {
            String name = (String) it.next();

            if (all.contentContains(name)) {
                all.setSession(name, all.contentAsObject(name));
            } else if (all.requestContains(name)) {
                all.setContent(name, all.requestAsObject(name));
                all.setSession(name, all.requestAsObject(name));
            } else if (all.sessionAsObject(name) != null) {
                all.setContent(name, all.sessionAsObject(name));
            } else {
                all.setContent(name, _enabledVars.get(name));
            }
        }
    }

    /**
     * Speichert alle Registrierten Variablen in der HashMap 'callByName' Dient
     * als erweiterung es VTL auch auf variablen per "Name" zugreifen (dies in
     * der Session registriert sind) meistens werden diese Variablen in Velocity
     * generich erzeugt z.b. Tabelle_[Navigation]
     */
    public void setCallByName(TcAll all) {
        HashMap callByName = new HashMap();
        Iterator it = _enabledVars.keySet().iterator();

        while (it.hasNext()) {
            String name = (String) it.next();
            if (all.sessionAsObject(name) != null) {
                callByName.put(name, all.sessionAsObject(name));
            }
        }
        all.setContent("callByName", callByName);
    }

    public String toString() {
        return super.toString() + " " + _enabledVars;
    }
}
