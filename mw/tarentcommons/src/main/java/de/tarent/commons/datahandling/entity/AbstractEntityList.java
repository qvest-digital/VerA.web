package de.tarent.commons.datahandling.entity;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * this abstract class implements only the funcionality to register listeners,
 * any other method is left abstract
 *
 * @author Steffi Tinder, tarent GmbH
 */

public abstract class AbstractEntityList implements EntityList {

    private List entityListListeners;

    public AbstractEntityList() {
        entityListListeners = new ArrayList();
    }

    public void addEntityListListener(EntityListListener listener) {
        entityListListeners.add(listener);
    }

    public void removeEntityListListener(EntityListListener listener) {
        entityListListeners.remove(listener);
    }

    public void removeAllEntityListListeners() {
        entityListListeners.clear();
    }

    public void fireEntityListChanged(EntityListEvent event) {
        Iterator iterator = entityListListeners.iterator();
        while (iterator.hasNext()) {
            ((EntityListListener) iterator.next()).entityListChanged(event);
        }
    }

    abstract public void addEntity(Object entity);

    abstract public void removeEntity(Object entity);

    abstract public Object getEntityAt(int index);

    abstract public int getSize();
}
