package if5181.finalproject.util;

public class VectorUtil {

    public static int dotProduct(int[] a, int[] b) {
        int s = 0;

        if (a.length != b.length) return 0;

        for (int i = 0; i < a.length; i++) {
            s = s + a[i] * b[i];
        }

        return s;
    }

    public static double similarity(double[] a, double[] b) {
        double s = 0;
        double la = 0;
        double lb = 0;

        if (a.length != b.length) return 0;

        for (int i = 0; i < a.length; i++) {
            s = s + a[i] * b[i];
            la = la + a[i] * a[i];
            lb = lb + b[i] * b[i];
        }

        return s / Math.sqrt(la * lb);
    }

    public static double similarity(int[] a, int[] b, int start, int finish) {
        double s = 1;
        double la = 1;
        double lb = 1;

        if (a.length != b.length) return 0;

        for (int i = start; i < finish; i++) {
            s = s + a[i] * b[i];
            la = la + a[i] * a[i];
            lb = lb + b[i] * b[i];
        }

        return s / Math.sqrt(la * lb);
    }

}
