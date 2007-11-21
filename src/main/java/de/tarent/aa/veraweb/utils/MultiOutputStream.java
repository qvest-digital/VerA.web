package de.tarent.aa.veraweb.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Dieser {@link OutputStream} bildet einen einzelnen
 * OutputStream auf mehrere Instanzen ab.
 */
public class MultiOutputStream extends OutputStream {
	/** Anzahl der Streams */
	private int size;
	/** Stream Instanzen */
	private List streams = new ArrayList();

	/**
	 * Fügt einen neuen Stream hinzu.
	 * 
	 * @param os
	 */
	public void add(OutputStream os) {
		if (os != null) {
			streams.add(os);
			size++;
		}
	}

	/**
	 * Übergibt die zu schreibende Information an mehrere OutputStreams.
	 */
	public void write(int b) throws IOException {
		for (int i = 0; i < size; i++) {
			try {
				((OutputStream)streams.get(i)).write(b);
			} catch (IOException e) {
			}
		}
	}

	/**
	 * Übergibt die zu schreibende Information an mehrere OutputStreams.
	 */
	public void write(byte[] b) throws IOException {
		for (int i = 0; i < size; i++) {
			try {
				((OutputStream)streams.get(i)).write(b);
			} catch (IOException e) {
			}
		}
	}

	/**
	 * Übergibt die zu schreibende Information an mehrere OutputStreams.
	 */
	public void write(byte[] b, int off, int len) throws IOException {
		for (int i = 0; i < size; i++) {
			try {
				((OutputStream)streams.get(i)).write(b, off, len);
			} catch (IOException e) {
			}
		}
	}

	/**
	 * Schließt alle Kind-OutputStreams.
	 */
	public void close() {
		for (int i = 0; i < size; i++) {
			try {
				((OutputStream)streams.get(i)).close();
			} catch (IOException e) {
			}
		}
	}
	
}
