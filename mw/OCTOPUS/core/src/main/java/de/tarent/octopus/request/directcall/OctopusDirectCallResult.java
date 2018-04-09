package de.tarent.octopus.request.directcall;

/*-
 * tarent-octopus, Webservice Data Integrator and Application Server
 * Copyright © 2002–2018 tarent solutions GmbH and its contributors
 * Copyright © 2015, 2017, 2018 mirabilos (t.glaser@tarent.de)
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

import de.tarent.octopus.client.OctopusResult;

import java.io.*;
import java.util.Iterator;

/**
 * Kapselung der Antwort auf eine Octopus Anfrage
 *
 * @author <a href="mailto:sebastian@tarent.de">Sebastian Mancke</a>, <b>tarent GmbH</b>
 */
public class OctopusDirectCallResult implements OctopusResult {
	TcDirectCallResponse response;

	public OctopusDirectCallResult(TcDirectCallResponse response) {
		this.response = response;
	}

	public boolean errorWhileProcessing() {
		return response.errorWhileProcessing();
	}

	public String getErrorMessage() {
		return response.getErrorMessage();
	}

	public Exception getErrorException() {
		return response.getErrorException();
	}

	public String getContentType() {
		return response.getContentType();
	}

	public boolean hasStreamContent() {
		return ((ByteArrayOutputStream)response.getOutputStream()).size() > 0;
	}

	public InputStream getContent() {
		return new ByteArrayInputStream(((ByteArrayOutputStream)response.getOutputStream()).toByteArray());
	}

	public void writeContent(OutputStream to)
	    throws IOException {
		((ByteArrayOutputStream)response.getOutputStream()).writeTo(to);
		to.flush();
	}

	public boolean hasMoreData() {
		return response.hasMoreResponseObjects();
	}

	public Object nextData() {
		if (!hasMoreData())
			return null;
		return response.readNextResponseObject();
	}

	public Iterator getDataKeys() {
		return response.getResponseObjectKeys();
	}

	public Object getData(String key) {
		return response.getResponseObject(key);
	}

	public Object nextDataAs(Class type) {
		if (!hasMoreData())
			return null;
		Object o = response.readNextResponseObject();
		if (!type.isInstance(o))
			throw new ClassCastException("Can not cast <" + o.getClass().getName() +
			    "> to <" + type.getClass().getName() + ">.");
		return o;
	}

	public String nextDataAsString() {
		return (String)nextData();
	}

	public int nextDataAsInt() {
		if (!hasMoreData())
			return -1;
		Object o = response.readNextResponseObject();
		if (o instanceof Integer)
			return ((Integer)o).intValue();

		try {
			return Integer.parseInt("" + o);
		} catch (NumberFormatException e) {
			throw new ClassCastException("Can not parse int from <" + o + ">.");
		}
	}

	public float nextDataAsFloat() {
		if (!hasMoreData())
			return -1f;
		Object o = response.readNextResponseObject();
		if (o instanceof Float)
			return ((Float)o).floatValue();

		try {
			return Float.parseFloat("" + o);
		} catch (NumberFormatException e) {
			throw new ClassCastException("Can not parse float from <" + o + ">.");
		}
	}

	public byte[] nextDataAsByteArray() {
		return (byte[])nextData();
	}
}
