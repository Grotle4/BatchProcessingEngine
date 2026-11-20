import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProcessingResult {

    public record compiledResults(
            List<Row> passedRows,
            List<Row> failedRows
    ){}

    public compiledResults processResult(FileValidator.completedEntries entries){

        List<Row> passedRows = new ArrayList<>();
        List<Row> failedRows = new ArrayList<>();

        for (Entry entry:entries.validEntries()){
            for(Row row:entry.getRows()){
                row.roundBalance();
                if (row.getValidity()){
                    passedRows.add(row);
                }
                else {
                    failedRows.add(row);
                }
            }
        }
        System.out.println("PASSED:");
        passedRows.forEach(passedRow -> System.out.println(Arrays.toString(passedRow.getRow())));
        System.out.println("FAILED:");
        failedRows.forEach(failedRow -> System.out.println(Arrays.toString(failedRow.getRow())));
        return new compiledResults(passedRows, failedRows);

    }

}
