/*
 * VerA.web,
 * Veranstaltungsmanagment VerA.web
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
 * interest in the program 'VerA.web'
 * Signature of Elmar Geese, 7 August 2007
 * Elmar Geese, CEO tarent GmbH.
 */

/*
 * $Id: BeanFactory.java,v 1.1 2007/06/20 11:56:51 christoph Exp $
 * Created on 08.02.2005
 */
package de.tarent.octopus.custom.beans;

import java.sql.Timestamp;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * Abstrakte Klasse zum Erstellen von Beans,
 * übernimmt das Initalisieren, Befüllen und Verifizieren.
 * 
 * @author Michael Klink, Alex Steeg, Christoph Jerolimov
 * @version 1.3
 */
public abstract class BeanFactory {
    //
    // Konstruktor
    //
    /**
     * Dieser Konstruktor setzt das zu benutzende Bean-Package.
     */
    protected BeanFactory(String beanPackage) {
        assert beanPackage != null;
        BEANPACKAGE = beanPackage;
    }
    
    //
    // öffentliche Methoden
    //
    /**
     * Erstellt eine neue Bean-Instanz.
     * 
     * @param beanname
     * @return Bean, nie null.
     * @throws BeanException
     */
    public Bean createBean(String beanname) throws BeanException {
        try {
            Class clazz = Class.forName(BEANPACKAGE + "." + beanname);
            return (Bean)clazz.newInstance();
        } catch (Exception e) {
            throw new BeanException(MessageFormat.format("Fehler beim erstellen der Bean ''{0}.{1}''.", new Object[] { BEANPACKAGE, beanname }), e);
        }
    }

    //
    // öffentliche Hilfsmethoden
    //
    /**
     * Gibt das übergebene Object value als Instanz der Klasse target oder einer
     * Unterklasse davon zurück. Sollte eine Überführung nicht möglich sein, wird
     * <code>null</code> zurückgegeben.
     * 
     * @param value
     * @param target
     * @return Instanz von target oder null
     * @throws BeanException wenn Transformatierung nicht möglich ist.
     */
    static public Object transform(Object value, Class target) throws BeanException {
        if (value != null && target.isAssignableFrom(value.getClass())) {
            return value;
        } else if (target.isAssignableFrom(String.class)) {
            if (value == null)
                return null;
            else
                return value.toString();
        } else if (target.isAssignableFrom(Boolean.class) || target.isAssignableFrom(Boolean.TYPE)) {
            if (value == null)
                return Boolean.FALSE;
            else if (value instanceof Integer)
                return Boolean.valueOf(((Integer)value).intValue() != 0);
            else
                return Boolean.valueOf(value.toString());
        } else if (value == null) {
            if (target.isAssignableFrom(List.class)) {
                return Collections.EMPTY_LIST;
            } else {
                return null;
            }
        } else if (target.isAssignableFrom(Integer.class) || target.isAssignableFrom(Integer.TYPE)) {
            if (value.toString().length() == 0)
                return null;
            try {
                return new Integer(value.toString());
            } catch (NumberFormatException e) {
                throw new BeanException(MessageFormat.format("''{0}'' ist keine gültige Zahl.", new Object[] { value }));
            }
        } else if (target.isAssignableFrom(Double.class) || target.isAssignableFrom(Double.TYPE)) {
            String s = value.toString();
            if (s.length() == 0)
                return null;
            try {
                return new Double(s.indexOf(',') == -1 ? s : s.replace(',', '.'));
            } catch (NumberFormatException e) {
                throw new BeanException(MessageFormat.format("''{0}'' ist keine gültige Zahl.", new Object[] { value }));
            }
        } else if (target.isAssignableFrom(Long.class) || target.isAssignableFrom(Long.TYPE)) {
            if (value.toString().length() == 0)
                return null;
            try {
                return new Long(value.toString());
            } catch (NumberFormatException e) {
                throw new BeanException(MessageFormat.format("''{0}'' ist keine gültige Zahl.", new Object[] { value }));
            }
        } else if (target.isAssignableFrom(Float.class) || target.isAssignableFrom(Float.TYPE)) {
            String s = value.toString();
            if (s.length() == 0)
                return null;
            try {
                return new Float(s.indexOf(',') == -1 ? s : s.replace(',', '.'));
            } catch (NumberFormatException e) {
                throw new BeanException(MessageFormat.format("''{0}'' ist keine gültige Zahl.", new Object[] { value }));
            }
        } else if (target.isAssignableFrom(List.class) && value instanceof Object[]) {
            return Arrays.asList((Object[])value);
        } else if (target.isAssignableFrom(List.class)) {
            return Collections.singletonList(value);
        } else if (target.isAssignableFrom(Date.class)) {
            if (value.toString().length() == 0)
                return null;
            return getDate(value.toString());
        } else if (target.isAssignableFrom(Timestamp.class)) {
            Date date = (Date)transform(value, Date.class);
            return date == null ? null : new Timestamp(date.getTime());
        } else {
            logger.warn("can not transform " + value.getClass().getName() + " ('" + value + "') to " + target.getName());
            return null;
        }
    }

    //
    // abstrakte Basismethoden
    //
	/**
	 * Liefert ein Objekt, das in ein bestimmtes Feld der "aktuellen" Bean
     * gesetzt werden soll.
	 * 
	 * @param key
	 * @return gewünschter Feldinhalt oder null
	 * @throws BeanException bei Datenzugriffsfehlern.
	 */
	abstract protected Object getField(String key) throws BeanException;
	
	/**
	 * Wird verwendet, um bei Bean-Listen zur nächsten Bohne zu springen,
	 * wird vor dem Einlesen einer Bean aufgerufen.
	 * 
	 * @return <code>true</code>, wenn weitere Beans vorhanden sind, ansonsten
     *  <code>false</code>.
	 * @throws BeanException bei Datenzugriffsfehlern.
	 */
	abstract protected boolean hasNext() throws BeanException;

    /**
     * Diese Methode setzt basierend auf dem Factory-Wissen das Bean-Feld
     * {@link Bean#isModified() Modified}. Sollten keine Informationen
     * verfügbar sein, bleibt das Feld unangetastet, es sollte also vor dem
     * Aufruf dieser Methode sinnvoll vorbelegt sein.
     * 
     * @param bean Bohne, deren {@link Bean#isModified() Modified}-Feld aktualisiert werden soll.
     * @throws BeanException
     */
    abstract protected void checkModified(Bean bean) throws BeanException;

    //
    // geschützte Methoden
    //
	/**
	 * Gibt eine verifizierte gefüllte Bean zurück.
	 * 
	 * @param bean zu füllende Bean-Instanz
	 * @return gefüllte Bean-Instanz.
	 * @throws BeanException 
	 */
	protected Bean fillBean(Bean bean) throws BeanException {
		for (Iterator it = bean.getFields().iterator(); it.hasNext(); ) {
			String field = (String)it.next();
			bean.setField(field, getField(field));
		}
		checkModified(bean);
		bean.verify();
		return bean;
	}

	/**
	 * Holt eine Liste von Beans.
	 * 
	 * @param beanname Bean-Klassenname
	 * @return Liste mit Beans, leere Liste möglich, nie null.
	 * @throws BeanException
	 */
	protected List fillBeanList(String beanname) throws BeanException {
		List list = new ArrayList();
		while (hasNext()) {
			Bean bean = createBean(beanname);
			fillBean(bean);
			list.add(bean);
		}
		return list;
	}

    //
    // private statische Hilfsmethoden
    //
	/**
	 * Extrahiert aus einem Datum, welches der Benutzer eingibt, eine neue
	 * {@link Date}-Instanz. Hierbei wird sowohl das Deutsche Format
	 * <code>dd.MM.yyyy</code> als auch das Englische Format
	 * <code>MM-dd-yyyy</code> unterstüzt. Zum Parsen wird der
	 * {@link SimpleDateFormat SimpleDateFormatter} verwendet, zusätzlich
	 * wird überprüft ob das Berechnete Ergebnis wirklich mit der
	 * Benutzereingabe übereinstimmt. (Grund: Der SimpleDateFormat rechnet
	 * "Überlauftage" automatisch um, aus 32.01.2000 wird z.B. automatisch
	 * der 01.02.2000. Dies war bei VerA.web explizit nicht gewollt.)
	 * 
	 * Unterstützt zwei und vier Stellige Jahreszahlen, siehe hierzu auch
	 * {@link #equalDate(String, String, String, Calendar)}.
	 * 
	 * @param input Ungeparste Benutzereingabe.
	 * @return Date-Instanz, nie null.
	 * @throws BeanException Wenn das Datum nicht geparst werden konnte.
	 */
	static private Date getDate(String input) throws BeanException {
		try {
			if (input.indexOf(".") != -1) {
				String tokens[] = input.split("\\.");
				if (tokens.length == 3 && (tokens[2].length() == 2 || tokens[2].length() == 4)) {
					Date date = DATE_FORMAT_DE.parse(input);
					if (equalDate(tokens[0], tokens[1], tokens[2], date)) {
						return date;
					}
				}
			} else if (input.indexOf("-") != -1) {
				String tokens[] = input.split("\\-");
				if (tokens.length == 3 && (tokens[2].length() == 2 || tokens[2].length() == 4)) {
					Date date = DATE_FORMAT_EN.parse(input);
					if (equalDate(tokens[1], tokens[0], tokens[2], date)) {
						return date;
					}
				}
			}
		} catch (Exception e) {
		}
		
		throw new BeanException(MessageFormat.format("''{0}'' ist kein gültiges Datum, bitte verwenden Sie das Format TT.MM.JJJJ.", new Object[] { input }));
	}

	/**
	 * Überprüft ob die als Strings übergebene Benutzereingaben (Tag, Monat
	 * und Jahr) mit dem ebenfalls übergebenem Datum übereinstimmen.
	 * Beachtet dabei insbesondere auch zweistellige Jahreszahlen, bei dennen
	 * automatisch eine Überführung in das aktuelle Jahrtausend geschieht,
	 * wenn das Jahr innerhalb der nächsten 20 Jahre liegt, andernfalls in das
	 * Jahrhundert davor. Dies entspricht dem Verhalten des
	 * {@link SimpleDateFormat SimpleDateFormatters}.
	 * 
	 * @param day Ungeparste Tagesangabe.
	 * @param month Ungeparste Monatsangabe.
	 * @param year Ungeparste Jahresangabe.
	 * @param date Zu vergleichendes Datum.
	 * @return true wenn die Datumsangaben übereinstimmen, ansonsten false.
	 * @throws NumberFormatException Wenn Tag, Monat oder Jahr keine Zahl ist.
	 */
	static private boolean equalDate(String day, String month, String year, Date date) {
		int d = Integer.parseInt(day);
		int m = Integer.parseInt(month);
		int y = Integer.parseInt(year);
		
		if (y <= 99) {
			Calendar currentY = Calendar.getInstance();
			currentY.setTime(new Date());
			int cy = currentY.get(Calendar.YEAR);
			int yh = cy / 100 * 100;
			y += (y + yh <= cy + 20) ? yh : yh - 100;
		}
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return (d == calendar.get(Calendar.DAY_OF_MONTH) &&
				m == calendar.get(Calendar.MONTH) + 1 &&
				y == calendar.get(Calendar.YEAR));
	}

    //
    // geschützte Member
    //
    /** Package, in dem die Beans liegen */
    protected final String BEANPACKAGE;

    /** Deutsches Datumsformat */
    private static final SimpleDateFormat DATE_FORMAT_DE = new SimpleDateFormat("d.M.y");

    /** Englisches Datumsformat */
    private static final SimpleDateFormat DATE_FORMAT_EN = new SimpleDateFormat("M-d-y");

    /** Log4J-Logger dieser Klasse */
	private static final Logger logger = Logger.getLogger(BeanFactory.class);
}
