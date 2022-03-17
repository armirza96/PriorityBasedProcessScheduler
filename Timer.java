

import java.sql.Timestamp;
import java.util.function.Supplier;

public class Timer {
    Thread thread;
    TimerRunnable run;

    public Timer() {

        run = new TimerRunnable();
        thread = new Thread(run);
        thread.start();
    }

    public int getTime() {
        return run.getTime();
    }
}
