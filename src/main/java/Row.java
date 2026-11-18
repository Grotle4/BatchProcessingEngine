import java.nio.file.Path;

public class Row {
    private String[] row;
    private boolean valid;
    private String failureReason = "";
    private int rowNumber;

    public Row(String[] Row, boolean Valid){
        this.row = Row;
        this.valid = Valid;
    }

    public void assignRowNum(int num){
        this.rowNumber = num;
    }

    public String[] getRow(){
        return row.clone();
    }

    public boolean getValidity(){
        return valid;
    }

    public String getFailureReason(){
        return failureReason;
    }

    public int getRowNumber(){
        return rowNumber;
    }

    public void setFailureReason(String reason){
        failureReason = reason;
    }

    public void setValidity(boolean val){
        valid = val;
    }

}
