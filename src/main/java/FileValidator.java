import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

public class FileValidator {

    public void readCsvFiles(List<Path> files){
        List<Entry> entries = getRows(files);
        List<Row> failedRows = new ArrayList<>();
        for (Entry entry:entries){
            entry.valid = validateHeader(entry);
            if (!entry.valid){
                //TODO: append to list of entries that will be written as invalid due to invalid structure.
            }
            validateColCount(entry);
            failedRows = removeFailedEntries(entry, failedRows);
            for (Row row:failedRows){
                System.out.println(Arrays.toString(row.row) +":"+row.valid + ":" + row.failureReason);
            }
            //entry.displayRows();
        }
    }

    private List<Row> removeFailedEntries(Entry entry, List<Row> failures){
        for (int i = entry.rows.size(); i >= 1; i--){
            Row currentRow = entry.rows.get(i - 1);
            if (!currentRow.valid){
                currentRow.failureReason = "Invalid column size, has " + currentRow.row.length + " columns.";
                failures.add(currentRow);
                entry.rows.remove(currentRow);
            }
        }
        return failures;
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

    private void validateColCount(Entry entry){
        for (Row row: entry.rows){
            if (row.row.length != 5){
                row.valid = false;
            }
        }
    }

}
