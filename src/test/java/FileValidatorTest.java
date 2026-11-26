import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileValidatorTest {
    private FileValidator validator;
    private TestingHelper helper;

    @BeforeEach
    void setUp(){
        validator = new FileValidator();
        helper = new TestingHelper();
    }

    @Test
    void validateCsvProcessing(){
        Path testPath = Path.of("src/test/resources/test.csv");
        Path testPath2 = Path.of("src/test/resources/test2.csv");

        Row content = new Row(new String[]{"1030", "test", "user", "test@gmail.com", "405"});
        Row content2 = new Row(new String[]{"1031", "test1", "user", "test@gmail.com", "405"});
        Row content3 = new Row(new String[]{"1032", "test", "user", "test@gmail.com"});
        Row content4 = new Row(new String[]{"1033", "test", "user", "test@gmail.com", "405", ""});
        content2.addFailureReason("Invalid character found in first name, cannot contain any symbols or numbers except \"-\", \"'\" or \".\".");
        content3.addFailureReason("Invalid column size, has " + content3.getRow().length + " columns.");
        content4.addFailureReason("Invalid column size, has " + content4.getRow().length + " columns.");
        List<Row> expectedRows = List.of(content, content2, content3, content4);
        Entry expected = new Entry(expectedRows, true, testPath);
        List<Entry> expectedEntries = new ArrayList<>();
        expectedEntries.add(expected);

        List<Path> testPaths = List.of(testPath, testPath2);
        FileValidator.completedEntries entries = validator.readCsvFiles(testPaths);

        record comparedEntry(
            boolean headerValid,
            Path originalFile,
            String headerFailureReason
        ){}

        record comparedRow(
                String row,
                List<String> failureReasons,
                int rowNumber
                ){}

        List<comparedEntry> givenEntries = new ArrayList<>();
        List<comparedEntry> baselineEntries = new ArrayList<>();

        List<comparedRow> givenRows = new ArrayList<>();
        List<comparedRow> baselineRows = new ArrayList<>();


        for (Entry given:entries.validEntries()){
            comparedEntry givenEntry = new comparedEntry(
                    given.getHeaderValidity(),
                    given.getOriginalFile(),
                    given.getHeaderFailureReason());
            givenEntries.add(givenEntry);

            for (Row givenRow:given.getRows()){
                comparedRow givenCompareRow = new comparedRow(
                        Arrays.toString(givenRow.getRow()),
                        givenRow.getFailureReasons(),
                        givenRow.getRowNumber()
                );
                givenRows.add(givenCompareRow);
            }
        }

        for (Entry baseline:expectedEntries){
            comparedEntry baselineEntry = new comparedEntry(
                    baseline.getHeaderValidity(),
                    baseline.getOriginalFile(),
                    baseline.getHeaderFailureReason());
            baselineEntries.add(baselineEntry);
            int i = 2;
            for (Row baselineRow:baseline.getRows()){

                comparedRow baselineCompareRow = new comparedRow(
                        Arrays.toString(baselineRow.getRow()),
                        baselineRow.getFailureReasons(),
                        i
                );
                baselineRows.add(baselineCompareRow);
                i++;
            }

        }

        helper.DisplayTestResults(baselineEntries, givenEntries, "Checking to see if our data is the same between entry classes");
        helper.DisplayTestResults(baselineEntries, givenEntries, "Checking to see if our data is the same between row classes");
        helper.DisplayTestResults(baselineEntries, givenEntries, "Checking to see if we are picking up invalid entries properly.");
        assertEquals(baselineEntries,givenEntries);
        assertEquals(baselineRows,givenRows);
        assertEquals(testPath2,entries.invalidEntries().getFirst().getOriginalFile());
    }

    @Test
    void validateEmptyList(){
        List<Path> files = new ArrayList<>();
        List<Entry> emptyList = new ArrayList<>();
        FileValidator.completedEntries entries = validator.readCsvFiles(files);

        helper.DisplayTestResults(emptyList, entries.validEntries());
        assertEquals(emptyList, entries.validEntries());
    }

    @Test
    void validateEmptyCsv(){
        Path testCase = Path.of("src\\test\\resources\\empty.csv");
        List<Row> expectedRows = new ArrayList<>();
        Entry expected = new Entry(expectedRows,false,testCase);
        expected.assignHeaderFailure("File is blank.");
        expected.updateHeaderValidity(false);

        List<Path> testCaseList = List.of(testCase);

        FileValidator.completedEntries entries = validator.readCsvFiles(testCaseList);

        record entryComparison(
            List<Row> rows,
            boolean validity,
            String reason
        ){}

        List<entryComparison> foundList = new ArrayList<>();
        List<entryComparison> expectedList = new ArrayList<>();


        for(Entry entry:entries.invalidEntries()){
            entryComparison foundRecord = new entryComparison(
                    entry.getRows(),
                    entry.getHeaderValidity(),
                    entry.getHeaderFailureReason()
            );
            foundList.add(foundRecord);
        }

        entryComparison expectedEntry = new entryComparison(
                expected.getRows(),
                expected.getHeaderValidity(),
                expected.getHeaderFailureReason()
        );

        expectedList.add(expectedEntry);
        helper.DisplayTestResults(expectedList, foundList);
        assertEquals(expectedList, foundList);

    }

    @Test
    void validateHeaderOnlyCsv(){
        Path testCase = Path.of("src\\test\\resources\\headerOnly.csv");
        List<Path> testCaseList = List.of(testCase);
        FileValidator.completedEntries entries = validator.readCsvFiles(testCaseList);

        String[] content = {"customerId","firstName","lastName","email","accountBalance"};
        Row expectedRow = new Row(content);
        List<Row> expectedRows = List.of(expectedRow);
        Entry expected = new Entry(expectedRows,false,testCase);
        expected.assignHeaderFailure("No content present in file besides header.");
        expected.updateHeaderValidity(false);



        record rowComparison(
            String [] row
        ){}

        record entryComparison(
                List<rowComparison> rows,
                boolean validity,
                String reason
        ){}

        List<entryComparison> foundList = new ArrayList<>();
        List<entryComparison> expectedList = new ArrayList<>();
        List<rowComparison> foundRecordRows = new ArrayList<>();
        List<rowComparison> expectedRecordRows = new ArrayList<>();


        for(Entry entry:entries.invalidEntries()){
            for (Row row:entry.getRows()){
                rowComparison foundRow = new rowComparison(row.getRow());
                foundRecordRows.add(foundRow);
            }
            entryComparison foundRecord = new entryComparison(
                    foundRecordRows,
                    entry.getHeaderValidity(),
                    entry.getHeaderFailureReason()
            );
            foundList.add(foundRecord);
        }

        entryComparison expectedEntry = new entryComparison(
                expectedRecordRows,
                expected.getHeaderValidity(),
                expected.getHeaderFailureReason()
        );

        expectedList.add(expectedEntry);
        helper.DisplayTestResults(expectedList, foundList);
        assertEquals(expectedList, foundList);
    }

    @Test
    void validateMissingColumns(){
        Path testCase = Path.of("src\\test\\resources\\test.csv");
        List<Path> testCaseList = List.of(testCase);
        FileValidator.completedEntries entries = validator.readCsvFiles(testCaseList);

        record rowComparison(
                String content,
                boolean validity,
                String reason
        ){}
        String[] expectedArray = {"1032","test","user","test@gmail.com"};
        String expectedContent = Arrays.toString(expectedArray);
        rowComparison expectedRowCompare = new rowComparison(
                expectedContent,
                false,
                "Invalid column size, has " + expectedArray.length + " columns."
        );

        Entry entry = entries.validEntries().getFirst();
        Row row = entry.getRows().get(2);
        rowComparison foundRowCompare = new rowComparison(
                Arrays.toString(row.getRow()),
                row.getValidity(),
                row.getFailureReasons().getFirst()
        );
        helper.DisplayTestResults(expectedRowCompare, foundRowCompare);
        assertEquals(expectedRowCompare, foundRowCompare);


    }

    @Test
    void validateExtraColumns(){
        Path testCase = Path.of("src\\test\\resources\\test.csv");
        List<Path> testCaseList = List.of(testCase);
        FileValidator.completedEntries entries = validator.readCsvFiles(testCaseList);

        record rowComparison(
                String content,
                boolean validity,
                String reason
        ){}
        String[] expectedArray = {"1033","test","user","test@gmail.com", "405", ""};
        String expectedContent = Arrays.toString(expectedArray);
        rowComparison expectedRowCompare = new rowComparison(
                expectedContent,
                false,
                "Invalid column size, has " + expectedArray.length + " columns."
        );

        Entry entry = entries.validEntries().getFirst();
        Row row = entry.getRows().get(3);
        rowComparison foundRowCompare = new rowComparison(
                Arrays.toString(row.getRow()),
                row.getValidity(),
                row.getFailureReasons().getFirst()
        );
        helper.DisplayTestResults(expectedRowCompare, foundRowCompare);
        assertEquals(expectedRowCompare, foundRowCompare);

    }

    @Test
    void validateTrailingEmptyFields(){

    }

    @Test
    void validateInvalidCustomerId(){

    }

    @Test
    void validateInvalidAccountBalance(){

    }

    @Test
    void validateDuplicateIds(){

    }

    @Test
    void validateInvalidRecordReservingId(){

    }

}
