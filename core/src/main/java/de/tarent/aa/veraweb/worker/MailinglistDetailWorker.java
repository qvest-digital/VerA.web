package de.tarent.aa.veraweb.worker;

/*-
 * Veranstaltungsmanagement VerA.web (platform-independent
 * webservice-based event management) is Copyright
 *  © 2014, 2015, 2016, 2017 Атанас Александров <a.alexandrov@tarent.de>
 *  © 2018 Атанас Александров <sirakov@gmail.com>
 *  © 2013 Иванка Александрова <i.alexandrova@tarent.de>
 *  © 2013 Patrick Apel <p.apel@tarent.de>
 *  © 2016 Eugen Auschew <e.auschew@tarent.de>
 *  © 2013 Andrei Boulgakov <a.boulgakov@tarent.de>
 *  © 2013 Valentin But <v.but@tarent.de>
 *  © 2016 Lukas Degener <l.degener@tarent.de>
 *  © 2017 Axel Dirla <a.dirla@tarent.de>
 *  © 2015 Julian Drawe <j.drawe@tarent.de>
 *  © 2014 Dominik George <d.george@tarent.de>
 *  © 2013 Sascha Girrulat <s.girrulat@tarent.de>
 *  © 2008 David Goemans <d.goemans@tarent.de>
 *  © 2015 Viktor Hamm <v.hamm@tarent.de>
 *  © 2013 Katja Hapke <k.hapke@tarent.de>
 *  © 2013 Hendrik Helwich <h.helwich@tarent.de>
 *  © 2007 jan <jan@evolvis.org>
 *  © 2005, 2006, 2007, 2008 Christoph Jerolimov <jerolimov@gmx.de>
 *  © 2008, 2009, 2010 Carsten Klein <c.klein@tarent.de>
 *  © 2014 Martin Ley <m.ley@tarent.de>
 *  © 2014, 2015 Max Marche <m.marche@tarent.de>
 *  © 2013, 2014, 2015, 2016, 2017, 2018 mirabilos <t.glaser@tarent.de>
 *  © 2016 Cristian Molina <c.molina@tarent.de>
 *  © 2017 Michael Nienhaus <m.nienhaus@tarent.de>
 *  © 2013 Claudia Nuessle <c.nuessle@tarent.de>
 *  © 2014, 2015 Jon Nunez Alvarez <j.nunez-alvarez@tarent.de>
 *  © 2016 Jens Oberender <j.oberender@tarent.de>
 *  © 2016, 2017 Miluška Pech <m.pech@tarent.de>
 *  © 2009 Martin Pelzer <m.pelzer@tarent.de>
 *  © 2013 Marc Radel <m.radel@tarent.de>
 *  © 2013 Sebastian Reimers <s.reimers@tarent.de>
 *  © 2015 Charbel Saliba <c.saliba@tarent.de>
 *  © 2008, 2009, 2010 Thomas Schmitz <t.schmitz@tarent.de>
 *  © 2013 Volker Schmitz <v.schmitz@tarent.de>
 *  © 2014 Sven Schumann <s.schumann@tarent.de>
 *  © 2014 Sevilay Temiz <s.temiz@tarent.de>
 *  © 2013 Kevin Viola Schmitz <k.schmitz@tarent.de>
 *  © 2015 Stefan Walenda <s.walenda@tarent.de>
 *  © 2015, 2016, 2017 Max Weierstall <m.weierstall@tarent.de>
 *  © 2013 Rebecca Weinz <r.weinz@tarent.de>
 *  © 2015, 2016 Stefan Weiz <s.weiz@tarent.de>
 *  © 2015, 2016 Tim Zimmer <t.zimmer@tarent.de>
 * and older code, Copyright © 2004–2008 ⮡ tarent GmbH and contributors.
 * Licensor is tarent solutions GmbH, http://www.tarent.de/
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
public class MailinglistDetailWorker extends ListWorkerVeraWeb {
    /**
     * Logger dieser Klasse
     */
    public static Logger logger = LogManager.getLogger(MailinglistDetailWorker.class.getName());
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
     * @throws BeanException
     * @throws IOException
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
                if (MAX_MAIL_TO_LENGTH.intValue() != -1 && !first && addresses.length() + str.length() + 5 > MAX_MAIL_TO_LENGTH.intValue()) {
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
