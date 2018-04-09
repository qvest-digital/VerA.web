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

package de.tarent.commons.web;

import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Contains a method for encoding a url with many parameters objects.
 *
 * @author Tim Steffens
 *
 */
public class ParametersProcessor {

	List commonParametersList;

	public ParametersProcessor() {
		commonParametersList = new LinkedList();
	}

	/**
	 * adds {@code parameters} to the list of common parameters
	 * @param parameters
	 */
	public void addCommonParameters(Parameters parameters) {
		if (parameters != null) {
			commonParametersList.add(parameters);
		}
	}

	public String encodeUrl(String url) throws UnsupportedEncodingException {
		return encodeUrl(url, null);
	}

	/**
	 * Attaches the common parameters and those in {@code parametersList}.
	 */
	public String encodeUrl(String url, Parameters parameters ) throws UnsupportedEncodingException {

		String newUrl =
			url == null
				? ""
				: url;
		if (parameters != null) {
			newUrl = parameters.encodeUrl(newUrl);
		}

		Iterator parametersIt = commonParametersList.iterator();
		while (parametersIt.hasNext()) {
			Parameters commonParameters = (Parameters) parametersIt.next();
			newUrl = commonParameters.encodeUrl(newUrl);
		}

		return newUrl;
	}

}
