package de.tarent.commons.datahandling.entity;

/**
 * This interface stands for an abstract unit that can easily be displayed
 * and managed in tables, without the need to publish any details of the actual object.
 *
 * @author Steffi Tinder, tarent GmbH
 * @author Sebastian Mancke, tarent GmbH
 *
 */

public interface Entity extends ReadableAttribute {
    /**
     *  returns the unique ID of the entity
     * @return the id
     */
    public int getId();
}
