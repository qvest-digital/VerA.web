package de.tarent.aa.veraweb.utils;

import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;


public class OnlineRegistrationHelperTest extends TestCase {

    /**
     * At least one digit
     * At leas one upper case letter
     * At least one special character
     */
    private static final String CONDITIONS = ".*(?=.*\\d)(?=.*[A-Z])(?=.*[-_$!#<>@&()+=}]).*";

    @Before
    public void setUp() {
    }

    @Test
    public void testGeneratePassword() throws Exception {
        for (int i = 0; i < 1000; i++) {
            String password = OnlineRegistrationHelper.generatePassword();
            assertEquals(8, password.length());

            assertTrue(password.matches(CONDITIONS));
        }
    }
    
    @Test
    public void testNullvaluesUsernameGenerator() throws Exception {
    	String username = OnlineRegistrationHelper.generateUsername(null, null, null);
    	assertNull(username);
    }

    @Test
    public void testNullFirstname() throws Exception {
    	String lastname = "Reuter";
    	String username = OnlineRegistrationHelper.generateUsername(null, lastname, null);
    	assertNull(username);
    }
    
    @Test
    public void testNullLastname() throws Exception {
    	String firstname = "Karen";
    	String username = OnlineRegistrationHelper.generateUsername(firstname, null, null);
    	assertNull(username);
    }
    
    @Test
    public void testGenerateUsernameWithoutNumberAndOneLastname() throws Exception {
    	// Without umlauts
    	String firstname = "Karin";
    	String lastname = "Schneider";
    	String username = OnlineRegistrationHelper.generateUsername(firstname, lastname, null);
    	
    	assertEquals("kschne", username);
    	
    	// With umlauts
    	String firstname2 = "Hörst";
    	String lastname2 = "Müller";
    	String username2 = OnlineRegistrationHelper.generateUsername(firstname2, lastname2, null);
    	
    	assertEquals("hmülle", username2);
    }
    
    @Test
    public void testGenerateUsernameWithoutNumberAndTwoLastname() throws Exception {
    	String firstname = "Javier";
    	String lastname = "Lopez Casas";
    	String username = OnlineRegistrationHelper.generateUsername(firstname, lastname, null);
    	
    	assertEquals("jlopez", username);
    }

    @Test
    public void testGenerateUsernameWithoutNumberAndShortNames() throws Exception {
    	String firstname = "Lie";
    	String lastname = "Yin";
    	String username = OnlineRegistrationHelper.generateUsername(firstname, lastname, null);
    	
    	assertEquals("lyin", username);
    }
    
    
}