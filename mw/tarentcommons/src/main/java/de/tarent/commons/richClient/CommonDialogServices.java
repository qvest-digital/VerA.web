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

package de.tarent.commons.richClient;

import javax.swing.JFrame;

public interface CommonDialogServices {

	/** Hauptfenster der GUI */
	public JFrame getFrame();

	public int askUser(String caption, String question, String[] answers, String[] tooltips, int defaultValue);

    public int askUser(JFrame parent, String caption, String question, String[] answers, String[] tooltips, int defaultValue);

	public void showInfo(String caption, String message);

	public void showInfo(JFrame comp, String caption, String message);

    /**
     * Show an error in the category of user faults.
     */
	public void showError(String caption, String message);

    /**
     * Show an error in the category of user faults.
     */
	public void showError(JFrame comp, String caption, String message);

    /**
     * Publish a System Error, which was not planed and may be an application bug.
     *
     */
	public void publishSystemError(String caption, String msg, Throwable e);

	//
	// Sonstige GUI-Wrapper
	//
	/**
	 * Setzt je nach Parameter den Mauspfeil auf "Sanduhr" bzw. "normal";
	 * sollte mit dem Parameter "true" aufgerufen werden, bevor eine
	 * langwierige Operation ausgeführt wird. Nach Beendigung der Operation
	 * muss die Methode erneut aufgerufen werden, jedoch nun mit dem Parameter
	 * "false"
	 *
	 * @param isWaiting
	 *            wenn true: Sanduhr anzeigen.
	 */
	public void setWaiting(boolean isWaiting);

}
