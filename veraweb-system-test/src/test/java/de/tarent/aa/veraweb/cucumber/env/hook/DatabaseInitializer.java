/**
 * veraweb, platform independent webservice-based event management
 * (Veranstaltungsmanagment VerA.web), is
 * Copyright © 2004–2008 tarent GmbH
 * Copyright © 2013–2015 tarent solutions GmbH
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
package de.tarent.aa.veraweb.cucumber.env.hook;

import org.springframework.beans.factory.annotation.Autowired;

import com.googlecode.flyway.core.Flyway;
import com.googlecode.flyway.core.api.MigrationInfo;
import com.googlecode.flyway.core.api.MigrationInfoService;
import com.googlecode.flyway.core.api.MigrationType;

import cucumber.runtime.ScenarioResult;
import de.tarent.aa.veraweb.cucumber.env.StartAndShutdownHook;
import de.tarent.aa.veraweb.cucumber.env.Utils;
import de.tarent.aa.veraweb.cucumber.env.event.HandlerAfter;
import de.tarent.aa.veraweb.cucumber.env.event.HandlerStart;
import de.tarent.aa.veraweb.db.dao.EventDao;
import de.tarent.aa.veraweb.db.dao.PersonDao;
import de.tarent.aa.veraweb.db.dao.TaskDao;

public class DatabaseInitializer implements HandlerStart, HandlerAfter {

    public static String SQL_MIGRATION_PREFIX__SCHEMA = "V";
    public static String SQL_MIGRATION_PREFIX__TESTDATA = "T";

    @Autowired
    private Flyway flyway;

    @Autowired
    private TaskDao taskDao;

    @Autowired
    private EventDao eventDao;

    @Autowired
    private PersonDao personDao;

    public DatabaseInitializer() {
        // register me
        StartAndShutdownHook.addOnStartHandler(this);
        StartAndShutdownHook.addOnAfterHandler(this);
    }

    /**
     * {@inheritDoc} <br/>
     * Perform Flyway databse migration.
     */
    @Override
    public void handleStart() {
        System.out.println("Flyway database migration...");

        // clean database
        long timeStart = System.currentTimeMillis();
        flyway.clean();
        long timeEnd = System.currentTimeMillis();
        System.out.println("Cleaned database schema '" + flyway.getSchemas()[0] + "' (execution time "
                + Utils.formatTime(timeEnd - timeStart) + ")");

        // migrate database
        flyway.setSqlMigrationPrefix(SQL_MIGRATION_PREFIX__SCHEMA);
        flyway.init();
        flyway.migrate();
        flyway.setSqlMigrationPrefix(SQL_MIGRATION_PREFIX__TESTDATA);
        int countMigrations = flyway.migrate();

        // output
        MigrationInfoService infos = flyway.info();
        for (MigrationInfo info : infos.applied()) {
            if (info.getType() == MigrationType.INIT) {
                continue;
            }
            StringBuffer sb = new StringBuffer();
            sb.append("Migrated to version ").append(info.getVersion()).append(": ").append(info.getScript());
            sb.append(" (execution time ").append(Utils.formatTime(info.getExecutionTime())).append(")");
            System.out.println(sb.toString());
        }
        System.out.println("Successfully applied " + countMigrations + " migrations.");
    }

    /**
     * {@inheritDoc} <br/>
     * Reset test data in database.
     */
    @Override
    public void handleAfterScenario(ScenarioResult result) {
        try {
            cleanUpDB();
        } catch (Exception e) {
            System.err.println("Error occurred while cleaning up database: " + e.getMessage());
        }
    }

    /**
     * Helper method to clean up database by removing all test data.
     *
     * @throws Exception
     *             exception
     */
    private void cleanUpDB() throws Exception {
        taskDao.deleteAll();
        eventDao.deleteAll();
        personDao.deleteAll();
    }
}
