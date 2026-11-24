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
        try{
            BigDecimal balance = new BigDecimal(row[4].trim());
            BigDecimal rounded = balance.setScale(2, RoundingMode.HALF_UP);
            String roundedStr = rounded.toPlainString();
            row[4] = roundedStr;
        }
        catch (NumberFormatException | ArrayIndexOutOfBoundsException e){

        }
    }
}
