public class TestingHelper {

    public void DisplayTestResults(Object expected, Object actual){
        String methodName = StackWalker.getInstance()
                .walk(frames -> frames
                        .skip(1) // Skip logCaller() itself
                        .findFirst()
                        .map(StackWalker.StackFrame::getMethodName)
                        .orElse("Unknown"));

        System.out.println("TESTING RESULTS FOR " + methodName);
        System.out.println("ACTUAL: " + actual);
        System.out.println("EXPECTED: " + expected + "\n");
    }

    public void DisplayTestResults(Object expected, Object actual, String comment){
        String methodName = StackWalker.getInstance()
                .walk(frames -> frames
                        .skip(1) // Skip logCaller() itself
                        .findFirst()
                        .map(StackWalker.StackFrame::getMethodName)
                        .orElse("Unknown"));

        System.out.println("TESTING RESULTS FOR " + methodName);
        System.out.println(comment);
        System.out.println("ACTUAL: " + actual);
        System.out.println("EXPECTED: " + expected + "\n");
    }

}
