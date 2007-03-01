package de.tarent.octopus.request;

import java.util.prefs.Preferences;

import de.tarent.octopus.config.TcModuleConfig;

/**
 * Mit dieser Schnittstelle werden dem Octopus Konfigurationsdaten zur
 * Verfügung gestellt.  
 */
public interface OctopusConfiguration {
    /**
     * Diese Methode liefert zu einem Modulnamen die Konfiguration.
     * 
     * @param module der Name des Moduls.
     * @param modulePreferences Preferences zum Modul als Hints.
     * @return Modulkonfiguration zu dem Modul. <code>null</code>
     *  steht hier für ein nicht gefundenes Modul.
     */
    public TcModuleConfig getModuleConfig(String module, Preferences modulePreferences);
}
