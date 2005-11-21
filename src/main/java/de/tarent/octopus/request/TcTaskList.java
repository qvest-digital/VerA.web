/* $Id: TcTaskList.java,v 1.1.1.1 2005/11/21 13:33:37 asteban Exp $
 * tarent-octopus, Webservice Data Integrator and Applicationserver
 * Copyright (C) 2002 tarent GmbH
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 * tarent GmbH., hereby disclaims all copyright
 * interest in the program 'tarent-octopus'
 * (which makes passes at compilers) written
 * by Sebastian Mancke and Michael Klink.
 * signature of Elmar Geese, 1 June 2002
 * Elmar Geese, CEO tarent GmbH
 */

package de.tarent.octopus.request;

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
    protected Map tasks;
    protected String description;

    public TcTaskList(Element taskTree, TcModuleConfig moduleConfig) {
        tasks = parseTasks(taskTree, moduleConfig);
    }

    public TcTask getTask(String name) {
        return (TcTask) tasks.get(name);
    }

    /**
     * Auslesen der TaskInformationen
     * Kopie auf TcCommon Config
     * Soll später nur noch hier existieren.
     */
    private Map parseTasks(Element tasksNode, TcModuleConfig moduleConfig) {
        Map tasks = new HashMap();
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
        return tasks;
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