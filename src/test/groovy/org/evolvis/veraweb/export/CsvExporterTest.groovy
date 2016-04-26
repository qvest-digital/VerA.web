package org.evolvis.veraweb.export

import de.tarent.extract.Extractor
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

        csvExporter = new CsvExporter(writer, dataSource)
        csvExporter.setExtractor(extractor)

    }

    void testExport() {


        when:
            csvExporter.export()

        then:
            1 * extractor.run(_)
    }
}
