package de.tarent.aa.veraweb.utils;

import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;


public class OnlineRegistrationHelperTest extends TestCase {

	private OnlineRegistrationHelper onlineRegistrationHelper;

    @Before
    public void setUp() {
    	onlineRegistrationHelper = new OnlineRegistrationHelper();
    }

    @Test
    public void testGeneratePassword() throws Exception {
        for (int i = 0; i < 1000; i++) {
            String password = onlineRegistrationHelper.generatePassword();
            assertEquals(8, password.length());

            assertTrue(password.matches(OnlineRegistrationHelper.CONDITIONS));
        }
    }
    
    @Test
    public void testGenerateUsernameWithoutNumberAndOneLastnameWithoutUmlauts() throws Exception {
    	// Without umlauts
    	String firstname = "Karin";
    	String lastname = "Schneider";
    	String username = onlineRegistrationHelper.generateUsername(firstname, lastname, null);
    	
    	assertEquals("kschne", username);
    }
    

    @Test
    public void testGenerateUsernameWithoutNumberAndOneLastnameWithUmlauts() throws Exception {
    	// With umlauts
    	String firstname2 = "Hörst";
    	String lastname2 = "Müller";
    	String username2 = onlineRegistrationHelper.generateUsername(firstname2, lastname2, null);
    	
    	assertEquals("hmuell", username2);
    }
    
    @Test
    public void testGenerateUsernameWithoutNumberAndTwoLastname() throws Exception {
    	String firstname = "Javier";
    	String lastname = "Lopez Casas";
    	String username = onlineRegistrationHelper.generateUsername(firstname, lastname, null);
    	
    	assertEquals("jlopez", username);
    }

    @Test
    public void testGenerateUsernameWithoutNumberAndShortNames() throws Exception {
    	String firstname = "Lie";
    	String lastname = "Yin";
    	String username = onlineRegistrationHelper.generateUsername(firstname, lastname, null);
    	
    	assertEquals("lyin", username);
    }
    
    
}