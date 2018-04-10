package de.tarent.dblayer.persistence;

public interface Field {

    /**
     * Returns the table column, this field is mapped to
     */
    public String getColumnName();

    /**
     * Returns objects property name for this field
     */
    public String getPropertyName();

}
