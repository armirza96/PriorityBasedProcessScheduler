

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.Thread.State;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

public class Scheduler {

    Timer timer;
    //int time = 1000; 
    volatile Process nextProcess;
    Thread fileReader;
    Thread scheduler;

    LinkedList<Process>  activeQueue;
    LinkedList<Process> deactiveQueue;

    //volatile int processExecutionCompleted = 1;
    int totalProcessCount = 0;
    int processCount = 0;
    /**
     * We define the constructor for t=0
     * @param processes
     */
    public Scheduler() {
    
        

        activeQueue = new LinkedList<Process>();
        deactiveQueue = new LinkedList<Process>();
        
        //processExecutionCompleted.set(false);

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
        
        // start timer on another thread
        timer = new Timer(this);
    }

    private void scheduleProcesses() {

        LinkedList<Process> old = activeQueue;

        activeQueue = deactiveQueue;

        deactiveQueue = old;
        
        while(!activeQueue.isEmpty()) {
            //System.out.println("Active queue contains:" + activeQueue.size());
            Process p = activeQueue.poll();
            //System.out.println("Process: " + p.id);
            if(p != null) {
                //System.out.println("Running " + p.id + ", Time:" + time);
                //addProcess = false;
                int timeSlotGranted = calculateTimeSlot(p);
                p.increaseTimeSlot();

                p.changeState();

                writeToFile(getOutPut(p, timeSlotGranted));

                p.processedTime += timeSlotGranted;
                addWaitingTimeToProcesses(timeSlotGranted);
                
                //for(int i = 0; i < timeSlotGranted; i++) {
                int currentTime = timer.getTime();
                int stopTime = currentTime + timeSlotGranted;
                System.out.println("Current time: " + currentTime + "Stoptime: " + stopTime);
                
               
                
//                while(timer.getTime() < stopTime) {
//                	System.out.println("Current Time: " + timer.getTime());
//                    synchronized(scheduler) { 
//                        try {
//
//                            if(timer.getTime() == nextProcess.arrivalTime) {
//                                writeToFile(getOutPut(nextProcess, 0));
//                            }
//                            scheduler.wait(1);
//                            
//                        } catch (InterruptedException e) {
//                            // TODO Auto-generated catch block
//                            e.printStackTrace();
//                        }
//                    }
//                }
                
                


                p.changeState();

                writeToFile(getOutPut(p, timeSlotGranted));   

                if(p.processedTime < p.burstTime) {
                    if(p.timeSlotsGranted % 2 == 0) {//if(p.wasGrantedMoreThan2TimeSlots()) {
                        int priority = getProcessPriority(p);
                        p.setPriority(priority);
                        writeToFile("Time " + timer.getTime() + ", " + p.id + ", Priority updated to " + priority);
                    }
                    
                    addProcess(p);
                }

                System.out.println("Finished: " +p.id+", time: " + timer.getTime() );
            }
        } // end while loop
        
        if(processCount < totalProcessCount || processCount == 0) {
            synchronized(fileReader) {
                //System.out.println("Resuming fileReader thread: Adding Process" + (nextProcess != null ? nextProcess.id : ""));
                fileReader.notify();
                
            }
            
            pauseScheduler();
            reorderProcesses();
            scheduleProcesses();
        } else {
            if(!deactiveQueue.isEmpty()) {
                scheduleProcesses();
            } else {
            	timer.stopTimer();
            	timer.join();
                System.out.println("DONE");
                
                return;
            }
        }

    }
    
    private void readInputFile() throws IOException {      
        BufferedReader reader = new BufferedReader(new FileReader("input.txt"));

        totalProcessCount  = Integer.parseInt(reader.readLine());

        //Queue<Process> processes = new LinkedList<Process>();
        
        String line;

        while((line = reader.readLine()) != null) {
            String[] values = line.split(" ");

            String id = values[0];
            int arrivalTime = Integer.parseInt(values[1]);
            int burstTime = Integer.parseInt(values[2]);
            int priority =Integer.parseInt(values[3]);
            
            Process p = new Process(id, arrivalTime, burstTime, priority);

            nextProcess = p;
            
            //System.out.println("Process found: -------------------------------- " + p.id+ ", time: " + time);
            
            if(timer.getTime() < arrivalTime) {
                try {
                    synchronized(fileReader) {
                        fileReader.wait();
                    }
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            addProcess(p);
            processCount++;
            
            resumeScheduler();
        }
        
        reader.close();
        
    }

    // returns time slot slice in miliseconds
    private int calculateTimeSlot(Process p) {
        return p.priority < 100 ? (140 - p.priority) * 20 : (140 - p.priority) * 5;
    }
 
    // sets process priority
    private int getProcessPriority(Process p) {
        // waiting time calculation = turn around time - burts
        int waitingTime = (p.waitingTime - p.burstTime);
        int bonus = (int) (10 * waitingTime / (timer.getTime() - p.arrivalTime)); 
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
    	System.out.println("Process aded: " + p.id + " at time: " + timer.getTime());
    	
    		deactiveQueue.add(p);

        if(scheduler.getState() == State.WAITING) {
	        synchronized (scheduler) {
	        	//System.out.println("REsuming scheduler thread");
				scheduler.notify();
			}
        }
    }

    public String getOutPut(Process p, int granted) {
        String output = "";
        switch(p.state) {
            case ARRIVED:
                output = "Time " + timer.getTime() + ", " + p.id + ", " + p.state.value;
            break;
            case STARTED:
                output = "Time " + timer.getTime() + ", " + p.id + ", " + p.state.value + ", Granted: " + granted;
            break;
            case PAUSED:
                output = "Time " + timer.getTime() + ", " + p.id + ", " + p.state.value;
            break;
            case RESUMED:
                output = "Time " + timer.getTime() + ", " + p.id + ", " + p.state.value + ", Granted: " + granted;
            break;
            case TERMINATED:
                output = "Time " + timer.getTime() + ", " + p.id + ", " + p.state.value;
            break;
        }

        return output;
    }

    private void addWaitingTimeToProcesses(int wt) {
        // for(Process p: activeQueue) {
        //     p.increaseWaitingTime(wt);
        // }

        for(Process p: deactiveQueue) {
            p.increaseWaitingTime(wt);
        }
    }
    
    private void reorderProcesses() {
    	//deactiveQueue.sort((p1, p2) -> p1.priority);
    	printOutQueue(deactiveQueue);
    	// descending sort
    	 Collections.sort(deactiveQueue, new Comparator<Process>() {
    	     @Override
    	     public int compare(Process p1, Process p2) {
    	         return p1.priority - p2.priority;
    	     }
    	 });
    	 printOutQueue(deactiveQueue);
    }
    
    private void printOutQueue(LinkedList<Process> queue) {
    	System.out.println("-------------------------------------");
    	for(Process p: queue)
    		System.out.println("In Queue => ID: " + p.id + ", " + p.priority);
    	//System.out.println("-------------------------------------");
    }
    
    public void resumeScheduler() {
        synchronized(scheduler) {
		    scheduler.notify();
		}
    }
    
    public void pauseScheduler() {
        synchronized(scheduler) {
		    try {
				scheduler.wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
    }
}
