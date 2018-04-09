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
