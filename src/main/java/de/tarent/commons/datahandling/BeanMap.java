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

package de.tarent.commons.datahandling;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import de.tarent.commons.utils.StringTools;
import de.tarent.commons.utils.Tools;

/**
 * Class for accessing attributes via reflection
 * 
 * @author tim
 *
 */

/*
 * Aus der Java-Beans Spezifikation, Version 1.01:
 *    
 *   8.3.1 Simple properties
 *   
 *   By default, we use design patterns to locate properties by looking for methods of the form:
 *   
 *      public < PropertyType> get< PropertyName>();
 *      public void set< PropertyName>(< PropertyType> a);
 *   
 *   If we discover a matching pair of get<PropertyName> and set<PropertyName> methods
 *   that take and return the same type, then we regard these methods as defining a read-write property
 *   whose name will be <propertyName>. We will use the get<PropertyName> method
 *   to get the property value and the set<PropertyName> method to set the property value. The
 *   pair of methods may be located either in the same class or one may be in a base class and the
 *   other may be in a derived class.
 *   
 *   If we find only one of these methods, then we regard it as defining either a read-only or a writeonly
 *   property called <propertyName>
 *   
 *   By default we assume that properties are neither bound nor constrained (see Section 7).
 *   So a simple read-write property foo might be represented by a pair of methods:
 *   
 *      public Wombat getFoo();
 *      public void setFoo(Wombat w);
 *   
 *   8.3.2 Boolean properties
 *   
 *   In addition, for boolean properties, we allow a getter method to match the pattern:
 *   public boolean is< PropertyName>();
 *   
 *   This is<PropertyName> method may be provided instead of a get<PropertyName> method,
 *   or it may be provided in addition to a get<PropertyName> method.
 *   In either case, if the is<PropertyName> method is present for a boolean property then we will
 *   use the is<PropertyName> method to read the property value.
 *   
 *   An example boolean property might be:
 *   
 *      public boolean isMarsupial();
 *      public void setMarsupial(boolean m);
 */

public class BeanMap implements Map {
	
	private class Property {
		String name;
		Class type;
		boolean canBeRead;
		boolean canBeWritten;
		
		public Property(String name, Class type, boolean canBeRead, boolean canBeWritten) {
			this.name = name;
			this.type = type;
			this.canBeRead = canBeRead;
			this.canBeWritten = canBeWritten;
		}
		
		public boolean isCanBeRead() {
			return canBeRead;
		}
		
		public void setCanBeRead(boolean canBeRead) {
			this.canBeRead = canBeRead;
		}
		
		public boolean isCanBeWritten() {
			return canBeWritten;
		}
		
		public void setCanBeWritten(boolean canBeWritten) {
			this.canBeWritten = canBeWritten;
		}
		
		public String getName() {
			return name;
		}
		
		public void setName(String name) {
			this.name = name;
		}
		
		public Class getType() {
			return type;
		}
		
		public void setType(Class type) {
			this.type = type;
		}
	}
 
    /**
     * Alle Methoden dieser Bean.
     */
    protected Method[] methods = null;

    /**
     * HashSet-Repraesentation dieser Bean
     */
    protected HashMap properties;
    

    public BeanMap()
    {
        super();
        this.methods = this.getMethods();
        this.properties = this.getPropertyMap();
    }
    
    /**
     * Returns all methods of this class, except those that are inheritet from Map and Object.
     * @return
     */
    private Method[] getMethods() {
    	Method[] allMethods = this.getClass().getMethods();
    	Vector filteredMethods = new Vector();
    	for (int i = 0; i < allMethods.length; i++) {
    		if (!Tools.arrayContains(BeanMap.class.getMethods(), allMethods[i])) {
    			filteredMethods.add(allMethods[i]);
    		}
    	}
    	Method[] returnMethods = new Method[filteredMethods.size()];
    	for (int i = 0; i < returnMethods.length; i++) {
    		returnMethods[i] = (Method) filteredMethods.get(i);
    	}
		return returnMethods;
	}

	/**
     * Returns the get method appropriate to {@code attribute}.
     * 
     * @param attribute the attribute corresponding to the searched get method
     * @param ignoreCase true, if the case should be ignored and false otherwise
     * @return the get-method appropriate to {@code attribute} or null, if none is found.
     */
    private Method getMatchingGetMethod(String attribute, boolean ignoreCase)
    {       
        for (int i = 0; i < methods.length; i++)
        {
            String methodCandidate = methods[i].getName();
            if ((!ignoreCase &&
            		methodCandidate.equals("get" + StringTools.capitalizeFirstLetter(attribute))) ||
            	(ignoreCase &&
            		methodCandidate.toLowerCase().equals("get" + attribute.toLowerCase()))) {
            	//System.out.println("Returned " + methods[i].getName());
            	return methods[i];	
            }
        }
        
        return null;
    }
    
    /**
     * Liefert den Typ eines Attributs in dieser Bean zurück.
     * 
     * @param attribute - Gesuchtes Attribut
     * @return Typ des Attributs
     * @throws NoSuchFieldException 
     * @throws SecurityException 
     */
    public Class getValueType(String attribute) throws SecurityException, NoSuchFieldException
    {
        if (!containsKey(attribute)) {
            throw new ClassCastException("Key not found.");        	
        }
        return this.getClass().getDeclaredField(attribute).getType();
    }
    
    /**
     * Diese Methode gibt den korrespondierenden Wert eines übergebenen Keys 
     * in der (virtuellen) Map zurück.
     * 
     * 
     * @param key - Key des gesuchten Werts.
     * @return Wert, oder null, falls Key nicht gefunden.
     */
    private Object getValue(Object key) throws NoSuchFieldException
    {
        return getValueCase(key, false);
    }

    /**
     * Returns the value of the attribute specified by {@code key}. If {@code ignoreCase}
     * is true, the case is ignored, otherwise not.
     * 
     * @param key the name of the attribute
     * @param ignoreCase true, if the case schould be ignored, otherwise false.
     * @return the value of the attribute specified by {@code key}
     * @throws NoSuchFieldException if no corresponding attribtue could be found
     */
	private Object getValueCase(Object key, boolean ignoreCase) throws NoSuchFieldException {
		Iterator iter = properties.keySet().iterator();
        while (iter.hasNext())
        {
            String aPropertyName = (String) iter.next();
            if ( 	(!ignoreCase && aPropertyName.equals(key)) ||
            		(ignoreCase && aPropertyName.toLowerCase().equals(
            				((String) key).toLowerCase())))
            {
                try 
                {
                    Method thisMethod = this.getMatchingGetMethod(aPropertyName, ignoreCase);
                    Object thisValue = thisMethod.invoke(this, new Object[] {});
                    return thisValue;
                } 
                catch (Exception e) 
                {
                    throw new RuntimeException(e);
                }
            }
        }
        // Key wurde nicht gefunden
        throw new NoSuchFieldException();
	}

    /**
     * Diese Methode gibt den korrespondierenden Wert eines übergebenen Keys 
     * in der (virtuellen) Map zurück und ignoriert dabei die Groß-Kleinschreibung.
     * 
     * @param key - Key des gesuchten Werts.
     * @return Wert, oder null, falls Key nicht gefunden.
     */
    private Object getValueIgnoreCase(Object key) throws NoSuchFieldException
    {
        return getValueCase(key, true);
    }

    /**
     * Setzt die Bean-Properties in ein HashSet um.
     */
    private HashMap getPropertyMap()
    {
        // Erster Pass - holen der Kandidaten
        HashMap properties = new HashMap();
        for (int i = 0; i < methods.length; i++) {
            Method thisMethod = methods[i];
            if (thisMethod.getName().startsWith("get")) {
            	String pureName = StringTools.minusculizeFirstLetter(thisMethod.getName().replaceFirst("get", ""));
            		properties.put(pureName, new Property(pureName, methods[i].getReturnType(), true, false));
            }
            else if (thisMethod.getName().startsWith("is")) {
            	String pureName = StringTools.minusculizeFirstLetter(thisMethod.getName().replaceFirst("is", ""));
        		properties.put(pureName, new Property(pureName, methods[i].getReturnType(), true, false));
            }
            else if (thisMethod.getName().startsWith("set")) {
            	String pureName = StringTools.minusculizeFirstLetter(thisMethod.getName().replaceFirst("set", ""));
            	if (properties.containsKey(pureName) &&
            			((Property) properties.get(pureName)).getType().equals(methods[i].getParameterTypes()[0])) {
            		((Property) properties.get(pureName)).setCanBeWritten(true);
            	}
            	else {
            		properties.put(pureName, new Property(pureName, methods[i].getParameterTypes()[0], false, true));
            	}
            }
        }
        
        // Zweiter Pass - Korrelieren
        //System.out.println(properties.keySet().toString());
        return properties;
    }

	/**
     * @see java.util.Map#size()
     */
    public int size()
    {
        return this.properties.size();
    }

    /**
     * @see java.util.Map#isEmpty()
     */
    public boolean isEmpty()
    {
        return this.properties.isEmpty();
    }

    /**
     * @see java.util.Map#containsKey(java.lang.Object)
     */
    public boolean containsKey(Object key)
    {
        return this.properties.containsKey(key);
    }

    /**
     * @see java.util.Map#keySet()
     */
    public Set keySet()
    {
        return properties.keySet();
    }
    
    /**
     * 
     * @see java.util.Map#containsValue(java.lang.Object) 
     */
    public boolean containsValue(Object value)
    {
        if (value == null) return true;
        
        Iterator iter = properties.keySet().iterator();
        while (iter.hasNext())
        {
            String aPropertyName = (String) iter.next();
            try 
            {
                Method thisMethod = this.getMatchingGetMethod(aPropertyName, false);
                if (thisMethod != null) {
	                Object thisValue = thisMethod.invoke(this, new Object[] {});
	                if (thisValue != null && thisValue.equals(value)) {
	                    return true;
	                }
                }
            } 
            catch (Exception e) 
            {
                throw new RuntimeException(e);
            }
        }
        
        return false;
    }

    /**
     * @see java.util.Map#get(java.lang.Object)
     */
    public Object get(Object key)
    {
        Object thisValue = null;
        try
        {
            thisValue = this.getValue(key);
        }
        catch (NoSuchFieldException e)
        {
            throw new ClassCastException("Key not found.");
        }
        return thisValue;
    }

    /**
     * Diese Operation ist nicht unterstützt, weil sich lt. Standard 
     * die Werte in der zurückgegebenen Collection synchron ändern, wenn sich
     * in der zugrundeliegenden Map etwas ändert.
     * 
     * @see java.util.Map#values()
     */
    public Collection values()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Diese Operation ist nicht unterstützt, weil sich lt. Standard 
     * die Werte im zurückgegebenen Set synchron ändern, wenn sich
     * in der zugrundeliegenden Map etwas ändert.
     * 
     * @see java.util.Map#entrySet()
     */
    public Set entrySet()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * @see java.util.Map#put(java.lang.Object, java.lang.Object)
     */
    public Object put(Object arg0, Object arg1)
    {
        throw new UnsupportedOperationException();
    }

    /**
     * @see java.util.Map#remove(java.lang.Object)
     */
    public Object remove(Object key)
    {
        throw new UnsupportedOperationException();
    }

    /**
     * @see java.util.Map#putAll(java.util.Map)
     */
    public void putAll(Map arg0)
    {
        throw new UnsupportedOperationException();
    }

    /**
     * @see java.util.Map#clear()
     */
    public void clear()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * @see java.util.Map#get(java.lang.Object)
     */
    public Object getIgnoreCase(Object key)
    {
        Object thisValue = null;
        try
        {
            thisValue = this.getValueIgnoreCase(key);
        }
        catch (NoSuchFieldException e)
        {
            throw new ClassCastException("Key not found.");
        }
        return thisValue;
    }
}
