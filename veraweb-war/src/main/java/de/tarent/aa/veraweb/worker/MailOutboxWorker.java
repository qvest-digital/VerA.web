/**
 * veraweb, platform independent webservice-based event management
 * (Veranstaltungsmanagment VerA.web), is
 * Copyright © 2004-2008 tarent GmbH
 * Copyright © 2013 tarent solutions GmbH
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see: http://www.gnu.org/licenses/
 */
package de.tarent.aa.veraweb.worker;

import java.io.IOException;
import java.sql.Timestamp;

import de.tarent.aa.veraweb.beans.MailOutbox;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.beans.TransactionContext;
import de.tarent.octopus.beans.veraweb.DatabaseVeraWeb;
import de.tarent.octopus.beans.veraweb.ListWorkerVeraWeb;
import de.tarent.octopus.server.OctopusContext;

/**
 * Dieser Octopus-Worker repr�sentiert eine �bersichtsseite
 * sowie die Detailseiten zu eMail-Vorlagen.
 * Siehe Task MailDraftList und MailDraftDetail.<br><br>
 * 
 * @author Christoph Jerolimov
 * @version $Revision: 1.1 $
 */
public class MailOutboxWorker extends ListWorkerVeraWeb {
    //
    // Konstruktoren
    //
    /**
     * Der Konstruktor legt den Bean-Namen fest.
     */
	public MailOutboxWorker() {
		super("MailOutbox");
	}

    //
    // Octopus-Aktionen
    //
	/** Octopus-Eingabe-Parameter f�r {@link #showDetail(OctopusContext, Integer, MailOutbox)} */
	public static final String INPUT_showDetail[] = { "id", "mailoutbox" };
	/** Octopus-Eingabe-Parameterzwang f�r {@link #showDetail(OctopusContext, Integer, MailOutbox)} */
	public static final boolean MANDATORY_showDetail[] = { false, false };
	/** Octopus-Ausgabe-Parameter f�r {@link #showDetail(OctopusContext, Integer, MailOutbox)} */
	public static final String OUTPUT_showDetail = "mailoutbox";
	/**
	 * L�dt eine eMail aus dem Postausgang und stellt
	 * diesen in den Content, wenn eine ID �bergeben wurde
	 * und sich noch keine eMail im Content befindet.
	 * 
	 * @param cntx Octopus-Context
	 * @param id Datenbank ID
	 * @param mailOutbox eMail-Entwurf aus dem Content.
	 * @return eMail-Entwurf oder null
	 * @throws BeanException 
	 * @throws IOException 
	 */
	public MailOutbox showDetail(OctopusContext cntx, Integer id, MailOutbox mailOutbox) throws BeanException, IOException {
		if (mailOutbox == null && id != null) {
			return (MailOutbox)getDatabase(cntx).getBean("MailOutbox", id);
		}
		return null;
	}

	/** Octopus-Eingabe-Parameter f�r {@link #saveDetail(OctopusContext, Boolean)} */
	public static final String INPUT_saveDetail[] = { "save" };
	/** Octopus-Eingabe-Parameterzwang f�r {@link #saveDetail(OctopusContext, Boolean)} */
	public static final boolean MANDATORY_saveDetail[] = { false };
	/** Octopus-Ausgabe-Parameter f�r {@link #saveDetail(OctopusContext, Boolean)} */
	public static final String OUTPUT_saveDetail = "mailoutbox";
	/**
	 * Speichert die �bergebenen eMail im Postausgang und setzt
	 * den Status auf 'zu versenden' zur�ck.
	 * 
	 * @param cntx Octopus-Context
	 * @param save Gibt an ob eMail-Entwurf gespeichert werden soll.
	 * @return eMail
	 * @throws BeanException 
	 * @throws IOException 
	 */
	public MailOutbox saveDetail(OctopusContext cntx, Boolean save) throws BeanException, IOException {
		if (save != null && save.booleanValue()) {
			MailOutbox mailOutbox = (MailOutbox)getRequest(cntx).getBean("MailOutbox", "mailoutbox");
			
			if (mailOutbox.lastupdate == null) {
				mailOutbox.lastupdate = new Timestamp(System.currentTimeMillis());
			}
			if (mailOutbox.status == null) {
				mailOutbox.status = new Integer(MailOutbox.STATUS_WAIT);
			}
			if (mailOutbox.status.intValue() != MailOutbox.STATUS_ERROR) {
				mailOutbox.errortext = null;
			}
			
			if (mailOutbox.isCorrect()) {
				TransactionContext context = ( new DatabaseVeraWeb(cntx) ).getTransactionContext();
				try
				{
					saveBean(cntx, mailOutbox, context);
					context.commit();
				}
				catch ( BeanException e )
				{
					context.rollBack();
					throw new BeanException( "Die E-Mail konnte nicht für den Versand vorbereitet werden.", e );
				}
			}
			return mailOutbox;
		}
		return null;
	}
}
