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

package de.tarent.commons.datahandling.entity;

import java.util.EventObject;

/**
 * this class implements an Event to handle EntityListEvents
 * An EntityListEvent can be of the type INSERT, DELETE or UPDATE,
 * the type has to be specified in the contructor
 * @author Steffi Tinder, tarent GmbH
 *
 */

public class EntityListEvent extends EventObject {
    /** serialVersionUID */
	private static final long serialVersionUID = 3997106056581798888L;
	
	public final static int INSERT = 0;
    public final static int DELETE = 1;
    public final static int UPDATE = 2;   
    
    private int type;

    int firstRow = 0;
    int lastRow = Integer.MAX_VALUE;
    
    public EntityListEvent(Object source, int t) {
        super(source);
        type = t;
    }    

    public EntityListEvent(Object source, int firstRow, int lastRow, int t) {
        this(source, t);
        this.firstRow = firstRow;
        this.lastRow = lastRow;
    }

    public int getFirstRow() {
        return firstRow;
    }

    public int getLastRow() {
        return lastRow;
    }

    public int getType(){
        return type;
    }
}
