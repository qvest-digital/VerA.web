package de.tarent.commons.utils;
import de.tarent.commons.utils.StringTools;
import junit.framework.TestCase;

public class StringToolsTest extends TestCase {

    /*
     * Test method for 'de.tarent.commons.utils.StringTools.capitalizeFirstLetter(String)'
     */
    public void testCapitalizeFirstLetter() {
        assertEquals("For null, null should be returned!", null, StringTools.capitalizeFirstLetter(null));
        assertEquals("For '', '' should be returned!", "", StringTools.capitalizeFirstLetter(""));
        assertEquals("Wrong return value!", "A", StringTools.capitalizeFirstLetter("a"));
        assertEquals("Wrong return value!", "A", StringTools.capitalizeFirstLetter("A"));
        assertEquals("Wrong return value!", "Ab", StringTools.capitalizeFirstLetter("ab"));
        assertEquals("Wrong return value!", "Ab", StringTools.capitalizeFirstLetter("Ab"));
    }

    /*
     * Test method for 'de.tarent.commons.utils.StringTools.minusculizeFirstLetter(String)'
     */
    public void testMinusculizeFirstLetter() {
        assertEquals("For null, null should be returned!", null, StringTools.capitalizeFirstLetter(null));
        assertEquals("For '', '' should be returned!", "", StringTools.minusculizeFirstLetter(""));
        assertEquals("Wrong return value!", "a", StringTools.minusculizeFirstLetter("a"));
        assertEquals("Wrong return value!", "a", StringTools.minusculizeFirstLetter("A"));
        assertEquals("Wrong return value!", "ab", StringTools.minusculizeFirstLetter("ab"));
        assertEquals("Wrong return value!", "ab", StringTools.minusculizeFirstLetter("Ab"));
    }
}
