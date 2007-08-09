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

/**
 * 
 */
package de.tarent.commons.mail;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * 
 * A tool for easily sending mails
 * 
 * <p>
 * <b>Caution</b>: This tool dependes on Suns Javamail-Library which is licensed under the "Common Development and Distribution License" (CDDL)
 * which is not compatible to the GNU General Public License (GPL) !!<br />
 * 
 * See appropriate <a href="https://kassandra.tarent.de/maven-projects/tarent-commons/mailtool.html">Maven-Site</a> for more information. 
 * </p>
 * 
 * @author Fabian K&ouml;ster (f.koester@tarent.de), tarent GmbH Bonn
 *
 */
public class MailTool
{
	private static MailTool instance; 
	
	private String smtpServer = "ikarus.tarent.de";
	private boolean authenticating = false;
	private String username;
	private String password;
	
	private MailTool()
	{
		
	}
	
	/**
	 * Returns the instance of MailTool
	 * 
	 * @return an instance of MailTool
	 */
	
	public static MailTool getInstance()
	{
		if(instance == null)
		{
			instance = new MailTool();
		}
		return instance;
	}

	/**
	 * sends {@code mail}
	 * @throws MessagingException 
	 */
	public void postMail(Email mail) throws MessagingException {
		Transport.send(mail.getMessage(getSession()));
	}
	
	/**
	 * Return a session
	 */
	protected Session getSession() {
		boolean debug = false;
		
		//Set the host smtp address
		Properties props = new Properties();
		props.put("mail.smtp.host", getSMTPServer());
		props.put("mail.smtp.auth", String.valueOf(authenticating()));
		
		Session session;
		if(authenticating())
		{
			Authenticator auth = new PasswordAuthenticator();
		    session = Session.getDefaultInstance(props, auth);
		}
		else
		{
			session = Session.getDefaultInstance(props, null);
		}
	
		session.setDebug(debug);
		session.getProperties().setProperty("mail.smtp.auth", String.valueOf(authenticating()));
		session.getProperties().setProperty("mail.smtp.host", getSMTPServer());
		return session;
	}
	
	/**
	 * Sends a mail
	 * 
	 * @param pRecipients an String-Array of mail-addresses this mail should go to
	 * @param pSubject the subject of this mail 
	 * @param pMessage the mail-text
	 * @param pFrom the address this mail should come from
	 * @throws MessagingException when an error occured during sending
	 */
	
	public void postMail(String pRecipients[ ], String pSubject, String pMessage , String pFrom) throws MessagingException
	{
		Session session = getSession();
		
		// create a message
		Message msg = new MimeMessage(session);

		// set the from and to address
		InternetAddress addressFrom = new InternetAddress(pFrom);
		msg.setFrom(addressFrom);

		InternetAddress[] addressTo = new InternetAddress[pRecipients.length]; 
		for (int i = 0; i < pRecipients.length; i++)
		{
			addressTo[i] = new InternetAddress(pRecipients[i]);
		}
		msg.setRecipients(Message.RecipientType.TO, addressTo);

		// Optional : You can also set your custom headers in the Email if you Want
		msg.addHeader("MyHeaderName", "myHeaderValue");
		
		// Setting the Subject and Content Type
		msg.setSubject(pSubject);
		msg.setContent(pMessage, "text/plain");
		Transport.send(msg);
	}
	
	/**
	 * Returns the specified address of the SMTP-Mail-Server
	 * @return a String representing the address of the SMTP-Server
	 */
	
	public String getSMTPServer()
	{
		return smtpServer;
	}
	
	/**
	 * Sets the address of the SMTP-Server the mail should go to
	 * 
	 * @param pAddress the address of the SMTP-Server
	 */
	
	public void setSMTPServer(String pAddress)
	{
		this.smtpServer = pAddress;
	}
	
	/**
	 * If the MailTool is going to authenticate before sending mails
	 * 
	 * @return true if authentication is enabled
	 */
	
	public boolean authenticating()
	{
		return authenticating;
	}
	
	/**
	 * 
	 * @param pAuthenticating
	 */
	
	public void setAuthenticating(boolean pAuthenticating)
	{
		authenticating = pAuthenticating;
	}
	
	/**
	 * 
	 * Returns the specified username
	 * 
	 * @return a String representing the username
	 */
	
	public String getUserName()
	{
		return username;
	}
	
	/**
	 * Sets the username for authentication<br />
	 * Only used if authenticating() is true
	 * 
	 * @param pUserName the username to use for authentication
	 */
	
	public void setUserName(String pUserName)
	{
		username = pUserName;
	}
	
	/**
	 * Sets the password for authentication<br />
	 * Only used if authenticating() is true
	 * 
	 * @param pPassword the password to use for authentication
	 */
	
	public void setPassword(String pPassword)
	{
		password = pPassword;
	}

	
	private class PasswordAuthenticator extends Authenticator
	{
		public PasswordAuthentication getPasswordAuthentication()
		{
			return new PasswordAuthentication(username, password);
		}
	}
}
