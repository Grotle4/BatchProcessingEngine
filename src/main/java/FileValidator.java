import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class FileValidator {

    public void readCsvFiles(List<Path> files){
        String delim = ",";
        String line = "";
        for (Path file : files){
            String filepath = file.toString();
            try (BufferedReader br = new BufferedReader(new FileReader(filepath))){
                while ((line = br.readLine()) != null){
                    String[] row = line.split(delim);

                    for (String field : row){
                        System.out.print(field + " | ");
                    }
                    System.out.println();
                }
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
    }

}
