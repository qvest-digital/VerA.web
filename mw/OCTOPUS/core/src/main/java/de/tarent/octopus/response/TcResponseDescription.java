package de.tarent.octopus.response;
/** Objekt zur Beschreibung der Ausgabeseite für verscheidenen Ausgabeformate.
 *  @author <a href="mailto:mancke@mancke-software.de">Sebastian Mancke</a>, <b>tarent GmbH</b>
 *  @author <a href="mailto:H.Helwich@tarent.de">Hendrik Helwich</a>, <b>tarent GmbH</b>
 */
public class TcResponseDescription {
    private String descName;
    private String responseType;

    /** Inititalisiert die Antwortbeschreibung
     *  @param descName Name der Response
     *  @param responseType Typ, von der die Response sein soll (z.B. velocityPage oder simplePage)
     */
    public TcResponseDescription(String descName, String responseType) {
        this.descName = descName;
        this.responseType = responseType;
    }

    /** Gibt den Typ zurück, von dem die Ausgabeseite sein soll.
     * Damit kann das Modul für die Ausgabegenerierung bestimmt werden.
     */
    public String getDescName() {
        return descName;
    }

    /** Gibt den Namen zurück, den das Modul für die Ausgabegenerierung benutzt.
     */
    public String getResponseType() {
        return responseType;
    }
}
