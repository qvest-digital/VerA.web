package de.tarent.octopus.request.directcall;

/*-
 * tarent-octopus, Webservice Data Integrator and Application Server
 * Copyright © 2002–2018 tarent solutions GmbH and its contributors
 * Copyright © 2015, 2017, 2018 mirabilos (t.glaser@tarent.de)
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

import de.tarent.octopus.config.TcModuleLookup;
import de.tarent.octopus.request.TcEnv;

import java.io.File;

/**
 * Diese Klasse liefert dem Octopus notwendige Daten.
 */
class DirectCallModuleLookup implements TcModuleLookup {
	/**
	 * DirectCallModuleLookup
	 */
	private final OctopusDirectCallStarter octopusDirectCallStarter;

	/**
	 * @param starter
	 */
	DirectCallModuleLookup(OctopusDirectCallStarter starter) {
		octopusDirectCallStarter = starter;
	}

	public File getModulePath(String module) {
		String realPath = octopusDirectCallStarter.getEnv().getValue(
		    TcEnv.KEY_PATHS_ROOT) + "modules/" + module + "/";

		if (realPath.length() != 0) {
			final File res = new File(realPath);
			if (res.exists())
				return res;
		}
		return null;
	}
}
