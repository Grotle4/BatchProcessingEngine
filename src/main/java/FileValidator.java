import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Pattern;

public class FileValidator {

    public void readCsvFiles(List<Path> files){
        List<Entry> entries = getRows(files);
        List<Row> failedRows = new ArrayList<>();

        for (Entry entry:entries){
            entry.valid = validateHeader(entry);

            if (entry.valid){
                validateColCount(entry);
                removeFailedEntries(entry, failedRows);
                validateColumns(entry, failedRows);

                entry.displayRows();
            }

            else {
                //TODO: append to list of entries that will be written as invalid due to invalid structure.
                System.out.println("File has invalid header structure, skipping: " + entry.originalFile);
            }
        }
        validateDuplicateId(entries, failedRows);
        for (Row row:failedRows){
            System.out.println(Arrays.toString(row.row) +":"+row.valid + ":" + row.failureReason);
        }
    }

    private void removeFailedEntries(Entry entry, List<Row> failures){
        for (int i = entry.rows.size(); i >= 1; i--){
            Row currentRow = entry.rows.get(i - 1);
            if (!currentRow.valid){
                System.out.println("found a failure: " + Arrays.toString(currentRow.row));
                failures.add(currentRow);
                entry.rows.remove(currentRow);
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
        try {
            Row header = entry.rows.getFirst();
            return Arrays.equals(header.row, validHeader);
        }
        catch (NoSuchElementException e){
            System.out.println("ERROR: CSV file " + entry.originalFile + " is empty");
            return false;
        }
    }

    private void validateColCount(Entry entry){
        for (Row row:entry.rows){
            if (row.row.length != 5){
                row.failureReason = "Invalid column size, has " + row.row.length + " columns.";
                row.valid = false;
            }
        }
    }

    private void validateColumns(Entry entry, List<Row> failedRows){
        for (int i = entry.rows.size() - 1; i >= 1; i--){
            Row currentRow = entry.rows.get(i);
            for(int j = 0; j <= currentRow.row.length; j++){
                validateInt(j,currentRow);
                validateNames(j, currentRow);
                validateEmail(j, currentRow);
            }
            removeFailedEntries(entry, failedRows);
        }
    }

    private void validateInt(int idx, Row row) {
        try {
            if (idx == 0) {
                Integer.parseInt(row.row[idx]);
            }
        }
        catch (NumberFormatException e) {
            row.valid = false;
            row.failureReason = "Unique ID column is not a sequence of whole numbers.";
        }
    }

    private void validateNames(int idx, Row row) {//Do not omit '-.
        if (idx == 1 || idx == 2) {
            System.out.println("Testing: " + row.row[idx]);
            List<Character> invalidChars = List.of('!','@', '#', '$', '%', '^', '&', '*', '(', ')');
            Set<Character> invalidSet = new HashSet<>(invalidChars);

            // Check if we have any symbols in our names
            boolean isInvalidChars = row.row[idx].chars()
                            .anyMatch(c -> invalidSet.contains((char) c));

            boolean isInvalidDigits = row.row[idx].matches(".*\\d.*"); // Regex check to see if we have any numbers in our name

            System.out.println(isInvalidChars + " " + isInvalidDigits);

            if (isInvalidChars || isInvalidDigits ){
                row.valid = false;
                if (idx == 1){
                    row.failureReason = "Invalid character found in first name, cannot contain any symbols or numbers except \"-\", \"'\" or \".\".";
                }
                else {
                    row.failureReason = "Invalid character found in last name, cannot contain any symbols or numbers except \"-\", \"'\" or \".\".";
                }
            }
        }
    }

    private void validateEmail(int idx, Row row){
        if (idx == 3){
            String emailRegex =
                    "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
            Pattern emailPattern = Pattern.compile(emailRegex);

            boolean isValidEmail = emailPattern.matcher(row.row[idx]).matches();
            if (!isValidEmail){
                row.valid = false;
                row.failureReason = "Invalid email structure provided. Should be ####@####.###";
            }
        }
    }

    private void validateDuplicateId(List<Entry> entries, List<Row> failedRows){
        List<String> uniqueIds = new ArrayList<>();

        for (Entry entry:entries){
            if (entry.valid){
                for (int i = 1; i < entry.rows.size(); i++){
                    Row currentRow = entry.rows.get(i);
                    if (!uniqueIds.contains(currentRow.row[0])){
                        uniqueIds.add(currentRow.row[0]);
                    }
                    else{
                        currentRow.valid = false;
                        currentRow.failureReason = "Duplicate unique id found in file: " + entry.originalFile;
                    }
                }
                removeFailedEntries(entry, failedRows);
                entry.displayRows();
            }
        }
    }
}
