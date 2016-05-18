package org.evolvis.veraweb.export

import de.tarent.extract.ExtractIo
import de.tarent.extract.Extractor
import org.apache.commons.io.IOUtils
import spock.lang.Ignore
import spock.lang.Specification

import javax.sql.DataSource

/**
 * Created by mweier on 26.04.16.
 */
class CsvExporterTest extends Specification {

    def csvExporter
    def extractor

    public void setup() {
        DataSource dataSource = Mock(DataSource)
        Writer writer = Mock(Writer)
        extractor = Mock(Extractor)

        csvExporter = new CsvExporter(writer, dataSource, 42)
//        csvExporter.setExtractor(extractor)

    }

    @Ignore
    void testExport() {
        when:
            csvExporter.export()

        then:
            1 * extractor.run(_) >> {arguments->
                ExtractIo io = (ExtractIo) arguments[0];
                String actual = IOUtils.toString(io.reader());
                String expected = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("config.yaml"));
                assert actual == expected;
            }
    }
}
