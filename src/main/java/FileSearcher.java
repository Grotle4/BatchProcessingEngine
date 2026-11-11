import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileSearcher {
    private String pathStr;

    public FileSearcher(String Path){
        this.pathStr = Path;
    }

    public List<Path> findCsvFiles() {

        Path startPath;
        try{
            startPath = Paths.get(pathStr);
        }
        catch (InvalidPathException e){
            System.out.print("Invalid path found");
            return new ArrayList<>();
        }

        try {
            try (Stream<Path> stream = Files.walk(startPath)){
                List<Path> csvFiles = stream
                        .filter(Files::isRegularFile)
                        .filter(path -> path.toString().toLowerCase().endsWith(".csv"))
                        .collect(Collectors.toList());

                csvFiles.forEach(System.out::println);
                return csvFiles;
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return new ArrayList<>();
    }


}
