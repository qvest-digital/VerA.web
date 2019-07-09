package org.evolvis.veraweb;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogConfigurationException;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.impl.LogFactoryImpl;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Tests for the commons-logging 1.2 stub from OCTOPUS-base
 *
 * @author mirabilos (t.glaser@tarent.de)
 */
public class CommonsLoggingStubTest {
    @Test
    public void test() {
        final Throwable c = new StringIndexOutOfBoundsException();
        final Throwable e = new LogConfigurationException(c);
        assertEquals(c, e.getCause());
        final LogFactory f = LogFactory.getFactory();
        // meh, inspect those manually
        final Log lC = LogFactory.getLog(CommonsLoggingStubTest.class);
        lC.info("successful log via class", e);
        final Log lS = LogFactory.getLog("miau");
        lS.info("meow");
        final Log lI = f.getInstance(org.springframework.jdbc.core.JdbcTemplate.class);
        lI.info("this should not have warned after the meow above");
        f.setAttribute(null, f);
        assertNull(f.getAttribute(null));
        assertNotNull(f.getAttributeNames());
        assertEquals(0, f.getAttributeNames().length);
        f.removeAttribute(null);
        f.release();
        assertNotEquals(f, new LogFactoryImpl());
        assertEquals(f, LogFactory.getFactory());
        LogFactory.release(null);
        LogFactory.releaseAll();
    }
}
