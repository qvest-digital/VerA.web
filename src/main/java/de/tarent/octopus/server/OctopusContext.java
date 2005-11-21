/* $Id: OctopusContext.java,v 1.1.1.1 2005/11/21 13:33:38 asteban Exp $
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

package de.tarent.octopus.server;

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

/**
 * Kontext, in dem ein Worker im Octopus existiert.
 * Über diesen Kontext kann ein Worker Request-, Content- Config- und Sessiondaten abfragen sowie setzen.
 * 
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
     * Liefert den Inhalt eines Feldes aus einem der Contextbereiche Request, Content, Session oder Config
     * Aus welchem Bereich das Feld Stammt wird über das Prefix entschieden.
     * REQUEST:username z.B liefert den Parameter username aus dem Request.
     * Kein Prefix bedeutet: Suche in Content, Request, Session
     *
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
}
