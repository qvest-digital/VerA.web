package de.tarent.octopus.content;

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
import de.tarent.octopus.request.TcRequest;
import de.tarent.octopus.server.Closeable;
import de.tarent.octopus.server.OctopusContext;

import java.io.File;
import java.util.*;

/**
 * Kontainer zur Speicherung von TcContent, TcRequest und TcConfig.
 */
public class TcAll implements OctopusContext {
    private TcContent content;
    private TcConfig config;
    private TcRequest request;

    /**
     * Erzeugt ein TcAll-Objekt
     */
    public TcAll(TcRequest tcRequest, TcContent tcContent, TcConfig tcConfig) {
        this.request = tcRequest;
        this.content = tcContent;
        this.config = tcConfig;
    }

    /**
     * Erzeugt ein TcAll-Objekt mit dem übergebenem Request und einem neuen (leeren) Content.
     *
     * @param requesttype siehe de.tarent.octopus.request.HttpHelper.REQUEST_TYPE_*
     */
    public TcAll(Map requestmap, int requesttype) {
        this.request = new TcRequest(TcRequest.createRequestID());
        this.request.setRequestParameters(requestmap);
        this.request.setRequestType(requesttype);
        this.content = new TcContent();
        this.config = new TcConfigClone();
    }

    public Object getContextField(String fieldName) {
        if (fieldName.startsWith(CONTENT_FIELD_PREFIX)) {
            // Aus dem Content
            return contentAsObject(fieldName.substring(CONTENT_FIELD_PREFIX.length()));
        } else if (fieldName.startsWith(REQUEST_FIELD_PREFIX)) {
            // Aus dem Request
            return requestAsObject(fieldName.substring(REQUEST_FIELD_PREFIX.length()));
        } else if (fieldName.startsWith(SESSION_FIELD_PREFIX)) {
            // Aus der Session
            return sessionAsObject(fieldName.substring(SESSION_FIELD_PREFIX.length()));
        } else if (fieldName.startsWith(CONFIG_FIELD_PREFIX)) {
            // Aus der Config
            return moduleConfig().getParamAsObject(fieldName.substring(CONFIG_FIELD_PREFIX.length()));
        }

        // DEFAULT, ohne Prefix:
        // Wenn vorhanden aus dem Content, sonst aus dem Request oder der Session.
        if (null != contentAsObject(fieldName)) {
            return contentAsObject(fieldName);
        } else if (null != requestAsObject(fieldName)) {
            return requestAsObject(fieldName);
        } else {
            return sessionAsObject(fieldName);
        }
    }

    public void setContextField(String fieldName, Object value) {
        if (fieldName.startsWith(CONTENT_FIELD_PREFIX)) {
            // In den Content
            setContent(fieldName.substring(CONTENT_FIELD_PREFIX.length()), value);
        } else if (fieldName.startsWith(REQUEST_FIELD_PREFIX)) {
            // In den Request - IST NICHT ERLAUBT
            throw new RuntimeException(
              "Anfragefehler: Setzen von Parametern im Request ist nicht erlaubt.");
        } else if (fieldName.startsWith(SESSION_FIELD_PREFIX)) {
            // In die Session
            setSession(fieldName.substring(SESSION_FIELD_PREFIX.length()), value);
        } else if (fieldName.startsWith(CONFIG_FIELD_PREFIX)) {
            // In die Config - IST NICHT ERLAUBT
            throw new RuntimeException(
              "Anfragefehler: Setzen von Parametern der Config ist nicht erlaubt.");
        } else {
            // DEFAULT, ohne Prefix:
            // In den Content
            setContent(fieldName, value);
        }
    }

    public OctopusTask getTask(String taskName) {
        return request.getTask(taskName);
    }

    public void addCleanupCode(Closeable closeable) {
        addCleanupCodeInternal(closeable);
    }

    public void addCleanupCode(Runnable runnable) {
        addCleanupCodeInternal(runnable);
    }

    protected void addCleanupCodeInternal(Object cleanable) {
        List cleanableList = (List) contentAsObject(CLEANUP_OBJECT_LIST);
        if (cleanableList == null) {
            cleanableList = new ArrayList();
            setContent(CLEANUP_OBJECT_LIST, cleanableList);
        }
        cleanableList.add(cleanable);
    }

    /**
     * @return TcContent-Object
     */
    public TcContent getContentObject() {
        return content;
    }

    /**
     * @return TcConfig-Object
     */
    public TcConfig getConfigObject() {
        return config;
    }

    /**
     * @return TcRequest-Object
     */
    public TcRequest getRequestObject() {
        return request;
    }

    //===>  C O N T E N T  <===//

    public boolean contentContains(String key) {
        return content.getAsObject(key) != null;
    }

    public Iterator contentKeys() {
        return content.getKeys();
    }

    public String contentAsString(String key) {
        return content.getAsString(key);
    }

    public Object contentAsObject(String key) {
        return content.getAsObject(key);
    }

    public void setContent(String key, Integer value) {
        content.setField(key, value);
    }

    public void setContent(String key, List value) {
        content.setField(key, value);
    }

    public void setContent(String key, Map value) {
        content.setField(key, value);
    }

    public void setContent(String key, String value) {
        content.setField(key, value);
    }

    public void setContent(String key, Object value) {
        content.setField(key, value);
    }

    public void setContentError(Exception e) {
        content.setError(e);
    }

    public void setContentError(String message) {
        content.setError(message);
    }

    public void setContentStatus(String status) {
        content.setStatus(status);
    }

    //===>  R E Q U E S T  <===//

    public int requestType() {
        return request.getRequestType();
    }

    public String requestTypeName() {
        return TcRequest.getRequestTypeName(request.getRequestType());
    }

    public boolean requestContains(String key) {
        return request.containsParam(key);
    }

    public Object requestAsObject(String key) {
        return request.getParam(key);
    }

    public String requestAsString(String key) {
        return request.get(key);
    }

    public Integer requestAsInteger(String key) {
        return new Integer(request.getParamAsInt(key));
    }

    public Boolean requestAsBoolean(String key) {
        return Boolean.valueOf(request.getParameterAsBoolean(key));
    }

    //===>  C O N F I G  <===//

    public TcCommonConfig commonConfig() {
        return config.getCommonConfig();
    }

    public TcPersonalConfig personalConfig() {
        return (TcPersonalConfig) config.getPersonalConfig();
    }

    public TcModuleConfig moduleConfig() {
        return config.getModuleConfig();
    }

    public File moduleRootPath() {
        return config.getModuleRootPath();
    }

    public String getModuleName() {
        return request.getModule();
    }

    public String getTaskName() {
        return request.getTask();
    }

    //===>  S E S S I O N  <===//

    public Object sessionAsObject(String key) {
        return config.getSessionValueAsObject(key);
    }

    public String sessionAsString(String key) {
        return config.getSessionValue(key);
    }

    public void setSession(String key, Object value) {
        config.setSessionValue(key, value);
    }

    //===>  S T A T U S  <===//

    public void setStatus(String status) {
        content.setStatus(status);
    }

    public String getStatus() {
        return content.getStatus();
    }

    public void setError(Exception e) {
        content.setError(e);
    }

    public void setError(String message) {
        content.setError(message);
    }

    private class TcConfigClone extends TcConfig {
        Map session = new HashMap();

        TcConfigClone() {
            super(null, null, null);
        }

        public void setSessionValue(String key, Object value) {
            session.put(key, value);
        }

        public String getSessionValue(String key) {
            if (session.containsKey(key)) {
                return session.get(key).toString();
            } else {
                return null;
            }
        }

        public Object getSessionValueAsObject(String key) {
            return session.get(key);
        }
    }

    /**
     * {@inheritDoc}
     */
    public OctopusContext cloneContext() {
        return cloneContext(true, true);
    }

    /**
     * {@inheritDoc}
     */
    public OctopusContext cloneContext(boolean newRequest, boolean newContent) {
        TcRequest r = newRequest ? new TcRequest() : getRequestObject();
        TcContent c = newContent ? new TcContent() : getContentObject();
        if (newRequest) {
            r.setRequestParameters(new LinkedHashMap());
        } else {
            r.setRequestParameters(new LinkedHashMap(r.getRequestParameters()));
        }
        r.setModule(getModuleName());
        return new TcAll(r, c, getConfigObject());
    }
}
