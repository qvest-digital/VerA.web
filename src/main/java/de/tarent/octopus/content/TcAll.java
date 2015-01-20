/**
 * tarent-octopus, Webservice Data Integrator and Application Server
 * Copyright © 2002–2015 tarent solutions GmbH and its contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package de.tarent.octopus.content;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import de.tarent.octopus.config.TcCommonConfig;
import de.tarent.octopus.config.TcConfig;
import de.tarent.octopus.config.TcModuleConfig;
import de.tarent.octopus.config.TcPersonalConfig;
import de.tarent.octopus.request.TcRequest;
import de.tarent.octopus.server.OctopusContext;
import de.tarent.octopus.server.Closeable;
import java.util.ArrayList;
import de.tarent.octopus.client.OctopusTask;


/** 
 * Kontainer zur Speicherung von TcContent, TcRequest und TcConfig.
 */
public class TcAll
    implements OctopusContext {

	private TcContent content;
	private TcConfig config;
	private TcRequest request;
	
	/**
	 * Erzeugt ein TcAll Object.
	 * 
	 * @param tcRequest
	 * @param tcContent
	 * @param tcConfig
	 */
	public TcAll(TcRequest tcRequest, TcContent tcContent, TcConfig tcConfig) {
		this.request = tcRequest;
		this.content = tcContent;
		this.config = tcConfig;
	}
	
	/**
	 * Erzeugt ein TcAll Object mit dem übergebenem Request und einem neuen (leeren) Content.
	 * 
	 * @param requestmap
	 * @param requesttype siehe de.tarent.octopus.request.HttpHelper.REQUEST_TYPE_*
	 */
	public TcAll(Map requestmap, int requesttype) {
		this.request = new TcRequest(TcRequest.createRequestID());
		this.request.setRequestParameters(requestmap);
		this.request.setRequestType(requesttype);
		this.content = new TcContent();
		this.config = new TcConfig_Clone();
	}


    public Object getContextField(String fieldName) {
        // Aus dem Content
        if (fieldName.startsWith(CONTENT_FIELD_PREFIX)) {
            return contentAsObject(fieldName.substring(CONTENT_FIELD_PREFIX.length()));
        }

        // Aus dem Request
        else if (fieldName.startsWith(REQUEST_FIELD_PREFIX)) {
            return requestAsObject(fieldName.substring(REQUEST_FIELD_PREFIX.length()));
        }

        // Aus der Session
        else if (fieldName.startsWith(SESSION_FIELD_PREFIX)) {
            return sessionAsObject(fieldName.substring(SESSION_FIELD_PREFIX.length()));
        }
        
        // Aus der Config
        else if (fieldName.startsWith(CONFIG_FIELD_PREFIX)) {
            return moduleConfig().getParamAsObject(fieldName.substring(CONFIG_FIELD_PREFIX.length()));
        }

        // DEFAULT, ohne Prefix: 
        // Wenn vorhanden aus dem Content, sonst aus dem Request oder der Session.        
        if (null != contentAsObject(fieldName))
            return contentAsObject(fieldName);
        else if (null != requestAsObject(fieldName))
            return requestAsObject(fieldName);
        else
            return sessionAsObject(fieldName);
    }

    public void setContextField(String fieldName, Object value) {
        // In den Content
        if (fieldName.startsWith(CONTENT_FIELD_PREFIX)) {
            setContent(fieldName.substring(CONTENT_FIELD_PREFIX.length()), value);
        }

        // In den Request - IST NICHT ERLAUBT
        else if (fieldName.startsWith(REQUEST_FIELD_PREFIX)) {
            throw new RuntimeException("Anfragefehler: Setzen von Parametern im Request ist nicht erlaubt.");
        }

        // In die Session
        else if (fieldName.startsWith(SESSION_FIELD_PREFIX)) {
            setSession(fieldName.substring(SESSION_FIELD_PREFIX.length()), value);
        }

        // In die Config - IST NICHT ERLAUBT
        else if (fieldName.startsWith(CONFIG_FIELD_PREFIX)) {
            throw new RuntimeException("Anfragefehler: Setzen von Parametern der Config ist nicht erlaubt.");
        }

        // DEFAULT, ohne Prefix: 
        // In den Content
        else {
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
        List cleanableList = (List)contentAsObject(CLEANUP_OBJECT_LIST);
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
		return (TcPersonalConfig)config.getPersonalConfig();
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
	
	private class TcConfig_Clone extends TcConfig {
		Map session = new HashMap();
		
		public TcConfig_Clone() {
			super(null, null, null);
		}
		
		public void setSessionValue(String key, Object value) {
			session.put(key, value);
		}
		
		public String getSessionValue(String key) {
			if (session.containsKey(key))
				return session.get(key).toString();
			else
				return null;
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
		if (newRequest)
			r.setRequestParameters(new LinkedHashMap());
		else
			r.setRequestParameters(new LinkedHashMap(r.getRequestParameters()));
		r.setModule(getModuleName());
		return new TcAll(r, c, getConfigObject());
	}
}
