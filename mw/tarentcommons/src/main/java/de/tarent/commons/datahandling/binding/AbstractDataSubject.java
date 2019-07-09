package de.tarent.commons.datahandling.binding;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Interface for a subject in an observer pattern.
 */
public class AbstractDataSubject implements DataSubject {

    protected List dataChangedListener;

    protected void fireDataChanged(DataChangedEvent e) {
        if (dataChangedListener == null) {
            return;
        }
        for (Iterator iter = dataChangedListener.iterator(); iter.hasNext(); ) {
            DataChangedListener listener = (DataChangedListener) iter.next();
            listener.dataChanged(e);
        }
    }

    public void addDataChangedListener(DataChangedListener listener) {
        if (dataChangedListener == null) {
            dataChangedListener = new ArrayList(2);
        }
        dataChangedListener.add(listener);
    }

    /**
     * Removes a DataChangedListener
     *
     * @param listener The registered listener
     */
    public void removeDataChangedListener(DataChangedListener listener) {
        if (dataChangedListener != null) {
            dataChangedListener.remove(listener);
        }
    }
}
