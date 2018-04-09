/*
 * tarent-octopus annotation extension,
 * an opensource webservice and webapplication framework (annotation extension)
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
 * interest in the program 'tarent-octopus annotation extension'
 * Signature of Elmar Geese, 11 June 2007
 * Elmar Geese, CEO tarent GmbH.
 */

package de.tarent.octopus.content.annotation;

import java.lang.annotation.*;

/**
 * Element Kennzeichnung eines Parameters als optionalen Parameter.
 * Der Defaultwert ist 'nicht optional', wenn ein WorkerParameter diese
 * Annotation nicht besitzt wird er somit als zwingen erforderlich angesehen.
 * <p>Kann in Verbindung mit JSR181 Annotations (WebService Meta Data)
 * zur Beschreibung von Workern verwendet werden.
 *
 *
 * @see javax.jws.WebMethod;
 * @see http://jcp.org/aboutJava/communityprocess/final/jsr181/index.html
 */
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = {ElementType.PARAMETER})
public @interface Optional {

    /**
     * Beschreibung des Objektes
     */
    boolean value() default true;
}
