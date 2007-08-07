/*
 * $Id: MailinglistDetailWorker.java,v 1.1 2007/06/20 11:56:51 christoph Exp $
 */
package de.tarent.aa.veraweb.worker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import de.tarent.aa.veraweb.beans.Mailinglist;
import de.tarent.dblayer.sql.clause.Expr;
import de.tarent.dblayer.sql.clause.Order;
import de.tarent.dblayer.sql.statement.Select;
import de.tarent.octopus.PersonalConfigAA;
import de.tarent.octopus.custom.beans.BeanException;
import de.tarent.octopus.custom.beans.Database;
import de.tarent.octopus.custom.beans.Request;
import de.tarent.octopus.custom.beans.veraweb.ListWorkerVeraWeb;
import de.tarent.octopus.server.OctopusContext;

/**
 * Dieser Octopus-Worker stellt die Übersichtliste eines Verteilers bereit.
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
	protected void extendColumns(OctopusContext cntx, Select select) throws BeanException {
		select.joinLeftOuter("veraweb.tperson", "fk_person", "tperson.pk");
		select.selectAs("lastname_a_e1", "lastname");
		select.selectAs("firstname_a_e1", "firstname");
		select.orderBy(Order.asc("lastname").andAsc("firstname"));
	}

	protected void extendWhere(OctopusContext cntx, Select select) throws BeanException {
		Mailinglist mailinglist = (Mailinglist)cntx.contentAsObject("mailinglist");
		select.where(Expr.equal("fk_mailinglist", mailinglist.id));
	}

	protected List getResultList(Database database, Select select) throws BeanException, IOException {
		return database.getList(select);
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
     * @param cntx Octopus-Kontext
	 */
	public void showDetail(OctopusContext cntx) throws BeanException, IOException {
		Database database = getDatabase(cntx);
		
		Integer id = cntx.requestAsInteger("id");
		Mailinglist mailinglist = (Mailinglist)
				database.getBean("Mailinglist",
				database.getSelect("Mailinglist").
				selectAs("tuser.username", "username").
				selectAs("tevent.shortname", "eventname").
				joinLeftOuter("veraweb.tuser", "tmailinglist.fk_user", "tuser.pk").
				joinLeftOuter("veraweb.tevent", "tmailinglist.fk_vera", "tevent.pk").
				where(Expr.equal("tmailinglist.pk", id)));
		if (mailinglist == null) {
			mailinglist = (Mailinglist)cntx.sessionAsObject("mailinglist");
		}
		cntx.setContent("mailinglist", mailinglist);
		cntx.setSession("mailinglist", mailinglist);
	}

    /** Eingabe-Parameter der Octopus-Aktion {@link #saveDetail(OctopusContext)} */
	public static final String INPUT_saveDetail[] = {};
	/**
     * Diese Octopus-Aktion liest eine Mailinglist aus dem Octopus-Request,
     * legt diese unter dem Schlüssel "mailinglist" in Octopus-Content und
     * -Session ab und testet sie auf Korrektheit. Falls sie korrekt ist,
     * wird sie in der Datenbank gespeichert, ansonsten wird der Status
     * "error" gesetzt.
     * 
     * @param cntx Octopus-Kontext
	 */
	public void saveDetail(OctopusContext cntx) throws BeanException, IOException {
		Database database = getDatabase(cntx);
		Request request = getRequest(cntx);
		
		Mailinglist mailinglist = (Mailinglist)request.getBean("Mailinglist", "mailinglist");
		mailinglist.updateHistoryFields(null, ((PersonalConfigAA)cntx.configImpl()).getRoleWithProxy());
		mailinglist.user = ((PersonalConfigAA)cntx.configImpl()).getVerawebId();
		mailinglist.orgunit = ((PersonalConfigAA)cntx.configImpl()).getOrgUnitId();
		
		cntx.setContent("mailinglist", mailinglist);
		cntx.setSession("mailinglist", mailinglist);
		
		if (mailinglist.isCorrect()) {
			database.saveBean(mailinglist);
		} else {
			cntx.setStatus("error");
			return;
		}
	}

    /** Eingabe-Parameter der Octopus-Aktion {@link #getAddressList(OctopusContext, Mailinglist, Integer)} */
	public static final String INPUT_getAddressList[] = { "CONTENT:mailinglist", "CONFIG:mailToUrlMaxSize" };
    /** Ausgabe-Parameter der Octopus-Aktion {@link #getAddressList(OctopusContext, Mailinglist, Integer)} */
	public static final String OUTPUT_getAddressList = "mailAddresses";
	/**
     * Diese Octopus-Aktion liefert eine Liste mit mailto-URLs, die jeweils nicht länger als
     * die übergebene Vorgabelänge sind, und die zusammengenommen alle Einträge der Mailinglist
     * mit E-Mail-Adresse adressiert.
     * 
     * @param cntx Octopus-Kontext
     * @param mailinglist Mailingliste
     * @param mailToUrlMaxSize Maximallänge einer mailto-URL
     * @return Liste mit mailto-URLs zu der Mailingliste
     * @throws BeanException
     * @throws IOException
	 */
	public List getAddressList(OctopusContext cntx, Mailinglist mailinglist, Integer mailToUrlMaxSize) throws BeanException, IOException {
		Database database = getDatabase(cntx);
		
		Select select = database.getSelect(BEANNAME);
		select.where(Expr.equal("fk_mailinglist", mailinglist.id));
		select.join("veraweb.tperson", "fk_person", "tperson.pk");
		
		// Adressen holen
		logger.info("Hole Adressen");
		List addressList = new ArrayList();
		StringBuffer addresses = new StringBuffer();
		boolean first = true;
		
		for (Iterator it = database.getList(select).iterator(); it.hasNext(); ) {
			Map data = (Map)it.next();
			String str = (String)(data).get("address");
			
			if (str != null && str.length() != 0) {
				// Länge der URL darf nicht zu groß werden 
				if (mailToUrlMaxSize.intValue() != -1 && !first &&
						addresses.length() + str.length() + 5 > mailToUrlMaxSize.intValue()) {
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
