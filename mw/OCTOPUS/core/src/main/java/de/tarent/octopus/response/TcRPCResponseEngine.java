package de.tarent.octopus.response;
/**
 * MarkerInterface - Es gibt an, dass eine ResponseEngine
 * eine Antwort auf einen ProcedureCall liefert und nach folgendem Schema arbeitet.
 *
 * <br><br>
 * Das Feld RPC_RESPONSE_OUTPUT_FIELDS enthält die Felder des TcContent,
 * die an den Client zurück geliefert werden.
 * RPC_RESPONSE_OUTPUT_FIELDS kann sein:
 * <ul>
 *  <li>Eine Liste. Dann werden die Felder des TcContent
 *      unter dem gleichen Namen an den Client zurück geliefert,
 *      unter dem sie auch in TcContent liegen.<li>

 *  <li>Eine Map. Die Keys enthalten den Namen des Parameters
 *      in der Antwort an den Client, die Values enthalten den
 *      Feldnamen des Feldes im TcContent.<li>
 *
 * @author <a href="mailto:mancke@mancke-software.de">Sebastian Mancke</a>, <b>tarent GmbH</b>
 *
 */
public interface TcRPCResponseEngine {

    /**
     * Liste im TcContent mit den Feldschlüsseln
     * der Inhalte, die als Antwort gesendet werden sollen.
     */
    public static final String RPC_RESPONSE_OUTPUT_FIELDS = "responseParams.OutputFields";
}
