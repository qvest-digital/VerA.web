package de.tarent.octopus.content;
/**
 * Ein TcMessageDefinitionPart ist ein Parameter einer TcMessageDefinition
 *
 * @see TcMessageDefinition
 * @see TcPortDefinition
 */
public class TcMessageDefinitionPart {
    private String name;
    private String description;
    private String partDataType;
    private boolean optional;

    /**
     * Erzeugt einen neuen Parameter
     *
     * @param name         Name des Parameters
     * @param partDataType Datentyp des Parameters als XML-Schema-Type. Die Konstanten der Klassen können und sollen benutzt
     *                     werden.
     * @param description  Beschreibung des Parameters
     * @param optional     Flag, ob der Parameter optional sein soll
     */
    public TcMessageDefinitionPart(String name, String partDataType, String description, boolean optional) {
        this.name = name;
        this.description = description;
        this.partDataType = partDataType;
        this.optional = optional;
    }

    /**
     * Liefert den Namen dieses Parts
     */
    public String getName() {
        return this.name;
    }

    /**
     * Setzt den Namen dieses Parts
     */
    public void setName(String newName) {
        this.name = newName;
    }

    /**
     * Liefert die Beschreibung dieses Parts
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Liefert den Datentyp dieses Parts
     */
    public String getPartDataType() {
        return this.partDataType;
    }

    /**
     * Liefert das Optional Flag dieses Parts
     */
    public boolean isOptional() {
        return this.optional;
    }

    public String toString() {
        return "( " + name + ", " + description + ", " + partDataType + ", " + optional + " )";
    }
}
