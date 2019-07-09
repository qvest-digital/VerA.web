package de.tarent.aa.veraweb.beans;
/**
 * Diese Schnittstelle markiert Beans, die Ergänzungen zu ImportPersons darstellen.
 *
 * @author mikel
 */
public interface ImportPersonExtra {
    /**
     * Diese Methode assoziiert diese Importpersonenergänzung mit ihrer Importperson;
     * dies bedeutet insbesondere die Übernahme deren ID als Fremdschlüssel auf sie.
     *
     * @param person Importperson, mit der dieses Extra assoziiert werden soll.
     */
    void associateWith(ImportPerson person);
}
