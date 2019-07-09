package de.tarent.aa.veraweb.worker;
import de.tarent.aa.veraweb.utils.LocaleMessage;
import de.tarent.octopus.server.OctopusContext;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Diese Octopus-Worker-Klasse stellt Helferobjekte in den Kontext, die Mitteilungstexte
 * und Build-Informationen liefern.
 *
 * @author Christoph
 * @author mikel
 */
public class MessageWorker {
    //
    // Octopus-Aktionen
    //
    /**
     * Octopus-Eingabeparameter für die Aktion {@link #init()}
     */
    public final static String INPUT_init[] = {};

    /**
     * Diese Octopus-Aktion initialisiert die Member dieses Workers.
     */
    public void init() {
        message = new LocaleMessage(Locale.getDefault());
        properties = ResourceBundle.getBundle("de.tarent.aa.veraweb.veraweb", Locale.getDefault());
    }

    /**
     * Octopus-Eingabeparameter für die Aktion {@link #load(OctopusContext)}
     */
    public final static String INPUT_load[] = {};

    /**
     * Diese Octopus-Aktion legt gebündelte lokalisierte Mitteilungen als "message"
     * und gebündelte Versions- und Buildinformationen als "properties" in den
     * Octopus-Content.
     *
     * @param cntx Octopus-Kontext
     */
    public void load(OctopusContext cntx) {
        cntx.setContent("message", message);
        cntx.setContent("properties", properties);
    }

    //
    // Member-Variablen
    //
    LocaleMessage message;
    ResourceBundle properties;
}
