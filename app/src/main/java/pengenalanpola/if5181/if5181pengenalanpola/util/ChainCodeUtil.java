package if5181.finalproject.util;

import java.util.ArrayList;
import java.util.List;

public class ChainCodeUtil {

    public static int translateSevenSegment(StringBuilder chainCode) {
        List<Integer> count;

        String simpleChain = getSimplifiedChainCode(chainCode).toString();

        switch (simpleChain) {
            case "2460":
                count = getChainCodeCount(chainCode);
                if ((double) count.get(0) / count.get(1) <= 0.2) return 1;
                else return 0;
            case "246424602060":
                count = getChainCodeCount(chainCode);
                if (count.get(1) > count.get(count.size() - 1)) return 2;
                else return 5;
            case "246020602060":
                return 3;
            case "2420246060":
                return 4;
            case "24642460":
                return 6;
            case "246060":
                return 7;
            case "24602060":
                return 9;
            default:
                return -1;
        }
    }

    public static int translateNumberArial(StringBuilder chainCode) {
        double similarity;

        double max = 0;
        double[] feature = new double[8];
        double[][] model = {
                {0.250, 0.075, 0.098, 0.075, 0.250, 0.075, 0.098, 0.075},
                {0.329, 0.079, 0.074, 0.000, 0.361, 0.053, 0.095, 0.005},
                {0.108, 0.161, 0.172, 0.044, 0.134, 0.112, 0.243, 0.022},
                {0.143, 0.098, 0.158, 0.105, 0.132, 0.098, 0.169, 0.094},
                {0.186, 0.146, 0.090, 0.005, 0.328, 0.005, 0.232, 0.005},
                {0.161, 0.060, 0.218, 0.067, 0.147, 0.070, 0.211, 0.063},
                {0.189, 0.086, 0.133, 0.103, 0.163, 0.086, 0.159, 0.077},
                {0.211, 0.091, 0.201, 0.000, 0.201, 0.105, 0.183, 0.004},
                {0.175, 0.095, 0.132, 0.101, 0.164, 0.101, 0.132, 0.095},
                {0.168, 0.090, 0.147, 0.086, 0.181, 0.086, 0.142, 0.095}
        };
        int result = 0;

        for (int i = 0; i < chainCode.length(); i++) {
            feature[Character.getNumericValue(chainCode.charAt(i))]++;
        }

        for (int i = 0; i < 8; i++) {
            feature[i] = feature[i] / chainCode.length();
        }

        for (int i = 0; i < model.length; i++) {
            similarity = VectorUtil.similarity(feature, model[i]);

            if(similarity > max){
                max = similarity;
                result = i;
            }
        }

        return result;
    }

    private static List<Integer> getChainCodeCount(StringBuilder chainCode) {
        if (chainCode.length() < 2) return new ArrayList<>();

        char current;

        ArrayList<Integer> count = new ArrayList<>();
        char last = chainCode.charAt(0);
        int counter = 0;

        for (int i = 1; i < chainCode.length(); i++) {
            current = chainCode.charAt(i);
            if (Character.getNumericValue(current) % 2 == 0) {
                if (current != last) {
                    count.add(counter);
                    last = chainCode.charAt(i);
                    counter = 1;
                } else {
                    counter++;
                }
            }
        }

        count.add(counter);

        return count;
    }

    private static StringBuilder getSimplifiedChainCode(StringBuilder chainCode) {
        if (chainCode.length() < 2) return chainCode;

        char current, last = chainCode.charAt(0);
        StringBuilder result = new StringBuilder().append(last);

        for (int i = 1; i < chainCode.length(); i++) {
            current = chainCode.charAt(i);
            if (current != last && Character.getNumericValue(current) % 2 == 0) {
                last = current;
                result.append(current);
            }
        }

        return result;
    }
}
