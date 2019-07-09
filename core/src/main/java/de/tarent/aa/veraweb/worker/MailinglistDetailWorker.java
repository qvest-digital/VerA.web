package de.tarent.aa.veraweb.worker;
import de.tarent.aa.veraweb.beans.Mailinglist;
import de.tarent.aa.veraweb.utils.VworUtils;
import de.tarent.dblayer.sql.clause.Expr;
import de.tarent.dblayer.sql.clause.Order;
import de.tarent.dblayer.sql.statement.Select;
import de.tarent.octopus.PersonalConfigAA;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.beans.Database;
import de.tarent.octopus.beans.Request;
import de.tarent.octopus.beans.veraweb.ListWorkerVeraWeb;
import de.tarent.octopus.server.OctopusContext;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Dieser Octopus-Worker stellt die übersichtliste eines Verteilers bereit.
 *
 * @author Hendrik, Christoph Jerolimov
 * @version $Revision: 1.1 $
 */
@Log4j2
public class MailinglistDetailWorker extends ListWorkerVeraWeb {
    private Integer MAX_MAIL_TO_LENGTH = -1;

    //
    // Konstruktoren
    //

    /**
     * Der Konstruktor legt den Bean-Namen fest.
     */
    public MailinglistDetailWorker() {
        super("MailinglistAddress");
    }

    //
    // Oberklasse BeanListWorker
    //
    @Override
    protected void extendColumns(final OctopusContext cntx, final Select select) {
        select.joinLeftOuter("veraweb.tperson", "fk_person", "tperson.pk");
        select.selectAs("lastname_a_e1", "lastname");
        select.selectAs("firstname_a_e1", "firstname");
        select.orderBy(Order.asc("lastname").andAsc("firstname"));
    }

    @Override
    protected void extendWhere(final OctopusContext cntx, final Select select) {
        final Mailinglist mailinglist = (Mailinglist) cntx.contentAsObject("mailinglist");
        select.where(Expr.equal("fk_mailinglist", mailinglist.id));
    }

    @Override
    protected List getResultList(final Database database, final Select select) throws BeanException {
        return database.getList(select, database);
    }

    //
    // Octopus-Aktionen
    //
    /**
     * Eingabe-Parameter der Octopus-Aktion {@link #showDetail(OctopusContext)}
     */
    public static final String INPUT_showDetail[] = {};

    /**
     * Diese Octopus-Aktion schreibt die Details zur Mailinglist mit dem im
     * Octopus-Request unter dem Schlüssel "id" angegebenen Primärschlüssel
     * unter "mailinglist" in Octopus-Content und -Session.
     *
     * @param octopusContext Octopus-Kontext
     * @throws BeanException BeanException
     * @throws IOException   IOException
     */
    public void showDetail(final OctopusContext octopusContext) throws BeanException, IOException {
        final Database database = getDatabase(octopusContext);

        final Integer id = octopusContext.requestAsInteger("id");
        Mailinglist mailinglist = (Mailinglist) database.getBean(
          "Mailinglist",
          database.getSelect("Mailinglist").selectAs("tuser.username", "username").selectAs("tevent.shortname", "eventname")
            .joinLeftOuter("veraweb.tuser", "tmailinglist.fk_user", "tuser.pk")
            .joinLeftOuter("veraweb.tevent", "tmailinglist.fk_vera", "tevent.pk")
            .where(Expr.equal("tmailinglist.pk", id)));
        if (mailinglist == null) {
            mailinglist = (Mailinglist) octopusContext.sessionAsObject("mailinglist");
        }
        octopusContext.setContent("vworendpoint", new VworUtils().getVworEndPoint());
        octopusContext.setContent("mailinglist", mailinglist);
        octopusContext.setSession("mailinglist", mailinglist);
    }

    /**
     * Eingabe-Parameter der Octopus-Aktion {@link #saveDetail(OctopusContext)}
     */
    public static final String INPUT_saveDetail[] = {};

    /**
     * Diese Octopus-Aktion liest eine Mailinglist aus dem Octopus-Request, legt
     * diese unter dem Schlüssel "mailinglist" in Octopus-Content und -Session
     * ab und testet sie auf Korrektheit. Falls sie korrekt ist, wird sie in der
     * Datenbank gespeichert, ansonsten wird der Status "error" gesetzt.
     *
     * @param octopusContext Octopus-Kontext
     * @throws Exception which one? My guess is as godd as yours!
     */
    public void saveDetail(final OctopusContext octopusContext) throws Exception {
        final Database database = getDatabase(octopusContext);
        final Request request = getRequest(octopusContext);

        try {
            final Mailinglist mailinglist = (Mailinglist) request.getBean("Mailinglist", "mailinglist");
            mailinglist.updateHistoryFields(((PersonalConfigAA) octopusContext.personalConfig()).getRoleWithProxy());
            mailinglist.user = ((PersonalConfigAA) octopusContext.personalConfig()).getVerawebId();
            mailinglist.orgunit = ((PersonalConfigAA) octopusContext.personalConfig()).getOrgUnitId();
            mailinglist.created = new Timestamp(new Date().getTime());

            octopusContext.setContent("mailinglist", mailinglist);
            octopusContext.setSession("mailinglist", mailinglist);

            mailinglist.verify(octopusContext);

            if (mailinglist.isCorrect()) {
                database.saveBean(mailinglist);
            } else {
                octopusContext.setStatus("error");
                return;
            }
        } catch (final Exception e) {
            logger.error("Could not save mailinglist", e);
            throw e;
        }
    }

    /**
     * Eingabe-Parameter der Octopus-Aktion
     * {@link #getAddressList(OctopusContext, Mailinglist)}
     */
    public static final String INPUT_getAddressList[] = { "CONTENT:mailinglist" };
    /**
     * Ausgabe-Parameter der Octopus-Aktion
     * {@link #getAddressList(OctopusContext, Mailinglist)}
     */
    public static final String OUTPUT_getAddressList = "mailAddresses";

    /**
     * Diese Octopus-Aktion liefert eine Liste mit mailto-URLs, die jeweils
     * nicht länger als die übergebene Vorgabelänge sind, und die
     * zusammengenommen alle Einträge der Mailinglist mit E-Mail-Adresse
     * adressiert.
     *
     * @param cntx        Octopus-Kontext
     * @param mailinglist Mailingliste
     * @return Liste mit mailto-URLs zu der Mailingliste
     * @throws BeanException BeanException
     * @throws IOException   IOException
     */
    public List getAddressList(final OctopusContext cntx, final Mailinglist mailinglist) throws BeanException,
      IOException {
        final Database database = getDatabase(cntx);

        final Select select = database.getSelect(BEANNAME);
        select.where(Expr.equal("fk_mailinglist", mailinglist.id));
        select.join("veraweb.tperson", "fk_person", "tperson.pk");

        // Adressen holen
        logger.info("Hole Adressen");
        final List addressList = new ArrayList();
        StringBuffer addresses = new StringBuffer();
        boolean first = true;

        for (final Iterator it = database.getList(select, database).iterator(); it.hasNext(); ) {
            final Map data = (Map) it.next();
            final String str = (String) (data).get("address");

            if (str != null && str.length() != 0) {
                // Länge der URL darf nicht zu groß werden
                if (MAX_MAIL_TO_LENGTH.intValue() != -1 && !first &&
                  addresses.length() + str.length() + 5 > MAX_MAIL_TO_LENGTH.intValue()) {
                    addressList.add(addresses.toString());
                    addresses = new StringBuffer();
                    first = true;
                }
                // eMail einfügen
                if (first) {
                    first = false;
                    addresses.append("mailto:?bcc=");
                } else {
                    addresses.append(',');
                }
                addresses.append(str);
            }
        }
        addressList.add(addresses.toString());
        return addressList;
    }
}
