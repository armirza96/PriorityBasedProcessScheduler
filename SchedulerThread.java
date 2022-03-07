import java.util.function.Supplier;

public class SchedulerThread extends Thread {
    Runnable run;
    
    public SchedulerThread(Runnable run) {
        this.run = run;
    }

    public SchedulerThread() {
        
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        super.run();

        if(run != null)
            run.run();
    }

}
