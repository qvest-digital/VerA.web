package de.tarent.commons.datahandling.entity;

import de.tarent.commons.logging.LogFactory;
import de.tarent.commons.utils.Pojo;
import de.tarent.commons.utils.StringTools;
import java.lang.reflect.Method;
import java.util.*;

import org.apache.commons.logging.Log;

/**
 * Abstract class for object creation and object filling over reflection.
 * It is intended for subclassing, as well as usage out of the box.
 *
 */
public class DefaultEntityFactory implements EntityFactory {
    private static final Log logger = LogFactory.getLog(DefaultEntityFactory.class);

    public static final String PROPERTY_SEPARATOR = ".";

    Class instantiationClass;
    private static Object[] emptyObjectArray = new Object[]{};
    private static Class[] emptyClassArray = new Class[]{};
    private String className;

    /**
     * The name of the PrimaryKey with which to look up entities in the
     * LookupContext for duplicate-identification. The default is null.
     */
    private String keyName;

    /**
     * Contructor for initialisation with the class, the factory should serve
     * for. Uses the default LookupContext-keyName "id".
     */
    public DefaultEntityFactory(Class instantiationClass) {
	this(instantiationClass, null);
    }

    /**
     * Contructor for initialisation with the class, the factory should serve
     * for.
     *
     * @param keyName The id with which to look up entities in the
     * LookupContext.
     */
    public DefaultEntityFactory(Class instantiationClass, String keyName) {
	setInstantiationClass(instantiationClass);
	this.keyName = keyName;
    }

    /**
     * Returns a factory responsible for creation of instances for supplied attributeName
     * This method should be customized by subclasses.
     */
    protected EntityFactory getFactoryFor(String attributeName) {
	return null;
    }

    /**
     * Returns a factory responsible for creation of instances for supplied attributeName
     * This method should be customized by subclasses.
     */
    protected EntityFactory getFactoryFor(Class attributeType, String attributeName) {
	return EntityFactoryRegistry.getEntityFactory(attributeType);
    }

    protected EntityFactory getFactoryFor(Object entity, String attributeName) {
	return null;
    }

    /**
     * Get the entity from the LookupContext using the keyName as ID.
     */
    public Object getEntityFromLookupContext(AttributeSource as, LookupContext lc) {
	if (keyName == null)
		return null;
	Object id = as.getAttribute(keyName);
	return lc.getEntity(id, instantiationClass.getName());
    }

    /**
     * Store the entity in the LookupContext using the keyName as ID.
     */
    public void storeEntityInLookupContext(Object entity, LookupContext lc) {
	if (keyName == null)
		return;
	Object id = Pojo.get(entity, keyName);
	lc.registerEntity(id, instantiationClass.getName(), entity);
    }

    public Object getEntity() {
	try {
	    return instantiationClass.getConstructor(emptyClassArray).newInstance(emptyObjectArray);
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }

    /**
     * Fills the entity with the AttributeSet
     * TODO: At the moment this implementation covers creation of linked entities only. The filing of referenced entities is not supported for now.
     */
    public void fillEntity(Object entity, AttributeSource as, LookupContext lc) {
	fillEntity(entity, as, lc, false);
    }

    /**
     * Fills the entity with the AttributeSet
     * TODO: At the moment this implementation covers creation of linked entities only. The filing of referenced entities is not supported for now.
     */
    public void fillEntity(Object entity, AttributeSource as, LookupContext lc, boolean fillLinkedEntitiesOnly) {
	assert (as.getAttributeNames() != null) : "AttributeSource.getAttributeNames() should not be null";

	if (logger.isTraceEnabled())
	    logger.trace("filling entity '"+entity+"' (class '"+className+"')");

	// a list to remember, which complex properties we had already set
	List addedReferredProperties = new ArrayList(0);

	for (Iterator iter = as.getAttributeNames().iterator(); iter.hasNext();) {
	    String name = (String)iter.next();

	    int pos = name.indexOf(PROPERTY_SEPARATOR);
	    // property to another entity, supplied from another factory
	    if (pos != -1 ) {
		String propertyName = name.substring(0, pos);

		if (addedReferredProperties.contains(propertyName))
		    // do not add the same assotiation twice
		    continue;
		addedReferredProperties.add(propertyName);

		// create or fill the entity if any field for the entity exist
		EntityFactory ef = getFactoryFor(propertyName);
		if (ef == null)
			ef = getFactoryFor(entity, propertyName);
		if (ef == null)
			ef = getFactoryFor(as.getAttributeType(propertyName), propertyName);
		if (ef == null)
		    throw new RuntimeException("No factory configured for property '"+propertyName+"' (in factory "+getClass().getName()+").");
		PrefixedAttributeSource pas = new PrefixedAttributeSource(propertyName+PROPERTY_SEPARATOR, as);
		if (!pas.hasNotNullFields()) {
		    logger.debug("skip entity creation with only null fields");
		    continue;
		}

		Object referredEntity = null;

		// try to lookup an existing referred entity
		Method getMethod = Pojo.getGetMethod(entity, propertyName);
		if (getMethod != null)
		    referredEntity = Pojo.get(entity, getMethod);

		if (referredEntity != null && !(referredEntity instanceof Collection)) {
		    ef.fillEntity(referredEntity, pas, lc);
		} else {
		    // create a new on, if none exists already
		    referredEntity = ef.getEntity(pas, lc);
		}

		// assign it
		Method setMethod = Pojo.getSetMethod(entity, propertyName);
		Class[] setType = null;
		if (setMethod != null)
		    setType = setMethod.getParameterTypes();

		// set directly (X:1)
		assert setType != null;
		if (setMethod != null && setType[0].isAssignableFrom(referredEntity.getClass())) {
		    if (logger.isTraceEnabled())
			logger.trace("set entity property '"+propertyName+"' to '"+referredEntity+"'");
		    Pojo.set(entity, propertyName, referredEntity);

		    // or append to over an addXXX method (X:N)
		} else {
		    // TODO: speed up with caching of the method lookup
		    try {
			Method addMethod = instantiationClass.getMethod("add"+StringTools.capitalizeFirstLetter(propertyName), new Class[]{referredEntity.getClass()});
			// TODO: all assignable datatypes should be accepted and not just the one specified by referredEntity.getClass()
			if (logger.isTraceEnabled())
			    logger.trace("add value to entity property list '"+propertyName+"' value: '"+referredEntity+"'");

			Pojo.set(entity, addMethod, referredEntity);
		    } catch (NoSuchMethodException nsme){
			throw new RuntimeException("No suitable set or add method in class "+instantiationClass+" for property '"+propertyName + "' with argument type "+referredEntity.getClass()+" found.");
		    }
		}

	     // easy settable property
	    } else {
		if (fillLinkedEntitiesOnly)
		    continue;

		if (logger.isTraceEnabled())
		    logger.trace("set entity property '"+name+"' to '"+as.getAttribute(name)+"'");
		Pojo.set(entity, name, as.getAttribute(name));
	    }
	}
    }

    /**
     * This default implementation fills the entity over reflection
     */
    public Object getEntity(AttributeSource as, LookupContext lc) {
	assert as != null;
	Object entity = getEntityFromLookupContext(as, lc);
	if (entity == null) {
	    entity = getEntity();
	    fillEntity(entity, as, lc, false);
	    storeEntityInLookupContext(entity, lc);
	} else {
	    fillEntity(entity, as, lc, false);
	}
	return entity;
    }

    /**
     * Returns an AttributeSource wrapper over the supplied entity
     */
    public void writeTo(ParamSet target, Object entity) {
	for (Iterator iter = target.getAttributeNames().iterator(); iter.hasNext();) {
	    String attributeName = (String)iter.next();

	    int pos = attributeName.indexOf(PROPERTY_SEPARATOR);
	    if (pos != -1 ) {
		String propertyName = attributeName.substring(0, pos);
		String referingEntityPropertyName = attributeName.substring(pos+1);
		Object referingEntity = Pojo.get(entity, propertyName);
		if (referingEntity != null)
		    target.setAttribute(attributeName, Pojo.get(referingEntity, referingEntityPropertyName));
		else {
		    target.setAttribute(attributeName, null);
		    if (logger.isDebugEnabled())
			logger.debug("No instance for property "+ propertyName + " found in entity "+ entity.getClass().getName() +".");
		}
	    } else {
		target.setAttribute(attributeName, Pojo.get(entity, attributeName));
	    }
	}
    }

    public Class getInstantiationClass() {
	return instantiationClass;
    }

    public void setInstantiationClass(Class newInstantiationClass) {
	this.instantiationClass = newInstantiationClass;
	className = this.instantiationClass.getName();
    }

	public void setKeyName(String keyName) {
		this.keyName = keyName;
	}
}
