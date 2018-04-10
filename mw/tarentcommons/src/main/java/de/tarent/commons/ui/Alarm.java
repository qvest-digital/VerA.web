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
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Robot;
import java.awt.Window;
import java.awt.color.ColorSpace;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class Alarm extends Window {
	/** serialVersionUID */
	private static final long serialVersionUID = -6899588866458187666L;

	public static void main(String[] args) throws Exception {
		Alarm alarm = new Alarm();
		alarm.setVisible(true);

		Thread.sleep(10000);
		System.exit(1);
	}

	protected Rectangle screensize;
	protected String text;
	protected Image capturedImage;
	protected Image alarmImage;

	public Alarm() {
		this("Alarm");
	}

	public Alarm(String text) {
		super(new Frame());
		this.text = text;

		capturedImage = getCaptureImage();
		alarmImage = getAlarmImage();

		addMouseListener(new DisposeListener(this) {
			public void mouseClicked(MouseEvent e) {
				dispose();
			}
		});
		setBounds(getScreenSize());
		setVisible(true);
	}

	public void paint(Graphics g) {
		g.drawImage(alarmImage, 0, 0, this);
	}

	private Image getCaptureImage() {
		try {
			return new Robot().createScreenCapture(getScreenSize());
		} catch (AWTException e) {
			return null;
		}
	}

	private Image getAlarmImage() {
		int w = getScreenSize().width;
		int h = getScreenSize().height;

		BufferedImage bi = new BufferedImage(w, h, ColorSpace.TYPE_RGB);
		createImage(bi.createGraphics());
		return bi;
	}

	private void createImage(Graphics2D g2d) {
		Font font = new Font("Verdana", Font.BOLD, 160);
		int w = getScreenSize().width;
		int h = getScreenSize().height;

		g2d.setFont(font);
		g2d.setRenderingHint(
				RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		FontMetrics metrics = g2d.getFontMetrics(font);

		int width = metrics.stringWidth("Alarm");
//		int height = metrics.getHeight();
		int top = h / 2; // + height / 2;
		int left = w / 2 - width / 2;

		g2d.drawImage(capturedImage, 0, 0, new Color(0, 0, 0, 127), this);

		g2d.setPaint(ColorHelper.getMetallic(Color.RED, h));
		g2d.fillRect(0, 0, w - 1, h - 1);

		createText(g2d, Color.BLACK, top, left, 4, 4);
		createText(g2d, Color.WHITE, top, left, 0, 255);
	}

	private void createText(Graphics2D g2d, Color color, int top, int left, int offset, int alpha) {
		g2d.setColor(ColorHelper.getAlpha(color, alpha));
		for (int ox = -offset; ox <= offset; ox++)
			for (int oy = -offset; oy <= offset; oy++)
				g2d.drawString(text, left + ox, top + oy);
	}

	protected Rectangle getScreenSize() {
		if (screensize != null)
			return screensize;

		return screensize =
			GraphicsEnvironment.
			getLocalGraphicsEnvironment().
			getMaximumWindowBounds();
	}
}
