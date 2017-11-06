package a1_webCrawler;

public class Main {

    public static void main(String[] args) {
        LinkNode root = new LinkNode("/wiki/Programming_language");
        OpenPage op = new OpenPage(root);
        Output out = new Output(op.traversed, root);


	// write your code here
    }
}
