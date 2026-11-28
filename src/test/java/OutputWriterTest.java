import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OutputWriterTest {

    private Instant timestamp = Instant.now();
    private String reportTimestamp = generateTimestamp();

    private String generateTimestamp(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy hh:mm:ss a")
                .withZone(ZoneId.systemDefault());
        return formatter.format(timestamp);
    }

    private List<Path> getFile(Path startPath, String extension){
        try (Stream<Path> stream = Files.walk(startPath, 1)){
            return stream
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().toLowerCase().endsWith("." + extension))
                    .collect(Collectors.toList())
                    .stream().sorted(Comparator.comparing(
                            path -> path.getFileName().toString(),
                            String.CASE_INSENSITIVE_ORDER
                    )).toList();
        }
        catch (NoSuchFileException e){
            return new ArrayList<>();
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    private List<String> getLines(Path path){
        try{
            return Files.readAllLines(path);
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    private FileSearcher searcher;
    private FileValidator validator;
    private ProcessingResult processor;
    private OutputWriter writer;
    private TestingHelper helper;
    ProcessingResult.compiledResults results;
    FileValidator.completedEntries entries;

    @TempDir
    Path tempPassed;
    @TempDir
    Path tempFailed;
    @TempDir
    Path tempInvalid;
    @TempDir
    Path tempSummary;
    @TempDir
    Path tempOutput;

    @BeforeEach
    void setUp(){
        searcher = new FileSearcher("src/test/resources");
        processor = new ProcessingResult();
        validator = new FileValidator();
        writer = new OutputWriter();
        helper = new TestingHelper();

        List<Path> files = searcher.findCsvFiles();
        entries = validator.readCsvFiles(files);

        results = processor.processResult(entries);
    }

    @Test
    void generatePassedRecordCSV(){
        writer.writeCSVFiles(results,
                tempPassed,
                tempFailed
        );

        List<Path> paths = getFile(tempPassed, "csv");
        List<String> lines = getLines(paths.getFirst());
        List<String> comparison = List.of(
                "Customer ID,First Name,Last Name,Email,Account Balance",
                "1030,test,user,test@gmail.com,405.00",
                "1032,test,user,test@gmail.com,405.00");
        helper.DisplayTestResults(comparison, lines);
        assertEquals(comparison, lines);
    }

    @Test
    void generateFailedRecordCSV(){
        writer.writeCSVFiles(results,
                tempPassed,
                tempFailed
        );

        List<Path> paths = getFile(tempFailed, "csv");
        List<String> lines = getLines(paths.getFirst());
        List<String> comparison = List.of(
                "Customer ID,First Name,Last Name,Email,Account Balance,Failure Reasons",
                "1031,test1,user,test@gmail.com,405.00,Invalid character found in first name, cannot contain any symbols or numbers except \"-\", \"'\" or \".\".",
                ",Row is empty.",
                ",Row is empty.",
                "1030,test,user,test@gmail.com,405.00,Duplicate unique id found in prior entry.",
                "1031,test1,user,test@gmail.com,405.00,Invalid character found in first name, cannot contain any symbols or numbers except \"-\", \"'\" or \".\".",
                "1032,test,user,test@gmail.com,Invalid column size, has 4 columns.",
                "1033,test,user,test@gmail.com,405.00,test,Invalid column size, has 6 columns.",
                "1034,test,user,test@gmail.com,405.00,,Invalid column size, has 6 columns.",
                "test,test,user,test@gmail.com,405.00,Unique ID column is not a sequence of whole numbers.",
                "1035,test,user,test@gmail.com,test,Account balance is not a valid number.",
                "1030,test,user,test@gmail.com,405.00,Duplicate unique id found in prior entry.");
        helper.DisplayTestResults(comparison, lines);
        assertEquals(comparison, lines);
    }
    @Test
    void generateInvalidRecordCSV(){
        writer.writeInvalidFilesCSV(entries.invalidEntries(),tempInvalid);

        List<Path> paths = getFile(tempInvalid, "csv");
        List<String> lines = getLines(paths.getFirst());
        List<String> comparison = List.of(
                "Filename,Failure Reasons",
                "src\\test\\resources\\empty.csv,File is blank.",
                "src\\test\\resources\\headerOnly.csv,No content present in file besides header.",
                "src\\test\\resources\\test2.csv,File has invalid header structure");
        helper.DisplayTestResults(comparison, lines);
        assertEquals(comparison, lines);
    }
    @Test
    void generateSummaryReport(){
        writer.writeSummaryReport(entries,results,tempSummary);

        List<Path> paths = getFile(tempSummary, "txt");
        List<String> lines = getLines(paths.getFirst());
        List<String> comparison = List.of(
                "REPORT FOR " + reportTimestamp.toUpperCase(),
                "",
                "Total found files: 6",
                "Total successful files: 3",
                "Total rejected files: 3",
                "",
                "Total records found: 13",
                "Total successful records: 2",
                "Total failed records: 11",
                "",
                "Status: Completed with errors");
        helper.DisplayTestResults(comparison, lines);
        assertEquals(comparison, lines);
    }

    @Test
    void createMissingDirectories(){
        Path rootPath = Paths.get(tempOutput.toString());
        writer.validateDirectories(tempOutput);
        List<Path> subDirs = new ArrayList<>();

        try (Stream<Path> stream = Files.walk(rootPath)){
            subDirs = stream
                    .skip(1)
                    .filter(Files::isDirectory)
                    .toList();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }

        List<Path> expectedPaths = List.of(
                Path.of(tempOutput.toString(), "failed"),
                Path.of(tempOutput.toString(), "invalid"),
                Path.of(tempOutput.toString(), "passed"),
                Path.of(tempOutput.toString(), "summary"));

        helper.DisplayTestResults(expectedPaths, subDirs);
        assertEquals(expectedPaths, subDirs);

    }


}
