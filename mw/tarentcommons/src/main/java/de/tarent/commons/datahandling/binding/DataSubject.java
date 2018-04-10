package de.tarent.commons.datahandling.binding;

/**
 * Interface for a subject in an observer pattern.
 */
public interface DataSubject {

    /**
     * Adds a listener for events about changes in the models data
     * @param listener The listener for dataChanged Events
     */
    public void addDataChangedListener(DataChangedListener listener);

    /**
     * Removes a DataChangedListener
     * @param listener The registered listener
     */
    public void removeDataChangedListener(DataChangedListener listener);

}
