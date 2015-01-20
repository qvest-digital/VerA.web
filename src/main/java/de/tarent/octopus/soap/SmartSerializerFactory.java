/**
 * tarent-octopus, Webservice Data Integrator and Application Server
 * Copyright © 2002–2015 tarent solutions GmbH and its contributors
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
package de.tarent.octopus.soap;

import javax.xml.rpc.encoding.Serializer;
import org.apache.axis.encoding.SerializerFactory;
import java.util.*;
import org.apache.axis.Constants;

public class SmartSerializerFactory implements SerializerFactory {

    /** serialVersionUID */
	private static final long serialVersionUID = -2476137120473428328L;

	List mechanisms;

    public SmartSerializerFactory() {
        mechanisms = new ArrayList(1);
        mechanisms.add(Constants.AXIS_SAX);
    }
    
    public Serializer getSerializerAs(String mechanismType) {
        return new SmartSerializer();
    }
    
    public Iterator getSupportedMechanismTypes() {
        return mechanisms.iterator();
    }
}