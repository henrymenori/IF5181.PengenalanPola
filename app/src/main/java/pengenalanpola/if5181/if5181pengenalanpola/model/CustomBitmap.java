package if5181.finalproject.model;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import if5181.finalproject.util.ChainCodeUtil;
import if5181.finalproject.util.PointUtil;
import if5181.finalproject.util.VectorUtil;

public class CustomBitmap {

    // ==== Properties ====

    private int height;
    private int width;
    private int size;
    private int[] pixels;
    private Bitmap.Config config;


    // ==== Constructor ====

    public CustomBitmap(Bitmap bitmap) {
        width = bitmap.getWidth();
        height = bitmap.getHeight();
        size = width * height;
        config = bitmap.getConfig();
        pixels = new int[size];

        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
    }

    public CustomBitmap(int[] pixels, int width, int height, Bitmap.Config config) {
        this.pixels = pixels;
        this.width = width;
        this.height = height;
        this.config = config;
        this.size = width * height;
    }


    // ==== Public Methods ====

    public Bitmap getCurrentBitmap() {
        return Bitmap.createBitmap(pixels, width, height, config);
    }

    public int[] floodFill(int[] source, int start, int colora, int colorb, boolean standard) {
        int p;
        int xmin = start % width;
        int xmax = start % width;
        int ymin = start / width;
        int ymax = start / width;
        int[] neighbors;
        Queue<Integer> queue = new LinkedList<>();

        queue.offer(start);

        while (!queue.isEmpty()) {
            p = queue.poll();

            if (isSameColor(source[p], colora)) {
                neighbors = getNeighborIndex(p);

                if (p % width < xmin)
                    xmin = p % width;
                if (p % width > xmax)
                    xmax = p % width;
                if (p / width < ymin)
                    ymin = p / width;
                if (p / width > ymax)
                    ymax = p / width;

                for (int i = 0; i < 8; i += (standard ? 2 : 1)) {
                    if (neighbors[i] != -1)
                        queue.offer(neighbors[i]);
                }

                setPixelColor(source, p, colorb);
            }
        }

        return new int[]{xmin + ymin * width, xmax + ymax * width};
    }

    public int[][] getColorStatistic() {
        int s;
        int[] rgb;
        int[][] statistic = new int[4][256];

        for (int i = 0; i < size; i++) {
            rgb = getRGB(pixels[i]);
            s = (rgb[0] + rgb[1] + rgb[2]) / 3;

            for (int j = 0; j < 3; j++) {
                statistic[j][rgb[j]]++;
            }

            statistic[3][s]++;
        }

        return statistic;
    }

    public StringBuilder getChainCode() {
        int i, cur, start, dir;
        int[] neighbors;
        StringBuilder chainCode = new StringBuilder();

        start = 0;
        while (!isSameColor(pixels[start], 0x000000)) {
            start++;
        }

        cur = start;
        dir = 2;
        do {
            neighbors = getNeighborIndex(cur);

            for (i = 0; i < 8; i++) {
                if (isSameColor(pixels[neighbors[(dir + i + 5) % 8]], 0x000000))
                    break;
            }

            if (i < 8) {
                dir = (dir + i + 5) % 8;
                cur = neighbors[dir];
                chainCode.append(dir);
            }
        } while (cur != start);

        return chainCode;
    }

    public CustomBitmap convertToBinary(int threshold) {
        int s;
        int[] rgb;
        int[] output = new int[size];

        for (int i = 0; i < size; i++) {
            rgb = getRGB(pixels[i]);
            s = (rgb[0] + rgb[1] + rgb[2]) / 3;

            if (s < threshold)
                output[i] = pixels[i] & 0xff000000;
            else
                output[i] = (pixels[i] & 0xff000000) + 0xffffff;
        }

        return new CustomBitmap(output, width, height, config);
    }

    public CustomBitmap convertToGrayscale() {
        int s;
        int[] rgb;
        int[] output = new int[size];

        for (int i = 0; i < size; i++) {
            rgb = getRGB(pixels[i]);
            s = (rgb[0] + rgb[1] + rgb[2]) / 3;

            output[i] = (pixels[i] & 0xff000000) + getColor(s, s, s);
        }

        return new CustomBitmap(output, width, height, config);
    }

    public CustomBitmap detectEdge() {
        int max, value;
        int[] neighbors, rgb;
        int[] output = new int[size];
        int[][] operator = {
                {1, 2, 1, 0, -1, -2, -1, 0},
                {0, 1, 2, 1, 0, -1, -2, -1},
                {-1, 0, 1, 2, 1, 0, -1, -2},
                {-2, -1, 0, 1, 2, 1, 0, -1}
        };

        for (int j = 1; j < height - 1; j++) {
            for (int i = 1; i < width - 1; i++) {
                neighbors = getNeighborIndex(i + j * width);
                max = 0;

                for (int k = 0; k < 8; k++) {
                    rgb = getRGB(pixels[neighbors[k]]);
                    neighbors[k] = (rgb[0] + rgb[1] + rgb[2]) / 3;
                }

                for (int l = 0; l < 4; l++) {
                    value = Math.abs(VectorUtil.dotProduct(neighbors, operator[l])) / 4;
                    if (value > max) max = value;
                }

                output[i + j * width] = 0xff000000 + getColor(max, max, max);
            }
        }

        return new CustomBitmap(output, width, height, config);
    }

    public CustomBitmap enhanceImage(double alpha) {
        int[] output = new int[size];
        int[] rgb;
        int[][] statistic = getColorStatistic();
        int[][] lookup = new int[4][256];

        for (int i = 1; i < 256; i++) {
            for (int j = 0; j < 4; j++) {
                statistic[j][i] = statistic[j][i - 1] + (int) (alpha * statistic[j][i]);
            }
        }

        for (int i = 0; i < 256; i++) {
            for (int j = 0; j < 4; j++) {
                lookup[j][i] = statistic[j][i] * 255 / statistic[j][255];
            }
        }

        for (int i = 0; i < size; i++) {
            rgb = getRGB(pixels[i]);
            output[i] = (pixels[i] & 0xff000000) + getColor(lookup[0][rgb[0]], lookup[1][rgb[1]], lookup[2][rgb[2]]);
        }

        return new CustomBitmap(output, width, height, config);
    }

    public CustomBitmap enhanceImage(double alpha, int x, int y, int z) {
        int[] rgb;
        int[] attribute = new int[256];
        int[] lookup = new int[256];
        int[] output = new int[size];

        for (int i = 0; i < 256; i++) {
            if (i < y) attribute[i] = x + i * (255 - x) / y;
            else if (i > y) attribute[i] = z + (255 - i) * (255 - z) / (255 - y);
            else attribute[i] = 255;
        }

        for (int i = 1; i < 256; i++) {
            attribute[i] = attribute[i - 1] + (int) (alpha * attribute[i]);
        }

        for (int i = 0; i < 256; i++) {
            lookup[i] = attribute[i] * 255 / attribute[255];
        }

        for (int i = 0; i < size; i++) {
            rgb = getRGB(pixels[i]);
            output[i] = (pixels[i] & 0xff000000) + getColor(lookup[rgb[0]], lookup[rgb[1]], lookup[rgb[2]]);
        }

        return new CustomBitmap(output, width, height, config);
    }

    public CustomBitmap erode() {
        int a, b, p, count;
        int[] neighbors;
        int[] output = pixels.clone();

        do {
            count = 0;

            for (int j = 1; j < height - 1; j++) {
                for (int i = 1; i < width - 1; i++) {
                    p = i + j * width;

                    if (isSameColor(output[p], 0x000000)) {
                        neighbors = getNeighborIndex(p);
                        a = b = 0;

                        for (int k = 0; k < 8; k++) {
                            if (isSameColor(output[neighbors[k]], 0xffffff) && !isSameColor(output[neighbors[(k + 1) % 8]], 0xffffff))
                                a++;
                            if (!isSameColor(output[neighbors[k]], 0xffffff))
                                b++;
                        }

                        if (a != 1)
                            continue;
                        if (b < 2 || 6 < b)
                            continue;
                        if (!isSameColor(output[neighbors[0]], 0xffffff) && !isSameColor(output[neighbors[2]], 0xffffff) && !isSameColor(output[neighbors[4]], 0xffffff))
                            continue;
                        if (!isSameColor(output[neighbors[2]], 0xffffff) && !isSameColor(output[neighbors[4]], 0xffffff) && !isSameColor(output[neighbors[6]], 0xffffff))
                            continue;

                        output[p] = (pixels[p] & 0xff000000) + 0x00ff00;
                    }
                }
            }

            for (int j = 0; j < size; j++) {
                if (isSameColor(output[j], 0x00ff00)) {
                    output[j] = (pixels[j] & 0xff000000) + 0xffffff;
                    count++;
                }
            }

            for (int j = 1; j < height - 1; j++) {
                for (int i = 1; i < width - 1; i++) {
                    p = i + j * width;

                    if (isSameColor(output[p], 0x000000)) {
                        neighbors = getNeighborIndex(p);
                        a = b = 0;

                        for (int k = 0; k < 8; k++) {
                            if (isSameColor(output[neighbors[k]], 0xffffff) && !isSameColor(output[neighbors[(k + 1) % 8]], 0xffffff))
                                a++;
                            if (!isSameColor(output[neighbors[k]], 0xffffff))
                                b++;
                        }

                        if (a != 1)
                            continue;
                        if (b < 2 || 6 < b)
                            continue;
                        if (!isSameColor(output[neighbors[0]], 0xffffff) && !isSameColor(output[neighbors[2]], 0xffffff) && !isSameColor(output[neighbors[6]], 0xffffff))
                            continue;
                        if (!isSameColor(output[neighbors[0]], 0xffffff) && !isSameColor(output[neighbors[4]], 0xffffff) && !isSameColor(output[neighbors[6]], 0xffffff))
                            continue;

                        output[p] = (pixels[p] & 0xff000000) + 0x00ff00;
                    }
                }
            }

            for (int i = 0; i < size; i++) {
                if (isSameColor(output[i], 0x00ff00)) {
                    output[i] = (pixels[i] & 0xff000000) + 0xffffff;
                    count++;
                }
            }
        } while (count > 0);

        return new CustomBitmap(output, width, height, config);
    }

    public CustomBitmap filter() {
        double ckx, cky, val;
        double c = 2 / Math.sqrt(width * height);
        double[] output1 = new double[size];
        double[] output2 = new double[size];
        int[] result = new int[size];

        for (int kx = 0; kx < width; kx++) {
            for (int ky = 0; ky < height; ky++) {
                if (kx == 0) ckx = 1 / Math.sqrt(2);
                else ckx = 1;
                if (ky == 0) cky = 1 / Math.sqrt(2);
                else cky = 1;

                val = 0;
                for (int x = 0; x < width; x++) {
                    for (int y = 0; y < height; y++) {
                        val += (pixels[x + y * width] & 0x000000ff)
                                * Math.cos((2 * x + 1) * kx * Math.PI / (2 * width))
                                * Math.cos((2 * y + 1) * ky * Math.PI / (2 * height));
                    }
                }

                output1[kx + ky * width] = c * ckx * cky * val;
            }
        }

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (i > width / 2 || j > height / 2) output1[i + j * width] = 0;
            }
        }

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                val = 0;
                for (int kx = 0; kx < width; kx++) {
                    for (int ky = 0; ky < height; ky++) {
                        if (kx == 0) ckx = 1 / Math.sqrt(2);
                        else ckx = 1;
                        if (ky == 0) cky = 1 / Math.sqrt(2);
                        else cky = 1;

                        val += ckx * cky * output1[kx + ky * width]
                                * Math.cos((2 * x + 1) * kx * Math.PI / (2 * width))
                                * Math.cos((2 * y + 1) * ky * Math.PI / (2 * height));
                    }
                }

                output2[x + y * width] = c * val;
            }
        }

        for (int i = 0; i < size; i++) {
            if (output2[i] < 0)
                result[i] = (pixels[i] & 0xff000000) + getColor(0, 0, 0);
            else if (output2[i] > 255)
                result[i] = (pixels[i] & 0xff000000) + getColor(255, 255, 255);
            else
                result[i] = (pixels[i] & 0xff000000) + getColor((int) output2[i], (int) output2[i], (int) output2[i]);
        }

        return new CustomBitmap(result, width, height, config);
    }

    public CustomBitmap getSubImage(int[] source, int[] border) {
        return new CustomBitmap(Bitmap.createBitmap(
                pixels,
                border[0] - width - 1,
                width,
                (border[1] - border[0]) % width + 3,
                (border[1] - border[0]) / width + 3,
                config));
    }

    public CustomBitmap reduceNoise() {
        int[] output = new int[size];
        int[] neighbors;
        int[] rgb;
        List<Integer> r = new ArrayList<>();
        List<Integer> g = new ArrayList<>();
        List<Integer> b = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            neighbors = getNeighborIndex(i);
            r.clear();
            g.clear();
            b.clear();

            for (int j = 0; j < 8; j++) {
                if (neighbors[j] != -1) {
                    rgb = getRGB(pixels[neighbors[j]]);

                    r.add(rgb[0]);
                    g.add(rgb[1]);
                    b.add(rgb[2]);
                }
            }

            Collections.sort(r);
            Collections.sort(g);
            Collections.sort(b);

            output[i] = (pixels[i] & 0xff000000) + getColor(r.get(r.size() / 2), g.get(g.size() / 2), b.get(b.size() / 2));
        }

        return new CustomBitmap(output, width, height, config);
    }

    public CustomBitmap smoothImage() {
        int counter, r, g, b;
        int[] output = new int[size];
        int[] neighbors;
        int[] rgb;

        for (int i = 0; i < size; i++) {
            neighbors = getNeighborIndex(i);
            counter = r = g = b = 0;

            for (int j = 0; j < 8; j++) {
                if (neighbors[j] != -1) {
                    rgb = getRGB(pixels[neighbors[j]]);

                    r += rgb[0];
                    g += rgb[1];
                    b += rgb[2];

                    counter++;
                }
            }

            output[i] = (pixels[i] & 0xff000000) + getColor(r / counter, g / counter, b / counter);
        }

        return new CustomBitmap(output, width, height, config);
    }


    // ==== Private Methods ====

    private boolean isSameColor(int colora, int colorb) {
        return (colora & 0x00ffffff) == (colorb & 0x00ffffff);
    }

    private int getColor(int r, int g, int b) {
        return (r << 16) + (g << 8) + b;
    }

    private int[] getNeighborIndex(int p) {
        boolean a = p % width > 0;
        boolean b = p % width < width - 1;
        boolean c = p / width > 0;
        boolean d = p / width < height - 1;
        int[] neighbors = new int[8];

        neighbors[0] = c ? p - width : -1;
        neighbors[1] = c && b ? p - width + 1 : -1;
        neighbors[2] = b ? p + 1 : -1;
        neighbors[3] = d && b ? p + width + 1 : -1;
        neighbors[4] = d ? p + width : -1;
        neighbors[5] = d && a ? p + width - 1 : -1;
        neighbors[6] = a ? p - 1 : -1;
        neighbors[7] = c && a ? p - width - 1 : -1;

        return neighbors;
    }

    private int[] getRGB(int color) {
        return new int[]{(color & 0x00ff0000) >> 16, (color & 0x0000ff00) >> 8, color & 0x000000ff};
    }

    private void setPixelColor(int[] source, int index, int color) {
        source[index] = (source[index] & 0xff000000) + color;
    }


    // ==== Alphanumeric Recognition Methods ====

    public StringBuilder recognizeSevenSegment() {
        CustomBitmap subImage;
        int[] border;
        int[] output = pixels.clone();
        StringBuilder result = new StringBuilder().append("Number recognized : \r\n");

        for (int i = 0; i < size; i++) {
            if (isSameColor(output[i], 0x000000)) {
                border = floodFill(output, i, 0x000000, 0xffffff, true);
                subImage = getSubImage(pixels, border);
                result.append(ChainCodeUtil.translateSevenSegment(subImage.getChainCode())).append("\r\n");
            }
        }

        return result;
    }

    public StringBuilder recognizeNumberArial() {
        CustomBitmap subImage;
        int[] border;
        int[] output = pixels.clone();
        StringBuilder result = new StringBuilder().append("Number recognized : \r\n");

        for (int i = 0; i < size; i++) {
            if (isSameColor(output[i], 0x000000)) {
                border = floodFill(output, i, 0x000000, 0xffffff, true);
                subImage = getSubImage(pixels, border);
                result.append(ChainCodeUtil.translateNumberArial(subImage.getChainCode())).append("\r\n");
            }
        }

        return result;
    }

    public StringBuilder recognizeAlphaNumericArial() {
        AlphaNumeric model = new AlphaNumeric("alphanumeric.txt");
        CustomBitmap subImage;
        int[] border, feature;
        int[] output = pixels.clone();
        StringBuilder result = new StringBuilder().append("Character recognized : \r\n");

        for (int i = 0; i < size; i++) {
            if (isSameColor(output[i], 0x000000)) {
                try {
                    border = floodFill(output, i, 0x000000, 0xffffff, false);
                    subImage = getSubImage(pixels, border);
                    feature = subImage.erode().getAlphaNumericFeature();
                    result.append(String.format("{%d,%d,%d,%d,%d,%d,%d,%d}",
                            feature[0], feature[1], feature[2], feature[3],
                            feature[4], feature[5], feature[6], feature[7])).append("\r\n");
                    result.append(String.format("{%d,%d,%d,%d,%d,%d,%d,%d,%d}",
                            feature[8], feature[9], feature[10],
                            feature[11], feature[12], feature[13],
                            feature[14], feature[15], feature[16])).append("\r\n");
                    result.append(String.format("{%d}", feature[17])).append("\r\n");
                    result.append(String.format("{%d,%d,%d}",
                            feature[18], feature[19], feature[20])).append("\r\n");
                    result.append(model.translate(feature)).append("\r\n");
                    result.append("\r\n");
                } catch (Exception e) {
                    result.append("error\r\n\r\n");
                }
            }
        }
		
        return result;
    }

    public StringBuilder recognizeHandwriting() {
        AlphaNumeric model = new AlphaNumeric("alphanumeric.txt");
        CustomBitmap subImage;
        int[] border, feature;
        int[] output = pixels.clone();
        StringBuilder result = new StringBuilder().append("Character recognized : \r\n");

        for (int i = 0; i < size; i++) {
            if (isSameColor(output[i], 0x000000)) {
                try {
                    border = floodFill(output, i, 0x000000, 0xffffff, false);
                    subImage = getSubImage(pixels, border);
                    feature = subImage.erode().getHandwritingFeature();
                    result.append(String.format("{%d,%d,%d,%d,%d,%d,%d,%d}",
                            feature[0], feature[1], feature[2], feature[3],
                            feature[4], feature[5], feature[6], feature[7])).append("\r\n");
                    result.append(String.format("{%d,%d,%d,%d,%d,%d,%d,%d,%d}",
                            feature[8], feature[9], feature[10],
                            feature[11], feature[12], feature[13],
                            feature[14], feature[15], feature[16])).append("\r\n");
                    result.append(String.format("{%d}", feature[17])).append("\r\n");
                    result.append(String.format("{%d,%d,%d}",
                            feature[18], feature[19], feature[20])).append("\r\n");
                    result.append(model.translate(feature)).append("\r\n");
                    result.append("\r\n");
                } catch (Exception e) {
                    result.append("error\r\n\r\n");
                }
            }
        }

        return result;
    }

    private int[] getAlphaNumericFeature() {
        int a, b, x, y, mid;
        int[] neighbors, border;
        int[] feature = new int[21];

        for (int i = 0; i < size; i++) {
            if (isSameColor(pixels[i], 0x000000)) {
                neighbors = getNeighborIndex(i);
                a = b = 0;

                for (int j = 0; j < 8; j++) {
                    if (isSameColor(pixels[neighbors[j]], 0xffffff) && !isSameColor(pixels[neighbors[(j + 1) % 8]], 0xffffff))
                        a++;
                    if (!isSameColor(pixels[neighbors[j]], 0xffffff))
                        b++;
                }

                if (a > 2) {
                    feature[17]++;
                }
                if (b == 1) {
                    for (int j = 0; j < 8; j++) {
                        if (!isSameColor(pixels[neighbors[j]], 0xffffff)) {
                            feature[(j + 4) % 8]++;
                        }
                    }

                    x = (i % width) * 3 / width;
                    y = (i / width) * 3 / height;
                    feature[8 + x + y * 3]++;
                }
            }
        }

        for (int i = 0; i < size; i++) {
            if (isSameColor(pixels[i], 0xffffff)) {
                border = floodFill(pixels, i, 0xffffff, 0x000000, true);
                mid = (border[0] + border[1]) / 2;

                if (mid / width < height * 4 / 10) feature[18]++;
                else if (mid / width < height * 6 / 10) feature[19]++;
                else feature[20]++;
            }
        }

        feature[19]--;

        return feature;
    }

    private int[] getHandwritingFeature() {
        int a, b, x, y, mid;
        int[] neighbors, border;
        int[] feature = new int[21];
        List<Integer> endpoints = new ArrayList<>();
        List<Integer> intersections = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            if (isSameColor(pixels[i], 0x000000)) {
                neighbors = getNeighborIndex(i);
                a = b = 0;

                for (int j = 0; j < 8; j++) {
                    if (isSameColor(pixels[neighbors[j]], 0xffffff) && !isSameColor(pixels[neighbors[(j + 1) % 8]], 0xffffff))
                        a++;
                    if (!isSameColor(pixels[neighbors[j]], 0xffffff))
                        b++;
                }

                if (a > 2) {
                    intersections.add(i);
                }
                if (b == 1) {
                    endpoints.add(i);
                }
            }
        }

        List<Integer> en = new ArrayList<>();
        List<Integer> in = new ArrayList<>();

        for (int i = 0; i < endpoints.size(); i++) {
            for (int j = 0; j < intersections.size(); j++) {
                double distance = PointUtil.getDistance(endpoints.get(i), intersections.get(j), width);
                if (distance < width / 4) {
                    en.add(endpoints.get(i));
                    in.add(intersections.get(j));
                }
            }
        }

        for (int value : en) {
            if (endpoints.contains(value)) {
                endpoints.remove(endpoints.indexOf(value));
            }
        }

        for (int value : in) {
            if (intersections.contains(value)) {
                intersections.remove(intersections.indexOf(value));
            }
        }

        feature[17] = intersections.size();

        for (int value : endpoints) {
            neighbors = getNeighborIndex(value);

            for (int j = 0; j < 8; j++) {
                if (!isSameColor(pixels[neighbors[j]], 0xffffff)) {
                    feature[(j + 4) % 8]++;
                    x = (value % width) * 3 / width;
                    y = (value / width) * 3 / height;
                    feature[8 + x + y * 3]++;
                }
            }
        }

        for (int i = 0; i < size; i++) {
            if (isSameColor(pixels[i], 0xffffff)) {
                border = floodFill(pixels, i, 0xffffff, 0x000000, true);
                mid = (border[0] + border[1]) / 2;

                if (mid / width < height * 4 / 10) feature[18]++;
                else if (mid / width < height * 6 / 10) feature[19]++;
                else feature[20]++;
            }
        }

        feature[19]--;

        return feature;
    }


    // ==== Face Recognition Methods ====

    public Bitmap recognizeFace() {
        double probr, probg, probb;
        int ax, ay, bx, by, x, y, counter, min, s;
        int[] rgb;
        int[] output = new int[size];
        int[] medium = pixels.clone();

        x = y = counter = 0;
        ax = ay = size;
        bx = by = -1;

        for (int i = 0; i < size; i++) {
            rgb = getRGB(pixels[i]);

            probr = getProbability(rgb[0], 180);
            probg = getProbability(rgb[1], 140);
            probb = getProbability(rgb[2], 110);

            if (probr * probg * probb > (1 - probr) * (1 - probg) * (1 - probb)) {
                output[i] = pixels[i];
                if (i % width < ax) ax = i % width;
                if (i % width > bx) bx = i % width;
                if (i / width < ay) ay = i / width;
                if (i / width > by) by = i / width;

                x += (i % width);
                y += (i / width);
                counter++;
            } else {
                output[i] = pixels[i] & 0xff000000;
            }
        }

        for (int i = 0; i < size; i++) {
            if (i % width == ax && ay <= i / width && i / width <= by)
                pixels[i] = 0xffff0000;
            if (i % width == bx && ay <= i / width && i / width <= by)
                pixels[i] = 0xffff0000;
            if (i / width == ay && ax <= i % width && i % width <= bx)
                pixels[i] = 0xffff0000;
            if (i / width == by && ax <= i % width && i % width <= bx)
                pixels[i] = 0xffff0000;
        }

        x = x / counter;
        y = y / counter;
        min = (bx - ax) < (by - ay) ? bx - ax : by - ay;
        ax = ay = size;
        bx = by = -1;

        for (int i = 0; i < size; i++) {
            rgb = getRGB(pixels[i]);

            probr = getProbability(rgb[0], 180);
            probg = getProbability(rgb[1], 140);
            probb = getProbability(rgb[2], 110);

            if (probr * probg * probb > (1 - probr) * (1 - probg) * (1 - probb)
                    && Math.abs((i % width) - x) <= 0.5 * min
                    && Math.abs((i / width) - y) <= 0.6 * min) {
                output[i] = pixels[i];
                if (i % width < ax) ax = i % width;
                if (i % width > bx) bx = i % width;
                if (i / width < ay) ay = i / width;
                if (i / width > by) by = i / width;

            } else {
                output[i] = pixels[i] & 0xff000000;
            }
        }

        for (int i = 0; i < size; i++) {
            if (i % width == ax && ay <= i / width && i / width <= by)
                pixels[i] = 0xff00ff00;
            if (i % width == bx && ay <= i / width && i / width <= by)
                pixels[i] = 0xff00ff00;
            if (i / width == ay && ax <= i % width && i % width <= bx)
                pixels[i] = 0xff00ff00;
            if (i / width == by && ax <= i % width && i % width <= bx)
                pixels[i] = 0xff00ff00;
        }

        CustomBitmap mediumImage = detectEdge();

        for (int j = ay; j <= by; j++) {
            for (int i = ax; i <= bx; i++) {

                if (((bx - ax) * 2 / 10 <= (i - ax) && (i - ax) <= (bx - ax) * 4 / 10)
                        && ((by - ay) * 2 / 10 <= (j - ay) && (j - ay) <= (by - ay) * 3 / 10)) {
                    if ((mediumImage.pixels[i + j * width] & 0x000000ff) > 50)
                        pixels[i + j * width] = 0xff00ff00;
                }

                if (((bx - ax) * 7 / 10 <= (i - ax) && (i - ax) <= (bx - ax) * 8 / 10)
                        && ((by - ay) * 2 / 10 <= (j - ay) && (j - ay) <= (by - ay) * 3 / 10)) {
                    if ((mediumImage.pixels[i + j * width] & 0x000000ff) > 50)
                        pixels[i + j * width] = 0xff00ff00;
                }
            }
        }

        return Bitmap.createBitmap(pixels, width, height, config);
    }

    private static double getProbability(int input, int standard) {
        if (Math.abs(standard - input) >= 50)
            return 0.1;
        else
            return 1 - ((double) Math.abs(standard - input) / 50 * 0.8 + 0.1);
    }

}
