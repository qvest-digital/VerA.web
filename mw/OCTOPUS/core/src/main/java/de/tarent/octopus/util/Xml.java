package de.tarent.octopus.util;

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
 *  © 2007, 2013, 2014, 2015, 2016, 2017, 2018, 2019, 2020
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
 * and older code, Copyright © 2001–2008 ⮡ tarent GmbH and contributors.
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

import lombok.extern.log4j.Log4j2;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

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
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
public class Xml {
    private static TransformerFactory tFactory = null;

    public static Transformer getXSLTTransformer(Source xsltSource) throws TransformerConfigurationException {
        if (tFactory == null) {
            logger.debug("erzeuge Transformer-Factory");
            tFactory = TransformerFactory.newInstance();
        }
        logger.trace("erzeuge Transformer aus XSLT-Objekt");
        return tFactory.newTransformer(xsltSource);
    }

    public static void doXsltTransformation(Source xmlSource, Source xsltSource, Result output)
      throws TransformerConfigurationException, TransformerException {
        Transformer transformer = getXSLTTransformer(xsltSource);
        logger.trace("transformiere XML-Objekt");
        transformer.transform(xmlSource, output);
    }

    public static Document getParsedDocument(String filename)
      throws SAXException, IOException, ParserConfigurationException, FactoryConfigurationError {
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
        while (next != null && next.getNodeType() != Node.ELEMENT_NODE) {
            next = next.getNextSibling();
        }
        return (Element) next;
    }

    /**
     * Liefert den Sibling
     */
    public static Element getNextSiblingElement(Node node) {
        Node next = node.getNextSibling();
        while (next != null && next.getNodeType() != Node.ELEMENT_NODE) {
            next = next.getNextSibling();
        }
        return (Element) next;
    }

    public static Element getFirstChildOrSiblingElement(Node node) {
        Element child = getFirstChildElement(node);
        if (child != null) {
            return child;
        }
        return getNextSiblingElement(node);
    }

    /**
     * Liefert die Paramattribute eines Knotens
     *
     * @param parentNode Dom Knoten, der 'param' Elemente mit den 'name' und 'value' Attributen als Kinder hat.
     *                   Das 'value' Attribut kann alternativ auch als nested 'value' Element angegeben werden.
     *                   <ul>
     *                   <li>Wenn das Attribut type=array ist, kann es auch eine Liste von 'value' Kindern haben,
     *                   die dann in einem Vector abgelegt werden.</li>
     *
     *                   <li>Wenn das Attribut type=map ist, können wieder Param-Elemente
     *                   darin enthalten sein, die dann als Map zurück geliefert werden.</li>
     *
     *                   <li>Anstatt des value Attributes kann es auch ein refvalue haben, dass dann in
     *                   dem entsprechenden Kontext aufzulösen ist.</li>
     *                   </ul>
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
        if ("".equals(name)) {
            throw new DataFormatException("Ein 'param' Element muss ein nicht leeres 'name' Attribut haben ");
        }
        return name;
    }

    /**
     * Liefert Wert eines Param Elementes
     */
    public static Object getParamValue(Element paramElement) throws DataFormatException {

        String type = paramElement.getAttribute("type");

        //Ganze Liste drinn
        if (type != null && (type.toLowerCase().equals("array") || type.toLowerCase().equals("list"))) {
            NodeList paramChilds = paramElement.getChildNodes();
            List values = new ArrayList(paramChilds.getLength());
            for (int j = 0; j < paramChilds.getLength(); j++) {
                Node valueChild = paramChilds.item(j);
                if (valueChild instanceof Element) {
                    Element valueChildElement = (Element) paramChilds.item(j);
                    if ("value".equals(valueChildElement.getTagName())) {
                        String value = valueChild.getFirstChild().getNodeValue();
                        values.add(value);
                    } else if ("param".equals(valueChildElement.getTagName())) {
                        values.add(getParamValue(valueChildElement));
                    }
                }
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
                        if (paramChilds.getLength() > 0) {
                            valueChild = paramChilds.item(0);
                            value = valueChild.getFirstChild().getNodeValue();
                        }
                    }
                    return value;
                } catch (NullPointerException npe) {
                    throw new DataFormatException("Das 'param' Element '" + paramElement.getAttribute("name") +
                      "' muss ein 'value' Attribut oder nested Element haben.");
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
}
