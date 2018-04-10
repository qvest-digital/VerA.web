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
import java.awt.GridLayout;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.text.html.HTMLEditorKit;

import de.tarent.commons.ui.JHTMLEditorKit;
import de.tarent.commons.ui.JHTMLPanel;
import de.tarent.commons.datahandling.binding.BindingManager;
import javax.swing.text.Element;
import java.util.*;
import javax.swing.text.AttributeSet;
import de.tarent.commons.datahandling.binding.BeanBinding;
import de.tarent.commons.logging.LogFactory;

import javax.swing.text.*;
import de.tarent.commons.utils.*;
import javax.swing.text.html.HTML;

import org.apache.commons.logging.Log;

/**
 * The HTMLPanel is a special panel class that uses a extended version of HTML
 * to render the panel's UI.
 * @author Sebastian Mancke (s.mancke@tarent.de)
 */
public class JHTMLEntityForm extends JPanel implements JHTMLPanel
{

    /** serialVersionUID */
	private static final long serialVersionUID = 1994966741308879428L;
	public static final String ATTRIBUTE_CONVERTER = "converter";
    public static final String ATTRIBUTE_TO = "to";
    public static final String ATTRIBUTE_PROPERTY = "property";
    public static final String TAG_BIND = "bind";

    private static final Log logger = LogFactory.getLog(JHTMLEntityForm.class);

    private JScrollPane scrollPane;
    private HTMLEditorKit htmlEditorKit;
    private Map widgetMap = null;
    private Map componentMap = null;
    private String htmlUrl = null;
    BindingManager bindingManager = null;
    private Object lastCreatedComponent;
    JEditorPane pane = null;

    /** Map of lists with the component as key and a list of
     *  the bound properties as values in the list.
     */
    private Map existingBindings = new HashMap();

    /** List of bound ComponentElements elements
     */
    private List existingBindingComponentElements = new ArrayList();

    /**
     * Constructs a new HTML panel.
     *
     * @param htmlFile HTML source for constucting the UI.
     */
    public JHTMLEntityForm(String htmlFile)
    {
        this(htmlFile, new HashMap(), null);
    }

    /**
     * Constructs a new HTML panel.
     *
     * @param htmlFile HTML source for constucting the UI.
     * @param bindingManager a BindingManager for DataBinding of the GUI-Components, null if no should be used
     */
    public JHTMLEntityForm(String htmlFile, BindingManager bindingManager)
    {
        this(htmlFile, new HashMap(), bindingManager);
    }

    /**
     * Constructs a new HTML panel.
     *
     * @param htmlFile HTML source for constucting the UI.
     * @param widgetMap a custom mapping from windget names to Class Objects for the widgets
     * @param bindingManager a BindingManager for DataBinding of the GUI-Components, null if no should be used
     */
    public JHTMLEntityForm(String htmlFile, Map widgetMap, BindingManager bindingManager)
    {
        super();
        this.widgetMap = widgetMap;
        this.componentMap = new HashMap();
        this.htmlUrl = htmlFile;
        this.bindingManager = bindingManager;
        initialize();
    }

    private void initialize()
    {
        // Construct GUI
        this.setLayout(new GridLayout(1,1));

        // Get HTML widget
        this.add(getHTMLPane());
    }

    public void load(String newHtmlFile) {
        this.htmlUrl = newHtmlFile;
        reload();
    }

    public void reload() {
        pane.getDocument().putProperty(Document.StreamDescriptionProperty, null);
        try {
            pane.setPage(new URL(htmlUrl));
        } catch (IOException e) {
            logger.error("Can't initialize GUI URL: " + htmlUrl, e);
            throw new RuntimeException("Can't initialize GUI URL: " + htmlUrl);
        }
    }

    private JScrollPane getHTMLPane()
    {
        if (scrollPane == null)
        {
            pane = new JEditorPane();
            pane.setContentType("text/html");
            htmlEditorKit = new JHTMLEditorKit(this, widgetMap);
            pane.setEditorKit(htmlEditorKit);
            pane.setFocusable(true);
            pane.setEditable(false);
            reload();
            pane.setBackground(UIManager.getColor("Label.background"));

            pane.setBorder(BorderFactory.createEmptyBorder());
            scrollPane = new JScrollPane(pane);
            scrollPane.setBorder(BorderFactory.createEmptyBorder());
        }
        return scrollPane;
    }

    public String toString() {
        return "JHTMLEntityForm["+htmlUrl+"]";
    }

    /**
     * This is a callback called from the custom HTML renderer when a component
     * is actually created. This is used for syncronizing the rendering thread
     * with the control (main) thread.
     *
     * @param name Name of the component.
     * @param component Component that was created.
     * @param componentElement The XML element for this component.
     */
    public void componentCreated(final String name, final Component component, final Element componentElement)
    {
        if (component == null) {
            if (componentElement.getName().equals(TAG_BIND)) {

                // every binding componentElement is only respected once
                if (!existingBindingComponentElements.contains(componentElement)) {
                    existingBindingComponentElements.add(componentElement);

                    AttributeSet attributes = componentElement.getAttributes();
                    //                     System.out.println("XXX found binding: "+attributes.getAttribute("idx"));
                    //                     System.out.println("component: "+componentElement.getClass());
                    //                     System.out.println("component: "+componentElement.hashCode());
                    //                     ((AbstractDocument.AbstractElement)componentElement).dump(System.out, 0);

                    //                     Enumeration e = attributes.getAttributeNames();
                    //                     while (e.hasMoreElements()) {
                    //                         Object o = e.nextElement();
                    //                         System.out.print(o+" => "+attributes.getAttribute(o)+", ");
                    //                     }
                    //                     System.out.println();

                    createBindingFor(lastCreatedComponent, (String)attributes.getAttribute(ATTRIBUTE_PROPERTY), (String)attributes.getAttribute(ATTRIBUTE_TO), (String)attributes.getAttribute(ATTRIBUTE_CONVERTER));
                }
            }
            else if (logger.isDebugEnabled()) {
                StringBuffer attributes = new StringBuffer();
                for (Enumeration en = componentElement.getAttributes().getAttributeNames(); en.hasMoreElements();) {
                    Object key = en.nextElement();
                    attributes.append(key)
                        .append(" => ")
                        .append(componentElement.getAttributes().getAttribute(key));
                    if (en.hasMoreElements())
                        attributes.append(", ");
                }
                logger.debug("Null component created by ["+componentElement.getName() +": "+ attributes+"]");
            }
            return;
        }

        if (logger.isDebugEnabled())
            logger.debug("Component added to HTML panel: " + name + " as " + component.getClass());
        componentMap.put(name, component);
        lastCreatedComponent = component;
        this.configureComponent(name, component, componentElement);
    }

    /**
     * Returns the component registered under the given name. This is the main
     * way to access the components on a html panel. The name is the value of
     * the name attribute in the html source. If you need to read or set some
     * values or properties of a component, get the component by passing it's
     * name to this method.
     *
     * @param name Name of the component.
     * @return Component.
     */
    public Component getComponentByName(String name)
    {
        Component out = (Component)componentMap.get(name);
        if (out==null)
            throw new RuntimeException("Component not created yet: " + name);
        return out;
    }

    /**
     * Create a data Binding and register it on the BindingManager
     */
    public void createBindingFor(Object component, String property, String modelProperty, String converter) {
        if (component instanceof JScrollPane)
            component = ((JScrollPane)component).getViewport().getView();

        try {
            // ensure, that each property is only bound once
            List boundProperties = (List)existingBindings.get(component);
            if (boundProperties == null) {
                boundProperties = new ArrayList(1);
                existingBindings.put(component, boundProperties);
            }
            if (!boundProperties.contains(property)) {
                if (modelProperty.startsWith("$") || modelProperty.startsWith("#")) {
                    if (bindingManager != null) {
                        logger.debug("binding property '"+property+"' for component '"+component+"' to '"+modelProperty+"'");
                        BeanBinding binding = new BeanBinding(component, property, modelProperty.substring(1));
                        binding.setReadOnly(modelProperty.startsWith("#"));
                        bindingManager.addBinding(binding);
                        boundProperties.add(property);
                    } else
                        logger.warn("no binding manager set, but binding found");
                } else {
                    logger.debug("setting property '"+property+"' for component '"+component+"' to '"+modelProperty+"'");
                    Pojo.set(component, property, modelProperty, true);
                }
            }
        } catch (IllegalArgumentException e) {
            logger.warn("Error on setting property '"+property+"' for component '"+component+"' to '"+modelProperty+"'", e);
        } catch (Exception e) {
            logger.error("Error on setting property '"+property+"' for component '"+component+"' to '"+modelProperty+"'", e);
        }
    }

    /**
     * configuring a component. Here we create a binding for every unknown attribute of the component.
     * You can change the configuration strategy by overriding this method.
     */
    public void configureComponent(final String name, final Component component, final Element componentElement) {

        AttributeSet attributes = componentElement.getAttributes();
        for (Enumeration en = attributes.getAttributeNames(); en.hasMoreElements();) {
            Object key = en.nextElement();
            Object value = attributes.getAttribute(key);
            //System.out.println("key: "+key+" ("+key.getClass()+") => "+value);
            // string key-value attribute is used as binding.
            // all attributes handled from swing-html have other types.
            if (key instanceof String && value instanceof String)
                createBindingFor(component, key.toString(), (String)attributes.getAttribute(key), null);
            else if (key instanceof HTML.Attribute && value instanceof String
                     && ("text".equals(key.toString())
                         || ("selected".equals(key.toString()))))
                createBindingFor(component, key.toString(), (String)attributes.getAttribute(key), null);
        }
    }

	public JEditorPane getJEditorPane() {
		return pane;
	}

	public void setJEditorPane(JEditorPane pane) {
		this.pane = pane;
	}
}
