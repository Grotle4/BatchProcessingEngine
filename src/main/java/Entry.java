import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Entry {
    private List<Row> rows;
    private boolean headerValid;
    private final Path originalFile;
    private String headerFailureReason;

    public Entry(List<Row> Rows, boolean Valid, Path OriginalFile){
        this.rows = new ArrayList<>(Rows);
        this.headerValid = Valid;
        this.originalFile = OriginalFile;
    }

    public void displayRows(){
        for (Row row:rows){
            System.out.println(row.getRowNumber() + ":" + Arrays.toString(row.getRow()) +":"+row.getValidity()+":"+row.getFailureReasons());
        }
        System.out.println();
    }

    public Row getHeader(){
        return rows.getFirst();
    }

    public List<Row> getRows(){
        return Collections.unmodifiableList(rows);
    }

    public void removeHeader(){
        rows.removeFirst();
    }

    public boolean getHeaderValidity(){
        return headerValid;
    }

    public Path getOriginalFile(){
        return originalFile;
    }

    public void assignHeaderFailure(String reason){
        headerFailureReason = reason;
    }

    public String getHeaderFailureReason(){
        return headerFailureReason;
    }

    public void updateHeaderValidity(boolean val){
        headerValid = val;
    }


}
