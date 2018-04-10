package de.tarent.commons.ui;

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
