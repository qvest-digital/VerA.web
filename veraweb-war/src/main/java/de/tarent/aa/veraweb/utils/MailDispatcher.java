/*
 * veraweb,
 * Veranstaltungsmanagment veraweb
 * Copyright (c) 2005-2007 tarent GmbH
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License,version 2
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 *
 * tarent GmbH., hereby disclaims all copyright
 * interest in the program 'veraweb'
 * Signature of Elmar Geese, 21 November 2007
 * Elmar Geese, CEO tarent GmbH.
 */

/* $Id$ */
package de.tarent.aa.veraweb.utils;

import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.Message.RecipientType;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MailDateFormat;

import com.sun.mail.smtp.SMTPMessage;

/**
 * Klasse zum versenden von eMails über einen SMTP-Server.
 * 
 * @author Christoph Jerolimov
 */
public class MailDispatcher {
	/**
	 * Test-Main-Funktion.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			MailDispatcher dispatcher = new MailDispatcher();
			dispatcher.setHost("hermes.tarent.de");
			dispatcher.send(getUserMail(), getUserMail(), "Betreff",
					"Toller Text\n" +
					"Noch mehr toller Text.\n\n" +
					"-- \n" +
					"powered by veraweb :o)");
		} catch (AddressException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}

    /**
     * Diese Methode liefert eine Mail-Adresse zum Benutzer, in dessen Kontext
     * dieses Programm läuft. Hierzu wird der Benutzername als Mailadresse in
     * der Domäne tarent.de interpretiert.
     * 
     * @return Mailadresse zum Benutzer in tarent.de
     */
	private static final String getUserMail() {
		String name = System.getProperty("user.name");
		return name + " <" + name + "@tarent.de>";
	}

	/** Hilfsklasse zum RFC-Konformen formatieren eines Datums. */
	private MailDateFormat dateFormat = new MailDateFormat();

	/** SMTP-Servername */
	protected String host;
	/** Benutzername zum versenden über SSMTP */
	protected String username;
	/** Passwort zum versenden über SSMTP */
	protected String password;

	/**
	 * @param host SMTP-Servername
	 */
	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * Sendet eine eMail an die übergebene eMail-Adresse und dem übergebenem
	 * Betreff und Text.
	 * 
	 * @param from Absender eMail-Adresse
	 * @param to Empfänger eMail-Adresse
	 * @param subject Betreff
	 * @param text Text
	 * @throws AddressException Wenn Adresse ungültig ist
	 * @throws MessagingException Wenn beim Versenden ein Fehler aufgetreten ist.
	 */
	public void send(String from, String to, String subject, String text) throws AddressException, MessagingException {
		Session session = getSession();
		Message message = getMessage(session, from, to, subject, text);
		Transport transport = session.getTransport("smtp");
		transport.connect(host, username, password);
		transport.sendMessage(message, message.getAllRecipients());
		transport.close();
	}

	protected Message getMessage(Session session, String from, String to, String subject, String text) throws AddressException, MessagingException {
		Message message = new SMTPMessage(session);
		message.setFrom(new InternetAddress(from));
		message.addRecipient(RecipientType.TO, new InternetAddress(to));
		message.setSubject(subject);
		message.setText(text);
		message.setHeader("Date", dateFormat.format(new Date(System.currentTimeMillis())));
//		message.setHeader("X-Mailer", "colibri.org");
		message.saveChanges();
		return message;
	}

	protected Session getSession() {
		Properties properties = System.getProperties();
		if (username != null && password != null) {
			properties.put("mail.smtp.auth", "true");
		}
		return Session.getDefaultInstance(properties);
	}
}
