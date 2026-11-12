import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

public class Entry {
    public List<Row> rows;
    public boolean valid;
    public Path originalFile;

    public Entry(List<Row> Rows, boolean Valid, Path OriginalFile){
        this.rows = Rows;
        this.valid = Valid;
        this.originalFile = OriginalFile;
    }

    public void displayRows(){
        for (Row row:rows){
            System.out.println(Arrays.toString(row.row) +":"+row.valid);
        }
        System.out.println();
    }


}
