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

import de.tarent.aa.veraweb.beans.MailDraft;
import de.tarent.aa.veraweb.beans.MailOutbox;
import de.tarent.aa.veraweb.beans.Mailinglist;
import de.tarent.aa.veraweb.beans.Person;
import de.tarent.aa.veraweb.beans.facade.PersonDoctypeFacade;
import de.tarent.aa.veraweb.utils.MailDispatcher;
import de.tarent.aa.veraweb.utils.VerawebUtils;
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
import org.apache.log4j.Logger;

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

	/** Modulnamen für den Datenbank-Pool */
	private String moduleName;
	/** Thread in dem das eigentliche versenden ausgeführt wird. */
    private Thread thread;
	/** MailDispatcher der den Versand an den SMTP-Server vornimmt. */
    private final MailDispatcher dispatcher = new MailDispatcher();
	/** Gibt an ob der Dispatcher weitere eMails verschicken soll. */
    private boolean keeprunning = false;
	/** Gibt an ob der Dispatcher gerade eMails verschickt. */
    private boolean isworking = false;
	/** Gibt die Wartezeit zwischen zwei Dispatch aufrufen an. */
    private int waitMillis = 0;

	private OctopusContext octopusContext;

	/** Octopus-Eingabe-Parameter für {@link #load(OctopusContext)} */
	public static final String INPUT_load[] = {};
	/**
	 * Startet das automatische versenden von eMails im Hintergrund.
	 *
	 * @param octopusContext Octopus-Context
	 */
	public void load(OctopusContext octopusContext) {
		this.octopusContext = octopusContext;
		logger.info("MailDispatcher wird im Hintergrund gestartet.");
		moduleName = octopusContext.getModuleName();

		final Map settings = (Map)octopusContext.moduleConfig().getParamAsObject("mailServer");
		if (settings != null) {
			loadSettings(settings);

			// Server status
			if (!keeprunning) {
				keeprunning = true;
				thread = new Thread(this);
				thread.start();
			}
		} else {
			unload();
		}
	}

	private void loadSettings(Map settings) {
		// Settingsladen
		dispatcher.setHost((String)settings.get("host"));
		setAuthData(settings);
		setWaitInterval(settings);
	}

	private void setWaitInterval(Map settings) {
		try {
            waitMillis = Integer.parseInt((String)settings.get("waitBetweenJobs")) * 1000;
        } catch (NumberFormatException e) {
            logger.warn("MailDispatcher konnte Parameter 'waitBetweenJobs' nicht parsen (" + settings.get("waitBetweenJobs") + "). Es wird der Default Wert (60 Sekunden) verwendet.");
            waitMillis = 60000; // default: 1 Minute
        } catch (NullPointerException e) {
            logger.warn("MailDispatcher konnte Parameter 'waitBetweenJobs' nicht finden. Es wird der Default Wert (60 Sekunden) verwendet.");
            waitMillis = 60000; // default: 1 Minute
        }
	}

	private void setAuthData(Map settings) {
		if (settings.get("host") != null && settings.get("password") != null) {
            dispatcher.setUsername((String)settings.get("username"));
            dispatcher.setPassword((String)settings.get("password"));
        }
	}

	/** Octopus-Eingabe-Parameter für {@link #unload()} */
	public static final String INPUT_unload[] = {};
	/**
	 * Stopppt das automatische versenden von eMails im Hintergrund.
	 *
	 */
    private void unload() {
		logger.info("MailDispatcher wird im Hintergrund gestoppt.");
		keeprunning = false;
	}

	/**
	 * @see Runnable#run()
	 */
	public void run() {
		try {
			isworking = true;
			while (keeprunning) {
				try {
					sendMails();
				} catch (Exception e) {
					logger.error("Allgemeiner Fehler beim Versenden einer eMail.", e);
				}
				Thread.sleep(waitMillis < 1000 ? 1000 : waitMillis);
			}
		} catch (InterruptedException e) {
			logger.error("Thread.sleep failed.", e);
		} finally {
			isworking = false;
			thread = null;
		}
	}

	/**
	 * Versendet alle offenen eMails.
	 */
    private void sendMails() {
		final Database db = new DatabaseVeraWeb( this.octopusContext);
		final Select select = SQL.Select( db ).
				from("veraweb.tmailoutbox").
				selectAs("pk", "id").
				selectAs("status", "status").
				selectAs("addrfrom", "from").
				selectAs("addrto", "to").
				selectAs("subject", "subject").
				selectAs("content", "text");

		select.where(Expr.equal("status", MailOutbox.STATUS_WAIT));
		select.orderBy(Order.asc("lastupdate"));
		select.Limit(new Limit(new Integer(1), new Integer(0)));

        handleSendEmail(select);
	}

    private void handleSendEmail(Select select) {
        ResultSet resultSet = null;
        Result result = null;
        try {
            result = (Result) select.execute();
            resultSet = result.resultSet();
            List allRecipients = VerawebUtils.copyResultListToArrayList(new ResultList(resultSet));
            for (int i = 0; i < allRecipients.size(); i++) {
                final HashMap recipient = (HashMap) allRecipients.get(i);
                executeMailSend(recipient);
            }
        } catch (SQLException e) {
            logger.error("Fehler beim Abholen der zu ausgehenden eMails.", e);
        } finally {
            if (resultSet != null) {
                closeResultSet(resultSet);
            }
            if (result != null) {
                result.closeAll();
            }
        }
    }

    private void executeMailSend(HashMap recipient) {
        final Integer id = (Integer) recipient.get("id");
        final String from = (String) recipient.get("from");
        final String to = (String) recipient.get("to");
        final String subject = (String) recipient.get("subject");
        final String text = (String) recipient.get("text");
        sendMail(id, from, to, subject, text);
    }

    private void closeResultSet(ResultSet resultSet) {
        try {
            resultSet.close();
        } catch (SQLException e) {
            logger.error("Closing ResulSet failed", e);
        }
    }

    /**
	 * Versendet eine einzelne eMail.
	 *
	 * @param id
	 * @param from
	 * @param to
	 * @param subject
	 * @param text
	 */
    private void sendMail(Integer id, String from, String to, String subject, String text) {
		try {
			updateMailOutbox(id, MailOutbox.STATUS_INPROCESS, null);
			dispatcher.send(from, to, subject, text);
			deleteMailOutbox(id);
		} catch (Exception e) {
			logger.error("Es ist ein Fehler beim versenden einer E-Mail aufgetreten.", e);
			try {
				updateMailOutbox(id, MailOutbox.STATUS_ERROR, e.getLocalizedMessage());
			} catch (SQLException e1) {
				logger.error("Es ist ein Fehler beim versenden einer E-Mail aufgetreten.", e);
			}
		}
	}

    private void updateMailOutbox(Integer id, int status, String error) throws SQLException {
		if (error != null && error.length() > 200) {
			error = error.substring(0, 195) + "\n...";
		}
		final Database database = new DatabaseVeraWeb( this.octopusContext);
		DB.update(moduleName, SQL.Update( database ).
				table("veraweb.tmailoutbox").
				update("status", status).
				update("lastupdate", new Timestamp(System.currentTimeMillis())).
				update("errortext", error).
				where(Expr.equal("pk", id)));
	}

    private void deleteMailOutbox(Integer id) throws SQLException {
        final Database database = new DatabaseVeraWeb( this.octopusContext);
        final TransactionContext transactionContext = database.getTransactionContext();
        try {
            transactionContext.execute(SQL.Delete(database).from("veraweb.tmailoutbox").where(Expr.equal("pk", id)));
            transactionContext.commit();
        } catch (BeanException e) {
            logger.error("Mail outbox could not be deleted", e);
        }
	}


	//
	// OCTOPUS-AKTIONEN ZUM ERSTELLEN UND SPEICHERN VON EMAILS
	//

	/** Octopus-Eingabe-Parameter für die Aktion {@link #writeMail(OctopusContext)} */
	public static final String INPUT_writeMail[] = {};
	/**
	 * Octopus-Aktion zur Verwaltung einer 'in process' eMail.
	 */
	public void writeMail(OctopusContext octopusContext) throws BeanException, IOException {
		Request request = new RequestVeraWeb(octopusContext);
		Database database = new DatabaseVeraWeb(octopusContext);
		List errors = new ArrayList();

		MailDraft mail = (MailDraft)request.getBean("MailDraft", "mail");

		if (octopusContext.requestAsBoolean("loaddraft")) {
			Integer draftId = octopusContext.requestAsInteger("mail-draft");
			if (draftId != null) {
				mail = (MailDraft)database.getBean("MailDraft", draftId);
			}
		} else if (octopusContext.requestAsBoolean("savedraft")) {
			if (mail.isCorrect()) {
				database.saveBean(mail);
			} else {
				errors.addAll(mail.getErrors());
			}
		}

		octopusContext.setContent("mail", mail);
		octopusContext.setContent("errors", errors);
	}

	/** Octopus-Eingabe-Parameter für die Aktion {@link #sendMail(OctopusContext)} */
	public static final String INPUT_sendMail[] = {};
	/**
	 * Octopus-Aktion zum versenden einer eMail an einen Verteiler.
	 */
	public void sendMail(OctopusContext octopusContext) throws BeanException, IOException {
        final MailDraft mail = getMailDraft(octopusContext);
        final Database database = new DatabaseVeraWeb(octopusContext);
        final TransactionContext transactionContext = database.getTransactionContext();

		try {
			int savedMailsCounter = 0;
            final ResultList resultList = getResultList(database, octopusContext, transactionContext);
            final List allRecipients = VerawebUtils.copyResultListToArrayList(resultList);
            for (Object recipient : allRecipients) {
                final HashMap currentRecipient = (HashMap) recipient;
                final Person person = (Person)database.getBean("Person", (Integer) currentRecipient.get("person"), transactionContext);
                final MailOutbox outbox = createMailOutbox(octopusContext, database, transactionContext, mail, currentRecipient, person);

                try {
                    transactionContext.execute(database.getInsert(outbox));
                    savedMailsCounter++;
                } catch (BeanException e) {
                    logger.error("Fehler beim anlegen einer E-Mail.", e);
                } catch (IOException e) {
                    logger.error("Fehler beim anlegen einer E-Mail.", e);
                }

            }
			transactionContext.commit();
			final Map result = new HashMap();
			result.put("count", savedMailsCounter);
			octopusContext.setContent("maildispatchParams", result);
		} catch (BeanException e) {
            transactionContext.rollBack();
        }

	}

    private ResultList getResultList(Database database, OctopusContext octopusContext, TransactionContext transactionContext) throws BeanException, IOException {
        final Select select = getMailinglistSelectStatement(octopusContext, database);
        return database.getList(select, transactionContext);
    }

    private MailDraft getMailDraft(OctopusContext octopusContext) throws BeanException {
        final Request request = new RequestVeraWeb(octopusContext);
        return (MailDraft)request.getBean("MailDraft", "mail");
    }

    private MailOutbox createMailOutbox(OctopusContext octopusContext, Database database, TransactionContext transactionContext, MailDraft mail, HashMap currentRecipient, Person person) throws BeanException, IOException {
        final Integer freitextfeld = ConfigWorker.getInteger(octopusContext, "freitextfeld");
        final MailOutbox outbox = new MailOutbox();
        outbox.status = MailOutbox.STATUS_WAIT;
        outbox.from = getMailAddress(octopusContext);
        outbox.subject = mail.subject;
        outbox.lastupdate = new Timestamp(System.currentTimeMillis());
        outbox.text = getMailText(mail.text, getPersonFacade(octopusContext, database, transactionContext, freitextfeld, person));
        outbox.to = (String) currentRecipient.get("address");
        return outbox;
    }

    private Select getMailinglistSelectStatement(OctopusContext octopusContext, Database database) throws BeanException, IOException {
        final Mailinglist mailinglist = (Mailinglist)octopusContext.contentAsObject("mailinglist");
        final List selection = (List)octopusContext.contentAsObject("listselection");
        final Select selectStatement = database.getSelect("MailinglistAddress");
        if (selection == null || selection.size() == 0) {
            selectStatement.where(Expr.equal("fk_mailinglist", mailinglist.id));
        } else {
            selectStatement.where(
                Where.and(
                    Expr.equal("fk_mailinglist", mailinglist.id),
                    Expr.in("pk", selection)
                )
            );
        }
        return selectStatement;
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

    private PersonDoctypeFacade getPersonFacade(OctopusContext cntx, Database database, ExecutionContext context, Integer doctypeId, Person person) throws BeanException {
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
	 * Gibt einen eMail-Text zurück bei dem die Platzhalter
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
		StringBuilder buffer = new StringBuilder(text.length());
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
				if (entry.equals(group)) {
					buffer.append("<").append(entry).append(">");
				} else if (!entry.equals("")) {
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
    private String getMailAddress(OctopusContext cntx) {
		String from = cntx.personalConfig().getEmail();
		if (from != null && from.length() != 0) {
			logger.info("Verwende E-Mail-Adresse aus dem LDAP: " + from);
			return from;
		}

		Map settings = (Map)cntx.moduleConfig().getParamAsObject("mailServer");
		from = (String)settings.get("from");
		if (from.contains("$role")) {
			from = from.replaceAll("(\\$role)", ((PersonalConfigAA)cntx.personalConfig()).getRole());
		}
		logger.info("Verwende E-Mail-Adresse aus der Konfigurationsdatei: " + from);
		return from;
	}
}
