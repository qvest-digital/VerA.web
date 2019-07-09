package org.evolvis.veraweb.export;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import de.tarent.extract.ExtractIo;
import de.tarent.extract.Extractor;
import de.tarent.extract.ExtractorQuery;
import de.tarent.extract.utils.ExtractorException;
import lombok.extern.log4j.Log4j2;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Log4j2
public class CsvExporter {
    private final Extractor extractor;
    private final CsvIo io;
    private final ExtractorQueryBuilder template;

    public CsvExporter(Reader reader, Writer writer, DataSource source, Properties properties)
      throws UnsupportedEncodingException {
        this(reader, writer, source, properties, null);
    }

    public CsvExporter(Reader reader, Writer writer, DataSource source, Properties properties, List<String> selectedColumns) {
        extractor = new Extractor(new JdbcTemplate(source));
        io = new CsvIo(reader, writer, properties);
        template = new ExtractorQueryBuilder(loadQuery(io, selectedColumns));
    }

    public void export(Map<String, String> substitutions, Map<String, String> filterSettings) {
        final ExtractorQuery query =
          template.replace(substitutions)
            .setFilters(filterSettings) //do not change without checking for SQL injection
            .build();
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

    private ExtractorQuery loadQuery(ExtractIo io, List<String> selectedColumns) {
        try {
            DynamicExtractorQuery query = this.mapper().readValue(io.reader(), DynamicExtractorQuery.class);
            query.setSelectedColumns(selectedColumns);
            return query;
        } catch (JsonParseException e1) {
            logger.error("Couldn\'t parse json", e1);
            throw new ExtractorException("Couldn\'t parse json", e1);
        } catch (JsonMappingException e2) {
            logger.error("Couldn\'t map json", e2);
            throw new ExtractorException("Couldn\'t map json", e2);
        } catch (IOException e3) {
            logger.error("Could not load configuration", e3);
            throw new ExtractorException("Could not load configuration", e3);
        }
    }
}
