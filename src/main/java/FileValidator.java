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
            entry.updateValidity(validateHeader(entry));
            entry.removeHeader();

            if (entry.getValidity()){
                validateColCount(entry);
                validateColumns(entry, failedRows);
                //entry.displayRows();
            }

            else {
                //TODO: append to list of entries that will be written as invalid due to invalid structure.
                System.out.println("File has invalid header structure, skipping: " + entry.getOriginalFile());
            }
        }
        validateDuplicateId(entries, failedRows);
    }

//    private void removeFailedEntries(Entry entry, List<Row> failures){
//        for (int i = entry.rows.size(); i >= 1; i--){
//            Row currentRow = entry.rows.get(i - 1);
//            if (!currentRow.valid){
//                System.out.println("found a failure: " + Arrays.toString(currentRow.row));
//                failures.add(currentRow);
//                entry.rows.remove(currentRow);
//            }
//        }
//    }

    private List<Entry> getRows(List<Path> files){

        List<Entry> entries = new ArrayList<>();

        String delim = ",";
        String line = "";

        for (Path file : files){
            String filepath = file.toString();
            List<Row> rows = new ArrayList<>();
            int rowNum = 0;

            try (BufferedReader br = new BufferedReader(new FileReader(filepath))){
                while ((line = br.readLine()) != null){
                    String[] row = line.split(delim);
                    Row entryRow = new Row(row, true);
                    entryRow.assignRowNum(rowNum);
                    rows.add(entryRow);
                    rowNum++;
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
            Row header = entry.getHeader();
            return Arrays.equals(header.getRow(), validHeader);
        }
        catch (NoSuchElementException e){
            System.out.println("ERROR: CSV file " + entry.getOriginalFile() + " is empty");

            return false;
        }
    }

    private void validateColCount(Entry entry){
        for (Row row:entry.getRows()){
            if (row.getRow().length != 5){
                row.setFailureReason("Invalid column size, has " + row.getRow().length + " columns.");
                row.setValidity(false);
            }
        }
    }

    private void validateColumns(Entry entry, List<Row> failedRows){
        for (int i = entry.getRows().size() - 1; i >= 0; i--){
            Row currentRow = entry.getRows().get(i);
            for(int j = 0; j <= currentRow.getRow().length; j++){
                validateInt(j,currentRow);
                validateNames(j, currentRow);
                validateEmail(j, currentRow);
            }

        }
    }

    private void validateInt(int idx, Row row) {
        try {
            if (idx == 0) {
                Integer.parseInt(row.getRow()[idx]);
            }
        }
        catch (NumberFormatException e) {
            row.setValidity(false);
            row.setFailureReason("Unique ID column is not a sequence of whole numbers.");
        }
    }

    private void validateNames(int idx, Row row) {//Do not omit '-.
        if (idx == 1 || idx == 2) {
            List<Character> invalidChars = List.of('!','@', '#', '$', '%', '^', '&', '*', '(', ')');
            Set<Character> invalidSet = new HashSet<>(invalidChars);

            // Check if we have any symbols in our names
            boolean isInvalidChars = row.getRow()[idx].chars()
                            .anyMatch(c -> invalidSet.contains((char) c));

            boolean isInvalidDigits = row.getRow()[idx].matches(".*\\d.*"); // Regex check to see if we have any numbers in our name

            if (isInvalidChars || isInvalidDigits ){
                row.setValidity(false);
                if (idx == 1){
                    row.setFailureReason("Invalid character found in first name, cannot contain any symbols or numbers except \"-\", \"'\" or \".\".");
                }
                else {
                    row.setFailureReason("Invalid character found in last name, cannot contain any symbols or numbers except \"-\", \"'\" or \".\".");
                }
            }
        }
    }

    private void validateEmail(int idx, Row row){
        if (idx == 3){
            String emailRegex =
                    "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
            Pattern emailPattern = Pattern.compile(emailRegex);

            boolean isValidEmail = emailPattern.matcher(row.getRow()[idx]).matches();
            if (!isValidEmail){
                row.setValidity(false);
                row.setFailureReason("Invalid email structure provided. Should be ####@####.###");
            }
        }
    }

    private void validateDuplicateId(List<Entry> entries, List<Row> failedRows){
        List<String> uniqueIds = new ArrayList<>();

        for (Entry entry:entries){
            if (entry.getValidity()){
                for (int i = 0; i < entry.getRows().size(); i++){
                    Row currentRow = entry.getRows().get(i);
                    if (!uniqueIds.contains(currentRow.getRow()[0])){
                        uniqueIds.add(currentRow.getRow()[0]);
                    }
                    else{
                        currentRow.setValidity(false);
                        currentRow.setFailureReason("Duplicate unique id found in file: " + entry.getOriginalFile());
                    }
                }
                entry.displayRows();
            }
        }
    }
}
