package de.tarent.dblayer.sql;
import java.util.List;

/**
 * Interface for elements which may contain parameters.
 */
public interface ParamHolder {

    /**
     * Appends the parameters of the paramHolder to the supplied list.
     * The order of the params is determined by the order of appearance
     * of the params in the holder object.
     *
     * @param list A list to take up ParamValue ebjects.
     */
    public void getParams(List list);
}
