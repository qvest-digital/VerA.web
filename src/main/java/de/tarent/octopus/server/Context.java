package de.tarent.octopus.server;

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
