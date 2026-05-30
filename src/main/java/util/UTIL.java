package util;

import java.io.PrintWriter;
import java.time.LocalDateTime;

// Miscellaneous static methods
public class UTIL {

    // Print date and time.
    // PR - PrintWriter for listing file
    public static void printDate(PrintWriter PR) {

        LocalDateTime now = LocalDateTime.now();

        PR.printf("Date: %d-%02d-%02d  Time: %02d:%02d:%02d\n",
            now.getYear(), now.getMonthValue(), now.getDayOfMonth(),
            now.getHour(), now.getMinute(), now.getSecond());
    }

    // Print error message and throw runtime exception.
    // message - error message that is printed.
    public static void errorMsg(String message) {
        throw new FeaException(message);
    }

    // Transform text direction into integer.
    // s - direction x/y/z/n.
    // returns  integer direction 1/2/3/0, error: -1.
    public static int direction(String s) {
        if      (s.equals("x")) return 1;
        else if (s.equals("y")) return 2;
        else if (s.equals("z")) return 3;
        else if (s.equals("n")) return 0;
        else return -1;
    }

}
