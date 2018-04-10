package de.tarent.octopus.request;

/*-
 * VerA.web-Middleware, newly MIT licenced, is comprised of:
 * tarent-commons, a set of common components and solutions
 * tarent-contact, platform-independent webservice-based contact management
 * tarent-database, jdbc database library
 * tarent-doctor, Document Generation Platform
 * tarent-octopus, Webservice Data Integrator and Application Server
 *  © 2005, 2006, 2007 asteban <s.mancke@tarent.de>
 *  © 2007 David Goemans <d.goemans@tarent.de>
 *  © 2006, 2007, 2010 Hendrik Helwich <h.helwich@tarent.de>
 *  © 2005, 2006, 2007 Christoph Jerolimov <c.jerolimov@tarent.de>
 *  © 2006 Philipp Kirchner <p.kirchner@tarent.de>
 *  © 2010 Carsten Klein <c.klein@tarent.de>
 *  © 2006 Michael Kleinhenz <m.kleinhenz@tarent.de>
 *  © 2006 Michael Klink <m.klink@tarent.de>
 *  © 2007 Fabian Köster <f.koester@tarent.de>
 *  © 2006 Martin Ley <m.ley@tarent.de>
 *  © 2007 Alex Maier <a.maier@tarent.de>
 *  © 2007, 2015, 2017, 2018 mirabilos <t.glaser@tarent.de>
 *  © 2006, 2007 Jens Neumaier <j.neumaier@tarent.de>
 *  © 2006 Nils Neumaier <n.neumaier@tarent.de>
 *  © 2007, 2008 Martin Pelzer <m.pelzer@tarent.de>
 *  © 2008, 2009 Christian Preilowski <c.thiel@tarent.de>
 *  © 2006, 2008, 2009 Thomas Schmitz <t.schmitz@tarent.de>
 *  © 2007 Robert Schuster <r.schuster@tarent.de>
 * and older code, Copyright © 2001–2007 ⮡ tarent GmbH and contributors.
 * Licensor is tarent solutions GmbH, http://www.tarent.de/
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
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
		return (TcTask)tasks.get(name);
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
		for (Iterator e = tasks.values().iterator(); e.hasNext(); ) {
			TcTask currTask = (TcTask)e.next();
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
			TcTask currTask = (TcTask)e.next();
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
	 *
	 * @return Map
	 */
	public Iterator getTasksKeys() {
		return tasks.keySet().iterator();
	}
}
