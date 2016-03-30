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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

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

/**
 * Dieser Octopus-Worker stellt die übersichtliste eines Verteilers bereit.
 *
 * @author Hendrik, Christoph Jerolimov
 * @version $Revision: 1.1 $
 */
public class MailinglistDetailWorker extends ListWorkerVeraWeb {
    /** Logger dieser Klasse */
    public static Logger logger = Logger.getLogger(MailinglistDetailWorker.class.getName());

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
    protected void extendColumns(final OctopusContext cntx, final Select select) throws BeanException {
        select.joinLeftOuter("veraweb.tperson", "fk_person", "tperson.pk");
        select.selectAs("lastname_a_e1", "lastname");
        select.selectAs("firstname_a_e1", "firstname");
        select.orderBy(Order.asc("lastname").andAsc("firstname"));
    }

    @Override
    protected void extendWhere(final OctopusContext cntx, final Select select) throws BeanException {
        final Mailinglist mailinglist = (Mailinglist) cntx.contentAsObject("mailinglist");
        select.where(Expr.equal("fk_mailinglist", mailinglist.id));
    }

    @Override
    protected List getResultList(final Database database, final Select select) throws BeanException, IOException {
        return database.getList(select, database);
    }

    //
    // Octopus-Aktionen
    //
    /** Eingabe-Parameter der Octopus-Aktion {@link #showDetail(OctopusContext)} */
    public static final String INPUT_showDetail[] = {};

    /**
     * Diese Octopus-Aktion schreibt die Details zur Mailinglist mit dem im
     * Octopus-Request unter dem Schlüssel "id" angegebenen Primärschlüssel
     * unter "mailinglist" in Octopus-Content und -Session.
     *
     * @param octopusContext
     *            Octopus-Kontext
     */
    public void showDetail(final OctopusContext octopusContext) throws BeanException, IOException {
        final Database database = getDatabase(octopusContext);

        final Integer id = octopusContext.requestAsInteger("id");
        Mailinglist mailinglist = (Mailinglist) database.getBean(
                "Mailinglist",
                database.getSelect("Mailinglist").selectAs("tuser.username", "username").selectAs("tevent.shortname", "eventname")
                .joinLeftOuter("veraweb.tuser", "tmailinglist.fk_user", "tuser.pk")
                .joinLeftOuter("veraweb.tevent", "tmailinglist.fk_vera", "tevent.pk").where(Expr.equal("tmailinglist.pk", id)));
        if (mailinglist == null) {
            mailinglist = (Mailinglist) octopusContext.sessionAsObject("mailinglist");
        }
        octopusContext.setContent("vworendpoint", new VworUtils().getVworEndPoint());
        octopusContext.setContent("mailinglist", mailinglist);
        octopusContext.setSession("mailinglist", mailinglist);
    }

    /** Eingabe-Parameter der Octopus-Aktion {@link #saveDetail(OctopusContext)} */
    public static final String INPUT_saveDetail[] = {};

    /**
     * Diese Octopus-Aktion liest eine Mailinglist aus dem Octopus-Request, legt
     * diese unter dem Schlüssel "mailinglist" in Octopus-Content und -Session
     * ab und testet sie auf Korrektheit. Falls sie korrekt ist, wird sie in der
     * Datenbank gespeichert, ansonsten wird der Status "error" gesetzt.
     *
     * @param octopusContext
     *            Octopus-Kontext
     */
    public void saveDetail(final OctopusContext octopusContext) throws BeanException, IOException {
        final Database database = getDatabase(octopusContext);
        final Request request = getRequest(octopusContext);

        final Mailinglist mailinglist = (Mailinglist) request.getBean("Mailinglist", "mailinglist");
        mailinglist.updateHistoryFields(null, ((PersonalConfigAA) octopusContext.personalConfig()).getRoleWithProxy());
        mailinglist.user = ((PersonalConfigAA) octopusContext.personalConfig()).getVerawebId();
        mailinglist.orgunit = ((PersonalConfigAA) octopusContext.personalConfig()).getOrgUnitId();

        octopusContext.setContent("mailinglist", mailinglist);
        octopusContext.setSession("mailinglist", mailinglist);

        mailinglist.verify(octopusContext);

        if (mailinglist.isCorrect()) {
            database.saveBean(mailinglist);
        } else {
            octopusContext.setStatus("error");
            return;
        }
    }

    /**
     * Eingabe-Parameter der Octopus-Aktion
     * {@link #getAddressList(OctopusContext, Mailinglist, Integer)}
     */
    public static final String INPUT_getAddressList[] = { "CONTENT:mailinglist", "CONFIG:mailToUrlMaxSize" };
    /**
     * Ausgabe-Parameter der Octopus-Aktion
     * {@link #getAddressList(OctopusContext, Mailinglist, Integer)}
     */
    public static final String OUTPUT_getAddressList = "mailAddresses";

    /**
     * Diese Octopus-Aktion liefert eine Liste mit mailto-URLs, die jeweils
     * nicht länger als die übergebene Vorgabelänge sind, und die
     * zusammengenommen alle Einträge der Mailinglist mit E-Mail-Adresse
     * adressiert.
     *
     * @param cntx
     *            Octopus-Kontext
     * @param mailinglist
     *            Mailingliste
     * @param mailToUrlMaxSize
     *            Maximallänge einer mailto-URL
     * @return Liste mit mailto-URLs zu der Mailingliste
     * @throws BeanException
     * @throws IOException
     */
    public List getAddressList(final OctopusContext cntx, final Mailinglist mailinglist, final Integer mailToUrlMaxSize) throws BeanException,
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

        for (final Iterator it = database.getList(select, database).iterator(); it.hasNext();) {
            final Map data = (Map) it.next();
            final String str = (String) (data).get("address");

            if (str != null && str.length() != 0) {
                // Länge der URL darf nicht zu groß werden
                if (mailToUrlMaxSize.intValue() != -1 && !first && addresses.length() + str.length() + 5 > mailToUrlMaxSize.intValue()) {
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
