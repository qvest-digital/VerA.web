/*
 * tarent-octopus bean extension,
 * an opensource webservice and webapplication framework (bean extension)
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
 * interest in the program 'tarent-octopus bean extension'
 * Signature of Elmar Geese, 11 June 2007
 * Elmar Geese, CEO tarent GmbH.
 */

/*
 * $Id: Person.java,v 1.2 2007/06/11 13:24:36 christoph Exp $
 * 
 * Created on 03.05.2006
 */
package de.tarent.beans;

import java.util.Date;

import de.tarent.octopus.beans.MapBean;

/**
 * This bean class represents an entry of the table <code>person</code> of the
 * test database schema. 
 * 
 * @author mikel
 */
public class Person extends MapBean {
    //
    // bean attributes
    //
    /** pk_person integer */
    public Integer id;
    /** fk_firma integer */
    public Integer firmId;
    /** vorname varchar(50) */
    public String forename;
    /** nachname varchar(50) */
    public String surname;
    /** geburtstag date */
    public Date dateOfBirth;
    
    //
    // sample bean
    //
    /** sample {@link Person} bean for easier use of the bean framework */
    public final static Person SAMPLE = new Person();
}
