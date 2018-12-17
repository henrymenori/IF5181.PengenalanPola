package if5181.finalproject.model;

import java.util.ArrayList;
import java.util.List;

import if5181.finalproject.util.FileUtil;
import if5181.finalproject.util.VectorUtil;

public class AlphaNumeric {

    private List<int[]> X;
    private List<Character> Y;

    public AlphaNumeric(String filename) {
        int counter;

        X = new ArrayList<>();
        Y = new ArrayList<>();

        for (String line : FileUtil.read(filename)) {
            counter = 0;
            int[] feature = new int[21];

            for (String value : line.split(" ")) {
                if (counter == 0)
                    Y.add(value.charAt(0));
                else
                    feature[counter - 1] = Integer.parseInt(value);

                counter++;
            }

            X.add(feature);
        }
    }

    public char translate(int[] feature) {
        double s;

        double vmax = 0;
        int imax = 0;

        for (int i = 0; i < Y.size(); i++) {
            s = VectorUtil.similarity(feature, X.get(i), 0, 8)
                    + VectorUtil.similarity(feature, X.get(i), 8, 17)
                    + VectorUtil.similarity(feature, X.get(i), 17, 18)
                    + VectorUtil.similarity(feature, X.get(i), 18, 21);

            if (s > vmax) {
                vmax = s;
                imax = i;
            }
        }

        return Y.get(imax);
    }

}
