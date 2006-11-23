/* $Id: TcVelocityResponseLogger.java,v 1.2 2006/11/23 14:33:30 schmitz Exp $
 * tarent-octopus, Webservice Data Integrator and Applicationserver
 * Copyright (C) 2002 tarent GmbH
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
 * by Christoph Jerolimov.
 * signature of Elmar Geese, 1 June 2002
 * Elmar Geese, CEO tarent GmbH
 */

package de.tarent.octopus.response;

import org.apache.commons.logging.Log;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.log.LogSystem;

import de.tarent.octopus.logging.LogFactory;

public class TcVelocityResponseLogger implements LogSystem {
	/** Der Logger */
	private static Log logger = LogFactory.getLog(TcVelocityResponseLogger.class);

	public TcVelocityResponseLogger () {
		// do Nothing
	}
	
	public void init (RuntimeServices rsvc) {
		// do Nothing
	}
	
	public void logVelocityMessage(int level, String message) {
		switch (level) {
			case 1:
  				logger.trace(message);
				break;
			case 2:
				logger.debug(message);
				break;
			case 3:
				logger.warn(message);
				break;
			default:
				logger.error("[Unknown Level (" + level + ")] " + message);
				break;
		}
	}
}
