/*
 * Copyright (c) tarent GmbH
 * Bahnhofstrasse 13 . 53123 Bonn
 * www.tarent.de . info@tarent.de
 *
 * Created on 28.04.2005
 */
package de.tarent.commons.ui;

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
public class SplashScreen extends JFrame
{
    /** serialVersionUID */
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
     * @param w Width of image in pixels.
     * @param h Height of image in pixels.
     * @param millis The duration of the display in milliseconds.
     * @throws URISyntaxException
     */
    public SplashScreen(String filename, int w, int h, long millis)
    {
        URL file = null;
        try
        {
            file = new File(filename).toURL();
        }
        catch (MalformedURLException e)
        {
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

        try
        {
            Robot rob = new Robot();
            Rectangle rect = new Rectangle(frmX, frmY, newW, newH);
            capture = rob.createScreenCapture(rect);
        }
        catch (AWTException e)
        {
            e.printStackTrace();
        }

        MediaTracker mt = new MediaTracker(this);

        try
        {
            picture = Toolkit.getDefaultToolkit().getImage(file).getScaledInstance(w, h, Image.SCALE_SMOOTH);
            mt.addImage(picture, 0);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        try
        {
            mt.waitForAll();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }

// FIXME        setAlwaysOnTop(true);
        if (picture == null)
            picture = createImage(w, h);
        timer = new Timer();
        timer.schedule(new ExitTimerTask(this), millis);

        addMouseListener(new DisposeListener(this));
    }

    public void paint(Graphics g)
    {
        if (picture != null && capture != null)
        {
            capture.getGraphics().drawImage(picture,
                    0 + defaultScreenWidthMargin / 2,
                    0 + defaultScreenHeightMargin / 2, this);
            g.drawImage(capture, 0, 0, this);
            g.setColor(new Color(0,0,0));
            g.drawString(Version.getVersion(), 320, 350);
        }
    }

    private class ExitTimerTask extends TimerTask
    {
        private JFrame frm;
        public ExitTimerTask(JFrame frm)
        {
            this.frm = frm;
        }

        public void run()
        {
            frm.setVisible(false);
            frm.dispose();
        }
    }
}
