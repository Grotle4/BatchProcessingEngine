import java.nio.file.Path;
import java.util.List;

public class Main {
    public static void main(String[] args){
        InputParser parser = new InputParser();

        String dir = parser.getUserInput();
        FileSearcher searcher = new FileSearcher(dir);
        FileValidator validator = new FileValidator();

        List<Path> files = searcher.findCsvFiles();
        validator.readCsvFiles(files);

    }
}
