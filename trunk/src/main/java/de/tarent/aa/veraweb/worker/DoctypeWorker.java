/*
 * $Id: DoctypeWorker.java,v 1.1 2007/06/20 11:56:51 christoph Exp $
 * Created on 28.02.2005
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
import de.tarent.octopus.custom.beans.Bean;
import de.tarent.octopus.custom.beans.BeanException;
import de.tarent.octopus.custom.beans.Database;
import de.tarent.octopus.server.OctopusContext;

/**
 * Diese Octopus-Worker-Klasse stellt Operationen für Dokumenttypen
 * zur Verfügung. Details bitte dem BeanListWorker entnehmen.
 * 
 * Wenn eine Veranstaltung als 'event' im Octopus-Content
 * steht, wird das Ergebnis der <code>getAll</code>-Aktion auf die
 * Dokumenttypen eingeschränkt die NICHT diesem Event zugeordnet sind.
 * 
 * Wenn eine Anrede als 'salutation' im Octopus-Content
 * steht, wird das Ergebnis der <code>getAll</code>-Aktion auf die
 * Dokumenttypen eingeschränkt die NICHT dieser Anrede zugeordnet sind.
 * 
 * @see de.tarent.octopus.custom.beans.BeanListWorker
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

	protected void saveBean(OctopusContext cntx, Bean bean) throws BeanException, IOException {
		Doctype doctype = (Doctype)bean;
		Boolean isdefault = doctype.isdefault;
		if (isdefault != null && isdefault.booleanValue()) {
			getDatabase(cntx).execute(SQL.Update().
					table("veraweb.tdoctype").
					update("isdefault", Boolean.FALSE));
		}
		if (doctype.id != null && doctype.flags != null && doctype.flags.intValue() == 50) {
			Database database = getDatabase(cntx);
			Doctype old = (Doctype)database.getBean(BEANNAME, doctype.id);
			if (old.flags == null || old.flags.intValue() != 50) {
				database.execute(database.getUpdate("GuestDoctype").
						update("textfield", "").
						update("textfield_p", "").
						update("textjoin", "").
						where(Expr.equal("fk_doctype", doctype.id)));
				database.execute(database.getUpdate("PersonDoctype").
						update("textfield", "").
						update("textfield_p", "").
						update("textjoin", "").
						where(Expr.equal("fk_doctype", doctype.id)));
			}
		}
		super.saveBean(cntx, bean);
	}
}
