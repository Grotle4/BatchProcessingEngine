import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.text.Normalizer;
import java.util.*;
import java.util.regex.Pattern;

public class FileValidator {

    public record completedEntries(
            List<Entry> validEntries,
            List<Entry> invalidEntries
    ) {}


    public completedEntries readCsvFiles(List<Path> files){
        List<Entry> entries = getRows(files);
        List<Entry> invalidEntries = new ArrayList<>();
        List<Entry> validEntries = new ArrayList<>();

        for (Entry entry:entries){
            entry.updateHeaderValidity(validateHeader(entry));

            if (entry.getHeaderValidity()){
                entry.removeHeader();
                if (!entry.getRows().isEmpty()){
                    validateColCount(entry);
                    validateColumns(entry);
                }
                else{
                    invalidEntries.add(entry);
                    entry.assignHeaderFailure("No content present in file besides header.");
                }
            }
            else {
                invalidEntries.add(entry);
            }
        }

        validateDuplicateId(entries);

        for (Entry entry:entries){
            entry.displayRows();
            if (entry.getHeaderValidity()){
                validEntries.add(entry);
            }
        }

        completedEntries complete = new completedEntries(
                validEntries,
                invalidEntries
                );


        return complete;
    }


    private List<Entry> getRows(List<Path> files){

        List<Entry> entries = new ArrayList<>();

        String delim = "\\|";
        String line;

        for (Path file : files){
            String filepath = file.toString();
            List<Row> rows = new ArrayList<>();
            int rowNum = 1;

            try (BufferedReader br = new BufferedReader(new FileReader(filepath))){
                while ((line = br.readLine()) != null){
                    String[] row = line.split(delim);
                    Row entryRow = new Row(row);
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
            boolean isMatching = Arrays.equals(header.getRow(), validHeader);
            if (!isMatching){
                entry.assignHeaderFailure("File has invalid header structure");
                System.out.println("File has invalid header structure, skipping: " + entry.getOriginalFile());
            }

            return isMatching;
        }
        catch (NoSuchElementException e){
            System.out.println("ERROR: CSV file " + entry.getOriginalFile() + " is empty");
            entry.assignHeaderFailure("File is blank.");
            return false;
        }
    }

    private void validateColCount(Entry entry){
        for (Row row:entry.getRows()){
            if (row.getRow().length != 5){
                row.addFailureReason("Invalid column size, has " + row.getRow().length + " columns.");
            }
        }
    }

    private void validateColumns(Entry entry){
        for (int i = entry.getRows().size() - 1; i >= 0; i--){
            Row currentRow = entry.getRows().get(i);
            if (currentRow.getRow().length == 5){
                validateInt(currentRow);
                validateNames(1, currentRow);
                validateNames(2, currentRow);
                validateEmail(currentRow);
                validateBalance(currentRow);
            }
        }
    }

    private void validateInt(Row row) {
        try {
            Integer.parseInt(row.getRow()[0]);
        }
        catch (NumberFormatException e) {
            row.addFailureReason("Unique ID column is not a sequence of whole numbers.");
        }
    }

    private void validateNames(int idx, Row row) {
        Pattern allLetters = Pattern.compile("^[\\p{L}&&\\p{IsLatin}]+$");

        String normalized = Normalizer.normalize(row.getRow()[idx], Normalizer.Form.NFC);
        boolean isInvalidChars = !allLetters.matcher(normalized).matches();

        boolean isInvalidDigits = row.getRow()[idx].matches(".*\\d.*"); // Regex check to see if we have any numbers in our name

            if (isInvalidChars || isInvalidDigits){
                if (idx == 1){
                    row.addFailureReason("Invalid character found in first name, cannot contain any symbols or numbers except \"-\", \"'\" or \".\".");
                }
                else {
                    row.addFailureReason("Invalid character found in last name, cannot contain any symbols or numbers except \"-\", \"'\" or \".\".");
                }
            }
        }


    private void validateEmail(Row row){
        String emailRegex =
                "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern emailPattern = Pattern.compile(emailRegex);

        boolean isValidEmail = emailPattern.matcher(row.getRow()[3]).matches();
        if (!isValidEmail){
            row.addFailureReason("Invalid email structure provided. Should be ####@####.###");
        }

    }

    private void validateDuplicateId(List<Entry> entries){
        Set<String> uniqueIds = new HashSet<>();

        for (Entry entry:entries){
            for (int i = 0; i < entry.getRows().size(); i++){
                Row currentRow = entry.getRows().get(i);
                if (currentRow.getValidity()) {
                    if (!uniqueIds.add(currentRow.getRow()[0])){
                        currentRow.addFailureReason("Duplicate unique id found in prior entry.");
                    }
                }

            }
                //entry.displayRows();
        }
    }

    private void validateBalance(Row row){
        try{
            new BigDecimal(row.getRow()[4].trim());
        }
        catch (NumberFormatException e){
            row.addFailureReason("Account balance is not a valid number.");
        }
    }
}
