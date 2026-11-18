import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Entry {
    private List<Row> rows;
    private boolean valid;
    private final Path originalFile;

    public Entry(List<Row> Rows, boolean Valid, Path OriginalFile){
        this.rows = Rows;
        this.valid = Valid;
        this.originalFile = OriginalFile;
    }

    public void displayRows(){
        for (Row row:rows){
            System.out.println(row.getRowNumber() + ":" + Arrays.toString(row.getRow()) +":"+row.getValidity()+":"+row.getFailureReason());
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

    public boolean getValidity(){
        return valid;
    }

    public Path getOriginalFile(){
        return originalFile;
    }


    public void updateValidity(boolean val){
        valid = val;
    }


}
