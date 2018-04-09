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

package de.tarent.commons.messages;

/**
 * This simple message helper class help you to i18n your application
 * and make error messages compile safe. Use this with the static
 * {@link MessageHelper#init() init}-method of the {@link MessageHelper}.
 *
 * @author Christoph Jerolimov, tarent GmbH
 */
public interface Message {
	/**
	 * Return the key of the current message.
	 *
	 * @return Key of current message.
	 */
	public String getKey();

	/**
	 * Return the source path of the current message. For example the full
	 * resourcepath (Classpath incl. the absolut position of the jar file
	 * which include this.)
	 *
	 * @return Absolut source position of current message.
	 */
	public String getSource();

	/**
	 * Return the current message in a plain format, incl. <code>{0}</code>
	 * as placeholder for arguments.
	 *
	 * @return
	 */
	public String getPlainMessage();

//	/**
//	 * This was the short form for the multiple getMessage methods, but it is
//	 * only available in java 5.
//	 *
//	 * @param arguments
//	 * @return
//	 */
//	public String getMessage(Object... arguments);

	/**
	 * Return the transformed message with no argument.
	 *
	 * @return Message
	 */
	public String getMessage();

	/**
	 * Return the transformed message with one argument.
	 *
	 * @param arg0
	 * @return Message
	 */
	public String getMessage(Object arg0);

	/**
	 * Return the transformed message with two arguments.
	 *
	 * @param arg0
	 * @param arg1
	 * @return Message
	 */
	public String getMessage(Object arg0, Object arg1);

	/**
	 * Return the transformed message with three arguments.
	 *
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @return Message
	 */
	public String getMessage(Object arg0, Object arg1, Object arg2);

	/**
	 * Return the transformed message with four arguments.
	 *
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @param arg3
	 * @return Message
	 */
	public String getMessage(Object arg0, Object arg1, Object arg2, Object arg3);

	/**
	 * Return the transformed message with five arguments.
	 *
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @param arg3
	 * @param arg4
	 * @return Message
	 */
	public String getMessage(Object arg0, Object arg1, Object arg2, Object arg3, Object arg4);

	/**
	 * Return the transformed message with flexible arguments as an array.
	 *
	 * @param arguments
	 * @return Message
	 */
	public String getMessage(Object arguments[]);
}
