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
import de.tarent.dblayer.sql.clause.Expr;
import de.tarent.dblayer.sql.statement.Select;
import de.tarent.octopus.PersonalConfigAA;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.beans.Database;
import de.tarent.octopus.beans.TransactionContext;
import de.tarent.octopus.beans.veraweb.DatabaseVeraWeb;
import de.tarent.octopus.beans.veraweb.ListWorkerVeraWeb;
import de.tarent.octopus.server.OctopusContext;

import java.io.IOException;

public class PdfTemplateWorker extends ListWorkerVeraWeb {

	public PdfTemplateWorker() {
		super("PdfTemplate");
	}

	public static final String INPUT_showDetail[] = { "id", "pdftemplate" };
	public static final boolean MANDATORY_showDetail[] = { false, false };
	public static final String OUTPUT_showDetail = "pdftemplate";

	public PdfTemplate showDetail(OctopusContext octopusContext, Integer id, PdfTemplate pdfTemplate) throws BeanException, IOException {
        final Database database = getDatabase(octopusContext);
        if (pdfTemplate == null && id != null) {
            return (PdfTemplate)getDatabase(octopusContext).getBean("PdfTemplate", id);
		}
		return (PdfTemplate) database.getBean("PdfTemplate", id);
	}

    @Override
	protected void extendWhere(OctopusContext octopusContext, Select select) throws BeanException, IOException {
		select.where(Expr.equal("pdftemplate.fk_orgunit", ((PersonalConfigAA)(octopusContext.personalConfig())).getOrgUnitId()));
	}

	public static final String INPUT_saveDetail[] = { "save" };
	public static final boolean MANDATORY_saveDetail[] = { false };
	public static final String OUTPUT_saveDetail = "pdftemplate";
	/**
	 * Speichert den übergebenen eMail-Entwurf bzw. lädt diesen aus dem
	 * Request und speichert diesen im Content und in der Datenbank,
	 * wenn im Request der Parameter save auf true gesetzt ist.
	 *
	 * @param octopusContext Octopus-Context
	 * @param save Gibt an ob eMail-Entwurf gespeichert werden soll.
	 * @return eMail-Entwurf
	 * @throws BeanException
	 */
	public PdfTemplate saveDetail(final OctopusContext octopusContext, final Boolean save) throws BeanException {
		if (save != null && save) {
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
                octopusContext.setContent("countInsert", 1);
            } else {
                octopusContext.setContent("countUpdate", 1);
            }

			saveBean(octopusContext, pdfTemplate, transactionContext);
			transactionContext.commit();
        } catch ( Throwable e ) {
            transactionContext.rollBack();
            throw new BeanException( "Die PDF-Vorlage konnte nicht gespeichert werden.", e );
        }
	}
}
