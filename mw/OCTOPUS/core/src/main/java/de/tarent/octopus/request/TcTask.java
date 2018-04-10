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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.tarent.octopus.config.TcModuleConfig;
import de.tarent.octopus.content.TcContentProzessException;
import de.tarent.octopus.content.TcContentWorker;
import de.tarent.octopus.content.TcContentWorkerFactory;
import de.tarent.octopus.content.TcMessageDefinition;
import de.tarent.octopus.content.TcMessageDefinitionPart;
import de.tarent.octopus.content.TcOperationDefinition;
import de.tarent.octopus.content.TcPortDefinition;
import de.tarent.octopus.logging.LogFactory;
import de.tarent.octopus.resource.Resources;
import de.tarent.octopus.response.TcResponseCreator;
import de.tarent.octopus.server.OctopusContext;
import de.tarent.octopus.util.DataFormatException;
import de.tarent.octopus.util.ParamReference;
import de.tarent.octopus.util.Xml;

/**
 * Klasse zur Repräsentation eines Task
 *
 * @author <a href="mailto:mancke@mancke-software.de">Sebastian Mancke</a>, <b>tarent GmbH</b>
 */
public class TcTask {
    protected TaskNode rootNode;
    protected TcTaskList taskList;
    protected List taskErrors;

    private TcModuleConfig moduleConfig;
    private TcMessageDefinition contractInput = null;
    private TcMessageDefinition contractOutput = null;

    private static Log logger = LogFactory.getLog(TcTask.class);

    //
    // Konstanten
    //
    static final String CONTRACT_ELEMENT_NAME = "contract";
    static final String DESCRIPTION_ELEMENT_NAME = "description";
    static final String PARAM_ELEMENT_NAME = "param";
    static final String DOTASK_ELEMENT_NAME = "doTask";
    static final String RESPONSE_ELEMENT_NAME = "response";
    static final String STATUS_ELEMENT_NAME = "status";
    static final String ACTION_ELEMENT_NAME = "action";
    static final String ONERROR_ELEMENT_NAME = "onError";

    static final String TASK_ACCESS_ATTRIBUTE_NAME = "access";
    static final String TASK_GOUPS_ATTRIBUTE_NAME = "groups";
    static final String TASK_NAME_ATTRIBUTE_NAME = "name";

    static final String ACTION_WORKER_ATTRIBUTE_NAME = "worker";
    static final String ACTION_NAME_ATTRIBUTE_NAME = "name";

    static final String STATUS_EQUALS_ATTRIBUTE_NAME = "equals";

    static final String RESPONSE_TYPE_ATTRIBUTE_NAME = "type";
    static final String RESPONSE_NAME_ATTRIBUTE_NAME = "name";

    static final String CONTRACT_PART_OPTIONAL_ATTRIBUTE_NAME = "optional";
    static final String CONTRACT_PART_DESCRIPTION_ATTRIBUTE_NAME = "description";
    static final String CONTRACT_PART_TYPE_ATTRIBUTE_NAME = "type";
    static final String CONTRACT_PART_NAME_ATTRIBUTE_NAME = "name";
    static final String CONTRACT_OUTPUT_ELEMENT_NAME = "output";
    static final String CONTRACT_INPUT_ELEMENT_NAME = "input";

    static final String ONERROR_ACTION_ATTRIBUTE_NAME = "action";
    //
    // Konstruktor
    //
    /**
     * Der Konstruktor parst das übergebene task-DOM-Element und erzeugt daraus
     * eine interne Darstellung des Tasks.
     *
     * @param taskList TaskListe, in der der Task zu stehen kommt. Diese
     *  wird bei doTask-Verzweigungen benötigt.
     * @param taskXmlTree DOM-Element des Tasks
     * @param moduleConfig Modulkonfiguration, zu der der Task geparst wird.
     */
    public TcTask(TcTaskList taskList, Element taskXmlTree, TcModuleConfig moduleConfig) {
	this.taskList = taskList;
	this.moduleConfig = moduleConfig;
	taskErrors = null;
	rootNode = new TaskNode(taskXmlTree);
    }

    //
    // Eigenschaften
    //
    /**
     * Liefert den Namen des Tasks
     */
    public String getName() {
	return rootNode.getName();
    }

    /**
     * Liefert die Beschreibung des Tasks
     */
    public String getDescription() {
	return rootNode.getDescription();
    }

    /**
     * Liefert die Sichtbarkeit des Tasks
     */
    public String getAccess() {
	return rootNode.getAccess();
    }

    /**
     * Liefert die Gruppen, die dieses Task ausführen dürfen.
     */
    public String[] getGroups() {
	return rootNode.getGroups();
    }

    /**
     * Liefert die zu Grunde liegende TaskListe
     */
    public TcTaskList getTaskList() {
	return taskList;
    }

    /**
     * Liefert den TaskNode, die interne Beschreibung des Task-DOM-Elements.
     */
    protected TaskNode getRootNode() {
	return rootNode;
    }

    /**
     * Liefert die kummulierte Ausgabe des Tasks. Falls der Task einen
     * Kontrakt enthält, wird die darin vereinbarte Ausgabe geliefert,
     * sonst eine aus den Taskelementen extrapolierte.
     */
    public TcMessageDefinition getOutputMessage() {
	return contractOutput != null ? contractOutput : rootNode.out(new TcMessageDefinition());
    }

    /**
     * Liefert die kummulierte Eingabe des Tasks. Falls der Task einen
     * Kontrakt enthält, wird die darin vereinbarte Eingabe geliefert,
     * sonst eine aus den Taskelementen extrapolierte.
     */
    public TcMessageDefinition getInputMessage() {
	return contractInput != null ? contractInput : rootNode.in(new TcMessageDefinition(), new TcMessageDefinition());
    }

    // TODO: Ggfs aus Kontrakt Faults herausfischen

    /**
     * Liefert eine äußere Beschreibung des Tasks als WSDL-ähnliche Operation.
     * Falls der Task einen Kontrakt enthält, wird auch darauf zurückgegriffen,
     * sonst wirdnur aus den Taskelementen extrapoliert.
     */
    public TcOperationDefinition getOperationDefinition() {
	TcOperationDefinition od = new TcOperationDefinition(getName(), getDescription());
	od.setInputMessage(getInputMessage());
	od.setOutputMessage(getOutputMessage());
	// TODO: Ggfs aus Kontrakt Faults herausfischen und hier einfügen
	return od;
    }

    //
    // Validierung: Fehlerüberprüfung
    //
    /**
     * Überprüft, ob das Task gültig ist.
     * Wenn dies nicht der Fall ist, kann anschließend über
     * getTaskErrors() eine Liste mit Fehlern abgefragt werden.
     */
    public boolean isValid() {
	if (taskErrors == null) {
	    taskErrors = rootNode.getErrors();
	    taskErrors.addAll(getContractErrors());
	}
	return (taskErrors.size() == 0);
    }

    /**
     * Diese Methode überprüft, falls es einen Kontrakt gibt, ob die
     * tatsächlichen Ein- und Ausgaben des Tasks dem Kontrakt genügen.
     *
     * @return eine Liste mit Fehlermeldungen bezüglich der Kontrakterfüllung.
     */
    public List getContractErrors() {
	List errors = new ArrayList();
	if (contractInput != null) {
	    Map contractMap = partsListToMap(contractInput.getParts());
	    TcMessageDefinition taskInput = rootNode.in(new TcMessageDefinition(), new TcMessageDefinition());
	    if (taskInput == null)
		errors.add(Resources.getInstance().get("TASK_ERROR_UNKNOWN_TASK_INPUT", getName()));
	    else for(Iterator itIns = taskInput.getParts().iterator(); itIns.hasNext();) {
		TcMessageDefinitionPart inPart = (TcMessageDefinitionPart) itIns.next();
		TcMessageDefinitionPart conPart = (TcMessageDefinitionPart) contractMap.get(inPart.getName());
		if (inPart.isOptional()) {
		} else if (conPart == null)
		    errors.add(Resources.getInstance().get("TASK_ERROR_CONTRACT_MISSES_INPUT", getName(), inPart.getName()));
		else if (!isSubTypeOf(conPart.getPartDataType(), inPart.getPartDataType()))
		    errors.add(Resources.getInstance().get("TASK_ERROR_CONTRACT_INCOMPATIBLE_INPUT", getName(), inPart.getName()));
	    }
	}
	if (contractOutput != null) {
	    TcMessageDefinition taskOutput = rootNode.out(new TcMessageDefinition());
	    if (taskOutput == null)
		errors.add(Resources.getInstance().get("TASK_ERROR_UNKNOWN_TASK_OUTPUT", getName()));
	    else {
		Map taskMap = partsListToMap(taskOutput.getParts());
		Iterator itCons = contractOutput.getParts().iterator();
		while (itCons.hasNext()) {
		    TcMessageDefinitionPart conPart = (TcMessageDefinitionPart) itCons.next();
		    TcMessageDefinitionPart outPart = (TcMessageDefinitionPart) taskMap.get(conPart.getName());
		    if (outPart == null || outPart.isOptional())
			errors.add(Resources.getInstance().get("TASK_ERROR_CONTRACT_MISSES_OUTPUT", getName(), conPart.getName()));
		    else if (!isSubTypeOf(outPart.getPartDataType(), conPart.getPartDataType()))
			errors.add(Resources.getInstance().get("TASK_ERROR_CONTRACT_INCOMPATIBLE_OUTPUT", getName(), conPart.getName()));
		}
	    }
	}
	return errors;
    }

    /**
     * Liefert Fehlerbeschreibungen als Liste von Strings.
     *
     * @return String Liste mit Fehlerbeschreibungen.
     */
    public List getTaskErrors() {
	if (taskErrors == null)
	    isValid();
	return taskErrors;
    }

    //
    // Klasse Object
    //
    public String toString() {
	return rootNode.toString();
    }

    //
    // Hilfsmethoden
    //
    /**
     * Diese Methode erzeugt aus einer Liste von MessageParts eine Map der
     * Namen dieser MessageParts auf den jeweiligen MessagePart.
     */
    protected static Map partsListToMap(List parts) {
	Map partsMap = new LinkedHashMap();
	if (parts != null) {
	    Iterator itParts = parts.iterator();
	    while(itParts.hasNext()) {
		TcMessageDefinitionPart part = (TcMessageDefinitionPart) itParts.next();
		partsMap.put(part.getName(), part);
	    }
	}
	return partsMap;
    }

    /**
     * Diese Methode ermittelt, ob der erste Parameter einen Untertyp des
     * zweiten darstellt.
     */
    protected boolean isSubTypeOf(String subType, String superType) {
	// TODO: Implementierung des Vergleichs deutlich verbessern
	if (superType == null)
		return true;
	if (superType.equals(subType))
	    return true;
	return false;
    }

    protected static String expand(String arg, OctopusContext context) {
	final Pattern parsePattern = Pattern.compile("\\{([^{}]*)\\}");
	if (arg == null || context == null)
	    return arg;
	Matcher matcher = parsePattern.matcher(arg);
	StringBuffer result = new StringBuffer();
	int start;
	do {
	    start = 0;
		matcher.reset(arg);
		result.delete(0 , result.length());
		while(matcher.find()) {
		    result.append(arg.subSequence(start, matcher.start()));
		    result.append(context.getContextField(matcher.group(1)));
		    start = matcher.end();
		}
		result.append(arg.substring(start));
		arg = result.toString();
	} while(start > 0);
	return arg;
    }

    /**
     * Diese Klasse modelliert die Knoten der Taskbeschreibung.
     */
    protected abstract class TNode {
		protected TNode getTNode(Element element, TNode parent) {
	    TNode out = null;
	    if (element != null) {
		String elementName = element.getTagName();
		if (ACTION_ELEMENT_NAME.equals(elementName))
		    out = new ActionNode(element, parent);
		else if (STATUS_ELEMENT_NAME.equals(elementName))
		    out = new StatusNode(element, parent);
		else if (PARAM_ELEMENT_NAME.equals(elementName))
		    out = new ParamNode(element, parent);
		else if (RESPONSE_ELEMENT_NAME.equals(elementName))
		    out = new ResponseNode(element, parent);
		else if (DOTASK_ELEMENT_NAME.equals(elementName))
		    out = new DoTaskNode(element, parent);
		else if (DESCRIPTION_ELEMENT_NAME.equals(elementName))
			out = new CommentNode(element, parent);
		else if (CONTRACT_ELEMENT_NAME.equals(elementName))
		    out = new ContractNode(element, parent);
		else if (ONERROR_ELEMENT_NAME.equals(elementName))
		    out = new OnErrorNode(element, parent);
		}
	    return out;
	}

	protected TNode(TNode parent) {
	    this.parent = parent;
	}

	/**
	 * Selbstständiges Ausführen der Aktionen dieser TaskNode
	 */
	protected void perform(TcTaskManager manager, OctopusContext context)
	    throws TcTaskProzessingException, TcContentProzessException {
	    logger.debug(Resources.getInstance().get("TASK_STRING_PERFORMING_NODE", context.getRequestObject().getRequestID(),this.getClass().getName()));
	}

	public TcMessageDefinition out(TcMessageDefinition out) {
	    return out;
	}

	public TcMessageDefinition in(TcMessageDefinition in, TcMessageDefinition outInterim) {
	    return in;
	}

	public List getErrors() {
	    return new ArrayList();
	}

	public TNode getNext() {
	    return null;
	}

	public TNode getParent() {
	    return parent;
	}
	public TNode getChild(String status) {
	    return null;
	}

	public String toString() {
	    return "TNode";
	}

	protected TNode parent = null;
    }

    /**
     * Diese Klasse modelliert den task-Knoten der Task-Beschreibung
     */
    protected class TaskNode extends TNode {
		protected String name;
	protected String description = "";
	protected String access;
	protected String[] groups;

	protected TNode child;

	public TaskNode(Element taskXmlTree) {
	    super(null);
	    name = taskXmlTree.getAttribute(TASK_NAME_ATTRIBUTE_NAME);
	    access = taskXmlTree.getAttribute(TASK_ACCESS_ATTRIBUTE_NAME);
	    groups = parseGroups(taskXmlTree.getAttribute(TASK_GOUPS_ATTRIBUTE_NAME));

	    Element childNode = Xml.getFirstChildElement(taskXmlTree);
	    if (childNode == null) {
		logger.warn("No children found for: " + taskXmlTree);
	    } else if (DESCRIPTION_ELEMENT_NAME.equals(childNode.getNodeName())) {
		StringBuffer sb = new StringBuffer();
		NodeList children = childNode.getChildNodes();
		String content;
		for (int j = 0; j < children.getLength(); j++) {
		    content = children.item(j).getNodeValue();
		    if (content != null)
			sb.append(content);
		}
		description = sb.toString();
	    }
	    child = getTNode(childNode, this);
	}

	protected String[] parseGroups(String groupsString) {
		String groupArray[] = groupsString.split("[,]");
		for (int i = 0; i < groupArray.length; i++) {
			groupArray[i] = groupArray[i].trim();
		}
		return groupArray;
	}

	public String getName() {
	    return name;
	}
	public String getAccess() {
	    return access;
	}

	public String[] getGroups() {
	    return groups;
	}

	public String getDescription() {
	    return description;
	}

	public List getErrors() {
	    List out;
	    if (child == null) {
		out = new ArrayList();
		out.add(Resources.getInstance().get("TASK_ERROR_EMPTY_TASK", name));
	    } else {
		out = child.getErrors();
	    }
	    if (name == null || name.length() == 0)
		out.add(Resources.getInstance().get("TASK_ERROR_EMPTY_TASK_NAME", this));
	    if (access == null || access.length() == 0)
		out.add(Resources.getInstance().get("TASK_ERROR_EMPTY_ACCESS_ATTRIBUTE", name));
	    return out;
	}

	public TNode getChild(String status) {
	    return child;
	}

	public TcMessageDefinition out(TcMessageDefinition out) {
	    return (child != null) ? child.out(out) : out;
	}

	public TcMessageDefinition in(TcMessageDefinition in, TcMessageDefinition outInterim) {
	    return (child != null) ? child.in(in, outInterim) : in;
	}

	public String toString() {
	    return Resources.getInstance().get("TASK_STRING_TASKNODE", new Object[] {name, access, description, child});
	}
    }

    /**
     * Diese Klasse modlliert einen action-Knoten der Task-Beschreibung.
     */
    protected class ActionNode extends TNode {
		protected String name;
	protected String worker;
	protected String workerClassName;

	protected TNode next;
	protected TNode child;

	public ActionNode(Element actionElement, TNode parent) {
	    super(parent);
	    name = actionElement.getAttribute(ACTION_NAME_ATTRIBUTE_NAME);
	    worker = actionElement.getAttribute(ACTION_WORKER_ATTRIBUTE_NAME);

	    Element childNode = Xml.getFirstChildElement(actionElement);
	    child = getTNode(childNode, this);

	    Element nextNode = Xml.getNextSiblingElement(actionElement);
	    next = getTNode(nextNode, getParent());
	}

	protected void perform(TcTaskManager manager, OctopusContext context)
	    throws TcTaskProzessingException, TcContentProzessException {
	    String requestID = context.getRequestObject().getRequestID();
	    logger.debug(Resources.getInstance().get("TASK_STRING_PERFORMING_NODE", requestID, "ActionNode", "do action action="+name +" with worker="+worker));
	    try {
		TcContentWorker workerInstance = TcContentWorkerFactory.getContentWorker(context.moduleConfig(), worker, requestID);
		String status = workerInstance.doAction(context.getConfigObject(), name, context.getRequestObject(), context.getContentObject());
		manager.setStatus(status);
		logger.debug(Resources.getInstance()
			    .get("TASK_LOG_WORKER_CALLED",
				 new Object[] { requestID, status, name, worker, workerInstance.getVersion()}));
	    } catch(TcContentProzessException cpe) {
		throw cpe;
	    } catch (Exception e) {
		throw new TcContentProzessException(e);
	    }
	}

	public List getErrors() {
	    List out = new ArrayList();
	    if (child != null)
		out.addAll(child.getErrors());

	    if (next != null)
		out.addAll(next.getErrors());

	    if (name == null || name.length() == 0)
		out.add(Resources.getInstance().get("TASK_ERROR_EMPTY_ACTION_NAME", this));

	    if (worker == null)
		out.add(Resources.getInstance().get("TASK_ERROR_EMPTY_ACTION_WORKER", name));

	    if (moduleConfig.getContentWorkerDeclaration(worker) == null)
		out.add(Resources.getInstance().get("TASK_ERROR_WORKER_CLASS_ERROR", new Object[]{name, worker}));
	    else
		try {
		    TcContentWorker workerObject = TcContentWorkerFactory.getContentWorker(moduleConfig, worker, null);

		    //TcContentWorker workerObject = (TcContentWorker) workerClass.newInstance();
		    TcPortDefinition port = workerObject.getWorkerDefinition();
		    if (port == null)
			out.add(Resources.getInstance().get("TASK_ERROR_WORKER_NO_PORT", new Object[]{name, worker, workerClassName, workerObject.getVersion()}));
		    else if (port.getOperation(name) == null)
			out.add(Resources.getInstance().get("TASK_ERROR_WORKER_NO_ACTION", new Object[]{name, worker, workerClassName, workerObject.getVersion()}));
		} catch (Exception e) {
		    out.add(Resources.getInstance().get("TASK_ERROR_WORKER_INSTANTIATION_ERROR", new Object[]{name, worker, workerClassName, e}));
		}
	    return out;
	}

	public TcMessageDefinition out(TcMessageDefinition out) {
	    TcMessageDefinition messageDefinition = getWorkerOutMessage();
	    if (messageDefinition != null)
		out.addParts(messageDefinition.getParts());
	    if (child != null)
		out = child.out(out);

	    if (next != null)
		return next.out(out);
	    else
		return out;
	}

	public TcMessageDefinition in(TcMessageDefinition in, TcMessageDefinition outInterim) {
	    TcMessageDefinition msgDefIn = getWorkerInMessage();
	    if (msgDefIn != null) {
		Map outParts = partsListToMap(outInterim.getParts());
		Iterator itParts = msgDefIn.getParts().iterator();
		while (itParts.hasNext()) {
		    TcMessageDefinitionPart part = (TcMessageDefinitionPart) itParts.next();
		    TcMessageDefinitionPart outPart = (TcMessageDefinitionPart) outParts.get(part.getName());
		    if (outPart != null && !outPart.isOptional()) {
			String partType = part.getPartDataType();
			if (!isSubTypeOf(outPart.getPartDataType(), partType))
			    logger.warn(Resources.getInstance().get("TASK_ERROR_INNER_INCOMPATIBILITY", new Object[] {TcTask.this.getName(), name, part.getName(), partType, outPart.getPartDataType()}));
		    } else
			in.addPart(part);
		}
	    }

	    TcMessageDefinition msgDefOut = getWorkerOutMessage();
	    if (msgDefOut != null)
		outInterim.addParts(msgDefOut.getParts());
	    if (child != null)
		in = child.in(in, outInterim);

	    return (next != null) ? next.in(in, outInterim) : in;
	}

	public TNode getNext() {
	    return next;
	}

	public TNode getChild(String status) {
	    return child;
	}

	protected TcMessageDefinition getWorkerOutMessage() {
	    try {
		TcContentWorker theWorker = TcContentWorkerFactory.getContentWorker(moduleConfig, worker, null);
		if (theWorker.getWorkerDefinition() == null || theWorker.getWorkerDefinition().getOperation(name) == null)
		    return null;
		return theWorker.getWorkerDefinition().getOperation(name).getOutputMessage();
		// Keine Fehlerbehandlung, da es hinterher eh static sein soll.
	    } catch (Exception e) {
		logger.warn(Resources.getInstance().get("TASK_ERROR_WORKER_DESCRIPTION_ERROR", worker), e);
	    }
	    return new TcMessageDefinition();
	}

	protected TcMessageDefinition getWorkerInMessage() {
	    try {
		TcContentWorker theWorker = TcContentWorkerFactory.getContentWorker(moduleConfig, worker, null);
		if (theWorker.getWorkerDefinition() == null || theWorker.getWorkerDefinition().getOperation(name) == null)
		    return null;
		return theWorker.getWorkerDefinition().getOperation(name).getInputMessage();
		// Keine Fehlerbehandlung, da es hinterher eh static sein soll.
	    } catch (Exception e) {
		logger.warn(Resources.getInstance().get("TASK_ERROR_WORKER_DESCRIPTION_ERROR", worker), e);
	    }
	    return new TcMessageDefinition();
	}

	public String toString() {
	    return Resources.getInstance().get("TASK_STRING_ACTIONNODE", new Object[] {name, worker, workerClassName, child, next});
	}
    }

    /**
     * Diese Klasse modelliert status-Knoten in der Task-Beschreibung
     */
    protected class StatusNode extends TNode {
		protected TNode child;
	protected TNode next;

	protected String equalsCondition;

	public StatusNode(Element statusElement, TNode parent) {
	    super(parent);
	    equalsCondition = statusElement.getAttribute(STATUS_EQUALS_ATTRIBUTE_NAME);

	    Element childNode = Xml.getFirstChildElement(statusElement);
	    child = getTNode(childNode, this);

	    Element nextNode = Xml.getNextSiblingElement(statusElement);
	    next = getTNode(nextNode, getParent());
	}

	public TNode getNext() {
	    return next;
	}

	public TNode getChild(String status) {
	    return (equalsCondition.equals(status)) ? child : null;
	}

	public List getErrors() {
	    List out = new ArrayList();
	    if (child != null)
		out.addAll(child.getErrors());

	    if (next != null)
		out.addAll(next.getErrors());

	    if (equalsCondition == null || equalsCondition.length() == 0)
		out.add(Resources.getInstance().get("TASK_ERROR_STATUS_NO_CONDITION", this));

	    return out;
	}

	public TcMessageDefinition out(TcMessageDefinition in) {
	    // TODO: Entscheidung, ob Unterzweig relevant, anders treffen.
	    if ("ok".equals(equalsCondition))
		in = child.out(in);
	    if (next != null)
		return next.out(in);
	    return in;
	}

	public TcMessageDefinition in(TcMessageDefinition in, TcMessageDefinition outInterim) {
	    if (child != null)
		in = child.in(in, outInterim);

	    if (next != null)
		return next.in(in, outInterim);
	    return in;
	}

	public String toString() {
	    return Resources.getInstance().get("TASK_STRING_STATUSNODE", equalsCondition, child, next);
	}

    }

    /**
     * Diese Klasse modelliert den doTask-Knoten in der Task-Beschreibung
     */
    protected class DoTaskNode extends TNode {
	protected TNode next;

	protected String name;
	protected String doStatus;

	public DoTaskNode(Element doTaskElement, TNode parent) {
	    super(parent);
	    name = doTaskElement.getAttribute("name");
	    doStatus = doTaskElement.getAttribute("status");

	    Element nextNode = Xml.getNextSiblingElement(doTaskElement);
	    next = getTNode(nextNode, getParent());
	}

	public TcMessageDefinition out(TcMessageDefinition out) {
	    TaskNode task = getReferingTask(null);
	    if (task != null)
		out = task.out(out);
	    return (next != null) ? next.out(out) : out;
	}

	public TcMessageDefinition in(TcMessageDefinition in, TcMessageDefinition outInterim) {
	    TaskNode task = getReferingTask(null);
	    if (task != null)
		in = task.in(in, outInterim);
	    return (next != null)? next.in(in, outInterim) : in;
	}

	public TNode getNext() {
	    return next;
	}

	public TNode getChild(String status) {
	    return null;// getReferingTask();
	}

	public List getErrors() {
	    List out = new ArrayList();
	    if (next != null)
		out.addAll(next.getErrors());

	    if (getTaskList().getTask(name) == null)
		out.add(Resources.getInstance().get("TASK_ERROR_NO_TARGET_TASK", this, name));

	    if (name.equals(getName()))
		out.add(Resources.getInstance().get("TASK_ERROR_RECURSION", this, name));

	    return out;
	}

	public TaskNode getReferingTask(OctopusContext context) {
	    TcTask theTask = getTaskList().getTask(expand(name, context));
	    return (theTask != null) ? theTask.getRootNode() : null;
	}

	public String toString() {
	    return Resources.getInstance().get("TASK_STRING_DOTASKNODE", name, doStatus, next);
	}
    }

    /**
     * Diese Klasse modelliert response-Knoten in der Task-Beschreibung
     */
    protected class ResponseNode extends TNode {
		protected String type;
	protected String name;
	protected Map paramMap;

	public ResponseNode(Element responseElement, TNode parent) {
	    super(parent);
	    name = responseElement.getAttribute(RESPONSE_NAME_ATTRIBUTE_NAME);
	    type = responseElement.getAttribute(RESPONSE_TYPE_ATTRIBUTE_NAME);

	    try {
		paramMap = Xml.getParamMap(responseElement);
	    } catch (DataFormatException dfe) {
		logger.warn(Resources.getInstance().get("TASK_ERROR_RESPONSE_PARAMS_SPURIOUS", this), dfe);
	    }
	}

	protected void perform(TcTaskManager manager, OctopusContext context)
	    throws TcTaskProzessingException, TcContentProzessException {
	    logger.debug(Resources.getInstance().get("TASK_STRING_PERFORMING_NODE", context.getRequestObject().getRequestID(), "ResponseNode", " type="+type+" name="+name));

	    Map mapCopy = new HashMap();
	    mapCopy.putAll(paramMap);

	    // replace ref values
	    for (Iterator iter = mapCopy.entrySet().iterator(); iter.hasNext();) {
		Map.Entry me = (Map.Entry)iter.next();
		if (me.getValue() instanceof ParamReference) {
		    Object resolvedValue = context.getContextField(((ParamReference)me.getValue()).getRefvalue());
		    mapCopy.put(me.getKey(), resolvedValue);
		}
	    }
	    context.setContent("responseParams", mapCopy);
	}

	public List getErrors() {
	    List out = new ArrayList();

	    if (type == null || type.length() == 0)
		out.add(Resources.getInstance().get("TASK_ERROR_RESPONSE_NO_TYPE", this));

	    return out;
	}

	public TcMessageDefinition out(TcMessageDefinition out) {
	    TcMessageDefinition returnDefinition;
	    if (out != null && paramMap != null &&
		// TODO: Generische lösung ...
		("soap".equalsIgnoreCase(type)
		 ||"rpc".equalsIgnoreCase(type)
		 ||"xmlrpc".equalsIgnoreCase(type))
		) {

		returnDefinition = new TcMessageDefinition();
		Map responses = TcResponseCreator.refineOutputFields(paramMap.get("OutputFields"));
		for (Iterator respIter = responses.keySet().iterator(); respIter.hasNext();) {
		    String fieldNameOutput = (String)respIter.next();
		    String fieldNameContent = (String)responses.get(fieldNameOutput);
		    TcMessageDefinitionPart part = out.getPartByName(fieldNameContent);
		    if (part != null) {
			part.setName(fieldNameOutput);
			returnDefinition.addPart(part);
		    }
		}
	    } else
		returnDefinition = super.out(out);
	    return returnDefinition;
	}

	public String toString() {
	    return Resources.getInstance().get("TASK_STRING_RESPONSENODE", name, type, paramMap);
	}
    }

    /**
     * Diese Klasse modelliert param-Knoten in der Task-Beschreibung
     */
    protected class ParamNode extends TNode {
		protected String name;
	protected Object value;

	protected TNode next;

	public ParamNode(Element paramElement, TNode parent) {
	    super(parent);
	    try {
		name = Xml.getParamName(paramElement);
		value = Xml.getParamValue(paramElement);
	    } catch (DataFormatException dfe) {
		logger.warn(Resources.getInstance().get("TASK_ERROR_PARAM_PARAMS_SPURIOUS", this), dfe);
	    }

	    Element nextNode = Xml.getNextSiblingElement(paramElement);
	    next = getTNode(nextNode, getParent());
	}

	protected void perform(TcTaskManager manager, OctopusContext context)
	    throws TcTaskProzessingException, TcContentProzessException {
	    logger.debug(Resources.getInstance().get("TASK_STRING_PERFORMING_NODE", context.getRequestObject().getRequestID(), "ParamNode", " name="+name+" value="+value));

	    // TODO: Die Params müssten eigentlich rekursiv durchlaufen werden um alle darinliegenden ParamReference auf auflösen zu können
	    Object resolvedValue = value;
	    if (value instanceof ParamReference) {
		resolvedValue = context.getContextField(((ParamReference)value).getRefvalue());
		if (resolvedValue == null)
		    logger.debug(Resources.getInstance().get("TASK_LOG_PARAM_RESOLVED_NULL", context.getRequestObject().getRequestID(), name));
	    }

	    context.setContextField(name, resolvedValue);
	}

	public TNode getNext() {
	    return next;
	}

	public TNode getChild(String status) {
	    return null;
	}

	public List getErrors() {
	    List out = new ArrayList();

	    if (name == null || name.length() == 0)
		out.add(Resources.getInstance().get("TASK_ERROR_PARAM_NO_NAME", this));

	    if (value == null)
		out.add(Resources.getInstance().get("TASK_ERROR_PARAM_NO_VALUE", this));

	    return out;
	}

	public TcMessageDefinition out(TcMessageDefinition out) {
	    String type = "xsd:anyType";

	    if (value instanceof String)
		type = TcMessageDefinition.TYPE_SCALAR;
	    else if (value instanceof List)
		type = TcMessageDefinition.TYPE_ARRAY;
	    else if (value instanceof Map)
		type = TcMessageDefinition.TYPE_STRUCT;

	    out.addPart(name, type, "");
	    return out;
	}

	public TcMessageDefinition in(TcMessageDefinition in, TcMessageDefinition outInterim) {
	    return in;
	}

	public String toString() {
	    return Resources.getInstance().get("TASK_STRING_PARAMNODE", name, value);
	}
    }

    /**
     * Diese Klasse modelliert comment-Knoten in der Task-Beschreibung.
     */
    protected class CommentNode extends TNode {
	protected TNode next;

	protected String comment;

	public CommentNode(Element commentElement, TNode parent) {
	    super(parent);

	    StringBuffer buffer = new StringBuffer();
	    for (Node child = commentElement.getFirstChild(); child != null; child = child.getNextSibling()) {
		buffer.append(child.getNodeValue());
	    }
	    comment = buffer.toString();

	    next = getTNode(Xml.getNextSiblingElement(commentElement), getParent());
	}

	public TNode getNext() {
	    return next;
	}

	public TNode getChild(String status) {
	    return null;
	}

	public List getErrors() {
	    return (next != null) ? next.getErrors() : new ArrayList();
	}

	public TcMessageDefinition out(TcMessageDefinition out) {
	    return (next != null) ? next.out(out) : out;
	}

	public TcMessageDefinition in(TcMessageDefinition in, TcMessageDefinition outInterim) {
	    return (next != null) ? next.in(in, outInterim) : in;
	}

	public String toString() {
	    return Resources.getInstance().get("TASK_STRING_COMMENTNODE", comment);
	}
    }

    /**
     * Diese Klasse beschreibt contract-Knoten in der Task-Beschreibung
     */
    protected class ContractNode extends TNode {
		protected TNode next;

	protected TcMessageDefinition input = new TcMessageDefinition();
	protected TcMessageDefinition output = new TcMessageDefinition();

	public ContractNode(Element contractElement, TNode parent) {
	    super(parent);

	    next = getTNode(Xml.getNextSiblingElement(contractElement), getParent());

	    if (contractInput == null)
		contractInput = new TcMessageDefinition();
	    if (contractOutput == null)
		contractOutput = new TcMessageDefinition();
	    for (Element child = Xml.getFirstChildElement(contractElement); child != null; child = Xml.getNextSiblingElement(child)) {
		String name = child.getAttribute(CONTRACT_PART_NAME_ATTRIBUTE_NAME);
		String datatype = child.getAttribute(CONTRACT_PART_TYPE_ATTRIBUTE_NAME);
		String description = child.getAttribute(CONTRACT_PART_DESCRIPTION_ATTRIBUTE_NAME);
		String optionalString = child.getAttribute(CONTRACT_PART_OPTIONAL_ATTRIBUTE_NAME);
		boolean optional = optionalString != null && optionalString.length() > 0 && "1jJyYtTwW".indexOf(optionalString.charAt(0)) >= 0;
		// TODO: Test, ob wohlbeschrieben, ggfs übertragen in Fehlerbereich
		TcMessageDefinitionPart part = new TcMessageDefinitionPart(name, datatype, description, optional);
		if (CONTRACT_INPUT_ELEMENT_NAME.equals(child.getNodeName())) {
		    input.addPart(part);
		    // TODO: Test, ob schon enthalten, Vergleich
		    contractInput.addPart(part);
		} else if (CONTRACT_OUTPUT_ELEMENT_NAME.equals(child.getNodeName())) {
		    output.addPart(part);
		    // TODO: Test, ob schon enthalten, Vergleich
		    contractOutput.addPart(part);
		} else {
		    logger.warn(Resources.getInstance().get("TASK_ERROR_CONTRACT_PART_UNKNOWN", child.getNodeName(), part));
		}
	    }
	}

	public TNode getNext() {
	    return next;
	}

	public TNode getChild(String status) {
	    return null;
	}

	public List getErrors() {
	    // TODO: Fehler im Kontrakt erkennen und anwarnen
	    return (next != null) ? next.getErrors() : new ArrayList();
	}

	public TcMessageDefinition out(TcMessageDefinition out) {
	    return (next != null) ? next.out(out) : out;
	}

	public TcMessageDefinition in(TcMessageDefinition in, TcMessageDefinition outInterim) {
	    return (next != null) ? next.in(in, outInterim) : in;
	}

	public String toString() {
	    return Resources.getInstance().get("TASK_STRING_CONTRACTNODE", input, output);
	}
    }

    /**
     * Diese Klasse beschreibt onError-Knoten in der Task-Beschreibung, die
     * für die folgenden Knoten die Fehlerbehandlung bei
     * {@link de.tarent.octopus.content.TcContentProzessException}s festlegen.
     */
    protected class OnErrorNode extends TNode {
		protected TNode next;

		protected String action = null;

	public OnErrorNode(Element contractElement, TNode parent) {
	    super(parent);

	    next = getTNode(Xml.getNextSiblingElement(contractElement), getParent());

	    action = contractElement.getAttribute(ONERROR_ACTION_ATTRIBUTE_NAME);
	}

	protected void perform(TcTaskManager manager, OctopusContext context)
	    throws TcTaskProzessingException, TcContentProzessException {
	    logger.debug(Resources.getInstance().get("TASK_STRING_PERFORMING_NODE", context.getRequestObject().getRequestID(), "OnErrorNode", "set action="+action));
			manager.setOnErrorAction(action);
	}

	public TNode getNext() {
	    return next;
	}

	public TNode getChild(String status) {
	    return null;
	}

	public List getErrors() {
	    List out = new ArrayList();
	    if (next != null)
		out.addAll(next.getErrors());

	    if (action == null || action.length() == 0)
		out.add(Resources.getInstance().get("TASK_ERROR_ONERROR_NO_ACTION", this));

	    return out;
	}

	public TcMessageDefinition out(TcMessageDefinition out) {
	    return (next != null) ? next.out(out) : out;
	}

	public TcMessageDefinition in(TcMessageDefinition in, TcMessageDefinition outInterim) {
	    return (next != null) ? next.in(in, outInterim) : in;
	}

	public String toString() {
	    return Resources.getInstance().get("TASK_STRING_ONERRORNODE", action);
	}
    }
}
