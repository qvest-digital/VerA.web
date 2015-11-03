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
 * Created on 08.02.2005
 */
package de.tarent.octopus.beans;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Abstraktes Bean, das Map implementiert, damit in Velocity
 * Membervariablen direkt angesprochen werden können.
 * 
 * @author Michael Klink, Alex Steeg, Christoph Jerolimov
 * @version 1.3
 */
public class MapBean extends AbstractMap implements Bean {
    //
    // private Member
    //
	private boolean modified;
	private List errors = new ArrayList();
    private static Logger logger = Logger.getLogger(MapBean.class.getName());

    //
    // Konstruktor
    //
    /**
     * Dieser Konstruktor sammelt die nicht-statischen und nicht-finalen Felder
     * und die Methoden, die über Reflection bekannt sind, ein und legt sie lokal 
     * in Membervariablen ab.
     */
	public MapBean() {
		loadClass(this);
	}

    //
    // Implementierung von Bean
    //
    /**
     * Fügt eine Nachricht der Bean-Fehlerliste hinzu.
     * 
     * @param message neue Nachricht
     */
	public void addError(String message) {
		errors.add(message);
	}

    /**
     * Gibt eine nur-lese Liste mit Fehlermeldungen (Strings) zurück.
     * 
     * @return Fehlerliste
     */
	public List getErrors() {
		return errors;
	}

    /**
     * Gibt <code>true</code> zurück, wenn keine Fehler beim Erzeugen der Bean
     * aufgetreten sind, anderfalls <code>false</code>.
     * 
     * @return <code>true</code>, wenn die Bean fehlerfrei ist.
     */
	public boolean isCorrect() {
		return errors.isEmpty();
	}

    /**
     * Gibt <code>true</code> zurück, wenn das erzeugte der Bean
     * geändert wurde, anderfalls <code>false</code>.
     * 
     * @return <code>true</code>, wenn die Bean unmodifiziert ist.
     */
	public boolean isModified(){
		return modified;
	}

    /**
     * Setzt das Modified-Flag.
     * 
     * @param modified neues Modified-Flag
     */
	public void setModified(boolean modified){
		this.modified = modified;
	}

    /**
     * Überprüft das Bean auf innere Vollständigkeit.
     * Diese Methode sollte von konkreten Bohnen mit Ansprüchen an
     * Vollständigkeit passend überschrieben werden.
     * 
     * @throws BeanException bei Unvollständigkeit
     */
	public void verify() throws BeanException {
	}

    /**
     * Gibt eine String-Liste mit den Field-Keys zurück. 
     * 
     * @return String-Liste der Field-Keys
     */
	public Set getFields() {
		return getBeanAccessor(this).getFieldNames();
	}

    /**
     * Gibt den Inhalt eines Bean-Feldes zurück.
     * 
     * @param key Feldschlüssel
     * @return Feldinhalt
     */
	public Object getField(String key) throws BeanException {
		try {
			return getBeanAccessor(this).getField(key).get(this);
		} catch (Exception e) {
			throw new BeanException("Fehler beim Lesen von Feld "+key, e);
		}
	}

    /**
     * Setzt den Inhalt eines Bean-Feldes.
     * 
     * @param key Feldschlüssel
     * @param value neuer Feldwert
     */
	public void setField(String key, Object value) throws BeanException {
		try {
			BeanAccessor accessor = getBeanAccessor(this);
			Method setter = accessor.getSetter(key);
			
			if (setter == null) {
				Field field = accessor.getField(key);
				try {
					value = BeanFactory.transform(value, field.getType());
					field.set(this, value);
				} catch (BeanException e) {
                    logger.log(Level.WARNING, "Fehler beim indirekten Setzen des Inhalts des Felds " + key + " mittels Setter", e);
					addError(e.getLocalizedMessage());
				}
			} else {
				try {
					value = BeanFactory.transform(value, setter.getParameterTypes()[0]);
					setter.invoke(this, new Object[] { value });
				} catch (BeanException e) {
                    logger.log(Level.WARNING, "Fehler beim direkten Setzen des Inhalts des Felds " + key, e);
					addError(e.getLocalizedMessage());
				}
			}
		} catch (Exception e) {
            logger.log(Level.WARNING, "Fehler beim Setzen des Inhalts des Felds " + key, e);
			throw new BeanException("Fehler beim Setzten des Felds '" + key + "' mit dem Wert '" + value + "'.", e);
		}
	}

    /**
     * Gibt die Klasse eines Bean-Feldes zurück.
     * 
     * @param key Feldschlüssel
     * @return Klasse des Bean-Fields
     */
	public Class getFieldClass(String key) {
		try {
			return getClass().getField(key).getType();
		} catch (Exception e) {
            logger.log(Level.WARNING, "Fehler beim Holen der Klasse des Felds " + key, e);
			return null;
		}
	}
    


    //
    // Überschreibungen von Object
    //
    /**
     * Diese Methode liefert eine Selbstbeschreibung der Bean zurück, in der neben
     * dem Klassennamen auch die Feldwerte und das Korrektheitsflag stehen.
     */
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(getClass().getName());
		buffer.append(" {");
		Field field[] = getClass().getFields();
		for (int i = 0; i < field.length; i++) {
            if (Modifier.isStatic(field[i].getModifiers()))
                continue;
			buffer.append(field[i].getName()).append("=");
			try {
				buffer.append(field[i].get(this));
			} catch (IllegalArgumentException e) {
                logger.log(Level.WARNING, "Fehler beim Lesen des Inhalts des Felds " + field[i].getName(), e);
			} catch (IllegalAccessException e) {
                logger.log(Level.WARNING, "Fehler beim Lesen des Inhalts des Felds " + field[i].getName(), e);
			}
			buffer.append(", ");
		}
		buffer.append("isCorrect=");
		buffer.append(isCorrect());
		buffer.append("}");
		return buffer.toString();
	}

    //
    // Überschreibungen von AbstractMap
    //
	/**
	 * Für den direkten Zugriff aus Velocity auf Field-Variablen. Der
     * Aufruf dieser Methode entspricht einem <code>getField(key.toString())</code>.
	 * 
     * @param key Feldschlüssel
	 * @return Feldwert zu dem angegebenen Feldschlüssel 
	 */
	public Object get(Object key) {
		try {
			return key != null ? getField(key.toString()) : null;
		} catch (BeanException e) {
			return null;
		}
	}

	/**
	 * Für den direkten Zugriff aus Velocity auf Field-Variablen. Der Aufruf dieser
     * Methode entspricht einem <code>setField(key.toString(), value)</code>.
     * 
     * @param key Feldschlüssel
     * @param value neuer Feldwert
     * @return alter Feldwert
	 */
	public Object put(Object key, Object value) {
        if (key == null)
            return null;
		Object o = get(key);
		try {
			setField(key.toString(), value);
		} catch (BeanException e) {
            logger.log(Level.WARNING, "Fehler beim Setzen des Inhalts des Felds " + key, e);
		}
		return o;
	}

	/**
     * Der Aufruf dieser Methode entspricht einem <code>return getFields()</code>.
     * 
     * @return eine Sammlung von {@link Map.Entry}-Instanzen
	 */
	public Set entrySet() {
		Set set = new HashSet();
		for (Iterator it = getFields().iterator(); it.hasNext(); ) {
			String key = (String)it.next();
			set.add(new SimpleEntry(key));
		}
		return set;
	}

    /**
     * Returns <tt>true</tt> if this map contains a mapping for the specified
     * key. <p>
     * 
     * @see java.util.AbstractMap#containsKey(java.lang.Object)
     */
    public boolean containsKey(Object key) {
        return getBeanAccessor(this).getField((String)key) != null;
    }

    //
    // innere Klassen
    //
    /**
     * Diese Klasse implementiert {@link Map.Entry} für die {@link MapBean}. 
     */
	class SimpleEntry implements Map.Entry {
        // Membervariablen
		final String key;

        /** Der Konstruktor trägt den Feldschlüssel ein. */
		SimpleEntry(String key) {
			this.key = key;
		}

        /** Liefert den Feldschlüssel */
		public Object getKey() {
			return this.key;
		}

        /** Liefert den Feldwert */
		public Object getValue() {
			return get(key);
		}

        /** Setzt einen neuen Feldwert */
		public Object setValue(Object value) {
            return put(key, value);
		}
	}

	// static
	static final private Map cache = new HashMap(); // ClassName => BeanAccessor

	static private void loadClass(Bean bean) {
		if (getBeanAccessor(bean) != null) return;
		cache.put(bean.getClass().getName(), new BeanAccessor(bean));
	}

	static private BeanAccessor getBeanAccessor(Bean bean) {
		return (BeanAccessor)cache.get(bean.getClass().getName());
	}

	static private class BeanAccessor {
		Map fields = new HashMap(); // String fieldname -> Field
		Map methods = new HashMap(); // String fieldname -> Method
		
		private BeanAccessor(Bean bean) {
			Field f[] = bean.getClass().getFields();
			for (int i = 0; i < f.length; i++) {
	            Field currentField = f[i];
	            int currentModifier = currentField.getModifiers();
	            if (!(Modifier.isStatic(currentModifier) || Modifier.isFinal(currentModifier))) {
	                fields.put(currentField.getName(), currentField);   
	            }
			}
			
			Method m[] = bean.getClass().getMethods();
			for (int i = 0; i < m.length; i++) {
				String name = m[i].getName();
				if (name.startsWith("set") && name.length() > 3) {
					name = name.substring(3, 4).toLowerCase() + name.substring(4);
					if (getField(name) != null) {
						methods.put(name, m[i]);
					}
				}
			}
		}
		
		private Set getFieldNames() {
			return fields.keySet();
		}
		
		private Field getField(String fieldname) {
			return (Field)fields.get(fieldname);
		}
		
		private Method getSetter(String fieldname) {
			return (Method)methods.get(fieldname);
		}
	}
}
