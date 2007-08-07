/* $Id: PersonCategorieWorker.java,v 1.1 2007/06/20 11:56:51 christoph Exp $ */
package de.tarent.aa.veraweb.worker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.tarent.aa.veraweb.beans.PersonCategorie;
import de.tarent.dblayer.sql.clause.Expr;
import de.tarent.dblayer.sql.statement.Select;
import de.tarent.octopus.custom.beans.Bean;
import de.tarent.octopus.custom.beans.BeanException;
import de.tarent.octopus.custom.beans.veraweb.ListWorkerVeraWeb;
import de.tarent.octopus.server.OctopusContext;

/**
 * Dieser Octopus-Worker bearbeitet Personen-Kategorien-Listen.
 */
public class PersonCategorieWorker extends ListWorkerVeraWeb {
    //
    // Konstruktoren
    //
    /**
     * Der Konstruktor legt den Bean-Namen fest.
     */
	public PersonCategorieWorker() {
		super("PersonCategorie");
	}

    //
    // Oberklasse BeanListWorker
    //
	protected void extendWhere(OctopusContext cntx, Select select) throws BeanException, IOException {
		Bean bean = (Bean)cntx.contentAsObject("person");
		select.where(Expr.equal("fk_person", bean.getField("id")));
	}

	protected void extendColumns(OctopusContext cntx, Select select) throws BeanException, IOException {
		select.join("veraweb.tcategorie", "fk_categorie", "tcategorie.pk");
		select.selectAs("tcategorie.rank", "catrank");
		select.selectAs("tcategorie.catname", "name");
		select.selectAs("tcategorie.flags", "flags");
	}

	public void saveList(OctopusContext cntx) throws BeanException, IOException {
		super.saveList(cntx);
		
		String addRank = cntx.requestAsString("add-rank");
		String addCategorie = cntx.requestAsString("add-categorie");
		if (addRank != null && addRank.length() != 0 && (addCategorie == null || addCategorie.length() == 0)) {
			List errors = (List)cntx.contentAsObject(OUTPUT_saveListErrors);
			if (errors == null) {
				errors = new ArrayList();
				cntx.setContent(OUTPUT_saveListErrors, errors);
			}
			errors.add("Um eine neue Kategorie hinzuzuf&uuml;gen w&auml;hlen" +
					" Sie bitte eine Kategorie aus. " +
					"(Sie haben nur einen Rang eingegeben.)");
		}
	}

	protected void saveBean(OctopusContext cntx, Bean bean) throws BeanException, IOException {
		super.saveBean(cntx, bean);
		WorkerFactory.getPersonDetailWorker(cntx).updatePerson(cntx, null, ((PersonCategorie)bean).person);
	}
}
