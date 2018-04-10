
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
     */
    public void publishSystemError(String caption, String msg, Throwable e) {
        logger.error(msg, e);

        // FIXME Sebastian Mancke: Evaluation of extendedtext is unused.
        String extendedtext = null;
        // Aller tiefste Exception als Cause Message verwenden.
        if (e != null) {
            Throwable cause = e;
            while (cause.getCause() != null) {
                cause = cause.getCause();
            }
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
     * Method setWaiting. Setzt je nach Parameter den Mauspfeil auf "Sanduhr" bzw. "normal" sollte mit dem Parameter "true"
     * aufgerufen werden bevor eine langwierige Operation
     * ausgefhrt wird. Nach Beendigung der Operation muss die Methode erneut aufgerufen werden, jedoch nun mit dem Parameter
     * "false"
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
            if (glassPanel != null) {
                glassPanel.setVisible(false);
            }
        }
    }

    // ------------------ getter and setter ------------------------
    public JFrame getFrame() {

        return frame;
    }
}
