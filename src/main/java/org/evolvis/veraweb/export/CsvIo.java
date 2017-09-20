package org.evolvis.veraweb.export;

/*-
 * veraweb, platform independent webservice-based event management
 * (Veranstaltungsmanagment VerA.web), export module is
 * Copyright © 2016, 2017
 * 	Атанас Александров <a.alexandrov@tarent.de>
 * Copyright © 2016
 * 	Lukas Degener <l.degener@tarent.de>
 * 	Max Weierstall <m.weierstall@tarent.de>
 * Copyright © 2017
 * 	mirabilos <t.glaser@tarent.de>
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
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Properties;

import de.tarent.dfg.commons.backgroundjobs.BackgroundJobMonitor;
import de.tarent.extract.DummyMonitor;
import de.tarent.extract.ExtractIo;

public class CsvIo implements ExtractIo {
	
	private final Reader reader;
	private final Writer writer;
    private final Properties properties;
	
	public CsvIo(Reader reader, Writer writer, Properties properties){
		this.reader = reader;
		this.writer = writer;
        this.properties = properties;
	}

	public File getOutputFile() {
		return null;
	}

	public File getInputFile() {
		return null;
	}

	public Reader reader() throws IOException {
		return reader;
	}

	public Writer writer() throws IOException {
		return writer;
	}

	public BackgroundJobMonitor getMonitor() {
		return new DummyMonitor(null);
	}

	public Properties getProperties() {
		return properties;
	}

}
