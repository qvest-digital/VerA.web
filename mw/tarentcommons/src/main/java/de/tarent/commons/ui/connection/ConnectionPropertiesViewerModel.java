package de.tarent.commons.ui.connection;

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
import java.awt.Font;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.ListModel;

/**
 * @author Robert Schuster (r.schuster@tarent.de) tarent GmbH Bonn
 */

class ConnectionPropertiesViewerModel {

    String label, serverURL, module;
    boolean modifiable;

    DefaultListModel connectionPropertiesList = new DefaultListModel();

    Runnable updater;

    ConnectionPropertiesViewerModel(Runnable updater) {
        this.updater = updater;
        label = serverURL = module = "";
        modifiable = false;
    }

    void delete(int index) {
        connectionPropertiesList.remove(index);
    }

    void insert(ConnectionProperties cp, int index) {
        connectionPropertiesList.insertElementAt(cp, index);
    }

    void add(ConnectionProperties cp) {
        connectionPropertiesList.addElement(cp);
    }

    void clear() {
        connectionPropertiesList.clear();
    }

    void setSelected(int index) {
        if (index == -1) {
            // After deletion the selected index jumps to -1 which we
            // handle as "no selection".
            label = serverURL = module = "";
            modifiable = false;
        } else {
            ConnectionProperties cp = (ConnectionProperties) connectionPropertiesList.getElementAt(index);
            label = cp.label;
            serverURL = cp.serverURL;
            module = cp.moduleName;
            modifiable = cp.modifiable;
        }

        updater.run();
    }

    ListModel getListModel() {
        return connectionPropertiesList;
    }

    Renderer getRenderer() {
        return new Renderer();
    }

    class Renderer extends DefaultListCellRenderer {
        /**
         *
         */
        private static final long serialVersionUID = 157978706853131134L;

        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
                boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            // Prototype is a string and should not be rendered special.
            if (value instanceof String) {
                return this;
            }

            ConnectionProperties cp = (ConnectionProperties) value;

            setText(cp.label);
            setToolTipText(cp.serverURL);
            if (!cp.modifiable) {
                setFont(getFont().deriveFont(Font.ITALIC));
            }

            return this;
        }
    }
}
