package de.tarent.commons.logging;

/*-
 * VerA.web-Middleware, newly MIT licenced, is comprised of:
 * tarent-commons, a set of common components and solutions
 * tarent-contact, platform-independent webservice-based contact management
 * tarent-database, jdbc database library
 * tarent-doctor, Document Generation Platform
 * tarent-octopus, Webservice Data Integrator and Application Server
 *  © 2005, 2006, 2007 asteban (s.mancke@tarent.de)
 *  © 2007 David Goemans (d.goemans@tarent.de)
 *  © 2006, 2007, 2010 Hendrik Helwich (h.helwich@tarent.de)
 *  © 2005, 2006, 2007 Christoph Jerolimov (c.jerolimov@tarent.de)
 *  © 2006 Philipp Kirchner (p.kirchner@tarent.de)
 *  © 2010 Carsten Klein (c.klein@tarent.de)
 *  © 2006 Michael Kleinhenz (m.kleinhenz@tarent.de)
 *  © 2006 Michael Klink (m.klink@tarent.de)
 *  © 2007 Fabian Köster (f.koester@tarent.de)
 *  © 2006 Martin Ley (m.ley@tarent.de)
 *  © 2007 Alex Maier (a.maier@tarent.de)
 *  © 2007, 2015, 2017, 2018 mirabilos (t.glaser@tarent.de)
 *  © 2006, 2007 Jens Neumaier (j.neumaier@tarent.de)
 *  © 2006 Nils Neumaier (n.neumaier@tarent.de)
 *  © 2007, 2008 Martin Pelzer (m.pelzer@tarent.de)
 *  © 2008, 2009 Christian Preilowski (c.thiel@tarent.de)
 *  © 2006, 2008, 2009 Thomas Schmitz (t.schmitz@tarent.de)
 *  © 2007 Robert Schuster (r.schuster@tarent.de)
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

import org.apache.commons.logging.Log;

/**
 * Simple Tool for time measurement to the logging system.
 * This tool logs to "info" if the SystemProperty <code>enableTimeMeasureTool</code< is set.
 * If the SystemProperty is not set, if does not log anyway.
 */
public class TimeMeasureTool {

    static boolean doLogging = (System.getProperty("enableTimeMeasureTool") != null &&
      !"false".equalsIgnoreCase(System.getProperty("enableTimeMeasureTool")));

    private static final Log defaultLogger = LogFactory.getLog(TimeMeasureTool.class);
    private static final TimeMeasureTool NOP = new TimeMeasureTool();
    Log log;
    String level;
    long start;
    long last;
    int steps = 0;

    /**
     * Construcs a TimeMeasureTool with the default logger
     */
    protected TimeMeasureTool() {
        this(defaultLogger);
    }

    /**
     * Construcs a TimeMeasureTool with the supplied logger
     */
    protected TimeMeasureTool(Log logger) {
        this.start = System.currentTimeMillis();
        this.last = start;
        this.log = logger;
    }

    /**
     * Returns a measurement tool for the supplied logger and level.
     * If the logger should not log on this level, this returns a nop object.
     */
    public static TimeMeasureTool getMeasureTool(Log logger) {
        if (doLogging) {
            return new TimeMeasureTool(logger);
        }
        return NOP;
    }

    /**
     * Logs the time since the last step or the beginning.
     */
    public void step(String name) {
        if (!doLogging) {
            return;
        }
        long time = System.currentTimeMillis() - last;
        last = System.currentTimeMillis();
        log.info(name + " took " + time + "ms");
        steps++;
    }

    /**
     * Logs the time since the beginning.
     */
    public void total(String name) {
        if (!doLogging) {
            return;
        }
        steps++;
        long total = System.currentTimeMillis() - start;
        log.info(name + " took " + (total) + "ms" + " average " + (total / steps) + "ms per step");
    }
}