package org.evolvis.veraweb.export

import de.tarent.extract.Extractor
import spock.lang.Specification

import javax.sql.DataSource

/**
 * Created by mweier on 26.04.16.
 */
class CsvExporterTest extends Specification {

    CsvExporter csvExporter
    Writer writer = Mock(Writer)
    DataSource dataSource = Mock(DataSource)
    Extractor extractor = Mock(Extractor)

    public void setup() {
        csvExporter = new CsvExporter(writer, dataSource, 42, extractor)
    }

    void testExport() {
        when:
            csvExporter.export()

        then:
            1 * extractor.run(_,_)
    }
}
