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
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import de.tarent.commons.datahandling.ListFilter;

/**
 * @author Fabian K&ouml;ster (f.koester@tarent.de) tarent GmbH Bonn
 */
public class FilterPanel extends JPanel {

    /**
     *
     */
    private static final long serialVersionUID = 8500774231416921033L;
    protected DescriptiveTextField textField;
    protected JComboBox filterSelection;

    // java 1.5: protected List<ListFilter> availableFilters;
    protected List availableFilters;

    protected List filterChangeListeners;

    // java 1.5: public FilterPanel(List<ListFilter> availableFilters) {
    public FilterPanel(List availableFilters) {

        this.availableFilters = availableFilters;

        FormLayout layout = new FormLayout("fill:pref:grow, 2dlu, pref", // columns
                "fill:pref"); // rows

        layout.setColumnGroups(new int[][] { { 1, 3 } });

        setLayout(layout);

        CellConstraints cc = new CellConstraints();

        add(getTextField(), cc.xy(1, 1));
        add(getFilterSelection(), cc.xy(3, 1));
    }

    protected DescriptiveTextField getTextField() {
        if (textField == null) {
            textField = new DescriptiveTextField("Enter search params here...");
            textField.getDocument().addDocumentListener(new DocumentListener() {

                /**
                 * @see javax.swing.event.DocumentListener#changedUpdate(javax.swing.event.DocumentEvent)
                 */
                public void changedUpdate(DocumentEvent e) {
                    fireFilterChanged();
                }

                /**
                 * @see javax.swing.event.DocumentListener#insertUpdate(javax.swing.event.DocumentEvent)
                 */
                public void insertUpdate(DocumentEvent e) {
                    fireFilterChanged();
                }

                /**
                 * @see javax.swing.event.DocumentListener#removeUpdate(javax.swing.event.DocumentEvent)
                 */
                public void removeUpdate(DocumentEvent e) {
                    fireFilterChanged();
                }

            });
        }

        return textField;
    }

    protected JComboBox getFilterSelection() {
        if (filterSelection == null) {
            filterSelection = new JComboBox(availableFilters.toArray());
            filterSelection.setRenderer(new DefaultListCellRenderer() {

                /**
                 *
                 */
                private static final long serialVersionUID = 3011751938668796062L;

                /**
                 * @see javax.swing.DefaultListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object,
                 * int, boolean, boolean)
                 */
                public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
                        boolean cellHasFocus) {
                    return super.getListCellRendererComponent(list, ((ListFilter) value).getFilterName(), index, isSelected,
                            cellHasFocus);
                }

            });
            filterSelection.addItemListener(new ItemListener() {

                /**
                 * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
                 */
                public void itemStateChanged(ItemEvent e) {
                    if (e.getStateChange() == ItemEvent.SELECTED) {
                        fireFilterChanged();
                    }
                }
            });
            filterSelection.addMouseWheelListener(new ComboBoxMouseWheelNavigator(filterSelection));
        }

        return filterSelection;
    }

    public ListFilter getActiveFilter() {
        return (ListFilter) getFilterSelection().getSelectedItem();
    }

    public void addFilterChangeListener(FilterChangeListener listener) {
        getFilterChangeListeners().add(listener);
    }

    public void removeFilterChangeListener(FilterChangeListener listener) {
        getFilterChangeListeners().remove(listener);
    }

    protected List getFilterChangeListeners() {
        if (filterChangeListeners == null) {
            filterChangeListeners = new ArrayList();
        }
        return filterChangeListeners;
    }

    protected void fireFilterChanged() {
        Iterator it = getFilterChangeListeners().iterator();

        while (it.hasNext()) {
            ((FilterChangeListener) it.next()).filterChanged((ListFilter) getFilterSelection().getSelectedItem());
        }
    }

    public interface FilterChangeListener {
        public void filterChanged(ListFilter newFilter);
    }
}
