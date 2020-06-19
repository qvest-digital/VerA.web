package de.tarent.commons.utils;

/*-
 * Veranstaltungsmanagement VerA.web, comprised of…
 * VerA.web, platform-independent webservice-based event management
 * tarent-commons, a set of common components and solutions
 * tarent-contact, platform-independent webservice-based contact management
 * tarent-database, jdbc database library
 * tarent-doctor, Document Generation Platform
 * tarent-octopus, Webservice Data Integrator and Application Server
 * … is newly MIT-licenced and Copyright
 *  © 2018 Атанас Александров (sirakov@gmail.com)
 *  © 2014, 2015, 2016, 2017 Атанас Александров (a.alexandrov@tarent.de)
 *  © 2013 Иванка Александрова (i.alexandrova@tarent.de)
 *  © 2005, 2006, 2007 asteban (s.mancke@tarent.de)
 *  © 2013 Patrick Apel (p.apel@tarent.de)
 *  © 2016 Eugen Auschew (e.auschew@tarent.de)
 *  © 2013 Andrei Boulgakov (a.boulgakov@tarent.de)
 *  © 2013 Valentin But (v.but@tarent.de)
 *  © 2016 Lukas Degener (l.degener@tarent.de)
 *  © 2017 Axel Dirla (a.dirla@tarent.de)
 *  © 2015 Julian Drawe (j.drawe@tarent.de)
 *  © 2009 Sven Frommeyer (s.frommeyer@tarent.de)
 *  © 2014, 2018 Dominik George (d.george@tarent.de)
 *  © 2013 Martin Gernhardt (m.gernhardt@tarent.de)
 *  © 2013 Sascha Girrulat (s.girrulat@tarent.de)
 *  © 2007, 2008 David Goemans (d.goemans@tarent.de)
 *  © 2018 Christian Gorski (c.gorski@tarent.de)
 *  © 2015 Viktor Hamm (v.hamm@tarent.de)
 *  © 2013 Katja Hapke (k.hapke@tarent.de)
 *  © 2006, 2007, 2010, 2013 Hendrik Helwich (h.helwich@tarent.de)
 *  © 2018 Thomas Hensel (t.hensel@tarent.de)
 *  © 2018, 2019 Benedict Hoeger (b.hoeger@tarent.de)
 *  © 2018, 2019 Titian Horvath (t.horvath@tarent.de)
 *  © 2005, 2006, 2007, 2008 Christoph Jerolimov (jerolimov@gmx.de)
 *  © 2018, 2019 Timo Kanera (t.kanera@tarent.de)
 *  © 2006 Philipp Kirchner (p.kirchner@tarent.de)
 *  © 2008, 2009, 2010 Carsten Klein (c.klein@tarent.de)
 *  © 2006 Michael Kleinhenz (m.kleinhenz@tarent.de)
 *  © 2006 Michael Klink (m.klink@tarent.de)
 *  © 2007 Fabian Köster (f.koester@tarent.de)
 *  © 2006, 2014 Martin Ley (m.ley@tarent.de)
 *  © 2007 Alex Maier (a.maier@tarent.de)
 *  © 2014, 2015 Max Marche (m.marche@tarent.de)
 *  © 2007 Jan Meyer (jan@evolvis.org)
 *  © 2007, 2013, 2014, 2015, 2016, 2017, 2018, 2019, 2020
 *     mirabilos (t.glaser@tarent.de)
 *  © 2016 Cristian Molina (c.molina@tarent.de)
 *  © 2006, 2007 Jens Neumaier (j.neumaier@tarent.de)
 *  © 2006 Nils Neumaier (n.neumaier@tarent.de)
 *  © 2018 Yorka Neumann (y.neumann@tarent.de)
 *  © 2017 Michael Nienhaus (m.nienhaus@tarent.de)
 *  © 2013 Claudia Nuessle (c.nuessle@tarent.de)
 *  © 2014, 2015 Jon Nuñez Alvarez (j.nunez-alvarez@tarent.de)
 *  © 2016 Jens Oberender (j.oberender@tarent.de)
 *  © 2016, 2017 Miluška Pech (m.pech@tarent.de)
 *  © 2007, 2008, 2009 Martin Pelzer (m.pelzer@tarent.de)
 *  © 2008, 2009 Christian Preilowski (c.thiel@tarent.de)
 *  © 2013 Marc Radel (m.radel@tarent.de)
 *  © 2013 Sebastian Reimers (s.reimers@tarent.de)
 *  © 2015 Charbel Saliba (c.saliba@tarent.de)
 *  © 2006, 2008, 2009, 2010 Thomas Schmitz (t.schmitz@tarent.de)
 *  © 2013 Volker Schmitz (v.schmitz@tarent.de)
 *  © 2014 Sven Schumann (s.schumann@tarent.de)
 *  © 2007 Robert Schuster (r.schuster@tarent.de)
 *  © 2014 Sevilay Temiz (s.temiz@tarent.de)
 *  © 2013 Kevin Viola Schmitz (k.schmitz@tarent.de)
 *  © 2008, 2015 Stefan Walenda (s.walenda@tarent.de)
 *  © 2015, 2016, 2017 Max Weierstall (m.weierstall@tarent.de)
 *  © 2013 Rebecca Weinz (r.weinz@tarent.de)
 *  © 2015, 2016 Stefan Weiz (s.weiz@tarent.de)
 *  © 2015, 2016 Tim Zimmer (t.zimmer@tarent.de)
 * and older code, Copyright © 2001–2008 ⮡ tarent GmbH and contributors.
 * Licensor is tarent solutions GmbH, http://www.tarent.de/
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

import de.tarent.commons.utils.converter.*;

import java.util.*;

/**
 * This is a registry for converter of data items using the converter interface.
 * A default set of converter is provided out of the box. Other converter can
 * be added.
 */
public class ConverterRegistry {

    protected static ConverterRegistry defaultRegistry = null;

    /**
     * The default converter in a list
     */
    private LinkedList converterList = new LinkedList();

    /**
     * All converter by there name
     */
    private Map converterMap = new HashMap();

    protected ConverterRegistry() {
        register(new ObjectToString(), true);
        register(new StringToInteger(), true);
        register(new StringToBoolean(), true);
        register(new StringToLong(), true);
        register(new StringToFloat(), true);
        register(new StringToDouble(), true);
        register(new StringToDouble(), true);
        register(new StringToDate(), true);
        register(new CalendarToDate(), true);
        register(new LongToDate(), true);
        register(new DateToLong(), true);
        register(new DateToString(), true);
        register(new BooleanToString(), true);
        register(new BooleanToInteger(), true);
        register(new NumberToBoolean(), true);
        register(new NumberToInteger(), true);
        register(new NumberToLong(), true);
        register(new StringToDimension(), true);
        register(new StringToCharacter(), true);
        register(new ClobToString(), true);
        register(new XMLEncoderConverter(Map.class), true); //MapToString
        register(new XMLEncoderConverter(List.class), true); //MapToString
        register(new XMLDecoderConverter(Map.class), true); //StringToMap
        register(new XMLDecoderConverter(List.class), true); //StringToList
    }

    public static synchronized ConverterRegistry getDefaultRegistry() {
        if (defaultRegistry == null) {
            defaultRegistry = new ConverterRegistry();
        }
        return defaultRegistry;
    }

    /**
     * Quick method for conversion over the registered converters of the default registry.
     * If the sourceData is Null and the target is a primitive, a default value is returned.
     * In the case, where the sourceData is directly assigneable to the targetType, no conversion will be done.
     *
     * @throws IllegalArgumentException if no converter was found, or the conversion fails.
     */
    public static Object convert(Object sourceData, Class targetType) throws IllegalArgumentException {
        if (sourceData == null) {
            if (targetType.isPrimitive()) {
                return getNullDefault(targetType);
            }
            return null;
        }
        if (targetType.isPrimitive()) {
            targetType = getComplexForPrimitive(targetType);
        }

        if (targetType.isAssignableFrom(sourceData.getClass())) {
            return sourceData;
        }
        Converter converter = ConverterRegistry.getDefaultRegistry().getConverter(sourceData.getClass(), targetType);
        if (converter == null) {
            throw new IllegalArgumentException("No suitable converter for " + sourceData.getClass() + " => " + targetType);
        }
        return converter.convert(sourceData);
    }

    /**
     * Quick method for conversion over the registered converters of the default registry.
     */
    public static Object convert(Object sourceData, String converterName) throws IllegalArgumentException {
        if (sourceData == null) {
            return null;
        }
        Converter converter = ConverterRegistry.getDefaultRegistry().getConverter(converterName);
        if (converter == null) {
            throw new IllegalArgumentException("No suitable converter for name " + converterName);
        }
        return converter.convert(sourceData);
    }

    /**
     * Add a new converter to the Registry. Later registered converters are preferred by the lookup via type.
     * If the flag useByDefault is true, the converter is returned for a query by datatype,
     * otherwise it is only registered by name.
     *
     * @param useByDefault use the supplied converter by default
     */
    public void register(Converter converter, boolean useByDefault) {
        if (useByDefault) {
            converterList.addFirst(converter);
        }
        converterMap.put(converter.getConverterName(), converter);
    }

    /**
     * Returns a converter for convertion from sourceType to targetType.
     * The first converter matching the data types is returned.
     * If you need a special converter, use the getConverter(String converterName).
     */
    public Converter getConverter(Class sourceType, Class targetType) {
        if (targetType.isPrimitive()) {
            targetType = getComplexForPrimitive(targetType);
        }

        for (Iterator iter = converterList.iterator(); iter.hasNext(); ) {
            Converter converter = (Converter) iter.next();
            if (targetType.isAssignableFrom(converter.getTargetType())
              && converter.getSourceType().isAssignableFrom(sourceType)) {
                return converter;
            }
        }
        return null;
    }

    public static Class getComplexForPrimitive(Class targetType) {
        if (targetType == Boolean.TYPE) {
            return Boolean.class;
        } else if (targetType == Character.TYPE) {
            return Character.class;
        } else if (targetType == Byte.TYPE) {
            return Byte.class;
        } else if (targetType == Short.TYPE) {
            return Short.class;
        } else if (targetType == Integer.TYPE) {
            return Integer.class;
        } else if (targetType == Long.TYPE) {
            return Long.class;
        } else if (targetType == Float.TYPE) {
            return Float.class;
        } else if (targetType == Double.TYPE) {
            return Double.class;
        } else {
            return targetType;
        }
    }

    /**
     * Returns a default Value for primitives which best match a null value.
     */
    public static Object getNullDefault(Class targetType) {

        if (targetType == Boolean.TYPE) {
            return Boolean.FALSE;
        } else if (targetType == Character.TYPE) {
            return new Character((char) 0);
        } else if (targetType == Byte.TYPE) {
            return new Byte((byte) 0);
        } else if (targetType == Short.TYPE) {
            return new Short((short) 0);
        } else if (targetType == Integer.TYPE) {
            return new Integer(0);
        } else if (targetType == Long.TYPE) {
            return new Long(0);
        } else if (targetType == Float.TYPE) {
            return new Float(0);
        } else if (targetType == Double.TYPE) {
            return new Double(0);
        }

        return null;
    }

    /**
     * Returns a special named converter.
     */
    public Converter getConverter(String converterName) {
        return (Converter) converterMap.get(converterName);
    }
}
