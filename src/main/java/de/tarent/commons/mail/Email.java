/*
 * tarent commons,
 * a set of common components and solutions
 * Copyright (c) 2006-2007 tarent GmbH
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
 * interest in the program 'tarent commons'
 * Signature of Elmar Geese, 14 June 2007
 * Elmar Geese, CEO tarent GmbH.
 */

package de.tarent.commons.mail;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.logging.Log;

import com.sun.mail.smtp.SMTPMessage;

import de.tarent.commons.logging.LogFactory;

/**
 * Represents an email.
 * 
 * @author Tim Steffens, tarent GmbH
 * @author Christoph Jerolimov, tarent GmbH
 * @author Jan Meyer, tarent GmbH
 */
public class Email {
	/** Logger instance. */
	private static final Log logger = LogFactory.getLog(Email.class);
	/** From list */
	private LinkedList from = new LinkedList();
	/** To list */
	private LinkedList to = new LinkedList();
	/** To cc list */
	private LinkedList cc = new LinkedList();
	/** To bcc list */
	private LinkedList bcc = new LinkedList();
	/** Subject */
	private String subject;
	/** Text */
	private String text;
	/** attachments */
	private LinkedList attachments = new LinkedList();
	/** attachment names */
	private LinkedList attachmentNames = new LinkedList();
	/** attachment content types */
	private LinkedList attachmentContentTypes = new LinkedList();

	/** Set the from address. */
	public void setFrom(String email) {
		from.clear();
		if (email != null && email.length() != 0)
			from.add(email);
	}
	
	/** Get the from address. */
	public LinkedList getFrom(){
		return from;
	}

	/** Add a to address. */
	public void addTo(String email) {
		if (email != null && email.length() != 0)
			to.add(email);
	}

	
	/** Add a to cc address. */
	public void addCc(String email) {
		if (email != null && email.length() != 0)
			cc.add(email);
	}
	
	/** Add a to bcc address. */
	public void addBcc(String email) {
		if (email != null && email.length() != 0)
			bcc.add(email);
	}
	
	/** Set the subject. */
	public void setSubject(String subject) {
		this.subject = subject;
	}
	
	/** Get the subject. */
	public String getSubject(){
		return subject;
	}
	
	/** Set the plain text message content. */
	public void setMessagePlainText(String message) {
		this.text = message;
	}
	
	/** Get the plain text message content. */
	public String getMessagePlainText() {
		return text;
	}
	
	/** Set the html text message content. Not impl. yet. */
	public void setMessageHtml(String message) {
		throw new UnsupportedOperationException();
	}
	
	/** Add an attachment to the email. Not impl yet. */
	public void addAttachment(byte[] attachment) {
		throw new UnsupportedOperationException();
	}
	
	/** Adds an attachment to the message. This causes
	 * the email to be encoded as a multipart message.
	 * 
	 * @param filename the name of the file to attach
	 * @param attachmentName the name the attachment shall have in the email
	 * @param contentType type of the attachment, e.g. "text/plain; charset=ISO-8859-1". If null, Java Mail API tries to detect the correct content type.
	 */
	public void addAttachment(String filename, String attachmentName, String contentType) {
		this.attachments.add(filename);
		this.attachmentNames.add(attachmentName);
		this.attachmentContentTypes.add(contentType);
	}
	
	/** Get the Attachment Filename*/
	public LinkedList getAttachmentFileName(){
		return attachments;
	}
	
	/** Get the Attachment Name*/
	public LinkedList getAttachmentName(){
		return attachmentNames;
	}
	
	/** Get the Attachment Content Types */
	public LinkedList getAttachmentContentTypes(){
		return attachmentContentTypes;
	}
	
	/**
	 * Return a new generated message instance copied fomr the current
	 * state of this email.
	 * 
	 * @param session
	 * @return
	 * @throws MessagingException
	 * @throws IllegalArgumentException
	 */
	public Message getMessage(Session session) throws MessagingException, IllegalArgumentException {
		Message message = new SMTPMessage(session);
		
		// From
		if (from.isEmpty())
			throw new IllegalArgumentException("No from address found. Can not send email.");
		Address fromAddress = getAddress((String)from.getFirst());
		if (fromAddress == null)
			throw new IllegalArgumentException("No from address found. Can not send email.");
		message.setFrom(fromAddress);
		
		// To, cc and bcc
		addRecipient(message, Message.RecipientType.TO, to);
		addRecipient(message, Message.RecipientType.CC, cc);
		addRecipient(message, Message.RecipientType.BCC, bcc);
		if (message.getAllRecipients().length == 0)
			throw new IllegalArgumentException("No recipient found. Can not send email.");
		
		// Subject
		if (subject == null || subject.length() == 0)
			logger.warn("Empty email subject.");
		message.setSubject(subject);
		
		// distinguish between plain text and multipart message
		if (this.attachments.size() == 0) {
			// Text
			message.setText(text);		
		}
		else {
			// multipart message
			BodyPart messageBodyPart = new MimeBodyPart();
			messageBodyPart.setText(text);
			
			// set message text
			Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(messageBodyPart);
			
			// add attachments
			for (int i = 0; i < this.attachments.size(); i++) {
				String fileName = (String) this.attachments.get(i);
				
				messageBodyPart = new MimeBodyPart();
				DataSource source = new FileDataSource(fileName);
				messageBodyPart.setDataHandler(new DataHandler(source));
				messageBodyPart.setFileName((String) this.attachmentNames.get(i));
				
				// Set content type if given. If not given the Java Mail API tries to
				// detect the content type automatically.
				if (this.attachmentContentTypes.get(i) != null)
				{
					messageBodyPart.setHeader("Content-Type", (String) this.attachmentContentTypes.get(i));
				}
				multipart.addBodyPart(messageBodyPart);
			}
			
			message.setContent(multipart);
		}
		
		return message;
	}

	/**
	 * Add a list of addresses to a message instance.
	 * 
	 * @param message
	 * @param type
	 * @param addresses
	 * @throws MessagingException
	 */
	protected void addRecipient(Message message, Message.RecipientType type, List addresses) throws MessagingException {
		if (addresses == null)
			return;
		for (Iterator it = addresses.iterator(); it.hasNext(); ) {
			setRecipient(message, type, (String)it.next());
		}
	}

	/**
	 * Set a address to a message instance. Also allow comma seperated lists.
	 * 
	 * @param message
	 * @param type
	 * @param addresses
	 * @throws MessagingException
	 */
	protected void setRecipient(Message message, Message.RecipientType type, String addresses) throws MessagingException {
		if (addresses == null || addresses.length() == 0) {
			return;
		} else if (-1 != addresses.indexOf(",") || -1 != addresses.indexOf(";")) {
			String a[] = addresses.split("[,;]");
			for (int i = 0; i < a.length; i++) {
				Address address = getAddress(a[i]);
				if (address != null)
					message.addRecipient(type, address);
			}
		} else {
			Address address = getAddress(addresses);
			if (address != null)
				message.addRecipient(type, address);
		}
	}

	protected Address getAddress(String address) {
		try {
			if (address == null)
				return null;
			return new InternetAddress(address);
		} catch (AddressException e) {
			logger.warn("Illegal email address '" + address + "' Will return null and ignore it.", e);
			return null;
		}
	}
}
