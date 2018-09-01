package if5181.pengenalanpola01;

import android.graphics.Bitmap;

public class ImageUtil {

    public static int[] getPixelColor(Bitmap bitmap, int x, int y) {
        int pixel = bitmap.getPixel(x, y);
        return new int[]{(pixel & 0x00ff0000) >> 16, (pixel & 0x0000ff00) >> 8, pixel & 0x000000ff};
    }

    public static int[][] getPixelCount(Bitmap bitmap) {
        int[] color;
        int[][] count = new int[4][256];

        for (int i = 0; i < bitmap.getWidth(); i++) {
            for (int j = 0; j < bitmap.getHeight(); j++) {
                color = getPixelColor(bitmap, i, j);
                count[0][color[0]]++;
                count[1][color[1]]++;
                count[2][color[2]]++;
                count[3][(color[0] + color[1] + color[2]) / 3]++;
            }
        }

        return count;
    }
}
