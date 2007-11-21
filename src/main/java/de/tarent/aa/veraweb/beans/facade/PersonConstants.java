/*
 * $Id: PersonConstants.java,v 1.1 2007/06/20 11:56:51 christoph Exp $
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
