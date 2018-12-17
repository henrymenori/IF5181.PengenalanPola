package if5181.finalproject.util;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {

    private static final String DIRECTORY = "/storage/emulated/0/Documents";

    public static List<String> read(String filename) {
        String line;

        File file = new File(DIRECTORY, filename);
        List<String> text = new ArrayList<>();

        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));

            while ((line = bufferedReader.readLine()) != null) {
                text.add(line);
            }

            bufferedReader.close();
        } catch (IOException e) {
            Log.e("Exception", "Read file failed : " + e.toString());
        }

        return text;
    }
    
}
