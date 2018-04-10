package de.tarent.commons.richClient;

import de.tarent.commons.datahandling.entity.Entity;

public interface EntityEditor {

    public void init();
    public void destruct();

    public void setActive(boolean active);
    public void selectEntity(Entity entity);
    public void editCurrentEntity();
    public void newEntity();
    public void cancelEdit();
    public void saveEdit();

}
