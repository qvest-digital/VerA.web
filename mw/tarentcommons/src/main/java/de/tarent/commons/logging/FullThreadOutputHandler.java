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
