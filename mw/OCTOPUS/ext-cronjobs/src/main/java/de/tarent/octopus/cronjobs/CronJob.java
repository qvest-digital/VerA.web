package de.tarent.octopus.cronjobs;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.State;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.tarent.octopus.server.Context;

/**
 * This interface must be implemented when extending the cron system
 * with new job types. Please note that you'll also have to modify the
 * run() method in Cron to get a new type working.
 *
 * @author Nils Neumaier (n.neumaier@tarent.de)
 * @author Michael Kleinhenz (m.kleinhenz@tarent.de)
 */
public abstract class CronJob implements Runnable
{
    Logger logger = Logger.getLogger(getClass().getName());
    public static final String PROPERTIESMAP_KEY_ALREADYRUNNING     = "alreadyrunning";

    private static long cronJobCount = 0L;

    public static final int ALREADY_RUNNING_PARALLEL     = 1;    // if job is already running a second instance is started parallel
    public static final int ALREADY_RUNNING_WAIT         = 2;    // if job is already running process waits until job is finished to start next job
    public static final int ALREADY_RUNNING_INTERRUPT    = 3;    // if job is already running the actual job will be interrupted before starting a new job
    public static final int ALREADY_RUNNING_DROP         = 4;    // if job is already running the start of a second job is dropped

    protected static final int timeToWaitForNextTry         = 5000; // milliseconds to wait if job is already running and alreadyRunning == ALREADY_RUNNING_WAIT
    protected static final int maxTimeToWait                = 25000; // wait max 25 seconds for finishing a cronjob

    private Date lastRun;
    private Thread executionThread;
    private int alreadyRunning = ALREADY_RUNNING_PARALLEL;
    private boolean active = true;

    private String name;
    private String errorProcedure;
    private String procedure;
    private String errorMessage = new String();

    private Map properties;

    private Cron cron;

    public CronJob(Cron cron) {
	this.cron = cron;
    }

    public void setCron(Cron cron) {
	setCron(cron);
    }

    public Cron getCron() {
	return cron;
    }

    public void run() {
	// First try to instantiate procedure class
	setErrorMessage(new String()); // Set Errormessage to empty String
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

	for (int i = 0; i < m.length; i++) {
	    String methodname = m[i].getName();
	    Class[] params = m[i].getParameterTypes();

	    if (methodname.startsWith("set") && params.length == 1 && params[0].equals(String.class)) {
		String parameterName = Character.toLowerCase(methodname.charAt(3))+methodname.substring(4);
		if (properties.containsKey(parameterName)) {
		    try {
			m[i].invoke(runnableObject, new Object[] { properties.get(parameterName) } );
		    } catch (Exception e){
			runOnError("An Error occured while trying to invoke method " + m[i].getName() + " on object " + runnableObject.getClass(), e);
		    }
		}
	    } else if (methodname.equals("setProperties") && params.length == 1 && params[0].equals(Properties.class)) {
		try {
			Properties properties = new Properties();
			properties.putAll(this.properties);
			m[i].invoke(runnableObject, new Object[] { properties } );
		} catch (Exception e) {
			runOnError("An Error occured while trying to invoke method " + m[i].getName() + " on object " + runnableObject.getClass(), e);
		}
	    } else if (methodname.equals("setProperties") && params.length == 1 && params[0].equals(Map.class)) {
		try {
			m[i].invoke(runnableObject, new Object[] { Collections.unmodifiableMap(this.properties) } );
		} catch (Exception e) {
			runOnError("An Error occured while trying to invoke method " + m[i].getName() + " on object " + runnableObject.getClass(), e);
		}
	    }
	}

	Context.addActive(getCron().getOctopusContext().cloneContext());
	try {
		if (runnableObject instanceof Runnable) {
			((Runnable)runnableObject).run();
		} else {
		// If no runnable find the run method
		for (int i = 0; i < m.length; i++){
		    if (m[i].getName().equals("run")) {
			m[i].invoke(runnableObject, new Object[] {});
			break;
		    }
		}
		}
	} catch (Exception e) {
	    runOnError("Error trying to invoke run-method of procedure " + runnableObject.getClass(), e);
	    return;
	}
	Context.clear();

	setErrorMessage(""); // Set Errormessage to empty String
    }

    //abstract public void run();
    abstract public int getType();

    public void start()
    {
	executionThread = new Thread(this);
	executionThread.setName("Cron Job " + name + " #" + (cronJobCount++));
	executionThread.start();
    }

    public boolean runnable()
    {
	return runnable(false);
    }

    public boolean runnable(boolean ignoreDeactived)
    {
	// If cronjob is deactivated and ignoreDeactived isnt set true, ABORT
	if (!active && !ignoreDeactived)
	    return false;

	if(executionThread!= null && executionThread.isAlive())
	{
	    switch (alreadyRunning)
	    {
		case ALREADY_RUNNING_PARALLEL:  return true;
		case ALREADY_RUNNING_WAIT:    { Date startDate = new Date();
						Date lastTry = new Date();

						while (executionThread.getState() != Thread.State.TERMINATED )
						{
						    lastTry = new Date();
						    if ((lastTry.getTime() - startDate.getTime()) > maxTimeToWait){
							executionThread.interrupt();
							logger.log(Level.WARNING, "Waiting time expired. Current instance of " + getName() + "will be interrupted and new instance will be started.");
							break;
						    }
						    try {
							Thread.sleep(timeToWaitForNextTry);
						    } catch (InterruptedException e) {
							e.printStackTrace();
						    }
						}
						return true;
					      }
		case ALREADY_RUNNING_INTERRUPT: {executionThread.interrupt();
						 return true;}
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
	    errorMsg += "\n the underlying method is inaccessible. ";
	}
	else if (e.getClass() == IllegalArgumentException.class ) {
	    errorMsg += "\n the number of actual and formal parameters differ, or an unwrapping conversion fails. ";
	}
	else if (e.getClass() == InstantiationException.class ) {

	}
	else if (e.getClass() == InvocationTargetException.class) {
	    errorMsg += "\n the underlying method throws an exception. ";
	    // Error while invocation of method
	}
	else if (e.getClass() == ClassNotFoundException.class){
	    errorMsg += "\n the class cannot be located.";
	    // Error while instantiation of class
	}

	StringWriter sw = new StringWriter();
	e.printStackTrace(new PrintWriter(sw));
	String stacktrace = sw.toString();
	errorMsg += "\n" + stacktrace;

	Class c = null;
	Object runnableObject = null;

	try {
	    c = Class.forName(errorProcedure);
	    runnableObject = c.newInstance();
	}catch (Exception ex){
	    errorMsg += "\n An Error occured while trying to instantiate the error procedure " + errorProcedure + ". ";
	}

	if (errorProcedure != null && errorProcedure.length() > 0 ){
		if (runnableObject != null && c != null){
		    Context.addActive(getCron().getOctopusContext().cloneContext());
		    try {
			boolean methodRunStarted = false;
			Method m[] = c.getMethods();
			// Finally find the run()-method and start the errorprocedure
			for (int i = 0; i < m.length; i++){
			    if (m[i].getName().equals("run")){
				m[i].invoke(runnableObject, new Object[] {});
				methodRunStarted = true;
				break;
			    }
			}
			if (!methodRunStarted)
			    errorMsg += "\n An Error occured while trying to invoke run-method of the error procedure " + errorProcedure + ".\n No run-method found.";

		    } catch (Exception exp) {
			errorMsg += "\n An Error occured while trying to invoke run-method of the error procedure " + errorProcedure + ". ";
			StringWriter sw2 = new StringWriter();
			exp.printStackTrace(new PrintWriter(sw2));
			String stacktrace2 = sw2.toString();
			errorMsg += "\n" + stacktrace2;
		    }
		    Context.clear();
		}
	}
	logger.log(Level.SEVERE, errorMsg);
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
	properties.put(PROPERTIESMAP_KEY_ALREADYRUNNING, new Integer(getAlreadyRunning()));
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
	jobMap.put(Cron.CRONJOBMAP_KEY_ERROR, getErrorMessage());
	jobMap.put(Cron.CRONJOBMAP_KEY_PROPERTIES, getProperties());
	jobMap.put(Cron.CRONJOBMAP_KEY_STATUS, getStatus() == null? "":getStatus().toString());
	if (getLastRun() != null){
		SimpleDateFormat formatter = new SimpleDateFormat("dd'.' MMMM yyyy',' HH:mm:ss");
		String lastRun = null;
			try {
				lastRun = formatter.format(getLastRun());
			} catch (Exception e) {
				logger.log(Level.WARNING, "Error during conversion of Date LastRun to String");
			}
			if (lastRun != null)
			jobMap.put(Cron.CRONJOBMAP_KEY_LASTRUN, lastRun);
	}

	jobMap.put(Cron.CRONJOBMAP_KEY_ACTIVE, new Boolean(active));

	return jobMap;
    }

    public String getErrorMessage() {
	return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
	this.errorMessage = errorMessage;
    }

    public State getStatus(){
	return executionThread == null? null:executionThread.getState();
    }

    public void activate(){
	active = true;
    }

    public void deactivate(){
	active = false;
    }

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
}
