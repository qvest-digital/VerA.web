package de.tarent.aa.veraweb.beans;

/*-
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
import de.tarent.aa.veraweb.utils.VerawebMessages;
import de.tarent.octopus.PersonalConfigAA;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.server.OctopusContext;

/**
 * The bean class WorkArea represents an entity that
 * stores so-called standing data for association with
 * {@link Person}s.
 *
 * @author cklein
 * @since 1.2.0
 *
 * @see de.tarent.aa.veraweb.worker.WorkAreaWorker
 */
public class WorkArea extends AbstractBean {
	public Integer id;
	public Integer orgunit;
	public String name;

	/**
	 * Verifies that the name was correctly set.
	 * @param octopusContext FIXME
	 * @throws BeanException FIXME
	 */
    public void verify(final OctopusContext octopusContext) throws BeanException {
        final VerawebMessages messages = new VerawebMessages(octopusContext);
        if (name == null || name.trim().length() == 0) {
			addError(messages.getMessageWorkAreaMissingName());
		}
	}

    /**
     * Tests whether the user may read the bean
     * from the database.
     *
     * @param octopusContext the current octopus context
     * @throws BeanException FIXME
     * @see de.tarent.aa.veraweb.beans.AbstractBean#checkRead(de.tarent.octopus.server.OctopusContext)
     */
    @Override
    public void checkRead( OctopusContext octopusContext) throws BeanException {
        checkGroups(octopusContext, PersonalConfigAA.GROUP_READ_STANDARD, PersonalConfigAA.GROUP_SYSTEM_USER);
    }

   /**
    * Tests whether the user may write the bean
    * to the database.
    *
    * @param octopusContext the current octopus context
    * @throws BeanException FIXME
    * @see de.tarent.aa.veraweb.beans.AbstractBean#checkWrite(de.tarent.octopus.server.OctopusContext)
    */
   @Override
   public void checkWrite( OctopusContext octopusContext) throws BeanException {
       checkGroups(octopusContext, PersonalConfigAA.GROUP_ADMIN, PersonalConfigAA.GROUP_SYSTEM_USER);
   }
}
