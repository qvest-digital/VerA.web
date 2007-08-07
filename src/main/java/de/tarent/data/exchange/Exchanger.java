/*
 * $Id: Exchanger.java,v 1.1 2007/06/20 11:56:52 christoph Exp $
 * 
 * Created on 22.08.2005
 */
package de.tarent.data.exchange;

import java.io.InputStream;
import java.io.OutputStream;

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
    /** Das zu verwendende Austauschformat */
    ExchangeFormat getExchangeFormat();
    /** Das zu verwendende Austauschformat */
    void setExchangeFormat(ExchangeFormat format);
    
    /** Der zu verwendende Eingabedatenstrom */
    InputStream getInputStream();
    /** Der zu verwendende Eingabedatenstrom */
    void setInputStream(InputStream stream);
    
    /** Der zu verwendende Ausgabedatenstrom */
    OutputStream getOutputStream();
    /** Der zu verwendende Ausgabedatenstrom */
    void setOutputStream(OutputStream stream);
}
