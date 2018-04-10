package de.tarent.octopus.request.directcall;
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
