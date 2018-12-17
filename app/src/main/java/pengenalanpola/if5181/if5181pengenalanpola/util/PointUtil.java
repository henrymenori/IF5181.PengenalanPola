package if5181.finalproject.util;

public class PointUtil {

    public static double getDistance(int a, int b, int width) {
        int x = Math.abs(b % width - a % width);
        int y = Math.abs(b / width - a / width);

        return Math.sqrt(x * x + y * y);
    }

}
