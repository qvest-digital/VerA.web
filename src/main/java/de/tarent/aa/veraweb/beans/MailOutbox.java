/*
 * VerA.web,
 * Veranstaltungsmanagment VerA.web
 * Copyright (c) 2005-2007 tarent GmbH
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License,version 2
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 *
 * tarent GmbH., hereby disclaims all copyright
 * interest in the program 'VerA.web'
 * Signature of Elmar Geese, 7 August 2007
 * Elmar Geese, CEO tarent GmbH.
 */

/*
 * $Id: MailOutbox.java,v 1.1 2007/06/20 11:56:51 christoph Exp $
 */
package de.tarent.aa.veraweb.beans;

import java.sql.Timestamp;

import de.tarent.octopus.PersonalConfigAA;
import de.tarent.octopus.custom.beans.BeanException;
import de.tarent.octopus.server.OctopusContext;

/**
 * Dieses Bean repr�sentiert eine ausgehende eMail und wird als Tupel
 * in der Tabelle <code>veraweb.tmailoutbox</code> gespeichert.
 * 
 * @author Christoph Jerolimov
 * @version $Revision: 1.1 $
 */
public class MailOutbox extends AbstractBean {
	/** Status der eMail: Offen, wird nicht versendet. */
	public static final int STATUS_OPEN = 1;
	/** Status der eMail: Wartet auf die Versendung. */
	public static final int STATUS_WAIT = 2;
	/** Status der eMail: Wird gerade versendet. */
	public static final int STATUS_INPROCESS = 3;
	/** Status der eMail: eMail Wurde versendet. */
	public static final int STATUS_SEND = 4;
	/** Status der eMail: Beim versenden ist ein Fehler aufgetreten. */
	public static final int STATUS_ERROR = Integer.MAX_VALUE;

	/** PK der Tabelle tmailoutbox */
	public Integer id;
	/** Status der eMail */
	public Integer status;
	/** Absender der eMail */
	public String from;
	/** Empf�nger der eMail */
	public String to;
	/** Betreff der eMail */
	public String subject;
	/** Text der eMail */
	public String text;
	/** Daten der letzten Ver�nderung */
	public Timestamp lastupdate;
	/** Error Text */
	public String errortext;

	public void verify() throws BeanException {
		if (from == null || from.length() == 0)
			addError("Die E-Mail kann nicht ohne Absender versendet werden.");
		if (to == null || to.length() == 0)
			addError("Die E-Mail kann nicht ohne Empf�nger versendet werden.");
		if (subject == null || subject.length() == 0)
			addError("Die E-Mail kann nicht ohne Betreff versendet werden.");
	}

	/**
	 * Diese Methode testet, ob im aktuellen Kontext diese Bohne gelesen werden
	 * darf.<br>
	 * Test ist, ob der Benutzer Standard-Reader ist.
	 * 
	 * @param cntx Octopus-Kontext
	 * @throws BeanException Wenn im angegebenen Kontext diese Bohne nicht gelesen werden darf.
	 * @see AbstractBean#checkRead(OctopusContext)
	 */
	public void checkRead(OctopusContext cntx) throws BeanException {
		checkGroup(cntx, PersonalConfigAA.GROUP_READ_STANDARD);
	}

	/**
	 * Diese Methode testet, ob im aktuellen Kontext diese Bohne geschrieben
	 * werden darf.<br>
	 * Test ist, ob der Benutzer Writer ist.
	 * 
	 * @param cntx Octopus-Kontext
	 * @throws BeanException Wenn im angegebenen Kontext diese Bohne nicht geschrieben werden darf.
	 * @see AbstractBean#checkWrite(OctopusContext)
	 */
	public void checkWrite(OctopusContext cntx) throws BeanException {
		checkGroup(cntx, PersonalConfigAA.GROUP_READ_STANDARD);
	}
}
