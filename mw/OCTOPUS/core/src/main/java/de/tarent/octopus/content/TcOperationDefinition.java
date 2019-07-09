package de.tarent.octopus.content;
import java.util.HashMap;
import java.util.Map;

/**
 * Beschreibgung einer Operation eines Ports.
 * Diese OperationDefinition enthält Input-, Output- und FaultMessages
 *
 * Eine Ausführlichere Beschreibung ist in TcPortDefinition
 *
 * @see TcPortDefinition
 */
public class TcOperationDefinition {
    private String name;
    private String description;

    private TcMessageDefinition inputMessage;
    private TcMessageDefinition outputMessage;
    private Map faultMessages;
    private Map faultMessageDescriptions;

    /**
     * Initialisiert eine OperationDefinition
     *
     * @param name        Name der TcPortDefinition
     * @param description Sinnvolle Beschreibung des Ports
     */
    public TcOperationDefinition(String name, String description) {
        this.name = name;
        this.description = description;
        faultMessages = new HashMap();
        faultMessageDescriptions = new HashMap();
    }

    /**
     * Liefert den Namen, der im Constructor übergeben wurde.
     *
     * @return String mit dem Namen
     */
    public String getName() {
        return this.name;
    }

    /**
     * Liefert die Beschreibung, die im Constructor übergeben wurde.
     *
     * @return String mit der Beschreibung
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Liefert die InputMessage, das sind die Parameter, die diese Operation als Eingabe benötigt.
     *
     * @return TcMessageDefinition mit den Inputparametern als Inhalt
     */
    public TcMessageDefinition getInputMessage() {
        return inputMessage;
    }

    /**
     * Liefert die OutputMessage, das sind die Parameter, die diese Operation als Ausgabe liefert.
     *
     * @return TcMessageDefinition mit den Ausgabeparametern als Inhalt
     */
    public TcMessageDefinition getOutputMessage() {
        return outputMessage;
    }

    /**
     * Liefert die FaultMessages. Das ist eine Map, die für jede Fehlerrückgabe
     * der Operation einen MessageBeschreibung der Ausgabeparameter enthält.
     *
     * @return Map mit den Namen der Fehlerzustände als String Keys, und den TcMessageDefinitions dieser Fehler als Values.
     */
    public Map getFaultMessages() {
        return faultMessages;
    }

    /**
     * Liefert eine FaultMessage, zu ihrem zugehörigen Statuscode
     *
     * @param faultStatusCode Der Fehlerstatus, zu dem die Beschreibung zurück gegeben werden soll.
     * @return Die Beschreibung der Rückgabeparameter bei einem bestimmten FaultCode
     */
    public TcMessageDefinition getFaultMessage(String faultStatusCode) {
        return (TcMessageDefinition) faultMessages.get(faultStatusCode);
    }

    /**
     * Liefert die Beschreibung zu einem faultStatusCode.
     * Das soll eine Beschreibung sein, die den Fehler kurz Charakterisiert.
     *
     * @return Beschreibung der Fehlerrückgabe
     */
    public String getFaultMessageDescription(String faultStatusCode) {
        return (String) faultMessageDescriptions.get(faultStatusCode);
    }

    /**
     * Setzt die InputMessage mit einem bereits existierenden Objekt.
     * Eine Operation hat genau eine InputMessage.
     *
     * @param inputMessage Die Beschreibung der Parameter der Operation
     * @return Gibt die neu gesetzte InputMessage zurück.
     */
    public TcMessageDefinition setInputMessage(TcMessageDefinition inputMessage) {
        this.inputMessage = inputMessage;
        return inputMessage;
    }

    /**
     * Erzeugt ein neues InputMessage Objekt und fügt es dieser Operation hinzu.
     * Eine Operation hat genau eine InputMessage.
     *
     * @return Gibt die neu erzeugte InputMessage zurück.
     */
    public TcMessageDefinition setInputMessage() {
        return setInputMessage(new TcMessageDefinition());
    }

    /**
     * Setzt die OutputMessage mit einem bereits existierenden Objekt.
     * Eine Operation hat genau eine OutputMessage.
     *
     * @param outputMessage Die Beschreibung der Ausgabeparameter der Operation
     * @return Gibt die neu gesetzte OutputMessage zurück.
     */
    public TcMessageDefinition setOutputMessage(TcMessageDefinition outputMessage) {
        this.outputMessage = outputMessage;
        return outputMessage;
    }

    /**
     * Erzeugt ein neues OutputMessage Objekt und fügt es dieser Operation hinzu.
     * Eine Operation hat genau eine OutputMessage.
     *
     * @return Gibt die neu erzeugte OutputMessage zurück.
     */
    public TcMessageDefinition setOutputMessage() {
        return setOutputMessage(new TcMessageDefinition());
    }

    /**
     * Fügt eine MessageDefinition für einen bestimmten Statuscode an.
     *
     * @param faultStatusCode  Der Fehlercode zu der diese FaultMessage gehört.
     * @param faultDescription Liefert die Beschreibung zu einem faultStatusCode. Das soll eine Beschreibung sein, die den
     *                         Fehler kurz Charakterisiert.
     * @param faultMessage     Die Beschreibung der Ausgabeparameter der Operation bei einem Fehler mit dem faultStatusCode
     * @return Die gesetzte FaultMessage
     */
    public TcMessageDefinition addFaultMessage(
      String faultStatusCode,
      String faultDescription,
      TcMessageDefinition faultMessage) {

        faultMessages.put(faultStatusCode, faultMessage);
        faultMessageDescriptions.put(faultStatusCode, faultDescription);
        return faultMessage;
    }

    /**
     * Erstellt eine neue MessageDefinition und fügt sie für einen bestimmten Statuscode an.
     *
     * @param faultStatusCode  Der Fehlercode zu der diese FaultMessage gehört.
     * @param faultDescription Liefert die Beschreibung zu einem faultStatusCode. Das soll eine Beschreibung sein, die den
     *                         Fehler kurz Charakterisiert.
     * @return Die neu erzeugte FaultMessage
     */
    public TcMessageDefinition addFaultMessage(String faultStatusCode, String faultDescription) {
        return addFaultMessage(faultStatusCode, faultDescription, new TcMessageDefinition());
    }
}
