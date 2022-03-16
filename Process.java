
/**
 * will hold process data
 */

public class Process {
    Thread thread;
    String id;
    int arrivalTime;
    int burstTime;
    int priority;
    int timeSlotsGranted = 0;
    STATE state;
    int waitingTime = 0;
    int processedTime = 0;

    public Process(String id, int arrivalTime, int burstTime, int priority) {
        this.id = id;
        this. arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.priority = priority;

        state = STATE.ARRIVED;

        thread = new Thread(id);
    }

    public void increaseTimeSlot() {
        timeSlotsGranted++;
    }

    public void setPriority(int newPriority) {
        this.priority = newPriority;
    }

    public boolean wasGrantedMoreThan2TimeSlots() {
        return timeSlotsGranted >= 2;
    }

    public void startProcess() {
        thread.start();
    }

    public void suspendProcess() {
        try {
            synchronized (thread) {
                thread.wait();
            }
            
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // synchronized(thread) {
        //     thread.interrupt();
        // }
    }

    public void resumeProcess() {
        synchronized (thread) {
            thread.notify();
        }
    }

    public void terminate() {
        state = STATE.TERMINATED;
        //thread.interrupt();
        // try {
        //     thread.join();
        // } catch (InterruptedException e) {
        //     // TODO Auto-generated catch block
        //     e.printStackTrace();
        // }
    }

    public void changeState() {
        if(processedTime >= burstTime) {
            terminate();
            return;
        }
        switch(state) {
            case ARRIVED:
                state = STATE.STARTED;
                startProcess();
            break;
            case STARTED:
                state = STATE.PAUSED;
                //suspendProcess();
            break;
            case PAUSED:
                state = STATE.RESUMED;
                //resumeProcess();
            break;
            case RESUMED:
                state = STATE.PAUSED;
                //suspendProcess();
            break;
        }
    }

    public void increaseWaitingTime(int wt) {
        this.waitingTime += wt;
    }
    // public void pause(int timeSlot) {
    //     try {
    //         thread.wait(timeSlot);
    //     } catch (InterruptedException e) {
    //         // TODO Auto-generated catch block
    //         e.printStackTrace();
    //     }
    // }
}
