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

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Robot;
import java.awt.SystemColor;
import java.awt.Window;
import java.awt.color.ColorSpace;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.Serializable;

import javax.swing.SwingConstants;

/**
 * This class provides you a simple way to show user information as an
 * "nice looking on-screen-display (OSD) dialog".
 *
 * @author Christoph Jerolimov, tarent GmbH
 */
public class OSD implements Serializable, Cloneable, SwingConstants {
	/** serialVersionUID */ // just another cvs spam test
	private static final long serialVersionUID = 6078940636791325195L;

	public final static String THEME_NONE = "none";

	public final static String THEME_SUCCESS = "success";

	public final static String THEME_DENIED = "denied";

	public final static String THEME_TOOLTIP = "tooltip";

	public final static String THEME_INFO = "info";

	public final static String THEME_WARNING = "warning";

	public final static String THEME_ERROR = "error";

	private String text;

	private Font font;
	private Color fontColor;
	private Color fontBorderColor;
	private Color borderColor;
	private Color backgroundColor;

	private long timeout = 10000L;

	private int position = SwingConstants.NORTH_WEST;
	/** space outside the box */
	private int paddingX = 50;
	/** space outside the box */
	private int paddingY = 50;
	/** space inside the box */
	private int marginX = 20;
	/** space inside the box */
	private int marginY = 8;

	public OSD() {
		this(THEME_INFO);
	}

	public OSD(String theme) {
		setTheme(theme);
	}

	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}

	public void setPosition(int compass) {
		position = compass;
	}

	public void setPosition(int boxx, int boxy) {
		position = PositionHelper.getCompass(boxx, boxy);
	}

	public void setMargin(int x, int y) {
		marginX = x;
		marginY = y;
	}

	public void setMarginX(int x) {
		marginX = x;
	}

	public void setMarginY(int y) {
		marginY = y;
	}

	public void setPadding(int x, int y) {
		paddingX = x;
		paddingY = y;
	}

	public void setPaddingX(int x) {
		paddingX = x;
	}

	public void setPaddingY(int y) {
		paddingY = y;
	}

	public void setTheme(String theme) {
		if (theme == null || theme.equals(THEME_NONE)) {
			setColors(Color.BLACK, Color.WHITE, Color.WHITE, Color.BLACK);
		} else if (theme.equals(THEME_TOOLTIP)) {
			setColors(Color.WHITE, Color.BLACK, SystemColor.info, Color.BLACK);
		} else if (theme.equals(THEME_SUCCESS)) {
			setColors(Color.WHITE, Color.BLACK, Color.GREEN, Color.BLACK);
		} else if (theme.equals(THEME_DENIED)) {
			setColors(Color.WHITE, Color.BLACK, Color.RED, Color.BLACK);
		} else if (theme.equals(THEME_INFO)) {
			setColors(Color.WHITE, Color.BLACK, Color.GREEN, Color.BLACK);
		} else if (theme.equals(THEME_WARNING)) {
			setColors(Color.WHITE, Color.BLACK, Color.YELLOW, Color.BLACK);
		} else if (theme.equals(THEME_ERROR)) {
			setColors(Color.WHITE, Color.BLACK, Color.RED, Color.BLACK);
		//} else {
		//	assert false;
		}
		setFont(new Font("Sanf", Font.BOLD, 16));
	}

	public void setText(String text) {
		this.text = text;
	}

	public void setFont(Font font) {
		this.font = font;
	}

	private void setColors(Color fontColor, Color fontBorderColor, Color backgroundColor, Color borderColor) {
		setFont(fontColor);
		setFontBorder(fontBorderColor);
		setBackground(backgroundColor);
		setBorder(borderColor);
	}

	public void setFont(Color fontColor) {
		this.fontColor = fontColor;
	}

	public void setFontBorder(Color fontBorderColor) {
		this.fontBorderColor = fontBorderColor;
	}

	public void setBorder(Color borderColor) {
		this.borderColor = borderColor;
	}

	public void setBackground(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	public Window showText(String text) {
		setText(text);
		OSD osd = null;
		try {
			osd = (OSD)this.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		Inner inner = new Inner(osd);
		inner.setVisible(true);
		return inner;
	}

	private static class Inner extends Window {
		/** serialVersionUID */
		private static final long serialVersionUID = -1960685390198037194L;

		/** Cloned information of parent OSD */
		private OSD osd;

		private Rectangle position;
		private Image capturedImage;
		private Image mergedOsdImage;
		private int top, left; // for text

		public Inner(OSD osd) {
			super(new Frame());
			this.osd = osd;
			init();

// FIXME			setAlwaysOnTop(true);

			addMouseListener(new DisposeListener(this) {
				public void mouseClicked(MouseEvent e) {
					dispose();
				}
			});

			final long timeout = osd.timeout;
			new Thread(new Runnable() {
				public void run() {
					try {
						Thread.sleep(timeout);
						setVisible(false);
						dispose();
					} catch (InterruptedException e) {
					}
				}
			}).start();
		}

		public void init() {
			calculateSize();
			capturedImage = getCaptureImage();
			mergedOsdImage = createImage();
		}

		public void paint(Graphics g) {
			g.drawImage(mergedOsdImage, 0, 0, this);
		}

		private Image getCaptureImage() {
			try {
				return new Robot().createScreenCapture(position);
			} catch (AWTException e) {
				return null;
			}
		}

		private void calculateSize() {
			BufferedImage bi = new BufferedImage(1, 1, ColorSpace.TYPE_RGB);
			FontMetrics metrics = bi.getGraphics().getFontMetrics(osd.font);

			int width = metrics.stringWidth(osd.text);
			int height = metrics.getHeight();

			position = PositionHelper.getRectangle(
					osd.position,
					osd.marginX * 2 + width,
					osd.marginY * 2 + height,
					osd.paddingX,
					osd.paddingY);

			setBounds(position);
		}

		private Image createImage() {
			int w = position.width;
			int h = position.height;
			int arc = w < h ? w / 3 : h / 3;
			top = osd.marginY + osd.font.getSize();
			left = osd.marginX;

			BufferedImage bi = new BufferedImage(w, h, ColorSpace.TYPE_RGB);
			Graphics2D g2d = bi.createGraphics();

			g2d.setFont(osd.font);
			g2d.setRenderingHint(
					RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);

			g2d.drawImage(capturedImage, 0, 0, new Color(0, 0, 0, 127), this);

			g2d.setPaint(ColorHelper.getMetallic(osd.backgroundColor, h));
			g2d.fillRoundRect(0, 0, w - 1, h - 1, arc, arc);
			g2d.setPaint(ColorHelper.getMetallic(osd.borderColor, h));
			g2d.drawRoundRect(0, 0, w - 1, h - 1, arc, arc);

//			createText(g2d, Color.WHITE, 20, 1);
//			createText(g2d, osd.fontBorderColor, 1, 255);
//			createText(g2d, osd.fontBorderColor, 2, 0);
			createText(g2d, osd.fontColor, 0, 255);

			return bi;
		}

		private void createText(Graphics2D g2d, Color color, int offset, int alpha) {
			g2d.setColor(ColorHelper.getAlpha(color, alpha));
			for (int ox = -offset; ox <= offset; ox++)
				for (int oy = -offset; oy <= offset; oy++)
					g2d.drawString(osd.text, left + ox, top + oy);
		}

		public void dispose() {
			if (capturedImage != null) capturedImage = null;
			if (mergedOsdImage != null) mergedOsdImage = null;
			super.dispose();
		}
	}
}
