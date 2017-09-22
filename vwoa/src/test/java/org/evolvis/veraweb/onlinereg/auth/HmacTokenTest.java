package org.evolvis.veraweb.onlinereg.auth;

/*-
 * veraweb, platform independent webservice-based event management
 * (Veranstaltungsmanagment VerA.web), is
 * Copyright © 2004–2008 tarent GmbH
 * Copyright © 2013–2015 tarent solutions GmbH
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see: http://www.gnu.org/licenses/
 */
import com.sun.jersey.core.util.Base64;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

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
