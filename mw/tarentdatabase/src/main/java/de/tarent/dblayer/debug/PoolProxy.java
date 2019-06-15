package de.tarent.dblayer.debug;

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

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.logging.Log;

import de.tarent.dblayer.engine.Pool;

/**
 * <p>
 * This pooling proxy start a new thread per new database connection.
 * It checks if the connection is alive ot not every 100 miliseconds
 * the first 10 seconds, after that every second.
 * It shows the status message on the standard output. Every five
 * seconds it also print out a full stacktrace of the moment where
 * this connection is opened.
 * </p>
 *
 * <p>
 * <strong>Note:</strong>
 * Use this only for debugging, not for logging. Its really slow.
 * </p>
 *
 * @author Christoph Jerolimov, tarent GmbH
 */
import lombok.extern.log4j.Log4j2;@Log4j2
public class PoolProxy implements Pool {
    /**
     * Delegate instance
     */
    private Pool delegate;

    /**
     * Connection counter for all connections
     */
    private long allConnectionCount = 0L;

    /**
     * Connection counter for opened connections
     */
    private long inUseConnectionCount = 0L;

    public PoolProxy(Pool delegate) {
        this.delegate = delegate;
    }

    public String getTargetDB() {
        return delegate.getTargetDB();
    }

    public void init() {
        delegate.init();
    }

    public void close() {
        delegate.close();
    }

    public Connection getConnection() throws SQLException {
        // Debug informations
        final long currentCC = incrementConnectionCount();
        final long currentTime = System.currentTimeMillis();
        final Connection con = delegate.getConnection();
        final StackTraceElement trace[] = new Exception().getStackTrace();

        // Start watching thread.
        new Thread(new Runnable() {
            public void run() {
                long sleep = 0L;
                try {
                    while (sleep == 0L || !con.isClosed()) {
                        print("opened for " + (System.currentTimeMillis() - currentTime) + "ms.",
                          ((sleep % 5000L) == 0L) ? trace : null);
                        Thread.sleep(sleep);
                        sleep += (sleep < 10000L) ? 100L : 1000L;
                    }
                    decrementConnectionCount();
                    print("CLOSED after " + (System.currentTimeMillis() - currentTime) + "ms.", null);
                } catch (Exception e) {
                    decrementConnectionCount();
                    logger.warn("Error while watching a database connection.", e);
                }
            }

            private void print(String message, StackTraceElement trace[]) {
                StringBuffer buffer = new StringBuffer(500);
                buffer.append("Connection #").append(currentCC).append(": ");
                buffer.append(message);
                buffer.append("\t[Open: ").append(inUseConnectionCount).append("]\n");
                if (trace != null) {
                    for (int i = 0; i < trace.length; i++) {
                        buffer.append("\tat ").append(trace[i]).append("\n");
                    }
                }
                PoolProxy.print(buffer.toString());
            }
        }).start();

        // Return delegate connection.
        return con;
    }

    synchronized private long incrementConnectionCount() {
        inUseConnectionCount++;
        return allConnectionCount++;
    }

    synchronized private void decrementConnectionCount() {
        inUseConnectionCount--;
    }

    synchronized static private void print(String message) {
        System.out.print(message);
    }
}
