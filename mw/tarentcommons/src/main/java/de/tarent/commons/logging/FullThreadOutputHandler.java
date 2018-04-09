/*
 * tarent commons,
 * a set of common components and solutions
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
 * interest in the program 'tarent commons'
 * Signature of Elmar Geese, 14 June 2007
 * Elmar Geese, CEO tarent GmbH.
 */

package de.tarent.commons.logging;

import java.io.PrintWriter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class FullThreadOutputHandler extends Handler {
	protected LogFormatter formatter = new LogFormatter();
	protected PrintWriter outputWriter;

	public void setOutput(String output) {
		if (output != null && output.indexOf("out") != -1) {
			outputWriter = new PrintWriter(System.out);
		} else {
			outputWriter = new PrintWriter(System.err);
		}
	}

	public void publish(LogRecord record) {
		formatter.format(outputWriter, record);
		outputWriter.flush();
	}

	public void flush() {
		// Nothing todo here.
	}

	public void close() throws SecurityException {
		// Nothing todo here.
	}
}
