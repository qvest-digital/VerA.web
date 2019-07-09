package de.tarent.commons.spreadsheet.export.ods;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import de.tarent.commons.spreadsheet.export.XMLDocument;

/**
 * Repräsentiert ein OpenDocument SpreadSheet Dokument.
 * <p>
 * <em>Als Vorlage diente eine unter Windows 2000 SP4 mit
 * OpenOffice.org 1.9.79 erzeugte ODS-Datei.</em>
 *
 * @author Christoph Jerolimov
 */
public class ODSDocument extends ODSContent {
    public void save(OutputStream outputStream) throws IOException {
        try {
            ZipOutputStream zip = new ZipOutputStream(outputStream);

            zip.putNextEntry(new ZipEntry("mimetype"));
            zip.write(CONTENT_TYPE.getBytes("UTF-8"));
            zip.closeEntry();

            pipe(zip, "META-INF/manifest.xml", getStream("manifest.xml"));
            pipe(zip, "styles.xml", getStream("styles.xml"));

            zip.putNextEntry(new ZipEntry("content.xml"));

            super.save(zip);
            zip.closeEntry();
            zip.close();
        } catch (Exception e) {
            throwIOException(e);
        }
    }

    protected static void pipe(ZipOutputStream zip, String entry, InputStream inputStream)
      throws IOException, ParserConfigurationException, FactoryConfigurationError, TransformerFactoryConfigurationError,
      TransformerException {
        zip.putNextEntry(new ZipEntry(entry));
        XMLDocument doc = new XMLDocument();
        doc.loadDocument(inputStream);
        doc.saveDocument(zip);
        zip.closeEntry();
    }
}
