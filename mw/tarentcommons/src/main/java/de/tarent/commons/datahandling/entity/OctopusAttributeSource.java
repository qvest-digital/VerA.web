package de.tarent.commons.datahandling.entity;

/*-
 * VerA.web-Middleware, newly MIT licenced, is comprised of:
 * tarent-commons, a set of common components and solutions
 * tarent-contact, platform-independent webservice-based contact management
 * tarent-database, jdbc database library
 * tarent-doctor, Document Generation Platform
 * tarent-octopus, Webservice Data Integrator and Application Server
 *  © 2005, 2006, 2007 asteban <s.mancke@tarent.de>
 *  © 2007 David Goemans <d.goemans@tarent.de>
 *  © 2006, 2007, 2010 Hendrik Helwich <h.helwich@tarent.de>
 *  © 2005, 2006, 2007 Christoph Jerolimov <c.jerolimov@tarent.de>
 *  © 2006 Philipp Kirchner <p.kirchner@tarent.de>
 *  © 2010 Carsten Klein <c.klein@tarent.de>
 *  © 2006 Michael Kleinhenz <m.kleinhenz@tarent.de>
 *  © 2006 Michael Klink <m.klink@tarent.de>
 *  © 2007 Fabian Köster <f.koester@tarent.de>
 *  © 2006 Martin Ley <m.ley@tarent.de>
 *  © 2007 Alex Maier <a.maier@tarent.de>
 *  © 2007, 2015, 2017, 2018 mirabilos <t.glaser@tarent.de>
 *  © 2006, 2007 Jens Neumaier <j.neumaier@tarent.de>
 *  © 2006 Nils Neumaier <n.neumaier@tarent.de>
 *  © 2007, 2008 Martin Pelzer <m.pelzer@tarent.de>
 *  © 2008, 2009 Christian Preilowski <c.thiel@tarent.de>
 *  © 2006, 2008, 2009 Thomas Schmitz <t.schmitz@tarent.de>
 *  © 2007 Robert Schuster <r.schuster@tarent.de>
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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import de.tarent.octopus.server.OctopusContext;

/**
 * @author Alex Maier
 */
public class OctopusAttributeSource implements AttributeSource {
    OctopusContext oct;
    List attributeNames;

    public OctopusAttributeSource(OctopusContext oct) {
        this.oct = oct;
    }

    public OctopusAttributeSource(OctopusContext oct, List attributeNames) {
        this.oct = oct;
        this.attributeNames = attributeNames;
    }

    public OctopusAttributeSource(OctopusContext oct, String attributeName) {
        this.oct = oct;
        addAttributeName(attributeName);
    }

    public OctopusAttributeSource addAttributeName(String attributeName) {
        if (attributeNames == null) {
            attributeNames = new LinkedList();
        }
        attributeNames.add(attributeName);
        return this;
    }

    /* (non-Javadoc)
     * @see de.tarent.commons.datahandling.entity.AttributeSource#getAttribute(java.lang.String)
     */
    public Object getAttribute(String attributeName) {
        Object param = oct.getRequestObject().getParam(attributeName);
        //trim the spaces
        if (String.class.isInstance(param)) {
            return ((String) param).trim();
        }
        return param;
    }

    public Class getAttributeType(String attributeName) {
        Object param = oct.getRequestObject().getParam(attributeName);
        return param == null ? null : param.getClass();
    }

    /* (non-Javadoc)
     * @see de.tarent.commons.datahandling.entity.AttributeSource#getAttributeNames()
     */
    public List getAttributeNames() {
        if (attributeNames == null) {
            Set params = oct.getRequestObject().getRequestParameters().keySet();
            Iterator iter = params.iterator();
            int count = params.size();
            attributeNames = new ArrayList(count);
            while (iter.hasNext()) {
                attributeNames.add(iter.next());
            }
        }
        return attributeNames;
    }

}
