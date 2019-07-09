package de.tarent.commons.datahandling.entity;
import java.util.*;

/**
 * Map based implementation for the attribute source interface.
 *
 * @author Sebastian Mancke, tarent GmbH
 */
public class MapAttributeSource implements AttributeSource {

    Map delegate;

    public MapAttributeSource(Map delegate) {
        setDelegate(delegate);
    }

    public Object getAttribute(String attributeName) {
        return delegate.get(attributeName);
    }

    public Class getAttributeType(String attributeName) {
        Object param = delegate.get(attributeName);
        return param == null ? null : param.getClass();
    }

    /**
     * Returns a list the attribute names
     *
     * @return list of Strings
     */
    public List getAttributeNames() {
        List names = new ArrayList(delegate.size());
        for (Iterator iter = delegate.keySet().iterator(); iter.hasNext(); ) {
            names.add(iter.next());
        }
        return names;
    }

    public Map getDelegate() {
        return delegate;
    }

    public void setDelegate(Map newDelegate) {
        this.delegate = newDelegate;
    }
}
