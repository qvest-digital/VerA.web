package de.tarent.octopus.util;

import junit.framework.TestCase;
import org.apache.axis.server.AxisServer;
import org.apache.commons.discovery.tools.DiscoverSingleton;
import org.apache.commons.logging.LogFactory;

import java.security.AccessController;
import java.security.PrivilegedAction;

public class AxisLoggingTest extends TestCase {
    @SuppressWarnings("unchecked")
    private static LogFactory getLogFactory() {
        return (LogFactory)
          AccessController.doPrivileged(
            new PrivilegedAction() {
                public Object run() {
                    return DiscoverSingleton.find(LogFactory.class,
                      LogFactory.FACTORY_PROPERTIES,
                      LogFactory.FACTORY_DEFAULT);
                }
            });
    }

    public void test() {
        final LogFactory lf = getLogFactory();
        assertNotNull(lf);
        assertNotNull(new org.apache.axis.components.logger.LogFactory());
        assertNotNull(new AxisServer());
    }
}
