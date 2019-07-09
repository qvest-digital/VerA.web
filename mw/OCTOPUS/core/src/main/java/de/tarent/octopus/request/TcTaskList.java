package de.tarent.octopus.request;
import de.tarent.octopus.config.TcModuleConfig;
import de.tarent.octopus.content.TcPortDefinition;
import de.tarent.octopus.util.Xml;
import org.w3c.dom.Element;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Klasse zur Repräsentation einer Sammlung von Tasks
 *
 * @author <a href="mailto:mancke@mancke-software.de">Sebastian Mancke</a>, <b>tarent GmbH</b>
 */
public class TcTaskList implements Serializable {
    private static final long serialVersionUID = 8732899088703240707L;

    protected String nameSpace;
    protected HashMap tasks = new HashMap();
    protected String description;

    public TcTaskList() {
    }

    public TcTask getTask(String name) {
        return (TcTask) tasks.get(name);
    }

    /**
     * Auslesen der TaskInformationen
     * Kopie auf TcCommon Config
     * Soll später nur noch hier existieren.
     */
    public void parseTasks(Element tasksNode, TcModuleConfig moduleConfig) {
        // Evtl. auch nicht vorhanden
        nameSpace = tasksNode.getAttribute("name");
        if (nameSpace == null || nameSpace.length() == 0) {
            nameSpace = moduleConfig.getName();
        }

        Element currTaskElement = Xml.getFirstChildElement(tasksNode);
        while (currTaskElement != null) {
            TcTask currTask = new TcTask(this, currTaskElement, moduleConfig);
            tasks.put(currTask.getName(), currTask);

            currTaskElement = Xml.getNextSiblingElement(currTaskElement);
        }
    }

    public TcPortDefinition getPortDefinition() {
        TcPortDefinition pd = new TcPortDefinition(nameSpace, description);
        for (Iterator e = tasks.values().iterator(); e.hasNext(); ) {
            TcTask currTask = (TcTask) e.next();
            pd.addOperation(currTask.getOperationDefinition());
        }
        return pd;
    }

    /**
     * Liefert eine Map.
     * Diese enthält für jedes Task, das fehlerhaft ist einen Eintrag
     * mit dem Tasknamen als String Key und Fehlerliste als String Vector.
     * Oder null, wenn kein Task fehlerhalt ist.
     *
     * @return Map mit Tasknamen als String Keys und Vectoren von Strings als Values, oder null
     */
    public Map getErrors() {
        Map out = new HashMap();
        for (Iterator e = tasks.values().iterator(); e.hasNext(); ) {
            TcTask currTask = (TcTask) e.next();
            List errList = currTask.getTaskErrors();
            if (errList.size() > 0) {
                out.put(currTask.getName(), errList);
            }
        }
        if (out.size() > 0) {
            return out;
        }
        return null;
    }

    /**
     * Liefert die Tasks
     *
     * @return Map
     */
    public Iterator getTasksKeys() {
        return tasks.keySet().iterator();
    }
}
