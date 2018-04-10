package de.tarent.octopus.content;
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
		if (null != contentAsObject(fieldName))
			return contentAsObject(fieldName);
		else if (null != requestAsObject(fieldName))
			return requestAsObject(fieldName);
		else
			return sessionAsObject(fieldName);
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

	private class TcConfigClone extends TcConfig {
		Map session = new HashMap();

		TcConfigClone() {
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
