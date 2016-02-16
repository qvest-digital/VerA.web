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

	/** Hilfsklasse zum RFC-Konformen formatieren eines Datums. */
	private MailDateFormat dateFormat = new MailDateFormat();

	/** SMTP-Servername */
	protected String host;

	/** Benutzername zum versenden über SSMTP */
	protected String username;

	/** Passwort zum versenden über SSMTP */
	protected String password;
    
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

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
