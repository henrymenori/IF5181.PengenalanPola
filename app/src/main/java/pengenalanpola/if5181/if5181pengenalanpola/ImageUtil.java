package pengenalanpola.if5181.if5181pengenalanpola;

import android.graphics.Bitmap;
import android.graphics.Color;

public class ImageUtil {

    public static final int RED = 0;
    public static final int GREEN = 1;
    public static final int BLUE = 2;
    public static final int GRAYSCALE = 3;

    public static int[] getPixelColor(Bitmap bitmap, int x, int y) {
        int pixel, red, green, blue, grayscale;

        pixel = bitmap.getPixel(x, y);
        red = Color.red(pixel);
        green = Color.green(pixel);
        blue = Color.blue(pixel);
        grayscale = (red + green + blue) / 3;

        return new int[]{red, green, blue, grayscale};
    }

    public static int[][] getPixelCount(Bitmap bitmap) {
        int[] color;
        int[][] count = new int[4][256];

        for (int i = 0; i < bitmap.getWidth(); i++) {
            for (int j = 0; j < bitmap.getHeight(); j++) {
                color = getPixelColor(bitmap, i, j);
                count[RED][color[RED]]++;
                count[GREEN][color[GREEN]]++;
                count[BLUE][color[BLUE]]++;
                count[GRAYSCALE][color[GRAYSCALE]]++;
            }
        }

        return count;
    }
}
