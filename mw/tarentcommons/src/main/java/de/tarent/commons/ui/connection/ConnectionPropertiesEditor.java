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

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import de.tarent.commons.ui.CommonDialogButtons;
import de.tarent.commons.ui.EscapeDialog;
import de.tarent.commons.ui.Messages;

/**
 * @author Robert Schuster (r.schuster@tarent.de) tarent GmbH Bonn
 */

class ConnectionPropertiesEditor extends EscapeDialog {

    /**
     *
     */
    private static final long serialVersionUID = 2569284015031831219L;

    private boolean cancelled = true;

    private JTextField connectionLabel;

    private JTextField serverURL;

    private JTextField module;

    private ConnectionPropertiesViewer parent;

    private String editLabel;

    private ActionListener actionListener;

    String connectionLabelCopy, serverURLCopy, moduleCopy;

    ConnectionPropertiesEditor(ConnectionPropertiesViewer parent) {
        super(parent, true);

        this.parent = parent;

        init();
    }

    public boolean wasCancelled() {
        return cancelled;
    }

    private void init() {
        initComponents();

        pack();
        setLocationRelativeTo(getOwner());
    }

    void initEntry(String connectionLabel, String serverURL, String module) {
        if (connectionLabel != null && connectionLabel.trim().length() != 0) {
            editLabel = connectionLabel;
        }

        this.connectionLabel.setText(connectionLabel);
        this.serverURL.setText(serverURL);
        this.module.setText(module);
    }

    private void initComponents() {
        setTitle(Messages.getString("ConnectionPropertiesEditor_Title"));

        connectionLabel = new JTextField(30);
        serverURL = new JTextField();
        module = new JTextField();

        Container cp = getContentPane();
        FormLayout l = new FormLayout(
                "3dlu, pref, 3dlu, 150dlu:grow, 3dlu", // columns
                "3dlu, pref, 3dlu, pref, 3dlu, pref, 6dlu, pref, 3dlu"); // rows
        cp.setLayout(l);

        CellConstraints cc = new CellConstraints();

        cp.add(new JLabel(Messages.getString("ConnectionPropertiesEditor_ConnectionLabel")), cc.xy(2, 2));
        cp.add(connectionLabel, cc.xy(4, 2));

        cp.add(new JLabel(Messages.getString("ConnectionPropertiesEditor_ServerURL")), cc.xy(2, 4));
        cp.add(serverURL, cc.xy(4, 4));

        cp.add(new JLabel(Messages.getString("ConnectionPropertiesEditor_Module")), cc.xy(2, 6));
        cp.add(module, cc.xy(4, 6));

        cp.add(CommonDialogButtons.getSubmitCancelButtons(getActionListener(), getRootPane()), cc.xyw(2, 8, 3));
    }

    protected ActionListener getActionListener() {
        if (actionListener == null) {
            actionListener = new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    if (e.getActionCommand().equals("submit")) {
                        cancelled = false;

                        connectionLabelCopy = connectionLabel.getText().trim();
                        serverURLCopy = serverURL.getText().trim();
                        moduleCopy = module.getText().trim();

                        if (connectionLabelCopy.length() == 0) {
                            JOptionPane.showMessageDialog(
                                    ConnectionPropertiesEditor.this,
                                    Messages.getString("ConnectionPropertiesEditor_LabelInvalid_Message"));
                            return;
                        }

                        if (serverURLCopy.length() == 0) {
                            JOptionPane.showMessageDialog(
                                    ConnectionPropertiesEditor.this,
                                    Messages.getString("ConnectionPropertiesEditor_ServerURLInvalid_Message"));
                            return;
                        }

                        if (moduleCopy.length() == 0) {
                            JOptionPane.showMessageDialog(
                                    ConnectionPropertiesEditor.this,
                                    Messages.getString("ConnectionPropertiesEditor_ModuleInvalid_Message"));
                            return;
                        }

                        // if we are not in edit mode or the label has just been modified,
                        // check if a connection with this label is already existent

                        if (editLabel == null || !connectionLabelCopy.equals(editLabel)) {

                            if (parent.isLabelAlreadyExistent(connectionLabelCopy)) {
                                JOptionPane.showMessageDialog(
                                        ConnectionPropertiesEditor.this,
                                        Messages.getString("ConnectionPropertiesEditor_LabelAlreadyExistent_Message"));
                                return;
                            }
                        }

                        ConnectionPropertiesEditor.this.setVisible(false);
                    } else if (e.getActionCommand().equals("cancel")) {
                        cancelled = true;

                        ConnectionPropertiesEditor.this.setVisible(false);
                    }
                }
            };
        }
        return actionListener;
    }

    public void setVisible(boolean b) {
        if (b) {
            // Set to true, the default state after closing (which may happen
            // by pressing ESC).
            cancelled = true;
        }

        super.setVisible(b);

        if (!b) {
            // Resets the textfields (for later reuse).
            initEntry("", "", "");
            editLabel = null;
        }
    }
}
