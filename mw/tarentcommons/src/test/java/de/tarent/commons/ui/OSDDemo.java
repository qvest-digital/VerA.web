package de.tarent.commons.ui;

import java.awt.Color;

import javax.swing.SwingConstants;

import junit.framework.TestCase;

public class OSDDemo extends TestCase {
	public void testPositions() throws Exception {
		if (true) return;

		int timeout = 10000;

		OSD osd = new OSD();
		osd.setTimeout(timeout);

		osd.setPosition(SwingConstants.NORTH_WEST);
		osd.showText("NORTH_WEST");
		osd.setPosition(SwingConstants.NORTH);
		osd.showText("NORTH");
		osd.setPosition(SwingConstants.NORTH_EAST);
		osd.showText("NORTH_EAST");

		osd.setPosition(SwingConstants.WEST);
		osd.showText("WEST");
		osd.setPosition(SwingConstants.CENTER);
		osd.showText("CENTER");
		osd.setPosition(SwingConstants.EAST);
		osd.showText("EAST");

		osd.setPosition(SwingConstants.SOUTH_WEST);
		osd.showText("SOUTH_EAST");
		osd.setPosition(SwingConstants.SOUTH);
		osd.showText("SOUTH");
		osd.setPosition(SwingConstants.SOUTH_EAST);
		osd.showText("SOUTH_EAST");

		Thread.sleep(timeout + 1000);

		System.exit(0);
	}

	public void testShowTest() throws Exception {
		if (false) return;

		int timeout = 5000;

		OSD osd = new OSD();
		osd.setTimeout(timeout);

		osd.setTheme(OSD.THEME_NONE);
		osd.setPadding(50, 50);
		osd.showText("None theme.");

		osd.setTheme(OSD.THEME_TOOLTIP);
		osd.setPadding(50, 100);
		osd.showText("System tooltip theme.");

		osd.setTheme(OSD.THEME_SUCCESS);
		osd.setPadding(50, 200);
		osd.showText("Access success theme.");

		osd.setTheme(OSD.THEME_DENIED);
		osd.setPadding(50, 250);
		osd.showText("Access denied theme.");

		osd.setTheme(OSD.THEME_INFO);
		osd.setPadding(400, 50);
		osd.showText("Information theme.");

		osd.setTheme(OSD.THEME_WARNING);
		osd.setPadding(400, 100);
		osd.showText("Warning theme.");

		osd.setTheme(OSD.THEME_ERROR);
		osd.setPadding(400, 150);
		osd.showText("Error theme!");

		osd.setTheme(OSD.THEME_ERROR);
		osd.setBackground(Color.BLUE);
		osd.setPadding(650, 50);
		osd.showText("Blue");

		osd.setTheme(OSD.THEME_ERROR);
		osd.setBackground(Color.ORANGE);
		osd.setPadding(650, 100);
		osd.showText("Orange");

		Thread.sleep(timeout + 2000);
	}
}
