package de.tarent.octopus.cronjobs.worker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.tarent.octopus.cronjobs.Cron;
import de.tarent.octopus.cronjobs.CronJob;
import de.tarent.octopus.cronjobs.ExactCronJob;
import de.tarent.octopus.cronjobs.IntervalCronJob;
import de.tarent.octopus.server.OctopusContext;

public class CronJobWorker {
    
    private Cron cronjobQueue = new Cron(); 
    private static Logger logger = Logger.getLogger(CronJobWorker.class.getName());

    
    /**
     * this method creates a new cronjob. If a cronjob with the same name already exists, 
     * the old one isreplaced by the new cronjob.
     * 
     * @param name : can be set to get the name of a single cronjob and check if it exists
     * @param type: can be set to get names of just one type of cronjobs
     * @param sort: optional flag to sort the returned list alphabetically 
     * @return cronjob: cronjob-map of edited or created cronjob 
     */ 
   
    final static public String[] INPUT_setCronJob = {"cronjob"};
    final static public boolean[] MANDATORY_setCronJob = {true};
    final static public String OUTPUT_setCronJob = "cronjob";
    
    public Map setCronJob(OctopusContext all, Map cronJobMap) {

        cronJobMap = correctCronJobMap(cronJobMap);
        
        String name             = (String)cronJobMap.get(Cron.CRONJOBMAP_KEY_NAME);
        Integer type = null;
        try {
            type = new Integer(Integer.parseInt(cronJobMap.get(Cron.CRONJOBMAP_KEY_TYPE).toString()));
        } catch (NumberFormatException e1) {
            logger.log(Level.WARNING, "Error while parsing Integer CronJobType from Map. Value is not parseable: " + cronJobMap.get(Cron.CRONJOBMAP_KEY_TYPE));
        }
        String procedure        = (String)cronJobMap.get(Cron.CRONJOBMAP_KEY_PROCEDURE);
        String errorProcedure   = (String)cronJobMap.get(Cron.CRONJOBMAP_KEY_ERRORPROCEDURE);
        
        Map properties          = (Map)cronJobMap.get(Cron.CRONJOBMAP_KEY_PROPERTIES);
        
        // Some Entries must be set or we will return null 
        if (name == null || procedure == null || properties == null || type == null){
            logger.log(Level.WARNING, "Error in Task setCronJob. One of the following Map entries has not been set: " + "name: " + name + ", procedure: " + procedure + ", properties: " + properties + ", type: " + type);
            return null;
        }
        
        
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

                Entry e = (Entry) iter.next();
                String key = e.getKey().toString();
                
               if (key.equals(ExactCronJob.PROPERTIESMAP_KEY_HOUR)){
                    hour = ((Integer)e.getValue()).intValue();
               }
                
               if (key.equals(ExactCronJob.PROPERTIESMAP_KEY_MINUTE)){
                   minute = ((Integer)e.getValue()).intValue();
               }
                
               if (key.equals(ExactCronJob.PROPERTIESMAP_KEY_MONTH)){
                   month = ((Integer)e.getValue()).intValue();
               }
                                    
               if (key.toLowerCase().equals(ExactCronJob.PROPERTIESMAP_KEY_DAYOFMONTH)){
                   dayOfMonth = ((Integer)e.getValue()).intValue();
               }
                
               if (key.toLowerCase().equals(ExactCronJob.PROPERTIESMAP_KEY_DAYOFWEEK)){
                   dayOfWeek = ((Integer)e.getValue()).intValue();
               }
            }
            
            if (hour > -1 ) properties.remove(ExactCronJob.PROPERTIESMAP_KEY_HOUR);
            if (minute > -1) properties.remove(ExactCronJob.PROPERTIESMAP_KEY_MINUTE);
            if (month > -1) properties.remove(ExactCronJob.PROPERTIESMAP_KEY_MONTH);
            if (dayOfWeek > -1) properties.remove(ExactCronJob.PROPERTIESMAP_KEY_DAYOFMONTH);
            if (dayOfMonth > -1) properties.remove(ExactCronJob.PROPERTIESMAP_KEY_DAYOFWEEK);
            
            // At least one parameter has to be set (!= -1)
            if (hour != -1 || minute != -1 || month != -1 || dayOfMonth != -1 || dayOfWeek != -1)
                cronJob = new ExactCronJob(hour, minute, month, dayOfMonth, dayOfWeek);
            
        }
        
        // If cronjob is of type interval cronjob
        else if (type.intValue() == Cron.INTERVAL_CRONJOB){
            
            // Get interval value from properties map, store it and delete it in the map
            // so that later routines that iterate on the properties dont have to handle it
            Integer interval = (Integer) properties.get(IntervalCronJob.PROPERTIESMAP_KEY_INTERVAL);
            int intervalTime = 0;
            if (interval != null){
                intervalTime = interval.intValue();
                properties.remove(IntervalCronJob.PROPERTIESMAP_KEY_INTERVAL);
            }
            else
                logger.log(Level.WARNING, "Error trying to create an IntervalCronJob. Entry '" + IntervalCronJob.PROPERTIESMAP_KEY_INTERVAL + "' in properties map has not been set.");
            
            
            // intervalTime has to be set and must be greater than zero
            if (intervalTime  > 0) 
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
            if (alreadyRunning != null)
                cronJob.setAlreadyRunning(alreadyRunning.intValue());
            
            if (cronjobQueue.addJob(cronJob)){                
                Map newCronJobMap = cronJob.getCronJobMap();
                return newCronJobMap;
            }
            return null;
        }
        
        logger.log(Level.WARNING, "No CronJob could be created.");
        
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
    
    public List getCronJobNames(OctopusContext all, String name, Integer type, Boolean sort) {
         
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
    
    public List getCronJobs(OctopusContext all, String name, Integer type) {
        
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
    
    final static public String[] INPUT_GETCRONJOB = {"cronjobname"};
    final static public boolean[] MANDATORY_GETCRONJOB = {true};
    final static public String OUTPUT_GETCRONJOB = "cronjob";
    
    public Map getCronJob(OctopusContext all, String name) {
        
        return cronjobQueue.getCronJobMapByName(name);
    }
   
    
    /**
     * This action returns a cronjob-maps for a specific cronjob 
     * 
     * @param cronjob: cronjobmap that specifies the cronjob
     * @param name : the name of the specified cronjob 
     */ 
    
    final static public String[] INPUT_RUNCRONJOB = {"cronjob", "cronjobname"};
    final static public boolean[] MANDATORY_RUNCRONJOB = {false, false};
    final static public String OUTPUT_RUNCRONJOB = "cronjob";
    
    public Map runCronJob(OctopusContext all, Map inputCronJobMap, String cronJobName) {
        
         Map cronJobMap = cronjobQueue.mergeCronJobMapAndName(inputCronJobMap, cronJobName);
        
        if (cronJobMap != null){
            CronJob job = cronjobQueue.getCronJobByName(cronJobMap.get(Cron.CRONJOBMAP_KEY_NAME).toString());
            
            if (job != null){
                cronjobQueue.forceRun(job);
                Map returnMap = job.getCronJobMap();
                return returnMap;
            }
        }
        
        String errMsg = "An Error occured trying to activate a cronjob. The transfered parameters either reference different cornjobs or both parameters are null. ";
        errMsg += "\n inputCronJobMap: " + inputCronJobMap;
        errMsg += "\n cronJobName: " + cronJobName;
        
        all.setContentError(errMsg);
        logger.log(Level.WARNING, errMsg);
        return null;
    }
    
    /**
     * This action returns the status of a cronjob as String representing an object of Thread.State
     */
    
    final static public String[] INPUT_GETCRONJOBSTATUS = {"cronjob", "cronjobname"};
    final static public boolean[] MANDATORY_GETCRONJOBSTATUS = {false, false};
    final static public String OUTPUT_GETCRONJOBSTATUS = "status";
    
    public String getCronJobStatus(OctopusContext all, Map inputCronJobMap, String cronJobName) {
        
        Map cronJobMap = cronjobQueue.mergeCronJobMapAndName(inputCronJobMap, cronJobName);
        
        if (cronJobMap != null){
            CronJob job = cronjobQueue.getCronJobByCronJobMap(cronJobMap);
            return job.getStatus() != null ?(job.getStatus()).toString(): "";
        }
        
        String errMsg = "An Error occured trying get the status of a cronjob. The transfered parameters either reference different cornjobs or both parameters are null. ";
        errMsg += "\n inputCronJobMap: " + inputCronJobMap;
        errMsg += "\n cronJobName: " + cronJobName;
        
        all.setContentError(errMsg);
        logger.log(Level.WARNING, errMsg);
        
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
    
    public Map removeCronJob(OctopusContext all, Map inputCronJobMap, String cronJobName) {
        
        Map cronJobMap = cronjobQueue.mergeCronJobMapAndName(inputCronJobMap, cronJobName);
        
        if (cronJobMap != null && cronjobQueue.removeJob(cronJobMap)){
            //inputCronJobMap.put(Cron.CRONJOBMAP_KEY_STATUS, Thread.State.TERMINATED);
            logger.log(Level.INFO, "Cronjob has been removed from queue: " + cronJobMap.get(Cron.CRONJOBMAP_KEY_NAME));
            return cronJobMap;
        }
        String errMsg = "An Error occured trying to remove Cronjob from queue. The transfered parameters either reference different cornjobs or both parameters are null. ";
        errMsg += "\n inputCronJobMap: " + inputCronJobMap;
        errMsg += "\n cronJobName: " + cronJobName;
        
        all.setContentError(errMsg);
        logger.log(Level.WARNING, errMsg);
        return null;
    }
    
    /**
     * This action returns a list of all types of cronjobs
     */
    
    final static public String[] INPUT_GETAVAILABLECRONJOBTYPES = {};
    final static public boolean[] MANDATORY_GETAVAILABLECRONJOBTYPES = {};
    final static public String OUTPUT_GETAVAILABLECRONJOBTYPES = "availableTypes";
    
    public List getAvailableCronJobTypes(OctopusContext all) {
       
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
    
    public Map getAvailableCronJobProperties(OctopusContext all, Integer type) {
       
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
    
    public Map activateCronJob(OctopusContext all, Map inputCronJobMap, String cronJobName) {
        
        Map cronJobMap = cronjobQueue.mergeCronJobMapAndName(inputCronJobMap, cronJobName);
        
        if (cronJobMap != null) {
            if (cronjobQueue.activateCronJob(cronJobMap))
                logger.log(Level.INFO, "CronJob " + cronJobMap.get(Cron.CRONJOBMAP_KEY_NAME) + "has been activated and will be runnable."  );
                return cronJobMap;
        }
        
        String errMsg = "An Error occured trying to activate a cronjob. The transfered parameters either reference different cornjobs or both parameters are null. ";
        errMsg += "\n inputCronJobMap: " + inputCronJobMap;
        errMsg += "\n cronJobName: " + cronJobName;
        
        all.setContentError(errMsg);
        logger.log(Level.WARNING, errMsg);
        return null; 
       
    }
    
    final static public String[] INPUT_DEACTIVATECRONJOB = {"cronjob", "cronjobname"};
    final static public boolean[] MANDATORY_DEACTIVATECRONJOB = {false, false};
    final static public String OUTPUT_DEACTIVATECRONJOB = "cronjob";
    
    public Map deactivateCronJob(OctopusContext all, Map inputCronJobMap, String cronJobName) {
        
        Map cronJobMap = cronjobQueue.mergeCronJobMapAndName(inputCronJobMap, cronJobName);
        
        if (cronJobMap != null) {
            if (cronjobQueue.deactivateCronJob(cronJobMap))
                return cronJobMap;
        }
        
        String errMsg = "An Error occured trying to deactivate a cronjob. The transfered parameters either reference different cornjobs or both parameters are null. ";
        errMsg += "\n inputCronJobMap: " + inputCronJobMap;
        errMsg += "\n cronJobName: " + cronJobName;
        
        all.setContentError(errMsg);
        logger.log(Level.WARNING, errMsg);
        return null; 
       
    }
    
    final static public String[] INPUT_STARTCRONJOBROUTINE = {};
    final static public boolean[] MANDATORY_STARTCRONJOBROUTINE = {};
    final static public String OUTPUT_STARTCRONJOBROUTINE = null;
    
    public void startCronJobRoutine(OctopusContext all) {
        
        cronjobQueue.activateCron();
       
    }
    
    final static public String[] INPUT_STOPCRONJOBROUTINE = {};
    final static public boolean[] MANDATORY_STOPCRONJOBROUTINE = {};
    final static public String OUTPUT_STOPCRONJOBROUTINE = null;
    
    public void stopCronJobRoutine(OctopusContext all) {
        
        cronjobQueue.deactivateCron();
       
    }
    
    
    private Map correctCronJobMap(Map input){
        
        Map resultMap = input;
        Map properties = input.get(Cron.CRONJOBMAP_KEY_PROPERTIES) != null ? (Map) input.get(Cron.CRONJOBMAP_KEY_PROPERTIES): new HashMap();
        
        for (Iterator iter = input.entrySet().iterator(); iter.hasNext();){
            Entry entry = (Entry) iter.next();
            Object key = entry.getKey();
            
            // if there is an entry in the cronjobmap, that doesnt belong there
            // we remove it from the cronjobmap and add it to the internal properties map
            if (!key.equals(Cron.CRONJOBMAP_KEY_NAME) && !key.equals(Cron.CRONJOBMAP_KEY_PROPERTIES)
                && !key.equals(Cron.CRONJOBMAP_KEY_TYPE) && !key.equals(Cron.CRONJOBMAP_KEY_STATUS)
                && !key.equals(Cron.CRONJOBMAP_KEY_PROCEDURE) && !key.equals(Cron.CRONJOBMAP_KEY_ERRORPROCEDURE)
                && !key.equals(Cron.CRONJOBMAP_KEY_LASTRUN) && !key.equals(Cron.CRONJOBMAP_KEY_ERROR)){
            
                    properties.put(entry.getKey(), entry.getValue());
                    resultMap.remove(key);
             
            }
        }
        
        resultMap.put(Cron.CRONJOBMAP_KEY_PROPERTIES, properties);
        return resultMap;
    }
    
    private String loadStandardErrorProcedure() {
        // TODO Auto-generated method stub
        return null;
    }
}
