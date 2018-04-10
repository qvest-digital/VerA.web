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
