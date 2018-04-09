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
import java.io.ByteArrayInputStream;
import java.beans.XMLDecoder;

/**
 * Converts from String to an Java object, using the Java XMLEndoder API.
 */
public class XMLDecoderConverter extends AbstractConverter {

    Class targetClass;

    public XMLDecoderConverter(Class targetClass) {
        this.targetClass = targetClass;
    }

    public Class getTargetType() {
        return targetClass;
    }
    public Class getSourceType() {
        return String.class;
    }

    public Object doConversion(Object sourceData) throws IllegalArgumentException {
        if (sourceData == null || "".equals(sourceData))
            return null;

        if (! (sourceData instanceof String))
            throw new IllegalArgumentException("Source data is not instance of String ("+sourceData.getClass() +")");

        XMLDecoder decoder = new XMLDecoder(new ByteArrayInputStream(((String)sourceData).getBytes()));
        Object result = decoder.readObject();
        decoder.close();

        if (result != null && ! targetClass.isAssignableFrom(result.getClass()))
            throw new IllegalArgumentException("Decoded type is not instance of "+targetClass+" ("+result.getClass() +")");

        return result;
    }
}
