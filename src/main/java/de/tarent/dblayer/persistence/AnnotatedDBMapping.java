package de.tarent.dblayer.persistence;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Set;

import de.tarent.commons.utils.Pojo;
import de.tarent.dblayer.engine.DBContext;
import de.tarent.dblayer.persistence.annotations.Column;
import de.tarent.dblayer.persistence.annotations.Fields;
import de.tarent.dblayer.persistence.annotations.Id;
import de.tarent.dblayer.persistence.annotations.MappingType;
import de.tarent.dblayer.persistence.annotations.Reference;
import de.tarent.dblayer.persistence.annotations.Table;
import de.tarent.dblayer.persistence.annotations.Transient;

/** This DBMapping is used when an annotated Bean is used. It analyzes
 * the annotations of a bean and defines the mapping depending on these
 * information.
 * 
 * @author Martin Pelzer, tarent GmbH
 *
 */
public abstract class AnnotatedDBMapping extends AbstractDBMapping {

	
	public AnnotatedDBMapping() {
		super();
	}
	
	
	public AnnotatedDBMapping(DBContext dbc, Class bean) {
		super(dbc, bean);
	}
	
	
	/** This method uses the tarent-database annotations in the
	 * associated bean to configure the mapping of the attributes
	 * 
	 */
	@Override
	public void configureMapping() {
		// Iterate over getter methods of associated bean and
		// check annotations for each method. Depending on annotations
		// add field to mapping.
		Object bean;
		try {
			bean = this.associatedBean.newInstance();
		} catch (InstantiationException e) {
			// bean could not be instantiated. Don't do anything because
			// we can't!
			return;
		} catch (IllegalAccessException e) {
			// bean could not be instantiated. Don't do anything because
			// we can't!
			return;
		}
		
		// Maybe there are defined some custom field definitions.
		// We have to add them to the list so that they can be
		// accessed and used later.
		// fields don't have to be defined any longer. They are defined by using.
		//String [] newFields = ((Fields) this.associatedBean.getAnnotation(Fields.class)).value();
		//for (int i = 0; i < newFields.length; i++) {
		//	super.addCustomFieldDefinition(newFields[i]);
		//}
		
		Set attributes = Pojo.getPropertyNames(bean.getClass());
		
		Iterator iter = attributes.iterator();
		while (iter.hasNext()) {
			// get next attribute name and get get method for this attribute
			String attributeName = (String) iter.next();
			
			if (attributeName.equals("class"))
				continue;
			
			Method method = Pojo.getGetMethod(bean, attributeName);
			
			// If annotation @Transient is present for this method,
			// we do nothing.
			if (!method.isAnnotationPresent(Transient.class)) {
				if (method.isAnnotationPresent(Reference.class)) {
					// If annotation @Reference is present we have to add
					// several fields of another bean. To do that we use method "addFields".
					Reference reference = method.getAnnotation(Reference.class);
					
					int includeBitMask = getFieldSetBitMask(reference.fields());
					int fieldSet = getFieldSetBitMask(method);
					DBMapping dbMapping = DAORegistry.getDAOForBean(reference.bean()).getDbMapping();
					
					addFields((AbstractDBMapping) dbMapping, attributeName, includeBitMask, fieldSet);
					
				} else {
					// standard column mapping
					
					String tableName; // name of the table in the database
					String columnName; // name of the column in the database
					
					// get column name for mapping
					Column column = method.getAnnotation(Column.class);
					if (column == null) {
						columnName = attributeName;
					} else {
						columnName = column.name();
					}
					
					// get table name for mapping
					if (column == null || column.table().equals("")) {
						// no table name defined for this column;
						// use table defined for whole bean
						tableName = getTargetTable();
					} else {
						// use table defined for this attribute
						tableName = column.table();
					}
					
					// get fields for mapping
					int fieldSet = getFieldSetBitMask(method);
					
					// check if this key is a primary key field
					if (method.isAnnotationPresent(Id.class)) {
						// this attribute shall be the primary key
						fieldSet = fieldSet | super.getFieldDefinitionValue("primaryKeyFields") | super.getFieldDefinitionValue("minimalFields") | super.getFieldDefinitionValue("commonFields");
					}
					
					// add field mapping
					addField(this.mapToDbConventions(tableName) + PROPTERTY_SEPERATOR + this.mapToDbConventions(columnName), attributeName, fieldSet);
				}
			}
		}
		
		// call method for adding queries
		this.addQueries();
	}
	
	
	private int getFieldSetBitMask(Method method) {
		int fieldSet = 0;
		Fields fields = method.getAnnotation(Fields.class);
		if (fields == null) {
			// use default field set (but not if pk)
			if (!method.isAnnotationPresent(Id.class))
				fieldSet = super.getFieldDefinitionValue("defaultFieldSet");
		} else {
			String [] fieldSets = fields.value();
			
			// iterate over all given field sets, fetch value for each one from list and add it
			for (int i = 0; i < fieldSets.length; i++) {
				fieldSet = fieldSet | super.getFieldDefinitionValue(fieldSets[i]);
			}
		}
		return fieldSet;
	}
	
	
	private int getFieldSetBitMask(String [] fieldSets) {
		int fieldSet = 0;
			
		// iterate over all given field sets, fetch value for each one from list and add it
		for (int i = 0; i < fieldSets.length; i++) {
			fieldSet = fieldSet | super.getFieldDefinitionValue(fieldSets[i]);
		}

		return fieldSet;
	}

	
	/** Returns the name of the target table in the database
	 * as defined in the associated bean. If there is no associated
	 * bean or no annotation @Table defined in the bean null is
	 * returned.
	 * 
	 */
	@Override
	public String getTargetTable() {
		if (this.associatedBean != null) {
			Table annotation = (Table) this.associatedBean.getAnnotation(Table.class);
			if (annotation != null)
				return annotation.name();
		}
		return null;
	}
	
	
	/** method for adding queries
	 * 
	 *
	 */
	public abstract void addQueries();
	
	
	/** Maps a string to the naming conventions of the
	 * database as given in the MappingType annotation. If
	 * MappingType is not defined, the string is returned as
	 * is.
	 * NOTE: the input parameter has to be in camelCase.
	 * 
	 * @return
	 */
	private String mapToDbConventions(String string) {
		MappingType mappingType = (MappingType) this.associatedBean.getAnnotation(MappingType.class);
		if (mappingType == null)
			return string;
		
		if (mappingType.value().equals(MappingType.Value.LOWER_CASE)) {
			return string.toLowerCase();
		}
		else if (mappingType.value().equals(MappingType.Value.UPPER_CASE)) {
			return string.toUpperCase();
		}
		else if (mappingType.value().equals(MappingType.Value.LOWER_CASE_UNDERSCORE)) {
			for (int i = 'A'; i <= 'Z'; i++) {
				String compare = "" + (char) i;
				string = string.replaceAll(compare.trim(), "_" + Character.toLowerCase((char) i));
			}
			return string;
		}
		else if (mappingType.value().equals(MappingType.Value.UPPER_CASE_UNDERSCORE)) {
			for (int i = 'A'; i <= 'Z'; i++) {
				String compare = "" + (char) i;
				string = string.replaceAll(compare.trim(), "_" + Character.toUpperCase((char) i));
			}
			return string;
		}
		else
			// camel case (input is already camel case)
			return string;
	}

}
