/*
 * $Id: MailinglistListWorker.java,v 1.1 2007/06/20 11:56:51 christoph Exp $
 */
package de.tarent.aa.veraweb.worker;

import java.io.IOException;

import de.tarent.aa.veraweb.beans.Mailinglist;
import de.tarent.dblayer.sql.clause.Expr;
import de.tarent.dblayer.sql.statement.Select;
import de.tarent.octopus.PersonalConfigAA;
import de.tarent.octopus.custom.beans.Bean;
import de.tarent.octopus.custom.beans.BeanException;
import de.tarent.octopus.custom.beans.veraweb.ListWorkerVeraWeb;
import de.tarent.octopus.server.OctopusContext;

/**
 * Dieser Octopus-Worker stellt eine Übersichtsliste aller Verteiler bereit.
 * 
 * @author Hendrik, Christoph Jerolimov
 * @version $Revision: 1.1 $
 */
public class MailinglistListWorker extends ListWorkerVeraWeb {
    //
    // Konstruktoren
    //
    /**
     * Der Konstruktor legt den Bean-Namen fest.
     */
	public MailinglistListWorker() {
		super("Mailinglist");
	}

    //
    // Oberklasse BeanListWorker
    //
	protected void extendWhere(OctopusContext cntx, Select select) throws BeanException, IOException {
		select.where(Expr.equal("tmailinglist.fk_orgunit", ((PersonalConfigAA)(cntx.personalConfig())).getOrgUnitId()));
	}

	protected void extendAll(OctopusContext cntx, Select select) throws BeanException, IOException {
		select.where(Expr.equal("tmailinglist.fk_orgunit", ((PersonalConfigAA)(cntx.personalConfig())).getOrgUnitId()));
	}

	protected void extendColumns(OctopusContext cntx, Select select) throws BeanException {
		select.joinLeftOuter("veraweb.tuser", "tmailinglist.fk_user", "tuser.pk");
		select.joinLeftOuter("veraweb.tevent", "tmailinglist.fk_vera", "tevent.pk");
		select.selectAs("tuser.username", "username");
		select.selectAs("tevent.shortname", "eventname");
	}

	protected void saveBean(OctopusContext cntx, Bean bean) throws BeanException, IOException {
		((Mailinglist)bean).orgunit = ((PersonalConfigAA)(cntx.personalConfig())).getOrgUnitId();
		super.saveBean(cntx, bean);
	}
}
