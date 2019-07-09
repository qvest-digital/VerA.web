package de.tarent.octopus.config;
import java.io.File;

import de.tarent.octopus.server.PersonalConfig;
import de.tarent.octopus.data.TcDataAccessException;
import de.tarent.octopus.data.TcGenericDataAccessWrapper;
import de.tarent.octopus.request.TcTaskList;
import de.tarent.octopus.response.TcResponseDescription;

/**
 * TcConfig besteht aus TcCommonConfig und TcPersonalConfig und kapselt die Informationen
 * aus beiden Konfigurations-Kontainern.
 * <br><br>
 * Dadurch dass alle Komponenten nur auf TcConfig zugreifen, ist es möglich zu verbergen, woher
 * die Informationen kommen. Auf diese weise können Benutzerspezifische Informationen
 * die Standardeinstellungen des Systems teilweise erweitern uns überschreiben.
 *
 * @author <a href="mailto:mancke@mancke-software.de">Sebastian Mancke</a>, <b>tarent GmbH</b>
 */
public class TcConfig {

    private TcCommonConfig commonConfig;
    private PersonalConfig personalConfig;
    private String currentModuleName;

    /**
     * @param commonConfig      Die Systemweiten Einstellungen
     * @param personalConfig    Die Benutzerspezifischen Einstellungen
     * @param currentModuleName Names des Modules, aus dem das aktuelle Task ist und aus dem die aktuellen Config Daten geladen
     *                          werden.
     */
    public TcConfig(
      TcCommonConfig commonConfig,
      PersonalConfig personalConfig,
      String currentModuleName) {
        this.commonConfig = commonConfig;
        this.personalConfig = personalConfig;
        this.currentModuleName = currentModuleName;
    }

    /**
     * Liefert die Wrapperklasse für Datenzugriffe
     *
     * @see TcCommonConfig#getDataAccess(String, String)
     */
    public TcGenericDataAccessWrapper getDataAccess(String dataAccessName) throws TcDataAccessException {
        return commonConfig.getDataAccess(currentModuleName, dataAccessName);
    }

    /**
     * Diese Methode liefert eine strukturierte Liste der Tasks des angegebenen
     * Moduls.
     *
     * @param moduleName Name des betreffenden Moduls.
     * @return eine strukturierte Taskliste zum betreffenden Modul oder null.
     */
    public TcTaskList getTaskList(String moduleName) {
        return commonConfig.getTaskList(moduleName);
    }

    /**
     * Liefert die systemweiten Einstellungen
     */
    public TcCommonConfig getCommonConfig() {
        return commonConfig;
    }

    /**
     * Setzt die User Einstellungen
     */
    public void setPersonalConfig(PersonalConfig personalConfig) {
        this.personalConfig = personalConfig;
    }

    /**
     * Liefert die User Einstellungen
     */
    public PersonalConfig getPersonalConfig() {
        return personalConfig;
    }

    /**
     * Liefert die Modul Einstellungen
     */
    public TcModuleConfig getModuleConfig() {
        return commonConfig.getModuleConfig(currentModuleName);
    }

    /**
     * Verzeichnis, indem die Templates für das aktuelle Modul liegen.
     *
     * @return Verzeichnis
     */
    public File getTemplateRootPath() {
        return commonConfig.getTemplateRootPath(currentModuleName);
    }

    /**
     * Verzeichnis, indem die Templates für das aktuelle Modul liegen.
     * Relativ zum Rootverzeichnis der Module
     *
     * @return Verzeichnis
     */
    public String getRelativeTemplateRootPath() {
        return commonConfig.getRelativeTemplateRootPath(currentModuleName);
    }

    /**
     * Verzeichnis, indem sich die Statischen Web Ressourcen für das aktuelle Modul befinden.
     * Relativ zum Rootverzeichnis des Servers
     *
     * @return Verzeichnis
     */
    public String getRelativeWebRootPath() {
        return commonConfig.getRelativeWebRootPath(currentModuleName);
    }

    /**
     * Verzeichnis, indem die Daten des aktuellen Modules liegen.
     *
     * @return Verzeichnis
     */
    public File getModuleRootPath() {
        return commonConfig.getModuleRootPath(currentModuleName);
    }

    /*
     * Liefert eine Liste der registrierten Worker
     * @return Bezeichnende Namen als Keys und vollqualifizierte Klassennamen als String-Values
    public Map getDeclaredContentWorkers() {
	return commonConfig.getDeclaredContentWorkers(currentModuleName);
    }
     */

    /**
     * Liefert den Typ der Response, der benutzt werden soll,
     * wenn keiner angegeben ist, z.B. wenn ein Fehler auf getreten ist.
     */
    public String getDefaultResponseType() {
        return commonConfig.getDefaultResponseType(currentModuleName);
    }

    /**
     * Gibt den Namen einer Fehlerausgabe
     */
    public String getDefaultErrorDescriptionName() {
        return commonConfig.getDefaultErrorDescriptionName(currentModuleName);
    }

    /**
     * Gibt an, ob das System Debugmeldungen ausgeben soll.
     * <br><br>
     * Dies ist völlig unabhängig vom Logger und  hängt davon ab,
     * ob 'global.allowDebugMessages' im DeploymentDescriptor und
     * 'debug' im Request aus true sind.
     *
     * @return true, wenn Debugmeldugen ausgegeben werden sollen, false sonst.
     * @deprecated Direkte ausgabe von Debug Messages werden nicht mehr unterstützt. Bitte Logging Api verwenden!
     */
    public boolean debug() {
        return false;
        //         return commonConfig.configData.getValueAsBoolean(TcEnv.KEY_ALLOW_DEBUG_MESSAGES)
        //             && tcRequest.getParameterAsBoolean("debug");
    }

    /**
     * Liefert den Namen des eingeloggten Benutzers
     */
    public String getLoginname() {
        return personalConfig != null ? personalConfig.getUserLogin() : null;
    }

    /**
     * Liefert das Default-Encoding z.B. für Velocity-Templates
     */
    public String getDefaultEncoding() {
        return commonConfig.getDefaultEncoding(currentModuleName);
    }

    /**
     * Liefert den ContentType, der gesetzt werden soll, wenn nichts anderes angegeben ist.
     */
    public String getDefaultContentType() {
        return commonConfig.getDefaultContentType(currentModuleName);
    }

    /**
     * Liefert Sessionwerte aus der PersonalConfig als String
     *
     * @return Inhalt als String. Wenn es kein String ist, wird toString aufgerufen.
     */
    public String getSessionValue(String key) {
        Object value = getSessionValueAsObject(key);
        if (value == null) {
            return null;
        }
        if (value instanceof String) {
            return (String) value;
        }
        return value.toString();
    }

    /**
     * Liefert Sessionwerte aus der PersonalConfig als Object
     *
     * @return Inhalt als Object
     */
    public Object getSessionValueAsObject(String key) {
        return personalConfig != null ? personalConfig.getSessionValue(key) : null;
    }

    /**
     * Setzt Sessionwerte in die PersonalConfig
     */
    public void setSessionValue(String key, Object value) {
        if (personalConfig != null) {
            personalConfig.setSessionValue(key, value);
        }
    }

    public TcResponseDescription getDefaultResponseDescription() {
        return new TcResponseDescription(getDefaultErrorDescriptionName(), getDefaultResponseType());
    }

    public TcResponseDescription getLoginResponseDescription() {
        return new TcResponseDescription("login", getDefaultResponseType());
    }
}
