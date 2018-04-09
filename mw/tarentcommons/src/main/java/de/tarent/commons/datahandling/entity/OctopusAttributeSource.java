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

/**
 * 
 */
package de.tarent.commons.datahandling.entity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import de.tarent.octopus.server.OctopusContext;

/**
 * @author Alex Maier
 *
 */
public class OctopusAttributeSource implements AttributeSource {
	OctopusContext oct;
    List attributeNames;
	
    public OctopusAttributeSource(OctopusContext oct){
    	this.oct=oct;
    }
    
    public OctopusAttributeSource(OctopusContext oct, List attributeNames){
    	this.oct=oct;
    	this.attributeNames=attributeNames;
    }
    
    public OctopusAttributeSource(OctopusContext oct, String attributeName){
    	this.oct=oct;
    	addAttributeName(attributeName);
    }    
    
    public OctopusAttributeSource addAttributeName(String attributeName){    	
    	if(attributeNames==null)
    		attributeNames = new LinkedList();    		
    	attributeNames.add(attributeName);
    	return this;
    }    
    
	/* (non-Javadoc)
	 * @see de.tarent.commons.datahandling.entity.AttributeSource#getAttribute(java.lang.String)
	 */
	public Object getAttribute(String attributeName) {	
		Object param = oct.getRequestObject().getParam(attributeName);
		//trim the spaces
		if(String.class.isInstance(param))
			return ((String)param).trim();
		return param;
	}

	public Class getAttributeType(String attributeName) {
		Object param = oct.getRequestObject().getParam(attributeName);
		return param == null ? null : param.getClass();
	}

	/* (non-Javadoc)
	 * @see de.tarent.commons.datahandling.entity.AttributeSource#getAttributeNames()
	 */
	public List getAttributeNames() {
		if(attributeNames==null){
			Set params = oct.getRequestObject().getRequestParameters().keySet();			
	        Iterator iter = params.iterator();
	        int count = params.size();
	        attributeNames = new ArrayList(count);
	        while (iter.hasNext()){
	            attributeNames.add(iter.next());
	        }			
		}		
		return attributeNames;
	}

}
