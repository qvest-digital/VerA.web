package de.tarent.commons.web;
import java.text.ParseException;

import junit.framework.TestCase;

/**
 * @author tim
 */
public class WebFormatTest extends TestCase {

    WebFormat wf;

    /**
     * @param arg0
     */
    public WebFormatTest(String arg0) {
        super(arg0);
    }

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        wf = new WebFormat();
    }

    /* (non-Javadoc)
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test method for {@link de.tarent.commons.web.WebFormat#brokerDateToHumanDate(java.lang.String)}.
     *
     * @throws ParseException
     */
    public void testBrokerDateToHumanDate() throws ParseException {
        assertEquals("04.05.2006", wf.brokerDateToHumanDate("2006-05-04 00:00:00.0"));
    }
}
