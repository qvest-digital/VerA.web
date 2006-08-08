/* $Id: TcModuleConfig.java,v 1.7 2006/08/08 13:46:39 nils Exp $
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

package de.tarent.octopus.config;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.wsdl.Definition;
import javax.wsdl.WSDLException;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLWriter;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.axis.encoding.TypeMappingRegistry;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.tarent.octopus.cronjobs.CronJob;
import de.tarent.octopus.request.TcEnv;
import de.tarent.octopus.request.TcTaskList;
import de.tarent.octopus.security.TcSecurityException;
import de.tarent.octopus.server.LoginManager;
import de.tarent.octopus.server.PersonalConfig;
import de.tarent.octopus.soap.TcSOAPEngine;
import de.tarent.octopus.util.DataFormatException;
import de.tarent.octopus.util.Xml;
import org.w3c.dom.Attr;
import org.xml.sax.SAXException;

import de.tarent.octopus.resource.Resources;

/**
 * Beinhaltet die Einstellungen zu eimem Module.
 * Ein Module ist eine Sammlung von zusammengehörigen Dateien und Einstellungen.
 * Dies können z.B. ein paar TcContentWorker und ein paar Templates sein, die zusammen eine 
 * bestimmte Anwendung von tc bilden.
 *
 * @author <a href="mailto:mancke@mancke-software.de">Sebastian Mancke</a>, <b>tarent GmbH</b>
 */
public class TcModuleConfig {
    //
    // Konstanten
    //
    public final static String PREFS_DATA_ACCESS = "dataAccess";
    public final static String PREFS_PARAMS = "params";
    public static final String CONFIG_PARAM_ON_UNAUTHORIZED = "onUnauthorized";
    public static final String CONFIG_PARAM_PERSONAL_CONFIG_CLASS = "personalConfigClass";

    /**
     * Default Factory, die verwendet wird, wenn in der Config keine Angaben zu einem Worker gemacht wurden.
     */
    public static final String DEFAULT_WORKER_FACTORY_NAME = "de.tarent.octopus.content.DirectWorkerFactory";

    //
    // Variablen
    //
    protected String escapedUserHome = System.getProperty("user.home").replaceAll("\\\\","\\\\\\\\");
    protected String name = null;
    protected File realPath = null;
    protected String description = "";
    protected ClassLoader classLoader = null;
    protected LoginManager loginManager = null;
    protected Constructor personalConfigConstructor = null;

    /** Der Logger */
    private static Logger logger = Logger.getLogger(TcModuleConfig.class.getName());

    /*
     * Config Parameter
     */
    protected Map configParams;
    private Map rawConfigParams = new HashMap();
    private Preferences configPrefs;
    
    /**
     * Map mit den Einstellungen für den LoginManager.
     */
    protected Map loginManagerParams = new HashMap();

    /**
     * Map mit den verfügbaren Datenquellen.
     * <br>Eine Map, in der unter den Namen der Datenquellen wieder
     * Maps mit String Keys und String Values abgelegt sind.
     */
    protected Map dataAccess;

    /**
     * Map mit den deklarierten Workern.
     * Eindeutige Namen als String Keys ContentWorkerDeclatarion-Objekte als Values.
     */
    protected Map contentWorkersDeclarations;

    /**
     * Verfügbare Tasks
     */
    protected TcTaskList taskList;
    protected Definition wsdlDefinition;

	private Map otherNodes = new HashMap();

    /**
     * Initialisiert diese Config mit einem geparsten Document.
     * Dieses Document muss der DTD ModuleConfig genügen.
     */
    public TcModuleConfig(String name, File realPath, Document document, Preferences preferences) throws DataFormatException {
        this.name = name;
        this.realPath = realPath;
        this.configPrefs = preferences;
        
        collectSectionsFromDocument(document, preferences);
        
        configParams = new HashMap(rawConfigParams);
        override("Parameter von " + name, configParams, preferences.node(PREFS_PARAMS));

        if (taskList == null)
            throw new DataFormatException("Die Konfigurationsdatei muss einen Tasks-Abschnitt besitzen.");
        
        try {
            logger.config("Exportiere WSDL-Darstellung des Moduls");
            wsdlDefinition = taskList.getPortDefinition().getWsdlDefinition(true, "http://schema.tarent.de/" + name, name, "http://localhost:8080/octopus");
            OutputStream os = new FileOutputStream(new File(realPath, "module.wsdl"));
            WSDLFactory factory;
            factory = WSDLFactory.newInstance();
            WSDLWriter writer = factory.newWSDLWriter();
            writer.writeWSDL(wsdlDefinition, os);
            os.close();
            //logger.config(name + ": " + sw.toString());
        } catch (WSDLException e) {
            logger.log(Level.SEVERE, "WSDL-Fehler", e);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "IO-Fehler", e);
        }

    }

    /**
     * @param document
     * @param preferences
     * @throws DataFormatException
     */
    private void collectSectionsFromDocument(Document document, Preferences preferences) throws DataFormatException {
        
        NodeList sections = document.getDocumentElement().getChildNodes();

        
        // Auslesen der übrigen Section mit den in der Application angegebenen Namen
        for (int i = 0; i < sections.getLength(); i++) {
            Node currNode = sections.item(i);
            
            if ("include".equals(currNode.getNodeName())) {
                
                File configFile = null;                        
                Document includeDocument = null;
                
                NamedNodeMap attributes = currNode.getAttributes();
                Node fileNode = attributes.getNamedItem("file");
                if (fileNode != null){
                    String path = fileNode.getNodeValue();
                    name = fileNode.getNodeValue();
                
                
                    // absolute path or realtive path
                    configFile = new File(realPath, path);
                    if ( configFile.exists() ){
                        try {
                            includeDocument = Xml.getParsedDocument(Resources.getInstance().get("REQUESTPROXY_URL_MODULE_CONFIG", configFile.getAbsolutePath()));
                        } catch (Exception e) {
                            logger.log(Level.SEVERE, "Error while loading included config file. Parsing aborted" );
                        }
                    }
                }
                // classpath
                else if (attributes.getNamedItem("packagename") != null){
                    String pkgname = attributes.getNamedItem("packagename").getNodeValue();
                    String resname = pkgname.replace('.','/') + "/config.xml";
                    String tmpFileName = System.getProperty("java.io.tmpdir") + "config" +  new Date().getTime()  + ".xml";
                   
                    try {
                        // Extract config from jar-file, store it in temp directory, parse it and delete temp-file
                        InputStream is = getClassLoader().getResourceAsStream(resname);
                        FileOutputStream out = new FileOutputStream(tmpFileName);

                        int availableLength = is.available();
                        byte[] totalBytes = new byte[availableLength];
                        int bytedata = is.read(totalBytes);

                        out.write(totalBytes);
                        is.close();
                        out.close();
                    } catch (FileNotFoundException e1) {
                        logger.log(Level.SEVERE, "Error while loading included config file from classpath. Temporary File could not be found or generated" );
                    } catch (IOException e1) {
                         e1.printStackTrace();
                    }
                   
                    configFile = new File(tmpFileName);
                    if ( configFile.exists() ){
                        try {
                            includeDocument = Xml.getParsedDocument(Resources.getInstance().get("REQUESTPROXY_URL_MODULE_CONFIG", configFile.getAbsolutePath()));
                            if (!configFile.delete())
                                logger.log(Level.SEVERE, "Error deleting temporary config-file in " + tmpFileName + "." );
                        } catch (Exception e) {
                            logger.log(Level.SEVERE, "Error while loading included config file from classpath. Parsing aborted" );
                        }
                    }
                }
                
                if (includeDocument != null){
                    collectSectionsFromDocument(includeDocument, preferences);
                }
               
            }
            
            else if ("params".equals(currNode.getNodeName())) {
                try {
                    rawConfigParams.putAll(Xml.getParamMap(currNode));
                } catch (DataFormatException dfe) {
                    logger.log(Level.SEVERE, "Fehler beim Parsen der Config-Parameter.", dfe);
                    throw dfe;
                }
            } else if ("tasks".equals(currNode.getNodeName())) {
                taskList = new TcTaskList((Element) currNode, this);
            } else if ("loginManager".equals(currNode.getNodeName())) {
                try {
                    loginManagerParams.putAll(Xml.getParamMap(currNode));
                } catch (DataFormatException dfe) {
                    logger.log(Level.SEVERE, "Fehler beim Parsen der Config-Parameter.", dfe);
                    throw dfe;
                }
            } else if ("dataAccess".equals(currNode.getNodeName())) {
                try {
                    dataAccess = parseDataAccess(currNode, preferences.node(PREFS_DATA_ACCESS));
                } catch (DataFormatException dfe) {
                    logger.log(Level.SEVERE, "Fehler beim Parsen des Data Access Abschnitts.", dfe);
                    throw dfe;
                }
            } else if ("contentWorkerDeklaration".equals(currNode.getNodeName())) {
                try {
                    contentWorkersDeclarations = parseContentWorkerDeklarations(currNode);
                } catch (DataFormatException dfe) {
                    logger.log(Level.SEVERE, "Fehler beim Parsen der Content Worker Deklaration.", dfe);
                    throw dfe;
                }
            } else if ("description".equals(currNode.getNodeName())) {
                StringBuffer sb = new StringBuffer();
                NodeList childs = currNode.getChildNodes();
                String content;
                for (int j = 0; j < childs.getLength(); j++) {
                    content = childs.item(j).getNodeValue();
                    if (content != null)
                        sb.append(content);
                }
                description = sb.toString();
            } else if ("types".equals(currNode.getNodeName())) {
                try {
                    // TODO: richtig machen.
                    Class javaClass = getClassLoader().loadClass("de.tarent.schemas.Groupware_xsd.OptionMapType");
                    QName qname = new QName("http://schemas.tarent.de/Groupware.xsd", "OptionMapType");
                    TypeMappingRegistry reg = TcSOAPEngine.engine.getTypeMappingRegistry();
                    reg.getDefaultTypeMapping().register(javaClass, qname, new org.apache.axis.encoding.ser.BeanSerializerFactory(javaClass, qname), new org.apache.axis.encoding.ser.BeanDeserializerFactory(javaClass, qname));
                    javaClass = getClassLoader().loadClass("de.tarent.schemas.Groupware_xsd.EntryType");
                    qname = new QName("http://schemas.tarent.de/Groupware.xsd", "EntryType");
                    reg.getDefaultTypeMapping().register(javaClass, qname, new org.apache.axis.encoding.ser.BeanSerializerFactory(javaClass, qname), new org.apache.axis.encoding.ser.BeanDeserializerFactory(javaClass, qname));
                } catch (ClassNotFoundException e1) {
                    logger.log(Level.SEVERE, "Typenübergabe", e1);
                }
            } else if (currNode instanceof Element) {
                otherNodes.put(currNode.getNodeName(), currNode);
            }
        }
    }

   

    /**
     * Protected empty Contructor,
     * only for creation of MockUps
     */
    protected TcModuleConfig(String basedir, Map rawConfigParams) {
        this.name = "mockup-module";
        this.realPath = new File(basedir);
        this.rawConfigParams = rawConfigParams;
        this.configParams = rawConfigParams;
    }

    /** 
     * Creates an Config Object for testing purpose
     * @param rawConfigParams the config parameters
     */
    public static TcModuleConfig createMockupModuleConfig(String basedir, Map rawConfigParams) {
        return new TcModuleConfig(basedir, rawConfigParams);
    }



    /** 
     *  Auslesen der Content Worker Deklarationen
     */
    protected Map parseContentWorkerDeklarations(Node context) 
        throws DataFormatException {
        Map declarationMap = new HashMap();
        
        // Es gibt einen Default-Worker, 
        // der für das Hinzufügen von Parameern in den Content genutzt wird.
        ContentWorkerDeclaration putParamWorker = new ContentWorkerDeclaration();
        putParamWorker.setWorkerName(Resources.getInstance().get("REQUESTDISPATCHER_CLS_PARAM_WORKER"));
        putParamWorker.setImplementationSource("de.tarent.octopus.content.TcPutParams");
        putParamWorker.setSingletonInstantiation(true);
        putParamWorker.setFactory(DEFAULT_WORKER_FACTORY_NAME);
        declarationMap.put(putParamWorker.getWorkerName(), putParamWorker);

        
        NodeList nodes = context.getChildNodes();
        Node currNode;
        for (int i = 0; i < nodes.getLength(); i++) {
            currNode = nodes.item(i);

            // z.B. <worker name="SQLExecutor" implementation="de.tarent.demo.SQLExecutor" singleton="true" factory="direct"/>
            if (currNode instanceof Element && "worker".equals(currNode.getNodeName())) {
                ContentWorkerDeclaration workerDek = new ContentWorkerDeclaration();
                Element workerElement = (Element)currNode;
                
                // name
                Attr name = workerElement.getAttributeNode("name");
                if (name == null)
                    throw new DataFormatException("Eine Worker-Deklaration muss ein name-Attribut haben.");
                workerDek.setWorkerName(name.getValue());

                // implementation
                Attr implementation = workerElement.getAttributeNode("implementation");
                if (implementation == null)
                    throw new DataFormatException("Eine Worker-Deklaration muss ein implementation-Attribut haben.");
                workerDek.setImplementationSource(implementation.getValue());

                // singleton
                Attr singleton = workerElement.getAttributeNode("singleton");
                if (singleton != null)
                    workerDek.setSingletonInstantiation(new Boolean(singleton.getValue()).booleanValue());

                // factory
                Attr factory = workerElement.getAttributeNode("factory");
                if (factory != null)
                    workerDek.setFactory(getExpandedWorkerFactoryName(factory.getValue()));
                else
                    workerDek.setFactory(DEFAULT_WORKER_FACTORY_NAME);

                declarationMap.put(workerDek.getWorkerName(), workerDek);
            }
            // Zur Erhaltung der Kompatibilität sind auch <param name="workername" value="Klasse"/> erlaubt.
            else if (currNode instanceof Element && "param".equals(currNode.getNodeName())) {
                ContentWorkerDeclaration workerDek = new ContentWorkerDeclaration();
                Element workerElement = (Element)currNode;                
                
                // name
                Attr name = workerElement.getAttributeNode("name");
                if (name == null)
                    throw new DataFormatException("Eine param-Element muss ein name-Attribut haben.");
                workerDek.setWorkerName(name.getValue());

                // value
                Attr value = workerElement.getAttributeNode("value");
                if (value == null)
                    throw new DataFormatException("Ein param-Element muss ein value-Attribut haben.");
                workerDek.setImplementationSource(value.getValue());

                workerDek.setFactory(DEFAULT_WORKER_FACTORY_NAME);

                declarationMap.put(workerDek.getWorkerName(), workerDek);
            }
        }
        return declarationMap;
    }
    
    /**
     * Liefert einen voll Qualifizierten Klassennamen zurück.
     * Wenn 'shortname' keinen Punkt enthält wird er als Kurzname interpretiert,
     * und entsprechend expandiert. Sonst wird davon aus gegangen, dass es sich 
     * bereits um einen kompletten Klassennamen handelt.
     * 
     * Methode zur Expansion ist: "de.tarent.octopus.content."+ UK_FIRST_LOWER_REST(shortname) +"WorkerFactory"
     * 
     * @param shortname Ein Kurzname oder bereits ein voll qualifizierter Name
     * @return Den vollen Klassennamen für shortname, oder shortname, wenn es bereits ein Klassenname ist.
     */
    protected String getExpandedWorkerFactoryName(String shortname) {
        if (shortname.indexOf(".") < 0)
            return "de.tarent.octopus.content."+ shortname.substring(0,1).toUpperCase() + shortname.substring(1).toLowerCase()  +"WorkerFactory";
        return shortname;
    }

    /**
     * Auslesen der DataAccess Informationen
     */
    protected Map parseDataAccess(Node dataAccessNode, Preferences preferences) throws DataFormatException {

        Map sources = new HashMap();

        NodeList nodes = dataAccessNode.getChildNodes();
        Node currNode;
        for (int i = 0; i < nodes.getLength(); i++) {
            currNode = nodes.item(i);
            if ("dataSource".equals(currNode.getNodeName())) {
                NamedNodeMap attributes = currNode.getAttributes();
                Node nameNode = attributes.getNamedItem("name");
                String name = null;
                if (nameNode != null)
                    name = nameNode.getNodeValue();
                if (name == null)
                    throw new DataFormatException("Ein dataSource-Element muss ein name-Element haben.");

                Map currentSource = Xml.getParamMap(currNode);
                override("DataSource " + name, currentSource, preferences.node(name));
                currentSource.put("name", name);
                sources.put(name, currentSource);
                
                NodeList children = currNode.getChildNodes();
                for (int j=1;j<=children.getLength();j++) {
					Node node = children.item(j-1);
					if (node.getNodeName().equals("options")) {
						Map options = Xml.getParamMap(node);
						Iterator itOptions = options.keySet().iterator();
						while (itOptions.hasNext()) {
							Object key = itOptions.next();
							currentSource.put(key, options.get(key));
						}
					}
                }
            }
        }
        return sources;
    }

    /**
     * Liefert die verfügbaren Datenquellen
     * 
     * @return Eine Map, in der unter den Namen der Datenquellen wieder
     *         Maps mit String Keys und String Values abgelegt sind.
     */
    public Map getDataAccess() {
        return dataAccess;
    }

    /**
     * Liefert das Declaration Objekt zu einem ContentWorker
     * @param workerName Eindeutiger Bezeichner des Workers in diesem Modul
     * @return Objekt mit Informationen zur Instantiierung des Workers
     */
    public ContentWorkerDeclaration getContentWorkerDeclaration(String workerName) {
        return (ContentWorkerDeclaration)contentWorkersDeclarations.get(workerName);
    }

    /**
     * Liefert einen Parameter, der in der Config gesetzt wurde.
     */
    public String getParam(String key) {
        return substituteVars((String) configParams.get(key));
    }


    /**
     * Liefert einen Parameter, und konvertiert bei Bedarf den Fileseparator von / auf File.separatorChar
     */
    public String getParamForPath(String key) {
        if (File.separatorChar == '/')
            return getParam(key);
        
        String val = getParam(key);
        if (val != null)
            return val.replace('/', File.separatorChar);
        return null;
    }

    /**
     * Ersetzt variablen in den Parameterwerten mit werten.
     * Im Moment wird nur:
     *   ${HOME} => Homeverzeichnis 
     * unterstützt.
     */
    protected String substituteVars(String value) {
        if (value == null)
            return null;
        return value.replaceAll("\\$\\{HOME\\}", escapedUserHome);
    }

    public Object getParamAsObject(String key) {
        return configParams.get(key);
    }

    /**
     * Diese Methode überschreibt einen Parameter in den Preferences.  
     * 
     * @param key Schlüssel
     * @param value neuer Wert; falls <code>null</code>, so wird der Wert aus den Preferences entfernt.
     */
    public void setParam(String key, String value) {
        if (key == null || key.length() == 0)
            return;
        if (value == null) {
            getPreferences(PREFS_PARAMS).remove(key);
            if (rawConfigParams.containsKey(key))
                configParams.put(key, rawConfigParams.get(key));
            else
                configParams.remove(key);
        } else {
            getPreferences(PREFS_PARAMS).put(key, value);
            configParams.put(key, value);
        }
    }
    
    /**
     * Liefert einen Parameter, der in der Config gesetzt wurde als Liste.
     * Wenn in der Config nicht das Attribut array gesetzt wurde, wird einfach ein
     * einelementiger Vector erzeugt.
     */
    public List getParamAsList(String key) {
        Object value = configParams.get(key);
        if (value instanceof List)
            return (List) value;
        else if (value != null)
            return Collections.singletonList(value);
        else
            return Collections.EMPTY_LIST;
    }

    /**
     * Liefert die Modulbeschreibung
     * @return Modulbeschreibung.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Liefert den Pfad des Moduls im lokalen Dateisystem.
     * 
     * @return Modulpfad.
     */
    public File getRealPath() {
        return realPath;
    }

    /**
     * Liefert den Pfad der statischen Inhalte des Moduls im Web.
     * 
     * @return WebPfad.
     */
    public String getWebPathStatic() {
        String webPath = getName();
        if (webPath != null) {
            webPath = webPath.trim();
            if (!webPath.startsWith("/"))
                webPath = '/' + webPath;
            if (!webPath.endsWith("/"))
                webPath += '/';
        }
        return webPath;
    }

    /**
     * Liefert den Modulnamen.
     * 
     * @return Modulname.
     */
    public String getName() {
        return name;
    }

    /**
     * Liefert den ClassLoader für das Modul.
     * 
     * @return Modul-ClassLoader.
     */
    public ClassLoader getClassLoader() {
        if (classLoader == null) {
            logger.config("ClassLoader-Erstellung für das Modul " + getName());
            List urlList = new ArrayList();
            if (getRealPath().exists()) {
                File classesDir = new File(getRealPath(), "classes");
                if (classesDir.exists()) try {
                    urlList.add(classesDir.toURL());
                } catch (MalformedURLException mue) {
                    logger.log(Level.WARNING, "Fehler beim Wandeln des Modul-classes-Pfads '" + classesDir + "' in eine URL.");
                }
                File libs[] = new File(getRealPath(), "lib").listFiles();
                if (libs != null) {
                    for (int i = 0; i < libs.length; i++) try {
                        urlList.add(libs[i].toURL());
                    } catch (MalformedURLException mue) {
                        logger.log(Level.WARNING, "Fehler beim Wandeln des Modul-lib-Pfads '" + libs[i] + "' in eine URL.");
                    } 
                }
            } else {
                logger.warning("Modulverzeichnis '" + getRealPath() + "' des Moduls '" + getName() + "' existiert nicht.");
            }
            
            URL urls[] = new URL[urlList.size()];
            for (int i = 0; i < urlList.size(); i++)
                urls[i] = (URL) urlList.get(i);
            classLoader = new URLClassLoader(urls, getClass().getClassLoader());
        }
        return classLoader;
    }

    /**
     * Liefert die Liste der Tasks
     * @return TcTaskList
     */
    public TcTaskList getTaskList() {
        return taskList;
    }

    /**
     * Diese Methode liefert einen Preferences-Knoten im Modul-Zweig. 
     * 
     * @param subNode relativer Pfad des Unterknotens. Falls <code>null</code>, so wird der
     *  Modul-Basisknoten geliefert.
     * @return ausgewählter Preferences-Knoten
     */
    public Preferences getPreferences(String subNode) {
        while (subNode != null && subNode.startsWith("/"))
            subNode = subNode.substring(1);
        return subNode == null ? configPrefs : configPrefs.node(subNode);
    }

    /**
     * Diese Methode liefert die Parameter mit Überschreibungen aus den Preferences. 
     * 
     * @return maniüulierte Modul-Parameter.
     */
	public Map getParams() {
	    return Collections.unmodifiableMap(configParams);
	}
    
    /**
     * Diese Methode liefert die rohen Parameter, wie sie in der Konfiguration stehen. 
     * 
     * @return rohe Modul-Parameter.
     */
	public Map getRawParams() {
	    return Collections.unmodifiableMap(rawConfigParams);
	}
    
    /**
     * Diese Methode liefert einen DOM-Knoten in der Konfiguration, der nicht zu
     * den Standardknoten gehört 
     * 
     * @param nodeName Name des Knotens unter tcModuleConfig.
     * @return bezeichnetes Element, ggfs <code>null</code>.
     */
    public Element getOtherNode(String nodeName) {
        return (Element) otherNodes.get(nodeName);
    }
    
    /**
	 * @return Returns the log4jNode.
	 * @deprecated {@link #getOtherNode(String)} benutzen
	 */
	public Element getLog4jNode() {
		return getOtherNode("log4j:configuration");
	}

    public String getOnUnauthorizedAction() {
        return getParam(CONFIG_PARAM_ON_UNAUTHORIZED);
    }

	/**
	 * Diese Methode überschreibt Werte in einer Map mit Werten in einem Preferences-Knoten.
	 */
	public final static void override(String context, Map map, Preferences preferences) {
        String[] keys;
        try {
            keys = preferences.keys();
            if (keys != null && keys.length > 0) {
                for (int i = 0; i < keys.length; i++) {
                    String key = keys[i];
                    String value = preferences.get(key, asString(map.get(key)));
                    logger.config("[" + context + "] Override for " + key + ": " + value);
                    map.put(key, value);
                }
            }
        } catch (BackingStoreException e) {
            logger.log(Level.SEVERE, "[" + context + "] Preferences-API-Zugriff", e);
        }
	}
	
	public final static String asString(Object o) {
	    return o != null ? o.toString() : null;
	}
	
    /**
     * Diese Methode liefert einen LoginManager für dieses Modul. Liegt hier keine
     * (ausreichende) LoginManager-Konfiguration vor, so wird <code>null</code> geliefert. 
     * 
     * @return LoginManager für das angegebene Modul
     * @see #getLoginManager()
     */
	public synchronized LoginManager getLoginManager() throws TcSecurityException {
        if (loginManager == null) {
            if (!loginManagerParams.isEmpty()) {
                logger.config("Login-Manager-Erstellung für das Modul " + getName());
                //Modul möchte seine Config selber machen...
                String loginManagerClassName = (String) loginManagerParams.get("loginManagerClass");
                if (loginManagerClassName != null) {
                    logger.finer("Lade LoginManager-Implementierung: " + loginManagerClassName);
                    try {
                        //Eintrag ist angegeben...
                        Class loginManagerClass = getClassLoader().loadClass(loginManagerClassName);
                        loginManager = (LoginManager) loginManagerClass.newInstance();
                        loginManager.setConfiguration(loginManagerParams);
                    } catch (Exception e) {
                        logger.log(Level.SEVERE, "Fehler beim Laden des LoginManagers.", e);
                        throw new TcSecurityException(TcSecurityException.ERROR_SERVER_AUTH_ERROR, e);
                    }
                }
            }
        }
        return loginManager;
    }

    /**
     * Diese Methode erzeug eine neue {@link PersonalConfig}-Instanz. Die verwendete
     * Klasse kann über den Modul-Parameter {@link #CONFIG_PARAM_PERSONAL_CONFIG_CLASS}
     * gesetzt werden, Vorgabe ist {@link TcPersonalConfig}. 
     * 
     * @return eine leere {@link PersonalConfig}-Instanz
     * @throws TcConfigException 
     */
    public PersonalConfig createNewPersonalConfig() throws TcConfigException {
        synchronized(this) {
            if (personalConfigConstructor == null) {
                try {
                    String className = getParam(CONFIG_PARAM_PERSONAL_CONFIG_CLASS);
                    Class classClass = className != null ? getClassLoader().loadClass(className) : TcPersonalConfig.class;
                    if (!PersonalConfig.class.isAssignableFrom(classClass)) {
                        String msg = "Fehler beim Laden des Konstruktors für PersonalConfigs; angegebene Klasse implementiert nicht PersonalConfig.";
                        logger.severe(msg);
                        throw new TcConfigException(msg);
                    }
                    personalConfigConstructor = classClass.getConstructor(new Class[0]);
                } catch (Exception e) {
                    logger.log(Level.SEVERE, "Fehler beim Laden des Konstruktors für PersonalConfigs.", e);
                    throw new TcConfigException("Fehler beim Laden des Konstruktors für PersonalConfigs.", e);
                }
            }
        }
        try {
            return (PersonalConfig) personalConfigConstructor.newInstance(new Object[0]);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Fehler beim Ausführen des Konstruktors für PersonalConfigs.", e);
            throw new TcConfigException("Fehler beim Ausführen des Konstruktors für PersonalConfigs.", e);
        }
    }
}
