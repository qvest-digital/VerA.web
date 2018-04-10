package de.tarent.octopus.content;
import java.util.ArrayList;
import java.util.List;

/**
 * Eine MessageDefinition ist eine Auflistung
 * Ein- oder Ausgabeparameter von Operationen.
 *
 * Jeder Parameter wird durch eine TcMessageDefinitionPart Objekt dar gestellt.
 *
 * Eine Ausführlichere Beschreibung des Contextes ist in TcPortDefinition
 * @see TcPortDefinition
 */
public class TcMessageDefinition {
    /**
     * Constante für den Datentyp Scalar.
     * Ein Scalar ist ein Parameter, der beliebige einteilige Daten aufnehmen kann (z.B. integer, string, float, ...)
     */
    public static final String TYPE_SCALAR = "xsd:string";

    /**
     * Constante für den Datentyp Array mit beliebigen Elementen.
     * Ein Array ist ein Parameter, der eine Liste von anderen Parametern aufnehmen kann.
     */
    public static final String TYPE_ARRAY = "xsd:array";

    /**
     * Constante für den Datentyp Struct.
     * Ein Struct ist ein Parameter, der eine Map (z.B. wie eine Java Map) aufnehmen kann.
     * Darin können also andere Parameter mit ihren Namen als Keys abgelegt werden.
     */
    public static final String TYPE_STRUCT = "tc:struct";

    private List parts;

    /**
     * Initialisierung mit einer leeren Menge von Parametern
     */
    public TcMessageDefinition() {
        parts = new ArrayList();
    }

    /**
     * Erzeugt einen neuen Parameter und fügt diesen an.
     * @param newPart Der neue Parameter
     */
    public TcMessageDefinition addPart(TcMessageDefinitionPart newPart) {
        int size = parts.size();
        for (int i = 0; i < size; i++) {
            TcMessageDefinitionPart curr = (TcMessageDefinitionPart) parts.get(i);
            if (curr.getName().equals(newPart.getName())) {
                parts.remove(i);
                break;
            }
        }
        parts.add(newPart);
        return this;
    }

    /**
     * Erzeugt einen neuen Parameter und fügt diesen an.
     * @param name Name des Parameters
     * @param partDataType Datentyp des Parameters als XML-Schema-Type. Die Konstanten der Klassen können und sollen benutzt werden.
     * @param description Beschreibung des Parameters
     * @param optional Flag, ob der Parameter optional sein soll
     */
    public TcMessageDefinition addPart(String name, String partDataType, String description, boolean optional) {
        addPart(new TcMessageDefinitionPart(name, partDataType, description, optional));
        return this;
    }

    /**
     * Erzeugt einen neuen Parameter und fügt diesen an.
     * Optional wird auf false gesetzt.
     * @param name Name des Parameters
     * @param partDataType Datentyp des Parameters als XML-Schema-Type. Die Konstanten der Klassen können und sollen benutzt werden.
     * @param description Beschreibung des Parameters
     */
    public TcMessageDefinition addPart(String name, String partDataType, String description) {
        addPart(name, partDataType, description, false);
        return this;
    }

    /**
     * Fügt eine Liste von neuen Parametern an.
     * @param addParts Vector mit Elementen vom Type TcMessageDefinitionPart
     */
    public TcMessageDefinition addParts(List addParts) {
        int size = addParts.size();
        for (int i = 0; i < size; i++) {
            addPart((TcMessageDefinitionPart) addParts.get(i));
        }
        return this;
    }

    /**
     * Liefert die TcMessageDefinitionParts. Also die Parameter dieser Message
     * @return Vector mit TcMessageDefinitionPart Objekten
     */
    public List getParts() {
        return parts;
    }

    /**
     * Liefert die den TcMessageDefinitionPart mit dem entsprechenden Namen.
     * @return TcMessageDefinitionPart mit dem Namen oder NULL,
     *        wenn es keinen solchen gibt.
     */
    public TcMessageDefinitionPart getPartByName(String partName) {
        for (int i = 0; i < parts.size(); i++) {
            TcMessageDefinitionPart curr = (TcMessageDefinitionPart) parts.get(i);
            if (curr.getName().equals(partName))
                return curr;
        }
        return null;
    }

    public String toString() {
        return parts.toString();
    }
}
