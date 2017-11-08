package a1_webCrawler;

public class Main {

    public static void main(String[] args) {
        String[] urls = {"/wiki/Programming_language", "/wiki/Video_game"};

        for (String url: urls) {
            LinkNode root = new LinkNode(url);
            CrawlRoot op = new CrawlRoot(root);
            Output out = new Output(op.bfsResult, root);
        }
    }
}
