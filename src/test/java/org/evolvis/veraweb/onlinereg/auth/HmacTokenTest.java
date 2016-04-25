package org.evolvis.veraweb.onlinereg.auth;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.sun.jersey.core.util.Base64;

public class HmacTokenTest {

    @Test
    public void roundTrip() throws InvalidTokenException {

        long now = System.currentTimeMillis();
        String base64String = new HmacToken("fooBarBaz", now).toString();
        assertEquals(now,new HmacToken(base64String).getTimestamp());
        assertEquals("fooBarBaz",new HmacToken(base64String).getOsiamAccessToken());
        assertEquals(base64String,new HmacToken(base64String).toString());
    }

    @Test
    public void detectTempering() {
        byte[] token  = Base64.decode(new HmacToken("foo", 42l).toString());
        token[token.length-3] = 'b';
        token[token.length-2] = 'a';
        token[token.length-1] = 'r';
        try {
            new HmacToken(token);
            fail("should detect tempered token");
        } catch (InvalidTokenException e) {
            ;
        }
    }
    
    @Test
    public void checkMinLength(){
        // MAC + Timestamp are exactly 28 bytes. 
        // Both should fail early since the message (the osiam access token) cannot be empty.
        try{
            new HmacToken(new byte[28]);
        } catch(InvalidTokenException e){
            ;
        }

        try{
            new HmacToken(new byte[27]);
        } catch(InvalidTokenException e){
            ;
        }
    }
}
