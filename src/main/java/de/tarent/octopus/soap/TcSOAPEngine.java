/* $Id: TcSOAPEngine.java,v 1.5 2006/11/23 14:33:31 schmitz Exp $
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
 * by Sebastian Mancke and Michael Klink.
 * signature of Elmar Geese, 1 June 2002
 * Elmar Geese, CEO tarent GmbH
 */

package de.tarent.octopus.soap;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.zip.GZIPInputStream;

import org.apache.axis.AxisEngine;
import org.apache.axis.AxisFault;
import org.apache.axis.Constants;
import org.apache.axis.EngineConfiguration;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.configuration.FileProvider;
import org.apache.axis.encoding.DefaultSOAPEncodingTypeMappingImpl;
import org.apache.axis.encoding.TypeMapping;
import org.apache.axis.encoding.TypeMappingRegistry;
import org.apache.axis.encoding.ser.SimpleDeserializerFactory;
import org.apache.axis.encoding.ser.SimpleSerializerFactory;
import org.apache.axis.message.RPCElement;
import org.apache.axis.message.RPCParam;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.message.SOAPHeaderElement;
import org.apache.axis.server.AxisServer;
import org.apache.commons.logging.Log;
import org.xml.sax.SAXException;

import de.tarent.octopus.logging.LogFactory;
import de.tarent.octopus.request.TcEnv;
import de.tarent.octopus.request.TcRequest;
import de.tarent.octopus.resource.Resources;

/** 
 * Bereitstellung und Kapselung von SOAP Funktionalität
 * 
 * @author <a href="mailto:mancke@mancke-software.de">Sebastian Mancke</a>, <b>tarent GmbH</b>
 */
public class TcSOAPEngine {
    private static Log logger = LogFactory.getLog(TcSOAPEngine.class);
    public static final String NAMESPACE_URI = "http://schemas.tarent.de/";
    public static final String NAMESPACE_URI_TC = NAMESPACE_URI + "tccontact";
    
    boolean useSOAPNSAsModule = true;
    
    public static AxisEngine engine;

    TcEnv env;

    public TcSOAPEngine(TcEnv env) {
        this.env = env;
        
        if (null != env.getValue(TcEnv.KEY_USE_SOAP_NS_AS_MODULE))
            useSOAPNSAsModule = env.getValueAsBoolean(TcEnv.KEY_USE_SOAP_NS_AS_MODULE);
        
        // TODO: It would be better to bind the SOAP-Engine to a specific module
        //       and configure the type mapping for the modules of the octopus.
        String axisConfigFile = env.getValueAsString(TcEnv.KEY_PATHS_ROOT) + "axis-config.wsdd";
        if (new File(axisConfigFile).exists()) {
            logger.info(Resources.getInstance().get("SOAPENGINE_LOG_USING_AXIS_CONFIGURATION_FILE",axisConfigFile));
            EngineConfiguration engineConfiguration = new FileProvider(axisConfigFile);
            engine = new AxisServer(engineConfiguration);
        } else {
            logger.info(Resources.getInstance().get("SOAPENGINE_LOG_USING_AXIS_DEFAULT_CONFIGURATION"));
            engine = new AxisServer();
            registerTypes(engine.getTypeMappingRegistry(), env);
        }
    }

    /**
     * Registrieren der gewollten Default Type Mappings, wenn keine Axis Konfiguration angegeben wurde
	 * @deprecated not used since engine configuration is read from an engine configuration file
     */
    protected void registerTypes(TypeMappingRegistry reg, TcEnv env) {
        
        TypeMapping mapping = (TypeMapping) DefaultSOAPEncodingTypeMappingImpl.createWithDelegate(); 
        mapping.register(StringBuffer.class, 
                         Constants.XSD_STRING,
                         new SimpleSerializerFactory(StringBuffer.class, Constants.XSD_STRING),
                         new SimpleDeserializerFactory(String.class, Constants.XSD_STRING));
        
        //mapping.setSupportedEncodings(Constants.URIS_SOAP_ENC);
        for (int i=0; i<Constants.URIS_SOAP_ENC.length; i++) {
            reg.register(Constants.URIS_SOAP_ENC[i], mapping);
        }
        reg.registerDefault(mapping);
    }



    /**
     * Diese Methode analysiert eine SOAP-Anfrage.
     * 
     * @param inStream Die Anfrage
     * @param requestType der Anfragetyp
     * @param requestID die Anfrage-ID
     * @return ein generiertes Anfrage-Objekt
     * @throws TcSOAPException
     */
    public TcRequest[] readSoapRequests(InputStream inStream, int requestType, String requestID) throws TcSOAPException {
        logger.trace(TcSOAPEngine.class.getName() + " readSoapRequests " + new Object[] {inStream, new Integer(requestType), requestID});
        
        if (inStream != null) {
            List moduleList = new ArrayList();
            List taskList = new ArrayList();
            List paramsList = new ArrayList();
            List headerList = new ArrayList();
            analyseSoapRequest(inStream, headerList, moduleList, taskList, paramsList);
            TcRequest[] requests = new TcRequest[paramsList.size()];
            Iterator itModules = moduleList.iterator();
            Iterator itTasks = taskList.iterator();
            Iterator itParams = paramsList.iterator();
            for (int i = 0; itParams.hasNext(); i++) {
                Map params = (Map) itParams.next();
                TcRequest octRequest = new TcRequest(requestID);
                octRequest.setRequestType(requestType);
                octRequest.setRequestParameters(params);
                octRequest.setModule(itModules.next().toString());
                octRequest.setTask(itTasks.next().toString());
                requests[i] = octRequest;
            }
            return requests;
        } else
            return new TcRequest[0];
    }


    /**
     * Interpretiert die Eingabe als SOAP Message im RPC Style
     * 
     * Alle Bestandteile der Aufrufe werden in Maps zurückgegeben.
     * Dabei wird der Methodenname unter dem Key "task" abgelegt,
     * der Namespace der Methode unter "taskNamespaceURI" und
     * die Übergabeparameter unter ihren Namen. Sie können auch Maps
     * und Vektoren sein.
     */
    public void analyseSoapRequest(InputStream message, List headers, List modules, List tasks, List params) throws TcSOAPException {
        if (headers == null)
            headers = new ArrayList();
        if (modules == null)
            modules = new ArrayList();
        if (tasks == null)
            tasks = new ArrayList();
        if (params == null)
            params = new ArrayList();

        // TODO: Logging der Eingangsnachricht an eine andere Stelle verlagern.
        //       z.B. TcRequestProxy
        //         try {
        //             message = HttpHelper.logInput(message, env.get(TcEnv.KEY_LOG_SOAP_REQUEST_LEVEL), "SOAPENGINE_LOG_INPUT");
        //         } catch (IOException e1) {
        //             logger.warn(Resources.getInstance().get("SOAPENGINE_LOG_INPUT_LOG_ERROR"), e1);
        //             throw new TcSOAPException(e1);
        // 		}
        
        Message requestMsg = new Message(message, false);
        MessageContext msgContext = createMessageContext(engine);
        requestMsg.setMessageContext(msgContext);

        SOAPEnvelope env = null;
        List bodyElements = null;
        Iterator itElements = null;
        List headerElements = null;
		try {
			env = requestMsg.getSOAPEnvelope();
            bodyElements = env.getBodyElements();
            headerElements = env.getHeaders();
		} catch (AxisFault e) {
            logger.warn(Resources.getInstance().get("SOAPENGINE_LOG_SOAP_MSG_ERROR"), e);
            throw new TcSOAPException(e);
		}
        if (bodyElements != null)
            itElements = bodyElements.iterator();
		while (itElements != null && itElements.hasNext()) try {
            Object element = itElements.next();
            if (!(element instanceof RPCElement))
                continue;
            RPCElement rpcElem = (RPCElement) element;
            String namespace = rpcElem.getNamespaceURI();
            if (namespace == null)
                namespace = "";
            modules.add(getModuleNameFromNamespace(namespace));
            //request.put(TcRequest.PARAM_TASK_NAMESPACE_URI, namespace);
            tasks.add(rpcElem.getMethodName());
            params.add(bodyPartToMap(rpcElem));
        } catch(SAXException ex) {
            logger.warn(Resources.getInstance().get("SOAPENGINE_LOG_SAX_BODY_ERROR"), ex);
            throw new TcSOAPException(ex);
        } catch(ClassCastException cce) {
            logger.warn(Resources.getInstance().get("SOAPENGINE_LOG_CAST_BODY_ERROR"), cce);
            throw new TcSOAPException(cce);
        }
        logger.info("BODIES: " + params);
        if (headerElements != null)
            itElements = headerElements.iterator();
        while (itElements != null && itElements.hasNext()) try {
            SOAPHeaderElement hdrElem = (SOAPHeaderElement) itElements.next();
            headers.add(headerPartToMap(hdrElem));
        } catch(SAXException ex) {
            logger.warn(Resources.getInstance().get("SOAPENGINE_LOG_SAX_HEADER_ERROR"), ex);
            throw new TcSOAPException(ex);
        } catch(ClassCastException cce) {
            logger.warn(Resources.getInstance().get("SOAPENGINE_LOG_CAST_HEADER_ERROR"), cce);
            throw new TcSOAPException(cce);
        }
        logger.info("HEADERS: " + headers);
    }

    /**
     * Diese Methode macht aus einem RPC-Element eine Map mit Parametern
     * eines Octopus-Requests.
     * 
     * @param bodyPart Body-RPC-Element
     * @return Map der Parameter, des Moduls und des Tasks im RPC-Element
     * @throws SAXException
     */
    private Map bodyPartToMap(RPCElement bodyPart) throws SAXException {
        Map request = new TreeMap();
        List params = bodyPart.getParams();
        for (int i = 0; i < params.size(); i++) {
            RPCParam rpcParam = (RPCParam) params.get(i);
            Object value = replaceArrayWithList(rpcParam.getObjectValue());
            request.put(rpcParam.getName(), value);
        }
        return request;
    }
    
    /**
     * Traversiert die den Map-List-Baum und ersetzt alle Vorkommen von Array durch Listen.
     * <br>
     * Vorsicht: Es werden nur Maps, Listen und Arrays traversiert. 
     * Wenn ein Array in einem anderen Datencontainer enthalten ist, wird es nicht gefunden
     * <br>
     * TODO: Besser wäre natürlich ein direktes Deserialisieren als List durch Axis (derzeit nicht unterstützt).
     */
    protected Object replaceArrayWithList(Object o) {
        Object out = o;
        if (out instanceof Object[]) {
            out = Arrays.asList((Object[])out);
        }

        if (out instanceof List) {
            List list = (List)out;
            for (int i = 0; i < list.size(); i++) {
                Object element = list.get(i);
                Object replacement = replaceArrayWithList(element);

                // Hier ist ein echtes == gemeint, kein equals, 
                // da nur ausgetauscht werden muss, wenn sich die Objektinstanz wirklich geändert hat.
                if (replacement != element)
                    list.set(i, replacement);
            }            
        } 
        else if (out instanceof Map) {
            Map map = (Map)out;
            for (Iterator iter = map.entrySet().iterator(); iter.hasNext();) {
                Map.Entry entry = (Map.Entry)iter.next();
                
                Object element = entry.getValue();
                Object replacement = replaceArrayWithList(element);

                // Hier ist ein echtes == gemeint, kein equals, 
                // da nur ausgetauscht werden muss, wenn sich die Objektinstanz wirklich geändert hat.
                if (replacement != element)
                    map.put(entry.getKey(), replacement);
            }            
        }
        return out;
    }

    /**
     * Diese Methode macht aus einem Header-Element einn DOM Element.<br>
     * TODO: Dies ist noch auszuarbeiten 
     * 
     * @param headerPart Header-Element
     * @return Map der Key-Value-Zuordnung des Headers
     * @throws SAXException
     */
    private Map headerPartToMap(SOAPHeaderElement headerPart) throws SAXException {
        return Collections.singletonMap(headerPart.getName(), headerPart.getObjectValue());
    }
    
    /**
     * Erstellt einen Context mit Parametern der Nachricht
     * Da unser System die Informationen, für die der MessageContext
     * ist anderweitig bereit stellt, müssen wir da nicht viel rein tun.
     */
    private MessageContext createMessageContext(AxisEngine engine) {
        MessageContext msgContext = new MessageContext(engine);

        msgContext.setTransportName("http");
        return msgContext;
    }

    /**
     * Leitet an createMessageContext( AxisEngine engine )
     * mit engine = this.engine weiter;
     */
    public MessageContext createMessageContext() {
        return createMessageContext(engine);
    }

    /**
     * Hängt einen GZIP Filter über den übergebenen Stream
     */
    public static InputStream addGZIPFilterToInputStream(InputStream inStream) throws java.io.IOException {
        return new GZIPInputStream(inStream);
    }

    /**
     * Soll Später einen PGP Filter über den übergebenen Stream legen.
     * Ist aber nocht nicht implementiert
     */
    public static InputStream addPGPFilterToInputStream(InputStream inStream) throws java.io.IOException {
        return inStream;
    }

    /**
     * Liefert den Namespacebezeichner,
     * unter dem das Schema eines Modules festgelegt ist.
     *
     * Im Moment wird einfach der Modulname an eine bestimmte URL angehängt
     *
     * @param module Der Name eines Modules
     * @return Namespacebezeichner zu dem Modul
     */
    public String getNamespaceByModuleName(String module) {
        if (module == null || module.equals("") || module.equals("default"))
            return NAMESPACE_URI_TC;
        return NAMESPACE_URI + module;
    }

    /**
     * Liefert den Modulnamen, des Modules, das für einen bestimmter Namespace zuständig ist.
     *
     * Im Moment wird der Modulname einfach aus der URI extrahiert.
     *
     * @param namespaceUri Namespacebezeichner zu dem Modul
     * @return Zugehöriger Modulname
     */
    public String getModuleNameFromNamespace(String namespaceUri) {
        logger.debug("namespace: " + namespaceUri + "\n TcPrefix: " + NAMESPACE_URI_TC);

        String out = ""; // default module
        
        if (useSOAPNSAsModule) {
            if (namespaceUri.startsWith(NAMESPACE_URI_TC))
                out = namespaceUri.substring(NAMESPACE_URI_TC.length());
            else if (namespaceUri.startsWith(NAMESPACE_URI))
                out = namespaceUri.substring(NAMESPACE_URI.length());
            else
                logger.debug("Namespace startet nicht mit erlaubten Präfixen");
        } 
        
        if (out.startsWith("/"))
            out = out.substring(1);
        
        logger.debug("Modulname: " + out);
        return out;
    }


//     protected class SingletonSerializerFactory 
//         implements SerializerFactory {

//         Serializer serializer;
//         List mechanisms;
//         public SingletonSerializerFactory(Serializer serializer) {
//             this.serializer = serializer;
//         }
//         public javax.xml.rpc.encoding.Serializer getSerializerAs(java.lang.String mechanismType) {
//             return serializer;
//         }
//         public Iterator getSupportedMechanismTypes() {
//             if (mechanisms == null) {
//                 mechanisms = new ArrayList(1);
//                 mechanisms.add(Constants.AXIS_SAX);
//             }
//             return mechanisms.iterator();
//         }

//     }

    
}