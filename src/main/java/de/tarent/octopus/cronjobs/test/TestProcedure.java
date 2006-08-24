package de.tarent.octopus.cronjobs.test;

public class TestProcedure implements Runnable {
    
    private String ausgabe;

    public void run() {
        for (int i = 0; i < 6; i++){
            if (Thread.interrupted()){
                System.out.println("THREAD INTERRUPTED");
                break;
            }
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                System.out.println("THREAD INTERRUPTED");
                break;
            }
            System.out.println("test... "+ i + " " + ausgabe);
        }

    }
    
    public void setAusgabe(String ausgabe){
        this.ausgabe = ausgabe;
    }

}
