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

package de.tarent.commons.utils;

/**
 * Interface for converter between data types
 */
public interface Converter {

    /**
     * Returns the Name of the converter
     */
    public String getConverterName();

    /**
     * Returns the target type of the converter
     */
    public Class getTargetType();

    /**
     * Returns the source type of the converter
     */
    public Class getSourceType();

    /**
     * Converts a data Item from the source to the target type.
     * If the supplied sourceData is null, a null should be returned.
     *
     *
     * @param sourceData the convertable data. null is allowed here.
     * @throws IllegalArgumentException if the input data is not convertable by this converter
     */
    public Object convert(Object sourceData) throws IllegalArgumentException;

}
