package a2_clustering;

import a2_clustering.Article;
import a2_clustering.Centroid;

import java.util.ArrayList;
import java.util.Random;

public class Randomizer {
    int min = 0;
    int centroidCount = 0;
    private Random r = new Random();
    private ArrayList<Word> words = new ArrayList<>();


    public Randomizer(ArrayList<Article> articles) {
        ArrayList<Word> allWords = (ArrayList<Word>) articles.get(0).words;

        // add all existing words into one array.
        for (Word w : allWords) {
            Word nw = new Word(w.word, 0.0);
            words.add(nw);
        }

        // check and set max counts for each word
        for (Article a : articles) {
            for (int i = 0; i < a.words.size(); i++) {
                double foreignWord = a.words.get(i).count;
                if (foreignWord > words.get(i).count) words.get(i).count = foreignWord;
            }
        }
    }

    public Centroid createRandom() {
        Article a = new Article(Integer.toString(centroidCount++));

        for (int i = 0; i < words.size(); i ++) {
            Word cw = words.get(i);
            int intValue = (int) cw.count;
            int rand = r.nextInt(intValue);
            double dRand = (double) rand;
            a.addWord(new Word(cw.word, dRand));
        }

        return new Centroid(a);
    }



}
