package de.tarent.aa.veraweb.db.entity;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.hibernate.LazyInitializationException;

/**
 * 
 * @author Valentin But (v.but@tarent.de), tarent solutions GmbH
 * 
 */
@MappedSuperclass
public abstract class AbstractEntity implements Serializable {

    /**
     * Generated serial id.
     */
    private static final long serialVersionUID = 2385997781356544101L;

    @Transient
    private Boolean toDeleteSelected = Boolean.FALSE;

    /**
     * @return the toDelete
     */
    public Boolean getToDeleteSelected() {
        return toDeleteSelected;
    }

    /**
     * @param toDelete the toDelete to set
     */
    public void setToDeleteSelected(Boolean toDeleteSelected) {
        this.toDeleteSelected = toDeleteSelected;
    }

    /**
     * Needed by hashCode() and equals().
     */
    public abstract Long getId();

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        if (getId() == null) {
            return super.hashCode();
        } else {
            return getId().hashCode();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(getClass().equals(obj.getClass()))) {
            return false;
        }
        if (getId() == null) {
            return super.equals(obj);
        } else {
            return getId().equals(((AbstractEntity) obj).getId());
        }
    }

    /**
     * Returns the current value of the given field of this object by calling a getter. This is needed to get values of
     * private fields.
     * 
     * @param field
     * @return
     * 
     * @throws SecurityException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    private Object getProperty(Field field) throws NoSuchMethodException, SecurityException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {
        String fieldName = field.getName();
        Class<? extends AbstractEntity> clazz = getClass();
        // capitalize name
        String nameCap = Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
        // get getter because field is probably private
        Method getter;
        Class<?> type = field.getType();
        if (Boolean.class.equals(type) || boolean.class.equals(type)) {
            try {
                getter = clazz.getMethod("is" + nameCap);
            } catch (NoSuchMethodException e) {
                getter = clazz.getMethod("get" + nameCap);
            }
        } else {
            getter = clazz.getMethod("get" + nameCap);
        }
        // call getter
        return getter.invoke(this);
    }

    /**
     * Structure to combine a field name and value.
     */
    private static final class Property implements Comparable<Property> {

        final String name;
        final Object value;

        public Property(String name, Object value) {
            this.name = name;
            this.value = value;
        }

        @Override
        public int compareTo(Property o) {
            return name.compareTo(o.name);
        }
    }

    /**
     * String used to indent returned content of {@link #toString()}.
     */
    private static final String INDENT_TO_STRING = "    ";

    /**
     * Used to format date types in returned content of {@link #toString()}.
     */
    private static final SimpleDateFormat TO_STRING_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");

    /**
     * Format objects in {@link #toString()} if the objects {@link Object#toString()} method does not return the
     * expected value.
     * 
     * @param obj
     * @return
     */
    private static String noPersistenceObjectToString(Object obj) {
        if (obj instanceof Date) {
            return TO_STRING_DATE_FORMAT.format(obj);
        }
        return obj == null ? "null" : obj.toString();
    }

    /**
     * Convert the given {@link PersistenceObject} to String and append to the given {@link StringBuilder}.
     * 
     * @param builder
     *            Add content to this builder
     * @param indent
     *            Current indentation
     * @param obj
     *            The object which should be formatted
     * @param appendedEntities
     *            remember all non null appended entities to reduce string size and cut of cycles
     * @return The given builder to enable chaining
     */
    private static StringBuilder toString(StringBuilder builder, String indent, AbstractEntity obj,
            List<AbstractEntity> appendedEntities) {
        Class<? extends AbstractEntity> clazz = obj.getClass();
        // append entity name
        builder.append(clazz.getSimpleName()).append(" [id=").append(obj.getId());
        if (appendedEntities.contains(obj)) {
            return builder.append(", ...]");
        }
        appendedEntities.add(obj);
        // append all simple fields and null entities
        Field[] fields = clazz.getDeclaredFields();
        List<Property> fieldsSingle = null; // remember single non null entity fields
        List<Property> fieldsMultiple = null; // remember multiple non null entity fields
        for (Field field : fields) {
            Column anColumn = field.getAnnotation(Column.class);
            ManyToOne anManyToOne = field.getAnnotation(ManyToOne.class);
            OneToMany anOneToMany = field.getAnnotation(OneToMany.class);
            ManyToMany anManyToMany = field.getAnnotation(ManyToMany.class);

            Class<?> type = field.getType();
            String name = field.getName();
            try {
                if (anColumn != null) {
                    if (!"pk".equals(name)) {
                        Object value = obj.getProperty(field);
                        builder.append(", ").append(name).append('=').append(noPersistenceObjectToString(value));
                    }
                } else if (anManyToOne != null) {
                    if (AbstractEntity.class.isAssignableFrom(type)) { // this instanceof AbstractEntity
                        Object value = obj.getProperty(field);
                        if (value == null) { // append null entity
                            builder.append(", ").append(name).append('=').append("null");
                        } else { // append later
                            if (fieldsSingle == null) {
                                fieldsSingle = new ArrayList<Property>();
                            }
                            fieldsSingle.add(new Property(name, value));
                        }
                    }
                } else if (anOneToMany != null || anManyToMany != null) {
                    if (Set.class.isAssignableFrom(type)) {
                        Set<?> values = (Set<?>) obj.getProperty(field);
                        boolean emptySet = false;
                        try {
                            emptySet = values == null || values.isEmpty();
                        } catch (LazyInitializationException e) {
                            builder.append(", ").append(name).append('=').append("{LAZY}");
                            continue;
                        }
                        if (emptySet) { // append empty set
                            builder.append(", ").append(name).append('=').append("{}");
                        } else { // append later
                            if (fieldsMultiple == null) {
                                fieldsMultiple = new ArrayList<Property>();
                            }
                            fieldsMultiple.add(new Property(name, values));
                        }
                    }
                }

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        // // append PersistenceObject properties
        // builder.append(", ").append("created").append('=').append(noPersistenceObjectToString(obj.created));
        // builder.append(", ").append("changed").append('=').append(noPersistenceObjectToString(obj.changed));
        // append single non null entity properties
        indent = indent + INDENT_TO_STRING;
        if (fieldsSingle != null) {
            Collections.sort(fieldsSingle);
            for (Property prop : fieldsSingle) {
                builder.append(",\n").append(indent).append(prop.name).append('=');
                toString(builder, indent, (AbstractEntity) prop.value, appendedEntities);
            }
        }
        if (fieldsMultiple != null) {
            Collections.sort(fieldsMultiple);
            for (Property prop : fieldsMultiple) {
                builder.append(",\n").append(indent).append(prop.name).append("={");
                Set<?> values = (Set<?>) prop.value; // set is not empty here
                boolean first = true;
                String indent2 = indent + INDENT_TO_STRING;
                for (Object value : values) {
                    if (value != null && AbstractEntity.class.isAssignableFrom(value.getClass())) {
                        if (first) {
                            first = false;
                            builder.append("\n").append(indent2);
                        } else {
                            builder.append(",\n").append(indent2);
                        }
                        toString(builder, indent2, (AbstractEntity) value, appendedEntities);
                    }
                }
                builder.append("}");
            }
        }
        return builder.append("]");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return toString(new StringBuilder(), "", this, new ArrayList<AbstractEntity>()).toString();
    }
}
