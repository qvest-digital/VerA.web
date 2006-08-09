package de.tarent.octopus.cronjobs.worker;

import de.tarent.octopus.content.TcAll;
import de.tarent.octopus.content.TcReflectedWorker;
import de.tarent.octopus.cronjobs.Cron;
import de.tarent.octopus.cronjobs.CronJob;
import de.tarent.octopus.cronjobs.ExactCronJob;
import de.tarent.octopus.cronjobs.IntervalCronJob;
import de.tarent.octopus.server.OctopusContext;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class CronJobWorker extends TcReflectedWorker{
    
    private static Cron cronjobQueue = new Cron(); 
    
    /**
     * this method creates a new cronjob. If a cronjob with the same name already exists, 
     * the old one isreplaced by the new cronjob.
     * 
     * @param name : can be set to get the name of a single cronjob and check if it exists
     * @param type: can be set to get names of just one type of cronjobs
     * @param sort: optional flag to sort the returned list alphabetically 
     * @return cronjob: cronjob-map of edited or created cronjob 
     */
   
    final static public String[] INPUT_setCronJob = {"cronjobmap"};
    final static public boolean[] MANDATORY_setCronJob = {true};
    final static public String OUTPUT_setCronJob = "cronjob";
    
    static public Map setCronJob(OctopusContext all, Map cronJobMap) {

        String name             = (String) cronJobMap.get(Cron.CRONJOBMAP_KEY_NAME);
        Integer type            = (Integer)cronJobMap.get(Cron.CRONJOBMAP_KEY_TYPE);
        String procedure        = (String)cronJobMap.get(Cron.CRONJOBMAP_KEY_PROCEDURE);
        String errorProcedure   = (String)cronJobMap.get(Cron.CRONJOBMAP_KEY_ERRORPROCEDURE);
        
        Map properties          = (Map)cronJobMap.get(Cron.CRONJOBMAP_KEY_PROPERTIES);
        
        Integer alreadyRunning  = (Integer) properties.get(CronJob.PROPERTIESMAP_KEY_ALREADYRUNNING);
        // Delete entry from properties after extraction
        if (alreadyRunning != null)
            properties.remove(CronJob.PROPERTIESMAP_KEY_ALREADYRUNNING);
        
        CronJob cronJob = null;
        
        // If cronjob is of type exact cronjob
        if (type.intValue() == Cron.EXACT_CRONJOB){
            
            int hour        = -1;
            int minute      = -1;
            int month       = -1;
            int dayOfMonth  = -1;
            int dayOfWeek   = -1;
            
            // search in properties map for entries specific for an exact cronjob
            // store them in variables and delete them from the map so that later 
            // routines that iterate on the properties dont have to handle them
            for (Iterator iter = properties.entrySet().iterator(); iter.hasNext();){

                Entry e = (Entry)iter.next();
                String key = e.getKey().toString();
                
               if (key.equals(ExactCronJob.PROPERTIESMAP_KEY_HOUR)){
                    hour = ((Integer)e.getValue()).intValue();
                    properties.remove(ExactCronJob.PROPERTIESMAP_KEY_HOUR);
               }
                
               if (key.equals(ExactCronJob.PROPERTIESMAP_KEY_MINUTE)){
                   minute = ((Integer)e.getValue()).intValue();
                   properties.remove(ExactCronJob.PROPERTIESMAP_KEY_MINUTE);
               }
                
               if (key.equals(ExactCronJob.PROPERTIESMAP_KEY_MONTH)){
                   month = ((Integer)e.getValue()).intValue();
                   properties.remove(ExactCronJob.PROPERTIESMAP_KEY_MONTH);
               }
                                    
               if (key.toLowerCase().equals(ExactCronJob.PROPERTIESMAP_KEY_DAYOFMONTH)){
                   dayOfMonth = ((Integer)e.getValue()).intValue();
                   properties.remove(ExactCronJob.PROPERTIESMAP_KEY_DAYOFMONTH);
               }
                
               if (key.toLowerCase().equals(ExactCronJob.PROPERTIESMAP_KEY_DAYOFWEEK)){
                   dayOfWeek = ((Integer)e.getValue()).intValue();
                   properties.remove(ExactCronJob.PROPERTIESMAP_KEY_DAYOFWEEK);
               }
            }
            
            // At least one parameter has to be set (!= -1)
            if (hour != -1 || minute != -1 || month != -1 || dayOfMonth != -1 || dayOfWeek != -1)
                cronJob = new ExactCronJob(hour, minute, month, dayOfMonth, dayOfWeek);
            
        }
        
        // If cronjob is of type interval cronjob
        else if (type.intValue() == Cron.INTERVAL_CRONJOB){
            
            // Get interval value from properties map, store it and delete it in the map
            // so that later routines that iterate on the properties dont have to handle it
            int intervalTime = ((Integer) properties.get(IntervalCronJob.PROPERTIESMAP_KEY_INTERVAL)).intValue();
            properties.remove(IntervalCronJob.PROPERTIESMAP_KEY_INTERVAL);
            
            // intervalTime has to be set and must be greater than zero
            if (intervalTime > 0) 
                cronJob = new IntervalCronJob(intervalTime);
        }
        
        if (cronJob != null){
            cronJob.setName(name);
            cronJob.setProcedure(procedure);
            if (errorProcedure != null)
                cronJob.setErrorProcedure(errorProcedure);
            else
                cronJob.setErrorProcedure(loadStandardErrorProcedure());
            
            cronJob.setProperties(properties);
            cronJob.setAlreadyRunning(alreadyRunning.intValue());
            
            cronjobQueue.addJob(cronJob);
            
            Map newCronJobMap = cronJob.getCronJobMap();
            return newCronJobMap;
        }
        return null;        
    }
    
    

    /**
     * this method returns a List of the cronjob-names as simple Strings
     * @param name : can be set to get the name of a single cronjob and check if it exists
     * @param type: can be set to get names of just one type of cronjobs
     * @param sort: optional flag to sort the returned list alphabetically 
     * 
     */
    
    final static public String[] INPUT_GETCRONJOBNAMES = {"name", "type", "sort"};
    final static public boolean[] MANDATORY_GETCRONJOBNAMES = {false, false, false};
    final static public String OUTPUT_GETCRONJOBNAMES = "cronjobnames";
    
    static public List getCronJobNames(OctopusContext all, String name, Integer type, Boolean sort) {
         
        List cronjobnames = new ArrayList();
        Map cronJobMaps = cronjobQueue.getCronJobMaps();
        for (Iterator iter = cronJobMaps.values().iterator(); iter.hasNext();){
            Map tmpMap = (Map)iter.next();
            
            if (name == null || (tmpMap.get(Cron.CRONJOBMAP_KEY_NAME)).equals(name)){
                if (type == null || ((Integer)tmpMap.get(Cron.CRONJOBMAP_KEY_TYPE)).equals(type)){
                    cronjobnames.add(name);
                }
            }
        }
       
        if (sort != null && sort.booleanValue()){
            Object[] tmpArray = cronjobnames.toArray();
            Arrays.sort(tmpArray);
            cronjobnames = Arrays.asList(tmpArray);
        }
                
        return cronjobnames.size() > 0?cronjobnames : null;        
    }
    
    /**
     * This action returns a list of cronjob-maps 
     * 
     *  @param name : can be set to get the cronjob-map of a single cronjob
     *  @param type: can be set to get cronjob-maps of just one type of cronjobs 
     */
    
    final static public String[] INPUT_GETCRONJOBS = {"name", "type"};
    final static public boolean[] MANDATORY_GETCRONJOBS = {false, false};
    final static public String OUTPUT_GETCRONJOBS = "cronjobs";
    
    static public List getCronJobs(OctopusContext all, String name, Integer type) {
        
        List filteredCronJobMaps = new ArrayList();
        
        Map cronJobMaps = cronjobQueue.getCronJobMaps();
        for (Iterator iter = cronJobMaps.values().iterator(); iter.hasNext();){
            
            Map tmpCronJobMap = (Map)iter.next();
            
            if (name == null || (tmpCronJobMap.get(Cron.CRONJOBMAP_KEY_NAME)).equals(name)){
                if (type == null || ((Integer)tmpCronJobMap.get(Cron.CRONJOBMAP_KEY_TYPE)).equals(type)){
                    filteredCronJobMaps.add(tmpCronJobMap);
                }
            }
        }
        return filteredCronJobMaps.size() > 0? filteredCronJobMaps: null;
    }
         
    /**
     * This action returns a cronjob-maps for a secific cronjob 
     * 
     *  @param name : the name of the specified cronjob 
     */ 
    
    final static public String[] INPUT_GETCRONJOB = {"name"};
    final static public boolean[] MANDATORY_GETCRONJOB = {true};
    final static public String OUTPUT_GETCRONJOB = "cronjob";
    
    static public Map getCronJob(OctopusContext all, String name) {
        
        return cronjobQueue.getCronJobMapByName(name);
    }
   
    
    /**
     * This action returns a cronjob-maps for a secific cronjob 
     * 
     * @param cronjob: cronjobmap that specifies the cronjob
     * @param name : the name of the specified cronjob 
     */ 
    
    final static public String[] INPUT_RUNCRONJOB = {"cronjob", "cronjobname"};
    final static public boolean[] MANDATORY_RUNCRONJOB = {false, false};
    final static public String OUTPUT_RUNCRONJOB = "cronjob";
    
    static public Map runCronJob(OctopusContext all, Map cronJobMap, String cronJobName) {
        
        boolean started = false;
        
        cronJobMap = cronjobQueue.mergeCronJobMapAndName(cronJobMap, cronJobName);
        
        if (cronJobMap != null){
            CronJob job = cronjobQueue.getCronJobByName(cronJobMap.get(Cron.CRONJOBMAP_KEY_NAME).toString());
            
            if (job != null){
                started = cronjobQueue.forceRun(cronjobQueue.getCronJobByName(cronJobMap.get(Cron.CRONJOBMAP_KEY_NAME).toString()));
                return job.getCronJobMap();
            }
        }
            //inputCronJobMap.put(Cron.CRONJOBMAP_KEY_ERROR, "No cronjob with name " + inputCronJobMap.get(Cron.CRONJOBMAP_KEY_NAME) + " found.");
        //return inputCronJobMap;
        return null;
    }
    
    /**
     * This action returns the status of a cronjob as String representing an object of Thread.State
     */
    
    final static public String[] INPUT_GETCRONJOBSTATUS = {"cronjob", "name"};
    final static public boolean[] MANDATORY_GETCRONJOBSTATUS = {false, false};
    final static public String OUTPUT_GETCRONJOBSTATUS = "status";
    
    static public String getCronJobStatus(OctopusContext all, Map cronJobMap, String cronJobName) {
        
        cronJobMap = cronjobQueue.mergeCronJobMapAndName(cronJobMap, cronJobName);
        
        if (cronJobMap != null){
            CronJob job = cronjobQueue.getCronJobByCronJobMap(cronJobMap);
            return ((Thread.State)job.getStatus()).toString();
        }
        
        return null;
    }
    
    
        
    /**
     * This action removes a cronjob from the cronjobqueue 
     * 
     *  @param cronjob : cronjobmap that specifies the cronjob
     *  @param name: name of the cronjob as String
     */ 
    
    final static public String[] INPUT_REMOVECRONJOB = {"cronjob", "cronjobname"};
    final static public boolean[] MANDATORY_REMOVECRONJOB = {false, false};
    final static public String OUTPUT_REMOVECRONJOB = "cronjob";
    
    static public Map removeCronJob(OctopusContext all, Map inputCronJobMap, String cronJobName) {
        
        inputCronJobMap = cronjobQueue.mergeCronJobMapAndName(inputCronJobMap, cronJobName);
        
        if (inputCronJobMap != null && cronjobQueue.removeJob(inputCronJobMap)){
            //inputCronJobMap.put(Cron.CRONJOBMAP_KEY_STATUS, Thread.State.TERMINATED);
            return inputCronJobMap;
        }
        return null;
    }
    
    /**
     * This action returns a list of all types of cronjobs
     */
    
    final static public String[] INPUT_GETAVAILABLECRONJOBTYPES = {};
    final static public boolean[] MANDATORY_GETAVAILABLECRONJOBTYPES = {};
    final static public String OUTPUT_GETAVAILABLECRONJOBTYPES = "availableTypes";
    
    static public List getAvailableCronJobTypes(OctopusContext all) {
       
        List types = new ArrayList();
        types.add(new Integer(Cron.EXACT_CRONJOB));
        types.add(new Integer(Cron.INTERVAL_CRONJOB));
        return types;
    }
    
    /**
     * This action returns a map that contains the available properties for a special type of cronjob
     * 
     *  @return Map availableProperties: key = the name of the property, value = the type of the property
     */
    
    final static public String[] INPUT_GETAVAILABLECRONJOBPROPERTIES = {"type"};
    final static public boolean[] MANDATORY_GETAVAILABLECRONJOBPROPERTIES = {true};
    final static public String OUTPUT_GETAVAILABLECRONJOBPROPERTIES = "availableProperties";
    
    static public Map getAvailableCronJobProperties(OctopusContext all, Integer type) {
       
        Map properties = new HashMap();
        
        properties.put(CronJob.PROPERTIESMAP_KEY_ALREADYRUNNING, Integer.class.getName());
        
        if (type.equals(new Integer(Cron.EXACT_CRONJOB))){
            properties.put(ExactCronJob.PROPERTIESMAP_KEY_HOUR, Integer.class.getName());
            properties.put(ExactCronJob.PROPERTIESMAP_KEY_MINUTE, Integer.class.getName());
            properties.put(ExactCronJob.PROPERTIESMAP_KEY_MONTH, Integer.class.getName());
            properties.put(ExactCronJob.PROPERTIESMAP_KEY_DAYOFWEEK, Integer.class.getName());
            properties.put(ExactCronJob.PROPERTIESMAP_KEY_DAYOFMONTH, Integer.class.getName());
        }
         
        else if (type.equals(new Integer(Cron.INTERVAL_CRONJOB))){
            properties.put(IntervalCronJob.PROPERTIESMAP_KEY_INTERVAL, Integer.class.getName());
        }
        return properties;
    }
    
    /**
     * This action activates a cronjob
     * 
     *  @param cronjob : cronjobmap that specifies the cronjob
     *  @param name: name of the cronjob as String
     */
    
    final static public String[] INPUT_ACTIVATECRONJOB = {"cronjob", "cronjobname"};
    final static public boolean[] MANDATORY_ACTIVATECRONJOB = {false, false};
    final static public String OUTPUT_ACTIVATECRONJOB = "cronjob";
    
    static public Map activateCronJob(OctopusContext all, Map cronJobMap, String cronJobName) {
        
        cronJobMap = cronjobQueue.mergeCronJobMapAndName(cronJobMap, cronJobName);
        
        if (cronJobMap != null) {
            if (cronjobQueue.activateCronJob(cronJobMap))
                return cronJobMap;
        }
        return null; 
       
    }
    
    final static public String[] INPUT_DEACTIVATECRONJOB = {"cronjob", "cronjobname"};
    final static public boolean[] MANDATORY_DEACTIVATECRONJOB = {false, false};
    final static public String OUTPUT_DEACTIVATECRONJOB = "cronjob";
    
    static public Map deactivateCronJob(OctopusContext all, Map cronJobMap, String cronJobName) {
        
        cronJobMap = cronjobQueue.mergeCronJobMapAndName(cronJobMap, cronJobName);
        
        if (cronJobMap != null) {
            if (cronjobQueue.deactivateCronJob(cronJobMap))
                return cronJobMap;
        }
        return null; 
       
    }
    
    final static public String[] INPUT_STARTCRONJOBROUTINE = {};
    final static public boolean[] MANDATORY_STARTCRONJOBROUTINE = {};
    final static public String OUTPUT_STARTCRONJOBROUTINE = null;
    
    static public void startCronJobRoutine(OctopusContext all) {
        
        cronjobQueue.activateCron();
       
    }
    
    final static public String[] INPUT_STOPCRONJOBROUTINE = {};
    final static public boolean[] MANDATORY_STOPCRONJOBROUTINE = {};
    final static public String OUTPUT_STOPCRONJOBROUTINE = null;
    
    static public void stopCronJobRoutine(OctopusContext all) {
        
        cronjobQueue.deactivateCron();
       
    }
    
    private static String loadStandardErrorProcedure() {
        // TODO Auto-generated method stub
        return null;
    }
}
