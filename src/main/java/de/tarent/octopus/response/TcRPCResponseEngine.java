package de.tarent.octopus.response;

/*-
 * tarent-octopus, Webservice Data Integrator and Application Server
 * Copyright © 2002–2017 tarent solutions GmbH and its contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
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
