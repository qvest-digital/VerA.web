package de.tarent.octopus.response;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Beschreibung eines Template Modules
 * Mit Name und Anordnung der Kinder
 *
 * @author <a href="mailto:mancke@mancke-software.de">Sebastian Mancke</a>, <b>tarent GmbH</b>
 */
public class TarTemplateDescription {
    /**
     * Feldbezeichner im übergeordneten Template,
     * in das dieses Template eingesetzt werden will
     */
    private String position;

    /**
     * Name des Template Moduls, der dazu benutzt werden kann
     * Das Template in einer Datenbank oder im Dateisystem zu finden
     */
    private String name;

    /**
     * Die TarTemplateDescription der untergeordneten Templates.
     * Wobei die positionen als Keys dienen.
     */
    private Map childs;

    /**
     * Initialisiert diese Beschreibung mit einen Node Element.
     *
     * @param templateElement Ein Dom Element, daß diesem Template entspricht.
     */
    public TarTemplateDescription(Node templateElement) {
        NamedNodeMap attr = templateElement.getAttributes();
        position = attr.getNamedItem("position").getNodeValue();
        name = attr.getNamedItem("name").getNodeValue();

        NodeList childList = templateElement.getChildNodes();

        childs = new HashMap();
        for (int i = 0; i < childList.getLength(); i++) {
            Node curr = childList.item(i);
            if ("template".equals(curr.getNodeName())) {
                TarTemplateDescription currChild = new TarTemplateDescription(curr);
                childs.put(currChild.getPosition(), currChild);
            }
        }
    }

    /**
     * Feldbezeichner im übergeordneten Template,
     * in das dieses Template eingesetzt werden will
     */
    public String getPosition() {
        return position;
    }

    /**
     * Name des Template Moduls, der dazu benutzt werden kann
     * Das Template in einer Datenbank oder im Dateisystem zu finden
     */
    public String getName() {
        return name;
    }

    /**
     * Die TarTemplateDescription der untergeordneten Templates.
     * Wobei die positionen als Keys dienen.
     *
     * @return Map, wobei die Positionen in Form von Strings als Keys dienen
     *         und die Values wieder TarTemplateDescriptions sind.
     */
    public Map getChildTemplateDescriptions() {
        return childs;
    }

    /**
     * Gibt eine lesbare Darstellung des Inhaltes zurück, wobei alle Child-Templates auch rekursiv ausgegeben werden.
     */
    public String toString() {
        return toString("");
    }

    private String toString(String einrueckung) {
        StringBuffer sb = new StringBuffer();
        sb.append("TarTemplateDescription: name=" + name + " position=" + position + "\n");

        for (Iterator e = childs.keySet().iterator(); e.hasNext();) {
            String key = (String) e.next();
            sb.append(
                einrueckung
                    + "      "
                    + key
                    + " => "
                    + ((TarTemplateDescription) childs.get(key)).toString(einrueckung + "      "));
        }
        return sb.toString();
    }
}
