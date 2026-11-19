import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class Row {
    private String[] row;
    private List<String> failureReasons = new ArrayList<>();
    private int rowNumber;

    public Row(String[] Row){
        this.row = Row.clone();
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
        return List.copyOf(failureReasons);
    }

    public int getRowNumber(){
        return rowNumber;
    }

    public void addFailureReason(String reason){
        failureReasons.add(reason);
    }

    public void roundBalance(){
        for (int i = 1; i < row.length; i++){
            try{
                BigDecimal balance = new BigDecimal(row[i].trim());
                BigDecimal rounded = balance.setScale(2, RoundingMode.DOWN);
                String roundedStr = rounded.toString();
                row[i] = roundedStr;
            }
            catch (NumberFormatException e){

            }
        }
    }
}
