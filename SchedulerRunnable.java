
import java.util.function.Supplier;

public class SchedulerRunnable<T, R> implements Runnable {
    T func;

    public SchedulerRunnable(T func) {
        this.func = func;
    }


    @Override
    public void run() {
        // TODO Auto-generated method stub
        
    }

    public R getValue() {
        if(func instanceof Supplier) {
            return ((Supplier<R>) func).get();
        }
        return null;         
    }
}