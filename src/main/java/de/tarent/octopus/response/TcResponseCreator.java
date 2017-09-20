package de.tarent.octopus.response;

/*-
 * tarent-octopus, Webservice Data Integrator and Application Server
 * Copyright © 2002–2017 tarent solutions GmbH and its contributors
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
import de.tarent.octopus.logging.LogFactory;
import de.tarent.octopus.request.directCall.TcDirectCallResponse;
import de.tarent.octopus.config.TcConfig;
import de.tarent.octopus.config.TcModuleConfig;
import de.tarent.octopus.content.TcContent;
import de.tarent.octopus.request.TcRequest;
import de.tarent.octopus.request.TcResponse;
import de.tarent.octopus.util.ConsistentMap;

import java.util.*;

import org.apache.commons.logging.Log;

/** 
 * View Komponente, die die Ausgabe steuert.
 * <br><br>
 * Abhängig von einer TcResponseDescription wird eine
 * Engine gestartet, die für den entsprechenden ResponseType zuständig ist.
 * Dieser ResponseType kann über die TcResponseDescription abgefragt werden.
 *
 * Der Content-Type der Ausgabe wird hier auf 
 *  theContent.get( "responseParams.ContentType" ) oder auf config.getDefaultContentType()
 * gesetzt, kann aber von der benutzten Engine später überschrieben werden.
 *
 * <br><br>
 * Bisher gibt es folgende Responsetypen:
 * <br>- simple
 * <br>- velocity
 * <br>- raw
 * <br>- soap
 * <br>- xslt
 *
 * <br><br>
 * Was die einzelnen Ausgabe Engines als Parameter erwarten und wie sie funktionieren muss im einzelnen 
 * nachgelesen werden.
 * 
 * @see TcVelocityResponseEngine
 * @see TcRawResponseEngine
 * @see TcSoapResponseEngine
 * @see TcXsltResponseEngine
 * @see TcSimpleResponseEngine
 * 
 * @author <a href="mailto:mancke@mancke-software.de">Sebastian Mancke</a>, <b>tarent GmbH</b>
 * @author <a href="mailto:H.Helwich@tarent.de">Hendrik Helwich</a>, <b>tarent GmbH</b>
 */
public class TcResponseCreator {
    private static Log logger = LogFactory.getLog(TcResponseCreator.class);
    private Map responseEngines;

    /**
     * Initialisierung mit Logger
     */
    public TcResponseCreator() {
        responseEngines = new LinkedHashMap();
    }

    /**
     * Erstellen der Response.
     * <br><br>
     * Abhängig vom Rückgabewert der Methode TcResponseDescription.getResponseType() 
     * wird über Reflaction eine entsprechende Engine geladen und die Ausgabe erzeugt.
     *
     * @param config Konfiguration
     * @param tcResponse Ausgabeobjekt, für die Rückgabe an den Client
     * @param theContent Datencontainer mit den Ausgabedaten
     * @param desc Parameter, die die Ausgabeart beeinflussen
     */
    public void sendResponse(TcModuleConfig moduleConfig, TcConfig config, TcResponse tcResponse, TcContent theContent, TcResponseDescription desc, TcRequest request)
        throws ResponseProcessingException {

        String engineIndex = moduleConfig.getName() + desc.getResponseType();
        TcResponseEngine engine = (TcResponseEngine) responseEngines.get(engineIndex);
        //falls die benötgte Engine noch nicht bekannt ist, wird versucht sie zu initialisieren.
        if (engine == null) {
            try {
                Class clazz =
                    Class.forName(
                        "de.tarent.octopus.response.Tc"
                            + desc.getResponseType().substring(0, 1).toUpperCase()
                            + desc.getResponseType().substring(1)
                            + "ResponseEngine");
                engine = (TcResponseEngine) clazz.newInstance();
                engine.init(moduleConfig, config.getCommonConfig());
            } catch (InstantiationException e) {
                throw new ResponseProcessingException(
                    "Die Response-Engine \"" + desc.getResponseType() + "\" ist nicht bekannt.");
            } catch (IllegalAccessException e) {
                throw new ResponseProcessingException(
                    "Die Response-Engine \"" + desc.getResponseType() + "\" ist nicht bekannt.");
            } catch (ClassNotFoundException e) {
                throw new ResponseProcessingException(
                    "Die Response-Engine \"" + desc.getResponseType() + "\" ist nicht bekannt.");
            }
            responseEngines.put(engineIndex, engine);
        }

        String contentType = theContent.getAsString("responseParams.ContentType");
		if (contentType == null)
			contentType = config.getDefaultContentType();
		tcResponse.setContentType(contentType);

		String cacheTime = theContent.getAsString("responseParams.CachingTime"); // millis
		String cacheParam = theContent.getAsString("responseParams.CachingParam"); // free text
		if (cacheTime == null && cacheParam == null) {
			tcResponse.setCachingTime(0);
		} else {
			if (cacheTime == null) {
				tcResponse.setCachingTime(0, cacheParam);
			} else {
				tcResponse.setCachingTime(Integer.parseInt(cacheTime), cacheParam);
			}
		}
		
		if (engine instanceof TcRPCResponseEngine && tcResponse instanceof TcDirectCallResponse) {
			pushRPCOutputParams(config, (TcDirectCallResponse) tcResponse, theContent, desc);
		} else {
			engine.sendResponse(config, tcResponse, theContent, desc, request);
		}
	}

    public void pushRPCOutputParams(TcConfig config, TcDirectCallResponse response, TcContent theContent, TcResponseDescription desc) {
        // Wenn ein ContentType angegeben wurde, 
        // wurde dieser bereits im TcRequestCreator gesetzt.
        String contentType = theContent.getAsString("responseParams.ContentType");
        if (contentType == null)
            response.setContentType("NONE:DirectCall");
        

        Map outputFields = refineOutputFields(theContent.getAsObject(TcRPCResponseEngine.RPC_RESPONSE_OUTPUT_FIELDS));
        for (Iterator iter = outputFields.keySet().iterator(); iter.hasNext();) {
            String fieldNameOutput = (String)iter.next();
            String fieldNameContent = (String)outputFields.get(fieldNameOutput);
            response.addResponseObject(fieldNameOutput, theContent.getAsObject(fieldNameContent));
        } 
        
        logger.debug("Gebe Daten per DirectCall zurück. Antwort auf Methode");
    }
    

    /**
     */
    public static Map refineOutputFields(Object fieldsObject) {        
        if (fieldsObject instanceof Map) {
            return (Map) fieldsObject;
        } else if (fieldsObject instanceof List) {
            ConsistentMap map = new ConsistentMap();
            for (Iterator iter = ((List)fieldsObject).iterator(); iter.hasNext();) {
                Object field = iter.next();
                map.put(field, field);
            }             
            return map;            
        } else {
            LinkedHashMap map = new LinkedHashMap();
            if (fieldsObject != null && fieldsObject.toString().length() != 0)
                map.put(fieldsObject.toString(), fieldsObject.toString());
            return map;            
        }
    }

}
