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

import javax.swing.SwingConstants;

import junit.framework.TestCase;

public class OSDDemo extends TestCase {
    public void testPositions() throws Exception {
        if (true) {
            return;
        }

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
        if (false) {
            return;
        }

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
