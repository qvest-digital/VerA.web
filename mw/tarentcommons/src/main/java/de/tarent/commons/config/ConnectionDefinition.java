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

import java.util.HashMap;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.tarent.commons.config.ConfigManager.Scope;

public final class ConnectionDefinition extends Base {
    /**
     * The scope in which this ConnectionDefinition was defined in.
     *
     * This property allows various evaluating various access rights
     * conditions (e.g. normal user may not modify site or installation scope
     * definitions).
     */
    Scope scope;

    public ConnectionDefinition(String label, String serverURL, String module) {
        putParam(Key.LABEL, label);
        putParam(Key.SERVER_URL, serverURL);
        putParam(Key.OCTOPUS_MODULE, module);
    }

    ConnectionDefinition(Scope scope, Node node) throws KeyUnavailableException {
        this.scope = scope;

        NodeList list = node.getChildNodes();
        int size = list.getLength();

        for (int i = 0; i < size; i++) {
            Node subNode = list.item(i);
            String nodeName = subNode.getNodeName();

            if (nodeName == null) {
                continue;
            } else if (nodeName.equals("param")) {
                NamedNodeMap attr = subNode.getAttributes();
                Node name = attr.getNamedItem("name");
                Key key = Key.getInstance(name.getNodeValue());
                putParam(key, subNode);
            }
        }
    }

    public String get(Key key) {
        return getParamValue(key, null);
    }

    public String toString() {
        return get(Key.LABEL);
    }

    /**
     * This class contains keys for the connection parameters. TODO: Enhance this
     * to allow existance checks at parse time or the ability to uncover
     * superfluous parameters.
     */
    public static final class Key extends Base.Key {
        private static HashMap instances = new HashMap();

        //

        public static final Key SERVER_URL = make("serverURL");

        public static final Key OCTOPUS_MODULE = make("octopusModule");

        public static final Key LABEL = make("label");

        //

        private Key(String label) {
            super(label);
            instances.put(label, this);
        }

        /**
         * Creates a new instance.
         * <p>
         * Use this instead of the constructor in light of future additions.
         * </p>
         *
         * @param label
         */
        private static Key make(String label) {
            return new Key(label);
        }

        /**
         * Returns an instance of this class or throws a {@KeyUnavailableException}
         * if it does not exist.
         *
         * @param label
         * @throws KeyUnavailableException if the key does not exist.
         */
        private static Key getInstance(String label) throws KeyUnavailableException {
            Key k = (Key) instances.get(label);

            if (k == null) {
                throw new KeyUnavailableException(label);
            }

            return k;
        }
    }
}
