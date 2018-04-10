package de.tarent.commons.logging;

import junit.framework.TestCase;

public class MethodCallTest extends TestCase {
	public void testInit() {

		MethodCall methodCall = new MethodCall();

		assertEquals("de.tarent.commons.logging.MethodCallTest", methodCall.getClassName());
		assertEquals("testInit", methodCall.getMethodName());
	}
}
