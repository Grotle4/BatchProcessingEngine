import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ProcessingResultTest {

    private ProcessingResult processor;
    private TestingHelper helper;

    @BeforeEach
    void setUp(){
        processor = new ProcessingResult();
        helper = new TestingHelper();
    }

    @Test
    void testCompiledResults(){
        Path testPath = Path.of("src/test/resources/test.csv");
        Row content = new Row(new String[]{"1030", "test", "user", "test@gmail.com", "405"});
        Row content2 = new Row(new String[]{"1031", "test1", "user", "test@gmail.com", "405"});
        content2.addFailureReason("Invalid character found in first name, cannot contain any symbols or numbers except \"-\", \"'\" or \".\".");
        List<Row> expectedRows = List.of(content, content2);
        Entry entry = new Entry(expectedRows, true, testPath);
        List<Entry> entries = new ArrayList<>();
        entries.add(entry);
        List<Entry> failedEntries = new ArrayList<>();
        FileValidator.completedEntries complete = new FileValidator.completedEntries(
                entries,
                failedEntries
        );


        ProcessingResult.compiledResults results = processor.processResult(complete);

        List<Row> baselinePassedRows = List.of(content);
        List<Row> baselineFailedRows = List.of(content2);

        ProcessingResult.compiledResults baselineResults = new ProcessingResult.compiledResults(baselinePassedRows, baselineFailedRows);

        helper.DisplayTestResults(baselineResults, results);
        assertEquals(baselineResults, results);
    }


}
