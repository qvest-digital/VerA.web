/* $Id: TcAll.java,v 1.5 2006/09/26 14:26:04 christoph Exp $
 * 
 * tarent-octopus, Webservice Data Integrator and Applicationserver
 * Copyright (C) 2002 tarent GmbH
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 * tarent GmbH., hereby disclaims all copyright
 * interest in the program 'tarent-octopus'
 * (which makes passes at compilers) written
 * by Wolfgang Klein and Christoph Jerolimov.
 * signature of Elmar Geese, 1 June 2002
 * Elmar Geese, CEO tarent GmbH
 */

package de.tarent.octopus.content;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
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
		request = tcRequest;
		content = tcContent;
		config = tcConfig;
	}
	
	/**
	 * Erzeugt ein TcAll Object mit dem übergebenem Request und einem neuen (leeren) Content.
	 * 
	 * @param requestmap
	 * @param requesttype siehe de.tarent.octopus.request.HttpHelper.REQUEST_TYPE_*
	 */
	public TcAll(Map requestmap, int requesttype) {
		request = new TcRequest(TcRequest.createRequestID());
		request.setRequestParameters(requestmap);
		request.setRequestType(requesttype);
		content = new TcContent();
		config = new TcConfig_Clone();
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
	
    /**
     * TODO: Auf PersonalConfig ändern!
     */
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
		return new TcAll(
				newRequest ? new TcRequest() : getRequestObject(),
				newContent ? new TcContent() : getContentObject(),
				getConfigObject());
	}
}
