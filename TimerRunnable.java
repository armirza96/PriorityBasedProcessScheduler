
import java.util.function.Supplier;

public class TimerRunnable implements Runnable {
    int time = 1000;

    public TimerRunnable() {
        
    }


    @Override
    public void run() {
        // TODO Auto-generated method stub
        time += 1;
    }

    public int getTime() {
        return time; 
    }
}