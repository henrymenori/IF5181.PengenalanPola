package pengenalanpola.if5181.if5181pengenalanpola.util;

import android.graphics.Bitmap;
import android.widget.TextView;

import pengenalanpola.if5181.if5181pengenalanpola.model.SkeletonFeature;

public class NewImageUtil {

    // -----------------------------
    // Image Enhancement Algorithm
    // -----------------------------

    public static Bitmap getBinaryImage(Bitmap bitmap, int threshold) {
        int height = bitmap.getHeight();
        int width = bitmap.getWidth();
        int size = width * height;
        int[] pixels = new int[size];

        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

        for (int i = 0; i < size; i++) {
            int pixel = pixels[i];
            int grayscale = (((pixel & 0x00ff0000) >> 16) + ((pixel & 0x0000ff00) >> 8) + (pixel & 0x000000ff)) / 3;

            if (grayscale < threshold) {
                pixels[i] = pixel & 0xff000000;
            } else {
                pixels[i] = pixel | 0x00ffffff;
            }
        }

        return Bitmap.createBitmap(pixels, bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
    }

    // -----------------------------
    // Feature Extraction Algorithm
    // -----------------------------

    public static Bitmap[] getSkeleton(Bitmap bitmap) {
        int count;
        int[] border;

        int height = bitmap.getHeight();
        int width = bitmap.getWidth();
        int size = height * width;
        int[] pixels = new int[size];
        int[] pixelsa = new int[size];
        int[] pixelsb = new int[size];

        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        bitmap.getPixels(pixelsa, 0, width, 0, 0, width, height);
        bitmap.getPixels(pixelsb, 0, width, 0, 0, width, height);

        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                if ((pixels[i + j * width] & 0x000000ff) != 255) {
                    border = SubImageUtil.floodFill(pixels, i, j, width);

                    do {
                        count = SubImageUtil.zhangSuenStep(pixelsa, border[0], border[1], border[2], border[3], width);
                    }
                    while (count != 0);

                    SubImageUtil.customStep(pixelsb, border[0], border[1], border[2], border[3], i, j, width);
                }
            }
        }

        return new Bitmap[]{
                Bitmap.createBitmap(pixelsa, width, height, bitmap.getConfig()),
                Bitmap.createBitmap(pixelsb, width, height, bitmap.getConfig())
        };
    }

    public static void getSkeletonFeature(Bitmap bitmap, TextView textView) {
        int count;
        int[] border, border2;

        int height = bitmap.getHeight();
        int width = bitmap.getWidth();
        int size = height * width;
        int[] pixels = new int[size];
        int[] pixelsa = new int[size];
        StringBuffer stringBuffer = new StringBuffer();

        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        bitmap.getPixels(pixelsa, 0, width, 0, 0, width, height);

        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                if ((pixels[i + j * width] & 0x000000ff) != 255) {
                    border = SubImageUtil.floodFill(pixels, i, j, width);

                    do {
                        count = SubImageUtil.zhangSuenStep(pixelsa, border[0], border[1], border[2], border[3], width);
                    }
                    while (count != 0);

                    border2 = SubImageUtil.getNewBorder(pixelsa, border[0], border[1], border[2], border[3], width);
                    SkeletonFeature sf = SubImageUtil.extractFeature(pixelsa, border2[0], border2[1], border2[2], border2[3], width);

                    stringBuffer.append(String.format("%d,%b,%b,%b,%b,%b,%b,%b,%b,%b\r\n",
                            sf.endpoints.size(),
                            sf.hTop, sf.hMid, sf.hBottom,
                            sf.vLeft, sf.vMid, sf.vRight,
                            sf.lTop, sf.lMid, sf.lBottom));
                }
            }
        }

        textView.setText(stringBuffer);
    }
}
