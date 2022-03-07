import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.function.Function;
import java.util.function.Supplier;

public class Scheduler {

    Timer timer;
    int time = 1000; // using as a semaphone
    //int nextProccessArrivalTime;
    Process nextProcess;
    Thread fileReader;
    Thread scheduler;
    // Queue<Process> q1 = new LinkedList<Process>();
    // Queue<Process> q2 = new LinkedList<Process>();
    Queue<Process>  activeQueue;
    Queue<Process> deactiveQueue;
    boolean addProcess = false;

    /**
     * We define the constructor for t=0
     * @param processes
     */
    public Scheduler() {
        // start timer on another thread
        timer = new Timer();

        activeQueue = new LinkedList<Process>();
        deactiveQueue = new LinkedList<Process>();

        //acts as a output break in the output filw
        writeToFile("\n--------------------------------------------------------------------");
    }

    public void start() {
        Runnable input = () -> {
            try {
                readInputFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        };

        Runnable process = () -> {
            scheduleProcesses();
        };

        fileReader = new Thread(input);
        fileReader.start();

        scheduler = new Thread(process);
        scheduler.start();
    }

    private void scheduleProcesses() {

        if(activeQueue.isEmpty()) {
            Queue<Process> old = activeQueue;

            activeQueue = deactiveQueue;

            deactiveQueue = old;
        } 

        while(!activeQueue.isEmpty()) {
            Process p = activeQueue.poll();
            if(p != null) {
                
                int timeSlotGranted = calculateTimeSlot(p);
                p.increaseTimeSlot();

                p.changeState();

                writeToFile(getOutPut(p, timeSlotGranted));

                p.processedTime += timeSlotGranted;
                addWaitingTimeToProcesses(timeSlotGranted);
                
                for(int i = 0; i < timeSlotGranted; i++) {
                    synchronized(scheduler) { 
                        try {
                            //time++;

                            //incrementTime();
                            time++;

                            if(time == nextProcess.arrivalTime) {
                                synchronized(fileReader) {
                                    fileReader.notify();
                                }
                            }
                            scheduler.wait(1);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }

                p.changeState();

                writeToFile(getOutPut(p, timeSlotGranted));   

                if(p.processedTime < p.burstTime) {
                    if(p.wasGrantedMoreThan2TimeSlots()) {
                        int priority = getProcessPriority(p);
                        p.setPriority(priority);
                        writeToFile("Time " + time + ", " + p.id + ", Priority updated to " + priority);
                    }
                    addProcess(p);
                }
                
                addProcess = true;
            }
        } 

        if(!activeQueue.isEmpty() || !deactiveQueue.isEmpty() || fileReader.isAlive()) {
            scheduleProcesses();
        } else {
            try {
                System.out.println("Program done");
                scheduler.join();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

    // returns time slot slice in miliseconds
    private int calculateTimeSlot(Process p) {
        return p.priority < 100 ? (140 - p.priority) * 20 : (140 - p.priority) * 5;
    }

    // sets process priority
    private int getProcessPriority(Process p) {
        // waiting time calculation = turn around time - burts
        int waitingTime = (p.waitingTime - p.burstTime);
        int bonus = (int) (10*waitingTime / (timer.getTime().getTime() - p.arrivalTime)); 
        return Math.max(100, Math.min(p.priority - bonus + 5, 139));
    }

    public void writeToFile(String output) {
        try {
            FileWriter w = new FileWriter("output.txt", true);
            w.write("\n"+output);
            w.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void addProcess(Process p) {
        deactiveQueue.add(p);
    }

    public String getOutPut(Process p, int granted) {
        String output = "";
        switch(p.state) {
            case ARRIVED:
                output = "Time " + time + ", " + p.id + ", " + p.state.value;
            break;
            case STARTED:
                output = "Time " + time + ", " + p.id + ", " + p.state.value + ", Granted: " + granted;
            break;
            case PAUSED:
                output = "Time " + time + ", " + p.id + ", " + p.state.value;
            break;
            case RESUMED:
                output = "Time " + time + ", " + p.id + ", " + p.state.value + ", Granted: " + granted;
            break;
            case TERMINATED:
                output = "Time " + time + ", " + p.id + ", " + p.state.value;
            break;
        }

        return output;
    }

    private void readInputFile() throws IOException {      
        BufferedReader reader = new BufferedReader(new FileReader("input.txt"));

        int processCount  = Integer.parseInt(reader.readLine());

        //Queue<Process> processes = new LinkedList<Process>();
        
        String line;
        boolean firstProcessAdded = false;

        while((line = reader.readLine()) != null) {
            String[] values = line.split(" ");

            String id = values[0];
            int arrivalTime = Integer.parseInt(values[1]);
            int burstTime = Integer.parseInt(values[2]);
            int priority =Integer.parseInt(values[3]);

            //nextProccessArrivalTime = arrivalTime;

            Process p = new Process(id, arrivalTime, burstTime, priority);

            nextProcess = p;

            if(time < arrivalTime) {
                try {
                    synchronized(fileReader) {
                        fileReader.wait();
                    }
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            writeToFile(getOutPut(p, 0));

            if(firstProcessAdded) {
                while(!addProcess);
                addProcess(p);
                addProcess = false;
            } else {
                addProcess(p);
                firstProcessAdded = true;
            }
        }
        
        reader.close();
        try {
            fileReader.join();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //fileRead = true;
    }

    private void addWaitingTimeToProcesses(int wt) {
        for(Process p: activeQueue) {
            p.increaseWaitingTime(wt);
        }

        for(Process p: deactiveQueue) {
            p.increaseWaitingTime(wt);
        }
    }

    private void incrementTime() {
        time++;

        if(time == nextProcess.arrivalTime) {
            synchronized(fileReader) {
                fileReader.notify();
            }
        }
    }
}
