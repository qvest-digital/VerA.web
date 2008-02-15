/*
 * veraweb,
 * Veranstaltungsmanagment veraweb
 * Copyright (c) 2005-2008 tarent GmbH
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
package de.tarent.aa.veraweb.beans;

import de.tarent.octopus.PersonalConfigAA;
import de.tarent.octopus.custom.beans.BeanException;
import de.tarent.octopus.server.OctopusContext;

/**
 * The bean class GuestWorkArea represents a relationship
 * entity that associates the specified {see WorkArea} to
 * a specified {Guest}.
 * 
 * @author cklein
 * @since 1.2.0
 */
public class GuestWorkArea extends AbstractBean
{
	public Integer guest;
	public Integer workarea;

    public void checkRead( OctopusContext cntx ) throws BeanException
    {
        checkGroup( cntx, PersonalConfigAA.GROUP_READ_STANDARD );
    }
   
    public void checkWrite( OctopusContext cntx ) throws BeanException
    {
        checkGroup( cntx, PersonalConfigAA.GROUP_WRITE );
    }
}
