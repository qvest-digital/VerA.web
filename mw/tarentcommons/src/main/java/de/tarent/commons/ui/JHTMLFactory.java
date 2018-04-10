package de.tarent.commons.ui;

import java.util.Map;

import javax.swing.text.Element;
import javax.swing.text.StyleConstants;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit.HTMLFactory;

/**
 * This class is the key for using arbitrary Swing elements in the
 * standard HTML panel. All new components are created by calling the create() method
 * of this class. Therefore, we check if the component that should be created is
 * a standard HTML element or a special custom defined widget. In the latter case, we
 * create a custom FormView that knows how to create a custom widget. If the component is
 * a standard HTML element, the super implementation is used.
 *
 * @author Michael Kleinhenz (m.kleinhenz@tarent.de)
 */
public class JHTMLFactory extends HTMLFactory implements ViewFactory
{
    private JHTMLPanel controller = null;
    private Map widgetMap = null;

    /**
     * Constructs a new JHTMLFactory.
     *
     * @param controller The controlling HTMLPanel.
     */
    public JHTMLFactory(JHTMLPanel controller, Map widgetMap)
    {
        super();
        this.controller  = controller;
        this.widgetMap = widgetMap;
    }

    /**
     * This is called from Swing only. It checks if the needed component is a
     * standard component or a custom defined one. Depending on this check, the
     * creation is delegated to the standard Swing process or to a custom JFormView.
     *
     * @param elem Element to be created.
     * @return FormView of element.
     */
    public View create(Element elem)
    {
        Object o = elem.getAttributes().getAttribute(StyleConstants.NameAttribute);

        if (o == HTML.Tag.INPUT || o == HTML.Tag.SELECT || o == HTML.Tag.TEXTAREA || o instanceof HTML.UnknownTag)
            return new JHTMLFormView(elem, controller, widgetMap);
        else
            return super.create(elem);
    }
}
