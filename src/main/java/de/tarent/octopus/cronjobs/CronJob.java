/*
 * Copyright (c) tarent GmbH
 * Bahnhofstrasse 13 . 53123 Bonn
 * www.tarent.de . info@tarent.de
 *
 * Created on 28.02.2006
 */

package de.tarent.octopus.cronjobs;

import java.lang.Thread.State;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * This interface must be implemented when extending the cron system
 * with new job types. Please note that you'll also have to modify the
 * run() method in Cron to get a new type working.
 *
 * @author Nils Neumaier (n.neumaier@tarent.de)
 * @author Michael Kleinhenz (m.kleinhenz@tarent.de)
 * 
 */

public abstract class CronJob implements Runnable
{
    public static final String PROPERTIESMAP_KEY_ALREADYRUNNING     = "alreadyrunning";
    
    protected static final int ALREADY_RUNNING_PARALLEL     = 1;    // if job is already running a second instance is started parallel
    protected static final int ALREADY_RUNNING_WAIT         = 2;    // if job is already running process waits until job is finished to start next job
    protected static final int ALREADY_RUNNING_INTERRUPT    = 3;    // if job is already running the actual job will be interrupted before starting a new job
    protected static final int ALREADY_RUNNING_DROP         = 4;    // if job is already running the start of a second job is dropped
    
    protected static final int timeToWaitForNextTry         = 5000; // milliseconds to wait if job is already running and alreadyRunning == ALREADY_RUNNING_WAIT
    
	private Date lastRun = new Date();
	private Thread executionThread;
    private int alreadyRunning = ALREADY_RUNNING_PARALLEL;
    private boolean active = true;
    
    private String name;
    private String errorProcedure;
    private String procedure;
    private String errorMessage = new String();
    
    private Map properties;
    
    private Map cronJobMap; 
    
    public void run(){
        
            cronJobMap = new HashMap();
            
        // First try to instantiate procedure class
          
            Class c = null;
            Object runnableObject = null;
            
            try {
                c = Class.forName(procedure);
                runnableObject = c.newInstance();
            }catch (Exception e){
                runOnError("An Error occured while trying to instantiate " + procedure, e); 
                return;
            }
            
            // Try to find all Setter-Methods that match properties from parameter map 
            // and run each Setter with the correct parameter
            
            Method m[] = c.getMethods();
            for (int i = 0; i < m.length; i++){
                String methodname = m[i].getName();
                
                if (methodname.startsWith("set")){
                    String parameterName = methodname.substring(4).toLowerCase();
                    if (properties.containsKey(parameterName)
                        && (m[i].getParameterTypes().length == 1 )
                        &&  m[i].getParameterTypes()[0].isInstance( properties.get(parameterName)))
                        try {
                            m[i].invoke(runnableObject, new Object[] {properties.get(parameterName)});
                        } catch (Exception e){
                            runOnError("An Error occured while trying to invoke method " + m[i].getName() + "on object " + runnableObject.getClass(), e); 
                        }
                }
            }
            
            try {
                // Finally find the run()-method and start the procedure
                for (int i = 0; i < m.length; i++){
                    if (m[i].getName().equals("run"));
                        m[i].invoke(runnableObject, new Object[] {});
                        break;
                }
            } catch (Exception e) {
                runOnError("Error trying to invoke run-method of procedure " + runnableObject.getClass(), e);
                return;
            }
        
        
        setLastRun(new Date());
        setErrorMessage(new String()); // Set Errormessage to empty String 
    }
    
    //abstract public void run();
    abstract public int getType();
    
    public void start()
    {
    	executionThread = new Thread(this);
    	executionThread.start();
    }
    
    public boolean runnable()
    {
        if (!active)
            return false;
        
    	if(executionThread!= null && executionThread.isAlive())
    	{
            switch (alreadyRunning)
            {
                case ALREADY_RUNNING_PARALLEL:  return true;
                case ALREADY_RUNNING_WAIT:    { while (executionThread.getState() != Thread.State.TERMINATED)
                                                {
                                                    try {
                                                        Thread.sleep(timeToWaitForNextTry);
                                                    } catch (InterruptedException e) {                                                       
                                                        e.printStackTrace();
                                                    }
                                                }
                                                return true;
                                              }
                case ALREADY_RUNNING_INTERRUPT: {executionThread.interrupt();
                                                 executionThread = null;}
                case ALREADY_RUNNING_DROP:      return executionThread.getState() == Thread.State.TERMINATED;
            }
    	}
    	return true;
    }
    
    public Date getLastRun()
    {
    	return lastRun;
    }
    
    public void setLastRun(Date date)
    {
    	lastRun = date;
    }
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
      
    public int getAlreadyRunning() {
        return alreadyRunning;
    }
    
    public void setAlreadyRunning(int alreadyRunning) {
        this.alreadyRunning = alreadyRunning;
    }

    public void runOnError(String errorMsg, Exception e){
        
        if (e.getClass() == SecurityException.class) {
            errorMsg += "\n the underlying method is inaccessible.";
        }
        else if (e.getClass() == IllegalArgumentException.class ) {
            errorMsg += "\n the number of actual and formal parameters differ, or an unwrapping conversion fails.";   
        }
        else if (e.getClass() == InstantiationException.class ) {
           
        } 
        else if (e.getClass() == InvocationTargetException.class) {
            errorMsg += "\n the underlying method throws an exception.";
            // Error while invocation of method
        }
        else if (e.getClass() == ClassNotFoundException.class){
            errorMsg += "\n the class cannot be located.";
            // Error while instantiation of class
        }
        
        boolean procedureStarted = false;
        Class c = null;
        Object runnableObject = null;
        
        try {
            c = Class.forName(errorProcedure);
            runnableObject = c.newInstance();
        }catch (Exception ex){
            errorMsg += "\n An Error occured while trying to instantiate the error procedure " + procedure; 
        }
        
        if (runnableObject != null){
            try {
                boolean methodRunStarted = false;
                Method m[] = c.getMethods();
                // Finally find the run()-method and start the procedure
                for (int i = 0; i < m.length; i++){
                    if (m[i].getName().equals("run")){
                        m[i].invoke(runnableObject, new Object[] {});
                        methodRunStarted = true;
                        break;
                    }
                }
                if (!methodRunStarted)
                    errorMsg += "\n An Error occured while trying to invoke run-method of the error procedure " + procedure + ".\n No run-method found.";       
                
            } catch (Exception exp) {
                errorMsg += "\n An Error occured while trying to invoke run-method of the error procedure " + procedure;                
            }       
        }
        
        setErrorMessage(errorMsg); 
    }

    public String getErrorProcedure() {
        return errorProcedure;
    }

    public void setErrorProcedure(String errorProcedure) {
        this.errorProcedure = errorProcedure;
    }

    public String getProcedure() {
        return procedure;
    }

    public void setProcedure(String procedure) {
        this.procedure = procedure;
    }

    public Map getProperties() {
        return properties;
    }

    public void setProperties(Map parameters) {
        this.properties = parameters;
    }

    public Map getCronJobMap() {
        
        Map jobMap = new HashMap();
        jobMap = new HashMap();
        jobMap.put(Cron.CRONJOBMAP_KEY_NAME, getName());
        jobMap.put(Cron.CRONJOBMAP_KEY_TYPE, new Integer(getType()));
        jobMap.put(Cron.CRONJOBMAP_KEY_PROCEDURE, getProcedure());
        jobMap.put(Cron.CRONJOBMAP_KEY_ERRORPROCEDURE, getErrorProcedure());
        jobMap.put(Cron.CRONJOBMAP_KEY_PROPERTIES, getProperties());
        jobMap.put(Cron.CRONJOBMAP_KEY_STATUS, getStatus().toString());
        jobMap.put(Cron.CRONJOBMAP_KEY_ERROR, getErrorMessage());
        jobMap.put(Cron.CRONJOBMAP_KEY_LASTRUN, getLastRun().toString());
        
        return cronJobMap;
    }

    public void setCronJobMap(Map cronJobMap) {
        this.cronJobMap = cronJobMap;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    
    public State getStatus(){
        return executionThread.getState();
    }
    
    public void activate(){
        active = true;
    }
    
    public void deactivate(){
        active = false;
    }
}
