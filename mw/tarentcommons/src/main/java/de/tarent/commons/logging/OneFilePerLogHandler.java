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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class OneFilePerLogHandler extends Handler {
	protected LogFormatter formatter = new LogFormatter();
	protected String path;
	protected String encoding;

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public String getEncoding() {
		if (encoding != null && encoding.length() != 0)
			return encoding;
		else
			return "UTF-8";
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getPath() {
		if (path != null && path.length() != 0)
			return path;
		else
			return System.getProperty("user.home");
	}

	protected String getFilePrefix() {
		return new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss-SSS_").format(new Date());
	}

	protected String getPlainMessage(String message) {
		StringBuffer buffer = new StringBuffer(message.length());

		char c[] = message.toCharArray();
		for (int i = 0; i < c.length; i++) {
			if ("-_.,".indexOf(c[i]) != -1 ||
					(c[i] >= 'a' && c[i] <= 'z') ||
					(c[i] >= 'A' && c[i] <= 'Z') ||
					(c[i] >= '0' && c[i] <= '9')) {
				buffer.append(c[i]);
			} else if (",;:@$~".indexOf(c[i]) != -1) {
				buffer.append('.');
			} else if (" ".indexOf(c[i]) != -1) {
				buffer.append('_');
			} else if ("\r\n".indexOf(c[i]) != -1) {
				break;
			}
		}
		return buffer.toString();
	}

	protected File getNonExistingFile(String message) {
		String filename = getFilePrefix() + getPlainMessage(message);
		if (filename.length() > 220)
			filename = filename.substring(0, 220);
		File logfile = new File(getPath(), filename + ".log");
		for (int i = 1; logfile.exists(); i++) {
			logfile = new File(getPath(), filename + "." + i + ".log");
		}
		if (!logfile.getParentFile().exists()) {
			logfile.getParentFile().mkdirs();
		}
		return logfile;
	}

	protected Writer getWriter(String message) throws IOException {
		OutputStream out = null;
		try {
			File file = getNonExistingFile(message);
			out = new FileOutputStream(file);
			out = new BufferedOutputStream(out);
			return new OutputStreamWriter(out, getEncoding());
		} catch (IOException e) {
			if (out != null)
				out.close();
			throw e;
		}
	}

	public void publish(LogRecord record) {
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(getWriter(record.getMessage()));

			formatter.format(writer, record);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (writer != null)
				writer.close();
		}
	}

	public void flush() {
		// Nothing todo here.
	}

	public void close() throws SecurityException {
		// Nothing todo here.
	}
}
