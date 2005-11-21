/* $Id: Xml.java,v 1.1.1.1 2005/11/21 13:33:38 asteban Exp $
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

package de.tarent.octopus.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Attr;
import org.xml.sax.SAXException;

public class Xml {
    /** Der Logger */
    private static Logger logger = Logger.getLogger(Xml.class.getName());
    private static TransformerFactory tFactory = null;

    public static Transformer getXSLTTransformer(Source xsltSource) throws TransformerConfigurationException {
        if (tFactory == null) {
            logger.finer("erzeuge Transformer-Factory");
            tFactory = TransformerFactory.newInstance();
        }
        logger.finest("erzeuge Transformer aus XSLT-Objekt");
        return tFactory.newTransformer(xsltSource);
    }

    public static void doXsltTransformation(Source xmlSource, Source xsltSource, Result output)
        throws TransformerConfigurationException, TransformerException {
        Transformer transformer = getXSLTTransformer(xsltSource);
        logger.finest("transformiere XML-Objekt");
        transformer.transform(xmlSource, output);
    }

    public static Document getParsedDocument(String filename) throws SAXException, IOException, ParserConfigurationException, FactoryConfigurationError {
        return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(filename);
    }

    public static String toString(Document doc) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            
            DOMSource source = new DOMSource(doc.getDocumentElement());
            StreamResult result = new StreamResult(out);
            TransformerFactory.newInstance().newTransformer().transform(source, result);
            
            String se = out.toString();
            return se;
        } catch (Exception h) {
            return "Knoten zu String hat nicht geklappt! ";
        }
    }

    public static Element getFirstChildElement(Node node) {
        Node next = node.getFirstChild();
        while (next != null && next.getNodeType() != Node.ELEMENT_NODE)
            next = next.getNextSibling();
        return (Element) next;
    }

    /**
     * Liefert den Sibling
     */
    public static Element getNextSiblingElement(Node node) {
        Node next = node.getNextSibling();
        while (next != null && next.getNodeType() != Node.ELEMENT_NODE)
            next = next.getNextSibling();
        return (Element) next;
    }

    public static Element getFirstChildOrSiblingElement(Node node) {
        Element child = getFirstChildElement(node);
        if (child != null)
            return child;
        return getNextSiblingElement(node);
    }

    /**
     * Liefert die Paramattribute eines Knotens
     *
     * @param parentNode Dom Knoten, der 'param' Elemente mit den 'name' und 'value' Attributen als Kinder hat.
     *                   Das 'value' Attribut kann alternativ auch als nested 'value' Element angegeben werden.
     * 					 <ul>
     *                   <li>Wenn das Attribut type=array ist, kann es auch eine Liste von 'value' Kindern haben, 
     * 			 	           die dann in einem Vector abgelegt werden.</li>
     *
     * 					   <li>Wenn das Attribut type=map ist, können wieder Param-Elemente 
     *                       darin enthalten sein, die dann als Map zurück geliefert werden.</li>
     *
     * 					   <li>Anstatt des value Attributes kann es auch ein refvalue haben, dass dann in 
     *                         dem entsprechenden Kontext aufzulösen ist.</li>
     *                 </ul>
     *
     * @return Map mit Strings als Keys und Values
     */
    public static Map getParamMap(Node parentNode) throws DataFormatException {
        Map paramMap = new HashMap();

        NodeList nodes = parentNode.getChildNodes();
        Node currNode;
        for (int i = 0; i < nodes.getLength(); i++) {
            currNode = nodes.item(i);
            if ("param".equals(currNode.getNodeName())) {
                Element paramElement = (Element) currNode;
                String name = getParamName(paramElement);
                Object value = getParamValue(paramElement);
                paramMap.put(name, value);
            }
        }
        return paramMap;
    }

    /**
     * Liefert den Paraemternamen aus einem Param Element
     */
    public static String getParamName(Element paramElement) throws DataFormatException {
        String name = paramElement.getAttribute("name");
        if ("".equals(name))
            throw new DataFormatException("Ein 'param' Element muss ein nicht leeres 'name' Attribut haben ");
        return name;
    }

    /**
     * Liefert Wert eines Param Elementes
     */
    public static Object getParamValue(Element paramElement) throws DataFormatException {
        
        String type = paramElement.getAttribute("type");        
        
        //Ganze Liste drinn
        if (type != null && type.toLowerCase().equals("array") || type.toLowerCase().equals("list")) {
            NodeList paramChilds = paramElement.getElementsByTagName("value");
            List values = new ArrayList();
            for (int j = 0; j < paramChilds.getLength(); j++) {
                Node valueChild = paramChilds.item(j);
                String value = valueChild.getFirstChild().getNodeValue();
                values.add(value);
            }
            return values;
        } 
        // Map
        else if (type != null && type.toLowerCase().equals("map")) {
            return getParamMap(paramElement);
        }
        // Nur ein value oder refvalue
        else {
                    
            String refvalue = paramElement.getAttribute("refvalue");
            if (refvalue == null || "".equals(refvalue)) {
                        
                try {
                    String value = null;
                    Attr valueAttr = paramElement.getAttributeNode("value");
                    if (valueAttr != null) {
                        value = valueAttr.getValue();
                    } 
                    // No attribute specified
                    else {
                        NodeList paramChilds = paramElement.getElementsByTagName("value");

                        Node valueChild = null;
                        if (paramChilds.getLength() > 0)
                            valueChild = paramChilds.item(0);
                        value = valueChild.getFirstChild().getNodeValue();
                    }
                    return value;
                } catch (NullPointerException npe) {
                    throw new DataFormatException("Das 'param' Element '" + paramElement.getAttribute("name") + "' muss ein 'value' Attribut oder nested Element haben.");
                }
            } else {
                return new ParamReference(refvalue);
            }
        }
    }

    /**
     * Diese Methode wandelt die XML-gefährlichen Zeichen eines Strings
     * in Entitäten.
     */
    public static String escape(String source) {
        StringBuffer buffer = new StringBuffer();
        if (source != null) {
            for (int index = 0; index < source.length(); index++) {
                switch(source.charAt(index)) {
                case '&': buffer.append("&amp;");  break;
                case '<': buffer.append("&lt;");   break;
                case '>': buffer.append("&gt;");   break;
                case '"': buffer.append("&quot;"); break;
                case '\'': buffer.append("&apos;"); break;
                default:  buffer.append(source.charAt(index));
                }
            }
        }
        return buffer.toString();
    }
}
