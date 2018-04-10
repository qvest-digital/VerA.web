
/*-
 * VerA.web-Middleware, newly MIT licenced, is comprised of:
 * tarent-commons, a set of common components and solutions
 * tarent-contact, platform-independent webservice-based contact management
 * tarent-database, jdbc database library
 * tarent-doctor, Document Generation Platform
 * tarent-octopus, Webservice Data Integrator and Application Server
 *  © 2005, 2006, 2007 asteban <s.mancke@tarent.de>
 *  © 2007 David Goemans <d.goemans@tarent.de>
 *  © 2006, 2007, 2010 Hendrik Helwich <h.helwich@tarent.de>
 *  © 2005, 2006, 2007 Christoph Jerolimov <c.jerolimov@tarent.de>
 *  © 2006 Philipp Kirchner <p.kirchner@tarent.de>
 *  © 2010 Carsten Klein <c.klein@tarent.de>
 *  © 2006 Michael Kleinhenz <m.kleinhenz@tarent.de>
 *  © 2006 Michael Klink <m.klink@tarent.de>
 *  © 2007 Fabian Köster <f.koester@tarent.de>
 *  © 2006 Martin Ley <m.ley@tarent.de>
 *  © 2007 Alex Maier <a.maier@tarent.de>
 *  © 2007, 2015, 2017, 2018 mirabilos <t.glaser@tarent.de>
 *  © 2006, 2007 Jens Neumaier <j.neumaier@tarent.de>
 *  © 2006 Nils Neumaier <n.neumaier@tarent.de>
 *  © 2007, 2008 Martin Pelzer <m.pelzer@tarent.de>
 *  © 2008, 2009 Christian Preilowski <c.thiel@tarent.de>
 *  © 2006, 2008, 2009 Thomas Schmitz <t.schmitz@tarent.de>
 *  © 2007 Robert Schuster <r.schuster@tarent.de>
 * and older code, Copyright © 2001–2007 ⮡ tarent GmbH and contributors.
 * Licensor is tarent solutions GmbH, http://www.tarent.de/
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
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
