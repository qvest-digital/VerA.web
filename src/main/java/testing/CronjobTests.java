package testing;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import de.tarent.octopus.client.OctopusConnection;
import de.tarent.octopus.client.OctopusConnectionFactory;
import de.tarent.octopus.client.OctopusResult;
import de.tarent.octopus.client.OctopusTask;
import de.tarent.octopus.client.remote.OctopusRemoteConnection;
import de.tarent.octopus.cronjobs.Cron;

public class CronjobTests {

    /**
     * @param args
     */
    public static void main(String[] args) {
        
        CronJobMapFactory mapFactory = new CronJobMapFactory();
        
        OctopusConnectionFactory ocFactory = OctopusConnectionFactory.getInstance();
        
        Map config = new HashMap();
        config.put(OctopusConnectionFactory.CONNECTION_TYPE_KEY, OctopusConnectionFactory.CONNECTION_TYPE_REMOTE);
        config.put(OctopusConnectionFactory.MODULE_KEY, "bonusprofil");
        config.put(OctopusRemoteConnection.PARAM_SERVICE_URL, "http://localhost:8080/octopus");
        config.put(OctopusRemoteConnection.PARAM_USERNAME, "hans");
        
        ocFactory.setConfiguration("bonusprofil", config);
        
        OctopusConnection ocConnection = ocFactory.getConnection("bonusprofil");
        
//        ocConnection.setServiceURL("http://localhost:8080/octopus/");
//        ocConnection.setModuleName("bonusprofil");
//        ocConnection.setUsername("hans");
        
        String procedure = TestProcedure.class.getName();
        
        
        //OctopusTask activateCron = ocConnection.getTask("startCronJobRoutine");
        //OctopusResult ocResult0 = activateCron.invoke();
        
        OctopusTask setCronJob1 = ocConnection.getTask("setCronJob");
        OctopusResult ocResult = setCronJob1.add("cronjob", mapFactory.createIntervalCronJobMap("interval_test_1", procedure , new Integer(2))).invoke();
        //printMap((Map) ocResult.getData("cronjob"));
        
        //OctopusTask setCronJob2 = ocConnection.getTask("setCronJob");
        //OctopusResult ocResult2 = setCronJob2.add("cronjob", mapFactory.createExactCronJobMap("exact_test_1",  procedure , new Integer(18), new Integer(22), new Integer(-1), new Integer(-1), new Integer(-1))).invoke();
        //printMap((Map) ocResult2.getData("cronjob"));
        
       
        OctopusTask getCronJobs = ocConnection.getTask("getCronJobs");
        OctopusResult ocResult3 = getCronJobs.invoke();
        printMapList((List) ocResult3.getData("cronjobs"));
        
        //OctopusTask runCronJob = ocConnection.getTask("getCronJobNames");
        //OctopusResult ocResult4 = runCronJob.add("sort", new Boolean(true)).invoke();
        //System.out.println("########\n" + ocResult4.getData("cronjobnames"));
        
        

        
        //ctopusTask getCronJob = ocConnection.getTask("getCronJob");
        //OctopusResult ocResult5 = getCronJob.add("cronjobname", "interval_test_2").invoke();
        //Map cronJobMap = (Map) ocResult5.getData("cronjob");
        
        //OctopusTask activateCronJob = ocConnection.getTask("activateCronJob");
        //activateCronJob.add("cronjob", cronJobMap);
        //OctopusResult ocResult6 = activateCronJob.add("cronjobname", cronJobMap.get(Cron.CRONJOBMAP_KEY_NAME)).invoke();
        
        
        //OctopusTask removeCronJob = ocConnection.getTask("removeCronJob");
        //removeCronJob.add("cronjobname", "exact_test_1").invoke();
        
        ocResult3 = getCronJobs.invoke();
        printMapList((List) ocResult3.getData("cronjobs"));
        
        
        
        /*
        for (int i = 0; i < 1000000 ; i++){
            
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(i);
            OctopusResult ocResult5 = getCronJob.invoke();
            //System.out.println("Status: " + ocResult5.getData("status") + ", " + new Date());
            printMap((Map) ocResult5.getData("cronjob"));
            
        }
        */
    }
    
    private static void printMap(Map input){
        
        System.out.println("************ Map ************");
        if (input == null) {
            System.out.println(" NULL \n" + "******************************\n");
            return;
        }
        for (Iterator iter = input.entrySet().iterator(); iter.hasNext();)
        {
            Entry entry = (Entry) iter.next();
            System.out.println("* " + entry.getKey() + ", " + entry.getValue());
            
        }
        System.out.println("******************************\n");
        
    }
    
    private static void printMapList(List input){
        if (input == null) {
            System.out.println(" NULL \n" + "******************************\n");
            return;
        }
        
        System.out.println("************ List ************");
        for (Iterator iter = input.iterator(); iter.hasNext();)
        {
            printMap((Map) iter.next());
        }
        System.out.println("******************************\n");
        
    }

}
