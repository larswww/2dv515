package a2_clustering;

import java.util.ArrayList;
import java.util.List;

public class Article {
    public String article;
    public List<Word> words = new ArrayList<Word>();

    public Article(String a) {
        article = a;
    }

    public void addWord(Word w) {
        words.add(w);

    }
}
