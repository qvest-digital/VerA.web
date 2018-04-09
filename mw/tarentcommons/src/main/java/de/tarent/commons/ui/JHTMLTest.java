/*
 * tarent commons,
 * a set of common components and solutions
 * Copyright (c) 2006-2007 tarent GmbH
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License,version 2
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 *
 * tarent GmbH., hereby disclaims all copyright
 * interest in the program 'tarent commons'
 * Signature of Elmar Geese, 14 June 2007
 * Elmar Geese, CEO tarent GmbH.
 */

package de.tarent.commons.ui;

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
 * @author Sebastian Mancke, Tarent GmbH
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
