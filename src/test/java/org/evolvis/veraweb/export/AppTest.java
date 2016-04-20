package org.evolvis.veraweb.export;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by mweier on 20.04.16.
 */
public class AppTest{

    @Test
    public void testPrint(){
        App app = new App(" Hello World!  ");

        assertEquals("Hello World!", app.print());
    }
}
