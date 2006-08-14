/*
 * Copyright (c) tarent GmbH
 * Bahnhofstrasse 13 . 53123 Bonn
 * www.tarent.de . info@tarent.de
 *
 * Created on 28.02.2006
 */

package de.tarent.octopus.cronjobs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.tarent.octopus.cronjobs.CronJob;
import de.tarent.octopus.cronjobs.worker.CronJobWorker;

/**
 * This implements a Unix(tm) style cron job system. To submit a job, subclass
 * either ExactCronJob or IntervalCronJob and add them to the queue using
 * addJob(). Then start the cron system by running start() on the Cron instance.
 *
 * @author Michael Kleinhenz (m.kleinhenz@tarent.de)
 * @author Nils Neumaier (n.neumaier@tarent.de)
 */
public class Cron extends Thread
{
    public static final int EXACT_CRONJOB = 1;
    public static final int INTERVAL_CRONJOB = 2;    
    
    public static final String CRONJOBMAP_KEY_NAME              = "name";
    public static final String CRONJOBMAP_KEY_TYPE              = "type";
    public static final String CRONJOBMAP_KEY_PROCEDURE         = "procedure";
    public static final String CRONJOBMAP_KEY_ERRORPROCEDURE    = "errorprocedure";
    public static final String CRONJOBMAP_KEY_PROPERTIES        = "properties";
    public static final String CRONJOBMAP_KEY_STATUS            = "status";
    public static final String CRONJOBMAP_KEY_ERROR             = "error";
    public static final String CRONJOBMAP_KEY_LASTRUN           = "lastrun";
    
    private int CHECK_INTERVAL = 30000;
    private int TIMEBASE = 60000;
    
    private boolean stopped = false;
    private Map jobs = null;
    
    private CronExporter cronExporter;
    
    private static Logger logger = Logger.getLogger(Cron.class.getName());

    
    /**
     * Standard constructor. Creates a new instance of the cron system.
     */
    
    public Cron()
    {
        this.jobs = new HashMap();
        this.start();
    }

    public Cron(int timeBase)
    {
    	this();
    	this.TIMEBASE = timeBase;
    	this.CHECK_INTERVAL = timeBase / 2;
    }
   
    
    /**
     * Stops the cron system. The system will not stop immediately, but when
     * reaching the next check time. 
     */
    public void deactivateCron()
    {
        this.stopped = true;
    }
    
    /**
     * Activates the cron system. The system will start immediately if it isnt already running.
     * If it is already running (State != RUNNABLE) it will proceed.
     */
     
    public void activateCron()
    {
        stopped = false;
        State state = this.getState();
        if (state.equals(State.NEW) || state.equals(State.RUNNABLE))
                this.start();
    }
    
    /**
     * Adds a job to the list of jobs.
     * 
     * @param job Job to be added.
     */
    public boolean addJob(CronJob job)
    {
        // If a cronjob with the same name already exists, we will wait, until this job is finished 
        // before we replace it with the new cronjob
        if (job == null)            
            return false;
        
        if (jobs.containsKey(job.getName())){
            CronJob oldJob = (CronJob)jobs.get(job.getName());
            while(!oldJob.runnable()){
               try {
                Thread.sleep(CHECK_INTERVAL);
                } catch (InterruptedException e) {
                    // e.printStackTrace();
                }
            }   
        }
        
        jobs.put(job.getName(), job);
        logger.log(Level.FINEST, "New Cronjob added to queue: " + job.getName());
        
        return true;
    }
    
    /**
     * returns a CronJob Object corresponding to the given cronjobmap
     * @param cronJobMap: specifies the wanted cronjob
     * @return cronJob: the corresponding cronjob or null if there is no 
     */
    public CronJob getCronJobByCronJobMap(Map cronJobMap){
        
        if (cronJobMap == null)
            return null;
        
        CronJob tmpJob = getCronJobByName(cronJobMap.get(Cron.CRONJOBMAP_KEY_NAME).toString());
        
        // If there is a cronjob, at least the parameters type, procedure and name have to be correct
        if (tmpJob.getProcedure().equals(cronJobMap.get(Cron.CRONJOBMAP_KEY_PROCEDURE))
                && tmpJob.getName().equals(cronJobMap.get(Cron.CRONJOBMAP_KEY_NAME))
                && new Integer(tmpJob.getType()).equals(cronJobMap.get(Cron.CRONJOBMAP_KEY_TYPE))){
            return tmpJob;
        }
        return null;        
    }
    
    
    /**
     * Removes a job from the list of jobs.
     * returns true if job could be delted or false if not
     * @param jobMap: cronjobmap that specifies the job
     * 
     */
    public boolean removeJob(Map jobMap)
    {
        // First check if there is a cronjob with the correct name
        if (jobMap != null && getCronJobByCronJobMap(jobMap) != null){
                jobs.remove(jobMap.get(Cron.CRONJOBMAP_KEY_NAME));
                return true;    
        }
        return false;
    }
    
    /**
     * Will be called from the thread subsystem to start the cron system.
     */
    public void run()
    {
        while (!stopped)
        {
            
            try
            {
                Thread.sleep(CHECK_INTERVAL);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            
            logger.log(Level.FINEST, "Cron is checking for Jobs to Start. " + new Date() );
            
            Thread storeThread = new Thread(new CronExporter()); 
            storeThread.start();
            
            List clonedJobs = new ArrayList(jobs.values());
            Iterator iter = clonedJobs.iterator();
            while (iter.hasNext())
            {
                CronJob thisJob = (CronJob)iter.next();

                // Check if job can be started
                if (!thisJob.runnable())
                {
                    continue;
                }
                
                                
                switch (thisJob.getType())
                {
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

    private void runIntervalCronJob(CronJob job)
    {
        IntervalCronJob thisJob = (IntervalCronJob)job;

        // Check time constraints
        int intervalMinutes = thisJob.getIntervalMinutes();
        if (intervalMinutes<1)
            return;
        

        Date lastRun = thisJob.getLastRun();
        Date currentDate = new Date();

        if (lastRun == null || (currentDate.getTime() - lastRun.getTime()) > intervalMinutes * TIMEBASE)
        {
            thisJob.setLastRun(currentDate);
            logger.log(Level.FINEST, "Cron starts Interval CronJob " + thisJob.getName() + " at " + currentDate );
            thisJob.start();
        }
    }

    private void runExactCronJob(CronJob job)
    {
        logger.log(Level.FINEST, "Cron checks Exact CronJob " + job.getName() + " at " + new Date() );
        
        ExactCronJob thisJob = (ExactCronJob)job;
        
       
        // Check if this job was already run in this minute
        Date lastRun = thisJob.getLastRun();
        Date currentRunDate = new Date();
        if (lastRun != null && currentRunDate.getTime() - lastRun.getTime() < TIMEBASE)
        {	
            return;
        }
       
        // Check time constraints
        Calendar currentDate = new GregorianCalendar();
        boolean run = true;
        run = run && (thisJob.getHour()==-1 || (thisJob.getHour()==currentDate.get(Calendar.HOUR_OF_DAY)));
        run = run && (thisJob.getMinute()==-1 || (thisJob.getMinute()==currentDate.get(Calendar.MINUTE)));
        run = run && (thisJob.getMonth()==-1 || (thisJob.getMonth()==currentDate.get(Calendar.MONTH)));
        run = run && (thisJob.getDayOfMonth()==-1 || (thisJob.getDayOfMonth()==currentDate.get(Calendar.DAY_OF_MONTH)));
        run = run && (thisJob.getDayOfWeek()==-1 || (thisJob.getDayOfWeek()==currentDate.get(Calendar.DAY_OF_WEEK)));
     

        // Run it..
        if (run && job.runnable())
        {
            logger.log(Level.FINE, "Cron starts Exact CronJob " + job.getName() + " at " + new Date() );
            
            job.setLastRun(currentRunDate);
            job.start();
        }
    }
    
    /**
     * returns the cronjobp corresponding to the given name
     * returns null if there is no corresponding job in queue 
     * @param name
     * @return cronjob
     */
    
    public CronJob getCronJobByName(String name){
        return (CronJob)jobs.get(name);
    }
    
    /**
     * returns the cronjobmap corresponding to the given name
     * returns null if there is no corresponding job in queue 
     * @param name
     * @return Map: cronjobmap of the cronjob
     */
    public Map getCronJobMapByName(String name){
        CronJob cronjob = (CronJob)jobs.get(name);
        if (cronjob != null)
            return cronjob.getCronJobMap();
        return null;
    }
    
    /**
     * starts a cronjob immediately if it is runnable
     * @param job
     * @return
     */
    public boolean forceRun(CronJob job){
        
        if (job.runnable()){
            job.start();
            return true;
        }
        return false;
    }
    
    /**
     * returns a Map with names (key) and cronjobmaps (value) of all cronjobs
     * @return Map: Map with cronjobmaps
     */
    public Map getCronJobMaps(){
        
        Map cronJobMaps = new HashMap();
        for (Iterator iter = jobs.values().iterator(); iter.hasNext();){
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
    
    public boolean activateCronJob(Map cronJobMap){
        CronJob job = getCronJobByCronJobMap(cronJobMap);
        
        if (job != null){
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
    public boolean deactivateCronJob(Map cronJobMap){
        CronJob job = getCronJobByCronJobMap(cronJobMap);
        
        if (job != null){
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
     * @param cronJobMap: Map that specifies a cronjob
     * @param cronJobName: Name of the cronjob
     * @return
     */
    public Map mergeCronJobMapAndName(Map cronJobMap, String cronJobName){
        
        // cronjobmap is set but name is null or empty
        if (cronJobMap != null && (cronJobName == null || cronJobName.equals("")))
            return cronJobMap;
        
        // name is set and cronjobmap is null
        if (cronJobName != null && !cronJobName.equals("") && cronJobMap == null && getCronJobByName(cronJobName) != null )
            return getCronJobByName(cronJobName).getCronJobMap();
        
        if (cronJobMap != null && cronJobName != null && cronJobMap.get(Cron.CRONJOBMAP_KEY_NAME).equals(cronJobName))
            return cronJobMap;
        
        return null;
    }
    
    
    /**
     * This routine stores the actual list of cronjobs persistent on harddrive
     *
     */
    
   class CronExporter implements Runnable{
       
        public CronExporter(){            
        }
        
        public void run(){
            try {
                
                FileOutputStream fileOut = new FileOutputStream(new File(System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + "cronJobBackup"));
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
}
