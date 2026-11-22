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


    public void writeInvalidFilesCSV(List<Entry> entries){
        if (!entries.isEmpty()){
            Path invalidFilesCsv = Path.of("output\\invalid\\invalid.csv");
            try (BufferedWriter writer = Files.newBufferedWriter(invalidFilesCsv, StandardCharsets.UTF_8)) {
                String[] header = {"Filename", "Failure Reasons"};
                writer.write(String.join(",", header));
                writer.newLine();
                for (Entry entry:entries) {
                    String[] csvRow = {entry.getOriginalFile().toString(), entry.getHeaderFailureReason()};

                    writer.write(String.join(",", csvRow));
                    writer.newLine();
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public enum Status {
        COMPLETE("Complete"), COMPLETE_WITH_ERRORS("Completed with errors"), ERROR("Error"), NO_FILES_FOUND("No files found");

        private final String statusMessage;

        Status(String statusMessage) {

            this.statusMessage = statusMessage;
        }
    }

    public void writeSummaryReport(FileValidator.completedEntries entries, ProcessingResult.compiledResults results){
        Path summaryPath = Path.of("output\\summary\\summary.txt");
        int successfulFiles = entries.validEntries().size();
        int failedFiles = entries.invalidEntries().size();
        int totalFiles =  successfulFiles + failedFiles;
        int totalRecords = results.failedRows().size() + results.passedRows().size();

        try (BufferedWriter writer = Files.newBufferedWriter(summaryPath, StandardCharsets.UTF_8)){
            String[] content = {
                    "REPORT FOR DATE",
                    "",
                    "Total found files: " + totalFiles,
                    "Total successful files: " + successfulFiles,
                    "Total rejected files: " + failedFiles,
                    "",
                    "Total records found: " + totalRecords,
                    "Total successful records: " + results.passedRows().size(),
                    "Total failed records: " + results.failedRows().size(),
                    "",
                    "Status: " + getReportStatus(successfulFiles, failedFiles, results)
            };
            for (String row:content){
                writer.write(row);
                writer.newLine();
            }
        }


        catch (IOException e){
            e.printStackTrace();
        }
    }

    private String getReportStatus(int passedFiles, int failedFiles, ProcessingResult.compiledResults results){
        int passedRows = results.passedRows().size();
        int failedRows = results.failedRows().size();

        if (passedFiles == 0 && failedFiles > 0){
            return Status.ERROR.statusMessage;
        }
        else if (passedFiles > 0 && failedFiles == 0){
            if (passedRows == 0 && failedRows > 0){
                return Status.COMPLETE_WITH_ERRORS.statusMessage;
            }
            else if (passedRows > 0 && failedRows == 0){
                return Status.COMPLETE.statusMessage;
            }
            else if (passedRows > 0){
                return Status.COMPLETE_WITH_ERRORS.statusMessage;
            }
        }
        else{
            return Status.COMPLETE_WITH_ERRORS.statusMessage;
        }
        return Status.NO_FILES_FOUND.statusMessage;
    }

}
