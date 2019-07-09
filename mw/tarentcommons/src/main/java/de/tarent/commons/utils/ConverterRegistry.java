package de.tarent.commons.utils;
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
