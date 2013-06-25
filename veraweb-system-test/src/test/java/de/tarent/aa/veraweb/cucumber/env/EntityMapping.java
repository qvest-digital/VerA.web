package de.tarent.aa.veraweb.cucumber.env;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

import cucumber.table.DataTable;
import de.tarent.aa.veraweb.db.entity.AbstractEntity;
import de.tarent.aa.veraweb.db.entity.Person;

/**
 * Class for mapping between {@link DataTable} columns used in cucumber scenarios and real properties of corresponding
 * Entity.
 * 
 * @author Valentin But (v.but@tarent.de), tarent solutions GmbH
 */
public class EntityMapping {

    public static String SEPARATOR = "\\.";

    /**
     * Cucumber mapping for entity {@link Person}.
     */
    public static Map<String, String> person;
    static {
        person = new HashMap<String, String>();
        person.put("vorname", "firstName");
        person.put("nachname", "lastName");
    }

    /**
     * Cucumber mapping for entity {@link Person}.
     */
    public static Map<String, String> task;
    static {
        task = new HashMap<String, String>();
        task.put("checkboxohnebezeichnung", "toDeleteSelected");
        task.put("id", "id");
        task.put("titel", "title");
        task.put("beschreibung", "description");
        task.put("start", "startDate");
        task.put("ende", "endDate");
        task.put("fertigstellungsgrad", "degreeOfCompletion");
        task.put("verantwortlicher", "responsiblePerson.firstName");
        task.put("priorität", "priority");
    }

    /**
     * Create entities based on given <code>dataTable</code> and entity mapping information.
     * 
     * @param dataTable
     *            {@link DataTable}
     * @param clazz
     *            root class
     * @return list of entities
     * @throws Exception
     *             exception
     */
    public static <T extends AbstractEntity> List<T> createEntities(DataTable dataTable, Class<T> clazz)
            throws Exception {
        List<T> entities = getEntitiesFromDataTable(dataTable, clazz);

        return entities;
    }

    /**
     * Get entities based on given <code>dataTable</code> and entity mapping information.
     * 
     * @param dataTable
     *            {@link DataTable}
     * @param entityName
     *            name of target entity
     * @return list of entities
     * @throws Exception
     *             exception
     */
    @SuppressWarnings("unchecked")
    public static <T extends AbstractEntity> List<T> getEntitiesFromDataTable(DataTable dataTable, Class<T> clazz)
            throws Exception {
        List<Map<String, String>> dtMaps = dataTable.asMaps();
        List<T> entityList = new ArrayList<T>();

        // iterate over all entries in data table
        for (Map<String, String> dtMap : dtMaps) {
            T entityObject = clazz.newInstance();

            for (Entry<String, String> entry : dtMap.entrySet()) {
                String column = entry.getKey();
                String value = entry.getValue();

                // get field value via reflection
                Map<String, String> entityMapping = (Map<String, String>) FieldUtils.readStaticField(
                        EntityMapping.class, clazz.getSimpleName().toLowerCase());

                // get mapping property
                String propertyChain = entityMapping.get(column.toLowerCase());

                if (propertyChain == null) {
                    throw new Exception("Unknown field mapping for '" + column + "'!");
                }

                // fill entity
                fillEntity(entityObject, propertyChain, value);
            }

            entityList.add(entityObject);
        }

        return entityList;
    }

    /**
     * Helper method to fill entity with given property value.
     * 
     * @param entityObject
     *            entity to be filled with new property
     * @param propertyChain
     *            property chain
     * @param newPropertyValue
     *            new value to fill in
     * @throws Exception
     *             exception
     */
    public static void fillEntity(Object entityObject, String propertyChain, String newPropertyValue) throws Exception {
        String[] splittedPropertyChain = propertyChain.split(EntityMapping.SEPARATOR);

        // e.g. 'name'
        if (splittedPropertyChain.length == 1) {
            setPropertyValue(entityObject, propertyChain, newPropertyValue);
        }

        // e.g. 'licenceType.name' or 'contractItems.contractPosition'
        else if (splittedPropertyChain.length == 2) {

            String propertyName = splittedPropertyChain[0];
            String childPropertyName = splittedPropertyChain[1];

            Field field = FieldUtils.getField(entityObject.getClass(), propertyName, true);
            Class<?> fieldType = field.getType();

            Object property = PropertyUtils.getProperty(entityObject, propertyName);

            if (property == null) {
                property = fieldType.newInstance();
            }

            // property is a Set
            if (Set.class.equals(fieldType)) {
                ParameterizedType paramType = (ParameterizedType) field.getGenericType();
                Class<?> paramClazz = (Class<?>) paramType.getActualTypeArguments()[0];

                Boolean setIsEmpty = (Boolean) Set.class.getMethod("isEmpty").invoke(property);
                if (setIsEmpty) {
                    Object setElement = paramClazz.newInstance();
                    setPropertyValue(setElement, childPropertyName, newPropertyValue);
                    Set.class.getMethod("add", Object.class).invoke(property, setElement);
                } else {
                    Iterator<?> iter = (Iterator<?>) Set.class.getMethod("iterator").invoke(property);
                    Object setElement = iter.next();
                    setPropertyValue(setElement, childPropertyName, newPropertyValue);
                }
            }
            // property is a wrapper for single object definition
            else {
                setPropertyValue(property, childPropertyName, newPropertyValue);
                PropertyUtils.setProperty(entityObject, propertyName, property);
            }
        }

        // e.g 'contractItems.contractType.type'
        else if (splittedPropertyChain.length > 2) {

            String propertyName = splittedPropertyChain[0];
            String subPropertyChain = propertyChain.substring(propertyName.length() + 1, propertyChain.length());

            Field field = FieldUtils.getField(entityObject.getClass(), propertyName, true);
            Class<?> fieldType = field.getType();

            Object property = PropertyUtils.getProperty(entityObject, propertyName);
            if (property == null) {
                property = fieldType.newInstance();
            }

            Object subEntityObject;
            if (Set.class.equals(fieldType)) {
                Iterator<?> iter = (Iterator<?>) Set.class.getMethod("iterator").invoke(property);
                subEntityObject = iter.next();
                fillEntity(subEntityObject, subPropertyChain, newPropertyValue);
            } else {
                fillEntity(property, subPropertyChain, newPropertyValue);
                PropertyUtils.setProperty(entityObject, propertyName, property);
            }
        }
    }

    /**
     * Utility method for accessing any field within an instance of an object and updating field value, ignoring access
     * modifieres.
     * 
     * @param o
     *            Object containing request field
     * @param fieldName
     *            name of the field
     * @param stringValue
     *            new value to set for the requested field
     * @throws ParseException
     * @throws NoSuchFieldException
     *             thrown if the requested field is not a field of the given object
     * @throws IllegalAccessException
     *             thrown if it is not possible to access the request field
     * @throws InvocationTargetException
     */
    public static void setPropertyValue(final Object o, final String fieldName, final String stringValue)
            throws ParseException, IllegalAccessException, InvocationTargetException {
        Field field = FieldUtils.getField(o.getClass(), fieldName, true);
        if (field == null) {
            throw new NullPointerException("Field '" + fieldName + "' not found!");
        }
        Class<?> fieldType = field.getType();

        Object value;

        if (!stringValue.isEmpty() && !stringValue.contains("null")) {
            value = stringValue;
            if (Boolean.class.equals(fieldType)) {
                value = parseBoolean(stringValue);
            } else if (Date.class.equals(fieldType)) {
                value = parseDate(stringValue);
            } else if (Timestamp.class.equals(fieldType)) {
                value = parseTimestamp(stringValue);
            } else if (Integer.class.equals(fieldType)) {
                value = Integer.valueOf(stringValue.replace(".", ""));
            } else if (Long.class.equals(fieldType)) {
                value = Long.valueOf(stringValue);
            } else if (Double.class.equals(fieldType)) {
                value = Double.valueOf(stringValue);
            } else if (Float.class.equals(fieldType)) {
                value = Float.valueOf(stringValue);
            }
        } else {
            value = null;
        }
        BeanUtils.setProperty(o, fieldName, value);
    }

    /**
     * Helper method to parse a date string of format 'dd.MM.yyyy'.
     * 
     * @param dateString
     *            date string
     * @return {@link Date}
     * @throws ParseException
     *             exception
     */
    public static Date parseDate(String dateString) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        return sdf.parse(dateString);
    }
    
    /**
     * Helper method to parse a timestamp string of format 'dd.MM.yyyy'.
     * 
     * @param dateString
     *            date string
     * @return {@link Date}
     * @throws ParseException
     *             exception
     */
    public static Timestamp parseTimestamp(String dateString) throws ParseException {
        Pattern regex = Pattern.compile("^heute\\(([-+]{0,1})(\\d)\\)(\\s\\d{1,2}(?::\\d\\d)?){0,1}(?:\\sUhr)?$");
        Matcher matcher = regex.matcher(dateString);
        
        String prefix = null;
        String days = null;
        String hours = null;
        String minutes = null;
        if (matcher.matches()) {
            prefix = matcher.group(1);
            days = matcher.group(2);
            if ("-".equals(prefix)) {
                days = prefix + days;
            }
            if (matcher.groupCount() > 3) {
                hours = matcher.group(3);
                minutes = matcher.group(4);
            }
        }
        
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, Integer.parseInt(days));
        cal.set(Calendar.MILLISECOND, 0);
        
        if (hours == null || minutes == null) {
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 30);
        } else {
            cal.set(Calendar.HOUR_OF_DAY, Integer.valueOf(hours).intValue());
            cal.set(Calendar.MINUTE, Integer.valueOf(minutes).intValue());
            cal.set(Calendar.SECOND, 0);
        }
        
        return new Timestamp(cal.getTimeInMillis());
    }

    /**
     * Helper method to convert boolean string.
     * 
     * @param booleanString
     *            boolean string
     * @return boolean value
     */
    public static Boolean parseBoolean(String booleanString) {
        String s = booleanString.toLowerCase();
        if ("ja".equals(s) || "1".equals(s) || "wahr".equals(s) || "true".equals(s)) {
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
    }
}
