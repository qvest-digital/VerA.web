/**
 * tarent-octopus, Webservice Data Integrator and Application Server
 * Copyright © 2002–2015 tarent solutions GmbH and its contributors
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
package de.tarent.octopus.request;

/** 
 * Exception, die geworfen werden soll, wenn 
 * Probleme, wie z.B. fehlende Attribute und Anweidsungen wärend der
 * Taskabarbeitung im ActionManager auftreten.
 *
 * @author <a href="mailto:mancke@mancke-software.de">Sebastian Mancke</a>, <b>tarent GmbH</b>
 */
public class TcTaskProzessingException extends Exception {
    /**
	 * serialVersionUID = -3146183468486361425L
	 */
	private static final long serialVersionUID = -3146183468486361425L;

	public TcTaskProzessingException(String message) {
        super(message);
    }
}
