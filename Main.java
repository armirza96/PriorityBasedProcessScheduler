import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class Main {
    
    public static void main(String[] args) {
        System.out.println("Lets goooo");
        
        
        Scheduler scheduler = new Scheduler();

        

    }

    private List<Process> readInputFile() throws IOException{
        BufferedReader reader = new BufferedReader(new FileReader("input.txt"));

        int processCount  = Integer.parseInt(reader.readLine());

        ArrayList<Process> proccesses 

        String line;
        while((line = reader.readLine()) != null) {
            String[] values = line.split(" ");

            String name = values[0];
            int 
        }

        reader.close();

        return 
    }
}
