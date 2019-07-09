package de.tarent.data.exchange;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Diese Klasse stellt die Eigenschaften eines Datenaustauschformats für
 * Export- und Importimplementierungen zur Verfügung.
 *
 * @author mikel
 */
public class ExchangeFormat {
    //
    // Getter und Setter
    //

    /**
     * Das Standard-Suffix für Dateien dieses Formats.
     */
    public String getDefaultExtension() {
        return defaultExtension;
    }

    /**
     * Das Standard-Suffix für Dateien dieses Formats.
     */
    protected void setDefaultExtension(String defaultExtension) {
        this.defaultExtension = defaultExtension;
    }

    /**
     * Die Beschreibung dieses Formats
     */
    public String getDescription() {
        return description;
    }

    /**
     * Die Beschreibung dieses Formats
     */
    protected void setDescription(String description) {
        this.description = description;
    }

    /**
     * Die {@link de.tarent.aa.veraweb.utils.Exporter}-Klasse zu diesem Format.
     * Sie wird gegebenenfalls aus {@link #exporterClassName} erzeugt.
     *
     * @throws ClassNotFoundException
     */
    public Class getExporterClass() throws ClassNotFoundException {
        if (exporterClass == null && exporterClassName != null) {
            exporterClass = Thread.currentThread().getContextClassLoader().loadClass(exporterClassName);
        }
        return exporterClass;
    }

    /**
     * Der Name der {@link de.tarent.aa.veraweb.utils.Exporter}-Klasse zu diesem Format
     */
    public String getExporterClassName() {
        return exporterClassName;
    }

    /**
     * Der Name der {@link de.tarent.aa.veraweb.utils.Exporter}-Klasse zu diesem Format
     */
    protected void setExporterClassName(String exporterClassName) {
        this.exporterClassName = exporterClassName;
        this.exporterClass = null;
    }

    /**
     * Die URL zu einem Icon zu diesem Format
     */
    public URL getIconUrl() {
        return iconUrl;
    }

    /**
     * Die URL zu einem Icon zu diesem Format
     */
    protected void setIconUrl(URL iconUrl) {
        this.iconUrl = iconUrl;
    }

    /**
     * Die {@link de.tarent.aa.veraweb.utils.Importer}-Klasse zu diesem Format.
     * Sie wird gegebenenfalls aus {@link #importerClassName} erzeugt.
     *
     * @throws ClassNotFoundException
     */
    public Class getImporterClass() throws ClassNotFoundException {
        if (importerClass == null && importerClassName != null) {
            importerClass = Thread.currentThread().getContextClassLoader().loadClass(importerClassName);
        }
        return importerClass;
    }

    /**
     * Der Name der {@link de.tarent.aa.veraweb.utils.Importer}-Klasse zu diesem Format
     */
    public String getImporterClassName() {
        return importerClassName;
    }

    /**
     * Der Name der {@link de.tarent.aa.veraweb.utils.Importer}-Klasse zu diesem Format
     */
    protected void setImporterClassName(String importerClassName) {
        this.importerClassName = importerClassName;
        this.importerClass = null;
    }

    /**
     * Der MIME-Typ zu diesem Format
     */
    public String getMimeType() {
        return mimeType;
    }

    /**
     * Der MIME-Typ zu diesem Format
     */
    protected void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    /**
     * Der Name dieses Formats
     */
    public String getName() {
        return name;
    }

    /**
     * Der Name dieses Formats
     */
    protected void setName(String name) {
        this.name = name;
    }

    /**
     * Die speziellen Attribute dieses Formats
     */
    public Map getProperties() {
        return Collections.unmodifiableMap(properties);
    }

    /**
     * Die speziellen Attribute dieses Formats
     */
    protected void setProperties(Map properties) {
        this.properties.clear();
        this.properties.putAll(properties);
    }

    /**
     * Die speziellen Attribute dieses Formats
     */
    protected void addProperties(Map properties) {
        this.properties.putAll(properties);
    }

    //
    // Object
    //

    /**
     * Returns a string representation of the object.<br>
     * Diese Darstellung dient Debug-Zwecken.
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer(128);
        buffer.append("ExchangeFormat (Name: ").append(name)
          .append("; Description: ").append(description)
          .append("; MIME-Typ: ").append(mimeType)
          .append("; Suffix: ").append(defaultExtension)
          .append("; Icon: ").append(iconUrl)
          .append("; Exporter: ").append(exporterClassName)
          .append("; Importer: ").append(importerClassName)
          .append("; Properties: ").append(properties);
        return buffer.toString();
    }

    //
    // geschützte Member
    //
    /**
     * Der Name dieses Formats
     */
    String name = null;
    /**
     * Die Beschreibung dieses Formats
     */
    String description = null;
    /**
     * Die URL zu einem Icon zu diesem Format
     */
    URL iconUrl = null;
    /**
     * Der Name der {@link de.tarent.aa.veraweb.utils.Exporter}-Klasse zu diesem Format
     */
    String exporterClassName = null;
    /**
     * Die {@link de.tarent.aa.veraweb.utils.Exporter}-Klasse zu diesem Format.
     */
    Class exporterClass = null;
    /**
     * Der Name der {@link de.tarent.aa.veraweb.utils.Importer}-Klasse zu diesem Format
     */
    String importerClassName = null;
    /**
     * Die {@link de.tarent.aa.veraweb.utils.Importer}-Klasse zu diesem Format.
     */
    Class importerClass = null;
    /**
     * Der MIME-Typ zu diesem Format
     */
    String mimeType = null;
    /**
     * Das Standard-Suffix für Dateien dieses Formats.
     */
    String defaultExtension = null;
    /**
     * Die speziellen Attribute dieses Formats
     */
    final Map properties = new HashMap();
}
