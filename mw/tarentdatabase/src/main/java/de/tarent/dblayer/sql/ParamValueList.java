package de.tarent.dblayer.sql;

import de.tarent.commons.datahandling.entity.ParamSet;
import de.tarent.commons.datahandling.entity.AttributeSource;
import java.util.*;

/**
 * List of ParamValues.
 * This List extension is capable of crearing and filling the Params.
 */
public class ParamValueList extends ArrayList implements ParamSet {

    /**
     * Clears all attributes of this statement
     */
    public void clearAttributes() {
        for (Iterator iter = iterator(); iter.hasNext();) {
            ParamValue paramValue = (ParamValue)iter.next();
            paramValue.clear();
        }
    }

    /**
     * Returns true, if all params in this list are set. Returns false, otherwise.
     */
    public boolean isSet() {
        for (Iterator iter = iterator(); iter.hasNext();) {
            ParamValue paramValue = (ParamValue)iter.next();
            if (!paramValue.isSet())
                return false;
        }
        return true;
    }

    /**
     * Sets the attribute <code>attributeName</code> of this statement to the supplied value.
     */
    public void setAttribute(String attributeName, Object attributeValue) {
        for (Iterator iter = iterator(); iter.hasNext();) {
            ParamValue paramValue = (ParamValue)iter.next();
            if (attributeName.equals(paramValue.getName())) {
                paramValue.setValue(attributeValue);
            }
            // do not break, if there are more than one param with the same name, all have to be set
        }
    }

    /**
     * Sets the attributes of this statement to the supplied attributes in the map.
     */
    public void setAttributes(AttributeSource attributeSource) {
        for (Iterator iter = attributeSource.getAttributeNames().iterator(); iter.hasNext();) {
            String paramName = (String)iter.next();
            setAttribute(paramName, attributeSource.getAttribute(paramName));
        }
    }

    public List getAttributeNames() {
        List names = new ArrayList(size());
        for (Iterator iter = iterator(); iter.hasNext();) {
            ParamValue paramValue = (ParamValue)iter.next();
            names.add(paramValue.getName());
        }
        return names;
    }
}
