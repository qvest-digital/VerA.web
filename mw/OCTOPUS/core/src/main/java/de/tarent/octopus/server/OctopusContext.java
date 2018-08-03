package de.tarent.octopus.server;

/*-
 * VerA.web-Middleware, newly MIT licenced, is comprised of:
 * tarent-commons, a set of common components and solutions
 * tarent-contact, platform-independent webservice-based contact management
 * tarent-database, jdbc database library
 * tarent-doctor, Document Generation Platform
 * tarent-octopus, Webservice Data Integrator and Application Server
 *  © 2018 Атанас Александров (sirakov@gmail.com)
 *  © 2005, 2006, 2007 asteban (s.mancke@tarent.de)
 *  © 2007 David Goemans (d.goemans@tarent.de)
 *  © 2018 Christian Gorski (c.gorski@tarent.de)
 *  © 2006, 2007, 2010 Hendrik Helwich (h.helwich@tarent.de)
 *  © 2018 Benedict Hoeger (b.hoeger@tarent.de)
 *  © 2005, 2006, 2007 Christoph Jerolimov (c.jerolimov@tarent.de)
 *  © 2018 Timo Kanera (t.kanera@tarent.de)
 *  © 2006 Philipp Kirchner (p.kirchner@tarent.de)
 *  © 2010 Carsten Klein (c.klein@tarent.de)
 *  © 2006 Michael Kleinhenz (m.kleinhenz@tarent.de)
 *  © 2006 Michael Klink (m.klink@tarent.de)
 *  © 2007 Fabian Köster (f.koester@tarent.de)
 *  © 2006 Martin Ley (m.ley@tarent.de)
 *  © 2007 Alex Maier (a.maier@tarent.de)
 *  © 2007, 2015, 2017, 2018 mirabilos (t.glaser@tarent.de)
 *  © 2006, 2007 Jens Neumaier (j.neumaier@tarent.de)
 *  © 2006 Nils Neumaier (n.neumaier@tarent.de)
 *  © 2007, 2008 Martin Pelzer (m.pelzer@tarent.de)
 *  © 2008, 2009 Christian Preilowski (c.thiel@tarent.de)
 *  © 2006, 2008, 2009 Thomas Schmitz (t.schmitz@tarent.de)
 *  © 2007 Robert Schuster (r.schuster@tarent.de)
 * and older code, Copyright © 2001–2007 ⮡ tarent GmbH and contributors.
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

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.tarent.octopus.config.TcCommonConfig;
import de.tarent.octopus.config.TcModuleConfig;
import de.tarent.octopus.config.TcConfig;
import de.tarent.octopus.config.TcPersonalConfig;
import de.tarent.octopus.request.TcRequest;
import de.tarent.octopus.content.TcContent;
import de.tarent.octopus.client.OctopusTask;

/**
 * Kontext, in dem ein Worker im Octopus existiert.
 * Über diesen Kontext kann ein Worker Request-, Content- Config- und Sessiondaten abfragen sowie setzen.
 *
 * @author <a href="mailto:sebastian@tarent.de">Sebastian Mancke</a>, <b>tarent GmbH</b>
 * @version 1.0
 */
public interface OctopusContext {

    public static final String CONTENT_FIELD_PREFIX = "CONTENT:";
    public static final String REQUEST_FIELD_PREFIX = "REQUEST:";
    public static final String SESSION_FIELD_PREFIX = "SESSION:";
    public static final String CONFIG_FIELD_PREFIX = "CONFIG:";

    /**
     * Key for a list of objects to clean up after the execution of a task.
     * The objects in this list have be of the type Runnable or Cleanable.
     */
    public static final String CLEANUP_OBJECT_LIST = "octopus.cleanup.objects";

    /**
     * Liefert den Inhalt eines Feldes aus einem der Contextbereiche Request, Content, Session oder Config
     * Aus welchem Bereich das Feld Stammt wird über das Prefix entschieden.
     * REQUEST:username z.B liefert den Parameter username aus dem Request.
     * Kein Prefix bedeutet: Suche in Content, Request, Session
     */
    public Object getContextField(String contextFieldName);

    /**
     * Setzt ein Feld in einem der Contextbereiche Content, Session, abhängig vom Verwendeten Prefix des fieldNames.
     * Ohne Prefix wird das Feld im Content gesetzt.
     * Das Setzen von Feldern im Request und in der Config ist verboten.
     */
    public void setContextField(String fieldName, Object value);

    public TcContent getContentObject();

    public TcConfig getConfigObject();

    public TcRequest getRequestObject();

    /**
     * Returns an OctopusTask instance to the supplied task in the current module over the OctopusClient API.
     * A call to this task uses the same session an therefore the same authentication.
     * On the other hand, the request and content are fresh.
     *
     * @param taskName The name of the target task in this module
     * @return A callable task for the target task in the current module
     */
    public OctopusTask getTask(String taskName);

    /**
     * Adds the supplied object to the List of Cleanup-Objects in the Content.
     * After the processing of the request, the octopus will call the close() method for each of these objects.
     * This will be done even if the request processing will throw an exception.
     */
    public void addCleanupCode(Closeable closeable);

    /**
     * Adds the supplied object to the List of Cleanup-Objects in the Content.
     * After the processing of the request, the octopus will call the run() method for each of these objects.
     * This will be done even if the request processing will throw an exception.
     */
    public void addCleanupCode(Runnable runnable);

    //===>  C O N T E N T  <===//

    public boolean contentContains(String key);

    public Iterator contentKeys();

    public String contentAsString(String key);

    public Object contentAsObject(String key);

    public void setContent(String key, Integer value);

    public void setContent(String key, List value);

    public void setContent(String key, Map value);

    public void setContent(String key, String value);

    public void setContent(String key, Object value);

    public void setContentError(Exception e);

    public void setContentError(String message);

    public void setContentStatus(String status);

    //===>  R E Q U E S T  <===//

    public int requestType();

    public String requestTypeName();

    public boolean requestContains(String key);

    public Object requestAsObject(String key);

    public String requestAsString(String key);

    public Integer requestAsInteger(String key);

    public Boolean requestAsBoolean(String key);

    //===>  C O N F I G  <===//

    public TcCommonConfig commonConfig();

    public TcPersonalConfig personalConfig();

    public TcModuleConfig moduleConfig();

    public File moduleRootPath();

    public String getModuleName();

    public String getTaskName();

    //===>  S E S S I O N  <===//

    public Object sessionAsObject(String key);

    public String sessionAsString(String key);

    public void setSession(String key, Object value);

    //===>  S T A T U S  <===//

    public void setStatus(String status);

    public String getStatus();

    public void setError(Exception e);

    public void setError(String message);

    //===>  C L O N E  <===//

    /**
     * Return a new octopus context with an empty request an empty content.
     *
     * @see #cloneContext(boolean, boolean)
     */
    public OctopusContext cloneContext();

    /**
     * Return a new octopus context. If the parameter <code>newRequest</code>
     * is <code>true</code> a new empty request will be created, otherwise
     * the new context will also use the current {@link TcRequest} instance.
     * If the parameter <code>newContent</code> is <code>true</code> a new
     * empty content will be created. Otherwise the current {@link TcContent}
     * will be used.
     *
     * @param newRequest
     * @param newContent
     * @return ?
     */
    public OctopusContext cloneContext(boolean newRequest, boolean newContent);
}
