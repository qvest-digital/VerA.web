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
package de.tarent.aa.veraweb.beans.facade;

/**
 * Diese Schnittstelle stellt Konstanten f�r {@link de.tarent.aa.veraweb.beans.Person}-
 * und {@link de.tarent.aa.veraweb.beans.Person}-Instanzen zur Verf�gung.
 * 
 * @author christoph
 */
public interface PersonConstants {
	/** Person ist noch nicht als gel�scht markiert worden. */
	public static final String DELETED_FALSE = "f";

	/** Person wurde als gel�scht markiert. */
	public static final String DELETED_TRUE = "t";

	/** Person ist eine Privatperson. */
	public static final String ISCOMPANY_FALSE = "f";

	/** Person ist eine Firma / Institution. */
	public static final String ISCOMPANY_TRUE = "t";

	/** Member ID f�r die Hauptperson */
	public static final int MEMBER_MAIN = 1;

	/** Member ID f�r den Partner */
	public static final int MEMBER_PARTNER = 2;

	/** Adresstype ID f�r die gesch�ftliche Anschrift */
	public static final int ADDRESSTYPE_BUSINESS = 1;

	/** Adresstype ID f�r die private Anschrift */
	public static final int ADDRESSTYPE_PRIVATE = 2;

	/** Adresstype ID f�r die weitere Anschrift */
	public static final int ADDRESSTYPE_OTHER = 3;

	/** Locale ID f�r den lateinischen Zeichensatz */
	public static final int LOCALE_LATIN = 1;

	/** Locale ID f�r den lateinischen Zeichensatz */
	public static final int LOCALE_EXTRA1 = 2;

	/** Locale ID f�r den lateinischen Zeichensatz */
	public static final int LOCALE_EXTRA2 = 3;

	/** Gibt an ob eine Person aus dem Inland kommt. */
	public static final String DOMESTIC_INLAND = "t";

	/** Gibt an ob eine Person aus dem Ausland kommt. */
	public static final String DOMESTIC_AUSLAND = "f";

	/** Gibt an ob eine Person m�nnlich ist. */
	public static final String SEX_MALE = "m";

	/** Gibt an ob eine Person weiblich ist. */
	public static final String SEX_FEMALE = "f";

}
