import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileValidatorTest {
    private FileValidator validator;

    @BeforeEach
    void setUp(){
        validator = new FileValidator();
    }

    @Test
    void validateCsvProcessing(){
        Path testPath = Path.of("src/test/resources/test.csv");

        Row content = new Row(new String[]{"1030", "test", "user", "test@gmail.com", "405"});
        List<Path> testPaths = List.of(testPath);
        List<Row> expectedRows = List.of(content);
        Entry expected = new Entry(expectedRows, true, testPath);
        List<Entry> expectedEntries = new ArrayList<>();
        expectedEntries.add(expected);

        List<Entry> entries = validator.readCsvFiles(testPaths);

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


        for (Entry given:entries){
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

            for (Row baselineRow:baseline.getRows()){
                comparedRow baselineCompareRow = new comparedRow(
                        Arrays.toString(baselineRow.getRow()),
                        baselineRow.getFailureReasons(),
                        2
                );
                baselineRows.add(baselineCompareRow);
            }

        }

        assertEquals(baselineEntries,givenEntries);
        assertEquals(baselineRows,givenRows);
    }

}
