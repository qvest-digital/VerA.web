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
