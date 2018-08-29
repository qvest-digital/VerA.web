package de.tarent.commons.logging;

/*-
 * VerA.web-Middleware, newly MIT licenced, is comprised of:
 * tarent-commons, a set of common components and solutions
 * tarent-contact, platform-independent webservice-based contact management
 * tarent-database, jdbc database library
 * tarent-doctor, Document Generation Platform
 * tarent-octopus, Webservice Data Integrator and Application Server
 *  © 2018 Атанас Александров (sirakov@gmail.com)
 *  © 2005, 2006, 2007 asteban (s.mancke@tarent.de)
 *  © 2018 Dominik George (d.george@tarent.de)
 *  © 2007 David Goemans (d.goemans@tarent.de)
 *  © 2018 Christian Gorski (c.gorski@tarent.de)
 *  © 2006, 2007, 2010 Hendrik Helwich (h.helwich@tarent.de)
 *  © 2018 Benedict Hoeger (b.hoeger@tarent.de)
 *  © 2005, 2006, 2007 Christoph Jerolimov (c.jerolimov@tarent.de)
 *  © 2018 Timo Kanera (t.kanera@tarent.de)
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

import java.io.PrintWriter;
import java.util.logging.LogRecord;

import de.tarent.commons.utils.StringTools;
import de.tarent.commons.utils.Tools;

public class LogFormatter {

    public void format(PrintWriter writer, LogRecord record) {
        //assert writer != null;
        try {
            String threadName = Thread.currentThread().getName();

            // Loglevel and thread information
            writer.println();
            writer.println("  " + record.getLevel() + "   (in thread: " + record.getThreadID() + ", " + threadName + ")");
            writer.println();

            // Logged in class/method
            writer.println("  Logged in (class and methodname):");
            writer.println();
            writer.println("    " + record.getSourceClassName() + "#" + record.getSourceMethodName());
            writer.println();

            // Logged by category
            writer.println("  Logger by (logger/category name):");
            writer.println();
            writer.println("    " + record.getLoggerName());
            writer.println();

            // Logmessage
            if ((-1 != record.getMessage().indexOf("\n")) || (-1 != record.getMessage().indexOf("\r"))) {
                writer.println();
                writer.println("  Multiline log message:");
                writer.println();
                writer.println("  >>>" + record.getMessage() + "<<<");
                writer.println();
                writer.println();
            } else {
                writer.println();
                writer.println("  Log message:");
                writer.println();
                writer.println("    " + record.getMessage());
                writer.println();
                writer.println();
            }

            // Threadlogger
            if (ThreadLogger.isInstanceAvailable()) {
                ThreadLogger threadLogger = ThreadLogger.getInstance();
                writer.println("  Available method calls of current threadlogger (" + threadLogger.getThreadId() + "):");
                writer.println();
                writer.println(
                  Tools.iteratorToString(threadLogger.getMethodCalls().iterator(), "    ", true, StringTools.LINE_SEPARATOR,
                    true));
                writer.println();
            }

            // Stacktrace
            if (record.getThrown() != null) {
                writer.println("  Available Stacktrace of this exception:");
                writer.println();
                format(writer, record.getThrown());
            }

            writer.println();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void format(PrintWriter writer, Throwable throwable) {
        format(writer, throwable, 0);
    }

    protected void format(PrintWriter writer, Throwable throwable, int ignoreLines) {
        writer.print("    ");
        if (ignoreLines != 0) {
            writer.print("Caused by: ");
        }
        writer.println(throwable);

        int x = 0;

        StackTraceElement ste[] = throwable.getStackTrace();
        for (int i = 0; i < ste.length; i++) {
            if (x < (ste.length - ignoreLines)) {
                writer.println("        at " + ste[i]);
            }
            x++;
        }

        Throwable cause = throwable.getCause();
        if (cause != null) {
            format(writer, cause, x - 1);
        }
    }
}
