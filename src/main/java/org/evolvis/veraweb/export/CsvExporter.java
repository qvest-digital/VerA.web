package org.evolvis.veraweb.export;

import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

import de.tarent.extract.Extractor;

public class CsvExporter {

	private Extractor extractor;
	private CsvIo io;

	public CsvExporter(Writer writer, DataSource source) throws UnsupportedEncodingException {
		this.extractor = new Extractor(new JdbcTemplate(source));
		this.io = new CsvIo(new InputStreamReader(getClass().getClassLoader().getResourceAsStream("config.yaml"), "utf-8"), writer);
	}

	public void export() {
		extractor.run(io);
	}

	public void setExtractor(Extractor extractor) {
		this.extractor = extractor;
	}
}
