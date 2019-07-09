package de.tarent.octopus.server;
import java.util.LinkedList;

/**
 * This class gives a static access to the OctopusContext Object associated with the
 * request of the current Thread. At the begin of the octopus request processing
 * the context is set by an ThreadLocal Variable. So it can be obtained at later time.
 * Without passing a reference to it.
 *
 * During the processing of an Octopus request the active OctpusContext may change
 * depending on the current executed scope.
 *
 * @author <a href="mailto:sebastian@tarent.de">Sebastian Mancke</a>, <b>tarent GmbH</b>
 * @version 1.1
 */
public class Context {
    /**
     * Hold a stack of context informations in a {@link LinkedList}.
     */
    private static ThreadLocal currentContext = new ThreadLocal() {
        public Object initialValue() {
            return new LinkedList();
        }
    };

    /**
     * Returns the current active OctopusContext for this thread
     */
    public static OctopusContext getActive() {
        LinkedList stack = (LinkedList) currentContext.get();
        if (!stack.isEmpty()) {
            return (OctopusContext) stack.getLast();
        } else {
            return null;
        }
    }

    /**
     * Add the current active OctopusContext on the content stack.
     */
    public static void addActive(OctopusContext context) {
        ((LinkedList) currentContext.get()).addLast(context);
    }

    /**
     * Remove one context information from the context stack.
     */
    public static void clear() {
        LinkedList stack = (LinkedList) currentContext.get();
        if (!stack.isEmpty()) {
            stack.removeLast();
        }
    }
}
