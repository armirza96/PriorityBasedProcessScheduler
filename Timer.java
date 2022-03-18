

import java.sql.Timestamp;
import java.util.function.Supplier;

public class Timer implements Runnable {
    Thread thread;
    boolean on;
    int time = 1000;
    //TimerRunnable run;
    Scheduler scheduler;
    
    public Timer(Scheduler scheduler) {

        //run = new TimerRunnable(thread);
        thread = new Thread(this);
        thread.start();
        on = true;
        this.scheduler = scheduler;
    }

    @Override
    public void run() {
        while(on) {
            
            //System.out.println("Current Time: " + time);
            synchronized(thread) {
                try {
                    thread.wait(1);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            time++;
        }
    }

    public int getTime() {
        return time; 
    }
    
    public void stop() {
        on = false;
    }

    public void start() {
        on = true;
    }
    
    public void join() {
		try {
			thread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

