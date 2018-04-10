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
import java.util.Map;

import javax.swing.text.Element;
import javax.swing.text.html.FormView;
import javax.swing.text.html.HTML;

/**
 * This represents a single component in the HTML panel. Because this
 * class is only used when a custom widget is encountered, this handles only
 * the custom components as defined in the configuration.
 *
 * @author Michael Kleinhenz (m.kleinhenz@tarent.de)
 */
public class JHTMLFormView extends FormView
{
    private JHTMLPanel controller = null;
    private Map widgetMap = null;
    private String typeAttribute = null;
    private String nameAttribute = null;
    private Element element;

    /**
     * Constructs a new JFormView.
     *
     * @param elem Element that should be created.
     * @param controller Controlling JHTMLPanel.
     */
    public JHTMLFormView(Element elem, JHTMLPanel controller, Map widgetMap)
    {
        super(elem);
        this.controller = controller;
        this.widgetMap = widgetMap;
        this.element = elem;

        nameAttribute  = (String)elem.getAttributes().getAttribute(HTML.Attribute.NAME);
        typeAttribute = (String)elem.getAttributes().getAttribute(HTML.Attribute.TYPE);
    }

    /**
     * This actually created the component by looking up the contents of the
     * "type" attribute of the input element in the configuration. The component
     * is created and returned to Swing which places it into the rendered HTML. This
     * virtually extends the HTML language to include arbitrary Swing widgets in
     * a HTML rendered panel.
     * <p>
     * Please note the callback to the controller's componentCreated method. This is
     * needed for synchronizing the creation of the HTML panel's content and the
     * main thread of the main application. Normally, the HTML is rendered in the background and
     * the widgets are not available when the main thread returns to the initialize method
     * of the panel. Thus, if the panel tries to register listeners on the widgets in the
     * JHTML panel, it may get errors. This is resolved by placing the component
     * configuration including the listener registration in the componentCreated method.
     *
     * @return Created component.
     */
    protected Component createComponent()
    {
        Component component = null;

        if (widgetMap.containsKey(typeAttribute))
        {
            // a tag defined or overwritten in the configuration was used
            Class componentClass = (Class)widgetMap.get(typeAttribute);

            try
            {
                component = (Component)componentClass.newInstance();
            }
            catch (InstantiationException e)
                {
                throw new RuntimeException("Can't get custom widget instance: " + componentClass.getName());
            }
            catch (IllegalAccessException e)
            {
                throw new RuntimeException("Illegal access while creating custom widget instance: " + componentClass.getName());
            }

            controller.componentCreated(nameAttribute, component, element);
        }
        else
        {
            // standard type was used - rely on standard Swing
            component = super.createComponent();
            controller.componentCreated(nameAttribute, component, element);
        }

        return component;
    }
}
