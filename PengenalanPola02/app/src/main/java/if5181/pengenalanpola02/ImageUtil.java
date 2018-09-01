package if5181.pengenalanpola02;

import android.graphics.Bitmap;
import android.graphics.Color;

public class ImageUtil {

    public static Bitmap getGrayscaleBitmap(Bitmap bitmap) {
        Bitmap result = bitmap.copy(bitmap.getConfig(), true);
        int grayscale;
        int[] color;

        for (int i = 0; i < bitmap.getWidth(); i++) {
            for (int j = 0; j < bitmap.getHeight(); j++) {
                color = getPixelColor(bitmap, i, j);
                grayscale = (color[0] + color[1] + color[2]) / 3;
                setPixelColor(result, i, j, grayscale, grayscale, grayscale);
            }
        }

        return result;
    }

    public static Bitmap getLinearTransform(Bitmap bitmap) {
        Bitmap result = bitmap.copy(bitmap.getConfig(),true);
        int grayscale;
        int sum = bitmap.getWidth() * bitmap.getHeight();
        int[] color;
        int[] count = new int[256];
        int[] lookup = new int[256];

        for (int i = 0; i < bitmap.getWidth(); i++) {
            for (int j = 0; j < bitmap.getHeight(); j++) {
                color = getPixelColor(bitmap, i, j);
                grayscale = (color[0] + color[1] + color[2]) / 3;
                count[grayscale]++;
            }
        }

        for (int i = 1; i < 256; i++) {
            count[i] = count[i] + count[i - 1];
            lookup[i] = count[i] * 256 / sum;
        }

        for (int i = 0; i < bitmap.getWidth(); i++) {
            for (int j = 0; j < bitmap.getHeight(); j++) {
                color = getPixelColor(bitmap, i, j);
                grayscale = lookup[(color[0] + color[1] + color[2]) / 3];
                setPixelColor(result, i, j, grayscale, grayscale, grayscale);
            }
        }

        return result;
    }


    private static int[] getPixelColor(Bitmap bitmap, int x, int y) {
        int pixel = bitmap.getPixel(x, y);
        return new int[]{(pixel & 0x00ff0000) >> 16, (pixel & 0x0000ff00) >> 8, pixel & 0x000000ff};
    }

    private static void setPixelColor(Bitmap bitmap, int x, int y, int red, int green, int blue) {
        bitmap.setPixel(x, y, Color.argb(255, red, green, blue));
    }
}
