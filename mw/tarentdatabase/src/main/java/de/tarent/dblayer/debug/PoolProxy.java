package de.tarent.dblayer.debug;
import de.tarent.dblayer.engine.Pool;
import lombok.extern.log4j.Log4j2;

import java.sql.Connection;
import java.sql.SQLException;

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
@Log4j2
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
