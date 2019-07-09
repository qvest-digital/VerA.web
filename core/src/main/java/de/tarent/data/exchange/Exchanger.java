package de.tarent.data.exchange;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

/**
 * Diese Schnittstelle beschreibt Grundfunktionen einer Datenaustauschklasse,
 * also eines {@link de.tarent.aa.veraweb.utils.Exporter Exporters} oder
 * {@link de.tarent.aa.veraweb.utils.Importer Importers}. Diese dienen im
 * Wesentlichen dazu, diesem Informationen zum Datenaustauschformat und zu
 * den beteiligten Datenströmen zu geben.
 *
 * @author mikel
 */
public interface Exchanger {
    //
    // Getter und Setter
    //

    /**
     * Das zu verwendende Austauschformat
     *
     * @return exchange Format
     */
    ExchangeFormat getExchangeFormat();

    /**
     * Das zu verwendende Austauschformat
     *
     * @param format exchange Format
     */
    void setExchangeFormat(ExchangeFormat format);

    /**
     * Der zu verwendende Eingabedatenstrom
     *
     * @return input Stream
     */
    InputStream getInputStream();

    /**
     * Der zu verwendende Eingabedatenstrom
     *
     * @param stream input stream
     */
    void setInputStream(InputStream stream);

    /**
     * Der zu verwendende Ausgabedatenstrom
     *
     * @return output Stream
     */
    OutputStream getOutputStream();

    /**
     * Der zu verwendende Ausgabedatenstrom
     *
     * @param stream outputStream
     */
    void setOutputStream(OutputStream stream);

    /**
     * Wir handhaben neuerdings das Encoding (Unix-Sinn, nicht VerA.web-„Zeichensatz“)
     * separat vom Ausgabeformat. Hier wird es gesetzt.
     *
     * @param cs file encoding to be used
     */
    void setFileEncoding(Charset cs);
}
