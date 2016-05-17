package org.evolvis.veraweb.export;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Properties;

import de.tarent.dfg.commons.backgroundjobs.BackgroundJobMonitor;
import de.tarent.extract.DummyMonitor;
import de.tarent.extract.ExtractIo;

public class CsvIo implements ExtractIo {
	
	private Reader reader;
	private Writer writer;
	
	public CsvIo(Reader reader, Writer writer){
		this.reader = reader;
		this.writer = writer;
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
		return null;
	}

}
