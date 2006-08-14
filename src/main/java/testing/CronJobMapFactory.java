package testing;

import java.util.HashMap;
import java.util.Map;

public class CronJobMapFactory {
    

    public static final int EXACT_CRONJOB = 1;
    public static final int INTERVAL_CRONJOB = 2;    
    
    public static final String CRONJOBMAP_KEY_NAME              = "name";
    public static final String CRONJOBMAP_KEY_TYPE              = "type";
    public static final String CRONJOBMAP_KEY_PROCEDURE         = "procedure";
    public static final String CRONJOBMAP_KEY_PROPERTIES        = "properties";

    // Exact Cronjob
    public static final String PROPERTIESMAP_KEY_HOUR               = "hour";
    public static final String PROPERTIESMAP_KEY_MINUTE             = "minute";
    public static final String PROPERTIESMAP_KEY_MONTH              = "month";
    public static final String PROPERTIESMAP_KEY_DAYOFWEEK          = "dayofweek";
    public static final String PROPERTIESMAP_KEY_DAYOFMONTH         = "dayofmonth";
    
    // Interval Cronjob
    public static final String PROPERTIESMAP_KEY_INTERVAL  = "interval";
    
    
    public CronJobMapFactory(){
        
    }
    
    public Map createExactCronJobMap(String name, String procedure, Integer hour, Integer minute, Integer month, Integer dayofweek, Integer dayofmonth){
        
        Map cronJobMap = getStandardMap(name, procedure);
        cronJobMap.put(CRONJOBMAP_KEY_TYPE, new Integer(1));
        
        Map properties = new HashMap();
        properties.put(PROPERTIESMAP_KEY_HOUR, hour == null? new Integer(-1): hour);
        properties.put(PROPERTIESMAP_KEY_MINUTE, minute == null? new Integer(-1): minute);
        properties.put(PROPERTIESMAP_KEY_MONTH, month == null? new Integer(-1): month);
        properties.put(PROPERTIESMAP_KEY_DAYOFWEEK, dayofweek == null? new Integer(-1): dayofweek);
        properties.put(PROPERTIESMAP_KEY_DAYOFMONTH, dayofmonth == null? new Integer(-1): dayofmonth);
        
        cronJobMap.put(CRONJOBMAP_KEY_PROPERTIES, properties);
        
        return cronJobMap;
    }
    
    public Map createIntervalCronJobMap(String name, String procedure, Integer interval){
        
        Map cronJobMap = getStandardMap(name, procedure);
        cronJobMap.put(CRONJOBMAP_KEY_TYPE, new Integer(2));
        
        Map properties = new HashMap();
        properties.put(PROPERTIESMAP_KEY_INTERVAL, interval == null? new Integer(0): interval);
        
        cronJobMap.put(CRONJOBMAP_KEY_PROPERTIES, properties);
        
        return cronJobMap;
    }
    
    private Map getStandardMap(String name, String procedure){
        Map result = new HashMap();
        
        result.put(CRONJOBMAP_KEY_NAME, name);
        result.put(CRONJOBMAP_KEY_PROCEDURE, procedure);
        
        return result;
    }

}
