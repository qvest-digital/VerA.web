package de.tarent.commons.datahandling.entity;

public interface EntityList {

   /**
    * returns the size of the list
    * @return
    */
   public int getSize();

   /**
    * adds an Entity to the List, no duplicates should be allowed
    * @param entity
    */
   public void addEntity(Object entity);

   public void removeEntity(Object entity);

   public Object getEntityAt(int index);

   public int indexOf(Object entity);

   public void addEntityListListener(EntityListListener listener);

   public void removeEntityListListener(EntityListListener listener);

   public void removeAllEntityListListeners();

   public boolean contains(Object entity);

   public void clear();

}
