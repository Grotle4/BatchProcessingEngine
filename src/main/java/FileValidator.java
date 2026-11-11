import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

public class FileValidator {

    public void readCsvFiles(List<Path> files){
        List<Map<String[], Boolean>> processedFiles = getRowList(files);
        for (Map<String[], Boolean> file:processedFiles){
            boolean isValid = validateHeader(file);
            System.out.println(isValid);
        }


    }

    private List<Map<String[], Boolean>> getRowList(List<Path> files){
        List<Map<String[], Boolean>> readFiles = new ArrayList<>();

        String delim = ",";
        String line = "";

        for (Path file : files){
            String filepath = file.toString();
            try (BufferedReader br = new BufferedReader(new FileReader(filepath))){
                Map<String[], Boolean> rows = new HashMap<>();
                while ((line = br.readLine()) != null){
                    String[] row = line.split(delim);
                    rows.put(row, true);
                }
                readFiles.add(rows);
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
        return readFiles;
    }


    private boolean validateHeader(Map<String[], Boolean> file){
        String[] validHeader = {"customerId", "firstName", "lastName", "email", "accountBalance"};

        List<Map.Entry<String[], Boolean>> entryList = new ArrayList<>(file.entrySet());

        Map.Entry<String[], Boolean> header = entryList.getFirst();

        return Arrays.equals(header.getKey(), validHeader);
    }

}
