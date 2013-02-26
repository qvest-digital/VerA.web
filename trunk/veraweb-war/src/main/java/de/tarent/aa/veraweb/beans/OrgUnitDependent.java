/**
 * veraweb, platform independent webservice-based event management
 * (Veranstaltungsmanagment VerA.web), is
 * Copyright © 2004-2008 tarent GmbH
 * Copyright © 2013 tarent solutions GmbH
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License, version 2, as
 * published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see: http://www.gnu.org/licenses/
 */
package de.tarent.aa.veraweb.beans;

/**
 * Diese Schnittstelle dient als Marker f�r Beans, die mandantenspezifisch behandelt werden
 * sollen.<br>
 * 
 * Dies wird �ber eine Markerschnittstelle statt einer Zwischenklasse in der Hierarchie
 * umgesetzt, da der konkrete Filter auf den Mandanten Factory-abh�ngig ist. 
 * 
 * @author mikel
 */
public interface OrgUnitDependent {

}
