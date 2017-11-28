package a2_clustering;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileOpener {
    public BufferedReader br;

    public FileOpener(Path path) {

        try {
            br = openFile(path);
        } catch (FileNotFoundException e) {
            System.err.println("Couldn't find file check path");

        }

    }

    private BufferedReader openFile(Path filepath) throws FileNotFoundException {

        File file = new File(filepath.toString());
        FileInputStream fis = new FileInputStream(file);
        InputStreamReader isr = new InputStreamReader(fis);
        return new BufferedReader(isr);
    }

    public void close() throws IOException {
        br.close();
    }

}
