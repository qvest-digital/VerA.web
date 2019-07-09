package de.tarent.ldap;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

/**
 * Kleine XML-Utilities für den LDAPManager
 *
 * @author philipp
 */
public class XMLUtil {
    private static DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();

    public static DocumentBuilder getDocumentBuilder() throws ParserConfigurationException {
        return domFactory.newDocumentBuilder();
    }

    public static Document getParsedDocument(String filename) throws SAXException, IOException, ParserConfigurationException {
        return getDocumentBuilder().parse(filename);
    }

    public static Document getParsedDocument(InputSource inputsource)
      throws SAXException, IOException, ParserConfigurationException {
        return getDocumentBuilder().parse(inputsource);
    }

    public static Node getObjectClass(Element mapping) {
        Node objectclass = null;
        if (mapping.hasChildNodes()) {
            NodeList testing = mapping.getChildNodes();
            for (int i = 0; i < testing.getLength(); i++) {
                //System.out.println("getObjectClass: "+ testing.item(i).getNodeName());
                if (testing.item(i).getNodeName().equals("objectclass")) {
                    objectclass = testing.item(i);
                    //System.out.println("getObjectClass: found");
                }
            }
        }
        return objectclass;
    }

    public static Node getUserList(Element mapping) {
        Node userlist = null;
        if (mapping.hasChildNodes()) {
            NodeList testing = mapping.getChildNodes();
            for (int i = 0; i < testing.getLength(); i++) {
                //System.out.println("getObjectClass: "+ testing.item(i).getNodeName());
                if (testing.item(i).getNodeName().equals("UserList")) {
                    userlist = testing.item(i);
                    //System.out.println("getObjectClass: found");
                }
            }
        }
        return userlist;
    }

    public static NodeList getRelevantChildren(Node parent) {
        Node parent2 = parent.cloneNode(true);
        Node objectclass = getObjectClass((Element) parent2);
        parent2.removeChild(objectclass);
        return parent2.getChildNodes();
    }
}
