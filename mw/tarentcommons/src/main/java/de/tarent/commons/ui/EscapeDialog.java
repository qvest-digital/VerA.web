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

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;

/**
 * A Dialog that closes when escape key pressed.<p>
 * <p>
 * Note: it works only if a content pane is not empty. 
 * <p>
 * Extend this Dialog to enable your subclass to react on escape event.
 * You can also overwrite <tt>closeWindow</tt> method 
 * to handle this event in a custom way.
 * <p> 
 * @author Aleksej Palij (a.palij@tarent.de), tarent GmbH Bonn
 */
public class EscapeDialog extends JDialog {

    public EscapeDialog() {
        this( (Frame) null, false );
    }

    public EscapeDialog( Frame owner ) {
        this( owner, false );
    }

    public EscapeDialog( Frame owner, boolean modal ) {
        this( owner, null, modal );
    }

    public EscapeDialog( Frame owner, String title ) {
        this( owner, title, false );
    }

    public EscapeDialog( Frame owner, String title, boolean modal ) {
        super( owner, title, modal );
    }

    public EscapeDialog( Dialog owner ) {
        this( owner, false );
    }

    public EscapeDialog( Dialog owner, boolean modal ) {
        this( owner, null, modal );
    }

    public EscapeDialog( Dialog owner, String title ) {
        this( owner, title, false );
    }

    public EscapeDialog( Dialog owner, String title, boolean modal ) {
        super( owner, title, modal );
    }

    protected JRootPane createRootPane() {
        ActionListener actionListener = new ActionListener() {
            public void actionPerformed( ActionEvent actionEvent ) {
                closeWindow();
            }
        };
        JRootPane rootPane = new JRootPane();
        KeyStroke stroke = KeyStroke.getKeyStroke( KeyEvent.VK_ESCAPE, 0 );
        rootPane.registerKeyboardAction( actionListener, stroke, JComponent.WHEN_IN_FOCUSED_WINDOW );
        return rootPane;
    }
    
    /** 
     * Hides the window and releases all resources have been held by it.<p>
     * Overwrite to handle escape event in a custom way.
     */
    public void closeWindow(){
        setVisible( false );
        dispose();
    }
}
