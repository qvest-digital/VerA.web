/**
 * veraweb, platform independent webservice-based event management
 * (Veranstaltungsmanagment VerA.web), is
 * Copyright © 2004-2008 tarent GmbH
 * Copyright © 2013 tarent solutions GmbH
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see: http://www.gnu.org/licenses/
 */
package de.tarent.aa.veraweb.cucumber.env;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import cucumber.annotation.After;
import cucumber.annotation.Before;
import cucumber.runtime.ScenarioResult;
import de.tarent.aa.veraweb.cucumber.env.event.HandlerAfter;
import de.tarent.aa.veraweb.cucumber.env.event.HandlerBefore;
import de.tarent.aa.veraweb.cucumber.env.event.HandlerStart;
import de.tarent.aa.veraweb.cucumber.env.event.HandlerStop;
import de.tarent.cucumber.datamanager.DataManager;

/**
 * This class contains all important (cucumber-)hooks.
 * 
 * @author Sven Schumann <s.schumann@tarent.de>
 * @version 1.0
 */
public class StartAndShutdownHook {

    /**
     * Handlers for processing hooks after cucumber ends.
     */
    private static List< HandlerAfter > afterHandler = new ArrayList< HandlerAfter >();

    /**
     * Handlers for processing hooks before cucumber starts.
     */
    private static List< HandlerBefore > beforeHandler = new ArrayList< HandlerBefore >();

    /**
     * Handlers for processing hooks before each scenario.
     */
    private static List< HandlerStart > startHandler = new ArrayList< HandlerStart >();

    /**
     * Handlers for processing hooks after each scenario.
     */
    private static List< HandlerStop > stopHandler = new ArrayList< HandlerStop >();

    /**
     * Initialization status.
     */
    private static boolean initialised = false;

    /**
     * This hook will be called before cucumber starts to process features.
     * 
     * @throws Exception
     *             exception
     */
    @PostConstruct
    public void postConstruct() throws Exception {
        if (!initialised) {
            initialised = true;
        } else {
            return;
        }

        // initialise shutdown-hook
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                preDestroy();
            }
        });

        // forward event
        for (HandlerStart handler : startHandler) {
            handler.handleStart();
        }

        // initialize datamanager
        DataManager.getInstance().init();
    }

    /**
     * This hook will be called before each scenario.
     */
    @Before
    @org.junit.Before
    public void beforeScenario() {
        // forward event
        for (HandlerBefore handler : beforeHandler) {
            handler.handleBeforeScenario();
        }
    }

    /**
     * This hook will be called after each step.
     * 
     * @param result
     *            {@link ScenarioResult}
     */
    @After
    @org.junit.After
    public void afterScenario(ScenarioResult result) {
        // forward event
        for (HandlerAfter handler : afterHandler) {
            handler.handleAfterScenario(result);
        }
    }

    /**
     * This hook will be called before the program will exit.
     */
    private static void preDestroy() {
        // forward event
        for (HandlerStop handler : stopHandler) {
            handler.handleStop();
        }
    }

    /**
     * Event start handler function.
     * 
     * @param handler
     *            handler
     */
    public static void addOnStartHandler(HandlerStart handler) {
        startHandler.add(handler);
    }

    /**
     * Event stop handler function.
     * 
     * @param handler
     *            handler
     */
    public static void addOnStopHandler(HandlerStop handler) {
        stopHandler.add(handler);
    }

    /**
     * Event after handler function.
     * 
     * @param handler
     *            handler
     */
    public static void addOnAfterHandler(HandlerAfter handler) {
        afterHandler.add(handler);
    }

    /**
     * Event before handler function.
     * 
     * @param handler
     *            handler
     */
    public static void addOnBeforeHandler(HandlerBefore handler) {
        beforeHandler.add(handler);
    }
}
