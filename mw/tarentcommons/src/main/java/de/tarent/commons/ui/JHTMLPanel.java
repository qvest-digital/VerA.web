package de.tarent.commons.ui;

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

import java.awt.Component;
import javax.swing.text.Element;

/**
 * This interface has to be implemented by all classes that wish to
 * use the JHTML widget. It is the communication link between the
 * internal engine of the JHTML widget and the main application
 * using this technology.
 * <p>
 * To use this technology, you have to follow these steps:
 * <ul>
 * <li>Implement this interface.
 * <li>Implement a mechanism to keep track of the created components inside
 * the JHTML widget. For each created component, the JHTMLEditorKit will call the
 * componentCreated() method. Store this information and use it to access the
 * components for common control tasks such as reading data, writing data or setting
 * parameters for the components. Access each component like it is a common Swing created
 * component.
 * <li>Implement some sort of configuration that maps a component name (String) to
 * a Swing component class (Class). Provide this map to the JHTMLEditorKit
 * <li>
 * </ul>
 *
 * @author Michael Kleinhenz (m.kleinhenz@tarent.de)
 */
public interface JHTMLPanel {
    /**
     * When the JHTML widget has created an actual instance of a widget, it is
     * announced with it's given name (taken from the name attribute in the JHTML source)
     * to the JHTMLPanel implementing object via this method.
     * <p>
     * This is needed for synchronization purposes between the JHTMLEditorKit display thread
     * that actually creates the view and the main thread.
     *
     * @param nameAttribute    Name of the component as given by the name attribute in the JHTML source.
     * @param component        The widget itself.
     * @param componentElement The XML element for this component.
     */
    public void componentCreated(String nameAttribute, Component component, Element componentElement);
}
