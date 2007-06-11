/*
 * tarent-octopus common,
 * an opensource webservice and webapplication framework (common part)
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
 * interest in the program 'tarent-octopus common'
 * Signature of Elmar Geese, 11 June 2007
 * Elmar Geese, CEO tarent GmbH.
 */

/*
 * tarent-octopus common worker,
 * tarent-octopus common worker
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
 * interest in the program 'tarent-octopus common worker'
 * Signature of Elmar Geese, 11 June 2007
 * Elmar Geese, CEO tarent GmbH.
 */

package de.tarent.octopus.commons;

import javax.jws.WebMethod;

import de.tarent.commons.web.ParametersProcessor;
import de.tarent.commons.web.WebFormat;
import de.tarent.octopus.content.annotation.Result;

/**
 * Set a {@link WebFormat} instance or a {@link ParametersProcessor}
 * instance into the octopus content.
 */
public class CommonWebWorker {
	/**
	 * Set a {@link WebFormat} instance into the octopus content.
	 * 
	 * @return
	 */
	@WebMethod
	@Result("format")
	public WebFormat getWebFormat() {
		return new WebFormat();
	}

	/**
	 * Set a {@link ParametersProcessor} instance into the octopus content.
	 * 
	 * @return
	 */
	@WebMethod
	@Result("parametersProcessor")
	public ParametersProcessor getParametersProcessor() {
		return new ParametersProcessor();
	}
}
