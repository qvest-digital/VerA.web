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
import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JDialog;
import javax.swing.JButton;
import javax.swing.JScrollPane;

/**
 * This class displays the qfba details dialog.
 *
 * @author Michael Kleinhenz (m.kleinhenz@tarent.de)
 */
public class QualityFeedbackAgentDetailDialog extends JDialog {
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -3038149248710691582L;
    private JPanel jContentPane = null;
    private JButton closeButton = null;
    private String message = null;

    /**
     * Constructs a new qfba details dialog. It displays the given text message.
     *
     * @param pMessage Text message.
     */
    public QualityFeedbackAgentDetailDialog(String pMessage) {
        this(pMessage, null);
    }

    /**
     * Constructs a new qfba details dialog. It displays the given text message and the given exception.
     *
     * @param pMessage   Text message.
     * @param pException a Exception
     */

    public QualityFeedbackAgentDetailDialog(String pMessage, Exception pException) {
        super();
        this.setModal(true);
        this.setTitle("Details");
        this.message = pMessage;
        if (pException != null) {
            this.message += "\r\n\nTechnical Information:\r\n" + pException.toString();

            StackTraceElement[] stackTrace = pException.getStackTrace();
            for (int i = 0; i < stackTrace.length; i++) {
                this.message += "\r\n" + stackTrace[i].toString();
            }
        }
        initialize();
    }

    private void initialize() {
        this.setContentPane(getJContentPane());
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    private JPanel getJContentPane() {
        if (jContentPane == null) {
            jContentPane = new JPanel();
            jContentPane.setLayout(new BorderLayout());
            jContentPane.add(getCloseButton(), java.awt.BorderLayout.SOUTH);

            JEditorPane pane = new JEditorPane();
            pane.setContentType("text/plain");
            pane.setEditorKit(JEditorPane.createEditorKitForContentType("text/plain"));
            pane.setFocusable(false);
            pane.setEditable(false);
            pane.setText(message);

            JScrollPane scrollPane = new JScrollPane(pane);

            jContentPane.setPreferredSize(new Dimension(400, 300));
            jContentPane.add(scrollPane, "Center");
        }
        return jContentPane;
    }

    private JButton getCloseButton() {
        if (closeButton == null) {
            closeButton = new JButton();
            closeButton.setText("Close");
            closeButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    QualityFeedbackAgentDetailDialog.this.setVisible(false);
                    QualityFeedbackAgentDetailDialog.this.dispose();
                }
            });
        }
        return closeButton;
    }
}
