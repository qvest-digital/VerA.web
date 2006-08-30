/* $Id: Context.java,v 1.6 2006/08/30 09:30:22 christoph Exp $
 * 
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
 * by Sebastian Mancke
 * signature of Elmar Geese, 1 June 2002
 * Elmar Geese, CEO tarent GmbH
 */
package de.tarent.octopus.server;

import java.util.LinkedList;

/**
 * This class gives a static access to the OctopusContext Object associated with the 
 * request of the current Thread. At the begin of the octopus request processing 
 * the context is set by an ThreadLocal Variable. So it can be obtained at later time.
 * Without passing a reference to it.
 * 
 * During the processing of an Octopus request the active OctpusContext may change 
 * depending on the current executed scope.
 *
 * @author <a href="mailto:sebastian@tarent.de">Sebastian Mancke</a>, <b>tarent GmbH</b>
 * @version 1.1
 */
public class Context {
	/** Hold a stack of context informations in a {@link LinkedList}. */
	private static ThreadLocal currentContext = new ThreadLocal() {
		public Object initialValue() {
			return new LinkedList();
		}
	};

	/**
	 * Returns the current active OctopusContext for this thread
	 */
	public static OctopusContext getActive() {
		LinkedList stack = (LinkedList) currentContext.get();
		if (!stack.isEmpty())
			return (OctopusContext) stack.getLast();
		else
			return null;
	}

	/**
	 * Add the current active OctopusContext on the content stack.
	 */
	public static void addActive(OctopusContext context) {
		((LinkedList) currentContext.get()).addLast(context);
	}

	/**
	 * Remove one context information from the context stack.
	 */
	public static void clear() {
		LinkedList stack = (LinkedList) currentContext.get();
		if (!stack.isEmpty())
			stack.removeLast();
	}
}
