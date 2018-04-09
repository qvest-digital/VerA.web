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

/*
 * Copyright (c) tarent GmbH
 * Bahnhofstrasse 13 . 53123 Bonn
 * www.tarent.de . info@tarent.de
 *
 * Created on 08.12.2005
 */

package de.tarent.commons.ui;

import de.tarent.commons.utils.Config;

import junit.framework.TestCase;

public class SplashDemo extends TestCase
{
    public void testSplashScreen() throws InterruptedException
    {
        Config.parse("src/test/resources/config.xml");

        SplashScreen splash = null;

        splash = new SplashScreen("src/test/resources/gfx/splash-example.png", 500, 346, 5000L);
        splash.setVisible(true);

        Thread.sleep(1500L);

        assertTrue(true);
    }
}
