/*
 * $Id: UserConfig.java,v 1.1 2007/06/20 11:56:51 christoph Exp $
 */
package de.tarent.aa.veraweb.beans;

import de.tarent.octopus.custom.beans.BeanException;
import de.tarent.octopus.server.OctopusContext;

/**
 * @author Christoph Jerolimov
 * @version $Revision: 1.1 $
 */
public class UserConfig extends AbstractBean {
	/** ID */
	public Integer id;
	/** FK auf User */
	public Integer user;
	/** Key */
	public String key;
	/** Value */
	public String value;

	/**
	 * Hebt den Leseschutz für die User-Config auf.
	 */
	public void checkRead(OctopusContext cntx) throws BeanException {
	}

	/**
	 * Hebt den Schreibschutz für die User-Config auf. 
	 */
	public void checkWrite(OctopusContext cntx) throws BeanException {
	}
}
