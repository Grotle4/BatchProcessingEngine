import java.nio.file.Path;
import java.util.List;

public class Main {
    public static void main(String[] args){
        InputParser parser = new InputParser();

        String dir = parser.getUserInput();
        FileSearcher searcher = new FileSearcher(dir);
        FileValidator validator = new FileValidator();
        ProcessingResult processor = new ProcessingResult();

        List<Path> files = searcher.findCsvFiles();
        FileValidator.completedEntries entries = validator.readCsvFiles(files);

        ProcessingResult.compiledResults results = processor.processResult(entries);

    }
}
