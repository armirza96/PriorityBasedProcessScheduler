import java.sql.Timestamp;
import java.util.Date;
import java.util.function.Supplier;

// public class Timer {
//     Thread thread;
//     SchedulerRunnable<Supplier<Long>, Long> run;

//     public Timer() {
//         Supplier<Long> time = () -> {
//             Date date = new Date();
//             //This method returns the time in millis
//             long timeMilli = date.getTime();
//             return timeMilli;
//         };

//         run = new SchedulerRunnable<Supplier<Long>, Long>(time);
//         thread = new Thread(run);
//         thread.start();
//     }

//     public Long getTime() {
//         return run.getValue();
//     }
// }
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
