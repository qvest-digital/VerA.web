/*
 * veraweb,
 * Veranstaltungsmanagment veraweb
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
 * interest in the program 'veraweb'
 * Signature of Elmar Geese, 21 November 2007
 * Elmar Geese, CEO tarent GmbH.
 */

/*
 * $Id: MailDraftWorker.java,v 1.1 2007/06/20 11:56:51 christoph Exp $
 */
package de.tarent.aa.veraweb.worker;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import de.tarent.aa.veraweb.beans.MailDraft;
import de.tarent.dblayer.sql.Escaper;
import de.tarent.dblayer.sql.SQL;
import de.tarent.dblayer.sql.clause.Expr;
import de.tarent.dblayer.sql.clause.RawClause;
import de.tarent.dblayer.sql.statement.Select;
import de.tarent.octopus.custom.beans.BeanException;
import de.tarent.octopus.custom.beans.Database;
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
public class MailDraftWorker extends ListWorkerVeraWeb {
    //
    // Konstruktoren
    //
    /**
     * Der Konstruktor legt den Bean-Namen fest.
     */
	public MailDraftWorker() {
		super("MailDraft");
	}

    //
    // Oberklasse BeanListWorker
    //
	protected Integer getAlphaStart(OctopusContext cntx, String start) throws BeanException, IOException {
		Database database = getDatabase(cntx);
		
		StringBuffer buffer = new StringBuffer();
		buffer.append("name < '");
		Escaper.escape(buffer, start);
		buffer.append("'");
		
		Select select = database.getCount(BEANNAME);
		select.where(new RawClause(buffer));
		
		Integer i = database.getCount(select);
		return new Integer(i.intValue() - (i.intValue() % getLimit(cntx).intValue()));
	}


	/**
	 * Updatet ausschlie�lich den Namen der in der Liste angezeigt wird.
	 */
	protected int updateBeanList(OctopusContext cntx, List errors, List beanlist) throws BeanException, IOException {
		int count = 0;
		for (Iterator it = beanlist.iterator(); it.hasNext(); ) {
			MailDraft mailDraft = (MailDraft)it.next();
			if (mailDraft.isModified()) {
				getDatabase(cntx).execute(
						SQL.Update().
						table("veraweb.tmaildraft").
						update("name", mailDraft.name).
						where(Expr.equal("pk", mailDraft.id)));
				count++;
			}
		}
		return count;
	}

    //
    // Octopus-Aktionen
    //
	/** Octopus-Eingabe-Parameter f�r {@link #showDetail(OctopusContext, Integer, MailDraft)} */
	public static final String INPUT_showDetail[] = { "id", "maildraft" };
	/** Octopus-Eingabe-Parameter f�r {@link #showDetail(OctopusContext, Integer, MailDraft)} */
	public static final boolean MANDATORY_showDetail[] = { false, false };
	/** Octopus-Ausgabe-Parameter f�r {@link #showDetail(OctopusContext, Integer, MailDraft)} */
	public static final String OUTPUT_showDetail = "maildraft";
	/**
	 * L�dt einen eMail-Entwurf aus der Datenbank und stellt
	 * diesen in den Content, wenn eine ID �bergeben wurde
	 * und sich noch kein Entwurf im Content befindet.
	 * 
	 * @param cntx Octopus-Context
	 * @param id Datenbank ID
	 * @param mailDraft eMail-Entwurf aus dem Content.
	 * @return eMail-Entwurf oder null
	 * @throws BeanException 
	 * @throws IOException 
	 */
	public MailDraft showDetail(OctopusContext cntx, Integer id, MailDraft mailDraft) throws BeanException, IOException {
		if (mailDraft == null && id != null) {
			return (MailDraft)getDatabase(cntx).getBean("MailDraft", id);
		}
		return null;
	}

	/** Octopus-Eingabe-Parameter f�r {@link #saveDetail(OctopusContext, Boolean)} */
	public static final String INPUT_saveDetail[] = { "save" };
	/** Octopus-Eingabe-Parameter f�r {@link #saveDetail(OctopusContext, Boolean)} */
	public static final boolean MANDATORY_saveDetail[] = { false };
	/** Octopus-Ausgabe-Parameter f�r {@link #saveDetail(OctopusContext, Boolean)} */
	public static final String OUTPUT_saveDetail = "maildraft";
	/**
	 * Speichert den �bergebenen eMail-Entwurf bzw. l�dt diesen aus dem
	 * Request und speichert diesen im Content und in der Datenbank,
	 * wenn im Request der Parameter save auf true gesetzt ist.
	 * 
	 * @param cntx Octopus-Context
	 * @param save Gibt an ob eMail-Entwurf gespeichert werden soll.
	 * @return eMail-Entwurf
	 * @throws BeanException 
	 * @throws IOException 
	 */
	public MailDraft saveDetail(OctopusContext cntx, Boolean save) throws BeanException, IOException {
		if (save != null && save.booleanValue()) {
			MailDraft mailDraft = (MailDraft)getRequest(cntx).getBean("MailDraft", "maildraft");
			if (mailDraft.isCorrect()) {
				saveBean(cntx, mailDraft);
			}
			return mailDraft;
		}
		return null;
	}
}
