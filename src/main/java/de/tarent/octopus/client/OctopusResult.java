package de.tarent.octopus.client;

/*
 * tarent-octopus, Webservice Data Integrator and Application Server
 * Copyright © 2002–2017 tarent solutions GmbH and its contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.util.Iterator;

/** 
 * Aufruf eines Task des Octopus.
 *
 * @author <a href="mailto:sebastian@tarent.de">Sebastian Mancke</a>, <b>tarent GmbH</b>
 */
public interface OctopusResult {

    public boolean hasStreamContent();
    public InputStream getContent();
    public String getContentType();
    public void writeContent(OutputStream to)
        throws IOException;


    public boolean hasMoreData();
    public Iterator getDataKeys();
    public Object getData(String key);
    public Object nextData();

//     /**
//      * @throws java.lang.ClassCastException
//      */
//     public Object nextDataAs(Class type);

    /**
     * @throws java.lang.ClassCastException
     */
    public String nextDataAsString();

//     /**
//      * @throws java.lang.ClassCastException
//      */
//     public int nextDataAsInt();

//     /**
//      * @throws java.lang.ClassCastException
//      */
//     public float nextDataAsFloat();

//     /**
//      * @throws java.lang.ClassCastException
//      */
//     public byte[] nextDataAsByteArray();
}
