package pengenalanpola.if5181.if5181pengenalanpola;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

public class ImageUtil {

    public static final int RED = 0;
    public static final int GREEN = 1;
    public static final int BLUE = 2;
    public static final int GRAYSCALE = 3;

    public static Bitmap getGrayscaleImage(Bitmap bitmap) {
        Bitmap result = bitmap.copy(bitmap.getConfig(), true);
        int[] color;

        for (int i = 0; i < bitmap.getWidth(); i++) {
            for (int j = 0; j < bitmap.getHeight(); j++) {
                color = getPixelColor(bitmap, i, j);
                setPixelColor(result, i, j, color[GRAYSCALE], color[GRAYSCALE], color[GRAYSCALE]);
            }
        }

        return result;
    }

    public static Bitmap[] getTransformedImage(Bitmap bitmap) {
        Bitmap resultA = bitmap.copy(bitmap.getConfig(), true);
        Bitmap resultB = bitmap.copy(bitmap.getConfig(), true);
        int sum = bitmap.getWidth() * bitmap.getHeight();
        int[] color;
        int[][] count = getPixelCount(bitmap);
        int[][] lookup = new int[4][256];

        for (int i = 0; i < 256; i++) {
            for (int j = 0; j < 4; j++) {
                if (i > 0)
                    count[j][i] = count[j][i] + count[j][i - 1];

                lookup[j][i] = count[j][i] * 255 / sum;
            }
        }

        for (int i = 0; i < bitmap.getWidth(); i++) {
            for (int j = 0; j < bitmap.getHeight(); j++) {
                color = getPixelColor(bitmap, i, j);
                setPixelColor(resultA, i, j, lookup[RED][color[RED]], lookup[GREEN][color[GREEN]], lookup[BLUE][color[BLUE]]);
                setPixelColor(resultB, i, j, lookup[GRAYSCALE][color[GRAYSCALE]], lookup[GRAYSCALE][color[GRAYSCALE]], lookup[GRAYSCALE][color[GRAYSCALE]]);
            }
        }

        return new Bitmap[]{resultA, resultB};
    }

    public static Bitmap[] getTransformedImage(Bitmap bitmap, int a, int b, int c) {
        Bitmap resultA = bitmap.copy(bitmap.getConfig(), true);
        Bitmap resultB = bitmap.copy(bitmap.getConfig(), true);
        int[] color;
        int[] count = new int[256];
        int[] lookup = new int[256];

        for (int i = 0; i < 256; i++) {
            if (i < b)
                count[i] = a + i * (255 - a) / b;
            else if (i > b)
                count[i] = c + (255 - i) * (255 - c) / (255 - b);
            else
                count[i] = 255;
        }

        for (int i = 1; i < 256; i++) {
            count[i] = count[i] + count[i - 1];
        }

        for (int i = 0; i < 256; i++) {
            lookup[i] = count[i] * 255 / count[255];
        }

        for (int i = 0; i < bitmap.getWidth(); i++) {
            for (int j = 0; j < bitmap.getHeight(); j++) {
                color = getPixelColor(bitmap, i, j);
                setPixelColor(resultA, i, j, lookup[color[RED]], lookup[color[GREEN]], lookup[color[BLUE]]);
                setPixelColor(resultB, i, j, lookup[color[GRAYSCALE]], lookup[color[GRAYSCALE]], lookup[color[GRAYSCALE]]);
            }
        }

        return new Bitmap[]{resultA, resultB};
    }

    public static Bitmap[] getSmoothingImage(Bitmap bitmap) {
        Bitmap resultA = bitmap.copy(bitmap.getConfig(), true);
        Bitmap resultB = bitmap.copy(bitmap.getConfig(), true);
        int count;
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int[] color;
        int[] sum = new int[4];

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                count = 0;
                sum[RED] = sum[GREEN] = sum[BLUE] = sum[GRAYSCALE] = 0;
                for (int a = -1; a <= 1; a++) {
                    for (int b = -1; b <= 1; b++) {
                        if (0 <= i + a && i + a < width && 0 <= j + b && j + b < height) {
                            color = getPixelColor(bitmap, i + a, j + b);
                            sum[RED] = sum[RED] + color[RED];
                            sum[GREEN] = sum[GREEN] + color[GREEN];
                            sum[BLUE] = sum[BLUE] + color[BLUE];
                            sum[GRAYSCALE] = sum[GRAYSCALE] + color[GRAYSCALE];
                            count++;
                        }
                    }
                }
                setPixelColor(resultA, i, j, sum[RED] / count, sum[GREEN] / count, sum[BLUE] / count);
                setPixelColor(resultB, i, j, sum[GRAYSCALE] / count, sum[GRAYSCALE] / count, sum[GRAYSCALE] / count);
            }
        }

        return new Bitmap[]{resultA, resultB};
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

    // private methods

    private static int[] getPixelColor(Bitmap bitmap, int x, int y) {
        int pixel, red, green, blue, grayscale;

        pixel = bitmap.getPixel(x, y);
        red = Color.red(pixel);
        green = Color.green(pixel);
        blue = Color.blue(pixel);
        grayscale = (red + green + blue) / 3;

        return new int[]{red, green, blue, grayscale};
    }

    private static void setPixelColor(Bitmap bitmap, int x, int y, int red, int green, int blue) {
        bitmap.setPixel(x, y, Color.argb(255, red, green, blue));
    }
}
