package de.tarent.aa.veraweb.utils;

/*-
 * veraweb, platform independent webservice-based event management
 * (Veranstaltungsmanagment VerA.web), is
 * Copyright © 2004–2008 tarent GmbH
 * Copyright © 2013–2016 tarent solutions GmbH
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
	 * @param os FIXME
	 */
	public void add(OutputStream os) {
		if (os != null) {
			streams.add(os);
			size++;
		}
	}

	/**
	 * übergibt die zu schreibende Information an mehrere OutputStreams.
	 */
	@Override
    public void write(int b) throws IOException {
		for (int i = 0; i < size; i++) {
			try {
				((OutputStream) streams.get(i)).write(b);
			} catch (IOException e) {
			}
		}
	}

	/**
	 * übergibt die zu schreibende Information an mehrere OutputStreams.
	 */
	@Override
    public void write(byte[] b) throws IOException {
		for (int i = 0; i < size; i++) {
			try {
				((OutputStream) streams.get(i)).write(b);
			} catch (IOException e) {
			}
		}
	}

	/**
	 * übergibt die zu schreibende Information an mehrere OutputStreams.
	 */
	@Override
    public void write(byte[] b, int off, int len) throws IOException {
		for (int i = 0; i < size; i++) {
			try {
				((OutputStream) streams.get(i)).write(b, off, len);
			} catch (IOException e) {
			}
		}
	}

	/**
	 * Schließt alle Kind-OutputStreams.
	 */
	@Override
    public void close() {
		for (int i = 0; i < size; i++) {
			try {
				((OutputStream) streams.get(i)).close();
			} catch (IOException e) {
			}
		}
	}

}
