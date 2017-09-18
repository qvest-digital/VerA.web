package de.tarent.octopus.response;

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
