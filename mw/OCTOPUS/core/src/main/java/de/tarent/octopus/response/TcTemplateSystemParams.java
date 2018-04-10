package de.tarent.octopus.response;
import de.tarent.octopus.config.TcConfig;

/**
 * Enthält Informationen und Parameter, die für alle Templates gleich sind.
 *
 * @author <a href="mailto:mancke@mancke-software.de">Sebastian Mancke</a>, <b>tarent GmbH</b>
 */
public class TcTemplateSystemParams {
    TcConfig config;
    String templateRootPath;

    /**
     * Initialisierung mit Config Objekkt, aus dem die Informationen kommen können
     */
    public TcTemplateSystemParams(TcConfig config) {
        this.config = config;
    }

    /**
     * Gibt das Verzeichniss zurück, indem nach Templates gesucht werden soll.
     *
     * @return Absolute Pfadangabe mit abschließendem Slash
     */
    public String getTemplateRootPath() {
        return templateRootPath;
    }

    /**
     * Setzt das Verzeichniss indem nach Templates gesucht werden soll.
     *
     * @param path Absolute Pfadangabe mit abschließendem Slash
     */
    public void setTemplateRootPath(String path) {
        templateRootPath = path;
    }
}
