/*
 * $Id: MailOutboxWorker.java,v 1.1 2007/06/20 11:56:51 christoph Exp $
 */
package de.tarent.aa.veraweb.worker;

import java.io.IOException;
import java.sql.Timestamp;

import de.tarent.aa.veraweb.beans.MailOutbox;
import de.tarent.octopus.custom.beans.BeanException;
import de.tarent.octopus.custom.beans.veraweb.ListWorkerVeraWeb;
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
				saveBean(cntx, mailOutbox);
			}
			return mailOutbox;
		}
		return null;
	}
}
