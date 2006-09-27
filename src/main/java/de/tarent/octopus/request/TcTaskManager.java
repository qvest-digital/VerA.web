/*
 * $Id: TcTaskManager.java,v 1.3 2006/09/27 11:50:04 asteban Exp $
 * tarent-octopus, Webservice Data Integrator and Applicationserver Copyright
 * (C) 2002 tarent GmbH
 * 
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option)
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place - Suite 330, Boston, MA 02111-1307, USA.
 * 
 * tarent GmbH., hereby disclaims all copyright interest in the program
 * 'tarent-octopus' (which makes passes at compilers) written by Sebastian
 * Mancke and Michael Klink. signature of Elmar Geese, 1 June 2002 Elmar Geese,
 * CEO tarent GmbH
 */

package de.tarent.octopus.request;

import de.tarent.octopus.server.OctopusContext;
import de.tarent.octopus.config.TcConfig;
import de.tarent.octopus.content.*;
import de.tarent.octopus.response.*;
import de.tarent.octopus.resource.Resources;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Diese Klasse verwaltet den Ablauf der Verarbeitung eines Tasks. 
 * 
 * @author <a href="mailto:mancke@mancke-software.de">Sebastian Mancke</a>,
 *         <b>tarent GmbH</b>
 * @author Michael Klink
 */
public class TcTaskManager {
	/** Der Logger */
	private static Logger logger =
		Logger.getLogger(TcTaskManager.class.getName());

	/**
	 * Strukturierte Liste der Tasks des aktuellen Moduls. Wird erst gesetzt,
	 * wenn eine Abarbeitung mit start() gestartet wird.
	 */
	TcTaskList taskList;

	/**
	 * Ben�tigt die Config, um sich eine Liste mit Tasks zum aktuellen Modul zu
	 * holen.
	 */
	TcConfig config;
    TcRequest tcRequest;
    TcContent theContent;
    OctopusContext context;

	// Merkt sich die aktuelle Position
	TcTask.TNode position;

	// Merkt sich die Positionen (TNodes), an die nach dem Aufruf von
	// anderen tasks zur�ck gesprungen werden soll.
	// Wird erst initialisiert, wenn er wirklich gebraucht wird.
	List trace;

    public static final String ON_ERROR_ACTION_RESUME_NEXT = "resumeNext";

	/** Dieser String enth�lt die Aktion, die im Fehlerfall ausgef�hrt werden soll */
	private String onErrorAction = null;


    // Status der letzten aktion
    private String status;
    

    /** Vom letzten ResponseNode erzeugte ResponseDescription 
     */
    TcResponseDescription responseDescription;

	
	public TcTaskManager(OctopusContext context) {
        this.context = context;
        this.config = context.getConfigObject();
        this.tcRequest = context.getRequestObject();
        this.theContent = context.getContentObject();
	}

	/**
	 * Setzt die Position auf ein Task, so da� der Aufruf von next() auf die
	 * erste Action ziehlt, und pr�ft, ob dieses Task das Attribut public hat.
	 * Wenn nicht, wird eine TcTaskProzessingException ausgel��t
	 */
	public void start(String moduleName, String taskName, boolean testAccess)
		throws TcTaskProzessingException {

        taskList = config.getTaskList(moduleName);
        if (taskList == null)
            throw new TcTaskProzessingException(Resources.getInstance().get("TASK_MANAGER_STRING_NO_TASK_LIST", moduleName));
		TcTask task = taskList.getTask(taskName);
        if (task == null)
            throw new TcTaskProzessingException(Resources.getInstance().get("TASK_MANAGER_STRING_NO_TASK", taskName, moduleName));
		position = task.rootNode;
		onErrorAction = config.getModuleConfig().getParam("onErrorAction");
        status = TcContentWorker.RESULT_ok;
        
		if (testAccess && (!"public".equals(((TcTask.TaskNode) position).access)))
			throw new TcTaskProzessingException(Resources.getInstance().get("TASK_MANAGER_STRING_TASK_NOT_PUBLIC", taskName));
	}

    public boolean doNextStep() 
        throws TcTaskProzessingException, TcContentProzessException {

        if (position instanceof TcTask.ResponseNode) {
            // createResponseDescription
            TcTask.ResponseNode respNode = (TcTask.ResponseNode)position;
            responseDescription = new TcResponseDescription(respNode.name, respNode.type);
        }
        
        try {
        	position.perform(this, context);
            return next();
        } catch (TcContentProzessException cpe) {
            if (ON_ERROR_ACTION_RESUME_NEXT.equalsIgnoreCase(getCurrentOnErrorAction())) {
                logger.log(Level.WARNING, Resources.getInstance().get("TASK_MANAGER_LOG_ERROR_RESUME", tcRequest.getRequestID()), cpe);
                status = TcContentWorker.RESULT_error;
                theContent.setError(cpe);
                return next();
            } else {
                throw cpe;
            }
        }
    }

	/**
	 * Setzt die Position auf die n�chste Action, eines Task. oder ausgabeseite, 
     * abh�ngig vonm status.
	 * 
	 * @return gibt true zur�ck, wenn die die neue Pointerposition auf eine
	 *         Action zeigt, also als n�chstes eine Action abgearbeitet werden
	 *         soll. Wenn als n�chstes die Response generiert werden soll wird
	 *         false zur�ck gegeben.
	 * @throws TcTaskProzessingException 
	 */
	protected boolean next() throws TcTaskProzessingException {
		logger.finer("Next called with '" + status + "' at " + position);
		if (position == null)
			throw new TcTaskProzessingException("Position steht nicht auf einem Node Objekt. Das verarbeiten der Tasks muss mit start() beginnen.");

        // end processing if it is a response node
        if (position instanceof TcTask.ResponseNode)            
            return false;

		TcTask.TNode next = null;

		// Entweder das erste Kind suchen
		next = position.getChild(status);

		// oder den ersten Nachbar suchen
		if (next == null) {
			logger.finer("No child, trying next");
			next = position.getNext();
		}

		// Wenn weder Kinder noch Nachbarn existieren muss
		// nach dem ersten Vater gesucht werden, der Nachfolger hat.
		if (next == null) {
			logger.finer("No next, stepping up");
			TcTask.TNode parent = position.getParent();
			do {
				if (parent != null && !(parent instanceof TcTask.TaskNode)) {
					next = parent.getNext();
					parent = parent.getParent();
				} else if (trace != null && trace.size() > 0) {
					position = (TcTask.TNode) trace.remove(trace.size() - 1);
                    // ATTENTION: the perform in a TNode is called twice,
                    // because here we move the position pointer to the tnode again.
					return true;
				} else {
					throw new TcTaskProzessingException(
						"Unerwartetes Ende des Tasks bei: " + position);
				}
			} while (next == null);
		}

		// Jetzt sollte next auf dem n�chsten Element stehen.
		// Dieses kann also nun ausgewertet werden
        if (next instanceof TcTask.DoTaskNode) {
			TcTask.DoTaskNode doTaskNode = (TcTask.DoTaskNode) next;
			String callStatus = doTaskNode.doStatus;
			if (callStatus == null || callStatus.length() == 0)
				callStatus = status;
			if (trace == null)
				trace = new ArrayList();
			trace.add(next);
			position = doTaskNode.getReferingTask(context);
			if (position == null) {
				throw new TcTaskProzessingException(
					"Undefiniertes Task '"
						+ doTaskNode.name
						+ "' bei: "
						+ doTaskNode);
			}
			return true;
		} else {
			position = next;
            return true;
        }

	}

// 	/**
// 	 * Liefert den Namen der aktuellen Aktion oder wirft eine Exception, wenn
// 	 * das aktuelle Element keine Aktion ist.
// 	 */
// 	public String getCurrentActionName() throws TcTaskProzessingException {
// 		if (position == null || !(position instanceof TcTask.ActionNode))
// 			throw new TcTaskProzessingException("Position steht nicht auf einem Action-Element. Das Verarbeiten der Tasks muss mit start() beginnen.");

// 		String value = ((TcTask.ActionNode) position).name;

// 		if (value == null)
// 			throw new TcTaskProzessingException("Das Action-Element muss ein 'name' Attribut besitzen.");

// 		return value;
// 	}

// 	/**
// 	 * Liefert den Namen des aktuellen Workers oder wirft eine Exception, wenn
// 	 * das aktuelle Element keine Aktion ist.
// 	 */
// 	public String getCurrentActionWorker() throws TcTaskProzessingException {
// 		if (position == null || !(position instanceof TcTask.ActionNode))
// 			throw new TcTaskProzessingException("Position steht nicht auf einem Action-Element. Das Verarbeiten der Tasks muss mit start() beginnen.");

// 		String value = ((TcTask.ActionNode) position).worker;

// 		if (value == null)
// 			throw new TcTaskProzessingException("Das Action-Element muss ein 'worker' Attribut besitzen.");

// 		return value;
// 	}

	/**
	 * Liefert die aktuelle ResponseDescription
	 */
	public TcResponseDescription getCurrentResponseDescription() {
        return responseDescription;
	}
	
	public String getCurrentOnErrorAction() {
		return onErrorAction;
	}

	public void setOnErrorAction(String newOnErrorAction) {
        onErrorAction = newOnErrorAction;
	}


    public String getStatus() {
        return status;
    }

    public void setStatus(String newStatus) {
        this.status = newStatus;
    }

}
