package de.tarent.octopus.cronjobs.test;

import java.util.logging.Level;
import java.util.logging.Logger;

public class TestProcedure implements Runnable {

    private Logger logger = Logger.getLogger(TestProcedure.class.getName());

    private String ausgabe;

    public void run() {
        for (int i = 0; i < 6; i++){
            if (Thread.interrupted()){
                logger.log(Level.WARNING,"THREAD INTERRUPTED");
                break;
            }
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                logger.log(Level.WARNING,"THREAD INTERRUPTED");
                break;
            }
            logger.log(Level.INFO,"TEST " + i + ": " + ausgabe );
            System.out.println("TEST " + i + ": " + ausgabe);

        }

    }

    public void setAusgabe(String ausgabe){
        this.ausgabe = ausgabe;
    }

}
