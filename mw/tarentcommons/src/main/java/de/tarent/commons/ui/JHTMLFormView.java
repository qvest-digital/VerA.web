/*
 * Copyright (c) tarent GmbH
 * Bahnhofstrasse 13 . 53123 Bonn
 * www.tarent.de . info@tarent.de
 *
 * Created on 18.10.2005
 */

package de.tarent.commons.ui;

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
