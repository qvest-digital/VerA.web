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

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Paint;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * "red alpha 20"
 *
 * @author Christoph Jerolimov
 */
public class ColorHelper {
    private ColorHelper() {
        // no instance are allowed.
    }

    private static final Map hue;

    static {
        //		Color.YELLOW;
        //		Color.CYAN;
        //		Color.GREEN;
        //		Color.BLUE;
        //		Color.RED;
        //		Color.MAGENTA;

        Map colors = new HashMap();
        colors.put("red", new Integer(0));
        colors.put("orange", new Integer(30));
        colors.put("yellow", new Integer(60));
        colors.put("neongreen", new Integer(90));
        colors.put("green", new Integer(120));
        colors.put("neonblue", new Integer(180));
        colors.put("lightblue", new Integer(210));
        colors.put("blue", new Integer(240));
        colors.put("magenta", new Integer(300));
        hue = Collections.unmodifiableMap(colors);
    }

    public static Color getColor(String string) {
        Color color = (Color) hue.get(string.toLowerCase());
        if (color != null) {
            return color;
        }

        //		String params[] = string.split(" ");

        return null;
    }

    public static int getHue(String color) {
        return ((Integer) hue.get(color.toLowerCase())).intValue();
    }

    public static Paint getMetallic(Color color, int h) {
        return new GradientPaint(
                0, 0, getAlpha(getLightMetallic(color), 191),
                0, h, getAlpha(getDarkMetallic(color), 191));
    }

    public static Color getLightMetallic(Color color) {
        float hsb[] = getHSB(color);
        hsb[1] = (float) (hsb[1] * 0.60);
        return getColor(hsb);
    }

    public static Color getDarkMetallic(Color color) {
        float hsb[] = getHSB(color);
        hsb[1] = (float) (hsb[1] * 0.80);
        hsb[2] = (float) (hsb[2] * 0.80);
        return Color.getHSBColor(hsb[0], hsb[1], hsb[2]);
    }

    public static Color getAlpha(Color color, int alpha) {
        if (color.getAlpha() == alpha) {
            return color;
        }
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
    }

    private static float[] getHSB(Color color) {
        return Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
    }

    private static Color getColor(float[] hsb) {
        if (hsb[0] < 0) {
            hsb[0] = 0;
        } else if (hsb[0] > 1) {
            hsb[0] = 1;
        }
        if (hsb[1] < 0) {
            hsb[1] = 0;
        } else if (hsb[1] > 1) {
            hsb[1] = 1;
        }
        if (hsb[2] < 0) {
            hsb[2] = 0;
        } else if (hsb[2] > 1) {
            hsb[2] = 1;
        }
        return Color.getHSBColor(hsb[0], hsb[1], hsb[2]);
    }

    public static Paint getMetallic(String color, int height) {
        return getMetallic(color, 0, 0, 0, height);
    }

    public static Paint getMetallic(String color, int x1, int y1, int x2, int y2) {
        return new GradientPaint(
                x1, y1, getLightMetallicColor(color),
                x2, y2, getDarkMetallicColor(color));
    }

    public static Color getLightMetallicColor(String color) {
        return new Color(gimpHSVtoRGB(getHue(color), 50, 100));
    }

    public static Color getDarkMetallicColor(String color) {
        return new Color(gimpHSVtoRGB(getHue(color), 0, 95));
    }

    private static int gimpHSVtoRGB(float h, float s, float v) {
        return Color.HSBtoRGB(h / 360, s / 100, v / 100);
    }
}
