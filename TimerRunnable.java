
import java.util.function.Supplier;

public class TimerRunnable implements Runnable {
    int time = 1000;
    boolean on = false;
    Thread t;

    public TimerRunnable(Thread t) {
        on = true;
        this.t = t;
    }


    @Override
    public void run() {
        while(on) {
            time++;
            System.out.println("Current Time: " + time);
            synchronized(t) {
                try {
                    t.wait(1);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

        }
    }

    public int getTime() {
        return time; 
    }
    
    public void stopTimer() {
        on = false;
    }

    public void startTimer() {
        on = true;
    }
}