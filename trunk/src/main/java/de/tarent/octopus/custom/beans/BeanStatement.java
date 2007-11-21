/*
 * veraweb,
 * Veranstaltungsmanagment veraweb
 * Copyright (c) 2005-2007 tarent GmbH
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
 * interest in the program 'veraweb'
 * Signature of Elmar Geese, 21 November 2007
 * Elmar Geese, CEO tarent GmbH.
 */

/*
 * $Id: BeanStatement.java,v 1.1 2007/06/20 11:56:51 christoph Exp $
 * 
 * Created on 20.01.2006
 */
package de.tarent.octopus.custom.beans;

/**
 * Diese Schnittstelle beschreibt gekapselte PreparedStatements, die auf
 * Beans arbeiten. 
 * 
 * @author mikel
 */
public interface BeanStatement {
    /**
     * Diese Methode f�hrt das Statement auf der �bergebenen Bean aus. 
     * 
     * @param bean Bean, auf der das Statement ausgef�hrt wird.
     * @return R�ckgabe von Updates etc.
     */
    int execute(Bean bean) throws BeanException;
}
