/*
 * $Id: MailDraft.java,v 1.1 2007/06/20 11:56:51 christoph Exp $
 */
package de.tarent.aa.veraweb.beans;

import java.sql.Timestamp;

import de.tarent.octopus.PersonalConfigAA;
import de.tarent.octopus.custom.beans.BeanException;
import de.tarent.octopus.server.OctopusContext;

/**
 * Dieses Bean repräsentiert eine eMail-Vorlage und wird als Tupel
 * in der Tabelle <code>veraweb.tmaildraft</code> gespeichert.
 * 
 * @author Christoph Jerolimov
 * @version $Revision: 1.1 $
 */
public class MailDraft extends AbstractHistoryBean {
	/** PK der Tabelle tmaildraft */
	public Integer id;
	/** Name der Vorlage */
	public String name;
	/** Betreff der eMail */
	public String subject;
	/** Text der eMail */
	public String text;
	/** Erstellt von */
	public String createdby;
	/** Erstellt am */
	public Timestamp created;
	/** Geänder von */
	public String changedby;
	/** Geändert am */
	public Timestamp changed;

	public void verify() throws BeanException {
		if (name == null || name.length() == 0)
			addError("Sie müssen der E-Mail-Vorlage einen Namen geben.");
		if (subject == null || subject.length() == 0)
			addError("Sie haben der E-Mail-Vorlage keinen Betreff gegeben.");
		if (text == null || text.length() == 0)
			addError("Sie haben in dieser E-Mail-Vorlage keinen Text eingegeben..");
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
