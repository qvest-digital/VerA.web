package de.tarent.octopus.cronjobs;

/*-
 * VerA.web-Middleware, newly MIT licenced, is comprised of:
 * tarent-commons, a set of common components and solutions
 * tarent-contact, platform-independent webservice-based contact management
 * tarent-database, jdbc database library
 * tarent-doctor, Document Generation Platform
 * tarent-octopus, Webservice Data Integrator and Application Server
 *  © 2005, 2006, 2007 asteban (s.mancke@tarent.de)
 *  © 2007 David Goemans (d.goemans@tarent.de)
 *  © 2006, 2007, 2010 Hendrik Helwich (h.helwich@tarent.de)
 *  © 2005, 2006, 2007 Christoph Jerolimov (c.jerolimov@tarent.de)
 *  © 2006 Philipp Kirchner (p.kirchner@tarent.de)
 *  © 2010 Carsten Klein (c.klein@tarent.de)
 *  © 2006 Michael Kleinhenz (m.kleinhenz@tarent.de)
 *  © 2006 Michael Klink (m.klink@tarent.de)
 *  © 2007 Fabian Köster (f.koester@tarent.de)
 *  © 2006 Martin Ley (m.ley@tarent.de)
 *  © 2007 Alex Maier (a.maier@tarent.de)
 *  © 2007, 2015, 2017, 2018 mirabilos (t.glaser@tarent.de)
 *  © 2006, 2007 Jens Neumaier (j.neumaier@tarent.de)
 *  © 2006 Nils Neumaier (n.neumaier@tarent.de)
 *  © 2007, 2008 Martin Pelzer (m.pelzer@tarent.de)
 *  © 2008, 2009 Christian Preilowski (c.thiel@tarent.de)
 *  © 2006, 2008, 2009 Thomas Schmitz (t.schmitz@tarent.de)
 *  © 2007 Robert Schuster (r.schuster@tarent.de)
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.tarent.octopus.cronjobs.CronJob;
import de.tarent.octopus.server.OctopusContext;

/**
 * This implements a Unix(tm) style cron job system. To submit a job, subclass
 * either ExactCronJob or IntervalCronJob and add them to the queue using
 * addJob(). Then start the cron system by running start() on the Cron instance.
 *
 * @author Michael Kleinhenz (m.kleinhenz@tarent.de)
 * @author Nils Neumaier (n.neumaier@tarent.de)
 */
public class Cron implements Runnable {
    public static final int EXACT_CRONJOB = 1;
    public static final int INTERVAL_CRONJOB = 2;

    public static final String CRONJOBMAP_KEY_NAME = "name";
    public static final String CRONJOBMAP_KEY_TYPE = "type";
    public static final String CRONJOBMAP_KEY_PROCEDURE = "procedure";
    public static final String CRONJOBMAP_KEY_ERRORPROCEDURE = "errorprocedure";
    public static final String CRONJOBMAP_KEY_PROPERTIES = "properties";
    public static final String CRONJOBMAP_KEY_STATUS = "status";
    public static final String CRONJOBMAP_KEY_ERROR = "error";
    public static final String CRONJOBMAP_KEY_LASTRUN = "lastrun";
    public static final Object CRONJOBMAP_KEY_ACTIVE = "active";

    private int CHECK_INTERVAL = 30000;
    private long TIMEBASE = 60000;

    private boolean stopped = false;
    private Map jobs = null;

    private OctopusContext octopusContext;
    private File savePath;

    private static Logger logger = Logger.getLogger(Cron.class.getName());
    private static long cronExportCount = 0L;

    /**
     * Standard constructor. Creates a new instance of the cron system.
     */

    public Cron(OctopusContext octopusContext, File savePath) {
        this.octopusContext = octopusContext;
        this.savePath = savePath;
        this.jobs = new HashMap();
        restoreBackup();
    }

    public Cron(OctopusContext octopusContext, File savePath, int timeBase) {
        this(octopusContext, savePath);
        this.TIMEBASE = timeBase;
        this.CHECK_INTERVAL = timeBase / 2;
    }

    /**
     * Stops the cron system. The system will not stop immediately, but when
     * reaching the next check time.
     */
    public void deactivateCron() {
        this.stopped = true;
    }

    /**
     * Activates the cron system. The system will start immediately if it isnt already running.
     * If it is already running (State != RUNNABLE) it will proceed.
     */

    public void activateCron() {
        stopped = false;
    }

    /**
     * Adds a job to the list of jobs.
     *
     * @param job Job to be added.
     */
    public boolean addJob(CronJob job) {
        // If a cronjob with the same name already exists, we will wait, until this job is finished
        // before we replace it with the new cronjob
        if (job == null) {
            return false;
        }

        //        if (jobs.containsKey(job.getName())){
        //            CronJob oldJob = (CronJob)jobs.get(job.getName());
        //            while(!oldJob.runnable()){
        //               try {
        //                Thread.sleep(CHECK_INTERVAL);
        //                } catch (InterruptedException e) {
        //                    // e.printStackTrace();
        //                }
        //            }
        //        }

        jobs.put(job.getName(), job);
        logger.log(Level.FINEST, "New Cronjob added to queue: " + job.getName());

        return true;
    }

    /**
     * returns a CronJob Object corresponding to the given cronjobmap
     *
     * @param cronJobMap: specifies the wanted cronjob
     * @return cronJob: the corresponding cronjob or null if there is no
     */
    public CronJob getCronJobByCronJobMap(Map cronJobMap) {

        if (cronJobMap == null) {
            return null;
        }

        CronJob tmpJob = getCronJobByName(cronJobMap.get(Cron.CRONJOBMAP_KEY_NAME).toString());

        // If there is a cronjob, at least the parameters type, procedure and name have to be correct
        if (tmpJob.getProcedure().equals(cronJobMap.get(Cron.CRONJOBMAP_KEY_PROCEDURE))
          && tmpJob.getName().equals(cronJobMap.get(Cron.CRONJOBMAP_KEY_NAME))
          && new Integer(tmpJob.getType()).equals(cronJobMap.get(Cron.CRONJOBMAP_KEY_TYPE))) {
            return tmpJob;
        }
        return null;
    }

    /**
     * Removes a job from the list of jobs.
     * returns true if job could be delted or false if not
     *
     * @param jobMap: cronjobmap that specifies the job
     */
    public boolean removeJob(Map jobMap) {
        // First check if there is a cronjob with the correct name
        if (jobMap != null && getCronJobByCronJobMap(jobMap) != null) {
            jobs.remove(jobMap.get(Cron.CRONJOBMAP_KEY_NAME));
            return true;
        }
        return false;
    }

    /**
     * Will be called from the thread subsystem to start the cron system.
     */
    public void run() {
        while (!stopped) {

            try {
                Thread.sleep(CHECK_INTERVAL);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Thread storeThread = new Thread(new CronExporter());
            storeThread.setName("Cron Export Thread #" + (cronExportCount++));
            storeThread.start();
            logger.log(Level.FINEST, "Cron is storing Backup to " + savePath.getAbsolutePath());

            logger.log(Level.FINEST, "Cron is checking for Jobs to Start. " + new Date());

            List clonedJobs = new ArrayList(jobs.values());
            Iterator iter = clonedJobs.iterator();
            while (iter.hasNext()) {
                CronJob thisJob = (CronJob) iter.next();

                // Check if job can be started
                if (!thisJob.runnable()) {
                    continue;
                }

                switch (thisJob.getType()) {
                case EXACT_CRONJOB:
                    runExactCronJob(thisJob);
                    break;
                case INTERVAL_CRONJOB:
                    runIntervalCronJob(thisJob);
                    break;
                }
            }
        }
    }

    private void runIntervalCronJob(CronJob job) {
        IntervalCronJob thisJob = (IntervalCronJob) job;

        // Check time constraints
        int intervalMinutes = thisJob.getIntervalMinutes();
        if (intervalMinutes < 1) {
            return;
        }

        Date lastRun = thisJob.getLastRun();
        Date currentDate = new Date();

        if (lastRun == null || (currentDate.getTime() - lastRun.getTime()) > intervalMinutes * TIMEBASE) {
            thisJob.setLastRun(currentDate);
            logger.log(Level.INFO, "Cron starts Interval CronJob " + thisJob.getName() + " at " + currentDate);
            thisJob.start();
        }
    }

    private void runExactCronJob(CronJob job) {
        logger.log(Level.FINEST, "Cron checks Exact CronJob " + job.getName() + " at " + new Date());

        ExactCronJob thisJob = (ExactCronJob) job;

        // Check if this job was already run in this minute
        Date lastRun = thisJob.getLastRun();
        Date currentRunDate = new Date();
        if (lastRun != null && currentRunDate.getTime() - lastRun.getTime() < TIMEBASE) {
            return;
        }

        // Check time constraints
        Calendar currentDate = new GregorianCalendar();
        boolean run = true;
        run = run && (thisJob.getHour() == -1 || (thisJob.getHour() == currentDate.get(Calendar.HOUR_OF_DAY)));
        run = run && (thisJob.getMinute() == -1 || (thisJob.getMinute() == currentDate.get(Calendar.MINUTE)));
        run = run && (thisJob.getMonth() == -1 || (thisJob.getMonth() == currentDate.get(Calendar.MONTH)));
        run = run && (thisJob.getDayOfMonth() == -1 || (thisJob.getDayOfMonth() == currentDate.get(Calendar.DAY_OF_MONTH)));
        run = run && (thisJob.getDayOfWeek() == -1 || (thisJob.getDayOfWeek() == currentDate.get(Calendar.DAY_OF_WEEK)));

        // Run it..
        if (run && job.runnable()) {
            logger.log(Level.INFO, "Cron starts Exact CronJob " + job.getName() + " at " + new Date());

            job.setLastRun(currentRunDate);
            job.start();
        }
    }

    /**
     * returns the cronjobp corresponding to the given name
     * returns null if there is no corresponding job in queue
     *
     * @param name
     * @return cronjob
     */

    public CronJob getCronJobByName(String name) {
        return (CronJob) jobs.get(name);
    }

    /**
     * returns the cronjobmap corresponding to the given name
     * returns null if there is no corresponding job in queue
     *
     * @param name
     * @return Map: cronjobmap of the cronjob
     */
    public Map getCronJobMapByName(String name) {
        CronJob cronjob = (CronJob) jobs.get(name);
        if (cronjob != null) {
            return cronjob.getCronJobMap();
        }
        return null;
    }

    /**
     * starts a cronjob immediately if it is runnable
     *
     * @param job
     */
    public boolean forceRun(CronJob job) {
        return forceRun(job, false);
    }

    /**
     * starts a cronjob immediately if it is runnable
     *
     * @param job
     */
    public boolean forceRun(CronJob job, boolean ignoreDeactived) {

        if (job.runnable(ignoreDeactived)) {
            job.setLastRun(new Date());
            job.start();
            return true;
        }
        return false;
    }

    /**
     * returns a Map with names (key) and cronjobmaps (value) of all cronjobs
     *
     * @return Map: Map with cronjobmaps
     */
    public Map getCronJobMaps() {

        Map cronJobMaps = new HashMap();
        for (Iterator iter = jobs.values().iterator(); iter.hasNext(); ) {
            CronJob job = (CronJob) iter.next();
            cronJobMaps.put(job.getName(), job.getCronJobMap());
        }
        return cronJobMaps;
    }

    /**
     * activates a cronjob
     * cronjob will be possibly runnable next time its started
     *
     * @param cronJobMap: specifies the cronjob
     * @return activated: flag to show if activation was succesful
     */

    public boolean activateCronJob(Map cronJobMap) {
        CronJob job = getCronJobByCronJobMap(cronJobMap);

        if (job != null) {
            job.activate();
            return true;
        }
        return false;
    }

    /**
     * deactivates a cronjob
     * cronjob wont stop immediately but wont be runnable until it is activated again.
     *
     * @param cronJobMap: specifies the cronjob
     * @return deactivated: flag to show if deactivation was succesful
     */
    public boolean deactivateCronJob(Map cronJobMap) {
        CronJob job = getCronJobByCronJobMap(cronJobMap);

        if (job != null) {
            job.deactivate();
            return true;
        }
        return false;
    }

    /**
     * This method gets a cronjobmap and/or a name of a cronjob.
     * If both parameters are set, the algorithms checks if the name
     * matches the cronjobmap, returns the map if they fit and null if not.
     * If only a name is given, the corresponding job is found and its map returned.
     * If only a map is given, it will be returned instantly.
     *
     * @param cronJobMap:  Map that specifies a cronjob
     * @param cronJobName: Name of the cronjob
     */
    public Map mergeCronJobMapAndName(Map cronJobMap, String cronJobName) {

        // cronjobmap is set but name is null or empty
        if (cronJobMap != null && (cronJobName == null || cronJobName.equals(""))) {
            return cronJobMap;
        }

        // name is set and cronjobmap is null
        if (cronJobName != null && !cronJobName.equals("") && cronJobMap == null && getCronJobByName(cronJobName) != null) {
            return getCronJobByName(cronJobName).getCronJobMap();
        }

        if (cronJobMap != null && cronJobName != null && cronJobMap.get(Cron.CRONJOBMAP_KEY_NAME).equals(cronJobName)) {
            return cronJobMap;
        }

        return null;
    }

    public void setOctopusContext(OctopusContext octopusContext) {
        this.octopusContext = octopusContext;
    }

    public OctopusContext getOctopusContext() {
        return octopusContext;
    }

    /**
     * This routine stores the actual list of cronjobs persistent on harddrive
     */

    class CronExporter implements Runnable {

        public CronExporter() {
        }

        public void run() {
            try {
                String moduleRootPath = savePath.getAbsolutePath();
                FileOutputStream fileOut =
                  new FileOutputStream(moduleRootPath + System.getProperty("file.separator") + "cronJobs.backup");
                ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);

                objectOut.writeObject(getCronJobMaps());
                objectOut.flush();
                objectOut.close();
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private void restoreBackup() {

        Map result = null;
        String moduleRootPath = savePath.getAbsolutePath();
        File backupFile = new File(moduleRootPath + System.getProperty("file.separator") + "cronJobs.backup");
        logger.log(Level.INFO, "Restoring Backup from " + backupFile.getAbsolutePath());

        if (backupFile.exists()) {
            try {
                //long fileSize = backupFile.length();
                FileInputStream fileIn = new FileInputStream(backupFile);
                ObjectInputStream objectInput = new ObjectInputStream(fileIn);

                result = (Map) objectInput.readObject();

                fileIn.close();
                objectInput.close();

                for (Iterator iter = result.values().iterator(); iter.hasNext(); ) {

                    Map tmpCronJobMap = (Map) iter.next();
                    CronJob tmpJob = null;
                    try {
                        tmpJob = createCronJobFromCronJobMap(tmpCronJobMap);
                    } catch (Exception e) {
                        logger.log(Level.WARNING, "An error occured trying to restore an old cron backup.\n" + e.getMessage());
                        e.printStackTrace();
                    }

                    if (tmpJob != null) {
                        logger.log(Level.INFO,
                          "Restoring Cronjob \"" + tmpJob.getName() + "\": (type = " + tmpJob.getType() + ", procedure = " +
                            tmpJob.getProcedure() + ")");
                        addJob(tmpJob);
                    }
                }

                if (getCronJobMaps().size() > 0) {
                    logger.log(Level.INFO, "Cronjobs restored from backup: " + getCronJobMaps());
                }
            } catch (FileNotFoundException e) {
                logger.log(Level.SEVERE, "No backup file found to restore old cron.\n" + e.getMessage());
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Error trying to restore backup of old cron.\n" + e.getMessage());
            } catch (ClassNotFoundException e) {
                logger.log(Level.SEVERE, "Error trying to restore backup of old cron.\n" + e.getMessage());
            }
        }
    }

    public CronJob createCronJobFromCronJobMap(Map cronJobMap) throws Exception {

        String exceptionErrorMessage = "";

        cronJobMap = correctCronJobMap(cronJobMap);

        String name = (String) cronJobMap.get(Cron.CRONJOBMAP_KEY_NAME);
        Integer type = new Integer(Integer.parseInt(cronJobMap.get(Cron.CRONJOBMAP_KEY_TYPE).toString()));
        String procedure = (String) cronJobMap.get(Cron.CRONJOBMAP_KEY_PROCEDURE);
        String errorProcedure = (String) cronJobMap.get(Cron.CRONJOBMAP_KEY_ERRORPROCEDURE);
        Map properties = (Map) cronJobMap.get(Cron.CRONJOBMAP_KEY_PROPERTIES);
        String errorMessage = (String) cronJobMap.get(Cron.CRONJOBMAP_KEY_ERROR);
        Boolean active = (Boolean) cronJobMap.get(Cron.CRONJOBMAP_KEY_ACTIVE);
        String lastRun = (String) cronJobMap.get(Cron.CRONJOBMAP_KEY_LASTRUN);

        // Some Entries must be set or we will return null
        if (name == null || name.equals("") || procedure == null || procedure.equals("") || properties == null) {
            exceptionErrorMessage +=
              "Error in Task setCronJob. One of the following Map entries has not been set or could not be used: " +
                "name: " + name + ", procedure: " + procedure + ", properties: " + properties + ", type: " + type +
                ". ";
            throw new Exception(exceptionErrorMessage);
        }

        Integer alreadyRunning = (Integer) properties.get(CronJob.PROPERTIESMAP_KEY_ALREADYRUNNING);

        CronJob cronJob = null;

        // If cronjob is of type exact cronjob
        if (type.intValue() == Cron.EXACT_CRONJOB) {

            int hour = -1;
            int minute = -1;
            int month = -1;
            int dayOfMonth = -1;
            int dayOfWeek = -1;

            // search in properties map for entries specific for an exact cronjob
            //  and store them in variables
            for (Iterator iter = properties.entrySet().iterator(); iter.hasNext(); ) {

                Entry e = (Entry) iter.next();
                String key = e.getKey().toString();

                if (key.equals(ExactCronJob.PROPERTIESMAP_KEY_HOUR)) {
                    hour = ((Integer) e.getValue()).intValue();
                } else if (key.equals(ExactCronJob.PROPERTIESMAP_KEY_MINUTE)) {
                    minute = ((Integer) e.getValue()).intValue();
                } else if (key.equals(ExactCronJob.PROPERTIESMAP_KEY_MONTH)) {
                    month = ((Integer) e.getValue()).intValue();
                } else if (key.toLowerCase().equals(ExactCronJob.PROPERTIESMAP_KEY_DAYOFMONTH)) {
                    dayOfMonth = ((Integer) e.getValue()).intValue();
                } else if (key.toLowerCase().equals(ExactCronJob.PROPERTIESMAP_KEY_DAYOFWEEK)) {
                    dayOfWeek = ((Integer) e.getValue()).intValue();
                }
            }

            // At least one parameter has to be set (!= -1)
            if (hour != -1 || minute != -1 || month != -1 || dayOfMonth != -1 || dayOfWeek != -1) {
                cronJob = new ExactCronJob(this, hour, minute, month, dayOfMonth, dayOfWeek);
            }
        }

        // If cronjob is of type interval cronjob
        else if (type.intValue() == Cron.INTERVAL_CRONJOB) {

            // Get interval value from properties map, store it and delete it in the map
            // so that later routines that iterate on the properties dont have to handle it

            Integer interval = (Integer) properties.get(IntervalCronJob.PROPERTIESMAP_KEY_INTERVAL);
            int intervalTime = interval.intValue();

            // intervalTime has to be set and must be greater than zero
            if (intervalTime > 0) {
                cronJob = new IntervalCronJob(this, intervalTime);
            } else {
                logger.log(Level.WARNING,
                  "Error trying to create an IntervalCronJob. Entry '" + IntervalCronJob.PROPERTIESMAP_KEY_INTERVAL +
                    "' in properties map has not been set or is lower than one.");
            }
        } else if (type.intValue() != Cron.INTERVAL_CRONJOB && type.intValue() != Cron.EXACT_CRONJOB) {
            exceptionErrorMessage += "Unknown cronjob type: " + type;
            throw new Exception(exceptionErrorMessage);
        }

        // If a cronjob has been created using the type-specific parameters we have to set the general parameters
        if (cronJob != null) {
            cronJob.setName(name);
            cronJob.setProcedure(procedure);
            if (errorProcedure != null) {
                cronJob.setErrorProcedure(errorProcedure);
            } else {
                cronJob.setErrorProcedure(loadStandardErrorProcedure());
            }
            if (errorMessage != null && errorMessage.length() > 0) {
                cronJob.setErrorMessage(errorMessage);
            }
            cronJob.setProperties(properties);
            if (alreadyRunning != null) {
                cronJob.setAlreadyRunning(alreadyRunning.intValue());
            }
            if (active != null) {
                cronJob.setActive(active.booleanValue());
            } else {
                cronJob.setActive(false);
            }
            if (lastRun != null && lastRun.length() > 0) {

                try {
                    // Thu Oct 19 14:07:49 CEST 2006
                    SimpleDateFormat formatter = new SimpleDateFormat("dd'.' MMMM yyyy',' HH:mm:ss");
                    Date date = formatter.parse(lastRun);
                    if (date != null) {
                        cronJob.setLastRun(date);
                    }
                } catch (Exception e) {
                    logger.log(Level.WARNING,
                      "Error trying to parse date " + lastRun + ". Lastrun could not be set while saving cronjob.");
                }
            }
        }

        return cronJob;
    }

    private String loadStandardErrorProcedure() {
        // TODO Auto-generated method stub
        return null;
    }

    private Map correctCronJobMap(Map input) {

        Map resultMap = input;
        Map properties = input.get(Cron.CRONJOBMAP_KEY_PROPERTIES) != null ? (Map) input.get(Cron.CRONJOBMAP_KEY_PROPERTIES) :
          new HashMap();
        Map propertiesToAdd = new HashMap();

        for (Iterator iter = input.entrySet().iterator(); iter.hasNext(); ) {
            Entry entry = (Entry) iter.next();
            Object key = entry.getKey();

            // if there is an entry in the cronjobmap, that doesnt belong there
            // we remove it from the cronjobmap and add it to the internal properties map
            if (!key.equals(Cron.CRONJOBMAP_KEY_NAME) && !key.equals(Cron.CRONJOBMAP_KEY_PROPERTIES)
              && !key.equals(Cron.CRONJOBMAP_KEY_TYPE) && !key.equals(Cron.CRONJOBMAP_KEY_STATUS)
              && !key.equals(Cron.CRONJOBMAP_KEY_PROCEDURE) && !key.equals(Cron.CRONJOBMAP_KEY_ERRORPROCEDURE)
              && !key.equals(Cron.CRONJOBMAP_KEY_LASTRUN) && !key.equals(Cron.CRONJOBMAP_KEY_ERROR) &&
              !key.equals(Cron.CRONJOBMAP_KEY_ACTIVE)) {

                propertiesToAdd.put(entry.getKey(), entry.getValue());
                resultMap.remove(key);
            }
        }

        properties.putAll(propertiesToAdd);

        resultMap.put(Cron.CRONJOBMAP_KEY_PROPERTIES, properties);
        return resultMap;
    }

    public boolean isActivated() {
        return !stopped;
    }
}
