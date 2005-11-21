/* $Id: UserDataProvider.java,v 1.1.1.1 2005/11/21 13:33:38 asteban Exp $
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

package de.tarent.octopus.client;

/**
 * Liefert auf Nachfrage die Benutzerdaten, 
 * die z.B. über einen Dialog angefordert werden können.
 */
public interface UserDataProvider {


    /**
     * Fordert die Benutzerdaten an (z.B. über einen Login-Dialog);
     *
     * @param message Nachricht, die einem Benutzer gezeigt werden kann.
     * @param usernamePreselection Vorbelegung des Benutzernamens, darf null sein.
     * @return true, wenn die Daten vorliegen, false sonst (z.B. bei Abbruch des Dialogs).
     */
    public boolean requestUserData(String message, String usernamePreselection);

    /**
     * @return Liefert den bereit gestellten Benutzernamen 
     */
    public String getUsername();

    /**
     * @return Liefert das bereit gestellte Passwort
     */
    public String getPassword();

}