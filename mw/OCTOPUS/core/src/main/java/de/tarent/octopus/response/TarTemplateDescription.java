package de.tarent.octopus.response;

/*-
 * tarent-octopus, Webservice Data Integrator and Application Server
 * Copyright © 2002–2018 tarent solutions GmbH and its contributors
 * Copyright © 2015, 2017, 2018 mirabilos (t.glaser@tarent.de)
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
