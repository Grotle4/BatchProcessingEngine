import java.nio.file.Path;

public class Row {
    public String[] row;
    public boolean valid;
    public String failureReason = "";

    public Row(String[] Row, boolean Valid){
        this.row = Row;
        this.valid = Valid;
    }
}
