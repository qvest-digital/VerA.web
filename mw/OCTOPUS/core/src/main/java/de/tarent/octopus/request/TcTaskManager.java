package de.tarent.octopus.request;

/*-
 * Veranstaltungsmanagement VerA.web, comprised of…
 * VerA.web, platform-independent webservice-based event management
 * tarent-commons, a set of common components and solutions
 * tarent-contact, platform-independent webservice-based contact management
 * tarent-database, jdbc database library
 * tarent-doctor, Document Generation Platform
 * tarent-octopus, Webservice Data Integrator and Application Server
 * … is newly MIT-licenced and Copyright
 *  © 2018 Атанас Александров (sirakov@gmail.com)
 *  © 2014, 2015, 2016, 2017 Атанас Александров (a.alexandrov@tarent.de)
 *  © 2013 Иванка Александрова (i.alexandrova@tarent.de)
 *  © 2005, 2006, 2007 asteban (s.mancke@tarent.de)
 *  © 2013 Patrick Apel (p.apel@tarent.de)
 *  © 2016 Eugen Auschew (e.auschew@tarent.de)
 *  © 2013 Andrei Boulgakov (a.boulgakov@tarent.de)
 *  © 2013 Valentin But (v.but@tarent.de)
 *  © 2016 Lukas Degener (l.degener@tarent.de)
 *  © 2017 Axel Dirla (a.dirla@tarent.de)
 *  © 2015 Julian Drawe (j.drawe@tarent.de)
 *  © 2009 Sven Frommeyer (s.frommeyer@tarent.de)
 *  © 2014, 2018 Dominik George (d.george@tarent.de)
 *  © 2013 Martin Gernhardt (m.gernhardt@tarent.de)
 *  © 2013 Sascha Girrulat (s.girrulat@tarent.de)
 *  © 2007, 2008 David Goemans (d.goemans@tarent.de)
 *  © 2018 Christian Gorski (c.gorski@tarent.de)
 *  © 2015 Viktor Hamm (v.hamm@tarent.de)
 *  © 2013 Katja Hapke (k.hapke@tarent.de)
 *  © 2006, 2007, 2010, 2013 Hendrik Helwich (h.helwich@tarent.de)
 *  © 2018 Thomas Hensel (t.hensel@tarent.de)
 *  © 2018, 2019 Benedict Hoeger (b.hoeger@tarent.de)
 *  © 2018, 2019 Titian Horvath (t.horvath@tarent.de)
 *  © 2005, 2006, 2007, 2008 Christoph Jerolimov (jerolimov@gmx.de)
 *  © 2018, 2019 Timo Kanera (t.kanera@tarent.de)
 *  © 2006 Philipp Kirchner (p.kirchner@tarent.de)
 *  © 2008, 2009, 2010 Carsten Klein (c.klein@tarent.de)
 *  © 2006 Michael Kleinhenz (m.kleinhenz@tarent.de)
 *  © 2006 Michael Klink (m.klink@tarent.de)
 *  © 2007 Fabian Köster (f.koester@tarent.de)
 *  © 2006, 2014 Martin Ley (m.ley@tarent.de)
 *  © 2007 Alex Maier (a.maier@tarent.de)
 *  © 2014, 2015 Max Marche (m.marche@tarent.de)
 *  © 2007 Jan Meyer (jan@evolvis.org)
 *  © 2007, 2013, 2014, 2015, 2016, 2017, 2018, 2019, 2020
 *     mirabilos (t.glaser@tarent.de)
 *  © 2016 Cristian Molina (c.molina@tarent.de)
 *  © 2006, 2007 Jens Neumaier (j.neumaier@tarent.de)
 *  © 2006 Nils Neumaier (n.neumaier@tarent.de)
 *  © 2018 Yorka Neumann (y.neumann@tarent.de)
 *  © 2017 Michael Nienhaus (m.nienhaus@tarent.de)
 *  © 2013 Claudia Nuessle (c.nuessle@tarent.de)
 *  © 2014, 2015 Jon Nuñez Alvarez (j.nunez-alvarez@tarent.de)
 *  © 2016 Jens Oberender (j.oberender@tarent.de)
 *  © 2016, 2017 Miluška Pech (m.pech@tarent.de)
 *  © 2007, 2008, 2009 Martin Pelzer (m.pelzer@tarent.de)
 *  © 2008, 2009 Christian Preilowski (c.thiel@tarent.de)
 *  © 2013 Marc Radel (m.radel@tarent.de)
 *  © 2013 Sebastian Reimers (s.reimers@tarent.de)
 *  © 2015 Charbel Saliba (c.saliba@tarent.de)
 *  © 2006, 2008, 2009, 2010 Thomas Schmitz (t.schmitz@tarent.de)
 *  © 2013 Volker Schmitz (v.schmitz@tarent.de)
 *  © 2014 Sven Schumann (s.schumann@tarent.de)
 *  © 2007 Robert Schuster (r.schuster@tarent.de)
 *  © 2014 Sevilay Temiz (s.temiz@tarent.de)
 *  © 2013 Kevin Viola Schmitz (k.schmitz@tarent.de)
 *  © 2008, 2015 Stefan Walenda (s.walenda@tarent.de)
 *  © 2015, 2016, 2017 Max Weierstall (m.weierstall@tarent.de)
 *  © 2013 Rebecca Weinz (r.weinz@tarent.de)
 *  © 2015, 2016 Stefan Weiz (s.weiz@tarent.de)
 *  © 2015, 2016 Tim Zimmer (t.zimmer@tarent.de)
 * and older code, Copyright © 2001–2008 ⮡ tarent GmbH and contributors.
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

import de.tarent.octopus.config.TcConfig;
import de.tarent.octopus.content.TcContent;
import de.tarent.octopus.content.TcContentProzessException;
import de.tarent.octopus.content.TcContentWorker;
import de.tarent.octopus.resource.Resources;
import de.tarent.octopus.response.TcResponseDescription;
import de.tarent.octopus.server.OctopusContext;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.List;

/**
 * Diese Klasse verwaltet den Ablauf der Verarbeitung eines Tasks.
 *
 * @author <a href="mailto:mancke@mancke-software.de">Sebastian Mancke</a>,
 * <b>tarent GmbH</b>
 * @author Michael Klink
 */
@Log4j2
public class TcTaskManager {
    /**
     * Strukturierte Liste der Tasks des aktuellen Moduls. Wird erst gesetzt,
     * wenn eine Abarbeitung mit start() gestartet wird.
     */
    TcTaskList taskList;

    /**
     * Benötigt die Config, um sich eine Liste mit Tasks zum aktuellen Modul zu
     * holen.
     */
    TcConfig config;
    TcRequest tcRequest;
    TcContent theContent;
    OctopusContext context;

    // Merkt sich die aktuelle Position
    TcTask.TNode position;

    // Merkt sich die Positionen (TNodes), an die nach dem Aufruf von
    // anderen tasks zurück gesprungen werden soll.
    // Wird erst initialisiert, wenn er wirklich gebraucht wird.
    List trace;

    public static final String ON_ERROR_ACTION_RESUME_NEXT = "resumeNext";

    /**
     * Dieser String enthält die Aktion, die im Fehlerfall ausgeführt werden soll
     */
    private String onErrorAction = null;

    // Status der letzten aktion
    private String status;

    /**
     * Vom letzten ResponseNode erzeugte ResponseDescription
     */
    TcResponseDescription responseDescription;

    public TcTaskManager(OctopusContext context) {
        this.context = context;
        this.config = context.getConfigObject();
        this.tcRequest = context.getRequestObject();
        this.theContent = context.getContentObject();
    }

    /**
     * Setzt die Position auf ein Task, so daß der Aufruf von next() auf die
     * erste Action ziehlt, und prüft, ob dieses Task das Attribut public hat.
     * Wenn nicht, wird eine TcTaskProzessingException ausgelößt
     */
    public void start(String moduleName, String taskName, boolean testAccess)
      throws TcTaskProzessingException {

        taskList = config.getTaskList(moduleName);
        if (taskList == null) {
            throw new TcTaskProzessingException(Resources.getInstance().get("TASK_MANAGER_STRING_NO_TASK_LIST", moduleName));
        }
        TcTask task = taskList.getTask(taskName);
        if (task == null) {
            throw new TcTaskProzessingException(Resources.getInstance().get("TASK_MANAGER_STRING_NO_TASK", taskName, moduleName));
        }
        position = task.rootNode;
        onErrorAction = config.getModuleConfig().getParam("onErrorAction");
        status = TcContentWorker.RESULT_ok;

        if (testAccess && (!"public".equals(((TcTask.TaskNode) position).access))) {
            throw new TcTaskProzessingException(Resources.getInstance().get("TASK_MANAGER_STRING_TASK_NOT_PUBLIC", taskName));
        }
    }

    public boolean doNextStep()
      throws TcTaskProzessingException, TcContentProzessException {

        if (position instanceof TcTask.ResponseNode) {
            // createResponseDescription
            TcTask.ResponseNode respNode = (TcTask.ResponseNode) position;
            responseDescription = new TcResponseDescription(respNode.name, respNode.type);
        }

        try {
            position.perform(this, context);
            return next();
        } catch (TcContentProzessException cpe) {
            if (ON_ERROR_ACTION_RESUME_NEXT.equalsIgnoreCase(getCurrentOnErrorAction())) {
                logger.warn(Resources.getInstance().get("TASK_MANAGER_LOG_ERROR_RESUME", tcRequest.getRequestID()), cpe);
                status = TcContentWorker.RESULT_error;
                theContent.setError(cpe);
                return next();
            } else {
                throw cpe;
            }
        }
    }

    /**
     * Setzt die Position auf die nächste Action, eines Task. oder ausgabeseite,
     * abhängig vonm status.
     *
     * @return gibt true zurück, wenn die die neue Pointerposition auf eine
     * Action zeigt, also als nächstes eine Action abgearbeitet werden
     * soll. Wenn als nächstes die Response generiert werden soll wird
     * false zurück gegeben.
     * @throws TcTaskProzessingException
     */
    protected boolean next() throws TcTaskProzessingException {
        logger.debug("Next called with '" + status + "' at " + position);
        if (position == null) {
            throw new TcTaskProzessingException(
              "Position steht nicht auf einem Node Objekt. Das verarbeiten der Tasks muss mit start() beginnen.");
        }

        // end processing if it is a response node
        if (position instanceof TcTask.ResponseNode) {
            return false;
        }

        TcTask.TNode next = null;

        // Entweder das erste Kind suchen
        next = position.getChild(status);

        // oder den ersten Nachbar suchen
        if (next == null) {
            logger.debug("No child, trying next");
            next = position.getNext();
        }

        // Wenn weder Kinder noch Nachbarn existieren muss
        // nach dem ersten Vater gesucht werden, der Nachfolger hat.
        if (next == null) {
            logger.debug("No next, stepping up");
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

        // Jetzt sollte next auf dem nächsten Element stehen.
        // Dieses kann also nun ausgewertet werden
        if (next instanceof TcTask.DoTaskNode) {
            TcTask.DoTaskNode doTaskNode = (TcTask.DoTaskNode) next;
            String callStatus = doTaskNode.doStatus;
            if (callStatus == null || callStatus.length() == 0) {
                callStatus = status;
            }
            if (trace == null) {
                trace = new ArrayList();
            }
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
    // 			throw new TcTaskProzessingException("Position steht nicht auf einem Action-Element. Das Verarbeiten der Tasks
    // muss mit start() beginnen.");

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
    // 			throw new TcTaskProzessingException("Position steht nicht auf einem Action-Element. Das Verarbeiten der Tasks
    // muss mit start() beginnen.");

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
