package org.evolvis.veraweb.export;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import de.tarent.extract.ExtractIo;
import de.tarent.extract.ExtractorQuery;
import de.tarent.extract.utils.ExtractorException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;

import de.tarent.extract.Extractor;

public class CsvExporter {

    private static final Logger LOGGER = LogManager.getLogger(Extractor.class);
    private final Extractor extractor;
    private final CsvIo io;
    private final ExtractorQueryBuilder template;

	public CsvExporter(Reader reader, Writer writer, DataSource source, Properties properties) throws UnsupportedEncodingException {
		extractor = new Extractor(new JdbcTemplate(source));
		io = new CsvIo(reader, writer, properties);
        template = new ExtractorQueryBuilder(loadQuery(io));
	}

	public void export(Map<String, String> substitutions) {
	    final ExtractorQuery query = template.replace(substitutions).build();
		extractor.run(io, query);
	}

    private ObjectMapper mapper() {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(MapperFeature.AUTO_DETECT_CREATORS, true);
        mapper.configure(MapperFeature.CAN_OVERRIDE_ACCESS_MODIFIERS, true);
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, true);
        return mapper;
    }

    private ExtractorQuery loadQuery(ExtractIo io) {
        try {
            ExtractorQuery query = this.mapper().readValue(io.reader(), ExtractorQuery.class);
            return query;
        } catch (JsonParseException e1) {
            LOGGER.error("Couldn\'t parse json", e1);
            throw new ExtractorException("Couldn\'t parse json", e1);
        } catch (JsonMappingException e2) {
            LOGGER.error("Couldn\'t map json", e2);
            throw new ExtractorException("Couldn\'t map json", e2);
        } catch (IOException e3) {
            LOGGER.error("Could not load configuration", e3);
            throw new ExtractorException("Could not load configuration", e3);
        }
    }
}
