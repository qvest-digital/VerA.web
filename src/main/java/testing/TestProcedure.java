package testing;

public class TestProcedure implements Runnable {

    public void run() {
        for (int i = 0; i < 6; i++){
            if (Thread.interrupted()){
                System.out.println("THREAD INTERRUPTED");
                return;
            }
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("test... "+ i);
        }

    }

}
