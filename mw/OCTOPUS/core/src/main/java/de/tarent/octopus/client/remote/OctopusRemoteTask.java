package de.tarent.octopus.client.remote;

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
 *  © 2007, 2013, 2014, 2015, 2016, 2017, 2018, 2019
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
 * and older code, Copyright © 2001–2008 ⮡ tarent GmbH and contributors.
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

import de.tarent.octopus.client.OctopusCallException;
import de.tarent.octopus.client.OctopusConstants;
import de.tarent.octopus.client.OctopusResult;
import de.tarent.octopus.client.OctopusTask;
import lombok.extern.log4j.Log4j2;
import org.apache.axis.AxisEngine;
import org.apache.axis.AxisFault;
import org.apache.axis.ConfigurationException;
import org.apache.axis.Constants;
import org.apache.axis.EngineConfiguration;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.configuration.FileProvider;
import org.apache.axis.encoding.TypeMappingRegistry;
import org.apache.axis.encoding.XMLType;

import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Aufruf eines Task des Octopus als Client-Server variante.
 *
 * @author <a href="mailto:sebastian@tarent.de">Sebastian Mancke</a>, <b>tarent GmbH</b>
 */
@Log4j2
public class OctopusRemoteTask implements OctopusTask {
    private static final String AXIS_CONFIG = "/axis-config.wsdd";

    static Service axisSoapService;

    Call axisSoapCall;

    String moduleName;
    String taskName;
    OctopusRemoteConnection connection;
    List params;
    boolean connectionTracking = false;
    private static OctopusRemoteLog log = null;

    public OctopusRemoteTask()
      throws javax.xml.rpc.ServiceException {
        initAxisSOAPService();

        axisSoapCall = (Call) axisSoapService.createCall();
        axisSoapCall.setMaintainSession(false);
        params = new ArrayList();
        if (log == null && isConnectionTracking()) {
            log = new OctopusRemoteLog(this);
        }
    }

    public OctopusRemoteTask(String moduleName, String taskName, OctopusRemoteConnection connection)
      throws javax.xml.rpc.ServiceException {
        this();
        setConnection(connection);
        setModuleName(moduleName);
        setTaskName(taskName);
        if (connection != null) {
            setConnectionTracking(connection.isConnectionTracking());
        }
        if (log == null && isConnectionTracking()) {
            log = new OctopusRemoteLog(this);
        }
        // Use of URL-Rewriting instead of Cookies at the Moment
        //         axisSoapCall.setMaintainSession(OctopusRemoteConnection.AUTH_TYPE_SESSION
        //                                         .equals(connection.getAuthType()));
    }

    protected synchronized void initAxisSOAPService() {
        if (axisSoapService == null) {
            InputStream is = OctopusRemoteTask.class.getResourceAsStream(AXIS_CONFIG);
            if (is != null) {
                logger.info("Reading axis soap configuration from ressource: '" + AXIS_CONFIG + "'.");
                EngineConfiguration engineConfiguration = new FileProvider(is) {
                    public TypeMappingRegistry getTypeMappingRegistry() {
                        try {
                            return super.getTypeMappingRegistry();
                        } catch (ConfigurationException e) {
                            // throw anything wrapped by a runtime exception to show the error at beginning
                            throw new RuntimeException(e);
                        }
                    }
                };
                axisSoapService = new Service(engineConfiguration);
                axisSoapService.getEngine().setOption(AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
            } else {
                logger.info("No axis soap configuration available in ressources ('" + AXIS_CONFIG + "').");
                axisSoapService = new Service();
            }
        }
    }

    public void add(String paramName, Object value, QName type) {
        params.add(value);
        axisSoapCall.addParameter(paramName, type, ParameterMode.IN);
    }

    /**
     * @return Gibt eine Referenz auf sich selbst zurück.
     * Damit sind z.B. folgende Aufrufe möglich: add().add().add ...
     */
    public OctopusTask add(String paramName, Object paramValue) {
        if (paramName == null) {
            return this;
        }

        QName xmlType = Constants.XSD_ANYTYPE;
        if (paramValue instanceof String) {
            xmlType = Constants.XSD_STRING;
        } else if (paramValue instanceof Integer) {
            xmlType = Constants.XSD_INTEGER;
        } else if (paramValue instanceof Boolean) {
            xmlType = Constants.XSD_BOOLEAN;
        } else if (paramValue instanceof Long) {
            xmlType = Constants.XSD_LONG;
        }

        add(paramName, paramValue, xmlType);
        return this;
    }

    public OctopusResult invoke()
      throws OctopusCallException {

        axisSoapCall.setTargetEndpointAddress(connection.getServiceURL());

        axisSoapCall.setOperationName(new QName("http://schemas.tarent.de/"
          + getModuleName(), getTaskName()));
        axisSoapCall.setReturnType(XMLType.AXIS_VOID);
        //axisSoapCall.setReturnQName(Constants.XSD_ANYTYPE);

        if (isConnectionTracking()) {
            log.startLogEntry(this);
        }
        try {
            try {
                axisSoapCall.invoke(params.toArray());

                if (isConnectionTracking()) {
                    log.commitLogEntry();
                }
            } catch (AxisFault e) {
                QName faultCode = e.getFaultCode();

                if (isConnectionTracking()) {
                    log.commitLogEntry();
                }

                if (connection.isAutoLogin()
                  && !connection.isIsDoingLogin()
                  && (OctopusConstants.SOAPF_AUTHENTICATION_UNKNOWN_ERROR.equals(faultCode)
                  || OctopusConstants.SOAPF_AUTHENTICATION_NEED_LOGIN.equals(faultCode)
                  || OctopusConstants.SOAPF_AUTHENTICATION_NOT_ENOUGH_RIGHTS.equals(faultCode)
                  || (OctopusConstants.SOAPF_AXIS_HTTP_ERROR.equals(faultCode) && e.getFaultString() != null &&
                  -1 != e.getFaultString().indexOf("401"))
                )) {
                    // Try to Login and invoke Task again
                    connection.login();

                    try {
                        if (isConnectionTracking()) {
                            log.startLogEntry(this);
                        }
                        axisSoapCall.setTargetEndpointAddress(connection.getServiceURL());
                        axisSoapCall.invoke(params.toArray());
                    } finally {
                        if (isConnectionTracking()) {
                            log.commitLogEntry();
                        }
                    }
                } else {
                    throw e;
                }
            }
        } catch (AxisFault e) {
            QName faultCode = e.getFaultCode();
            throw new OctopusCallException(faultCode.getLocalPart(),
              "Error while calling <" + getModuleName() + "#" + getTaskName() + "> with soap.", e);
        } catch (RemoteException e) {
            //            e.printStackTrace();
            throw new OctopusCallException("Error while calling <" + getModuleName() + "#" + getTaskName() + "> with soap.", e);
        }

        Map returnParams = axisSoapCall.getOutputParams();
        OctopusRemoteResult oResult = new OctopusRemoteResult();

        for (Iterator iter = returnParams.keySet().iterator(); iter.hasNext(); ) {
            QName name = (QName) iter.next();
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
     * TODO: Besser wäre natürlich ein direktes Deserialisieren als List durch Axis (derzeit nicht unterstützt).
     */
    protected Object replaceArrayWithList(Object o) {
        Object out = o;
        if (out instanceof Object[]) {
            out = Arrays.asList((Object[]) out);
        }

        if (out instanceof List) {
            List list = (List) out;
            for (int i = 0; i < list.size(); i++) {
                Object element = list.get(i);
                Object replacement = replaceArrayWithList(element);

                // Hier ist ein echtes == gemeint, kein equals,
                // da nur ausgetauscht werden muss, wenn sich die Objektinstanz wirklich geändert hat.
                if (replacement != element) {
                    list.set(i, replacement);
                }
            }
        } else if (out instanceof Map) {
            Map map = (Map) out;
            for (Iterator iter = map.entrySet().iterator(); iter.hasNext(); ) {
                Map.Entry entry = (Map.Entry) iter.next();

                Object element = entry.getValue();
                Object replacement = replaceArrayWithList(element);

                // Hier ist ein echtes == gemeint, kein equals,
                // da nur ausgetauscht werden muss, wenn sich die Objektinstanz wirklich geändert hat.
                if (replacement != element) {
                    map.put(entry.getKey(), replacement);
                }
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
