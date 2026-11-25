import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FileSearcherTest {

    private FileSearcher searcher;
    private TestingHelper helper;

    @BeforeEach
    void setUp(){
        searcher = new FileSearcher("src/test/resources");
        helper = new TestingHelper();
    }

    @Test
    void testFindingPath(){
        List<Path> result = searcher.findCsvFiles();
        List<Path> expected = new ArrayList<>();
        expected.add(Path.of("src\\test\\resources\\empty.csv"));
        expected.add(Path.of("src\\test\\resources\\headerOnly.csv"));
        expected.add(Path.of("src\\test\\resources\\test.csv"));
        expected.add(Path.of("src\\test\\resources\\test2.csv"));

        helper.DisplayTestResults(expected, result);
        assertEquals(expected, result);
    }

    @Test
    void checkNullPath(){
        FileSearcher fakeSearcher = new FileSearcher("src/test");

        List<Path> result = fakeSearcher.findCsvFiles();
        List<Path> expected = new ArrayList<>();
        helper.DisplayTestResults(expected, result);
        assertEquals(expected, result);
    }

    @Test
    void checkIncorrectPath(){
        FileSearcher fakeSearcher = new FileSearcher("src/tester");

        List<Path> result = fakeSearcher.findCsvFiles();
        List<Path> expected = new ArrayList<>();
        helper.DisplayTestResults(expected, result);
        assertEquals(expected, result);
    }

    @Test
    void checkDirectoryWithNoCsvs(){
        FileSearcher fakeSearcher = new FileSearcher("src/test/resources/fakeDirectory");

        List<Path> result = fakeSearcher.findCsvFiles();
        List<Path> expected = new ArrayList<>();
        helper.DisplayTestResults(expected, result);
        assertEquals(expected, result);
    }

    @Test
    void checkForAlphabeticalSorting(){
        List<Path> expected = new ArrayList<>();
        expected.add(Path.of("src\\test\\resources\\empty.csv"));
        expected.add(Path.of("src\\test\\resources\\headerOnly.csv"));
        expected.add(Path.of("src\\test\\resources\\test.csv"));
        expected.add(Path.of("src\\test\\resources\\test2.csv"));
        List<Path> result = searcher.findCsvFiles();
        helper.DisplayTestResults(expected, result);
        assertEquals(expected,result);
    }


}
