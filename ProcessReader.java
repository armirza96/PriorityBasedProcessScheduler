import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class ProcessReader {
    //SchedulerThread st;
    Thread st;
    Scheduler sc;

    public ProcessReader(Scheduler sc) {
        
        this.sc = sc;
    }

    public void start() {
        Runnable run = () -> {
            System.out.println("File reader is running");    
            try {
                readInputFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        };


        st = new Thread(run);
        st.start();
    }

    private void readInputFile() throws IOException {      
        BufferedReader reader = new BufferedReader(new FileReader("input.txt"));

        int processCount  = Integer.parseInt(reader.readLine());

        //Queue<Process> processes = new LinkedList<Process>();
        
        String line;
        
        while((line = reader.readLine()) != null) {
            String[] values = line.split(" ");

            String id = values[0];
            int arrivalTime = Integer.parseInt(values[1]);
            int burstTime = Integer.parseInt(values[2]);
            int priority =Integer.parseInt(values[3]);

            Process p = new Process(id, arrivalTime, burstTime, priority);

            //deactiveQueue.add(p);
            sc.addProcess(p);
            sc.writeToFile(sc.getOutPut(p, 0));
            System.out.println("Time " + 0 + ", " + p.id + ", " + p.state.value);

            try {
                //Thread.sleep(arrivalTime);
                
                synchronized(st) {
                    st.wait(arrivalTime);
                }
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        
        reader.close();
        //fileReader.interrupt();
        //fileRead = true;
    }
}
