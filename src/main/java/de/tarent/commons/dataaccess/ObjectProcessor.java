package de.tarent.commons.dataaccess;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import de.tarent.commons.dataaccess.data.AttributeSet;
import de.tarent.commons.dataaccess.data.AttributeSetImpl;
import de.tarent.commons.dataaccess.query.AbstractQueryContext;
import de.tarent.commons.utils.Pojo;

/**
 * Helper class for the {@link QueryProcessor} and the {@link StoreProcessor}
 * for seperate object beans in {@link AttributeSet attribute sets}.
 * 
 * @author Christoph Jerolimov, tarent GmbH
 * @author Timo Pick, tarent GmbH
 */
public abstract class ObjectProcessor extends AbstractQueryContext {
	/**
	 * Seperate the given bean attribute in many many attribute sets. For every
	 * full attribute set the method
	 * {@link #handleSeperatedBeanAttributeSets(Object, AttributeSet, Object[])}
	 * will be invoked.
	 * 
	 * @param bean
	 * @param parameters
	 */
	protected void seperateBeanArgumentsToAttributeSets(
			Object bean,
			Object[] parameters) {
		
		AttributeSet attributeSet = new AttributeSetImpl();
		
		Map propertyTypes = Pojo.getReadablePropertyTypes(bean);
		for (Iterator propertyIterator = propertyTypes.entrySet().iterator(); propertyIterator.hasNext(); ) {
			Map.Entry entry = (Map.Entry) propertyIterator.next();
			String property = (String) entry.getKey();
			Class type = (Class) entry.getValue();
			Object value = Pojo.get(bean, property);
			
			if (type.isAssignableFrom(String.class)) {
				attributeSet.setAttribute(property, value);
			} else if (type.isAssignableFrom(Number.class)) {
				attributeSet.setAttribute(property, value);
			} else if (type.isAssignableFrom(Collection.class)) {
				for (Iterator childIterator = ((Collection)value).iterator(); childIterator.hasNext(); ) {
					seperateBeanArgumentsToAttributeSets(childIterator.next(), parameters);
				}
//			} else if (type.isAssignableFrom(Calendar.class)) {
				// TODO calendar transformtaion
//			} else if (type.isAssignableFrom(Date.class)) {
				// TODO date transformation
			} else {
				throw new IllegalArgumentException(
						"Currently object trees are not supported. [" +
						"property=" + property + "; " +
						"type=" + type.getName() + "; " +
						"value=" + value + "]");
			}
		}
		
		handleSeperatedBeanAttributeSets(bean, attributeSet, parameters);
	}

	/**
	 * Handler for new attribute sets.
	 * 
	 * @param bean
	 * @param attributeSet
	 * @param parameters
	 */
	protected abstract void handleSeperatedBeanAttributeSets(
			Object bean,
			AttributeSet attributeSet,
			Object[] parameters);
}
