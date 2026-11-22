import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class OutputWriter {

    public void writeCSVFiles(ProcessingResult.compiledResults results){

        Path passedCsv = Path.of("output\\passed\\passed.csv");
        Path failedCsv = Path.of("output\\failed\\failed.csv");


        try (BufferedWriter writer = Files.newBufferedWriter(passedCsv, StandardCharsets.UTF_8)){
            String[] header = {"Customer ID", "First Name", "Last Name", "Email", "Account Balance"};
            writer.write(String.join(",", header));
            writer.newLine();
            for (Row row:results.passedRows()){
                String[] writtenRow = row.getRow();

                writer.write(String.join(",", writtenRow));
                writer.newLine();
            }
        }
        catch (IOException e) {
                e.printStackTrace();
        }

        try (BufferedWriter writer = Files.newBufferedWriter(failedCsv, StandardCharsets.UTF_8)){
            String[] header = {"Customer ID", "First Name", "Last Name", "Email", "Account Balance","Failure Reasons"};
            writer.write(String.join(",", header));
            writer.newLine();
            for (Row row:results.failedRows()){
                String[] currentRow = row.getRow();
                String[] failures = row.getFailureReasons().toArray(new String[0]);


                String[] combined = Stream.concat(Arrays.stream(currentRow), Arrays.stream(failures))
                                .toArray(String[]::new);

                writer.write(String.join(",", combined));
                writer.newLine();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }






    }


}
