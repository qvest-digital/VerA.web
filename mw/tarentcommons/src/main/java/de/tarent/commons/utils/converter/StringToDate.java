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

package de.tarent.commons.utils.converter;

import de.tarent.commons.utils.AbstractConverter;
import java.util.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class StringToDate extends AbstractConverter {

    String datePattern = "yy-M-d";
    String germanDatePattern = "d.M.yy";
    String dateTimePattern = "yy-M-d H:m:s";
    String germanDateTimePattern = "d.M.yy H:m:s";
    String shortDateTimePattern = "yy-M-d H:m";
    String shortGermanDateTimePattern = "d.M.yy H:m";

    DateFormat defautDf = new SimpleDateFormat();
    DateFormat date = new SimpleDateFormat(datePattern);
    DateFormat germanDate = new SimpleDateFormat(germanDatePattern);
    DateFormat dateTime = new SimpleDateFormat(dateTimePattern);
    DateFormat germanDateTime = new SimpleDateFormat(germanDateTimePattern);
    DateFormat shortDateTime = new SimpleDateFormat(shortDateTimePattern);
    DateFormat shortGermanDateTime = new SimpleDateFormat(shortGermanDateTimePattern);

    public Class getTargetType() {
	return Date.class;
    }

    public Class getSourceType() {
	return String.class;
    }

    public Object doConversion(Object sourceData) throws java.text.ParseException {
	String sourceString = ((String)sourceData).trim();
	if(sourceString.matches("[0-9]{1,2}\\.[0-9]{1,2}\\.[0-9]{2,4}\\ [0-9]{1,2}\\:[0-9]{1,2}\\:[0-9]{1,2}"))
		return germanDateTime.parse(sourceString);

	else if(sourceString.matches("[0-9]{1,2}\\.[0-9]{1,2}\\.[0-9]{2,4}\\ [0-9]{1,2}\\:[0-9]{1,2}"))
		return shortGermanDateTime.parse(sourceString);

	else if (sourceString.matches("[0-9]{2,4}\\-[0-9]{1,2}\\-[0-9]{1,2}\\ [0-9]{1,2}\\:[0-9]{1,2}\\:[0-9]{1,2}"))
	    return dateTime.parse(sourceString);

	else if (sourceString.matches("[0-9]{2,4}\\-[0-9]{1,2}\\-[0-9]{1,2}\\ [0-9]{1,2}\\:[0-9]{1,2}"))
	    return shortDateTime.parse(sourceString);

	else if (sourceString.matches("[0-9]{1,2}\\.[0-9]{1,2}\\.[0-9]{2,4}"))
	    return germanDate.parse(sourceString);

	else if (sourceString.matches("[0-9]{2,4}\\-[0-9]{1,2}\\-[0-9]{1,2}"))
	    return date.parse(sourceString);

	return defautDf.parse(sourceString);
    }
}
