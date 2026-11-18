import java.util.ArrayList;
import java.util.List;

public class Row {
    private String[] row;
    private List<String> failureReasons = new ArrayList<>();
    private int rowNumber;

    public Row(String[] Row){
        this.row = Row;
    }

    public void assignRowNum(int num){
        this.rowNumber = num;
    }

    public String[] getRow(){
        return row.clone();
    }

    public boolean getValidity(){
        return failureReasons.isEmpty();
    }

    public List<String> getFailureReasons(){
        return failureReasons;
    }

    public int getRowNumber(){
        return rowNumber;
    }

    public void addFailureReason(String reason){
        failureReasons.add(reason);
    }

    public boolean checkColCount(){
        return failureReasons.stream()
                .anyMatch(reason -> reason.contains("Invalid column size"));
    }

}
