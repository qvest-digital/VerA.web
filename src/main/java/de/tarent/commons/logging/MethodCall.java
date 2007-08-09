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

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import de.tarent.commons.utils.Tools;

/**
 * Used for saving name and parameters of a method call in {@link ThreadLogger}.
 * 
 * @author Tim Steffens
 */
public class MethodCall {
	
	private static String LINE_SEPERATOR = de.tarent.commons.utils.StringTools.LINE_SEPERATOR;
	
	/** The time this Object was instanciatet in millseconds. */
	private final long invokeTime = System.currentTimeMillis();
	/** Class name */
	private String classname;
	/** Method name. */
	private String methodname;
	/** Free text note. */
	private String note;
	/** Parameter list */
	protected List parameters = new LinkedList();
	/** Variable list. */
	protected List variables = new LinkedList();

	/**
	 * Creates a new method call object.
	 */
	public MethodCall() {
		init(+2);
		this.note = null;
	}

	/**
	 * Creates a new method call object.
	 */
	public MethodCall(int logpos) {
		init(logpos + 2);
		this.note = null;
	}

	/**
	 * creates a new method call object with a method note.
	 */
	public MethodCall(String note) {
		init(+2);
		this.note = note;
	}

	/**
	 * creates a new method call object with a method note.
	 */
	public MethodCall(int logpos, String note) {
		init(logpos + 2);
		this.note = note;
	}

	/**
	 * Init the current methodcall instance and set the classname and the
	 * method name of the given <code>logpos</code>. Logpos define the
	 * position of an entry in the current stack.
	 * 
	 * @param logpos
	 */
	protected void init(int logpos) {
		StackTraceElement ste[];
		try {
			throw new RuntimeException();
		} catch (RuntimeException e) {
			ste = e.getStackTrace();
		}
		if (ste.length >= logpos) {
			classname = ste[logpos].getClassName();
			methodname = ste[logpos].getMethodName();
			return;
		}
		throw new RuntimeException("Illegal stacktrace depth. Want get logpos "
				+ logpos + " from this stack: " + Arrays.asList(ste));
	}

	/**
	 * Adds a new parameter to the end of the list of parameters
	 * 
	 * @param name name of the parameter
	 * @param value value of the parameter
	 */
	public void addParameter(String name, Object value) {
		parameters.add(new NamedObject(name, value));
	}

	public void addVariable(String name, Object value) {
		variables.add(new NamedObject(name, value));
	}

	public String getClassName() {
		return classname;
	}

	public String getMethodName() {
		return methodname;
	}

	public String getNote() {
		return note;
	}

	public Date getInvokeTime() {
		return new Date(invokeTime);
	}

	public List getParameters() {
		return Collections.unmodifiableList(parameters);
	}

	public List getVariables() {
		return Collections.unmodifiableList(variables);
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer(500);
		buffer.append(classname).append("#").append(methodname);
		buffer.append(" [at ").append(getInvokeTime()).append("]:").append(LINE_SEPERATOR);
		if (!parameters.isEmpty()) {
			buffer.append("      With parameters:").append(LINE_SEPERATOR);
			buffer.append(Tools.iteratorToString(parameters.iterator(), "        ", true, LINE_SEPERATOR, true));
		}
		if (!variables.isEmpty()) {
			buffer.append("      Local variables:").append(LINE_SEPERATOR);
			buffer.append(Tools.iteratorToString(variables.iterator(), "        ", true, LINE_SEPERATOR, true));
		}
		return buffer.toString();
	}
}
