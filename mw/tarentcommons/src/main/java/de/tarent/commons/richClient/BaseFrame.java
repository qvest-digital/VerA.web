package de.tarent.commons.richClient;

import javax.swing.*;

import org.apache.commons.logging.Log;

import de.tarent.commons.logging.LogFactory;

import java.awt.*;

public abstract class BaseFrame implements CommonDialogServices, ApplicationFrame {

    private static final Log logger = LogFactory.getLog(BaseFrame.class);

	protected JFrame frame;
    private JPanel glassPanel;
    boolean iswaiting;

	public BaseFrame() {
		frame = new JFrame();

        glassPanel = new JPanel();
        getFrame().setGlassPane(glassPanel);
        glassPanel.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        glassPanel.setOpaque(false);
	}

    public void setVisible(boolean visible) {
        frame.setVisible(visible);
    }

    // Interface CommonDialogServices implementation

	public int askUser(String caption, String question, String[] answers, String[] tooltips, int defaultValue) {
        return askUser(getFrame(), caption, question, answers, tooltips, defaultValue);
    }

    public int askUser(JFrame parent, String caption, String question, String[] answers, String[] tooltips, int defaultValue) {
        logger.error("TODO: implement askUser()");
        return -1;
    }

    /**
     * Publish a System Error, which was not planed an may be a application bug.
     *
     */
	public void publishSystemError(String caption, String msg, Throwable e) {
		logger.error(msg, e);

		// FIXME Sebastian Mancke: Evaluation of extendedtext is unused.
		String extendedtext = null;
		// Aller tiefste Exception als Cause Message verwenden.
		if (e != null) {
			Throwable cause = e;
			while (cause.getCause() != null)
				cause = cause.getCause();
			extendedtext = cause.getMessage();
		}
        logger.error("TODO: implement publishError()");
	}

	public void showInfo(String caption, String message) {
        showInfo(getFrame(), caption, message);
	}

	public void showInfo(JFrame comp, String caption, String message) {
        logger.error("TODO: implement showInfo");
	}

    /**
     * Show an error in the category of user faults.
     */
	public void showError(String caption, String message) {
		showError(getFrame(), caption, message);
	}

    /**
     * Show an error in the category of user faults.
     */
	public void showError(JFrame comp, String caption, String message) {
        logger.error("TODO: implement showError");
	}

    /**
     * Method setWaiting. Setzt je nach Parameter den Mauspfeil auf "Sanduhr" bzw. "normal" sollte mit dem Parameter "true" aufgerufen werden bevor eine langwierige Operation
     * ausgefhrt wird. Nach Beendigung der Operation muss die Methode erneut aufgerufen werden, jedoch nun mit dem Parameter "false"
     *
     * @param iswaiting
     */
    public void setWaiting(boolean iswaiting) {
        if (iswaiting) {
            if (glassPanel != null) {
                glassPanel.setSize(getFrame().getSize());
                glassPanel.setVisible(true);
                getFrame().repaint();
            }
        } else {
            if (glassPanel != null)
                glassPanel.setVisible(false);
        }
    }

	// ------------------ getter and setter ------------------------
	public JFrame getFrame() {

		return frame;
	}
}
