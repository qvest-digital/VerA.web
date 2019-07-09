package de.tarent.commons.messages;
import junit.framework.TestCase;

public class MessageTest extends TestCase {
    public static String TEST_A;

    public static Message TEST_B;

    public static Message NOT_FOUND;

    public void testMessages() {
        MessageHelper.init();

        assertTrue(TEST_A.startsWith("Test A"));
        assertTrue(TEST_B.getPlainMessage().startsWith("Test B"));
        assertTrue(NOT_FOUND.getPlainMessage().startsWith("No message"));
    }
}
