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

import javax.swing.*;

import java.awt.event.*;
import de.tarent.commons.datahandling.binding.*;
import java.util.*;
import java.io.File;
import java.awt.*;
import java.net.*;

/**
 * Test application for JHTML panels.
 *
 *
 * @author Sebastian Mancke, tarent GmbH
 */
public final class JHTMLTest {

    JTextField urlField = new JTextField();
    JHTMLEntityForm form = null;

    /**
     * Erstellt eine neue <code>HTML</code> Instanz.
     *
     */
    private JHTMLTest(String[] args) throws Exception {
        if (args.length != 1) {
            System.out.println("First argument has to be a html file.");
            System.exit(0);
        }

        String fileURL = args[0];
        if (-1 == fileURL.indexOf("://"))
            fileURL = new File(args[0]).toURL().toString();
        urlField.setText(args[0]);

        BindingManager bindingManager = new BindingManager();
        MapModel mm = new MapModel();
        mm.setAttribute("alter", new Integer(26));
        mm.setAttribute("male", new Integer(1));
        bindingManager.setModel(mm);

        Map cMap = new HashMap();
        cMap.put("label", JLabel.class);
        form = new JHTMLEntityForm(fileURL, cMap, bindingManager);

        JFrame frame = new JFrame("HTML Form");

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(getControlPanel(), BorderLayout.NORTH);
        panel.add(form, BorderLayout.CENTER);
        frame.getContentPane().add(panel);
        frame.setSize(700, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    /**
     * Describe <code>main</code> method here.
     *
     * @param args a <code>String[]</code> value
     */
    public static void main(final String[] args)
        throws Exception {
        new JHTMLTest(args);
    }

    JPanel getControlPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JButton loadButton = new JButton("Load");
        ActionListener loadListener = new ActionListener() {
                public void actionPerformed(final ActionEvent e) {
                    try {
                        String fileURL = urlField.getText();
                        if (-1 == fileURL.indexOf("://"))
                            fileURL = new File(fileURL).toURL().toString();
                        form.load(fileURL);
                    } catch (MalformedURLException me) {
                        me.printStackTrace();
                    }
                }
            };
        loadButton.addActionListener(loadListener);
        urlField.addActionListener(loadListener);

        panel.add(urlField, BorderLayout.CENTER);
        panel.add(loadButton, BorderLayout.EAST);
        return panel;
    }
}
