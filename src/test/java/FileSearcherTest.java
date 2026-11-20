import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FileSearcherTest {

    private FileSearcher searcher;

    @BeforeEach
    void setUp(){
        searcher = new FileSearcher("src/test/resources");
    }

    @Test
    void testFindingPath(){
        String cwd = System.getProperty("user.dir");
        List<Path> result = searcher.findCsvFiles();
        List<Path> expected = new ArrayList<>();
        expected.add(Path.of("src\\test\\resources\\test.csv"));
        expected.add(Path.of("src\\test\\resources\\test2.csv"));
        assertEquals(expected, result, "Path should find the test csv.");
    }

    @Test
    void checkNullPath(){
        FileSearcher fakeSearcher = new FileSearcher("src/test");

        List<Path> result = fakeSearcher.findCsvFiles();
        List<Path> expected = new ArrayList<>();
        assertEquals(expected, result, "Path should return empty array list");
    }

    @Test
    void checkIncorrectPath(){
        FileSearcher fakeSearcher = new FileSearcher("src/tester");

        List<Path> result = fakeSearcher.findCsvFiles();
        List<Path> expected = new ArrayList<>();
        assertEquals(expected, result, "Path should return empty array list");
    }
}
