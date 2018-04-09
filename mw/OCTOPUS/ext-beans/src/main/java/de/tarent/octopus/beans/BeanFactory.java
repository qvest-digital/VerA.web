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
 * $Id: BeanFactory.java,v 1.7 2007/06/11 13:24:36 christoph Exp $
 * Created on 08.02.2005
 */
package de.tarent.octopus.beans;

import java.sql.Timestamp;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Abstract base factory class for {@link Bean} beans implementing
 * basic bean creation, initialization, filling, and verification
 * functions.
 *
 * @author Michael Klink, Alex Steeg, Christoph Jerolimov
 * @version 1.3
 */
public abstract class BeanFactory {
    //
    // construktor
    //
    /**
     * This constructor sets the Java package of the {@link Bean} implementations.
     */
    protected BeanFactory(String beanPackage) {
        assert beanPackage != null;
        BEANPACKAGE = beanPackage;
    }

    //
    // public methods
    //
    /**
     * Instantiates a new {@link Bean}.
     *
     * @param beanname name of the bean; the implementing class is expected
     *  in the Java package given in the {@link #BeanFactory(String) constructor}
     *  and must have a parameterless constructor.
     * @return Bean instance, never <code>null</code>.
     * @throws BeanException
     */
    public Bean createBean(String beanname) throws BeanException {
        try {
            Class clazz = Class.forName(BEANPACKAGE + "." + beanname);
            return (Bean)clazz.newInstance();
        } catch (Exception e) {
            throw new BeanException(MessageFormat.format("Failed to instantiate ''{0}.{1}''.", new Object[] { BEANPACKAGE, beanname }), e);
        }
    }

    //
    // public helper methods
    //
    /**
     * This method tries to convert the given value to an instance of the given
     * class. If the value already is an instance of the class or a sub class of
     * it, the value is returned unchanged.<br>
     * If the method has no knowledge about how to convert to the given class it
     * logs a warning and returns <code>null</code>. Depending on the target class
     * empty string representations may be returned as <code>null</code>, too,
     * while in some cases a <code>null</code> value may be returned as an "empty"
     * instance of the target class (e.g. for the target class {@link List} this
     * method returns {@link Collections#EMPTY_LIST}).<br>
     * If there are exceptions during actual conversion processes (e.g. when trying
     * to convert non-numerical data to a numerical type) an exception is thrown.<br>
     * Otherwise an instance of the target class or one of its sub classes is
     * returned.
     *
     * @param value the value to transform
     * @param target the target class the value is to be transformed into.
     * @return the transformed value
     * @throws BeanException when actual conversion prcesses fail.
     */
    static public Object transform(Object value, Class target) throws BeanException {
        if (value == null) {
            if (target.isAssignableFrom(List.class))
                return Collections.EMPTY_LIST;
            else if (target.isAssignableFrom(Set.class))
                return Collections.EMPTY_SET;
            else if (target.isAssignableFrom(Map.class))
                return Collections.EMPTY_MAP;
            else if (target.isAssignableFrom(Boolean.class) || target.isAssignableFrom(Boolean.TYPE))
                return Boolean.FALSE;
            else
                return null;
        }
        if (target.isAssignableFrom(value.getClass()))
            return value;
        if (target.isAssignableFrom(String.class))
            return value.toString();
        if (target.isAssignableFrom(Boolean.class) || target.isAssignableFrom(Boolean.TYPE)) {
            if (value instanceof Integer)
                return Boolean.valueOf(((Integer)value).intValue() != 0);
            else
                return Boolean.valueOf(value.toString());
        }
        if (target.isAssignableFrom(List.class)) {
            if (value instanceof Object[])
                return Arrays.asList((Object[])value);
            return Collections.singletonList(value);
        }
        if (target.isAssignableFrom(Set.class)) {
            if (value instanceof Object[])
                return new HashSet(Arrays.asList((Object[])value));
            return Collections.singleton(value);
        }
        String valueAsString = value.toString();
        if (target.isAssignableFrom(Integer.class) || target.isAssignableFrom(Integer.TYPE)) {
            if (valueAsString.length() == 0)
                return null;
            try {
                return new Integer(valueAsString);
            } catch (NumberFormatException e) {
                throw new BeanException(MessageFormat.format("''{0}'' ist keine gültige Zahl.", new Object[] { value }), e);
            }
        }
        if (target.isAssignableFrom(Double.class) || target.isAssignableFrom(Double.TYPE)) {
            if (valueAsString.length() == 0)
                return null;
            try {
                return new Double(valueAsString.indexOf(',') == -1 ? valueAsString : valueAsString.replace(',', '.'));
            } catch (NumberFormatException e) {
                throw new BeanException(MessageFormat.format("''{0}'' ist keine gültige Zahl.", new Object[] { value }), e);
            }
        }
        if (target.isAssignableFrom(Long.class) || target.isAssignableFrom(Long.TYPE)) {
            if (valueAsString.length() == 0)
                return null;
            try {
                return new Long(valueAsString);
            } catch (NumberFormatException e) {
                throw new BeanException(MessageFormat.format("''{0}'' ist keine gültige Zahl.", new Object[] { value }), e);
            }
        }
        if (target.isAssignableFrom(Float.class) || target.isAssignableFrom(Float.TYPE)) {
            if (valueAsString.length() == 0)
                return null;
            try {
                return new Float(valueAsString.indexOf(',') == -1 ? valueAsString : valueAsString.replace(',', '.'));
            } catch (NumberFormatException e) {
                throw new BeanException(MessageFormat.format("''{0}'' ist keine gültige Zahl.", new Object[] { value }), e);
            }
        }
        if (target.isAssignableFrom(Character.class) || target.isAssignableFrom(Character.TYPE)) {
            if (valueAsString.length() == 0)
                return null;
            return new Character(valueAsString.charAt(0));
        }
        if (target.isAssignableFrom(Date.class)) {
        	if (value instanceof Calendar)
        		return ((Calendar)value).getTime();
            if (valueAsString.length() == 0)
                return null;
            return getDate(valueAsString);
        }
        if (target.isAssignableFrom(Timestamp.class)) {
            Date date = (Date)transform(value, Date.class);
            return date == null ? null : new Timestamp(date.getTime());
        }
        logger.warning("can not transform " + value.getClass().getName() + " ('" + value + "') to " + target.getName());
        return null;
    }

    //
    // abstract base methods
    //
	/**
     * This methods shall return an object that is to be used as content
     * of a field of the currently filled bean instance.
	 *
	 * @param key key of the field for which a value is requested
	 * @return requested value of the field
	 * @throws BeanException when an attempt to receive the field value
     *  from the data source fails.
     * @see #fillBean(Bean)
     * @see #fillBeanList(String)
	 */
	abstract protected Object getField(String key) throws BeanException;

	/**
     * This method is used to proceed to the next (maybe the only) bean when
     * filling a single bean or a bean list.
	 *
	 * @return <code>true</code> if there is data to fill another bean,
     *  <code>false</code> otherwise.
	 * @throws BeanException when an attempt to receive data from the
     *  data source fails.
	 */
	abstract protected boolean hasNext() throws BeanException;

    /**
     * This method sets the {@link Bean} field {@link Bean#isModified() Modified}
     * according to special factory knowledge.<br>
     * If the factory can not decide whether or not the {@link Bean} data was
     * modified it leaves the bean untouched; therefore this attribute shall
     * be initialized sensibly.
     *
     * @param bean the bean to check for modifications.
     */
    abstract protected void checkModified(Bean bean) throws BeanException;

    //
    // protected methods
    //
	/**
	 * This method fills a bean using the {@link #getField(String)} method.
     * It requires a call to {@link #hasNext()} beforehand to ensure the
     * cursor on the data source is positioned on the data set to use to
     * fill the bean.<br>
     * After filling the attributes the bean its {@link Bean#verify()}
     * method is called.
	 *
	 * @param bean {@link Bean} instance to fill
	 * @return filled {@link Bean} instance; currently this is the same instance
     *  as the one given as parameter.
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
	 * This method fills a list with {@link Bean Beans} using the method
     * {@link #hasNext()} and {@link #fillBean(Bean)} repeatedly until no
     * more data is available.
	 *
	 * @param beanname name of the {@link Bean} class to use
	 * @return {@link List} of beans, never <code>null</code>.
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
    // private static helper methods
    //
    /**
     * This method tries to convert a {@link String} to a date value using the
     * {@link SimpleDateFormat SimpleDateFormats} {@link #DATE_FORMAT_DE} and
     * {@link #DATE_FORMAT_EN}. Afterwards it applies some sanity checks to the
     * result.
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
            throw new BeanException(MessageFormat.format("''{0}'' is not a valid date, please use the format DD.MM.YYYY.", new Object[] { input }), e);
		}

		throw new BeanException(MessageFormat.format("''{0}'' is not a valid date, please use the format DD.MM.YYYY.", new Object[] { input }));
	}

	/**
	 * This method verify the correctness of the date with the given user input.
	 *
	 * @param day Unparsed day.
	 * @param month Unparsed month.
	 * @param year Unparsed year.
	 * @param date Parsed date.
	 * @return Return true if the userinput (day, month and year) is equal the date.
	 * @throws NumberFormatException If some of the user input is unparseable.
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
    // private member
    //
    /** Java package of the {@link Bean} classes */
    protected final String BEANPACKAGE;

    /** German date format */
    private static final SimpleDateFormat DATE_FORMAT_DE = new SimpleDateFormat("d.M.y");

    /** English date format */
    private static final SimpleDateFormat DATE_FORMAT_EN = new SimpleDateFormat("M-d-y");

    /** logger of this class. */
	private static final Logger logger = Logger.getLogger(BeanFactory.class.getName());
}
