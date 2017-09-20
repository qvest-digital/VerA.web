package de.tarent.octopus.request;

/*-
 * tarent-octopus, Webservice Data Integrator and Application Server
 * Copyright © 2002–2017 tarent solutions GmbH and its contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
import de.tarent.octopus.config.TcModuleConfig;
import de.tarent.octopus.content.TcPortDefinition;
import de.tarent.octopus.util.Xml;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;

/** 
 * Klasse zur Repräsentation einer Sammlung von Tasks
 *
 * @author <a href="mailto:mancke@mancke-software.de">Sebastian Mancke</a>, <b>tarent GmbH</b>
 */
public class TcTaskList {
    protected String nameSpace;
    protected Map tasks = new HashMap();
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
        if (nameSpace == null || nameSpace.length() == 0)
            nameSpace = moduleConfig.getName();

        Element currTaskElement = Xml.getFirstChildElement(tasksNode);
        while (currTaskElement != null) {
            TcTask currTask = new TcTask(this, currTaskElement, moduleConfig);
            tasks.put(currTask.getName(), currTask);

            currTaskElement = Xml.getNextSiblingElement(currTaskElement);
        }
    }

    public TcPortDefinition getPortDefinition() {
        TcPortDefinition pd = new TcPortDefinition(nameSpace, description);
        for (Iterator e = tasks.values().iterator(); e.hasNext();) {
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
        for (Iterator e = tasks.values().iterator(); e.hasNext();) {
            TcTask currTask = (TcTask) e.next();
            List errList = currTask.getTaskErrors();
            if (errList.size() > 0)
                out.put(currTask.getName(), errList);
        }
        if (out.size() > 0)
            return out;
        return null;
    }

    /**
     * Liefert die Tasks
     * @return Map
     */
    public Iterator getTasksKeys() {
        return tasks.keySet().iterator();
    }
}