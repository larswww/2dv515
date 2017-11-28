package a2_clustering;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

public class ParseWikipediaData {
    ArrayList<Path> wordPaths = new ArrayList<>();
    HashSet<String> allWords = new HashSet<>(); // 19,398 original size w/o FilterWords()
    HashMap<String, HashMap<String, Integer>> wordPages = new HashMap<>();
    String entryPage = "not set";

    public ParseWikipediaData(String wordDataPath) {
        entryPage = lastInPath(wordDataPath);

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(wordDataPath))) {

            for (Path entry : stream) {
                CountWordBag(entry);
            }

            FilterWords();
            String lines = DataToStringLines();
            PrintLinesToFile(lines);



        } catch (Exception e) {
            System.err.print(e);
            e.printStackTrace();
        }

    }

    private void CountWordBag(Path path) throws IOException {
        BufferedReader br = new FileOpener(path).br;
        HashMap<String, Integer> wordCount = new HashMap<>();

        // todo lowercase the bag of words in crawler. remove punctuation. pure words.
        String line = br.readLine();
        while (line != null) {
            String[] splittedWordBag = line.split(" ");

            for (String word : splittedWordBag) {
                Integer count = wordCount.putIfAbsent(word, 1); // returns null if a count wasnt already set the value if already set
                if (count != null) {
                    count++;
                    wordCount.put(word, count);
                    allWords.add(word);
                }
            }

            line = br.readLine();
        }
        wordPages.put(lastInPath(path.toString()), wordCount);
    }

    private void FilterWords() {
        Integer totalPages = wordPages.size();
        System.out.println("Words before filter: " + allWords.size());
        ArrayList<String> toRemove = new ArrayList<>();

        allWords.forEach(word -> {
            final int[] count = {0};

            wordPages.forEach((pageName, wordCounts) -> {
                Integer wc = wordCounts.get(word);
                if (wc != null) count[0] += wc;
            });

//            if ((count[0] - totalPages) < 0) allWords.remove(word);
            int testC = count[0] - totalPages;
            if (testC > 1 || testC < (totalPages - (totalPages * 2) + 3)) {
                toRemove.add(word);
            }

        });

        for (String w : toRemove) {
            allWords.remove(w);
        }

        System.out.println("Words after filter: " + allWords.size());

    }


    private String DataToStringLines() {
        StringBuilder wholeFile = new StringBuilder();
        StringBuilder wordLine = new StringBuilder();
        wordLine.append("Word\t");
        ArrayList<String> pageLines = new ArrayList<>();

        allWords.forEach(word -> {
            wordLine.append(word).append("\t");
        });

        wordLine.append("\n");
        wholeFile.append(wordLine.toString());

        wordPages.forEach((pageName, wordCounts) -> {
            StringBuilder pageLine = new StringBuilder();
            pageLine.append(pageName).append("\t");

            allWords.forEach(word -> {
                Integer count = wordCounts.get(word);
                if (count == null) count = 0;
                pageLine.append(count.toString()).append("\t");

            });

            wholeFile.append(pageLine.toString()).append("\n");
        });

        return wholeFile.toString();
    }

    private void PrintLinesToFile(String lines) {
        String timeStamp = new SimpleDateFormat("dd.HH.mm.ss").format(new Date());

        File file = new File(System.getProperty("user.dir") + "/data/ParsedWikipedia/");
        file = new File(file, entryPage + " " + timeStamp + ".txt");

        try {
            if (!file.exists()) file.getParentFile().mkdirs();
            file.createNewFile();
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            fw.write(lines);
            fw.close();
        } catch (IOException exc) {
            exc.printStackTrace();
        }

    }

    private String lastInPath(String path) {
        String[] wholePath = path.toString().split("/");
        return wholePath[wholePath.length -1];
    }

}
