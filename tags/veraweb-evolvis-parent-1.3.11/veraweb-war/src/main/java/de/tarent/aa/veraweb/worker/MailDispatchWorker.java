/**
 * veraweb, platform independent webservice-based event management
 * (Veranstaltungsmanagment VerA.web), is
 * Copyright © 2004-2008 tarent GmbH
 * Copyright © 2013 tarent solutions GmbH
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License, version 2, as
 * published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see: http://www.gnu.org/licenses/
 */
package de.tarent.aa.veraweb.worker;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import de.tarent.aa.veraweb.beans.MailDraft;
import de.tarent.aa.veraweb.beans.MailOutbox;
import de.tarent.aa.veraweb.beans.Mailinglist;
import de.tarent.aa.veraweb.beans.Person;
import de.tarent.aa.veraweb.beans.facade.PersonDoctypeFacade;
import de.tarent.aa.veraweb.utils.MailDispatcher;
import de.tarent.dblayer.engine.DB;
import de.tarent.dblayer.engine.Result;
import de.tarent.dblayer.helper.ResultList;
import de.tarent.dblayer.sql.SQL;
import de.tarent.dblayer.sql.clause.Expr;
import de.tarent.dblayer.sql.clause.Limit;
import de.tarent.dblayer.sql.clause.Order;
import de.tarent.dblayer.sql.clause.Where;
import de.tarent.dblayer.sql.statement.Select;
import de.tarent.octopus.PersonalConfigAA;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.beans.Database;
import de.tarent.octopus.beans.ExecutionContext;
import de.tarent.octopus.beans.Request;
import de.tarent.octopus.beans.TransactionContext;
import de.tarent.octopus.beans.veraweb.DatabaseVeraWeb;
import de.tarent.octopus.beans.veraweb.RequestVeraWeb;
import de.tarent.octopus.server.OctopusContext;

/**
 * Octopus-Worker der das versenden von eMails verwaltet.
 * Diese werden im Hintergrund an einen SMTP-Server versendet.
 * 
 * @see MailDispatcher
 * 
 * @author Christoph Jerolimov
 * @version $Revision: 1.1 $
 */
public class MailDispatchWorker implements Runnable {
	/** Log4J Logger Instanz */
	private final Logger logger = Logger.getLogger(MailDispatchWorker.class);
	/** Regex Pattern zur erkennung von eMail Tags. */
	private final Pattern mailtags = Pattern.compile("<[A-Za-z]+>");

	/** Modulnamen f�r den Datenbank-Pool */
	protected String moduleName;
	/** Thread in dem das eigentliche versenden ausgef�hrt wird. */
	protected Thread thread;
	/** MailDispatcher der den Versand an den SMTP-Server vornimmt. */
	protected MailDispatcher dispatcher = new MailDispatcher();
	/** Gibt an ob der Dispatcher weitere eMails verschicken soll. */
	protected boolean keeprunning = false;
	/** Gibt an ob der Dispatcher gerade eMails verschickt. */
	protected boolean isworking = false;
	/** Gibt die Wartezeit zwischen zwei Dispatch aufrufen an. */
	protected int waitMillis = 0;

	private OctopusContext cntx;

	//
	// OCTOPUS-AKTIONEN ZUM STARTEN UND STOPPEN DES VERSENDE THREADS.
	//

	/** Octopus-Eingabe-Parameter f�r {@link #load(OctopusContext)} */
	public static final String INPUT_load[] = {};
	/**
	 * Startet das automatische versenden von eMails im Hintergrund.
	 * 
	 * @param cntx Octopus-Context
	 */
	public void load(OctopusContext cntx) {
		
		this.cntx = cntx;
		
		logger.info("MailDispatcher wird im Hintergrund gestartet.");
		
		moduleName = cntx.getModuleName();
		
		Map settings = (Map)cntx.moduleConfig().getParamAsObject("mailServer");
		if (settings != null) {
			// Settingsladen
			dispatcher.setHost((String)settings.get("host"));
			try {
				waitMillis = new Integer((String)settings.get("waitBetweenJobs")).intValue() * 1000;
			} catch (NumberFormatException e) {
				logger.warn("MailDispatcher konnte Parameter 'waitBetweenJobs' nicht parsen (" + settings.get("waitBetweenJobs") + "). Es wird der Default Wert (60 Sekunden) verwendet.");
				waitMillis = 60000; // default: 1 Minute
			} catch (NullPointerException e) {
				logger.warn("MailDispatcher konnte Parameter 'waitBetweenJobs' nicht finden. Es wird der Default Wert (60 Sekunden) verwendet.");
				waitMillis = 60000; // default: 1 Minute
			}
			
			// Server status
			if (!keeprunning) {
				keeprunning = true;
				thread = new Thread(this);
				thread.start();
			}
		} else {
			unload(cntx);
		}
	}

	/** Octopus-Eingabe-Parameter f�r {@link #unload(OctopusContext)} */
	public static final String INPUT_unload[] = {};
	/**
	 * Stopppt das automatische versenden von eMails im Hintergrund.
	 * 
	 * @param cntx Octopus-Context
	 */
	public void unload(OctopusContext cntx) {
		logger.info("MailDispatcher wird im Hintergrund gestoppt.");
		keeprunning = false;
	}

	/** @see Runnable#run() */
	public void run() {
		try {
			isworking = true;
			while (keeprunning) {
				try {
					sendMails();
				} catch (Exception e) {
					logger.error("Allgemeiner Fehler beim Versenden einer eMail.", e);
				}
				try {
					Thread.sleep(waitMillis < 1000 ? 1000 : waitMillis);
				} catch (InterruptedException e) {
				}
			}
		} finally {
			isworking = false;
			thread = null;
		}
	}

	/**
	 * Versendet alle offenen eMails.
	 */
	public void sendMails() {
		Database db = new DatabaseVeraWeb( this.cntx );
		Select select = SQL.Select( db ).
				from("veraweb.tmailoutbox").
				selectAs("pk", "id").
				selectAs("status", "status").
				selectAs("addrfrom", "from").
				selectAs("addrto", "to").
				selectAs("subject", "subject").
				selectAs("content", "text");
		
		select.where(Expr.equal("status", new Integer(MailOutbox.STATUS_WAIT)));
		select.orderBy(Order.asc("lastupdate"));
		select.Limit(new Limit(new Integer(1), new Integer(0)));
		
		Result r = null;
		ResultSet rs = null;
		for (boolean found = true; found; ) {
			try {
				found = false;
				r = ( Result ) select.execute();
				rs = r.resultSet();
				
				for (Iterator it = new ResultList(rs).iterator(); it.hasNext(); ) {
					Map data = (Map)it.next();
					found = true;
					sendMail((Integer)data.get("id"),
							(Integer)data.get("status"),
							(String)data.get("from"),
							(String)data.get("to"),
							(String)data.get("subject"),
							(String)data.get("text"));
				}
			} catch (SQLException e) {
				logger.error("Fehler beim Abholen der zu ausgehenden eMails.", e);
			} finally {
				if (rs != null) {
					try {
						rs.close();
					} catch (SQLException e) {
					}
				}
				if (r != null) {
					r.closeAll();
				}
			}
		}
	}

	/**
	 * Versendet eine einzelne eMail.
	 * 
	 * @param id
	 * @param status
	 * @param from
	 * @param to
	 * @param subject
	 * @param text
	 */
	protected void sendMail(Integer id, Integer status, String from, String to, String subject, String text) {
		try {
			updateMail(id, MailOutbox.STATUS_INPROCESS, null);
			dispatcher.send(from, to, subject, text);
			deleteMail(id);
		} catch (Exception e) {
			logger.error("Es ist ein Fehler beim versenden einer E-Mail aufgetreten.", e);
			try {
				updateMail(id, MailOutbox.STATUS_ERROR, e.getLocalizedMessage());
			} catch (SQLException e1) {
				logger.error("Es ist ein Fehler beim versenden einer E-Mail aufgetreten.", e);
			}
		}
	}

	protected void updateMail(Integer id, int status, String error) throws SQLException {
		if (error != null && error.length() > 200) {
			error = error.substring(0, 195) + "\n...";
		}
		Database db = new DatabaseVeraWeb( this.cntx );
		DB.update(moduleName, SQL.Update( db ).
				table("veraweb.tmailoutbox").
				update("status", new Integer(status)).
				update("lastupdate", new Timestamp(System.currentTimeMillis())).
				update("errortext", error).
				where(Expr.equal("pk", id)));
	}

	protected void deleteMail(Integer id) throws SQLException {
		Database db = new DatabaseVeraWeb( this.cntx );
		DB.update(moduleName, SQL.Delete( db ).
				from("veraweb.tmailoutbox").
				where(Expr.equal("pk", id)));
	}


	//
	// OCTOPUS-AKTIONEN ZUM ERSTELLEN UND SPEICHERN VON EMAILS
	//

	/** Octopus-Eingabe-Parameter f�r die Aktion {@link #writeMail(OctopusContext)} */
	public static final String INPUT_writeMail[] = {};
	/**
	 * Octopus-Aktion zur Verwaltung einer 'in process' eMail.
	 */
	public void writeMail(OctopusContext cntx) throws BeanException, IOException {
		Request request = new RequestVeraWeb(cntx);
		Database database = new DatabaseVeraWeb(cntx);
		List errors = new ArrayList();
		
		MailDraft mail = (MailDraft)request.getBean("MailDraft", "mail");
		
		if (cntx.requestAsBoolean("loaddraft").booleanValue()) {
			Integer draftId = cntx.requestAsInteger("mail-draft");
			if (draftId != null) {
				mail = (MailDraft)database.getBean("MailDraft", draftId);
			}
		} else if (cntx.requestAsBoolean("savedraft").booleanValue()) {
			if (mail.isCorrect()) {
				database.saveBean(mail);
			} else {
				errors.addAll(mail.getErrors());
			}
		}
		
		cntx.setContent("mail", mail);
		cntx.setContent("errors", errors);
	}

	/** Octopus-Eingabe-Parameter f�r die Aktion {@link #sendMail(OctopusContext)} */
	public static final String INPUT_sendMail[] = {};
	/**
	 * Octopus-Aktion zum versenden einer eMail an einen Verteiler.
	 */
	public void sendMail(OctopusContext cntx) throws BeanException, IOException {
		Request request = new RequestVeraWeb(cntx);
		Database database = new DatabaseVeraWeb(cntx);
		TransactionContext context  =database.getTransactionContext();
		Mailinglist mailinglist = (Mailinglist)cntx.contentAsObject("mailinglist");
		MailDraft mail = (MailDraft)request.getBean("MailDraft", "mail");
		List selection = (List)cntx.contentAsObject("listselection");
		Integer freitextfeld = ConfigWorker.getInteger(cntx, "freitextfeld");
		
		Select select = database.getSelect("MailinglistAddress");
		if (selection == null || selection.size() == 0) {
			select.where(Expr.equal("fk_mailinglist", mailinglist.id));
		} else {
			select.where(Where.and(
					Expr.equal("fk_mailinglist", mailinglist.id),
					Expr.in("pk", selection)));
		}
		
		MailOutbox outbox = new MailOutbox();
		outbox.status = new Integer(MailOutbox.STATUS_WAIT);
		outbox.from = getMailAddress(cntx);
		outbox.subject = mail.subject;
		outbox.lastupdate = new Timestamp(System.currentTimeMillis());
		
		try {
			int savedMails = 0;
			for (Iterator it = database.getList(select, context).iterator(); it.hasNext(); ) {
				Map data = (Map)it.next();
				Person person = (Person)database.getBean("Person", (Integer)data.get("person"), context);
				
				outbox.text = getMailText(mail.text, getPersonFacade(cntx, database, context, freitextfeld, person));
				outbox.to = (String)data.get("address");
				
				try {
					context.execute(database.getInsert(outbox));
	//				database.execute(database.getInsert(outbox));
					savedMails++;
				} catch (BeanException e) {
					logger.error("Fehler beim anlegen einer E-Mail.", e);
				} catch (IOException e) {
					logger.error("Fehler beim anlegen einer E-Mail.", e);
				}
			}
			context.commit();
			Map result = new HashMap();
			result.put("count", new Integer(savedMails));
			cntx.setContent("maildispatchParams", result);
		} 
		catch ( BeanException e )
		{
			context.rollBack();
		}
		
	}

	protected PersonDoctypeFacade getPersonFacade(OctopusContext cntx, Database database, Integer doctypeId, Person person) throws BeanException {
		PersonDoctypeFacade facade = new PersonDoctypeFacade(cntx, person);
		if (doctypeId == null) {
			facade.setFacade(null, null, true);
			return facade;
		}
		
		Integer addresstype, locale;
		Select select = SQL.Select( database ).
				from("veraweb.tdoctype").
				selectAs("tdoctype.addresstype", "at1").
				selectAs("tdoctype.locale", "l1").
				selectAs("tperson_doctype.addresstype", "at2").
				selectAs("tperson_doctype.locale", "l2").
				joinLeftOuter("veraweb.tperson_doctype", "fk_doctype = tdoctype.pk AND " +
						"fk_person", person.id.toString()).
				where(Expr.equal("tdoctype.pk", doctypeId));
		
		for (Iterator it = database.getList(select, database).iterator(); it.hasNext(); ) {
			Map data = (Map)it.next();
			addresstype = (Integer)data.get("at2");
			locale = (Integer)data.get("l2");
			if (addresstype != null && locale != null) {
				facade.setFacade(addresstype, locale, true);
				return facade;
			}
			addresstype = (Integer)data.get("at1");
			locale = (Integer)data.get("l1");
			if (addresstype != null && locale != null) {
				facade.setFacade(addresstype, locale, true);
				return facade;
			}
		}
		facade.setFacade(null, null, true);
		return facade;
	}
	
	protected PersonDoctypeFacade getPersonFacade(OctopusContext cntx, Database database, ExecutionContext context, Integer doctypeId, Person person) throws BeanException {
		PersonDoctypeFacade facade = new PersonDoctypeFacade(cntx, person);
		if (doctypeId == null) {
			facade.setFacade(null, null, true);
			return facade;
		}
		
		Integer addresstype, locale;
		Select select = SQL.Select( context ).
				from("veraweb.tdoctype").
				selectAs("tdoctype.addresstype", "at1").
				selectAs("tdoctype.locale", "l1").
				selectAs("tperson_doctype.addresstype", "at2").
				selectAs("tperson_doctype.locale", "l2").
				joinLeftOuter("veraweb.tperson_doctype", "fk_doctype = tdoctype.pk AND " +
						"fk_person", person.id.toString()).
				where(Expr.equal("tdoctype.pk", doctypeId));
		
		for (Iterator it = database.getList(select, context).iterator(); it.hasNext(); ) {
			Map data = (Map)it.next();
			addresstype = (Integer)data.get("at2");
			locale = (Integer)data.get("l2");
			if (addresstype != null && locale != null) {
				facade.setFacade(addresstype, locale, true);
				return facade;
			}
			addresstype = (Integer)data.get("at1");
			locale = (Integer)data.get("l1");
			if (addresstype != null && locale != null) {
				facade.setFacade(addresstype, locale, true);
				return facade;
			}
		}
		facade.setFacade(null, null, true);
		return facade;
	}

	/**
	 * Gibt einen eMail-Text zur�ck bei dem die Platzhalter
	 * durch Personen-Daten ersetzt sind.
	 * 
	 * @param text Text mit Platzhaltern
	 * @param facade PersonDoctypeFacade
	 * @return eMail-Text
	 * @throws IOException
	 * @throws BeanException
	 */
	protected String getMailText(String text, PersonDoctypeFacade facade) throws BeanException, IOException {
		if (text == null) return "";
		StringBuffer buffer = new StringBuffer(text.length());
		Matcher matcher = mailtags.matcher(text);
		String group;
		String entry;
		int last = 0;
		while (matcher.find()) {
			if (matcher.end() - matcher.start() > 0) {
				buffer.append(text.substring(last, matcher.start()));
				group = matcher.group();
				group = group.substring(1, group.length() - 1);
				entry = facade.getField(group);
				if (entry == group) {
					buffer.append("<").append(entry).append(">");
				} else if (entry != null) {
					buffer.append(entry);
				}
				last = matcher.end();
			}
		}
		if (text.length() > last) {
			buffer.append(text.substring(last));
		}
		return buffer.toString();
	}

	/**
	 * Holt die eMail-Adresse aus der Personal-Config (LDAP) und wenn
	 * dort keine hinterlegt ist aus der Konfiguration (config.xml).
	 * 
	 * @param cntx Octopus-Context
	 * @return eMail-Adresse
	 */
	protected String getMailAddress(OctopusContext cntx) {
		String from = cntx.personalConfig().getEmail();
		if (from != null && from.length() != 0) {
			logger.info("Verwende E-Mail-Adresse aus dem LDAP: " + from);
			return from;
		}
		
		Map settings = (Map)cntx.moduleConfig().getParamAsObject("mailServer");
		from = (String)settings.get("from");
		if (from.indexOf("$role") != -1) {
			from = from.replaceAll("(\\$role)", ((PersonalConfigAA)cntx.personalConfig()).getRole());
		}
		logger.info("Verwende E-Mail-Adresse aus der Konfigurationsdatei: " + from);
		return from;
	}
}
