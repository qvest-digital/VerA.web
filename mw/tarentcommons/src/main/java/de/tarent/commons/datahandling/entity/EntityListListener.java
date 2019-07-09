package de.tarent.commons.datahandling.entity;
import java.util.EventListener;

/**
 * This EventListener is a listener designed to handle change events on EntityLists
 *
 * @author Steffi Tinder, tarent GmbH
 */
public interface EntityListListener extends EventListener {

    public void entityListChanged(EntityListEvent e);
}
