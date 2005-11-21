/* $Id: TcRPCResponseEngine.java,v 1.1.1.1 2005/11/21 13:33:38 asteban Exp $
 * 
 * tarent-octopus, Webservice Data Integrator and Applicationserver
 * Copyright (C) 2002 tarent GmbH
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 * tarent GmbH., hereby disclaims all copyright
 * interest in the program 'tarent-octopus'
 * (which makes passes at compilers) written
 * by Sebastian Mancke and Michael Klink.
 * signature of Elmar Geese, 1 June 2002
 * Elmar Geese, CEO tarent GmbH
 */

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
