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

import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;

import javax.swing.SwingConstants;

public class PositionHelper implements SwingConstants {
	private PositionHelper() {
		// no instance are allowed.
	}

	public static int getCompass(int boxx, int boxy) {
		if (boxy == TOP && boxy == LEFT) return NORTH_WEST;
		else if (boxy == TOP && boxy == CENTER) return NORTH;
		else if (boxy == TOP && boxy == RIGHT) return NORTH_EAST;
		else if (boxy == CENTER && boxy == TOP) return WEST;
		else if (boxy == CENTER && boxy == CENTER) return CENTER;
		else if (boxy == CENTER && boxy == RIGHT) return EAST;
		else if (boxy == BOTTOM && boxy == LEFT) return SOUTH_WEST;
		else if (boxy == BOTTOM && boxy == CENTER) return SOUTH;
		else if (boxy == BOTTOM && boxy == RIGHT) return SOUTH_EAST;
		else throw new IllegalArgumentException("Illegal compass: " + boxx + "x" + boxy);
	}

	public static int getBoxX(int compass) {
		if (compass == NORTH_WEST || compass == WEST || compass == SOUTH_WEST)
			return LEFT;
		else if (compass == NORTH || compass == CENTER || compass == SOUTH)
			return CENTER;
		else if (compass == NORTH_EAST || compass == EAST || compass == SOUTH_EAST)
			return RIGHT;
		else
			throw new IllegalArgumentException("Illegal compass: " + compass);
	}

	public static int getBoxY(int compass) {
		if (compass == NORTH_WEST || compass == NORTH || compass == NORTH_EAST)
			return TOP;
		else if (compass == WEST || compass == CENTER || compass == EAST)
			return CENTER;
		else if (compass == SOUTH_WEST || compass == SOUTH || compass == SOUTH_EAST)
			return BOTTOM;
		else
			throw new IllegalArgumentException("Illegal compass: " + compass);
	}

	public static Rectangle getRectangle(int compass, int width, int height) {
		return getRectangle(getBoxX(compass), getBoxY(compass), width, height, 0, 0);
	}

	public static Rectangle getRectangle(int compass, int width, int height, int paddingX, int paddingY) {
		return getRectangle(getBoxX(compass), getBoxY(compass), width, height, paddingX, paddingY);
	}

	public static Rectangle getRectangle(int boxx, int boxy, int width, int height) {
		return getRectangle(boxx, boxy, width, height, 0, 0);
	}

	public static Rectangle getRectangle(int boxx, int boxy, int width, int height, int paddingX, int paddingY) {
		Rectangle s = getScreen();
		int x;
		int y;

		if (boxx == LEFT)
			x = s.x + paddingX;
		else if (boxx == CENTER)
			x = s.x + s.width / 2 - width / 2 /*+ paddingX*/;
		else if (boxx == RIGHT)
			x = s.x + s.width - width - paddingX;
		else
			throw new IllegalArgumentException("Illegal compass: " + boxx);

		if (boxy == TOP)
			y = s.y + paddingY;
		else if (boxy == CENTER)
			y = s.y + s.height / 2 - height / 2 /*+ paddingY*/;
		else if (boxy == BOTTOM)
			y = s.y + s.height - height - paddingY;
		else
			throw new IllegalArgumentException("Illegal compass: " + boxy);

		return new Rectangle(x, y, width, height);
	}

	private static Rectangle getScreen() {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		return ge.getMaximumWindowBounds();
//		GraphicsDevice gd = ge.getDefaultScreenDevice();
//		GraphicsConfiguration gc = gd.getDefaultConfiguration();
//		return gc.getBounds();
	}
}
