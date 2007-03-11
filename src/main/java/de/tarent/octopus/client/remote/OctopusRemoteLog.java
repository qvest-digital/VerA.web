/* $Id: OctopusRemoteLog.java,v 1.3 2007/03/11 14:04:34 christoph Exp $
 * tarent-octopus, Webservice Data Integrator and Applicationserver
 * Copyright (C) 2005 tarent GmbH
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 * tarent GmbH., hereby disclaims all copyright
 * interest in the program 'tarent-octopus'
 * (which makes passes at compilers) written
 * by Philipp Kirchner
 * signature of Elmar Geese, 1 June 2002
 * Elmar Geese, CEO tarent GmbH
 */
package de.tarent.octopus.client.remote;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.text.MessageFormat;
import java.util.Date;
import java.util.List;

import org.apache.axis.AxisFault;
import org.apache.axis.Message;
import org.apache.axis.client.Call;
import org.apache.commons.logging.Log;

import de.tarent.octopus.logging.LogFactory;

/** 
 * Klasse, die Logs zur Remotekommunikation anlegt...
 * 
 * @author Philipp Kirchner, tarent GmbH
 */
public class OctopusRemoteLog {
	private Log logger = LogFactory.getLog(this.getClass());
	
	private String taskName;
	private Date taskStart;
	private Date taskEnd;
	private long taskDataTransfer;
	private List taskParams;
	private PrintWriter conlog;
	private PrintWriter condumplog;

	private OctopusRemoteTask task;
	
	public OctopusRemoteLog(String taskName, int requestSize, List params){
		this.taskName = taskName;
		this.taskStart = new Date();
		taskDataTransfer = requestSize;
		taskParams = params;
		try {
			conlog = new PrintWriter(new FileOutputStream(new File(System.getProperty("user.home")+File.separator+"ContactClient.con.log")));
			condumplog = new PrintWriter(new FileOutputStream(new File(System.getProperty("user.home")+File.separator+"ContactClient.condump.log")));
		} catch (FileNotFoundException e) {
			logger.warn("Fehler beim Dateizugriff!", e);
		}
	}
	
	/**
	 * @param task
	 */
	public OctopusRemoteLog(OctopusRemoteTask task) {
		this(task.getTaskName(), 0, task.params);
		this.task = task;
	}
	
	public void startLogEntry(OctopusRemoteTask task){
		this.task = task;
		long size = -1;
		try {
			if(task!=null){
				Call soapCall = task.axisSoapCall;
				if(soapCall!=null){
					Message message = soapCall.getResponseMessage();
					if(message!=null){
						size = message.getContentLength();
					}
				}
			}
		} catch (AxisFault e) {
			//Ignorieren, wir möchten ja nur die Größe wissen
		}
		assert task != null;
		startLogEntry(task.getTaskName(), size, task.params);
	}
	
	public void startLogEntry(String taskName, long size, List params){
		this.taskName = taskName;
		this.taskStart = new Date();
		taskDataTransfer = size;
		taskParams = params;		
	}
	
	public void commitLogEntry(long resultSize){
		taskEnd = new Date();
		taskDataTransfer += resultSize;
		Date duration = new Date(taskEnd.getTime()-taskStart.getTime());
		String message = MessageFormat.format("Task: {0}, Duration: {1}, Size: {2}, Params: {3}", new Object[]{taskName, new Long(duration.getTime()), new Long(taskDataTransfer), taskParams});
		conlog.println(message);
		condumplog.println("==========");
		condumplog.println(message);
		try {
			condumplog.println(task.axisSoapCall.getMessageContext().getRequestMessage().getSOAPPartAsString());
			condumplog.println("----------");
            if (task.axisSoapCall.getResponseMessage() != null)
                condumplog.println(task.axisSoapCall.getResponseMessage().getSOAPPartAsString());
            else
                condumplog.println("axis call response is NULL");
		} catch (AxisFault e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		conlog.flush();
		condumplog.flush();
	}

	/**
	 * 
	 */
	public void commitLogEntry() {
		long size = -1;
		try {
			if(task!=null){
				Call soapCall = task.axisSoapCall;
				if(soapCall!=null){
					Message message = soapCall.getResponseMessage();
					if(message!=null){
						size = message.getContentLength();
					}
				}
			}
		} catch (AxisFault e) {
			//Ignorieren, wir möchten ja nur die Größe wissen
		}
		commitLogEntry(size);
	}
}
