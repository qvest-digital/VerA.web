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

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JFrame;

import de.tarent.commons.utils.Log;
import de.tarent.commons.utils.Version;

/**
 * This implements a transparent splash screen.
 *
 * @author Michael Kleinhenz (m.kleinhenz@tarent.de)
 */
public class SplashScreen extends JFrame {
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -2849648397422583671L;
    private int defaultScreenWidthMargin = 50;
    private int defaultScreenHeightMargin = 37;
    private Image capture;
    private Image picture;
    private Timer timer;

    /**
     * Shows a splash screen using a PNG image with alpha channel transparency.
     *
     * @param filename Path to the PNG relative to the application working directory.
     * @param w        Width of image in pixels.
     * @param h        Height of image in pixels.
     * @param millis   The duration of the display in milliseconds.
     * @throws URISyntaxException
     */
    public SplashScreen(String filename, int w, int h, long millis) {
        URL file = null;
        try {
            file = new File(filename).toURL();
        } catch (MalformedURLException e) {
            Log.error(this.getClass(), "Can't open splash screen image.", e);
        }
        int newW = w + defaultScreenWidthMargin;
        int newH = h + defaultScreenHeightMargin;
        setSize(newW, newH);
        setUndecorated(true);
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        int frmX = ((int) d.getWidth() - (w + defaultScreenWidthMargin)) / 2;
        int frmY = ((int) d.getHeight() - (h + defaultScreenHeightMargin)) / 2;
        setLocation(frmX, frmY);

        try {
            Robot rob = new Robot();
            Rectangle rect = new Rectangle(frmX, frmY, newW, newH);
            capture = rob.createScreenCapture(rect);
        } catch (AWTException e) {
            e.printStackTrace();
        }

        MediaTracker mt = new MediaTracker(this);

        try {
            picture = Toolkit.getDefaultToolkit().getImage(file).getScaledInstance(w, h, Image.SCALE_SMOOTH);
            mt.addImage(picture, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            mt.waitForAll();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // FIXME        setAlwaysOnTop(true);
        if (picture == null) {
            picture = createImage(w, h);
        }
        timer = new Timer();
        timer.schedule(new ExitTimerTask(this), millis);

        addMouseListener(new DisposeListener(this));
    }

    public void paint(Graphics g) {
        if (picture != null && capture != null) {
            capture.getGraphics().drawImage(picture,
                    0 + defaultScreenWidthMargin / 2,
                    0 + defaultScreenHeightMargin / 2, this);
            g.drawImage(capture, 0, 0, this);
            g.setColor(new Color(0, 0, 0));
            g.drawString(Version.getVersion(), 320, 350);
        }
    }

    private class ExitTimerTask extends TimerTask {
        private JFrame frm;

        public ExitTimerTask(JFrame frm) {
            this.frm = frm;
        }

        public void run() {
            frm.setVisible(false);
            frm.dispose();
        }
    }
}
