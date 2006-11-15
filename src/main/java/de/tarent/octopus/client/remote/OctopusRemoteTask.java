/* $Id: OctopusRemoteTask.java,v 1.2 2006/11/15 10:38:13 hendrik Exp $
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

package de.tarent.octopus.client.remote;


import de.tarent.octopus.client.*;


import java.rmi.RemoteException;
import java.util.*;

import org.apache.axis.*;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;

import javax.xml.namespace.QName;
import org.apache.axis.encoding.XMLType;
import javax.xml.rpc.*;

/** 
 * Aufruf eines Task des Octopus als Client-Server variante.
 *
 * @author <a href="mailto:sebastian@tarent.de">Sebastian Mancke</a>, <b>tarent GmbH</b>
 */
public class OctopusRemoteTask implements OctopusTask {

    
    static Service axisSoapService;

    Call axisSoapCall;
    static {
        axisSoapService = new Service();
        axisSoapService.getEngine().setOption(AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
    }

    String moduleName;   
    String taskName;
    OctopusRemoteConnection connection;    
    List params;
    boolean connectionTracking = false;
    private static OctopusRemoteLog log = null;

    
    public OctopusRemoteTask() 
        throws javax.xml.rpc.ServiceException {
        axisSoapCall = (Call) axisSoapService.createCall();
        axisSoapCall.setMaintainSession(false);
        params = new ArrayList();
        if(log==null&&isConnectionTracking())log = new OctopusRemoteLog(this);
    }

    public OctopusRemoteTask(String moduleName, String taskName, OctopusRemoteConnection connection) 
        throws javax.xml.rpc.ServiceException {
        this();
        setConnection(connection);
        setModuleName(moduleName);
        setTaskName(taskName);
        if(connection!=null)setConnectionTracking(connection.isConnectionTracking());
        if(log==null&&isConnectionTracking())log = new OctopusRemoteLog(this);
        // Use of URL-Rewriting instead of Cookies at the Moment
        //         axisSoapCall.setMaintainSession(OctopusRemoteConnection.AUTH_TYPE_SESSION
        //                                         .equals(connection.getAuthType()));
    }

    
    public void add(String paramName, Object value, QName type) {
        params.add(value);
        axisSoapCall.addParameter(paramName, type, ParameterMode.IN);
    }

    /**
     * @return Gibt eine Refferenz auf sich selbst zur�ck. 
     *         Damit sind z.B. folgende Aufrufe m�glich: add().add().add ...
     */
    public OctopusTask add(String paramName, Object paramValue) {
        if (paramName == null)
            return this;

        QName xmlType = Constants.XSD_ANYTYPE;
        if (paramValue instanceof String)
            xmlType = Constants.XSD_STRING;
        else if (paramValue instanceof  Integer)
            xmlType = Constants.XSD_INTEGER;
        else if (paramValue instanceof Boolean)
            xmlType = Constants.XSD_BOOLEAN;
        else if (paramValue instanceof Long)
            xmlType = Constants.XSD_LONG;
        else if (paramValue instanceof Date)
            xmlType = Constants.XSD_DATE;
        
        add(paramName, paramValue, xmlType);
        return this;
    }

    
    public OctopusResult invoke() 
        throws OctopusCallException {

        axisSoapCall.setTargetEndpointAddress(connection.getServiceURL());
//         System.out.println("axis: "+axisSoapCall.getTargetEndpointAddress());
        
        axisSoapCall.setOperationName(new QName("http://schemas.tarent.de/"
                                                + getModuleName(), getTaskName()));
        axisSoapCall.setReturnType(XMLType.AXIS_VOID);
        //axisSoapCall.setReturnQName(Constants.XSD_ANYTYPE);
        
        if(isConnectionTracking())
        	log.startLogEntry(this);
        try {
            try {
                axisSoapCall.invoke(params.toArray());

                if(isConnectionTracking())
                    log.commitLogEntry();
            } catch (AxisFault e) {
                QName faultCode = e.getFaultCode();

                if(isConnectionTracking())
                    log.commitLogEntry();
                                   
                if (connection.isAutoLogin()
                    && ! connection.isIsDoingLogin()
                    && (OctopusConstants.SOAPF_AUTHENTICATION_UNKNOWN_ERROR.equals(faultCode)
                        || OctopusConstants.SOAPF_AUTHENTICATION_NEED_LOGIN.equals(faultCode)
                        || OctopusConstants.SOAPF_AUTHENTICATION_NOT_ENOUGH_RIGHTS.equals(faultCode)
                        || (OctopusConstants.SOAPF_AXIS_HTTP_ERROR.equals(faultCode) && e.getFaultString() != null && -1 != e.getFaultString().indexOf("401"))
                        )) {
                    // Try to Login and invoke Task again
                    connection.login();

                    try {
                        if(isConnectionTracking())
                            log.startLogEntry(this);
                        axisSoapCall.setTargetEndpointAddress(connection.getServiceURL());                         
                        axisSoapCall.invoke(params.toArray());
                    } finally {
                        if(isConnectionTracking())
                            log.commitLogEntry();                        
                    }                    

                } else
                    throw e;
            }
        } catch (AxisFault e) {
            QName faultCode = e.getFaultCode();
            throw new OctopusCallException(faultCode.getLocalPart(), "Error while calling <"+getModuleName()+"#"+getTaskName()+"> with soap.", e);
        } catch (RemoteException e) {
            //            e.printStackTrace();
            throw new OctopusCallException("Error while calling <"+getModuleName()+"#"+getTaskName()+"> with soap.", e);
        } 
        
        Map returnParams = axisSoapCall.getOutputParams();
        //System.out.println("returnParams: "+returnParams);
        OctopusRemoteResult oResult = new OctopusRemoteResult();

        for (Iterator iter = returnParams.keySet().iterator(); iter.hasNext();) {
            QName name = (QName)iter.next();
            oResult.addData(name.toString(), replaceArrayWithList(returnParams.get(name)));
        }        
        return oResult;
    }


    /**
     * Traversiert die den Map-List-Baum und ersetzt alle Vorkommen von Array durch Listen.
     * <br>
     * Vorsicht: Es werden nur Maps, Listen und Arrays traversiert. 
     * Wenn ein Array in einem anderen Datencontainer enthalten ist, wird es nicht gefunden
     * <br>
     * TODO: Besser w�re nat�rlich ein direktes Deserialisieren als List durch Axis (derzeit nicht unterst�tzt).
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
                // da nur ausgetauscht werden muss, wenn sich die Objektinstanz wirklich ge�ndert hat.
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
                // da nur ausgetauscht werden muss, wenn sich die Objektinstanz wirklich ge�ndert hat.
                if (replacement != element)
                    map.put(entry.getKey(), replacement);
            }            
        }
        return out;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String newTaskName) {
        this.taskName = newTaskName;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String newModuleName) {
        this.moduleName = newModuleName;
    }



    public OctopusRemoteConnection getConnection() {
        return connection;
    }

    public void setConnection(OctopusRemoteConnection newConnection) {
        this.connection = newConnection;
        setConnectionTracking(newConnection.isConnectionTracking());
    }


    public String resp() {
        try {
            return axisSoapCall.getResponseMessage().getSOAPPartAsString();
        } catch (AxisFault e) {
            return "Bei der Auswertung des Repsonsecontents trat ein Fehler auf.";
        }
    }

    public String req() {
        try {
            return axisSoapCall.getMessageContext().getRequestMessage()
                    .getSOAPPartAsString();
        } catch (AxisFault e) {
            return "Bei der Auswertung der Request trat ein Fehler auf.";
        }
    }

	/* (non-Javadoc)
	 * @see de.tarent.octopus.client.OctopusTask#setConnectionTracking(boolean)
	 */
	public void setConnectionTracking(boolean contrack) {
		connectionTracking = contrack;
	}

	/* (non-Javadoc)
	 * @see de.tarent.octopus.client.OctopusTask#isConnectionTracing()
	 */
	public boolean isConnectionTracking() {
		return connectionTracking;
	}


}
