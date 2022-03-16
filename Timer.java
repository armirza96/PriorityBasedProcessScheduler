

import java.sql.Timestamp;
import java.util.function.Supplier;

public class Timer {
    Thread thread;
    SchedulerRunnable<Supplier<Timestamp>, Timestamp> run;

    public Timer() {
        Supplier<Timestamp> time = () -> {
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            return timestamp;
        };

        run = new SchedulerRunnable<Supplier<Timestamp>, Timestamp>(time);
        thread = new Thread(run);
        thread.start();
    }

    public Timestamp getTime() {
        return run.getValue();
    }
}
