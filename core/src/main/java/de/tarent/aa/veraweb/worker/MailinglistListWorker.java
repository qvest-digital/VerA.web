package de.tarent.aa.veraweb.worker;
import de.tarent.aa.veraweb.beans.Mailinglist;
import de.tarent.dblayer.sql.clause.Expr;
import de.tarent.dblayer.sql.statement.Select;
import de.tarent.octopus.PersonalConfigAA;
import de.tarent.octopus.beans.Bean;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.beans.TransactionContext;
import de.tarent.octopus.beans.veraweb.ListWorkerVeraWeb;
import de.tarent.octopus.server.OctopusContext;

import java.io.IOException;

/**
 * Dieser Octopus-Worker stellt eine übersichtsliste aller Verteiler bereit.
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
    @Override
    protected void extendWhere(OctopusContext cntx, Select select) throws BeanException, IOException {
        select.where(Expr.equal("tmailinglist.fk_orgunit", ((PersonalConfigAA) (cntx.personalConfig())).getOrgUnitId()));
    }

    @Override
    protected void extendAll(OctopusContext cntx, Select select) throws BeanException, IOException {
        select.where(Expr.equal("tmailinglist.fk_orgunit", ((PersonalConfigAA) (cntx.personalConfig())).getOrgUnitId()));
    }

    @Override
    protected void extendColumns(OctopusContext cntx, Select select) throws BeanException {
        select.joinLeftOuter("veraweb.tuser", "tmailinglist.fk_user", "tuser.pk");
        select.joinLeftOuter("veraweb.tevent", "tmailinglist.fk_vera", "tevent.pk");
        select.selectAs("tuser.username", "username");
        select.selectAs("tevent.shortname", "eventname");
    }

    @Override
    protected void saveBean(OctopusContext cntx, Bean bean, TransactionContext context) throws BeanException, IOException {
        ((Mailinglist) bean).orgunit = ((PersonalConfigAA) (cntx.personalConfig())).getOrgUnitId();
        super.saveBean(cntx, bean, context);
    }
}
