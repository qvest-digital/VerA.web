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

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/**
 * @author Fabian K&ouml;ster (f.koester@tarent.de) tarent GmbH Bonn
 */
public class UnsupportedVersionComplain {

    /**
     * Displays a warning dialog and returns whether the check which caused
     * the dialog to be opened should be done again at the next application started.
     *
     * @return false if user does not want to see this warning any more
     */
    public static boolean showUnsupportedVersionComplain(String appName) {
        // Warning dialog is presented in its own frame for the sole reason to have
        // a valid 'window entry' in the surrounding windowing environment in all cases
        // without the need to initialize a parental frame somewhere or reuse one (e.g.
        // the login window). However being on our own means that we have to implement
        // thread suspension ...

        // A special (privately-known) monitor object that is used
        // for thread suspension and re-awakening.
        final Object monitor = new Object();

        // A result array which is itself final and can be accessed
        // from an inner class to set its values (sort of a hack, but JLS compliant).
        final boolean result[] = new boolean[] { false };

        // Construction of the warning dialog.
        FormLayout l = new FormLayout("3dlu, 10dlu, 6dlu, pref:grow, 3dlu, pref:grow, 6dlu, 10dlu, 3dlu",
                "3dlu, pref, 12dlu, pref, 3dlu, pref, 3dlu, pref, 3dlu, pref, 12dlu, pref, 12dlu, pref, 3dlu, pref, 3dlu");

        final JFrame frame = new JFrame(Messages.getString("UnsupportedVersionComplain_Title"));

        frame.addWindowListener(new WindowAdapter() {

            public void windowClosing(WindowEvent arg0) {

                // Makes frame invisible and stores the result.
                super.windowClosing(arg0);
                result[0] = false;

                // Re-awakens the main thread.
                synchronized (monitor) {
                    monitor.notifyAll();
                }
            }
        });

        Container cp = frame.getContentPane();
        cp.setLayout(l);
        CellConstraints cc = new CellConstraints();

        cp.add(new JLabel(Messages.getFormattedString("UnsupportedVersionComplain_Unsupported", appName)), cc.xyw(2, 2, 7));

        cp.add(new JLabel(Messages.getString("UnsupportedVersionComplain_Implementation_Version")), cc.xyw(3, 4, 2));
        cp.add(new JLabel(System.getProperty("java.version")), cc.xyw(6, 4, 2));

        cp.add(new JLabel(Messages.getString("UnsupportedVersionComplain_Implementation_Vendor")), cc.xyw(3, 6, 2));
        cp.add(new JLabel(System.getProperty("java.vendor")), cc.xyw(6, 6, 2));

        cp.add(new JLabel(Messages.getString("UnsupportedVersionComplain_VM_Name")), cc.xyw(3, 8, 2));
        cp.add(new JLabel(System.getProperty("java.vm.name")), cc.xyw(6, 8, 2));

        cp.add(new JLabel(Messages.getString("UnsupportedVersionComplain_VM_Vendor")), cc.xyw(3, 10, 2));
        cp.add(new JLabel(System.getProperty("java.vm.vendor")), cc.xyw(6, 10, 2));

        cp.add(new JLabel(Messages.getString("UnsupportedVersionComplain_Again")), cc.xyw(2, 12, 7));

        // yes and no button get their own panel to make their layout independent from the rest
        // of the dialog (e.g. to have equal button widths).
        JButton yesButton = new JButton(Messages.getString("UnsupportedVersionComplain_Yes"));
        yesButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                // Makes frame invisible and stores the result.
                frame.setVisible(false);
                result[0] = true;

                // Re-awakens the main thread.
                synchronized (monitor) {
                    monitor.notifyAll();
                }
            }
        });

        frame.getRootPane().setDefaultButton(yesButton);

        JButton noButton = new JButton(Messages.getString("UnsupportedVersionComplain_No"));
        noButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                // Makes frame invisible and stores the result.
                frame.setVisible(false);
                result[0] = false;

                // Re-awakens the main thread.
                synchronized (monitor) {
                    monitor.notifyAll();
                }
            }
        });

        // Buttons resemble the layout of a JOptionPane dialog: They have the same width and
        // try to stay close in the middle.
        PanelBuilder pb = new PanelBuilder(new FormLayout("3dlu:grow, pref, 3dlu, pref, 3dlu:grow", "pref"));
        pb.add(yesButton, cc.xy(2, 1));
        pb.add(noButton, cc.xy(4, 1));
        pb.getLayout().setColumnGroups(new int[][] { { 2, 4 } });

        cp.add(pb.getPanel(), cc.xyw(2, 14, 7));

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // Sleeps until awakened by action events on the buttons.
        try {
            synchronized (monitor) {
                monitor.wait();
            }
        } catch (InterruptedException e) {
            // Expected (caused by monitor.notifyAll() ).
        }

        frame.dispose();

        return result[0];
    }
}
