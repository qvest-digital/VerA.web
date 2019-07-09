package de.tarent.dblayer.persistence;
import java.lang.reflect.Method;

import de.tarent.commons.datahandling.entity.DefaultEntityFactory;
import de.tarent.commons.datahandling.entity.EntityFactory;
import de.tarent.commons.datahandling.entity.EntityFactoryRegistry;
import de.tarent.dblayer.persistence.annotations.Id;
import de.tarent.dblayer.persistence.annotations.Reference;

/**
 * default entity factory for annotated beans
 *
 * @author Martin Pelzer, tarent GmbH
 */
public class DefaultAnnotatedBeanEntityFactory extends DefaultEntityFactory {

    public DefaultAnnotatedBeanEntityFactory(Class instantiationClass) {
        super(instantiationClass);

        // set keyName depending on annotation @Id in instantianClass (if given)
        Method[] methods = instantiationClass.getMethods();
        for (int i = 0; i < methods.length; i++) {
            if (methods[i].getAnnotation(Id.class) != null) {
                // We found the method marked with @Id. Set key name.
                super.setKeyName(methods[i].getName().substring(3).toLowerCase());
            }
        }
    }

    public DefaultAnnotatedBeanEntityFactory(Class instantiationClass, String keyName) {
        super(instantiationClass, keyName);
    }

    @Override
    protected EntityFactory getFactoryFor(Object entity, String attributeName) {
        Method getMethod;
        try {
            getMethod = entity.getClass().getMethod("get" + this.capitalize(attributeName.toLowerCase()), null);
        } catch (SecurityException e) {
            return null;
        } catch (NoSuchMethodException e) {
            // the bean does not have a get method for this attribute. So we can
            // not return an EntityFactory. Return null
            return null;
        }

        // Get annotation @Reference from method and get target bean
        // from this annotation. Then get EntityFactory for target bean
        // from EntityFactoryRegistry.
        Reference reference = getMethod.getAnnotation(Reference.class);
        return EntityFactoryRegistry.getEntityFactory(reference.bean());
    }

    /**
     * capitalizes the first character of a String
     *
     * @param s the string to capitalize
     * @return the capitalized string
     */
    private static String capitalize(String string) {
        char asCharArray[] = string.toCharArray();

        // capitalize first letter
        asCharArray[0] = Character.toUpperCase(asCharArray[0]);

        return new String(asCharArray);
    }
}
