package de.tarent.octopus.server;

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

import de.tarent.octopus.client.OctopusTask;
import de.tarent.octopus.config.TcCommonConfig;
import de.tarent.octopus.config.TcConfig;
import de.tarent.octopus.config.TcModuleConfig;
import de.tarent.octopus.config.TcPersonalConfig;
import de.tarent.octopus.content.TcContent;
import de.tarent.octopus.request.TcRequest;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Kontext, in dem ein Worker im Octopus existiert.
 * Über diesen Kontext kann ein Worker Request-, Content- Config- und Sessiondaten abfragen sowie setzen.
 *
 * @author <a href="mailto:sebastian@tarent.de">Sebastian Mancke</a>, <b>tarent GmbH</b>
 * @version 1.0
 */
public interface OctopusContext {
    String CONTENT_FIELD_PREFIX = "CONTENT:";
    String REQUEST_FIELD_PREFIX = "REQUEST:";
    String SESSION_FIELD_PREFIX = "SESSION:";
    String CONFIG_FIELD_PREFIX = "CONFIG:";

    /**
     * Key for a list of objects to clean up after the execution of a task.
     * The objects in this list have be of the type Runnable or Cleanable.
     */
    String CLEANUP_OBJECT_LIST = "octopus.cleanup.objects";

    /**
     * Liefert den Inhalt eines Feldes aus einem der Contextbereiche Request, Content, Session oder Config
     * Aus welchem Bereich das Feld Stammt wird über das Prefix entschieden.
     * REQUEST:username z.B liefert den Parameter username aus dem Request.
     * Kein Prefix bedeutet: Suche in Content, Request, Session
     */
    Object getContextField(String contextFieldName);

    /**
     * Setzt ein Feld in einem der Contextbereiche Content, Session, abhängig vom Verwendeten Prefix des fieldNames.
     * Ohne Prefix wird das Feld im Content gesetzt.
     * Das Setzen von Feldern im Request und in der Config ist verboten.
     */
    void setContextField(String fieldName, Object value);

    TcContent getContentObject();

    TcConfig getConfigObject();

    TcRequest getRequestObject();

    /**
     * Returns an OctopusTask instance to the supplied task in the current module over the OctopusClient API.
     * A call to this task uses the same session an therefore the same authentication.
     * On the other hand, the request and content are fresh.
     *
     * @param taskName The name of the target task in this module
     * @return A callable task for the target task in the current module
     */
    OctopusTask getTask(String taskName);

    /**
     * Adds the supplied object to the List of Cleanup-Objects in the Content.
     * After the processing of the request, the octopus will call the close() method for each of these objects.
     * This will be done even if the request processing will throw an exception.
     */
    void addCleanupCode(Closeable closeable);

    /**
     * Adds the supplied object to the List of Cleanup-Objects in the Content.
     * After the processing of the request, the octopus will call the run() method for each of these objects.
     * This will be done even if the request processing will throw an exception.
     */
    void addCleanupCode(Runnable runnable);

    //===>  C O N T E N T  <===//

    boolean contentContains(String key);

    Iterator contentKeys();

    String contentAsString(String key);

    Object contentAsObject(String key);

    void setContent(String key, Integer value);

    void setContent(String key, List value);

    void setContent(String key, Map value);

    void setContent(String key, String value);

    void setContent(String key, Object value);

    void setContentError(Exception e);

    void setContentError(String message);

    void setContentStatus(String status);

    //===>  R E Q U E S T  <===//

    int requestType();

    String requestTypeName();

    boolean requestContains(String key);

    Object requestAsObject(String key);

    String requestAsString(String key);

    Integer requestAsInteger(String key);

    Boolean requestAsBoolean(String key);

    //===>  C O N F I G  <===//

    TcCommonConfig commonConfig();

    TcPersonalConfig personalConfig();

    TcModuleConfig moduleConfig();

    File moduleRootPath();

    String getModuleName();

    String getTaskName();

    //===>  S E S S I O N  <===//

    Object sessionAsObject(String key);

    String sessionAsString(String key);

    void setSession(String key, Object value);

    //===>  S T A T U S  <===//

    void setStatus(String status);

    String getStatus();

    void setError(Exception e);

    void setError(String message);

    //===>  C L O N E  <===//

    /**
     * Return a new octopus context with an empty request an empty content.
     *
     * @see #cloneContext(boolean, boolean)
     */
    OctopusContext cloneContext();

    /**
     * Return a new octopus context. If the parameter <code>newRequest</code>
     * is <code>true</code> a new empty request will be created, otherwise
     * the new context will also use the current {@link TcRequest} instance.
     * If the parameter <code>newContent</code> is <code>true</code> a new
     * empty content will be created. Otherwise the current {@link TcContent}
     * will be used.
     */
    OctopusContext cloneContext(boolean newRequest, boolean newContent);
}
