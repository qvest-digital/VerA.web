package de.tarent.commons.config;

/*-
 * VerA.web-Middleware, newly MIT licenced, is comprised of:
 * tarent-commons, a set of common components and solutions
 * tarent-contact, platform-independent webservice-based contact management
 * tarent-database, jdbc database library
 * tarent-doctor, Document Generation Platform
 * tarent-octopus, Webservice Data Integrator and Application Server
 *  © 2005, 2006, 2007 asteban (s.mancke@tarent.de)
 *  © 2007 David Goemans (d.goemans@tarent.de)
 *  © 2006, 2007, 2010 Hendrik Helwich (h.helwich@tarent.de)
 *  © 2005, 2006, 2007 Christoph Jerolimov (c.jerolimov@tarent.de)
 *  © 2006 Philipp Kirchner (p.kirchner@tarent.de)
 *  © 2010 Carsten Klein (c.klein@tarent.de)
 *  © 2006 Michael Kleinhenz (m.kleinhenz@tarent.de)
 *  © 2006 Michael Klink (m.klink@tarent.de)
 *  © 2007 Fabian Köster (f.koester@tarent.de)
 *  © 2006 Martin Ley (m.ley@tarent.de)
 *  © 2007 Alex Maier (a.maier@tarent.de)
 *  © 2007, 2015, 2017, 2018 mirabilos (t.glaser@tarent.de)
 *  © 2006, 2007 Jens Neumaier (j.neumaier@tarent.de)
 *  © 2006 Nils Neumaier (n.neumaier@tarent.de)
 *  © 2007, 2008 Martin Pelzer (m.pelzer@tarent.de)
 *  © 2008, 2009 Christian Preilowski (c.thiel@tarent.de)
 *  © 2006, 2008, 2009 Thomas Schmitz (t.schmitz@tarent.de)
 *  © 2007 Robert Schuster (r.schuster@tarent.de)
 * and older code, Copyright © 2001–2007 ⮡ tarent GmbH and contributors.
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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.tarent.commons.config.ConnectionDefinition.Key;

/**
 * This class was once an integral part of the older
 * configuration system. It allowed access to raw
 * XML data which is now discouraged.
 *
 * A better approach is to add the neccessary accessor
 * methods in the {@link ConfigManager} class. It should
 * parse the XML and provide it as Map or other suitable
 * data structures to the user.
 */
public class XmlUtil {
    public final static String ENABLED_ATTRIBUTE = "enabled";
    public final static Logger logger = Logger.getLogger(XmlUtil.class.getName());

    private static DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();

    private static DocumentBuilder getDocumentBuilder() {
        try {
            return domFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            // The exception is swallowed and hidden in an unchecked one because:
            // If the XML parser cannot be configured something is seriously broken
            // and there is no need that every caller should check for a seriously
            // broken state.
            throw (IllegalStateException) new IllegalStateException("XML parser could not be configured").initCause(e);
        }
    }

    /**
     * Writes a given document out using the provided {@link OutputStream}.
     *
     * @param doc
     * @param dest
     * @throws XmlUtil.Exception
     */
    public static void storeDocument(Document doc, OutputStream dest)
      throws XmlUtil.Exception {
        Source source = new DOMSource(doc);
        StreamResult result = new StreamResult(dest);

        try {
            Transformer t = TransformerFactory.newInstance().newTransformer();

            // Enforces indentation enabling a half-way pretty printed output
            // (indentation sizes are outside the scope of the standard :( )
            t.setOutputProperty(OutputKeys.INDENT, "yes");

            t.transform(source, result);
        } catch (TransformerConfigurationException e) {
            throw new XmlUtil.Exception("Error configuring XML transformer", e);
        } catch (TransformerException e) {
            throw new XmlUtil.Exception("Error transforming XML", e);
        }
    }

    /**
     * Parses an XML document from an <code>InputStream</code>.
     *
     * <p>Provide the
     * <code>baseURI</code> from which relative URIs inside the document are
     * resolved.</p>
     *
     * <p>All kinds of exceptions which may happen while obtaining and
     * parsing the document are wrapped in a {@link XmlUtil.Exception}
     * for convenience.</p>
     *
     * @param is
     * @param baseURI
     * @throws XmlUtil.Exception
     */
    public static Document getParsedDocument(InputStream is, String baseURI) throws XmlUtil.Exception {
        try {
            return getDocumentBuilder().parse(is, baseURI);
        } catch (SAXException saxe) {
            throw new XmlUtil.Exception("Error parsing XML", saxe);
        } catch (IOException ioe) {
            throw new XmlUtil.Exception("Error parsing XML", ioe);
        }
    }

    public static Document createDocument() {
        return getDocumentBuilder().newDocument();
    }

    private static boolean isEnabled(Element e) {
        String enabled = e.getAttribute(ENABLED_ATTRIBUTE);
        return !("0".equals(enabled) ||
          "no".equalsIgnoreCase(enabled) ||
          "false".equalsIgnoreCase(enabled));
    }

    private static boolean isArray(Element e) {
        String type = e.getAttribute("type");
        return "array".equalsIgnoreCase(type);
    }

    /**
     * Returns found value string or empty string if value not found.
     *
     * <p>This method is used for the new configuration system
     * only. It should be moved ASAP.</p>
     */

    static String getValue(Element e) {
        try {
            String value = "";

            if (e.hasAttribute("value")) {

                value = e.getAttribute("value");//<param value=".."/>
            } else if (e.hasChildNodes()) {

                Node valueChild = null;
                NodeList paramChilds = e.getElementsByTagName("value");
                if (paramChilds.getLength() > 0) {
                    valueChild = paramChilds.item(0);
                    value = valueChild.getFirstChild().getNodeValue();//<param><value>..</value>...</param>
                } else {

                    value = e.getFirstChild().getNodeValue();//<param>..</param>

                    if ("".equals(value)) {
                        logger.info("[!] element value not found: " + e.getAttribute("name"));
                        printNodeInfos(e.getNodeName(), e);

                        return "";
                    }
                }
            }

            return value;
        } catch (NullPointerException npe) {
            logger.info("[!] element value not found: " + e.getAttribute("name"));
            return "";
        }
    }

    private static void printNodeInfos(String sNodeName, Node node) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("\n---------------------- node: " + sNodeName + "\n");
        if (null != node) {
            printObjIfVisible(buffer, "getNodeType()        = ", "" + node.getNodeType());
            printObjIfVisible(buffer, "getNodeName()        = ", node.getNodeName());
            printObjIfVisible(buffer, "getLocalName()       = ", node.getLocalName());
            printObjIfVisible(buffer, "getNodeValue()       = ", node.getNodeValue());
            if (node.hasAttributes()) {
                printObjIfVisible(buffer, "getAttributes()      = ", node.getAttributes());
            }
            if (node.hasChildNodes()) {
                printObjIfVisible(buffer, "getChildNodes()      = ", node.getChildNodes());
                printObjIfVisible(buffer, "getFirstChild()      = ", node.getFirstChild());
            }
            printObjIfVisible(buffer, "getPreviousSibling() = ", node.getPreviousSibling());
            printObjIfVisible(buffer, "getNextSibling()     = ", node.getNextSibling());
        }
        buffer.append("----------------------\n");

        logger.info(buffer.toString());
    }

    private static void printObjIfVisible(StringBuffer buffer, String sValName, Object obj) {
        if (null == obj) {
            return;
        }
        if (obj instanceof NamedNodeMap) {
            NamedNodeMap map = (NamedNodeMap) obj;
            if (map.getLength() > 0) {
                buffer.append(sValName);
            }
            for (int i = 0; i < map.getLength(); i++) {
                buffer.append(map.item(i).getNodeValue().trim() + " ");
            }
            buffer.append("\n");
            return;
        }
        String s = obj.toString();
        if (null != s && 0 < s.trim().length() && !s.trim().equals("\n")) {
            buffer.append(sValName + s + " \n");
        }
    }

    /**
     * Liefert die Paramattribute eines Knotens
     *
     * @param parentNode Dom Knoten, der 'param' Elemente mit den 'name' und 'value' Attributen als Kinder hat.
     *                   Das 'value' Attribut kann alternativ auch als nested 'value' Element angegeben werden.
     *                   Wenn das Attribut type=array ist, kann es auch eine Liste von 'value' Kindern haben,
     *                   die dann in einem Vector abgelegt werden.
     * @return Hashtable mir String als Keys und Values
     */
    public static Hashtable getParamMap(Node parentNode) throws DataFormatException {

        Hashtable paramMap = new Hashtable();

        Node currentNode;
        NodeList nodes = parentNode.getChildNodes();

        for (int i = 0; i < nodes.getLength(); i++) {

            currentNode = nodes.item(i);

            if ("param".equals(currentNode.getNodeName())) {

                Element paramElement = (Element) currentNode;

                String name = paramElement.getAttribute("name");

                if (!isEnabled(paramElement)) {
                    continue;
                }

                if ("".equals(name)) {
                    throw new DataFormatException("Ein 'param'  Element muss ein nicht leeres 'name' Attribut haben.");
                }

                // Ganze Liste drin
                if (isArray(paramElement)) {

                    NodeList paramChilds = paramElement.getElementsByTagName("value");

                    Vector values = new Vector();

                    for (int j = 0; j < paramChilds.getLength(); j++) {

                        Node valueChild = paramChilds.item(j);

                        String value = valueChild.getFirstChild().getNodeValue();

                        values.add(value);
                    }

                    paramMap.put(name, values);
                } else {
                    // Nur ein Value
                    paramMap.put(name, getValue(paramElement));
                }
            }
        }
        return paramMap;
    }

    /**
     * This method converts the XML-unsafe characters of a given string to XML-Entities.
     *
     * @return escaped string or empty string if a given string empty
     */
    public static String escape(String source) {
        if (source == null || "".equals(source)) {
            logger.warning("[!] can't escape an empty string");
            return "";
        }
        StringBuffer buffer = new StringBuffer();
        if (source != null) {
            for (int index = 0; index < source.length(); index++) {
                switch (source.charAt(index)) {
                case '&':
                    buffer.append("&amp;");
                    break;
                case '<':
                    buffer.append("&lt;");
                    break;
                case '>':
                    buffer.append("&gt;");
                    break;
                case '"':
                    buffer.append("&quot;");
                    break;
                case '\'':
                    buffer.append("&apos;");
                    break;
                default:
                    buffer.append(source.charAt(index));
                }
            }
        }
        return buffer.toString();
    }

    public static class Exception extends java.lang.Exception {
        Exception(String msg, Throwable cause) {
            super(msg, cause);
        }
    }

    /**
     * Creates an XML document for environment configuration.
     *
     * <p>Note: This method is quick and dirty. Don't spread the use, don't
     * copy and paste. Just survive the release (2.0) and get replaced.</p>
     *
     * <p>TODO: Get replaced.</p>
     *
     */
    static Document createQnDEnvironmentDocument() {
        Document doc = createDocument();

        Element root = doc.createElement("environment");
        doc.appendChild(root);

        Element connections = doc.createElement("connections");
        connections.setAttribute("file", "connections.xml");
        root.appendChild(connections);

        return doc;
    }

    /**
     * Creates an XML document for the given connection definitions.
     *
     * <p>Note: This method is quick and dirty. Don't spread the use, don't
     * copy and paste. Just survive the release (2.0) and get replaced.</p>
     *
     * <p>TODO: Get replaced.</p>
     *
     * @param connectionDefinitions
     */
    static Document createQnDConnectionsDocument(Collection connectionDefinitions) {
        Document doc = createDocument();

        Element root = doc.createElement("connections");
        doc.appendChild(root);

        Iterator ite = connectionDefinitions.iterator();
        while (ite.hasNext()) {
            ConnectionDefinition cd = (ConnectionDefinition) ite.next();

            Element connection = doc.createElement("connection");
            root.appendChild(connection);

            Element param = doc.createElement("param");
            param.setAttribute("name", Key.LABEL.toString());
            param.setAttribute("value", cd.get(Key.LABEL));
            connection.appendChild(param);

            param = doc.createElement("param");
            param.setAttribute("name", Key.SERVER_URL.toString());
            param.setAttribute("value", cd.get(Key.SERVER_URL));
            connection.appendChild(param);

            param = doc.createElement("param");
            param.setAttribute("name", Key.OCTOPUS_MODULE.toString());
            param.setAttribute("value", cd.get(Key.OCTOPUS_MODULE));
            connection.appendChild(param);
        }

        return doc;
    }
}