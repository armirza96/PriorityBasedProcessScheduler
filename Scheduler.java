import java.util.Date;
import java.util.function.Function;
import java.util.function.Supplier;

public class Scheduler {

    SchedulerRunnable<Supplier<Long>, Long> timer;

    public Scheduler() {
        Supplier<Long> time = () -> {
            Date date = new Date();
            //This method returns the time in millis
            long timeMilli = date.getTime();
            return timeMilli;
        };

        timer = new SchedulerRunnable<Supplier<Long>, Long>(time);

        createThread(timer);

        for(int i = 0; i < 10; i++) {
            try {
                Thread.sleep(1000);

                System.out.println("Every 10 seconds: " + timer.getValue());
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private void createThread(SchedulerRunnable run) {
        Process task = new Process(run);									// Generate new task
    }
}
