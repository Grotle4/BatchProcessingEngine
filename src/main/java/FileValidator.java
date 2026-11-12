import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

public class FileValidator {

    public void readCsvFiles(List<Path> files){
        List<Entry> entries = getRows(files);
        for (Entry entry:entries){
            boolean isValid = validateHeader(entry);
            if (!isValid){
                entry.valid = false;
            }
        }
    }

    private List<Entry> getRows(List<Path> files){

        List<Entry> entries = new ArrayList<>();

        String delim = ",";
        String line = "";

        for (Path file : files){
            String filepath = file.toString();
            List<Row> rows = new ArrayList<>();

            try (BufferedReader br = new BufferedReader(new FileReader(filepath))){
                while ((line = br.readLine()) != null){
                    String[] row = line.split(delim);
                    Row entryRow = new Row(row, true);
                    rows.add(entryRow);
                }
            }
            catch (IOException e){
                e.printStackTrace();
            }
            Entry entry = new Entry(rows, true, file);
            entries.add(entry);
        }
        return entries;
    }


    private boolean validateHeader(Entry entry){
        String[] validHeader = {"customerId", "firstName", "lastName", "email", "accountBalance"};
        Row header = entry.rows.getFirst();
        return Arrays.equals(header.row, validHeader);
    }

//    private boolean validateColCount(Entry entry){
//        for (String[] key:row.keySet()){
//            if(key.length == 5){
//                return true;
//            }
//            else{
//                return false;
//            }
//        }
//        return false;
//    }

}
