/**
 *
 */
package de.tarent.commons.web;

import java.io.UnsupportedEncodingException;

import junit.framework.TestCase;

/**
 * @author tim
 *
 */
public class ParametersProcessorTest extends TestCase {

	private ParametersProcessor proc;
	private Parameters params1;
	private Parameters params2;

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		proc = new ParametersProcessor();
		params1 = new Parameters();
		params1.put("a", "b");
		params2 = new Parameters();
		params2.put("c", "d");
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * Test method for {@link de.tarent.sbk.utils.ParametersProcessor#encodeUrl(java.lang.String, de.tarent.commons.web.Parameters)}.
	 * @throws UnsupportedEncodingException
	 */
	public void testEncodeUrl() throws UnsupportedEncodingException {
		assertEquals("Problems with null values", "test", proc.encodeUrl("test", null));
		assertEquals("Problems with null string", "", proc.encodeUrl(null, null));
		proc.addCommonParameters(null);
		assertEquals("Problems with null common parameters", "test", proc.encodeUrl("test", null));
		proc.addCommonParameters(new Parameters());
		assertEquals("Problems with empty maps", "test", proc.encodeUrl("test", new Parameters()));
		assertEquals("Problems with common parameters", "test?c=d", proc.encodeUrl("test", params2));
		proc.addCommonParameters(params1);
		assertEquals("Problems with common parameters", "test?a=b", proc.encodeUrl("test", null));
	}

}
