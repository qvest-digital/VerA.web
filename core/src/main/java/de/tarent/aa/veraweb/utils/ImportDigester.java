package de.tarent.aa.veraweb.utils;
import de.tarent.aa.veraweb.beans.ImportPerson;
import de.tarent.aa.veraweb.beans.ImportPersonExtra;
import de.tarent.octopus.beans.BeanException;

import java.io.IOException;
import java.util.List;

/**
 * Diese Schnittstelle stellt bei einem Import die Instanz dar, der ein
 * {@link Importer} neue Personendatensätze anliefert.
 *
 * @author mikel
 */
public interface ImportDigester {
    /**
     * Diese Methode wird zu Beginn eines Imports aufgerufen.
     *
     * @throws BeanException FIXME
     */
    void startImport() throws BeanException;

    /**
     * Diese Methode wird zum Ende eines Imports aufgerufen.
     *
     * @throws BeanException FIXME
     */
    void endImport() throws BeanException;

    /**
     * Diese Methode wird von einem {@link Importer} zu jeder zu importierenden
     * Person aufgerufen, übergeben wird die Person und eine Liste mit Beans,
     * die Zusätze zur Person darstellen.<br>
     * Falls Abhängigkeiten unter diesen Beans bestehen, stehen in der
     * Liste die Beans, von der eine bestimmte Bean abhängt, vor dieser.
     *
     * @param person eine {@link ImportPerson}-Instanz
     * @param extras eine Liste mit Beans, die Zusätze zur Person darstellen; es
     *               werden nur solche akzeptiert, die {@link ImportPersonExtra} implementieren.
     * @throws BeanException FIXME
     * @throws IOException   FIXME
     */
    void importPerson(ImportPerson person, List extras) throws BeanException, IOException;
}
