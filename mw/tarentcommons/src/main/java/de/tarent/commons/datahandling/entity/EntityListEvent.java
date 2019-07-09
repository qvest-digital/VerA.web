package de.tarent.commons.datahandling.entity;
import java.util.EventObject;

/**
 * this class implements an Event to handle EntityListEvents
 * An EntityListEvent can be of the type INSERT, DELETE or UPDATE,
 * the type has to be specified in the contructor
 *
 * @author Steffi Tinder, tarent GmbH
 */

public class EntityListEvent extends EventObject {
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 3997106056581798888L;

    public final static int INSERT = 0;
    public final static int DELETE = 1;
    public final static int UPDATE = 2;

    private int type;

    int firstRow = 0;
    int lastRow = Integer.MAX_VALUE;

    public EntityListEvent(Object source, int t) {
        super(source);
        type = t;
    }

    public EntityListEvent(Object source, int firstRow, int lastRow, int t) {
        this(source, t);
        this.firstRow = firstRow;
        this.lastRow = lastRow;
    }

    public int getFirstRow() {
        return firstRow;
    }

    public int getLastRow() {
        return lastRow;
    }

    public int getType() {
        return type;
    }
}
