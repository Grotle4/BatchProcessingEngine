import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileValidator {

    public void readCsvFiles(List<Path> files){
        String delim = ",";
        String line = "";
        List<List<String[]>> readFiles = new ArrayList<>();

        boolean isValid = true;

        for (Path file : files){
            String filepath = file.toString();
            try (BufferedReader br = new BufferedReader(new FileReader(filepath))){
                List<String[]> rows = new ArrayList<>();
                while ((line = br.readLine()) != null){
                    String[] row = line.split(delim);
                    rows.add(row);
                }
                readFiles.add(rows);
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
        for (List<String[]> nestedFile:readFiles){
            for (String[] nestedRow:nestedFile){
                System.out.println(Arrays.toString(nestedRow));
            }
        }
    }

    private void validateHeader(){

    }

}
