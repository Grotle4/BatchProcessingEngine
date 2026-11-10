import javax.swing.*;
import java.awt.*;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class InputParser {
    Scanner scanner = new Scanner(System.in);

    public String getUserInput(){
        System.out.println("Please select a directory where you are storing your report csvs.");
        sleep(2);
        String dir = getDirectory();
        return dir;
    }

    private String getDirectory(){
        JFrame parentFrame = new JFrame();
        parentFrame.setAlwaysOnTop(true);
        parentFrame.setVisible(false);

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int result = fileChooser.showOpenDialog(parentFrame);

        if (result == JFileChooser.APPROVE_OPTION){
            return fileChooser.getSelectedFile().getAbsolutePath();
        }
        else {
            System.out.println("Directory selected was not a valid directory, try again.");
        }
        parentFrame.dispose();
        return "";
    }

    private void sleep(int seconds){
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

}
