/**
 * veraweb, platform independent webservice-based event management
 * (Veranstaltungsmanagment VerA.web), is
 * Copyright © 2004–2008 tarent GmbH
 * Copyright © 2013–2016 tarent solutions GmbH
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

import de.tarent.aa.veraweb.beans.PdfTemplate;
import de.tarent.dblayer.sql.Escaper;
import de.tarent.dblayer.sql.SQL;
import de.tarent.dblayer.sql.clause.Expr;
import de.tarent.dblayer.sql.clause.RawClause;
import de.tarent.dblayer.sql.statement.Select;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.beans.Database;
import de.tarent.octopus.beans.TransactionContext;
import de.tarent.octopus.beans.veraweb.DatabaseVeraWeb;
import de.tarent.octopus.beans.veraweb.ListWorkerVeraWeb;
import de.tarent.octopus.server.OctopusContext;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class PdfTemplateWorker extends ListWorkerVeraWeb {

	public PdfTemplateWorker() {
		super("PdfTemplate");
	}

	@Override
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
	 * Updatet ausschließlich den Namen der in der Liste angezeigt wird.
	 */
	@Override
    protected int updateBeanList(OctopusContext octopusContext, List errors, List beanlist, TransactionContext transactionContext) throws BeanException, IOException {
		int count = 0;
		for (Iterator it = beanlist.iterator(); it.hasNext(); ) {
			PdfTemplate mailDraft = (PdfTemplate)it.next();
			if (mailDraft.isModified()) {
				Database db = transactionContext.getDatabase();
				transactionContext.execute(
						SQL.Update( db ).
						table("veraweb.tmaildraft").
						update("name", mailDraft.name).
						where(Expr.equal("pk", mailDraft.id)));
				count++;
				transactionContext.commit();
			}
		}
		return count;
	}

    //
    // Octopus-Aktionen
    //
	/** Octopus-Eingabe-Parameter für {@link #showDetail(OctopusContext, Integer, PdfTemplate)} */
	public static final String INPUT_showDetail[] = { "id", "maildraft" };
	/** Octopus-Eingabe-Parameter für {@link #showDetail(OctopusContext, Integer, PdfTemplate)} */
	public static final boolean MANDATORY_showDetail[] = { false, false };
	/** Octopus-Ausgabe-Parameter für {@link #showDetail(OctopusContext, Integer, PdfTemplate)} */
	public static final String OUTPUT_showDetail = "maildraft";
	/**
	 * Lädt einen eMail-Entwurf aus der Datenbank und stellt
	 * diesen in den Content, wenn eine ID übergeben wurde
	 * und sich noch kein Entwurf im Content befindet.
	 *
	 * @param cntx Octopus-Context
	 * @param id Datenbank ID
	 * @param mailDraft eMail-Entwurf aus dem Content.
	 * @return eMail-Entwurf oder null
	 * @throws BeanException
	 * @throws IOException
	 */
	public PdfTemplate showDetail(OctopusContext cntx, Integer id, PdfTemplate mailDraft) throws BeanException, IOException {
		if (mailDraft == null && id != null) {
			return (PdfTemplate)getDatabase(cntx).getBean("PdfTemplate", id);
		}
		return mailDraft;
	}

	/** Octopus-Eingabe-Parameter für {@link #saveDetail(OctopusContext, Boolean)} */
	public static final String INPUT_saveDetail[] = { "save" };
	/** Octopus-Eingabe-Parameter für {@link #saveDetail(OctopusContext, Boolean)} */
	public static final boolean MANDATORY_saveDetail[] = { false };
	/** Octopus-Ausgabe-Parameter für {@link #saveDetail(OctopusContext, Boolean)} */
	public static final String OUTPUT_saveDetail = "maildraft";
	/**
	 * Speichert den übergebenen eMail-Entwurf bzw. lädt diesen aus dem
	 * Request und speichert diesen im Content und in der Datenbank,
	 * wenn im Request der Parameter save auf true gesetzt ist.
	 *
	 * @param octopusContext Octopus-Context
	 * @param save Gibt an ob eMail-Entwurf gespeichert werden soll.
	 * @return eMail-Entwurf
	 * @throws BeanException
	 * @throws IOException
	 */
	public PdfTemplate saveDetail(final OctopusContext octopusContext, final Boolean save) throws BeanException, IOException {
		if (save != null && save.booleanValue()) {
			PdfTemplate pdfTemplate = (PdfTemplate)getRequest(octopusContext).getBean("PdfTemplate", "pdftemplate");
			TransactionContext transactionContext = new DatabaseVeraWeb(octopusContext).getTransactionContext();
            pdfTemplate.verify(octopusContext);

			if (pdfTemplate.isCorrect()) {
				handleSave(octopusContext, pdfTemplate, transactionContext);
			}
			return pdfTemplate;
		}
		return null;
	}

	private void handleSave(OctopusContext octopusContext, PdfTemplate pdfTemplate, TransactionContext transactionContext) throws BeanException {
		try {
            if (pdfTemplate.id == null) {
                octopusContext.setContent("countInsert", new Integer(1));
            } else {
                octopusContext.setContent("countUpdate", new Integer(1));
            }

			saveBean(octopusContext, pdfTemplate, transactionContext);
			transactionContext.commit();
        } catch ( Throwable e ) {
            transactionContext.rollBack();
            throw new BeanException( "Die PDF-Vorlage konnte nicht gespeichert werden.", e );
        }
	}
}
