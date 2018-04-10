package de.tarent.dblayer.persistence;

import java.util.LinkedList;
import java.util.List;

/**
 * A simple list of parameters and values.
 *
 * @see de.tarent.dblayer.persistence.AbstractDAO#getEntityByIdFilterList(DBContext, String, ParamList)
 *
 * @author Hendrik Helwich
 */
public class ParamList {

    private List params;

    /**
     * Adds a parameter and its value to the list.
     *
     * @param   name
     *          The name of the parameter
     * @param   value
     *          The value of the parameter
     * @return  The reference of this object
     */
    public ParamList add(String name, Object value) {
        if (params == null)
            params = new LinkedList();
        params.add(new Object[] { name, value });
        return this;
    }

    /**
     * Returns the list of parameters and its values.
     *
     * @return  The list of parameters and its values.
     *          An Element of this list is a two-dimensional
     *          {@link Object} array. The first element of this array is
     *          a {@link java.lang.String} object.
     */
    public List getParams() {
        return params;
    }
}
