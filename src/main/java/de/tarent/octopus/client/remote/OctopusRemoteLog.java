package de.tarent.octopus.client.remote;

/*
 * tarent-octopus, Webservice Data Integrator and Application Server
 * Copyright © 2002–2017 tarent solutions GmbH and its contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

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
