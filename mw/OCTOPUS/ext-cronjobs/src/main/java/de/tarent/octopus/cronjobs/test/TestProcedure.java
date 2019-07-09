package de.tarent.octopus.cronjobs.test;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class TestProcedure implements Runnable {
    private String ausgabe;

    public void run() {
        for (int i = 0; i < 6; i++) {
            if (Thread.interrupted()) {
                logger.warn("THREAD INTERRUPTED");
                break;
            }
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                logger.warn("THREAD INTERRUPTED");
                break;
            }
            logger.info("TEST " + i + ": " + ausgabe);
            System.out.println("TEST " + i + ": " + ausgabe);
        }
    }

    public void setAusgabe(String ausgabe) {
        this.ausgabe = ausgabe;
    }
}
