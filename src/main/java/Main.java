import java.io.File;
import java.nio.file.Path;
import java.util.List;

public class Main {
    public static void main(String[] args){
        InputParser parser = new InputParser();

        String dir = parser.getUserInput();
        System.out.println(dir);

        FileSearcher searcher = new FileSearcher(dir);
        List<Path> files = searcher.findCsvFiles();
    }
}
