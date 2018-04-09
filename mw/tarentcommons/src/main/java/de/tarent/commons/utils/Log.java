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

/*
 * Copyright (c) tarent GmbH
 * Bahnhofstrasse 13 . 53123 Bonn
 * www.tarent.de . info@tarent.de
 *
 * Created on 13.10.2005
 */

package de.tarent.commons.utils;

import de.tarent.commons.logging.LogFactory;

/**
 * This class is a static wrapper around the tarent-commons LogFactory, based on Apache Commons Logging.
 *
 * @author Michael Kleinhenz (m.kleinhenz@tarent.de)
 */
public class Log
{
    public static void info(Class source, Object message)
    {
        org.apache.commons.logging.Log log = LogFactory.getLog(source);
        log.info(message);
    }

    public static void debug(Class source, Object message)
    {
        org.apache.commons.logging.Log log = LogFactory.getLog(source);
        log.debug(message);
    }

    public static void debug(Class source, Throwable exception)
    {
        org.apache.commons.logging.Log log = LogFactory.getLog(source);
        log.debug("Exception thrown.", exception);
    }

    public static void fatal(Class source, Object message)
    {
        org.apache.commons.logging.Log log = LogFactory.getLog(source);
        log.fatal(message);
    }

    public static void error(Class source, Object message)
    {
        org.apache.commons.logging.Log log = LogFactory.getLog(source);
        log.error(message);
    }

    public static void fatal(Class source, Object message, Throwable throwable)
    {
        org.apache.commons.logging.Log log = LogFactory.getLog(source);
        log.fatal(message, throwable);
    }

    public static void error(Class source, Object message, Throwable throwable)
    {
        org.apache.commons.logging.Log log = LogFactory.getLog(source);
        log.error(message, throwable);
    }
}
