package de.tarent.commons.datahandling.entity;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Implementation for the AttributeSource, which delegates to another attribute source using a prefix for each requested
 * property name.
 *
 * @author Sebastian Mancke, tarent GmbH
 */
public class PrefixedAttributeSource implements AttributeSource {
    String prefix;
    AttributeSource delegate;
    private List attributeNames;

    public PrefixedAttributeSource(String prefix, AttributeSource delegate) {
        setPrefix(prefix);
        setDelegate(delegate);
    }

    public Object getAttribute(String attributeName) {
        return delegate.getAttribute(prefix.concat(attributeName));
    }

    public Class getAttributeType(String attributeName) {
        Object param = delegate.getAttribute(prefix.concat(attributeName));
        return param == null ? null : param.getClass();
    }

    /**
     * Returns a list the attribute names
     *
     * @return list of Strings
     */
    public List getAttributeNames() {
        if (attributeNames == null) {
            attributeNames = new ArrayList();
            for (Iterator iter = delegate.getAttributeNames().iterator(); iter.hasNext(); ) {
                String name = (String) iter.next();
                if (name.startsWith(prefix)) {
                    attributeNames.add(name.substring(prefix.length()));
                }
            }
        }
        return attributeNames;
    }

    /**
     * Helper method to test, if any of the values in this AS are != null
     */
    public boolean hasNotNullFields() {
        List attribs = getAttributeNames();
        for (Iterator iter = attribs.iterator(); iter.hasNext(); ) {
            if (getAttribute((String) iter.next()) != null) {
                return true;
            }
        }
        return false;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String newPrefix) {
        this.prefix = newPrefix;
    }

    public AttributeSource getDelegate() {
        return delegate;
    }

    public void setDelegate(AttributeSource newDelegate) {
        this.delegate = newDelegate;
    }
}
