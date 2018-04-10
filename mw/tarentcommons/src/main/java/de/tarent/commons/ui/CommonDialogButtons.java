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
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JRootPane;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/**
 * @author Fabian K&ouml;ster (f.koester@tarent.de), tarent GmbH Bonn
 */
public class CommonDialogButtons extends JPanel {
    /**
     *
     */
    private static final long serialVersionUID = -3446961333487571389L;

    protected JButton submitButton;
    protected JButton cancelButton;

    public final static String SUBMIT = Messages.getString("CommonDialogButtons_Submit"); //$NON-NLS-1$
    public final static String CANCEL = Messages.getString("CommonDialogButtons_Cancel"); //$NON-NLS-1$

    public final static String SUBMIT_TOOLTIP = Messages.getString("CommonDialogButtons_Submit_Tooltip"); //$NON-NLS-1$
    public final static String CANCEL_TOOLTIP = Messages.getString("CommonDialogButtons_Cancel_Tooltip"); //$NON-NLS-1$

    protected String submitText = SUBMIT;
    protected String cancelText = CANCEL;

    // TODO define icon
    protected ImageIcon submitIcon = null;
    protected ImageIcon cancelIcon = new ImageIcon(
            Toolkit.getDefaultToolkit().getImage(getClass().getResource("/de/tarent/commons/gfx/process-stop.png")));
            //$NON-NLS-1$

    protected ActionListener actionListener;

    protected List actionListeners;

    protected CommonDialogButtons(String submitText, String cancelText, ImageIcon submitIcon, ImageIcon cancelIcon,
            ActionListener listener, JButton actionButton, boolean submitVisible, JRootPane rootPane) {
        super();

        if (submitText != null) {
            this.submitText = submitText;
        }
        if (cancelText != null) {
            this.cancelText = cancelText;
        }

        if (submitIcon != null) {
            this.submitIcon = submitIcon;
        }
        if (cancelIcon != null) {
            this.cancelIcon = cancelIcon;
        }

        if (listener != null) {
            addActionListener(listener);
        }

        FormLayout layout = new FormLayout(
                "pref, 5dlu, pref, pref:grow, pref", // columns //$NON-NLS-1$
                "pref"); // rows //$NON-NLS-1$

        setLayout(layout);

        CellConstraints cc = new CellConstraints();

        add(getSubmitButton(), cc.xy(1, 1));
        getSubmitButton().setVisible(submitVisible);

        // if a RootPane is given set the submit button as default for it
        // if submit button should not be visible, set cancel-button as default
        if (rootPane != null) {
            if (submitVisible) {
                rootPane.setDefaultButton(getSubmitButton());
            } else {
                rootPane.setDefaultButton(getCancelButton());
            }
        }

        if (actionButton != null) {
            add(actionButton, cc.xy(3, 1));
        }
        add(getCancelButton(), cc.xy(5, 1));
    }

    public static CommonDialogButtons getSubmitCancelButtons(ActionListener listener) {
        return getSubmitCancelButtons(null, null, null, null, listener);
    }

    public static CommonDialogButtons getSubmitCancelButtons(ActionListener listener, JRootPane rootPane) {
        return getSubmitCancelButtons(null, null, null, null, listener, rootPane);
    }

    public static CommonDialogButtons getCancelButton(ActionListener listener) {
        return new CommonDialogButtons(null, null, null, null, listener, null, false, null);
    }

    public static CommonDialogButtons getCancelButton(ActionListener listener, JRootPane rootPane) {
        return new CommonDialogButtons(null, null, null, null, listener, null, false, rootPane);
    }

    public static CommonDialogButtons getSubmitCancelButtons(String submitText, String cancelText, ActionListener listener) {
        return getSubmitCancelButtons(submitText, cancelText, null, null, listener);
    }

    public static CommonDialogButtons getSubmitCancelButtons(String submitText, String cancelText, ImageIcon submitIcon,
            ImageIcon cancelIcon, ActionListener listener) {
        return getSubmitCancelAndActionButtons(submitText, cancelText, submitIcon, cancelIcon, listener, null);
    }

    public static CommonDialogButtons getSubmitCancelButtons(String submitText, String cancelText, ImageIcon submitIcon,
            ImageIcon cancelIcon, ActionListener listener, JRootPane rootPane) {
        return getSubmitCancelAndActionButtons(submitText, cancelText, submitIcon, cancelIcon, listener, null, rootPane);
    }

    public static CommonDialogButtons getSubmitCancelAndActionButtons(ActionListener listener, JButton actionButton) {
        return getSubmitCancelAndActionButtons(null, null, null, null, listener, actionButton);
    }

    public static CommonDialogButtons getSubmitCancelAndActionButtons(String submitText, String cancelText, ImageIcon submitIcon,
            ImageIcon cancelIcon, ActionListener listener, JButton actionButton) {
        return new CommonDialogButtons(submitText, cancelText, submitIcon, cancelIcon, listener, actionButton, true, null);
    }

    public static CommonDialogButtons getSubmitCancelAndActionButtons(String submitText, String cancelText, ImageIcon submitIcon,
            ImageIcon cancelIcon, ActionListener listener, JButton actionButton, JRootPane rootPane) {
        return new CommonDialogButtons(submitText, cancelText, submitIcon, cancelIcon, listener, actionButton, true, rootPane);
    }

    public JButton getSubmitButton() {
        if (this.submitButton == null) {
            this.submitButton = new JButton(this.submitText);
            this.submitButton.addActionListener(getActionListener());
            this.submitButton.setActionCommand("submit"); //$NON-NLS-1$
            this.submitButton.setIcon(this.submitIcon);
            this.submitButton.setToolTipText(SUBMIT_TOOLTIP);
        }
        return this.submitButton;
    }

    public JButton getCancelButton() {
        if (this.cancelButton == null) {
            this.cancelButton = new JButton(this.cancelText);
            this.cancelButton.addActionListener(getActionListener());
            this.cancelButton.setActionCommand("cancel"); //$NON-NLS-1$
            this.cancelButton.setIcon(this.cancelIcon);
            this.cancelButton.setToolTipText(CANCEL_TOOLTIP);
        }
        return this.cancelButton;
    }

    public void addActionListener(ActionListener listener) {
        getActionListeners().add(listener);
    }

    public void removeActionListener(ActionListener listener) {
        getActionListeners().remove(listener);
    }

    public void setSubmitButtonEnabled(boolean enabled) {
        getSubmitButton().setEnabled(enabled);
    }

    protected void fireActionEvent(ActionEvent event) {
        Iterator it = getActionListeners().iterator();
        while (it.hasNext()) {
            ((ActionListener) it.next()).actionPerformed(event);
        }
    }

    protected List getActionListeners() {
        if (this.actionListeners == null) {
            this.actionListeners = new ArrayList();
        }
        return this.actionListeners;
    }

    protected ActionListener getActionListener() {
        if (this.actionListener == null) {
            this.actionListener = new CommonDialogButtonsActionListener();
        }
        return this.actionListener;
    }

    protected class CommonDialogButtonsActionListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            CommonDialogButtons.this.fireActionEvent(e);
        }
    }
}
