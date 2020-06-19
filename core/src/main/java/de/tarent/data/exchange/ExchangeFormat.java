package de.tarent.data.exchange;

/*-
 * Veranstaltungsmanagement VerA.web, comprised of…
 * VerA.web, platform-independent webservice-based event management
 * tarent-commons, a set of common components and solutions
 * tarent-contact, platform-independent webservice-based contact management
 * tarent-database, jdbc database library
 * tarent-doctor, Document Generation Platform
 * tarent-octopus, Webservice Data Integrator and Application Server
 * … is newly MIT-licenced and Copyright
 *  © 2018 Атанас Александров (sirakov@gmail.com)
 *  © 2014, 2015, 2016, 2017 Атанас Александров (a.alexandrov@tarent.de)
 *  © 2013 Иванка Александрова (i.alexandrova@tarent.de)
 *  © 2005, 2006, 2007 asteban (s.mancke@tarent.de)
 *  © 2013 Patrick Apel (p.apel@tarent.de)
 *  © 2016 Eugen Auschew (e.auschew@tarent.de)
 *  © 2013 Andrei Boulgakov (a.boulgakov@tarent.de)
 *  © 2013 Valentin But (v.but@tarent.de)
 *  © 2016 Lukas Degener (l.degener@tarent.de)
 *  © 2017 Axel Dirla (a.dirla@tarent.de)
 *  © 2015 Julian Drawe (j.drawe@tarent.de)
 *  © 2009 Sven Frommeyer (s.frommeyer@tarent.de)
 *  © 2014, 2018 Dominik George (d.george@tarent.de)
 *  © 2013 Martin Gernhardt (m.gernhardt@tarent.de)
 *  © 2013 Sascha Girrulat (s.girrulat@tarent.de)
 *  © 2007, 2008 David Goemans (d.goemans@tarent.de)
 *  © 2018 Christian Gorski (c.gorski@tarent.de)
 *  © 2015 Viktor Hamm (v.hamm@tarent.de)
 *  © 2013 Katja Hapke (k.hapke@tarent.de)
 *  © 2006, 2007, 2010, 2013 Hendrik Helwich (h.helwich@tarent.de)
 *  © 2018 Thomas Hensel (t.hensel@tarent.de)
 *  © 2018, 2019 Benedict Hoeger (b.hoeger@tarent.de)
 *  © 2018, 2019 Titian Horvath (t.horvath@tarent.de)
 *  © 2005, 2006, 2007, 2008 Christoph Jerolimov (jerolimov@gmx.de)
 *  © 2018, 2019 Timo Kanera (t.kanera@tarent.de)
 *  © 2006 Philipp Kirchner (p.kirchner@tarent.de)
 *  © 2008, 2009, 2010 Carsten Klein (c.klein@tarent.de)
 *  © 2006 Michael Kleinhenz (m.kleinhenz@tarent.de)
 *  © 2006 Michael Klink (m.klink@tarent.de)
 *  © 2007 Fabian Köster (f.koester@tarent.de)
 *  © 2006, 2014 Martin Ley (m.ley@tarent.de)
 *  © 2007 Alex Maier (a.maier@tarent.de)
 *  © 2014, 2015 Max Marche (m.marche@tarent.de)
 *  © 2007 Jan Meyer (jan@evolvis.org)
 *  © 2007, 2013, 2014, 2015, 2016, 2017, 2018, 2019, 2020
 *     mirabilos (t.glaser@tarent.de)
 *  © 2016 Cristian Molina (c.molina@tarent.de)
 *  © 2006, 2007 Jens Neumaier (j.neumaier@tarent.de)
 *  © 2006 Nils Neumaier (n.neumaier@tarent.de)
 *  © 2018 Yorka Neumann (y.neumann@tarent.de)
 *  © 2017 Michael Nienhaus (m.nienhaus@tarent.de)
 *  © 2013 Claudia Nuessle (c.nuessle@tarent.de)
 *  © 2014, 2015 Jon Nuñez Alvarez (j.nunez-alvarez@tarent.de)
 *  © 2016 Jens Oberender (j.oberender@tarent.de)
 *  © 2016, 2017 Miluška Pech (m.pech@tarent.de)
 *  © 2007, 2008, 2009 Martin Pelzer (m.pelzer@tarent.de)
 *  © 2008, 2009 Christian Preilowski (c.thiel@tarent.de)
 *  © 2013 Marc Radel (m.radel@tarent.de)
 *  © 2013 Sebastian Reimers (s.reimers@tarent.de)
 *  © 2015 Charbel Saliba (c.saliba@tarent.de)
 *  © 2006, 2008, 2009, 2010 Thomas Schmitz (t.schmitz@tarent.de)
 *  © 2013 Volker Schmitz (v.schmitz@tarent.de)
 *  © 2014 Sven Schumann (s.schumann@tarent.de)
 *  © 2007 Robert Schuster (r.schuster@tarent.de)
 *  © 2014 Sevilay Temiz (s.temiz@tarent.de)
 *  © 2013 Kevin Viola Schmitz (k.schmitz@tarent.de)
 *  © 2008, 2015 Stefan Walenda (s.walenda@tarent.de)
 *  © 2015, 2016, 2017 Max Weierstall (m.weierstall@tarent.de)
 *  © 2013 Rebecca Weinz (r.weinz@tarent.de)
 *  © 2015, 2016 Stefan Weiz (s.weiz@tarent.de)
 *  © 2015, 2016 Tim Zimmer (t.zimmer@tarent.de)
 * and older code, Copyright © 2001–2008 ⮡ tarent GmbH and contributors.
 * Licensor is tarent solutions GmbH, http://www.tarent.de/
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

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
