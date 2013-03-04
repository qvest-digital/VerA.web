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

import de.tarent.aa.veraweb.beans.Doctype;
import de.tarent.aa.veraweb.beans.Event;
import de.tarent.aa.veraweb.beans.Salutation;
import de.tarent.dblayer.sql.SQL;
import de.tarent.dblayer.sql.clause.Expr;
import de.tarent.dblayer.sql.clause.RawClause;
import de.tarent.dblayer.sql.statement.Select;
import de.tarent.octopus.beans.Bean;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.beans.Database;
import de.tarent.octopus.beans.TransactionContext;
import de.tarent.octopus.server.OctopusContext;

/**
 * Diese Octopus-Worker-Klasse stellt Operationen f�r Dokumenttypen
 * zur Verf�gung. Details bitte dem BeanListWorker entnehmen.
 * 
 * Wenn eine Veranstaltung als 'event' im Octopus-Content
 * steht, wird das Ergebnis der <code>getAll</code>-Aktion auf die
 * Dokumenttypen eingeschr�nkt die NICHT diesem Event zugeordnet sind.
 * 
 * Wenn eine Anrede als 'salutation' im Octopus-Content
 * steht, wird das Ergebnis der <code>getAll</code>-Aktion auf die
 * Dokumenttypen eingeschr�nkt die NICHT dieser Anrede zugeordnet sind.
 * 
 * @see de.tarent.octopus.beans.BeanListWorker
 * 
 * @author Christoph
 */
public class DoctypeWorker extends StammdatenWorker {
    //
    // Konstruktoren
    //
    /**
     * Der Konstruktor legt den Bean-Namen fest.
     */
	public DoctypeWorker() {
		super("Doctype");
	}

    //
    // Oberklasse BeanListWorker
    //
	@Override
    protected void extendAll(OctopusContext cntx, Select select) throws BeanException, IOException {
		Event event = (Event)cntx.contentAsObject("event");
		if (event != null) {
			select.where(new RawClause("pk NOT IN (" +
					"SELECT fk_doctype FROM veraweb.tevent_doctype WHERE fk_event = " + event.id
					+ ")"));
		}
		Salutation salutation = (Salutation)cntx.contentAsObject("salutation");
		if (salutation != null) {
			select.where(new RawClause("pk NOT IN (" +
					"SELECT fk_doctype FROM veraweb.tsalutation_doctype WHERE fk_salutation = " + salutation.id
					+ ")"));
		}
	}

	@Override
    protected void saveBean(OctopusContext cntx, Bean bean, TransactionContext context) throws BeanException, IOException {
		Doctype doctype = (Doctype)bean;
		Boolean isdefault = doctype.isdefault;
		Database database = context.getDatabase();
		if (isdefault != null && isdefault.booleanValue()) {
			context.execute(SQL.Update( database ).
					table("veraweb.tdoctype").
					update("isdefault", Boolean.FALSE));
		}
		if (doctype.id != null && doctype.flags != null && doctype.flags.intValue() == 50) {
			Doctype old = (Doctype)database.getBean(BEANNAME, doctype.id);
			if (old.flags == null || old.flags.intValue() != 50) {
				context.execute(database.getUpdate("GuestDoctype").
						update("textfield", "").
						update("textfield_p", "").
						update("textjoin", "").
						where(Expr.equal("fk_doctype", doctype.id)));
				context.execute(database.getUpdate("PersonDoctype").
						update("textfield", "").
						update("textfield_p", "").
						update("textjoin", "").
						where(Expr.equal("fk_doctype", doctype.id)));
			}
		}
		super.saveBean(cntx, bean, context);
	}
}
