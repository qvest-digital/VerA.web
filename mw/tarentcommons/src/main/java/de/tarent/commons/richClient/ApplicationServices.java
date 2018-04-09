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

package de.tarent.commons.richClient;

import org.apache.commons.logging.Log;
import de.tarent.commons.logging.LogFactory;

/**
 * This class keeps different application services,
 * that can be used by every component of the application.
 * The implemetations of the services can be registered an replaced during execution.
 *
 *
 * @author Steffi Tinder, tarent GmbH
 *
 */
public class ApplicationServices {

	private static final Log logger = LogFactory.getLog(ApplicationServices.class);

	private static ApplicationServices instance;
	private CommonDialogServices commonDialogServices;
    private ApplicationFrame applicationFrame;

	protected ApplicationServices(){
	}

	/**
	 * This message returns the Instance of ApplicationServices.
	 * If it does not exist, it will be created at this point.
	 * @return the instance of ApplicationServices
	 */
	public synchronized static ApplicationServices getInstance(){
	if (instance == null){
	    instance = new ApplicationServices();
	}
		return instance;
	}

	/**
	 * This method returns the currently registered implementation of
	 * CommonDialogServices or null if no implementation is registered.
	 * @return the registered CommonDialogServices-Implementation
	 */
	public CommonDialogServices getCommonDialogServices() {
		if(commonDialogServices == null) logger.debug("[ApplicationServices]: Common Dialog Services not found");
		return commonDialogServices;
	}
	/**
	 * Registers an implementation of CommonDialogServices.
	 * @param commonDialogServices the Implementation of CommonDialogServices that shall be registered
	 */
	public void setCommonDialogServices(CommonDialogServices commonDialogServices) {
		this.commonDialogServices = commonDialogServices;
	}

    /**
     * @return the MainFrame
     */
    public ApplicationFrame getMainFrame() {
	if(applicationFrame == null) logger.debug("[ApplicationServices]: Main Frame not found");
	return applicationFrame;
    }

    /**
     * sets the MainFrame
     */
    public void setMainFrame(ApplicationFrame newApplicationFrame) {
	this.applicationFrame = newApplicationFrame;
    }
}
