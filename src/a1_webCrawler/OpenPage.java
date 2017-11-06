package a1_webCrawler;

import org.jsoup.*;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OpenPage {
    private int maxLevel = 2;
    private int maxLinks = 1000;
    ArrayList<Thread> threads = new ArrayList<Thread>();
    List<LinkNode> links = new ArrayList<>();
    Set<LinkNode> visited = new HashSet<LinkNode>();
    Map<String, LinkNode> traversed = new HashMap<String, LinkNode>();

    public OpenPage(LinkNode root) {
        links.add(root);
        BFS();
    }

    private class VisitThread extends Thread {
        LinkNode nd;

        public VisitThread(LinkNode node) {
            nd = node;
        }

        public void run() {
            CreateURL(nd);
            ExtractLinks(nd);
            getText(nd);
            traversed.put(nd.link, nd); //todo how to handle not found/dead links?
        }
    }

    private void VisitLink(LinkNode node) {
        CreateURL(node);
        ExtractLinks(node);
        getText(node);
        traversed.put(node.link, node); //todo how to handle not found/dead links?

    }

    private void getText(LinkNode node) {
        Document doc = Jsoup.parse(node.contents);
        doc.select("div#footer").remove();
        doc.select("div#mw-navigation").remove();
        doc.select("div#left-navigation").remove();
        doc.select("div#right-navigation").remove();

        String text = doc.body().text();
        preprocessText(text);
        node.text = text;
    }

    private void preprocessText(String text) {
        String[] toReplace = {"\\<.*?>", "\\[.*?\\]", "\\d{4}-\\d{2}-\\d{2}", "\\.", ",", "\\?", ";", "\"", ":", "\\(", "\\*", "_", "!", "#", "\\)"};
        text = text.toLowerCase();
        text = text.replaceAll("\r", " ");
        text = text.replaceAll("\n", " ");

        for (String str : toReplace) {
            text = text.replaceAll(str, "");
        }

    }

    private void BFS() {
        int bfsNo = 0;
        LinkNode nd = links.remove(0);
        nd.bfsNo = bfsNo++;
        visited.add(nd);
        VisitLink(nd);

        // loop based on depth requirement
        // and only whilst max links is below max links requirement
        while (visited.size() <= maxLinks && !links.isEmpty()) {
            LinkNode node = links.remove(0); //todo use a treeSet instead?

            if (!visited.contains(node)) {
                node.bfsNo = bfsNo++;
                visited.add(node);
                VisitLink(node);
//                VisitThread vl = new VisitThread(node);
//                threads.add(vl);
//                vl.start();

            }
        }

//        for (Thread t: threads) {
//            try {
//                t.join();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }

        System.out.println("done");
    }

    private String CreateURL(LinkNode node) {
        String contents = "";
        try {
            URL url = new URL("https://en.wikipedia.org/wiki/" + node.link);
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            String line;

            while ((line = reader.readLine()) != null) {
                //todo use a StringBuilder instead?
                contents += (line + "\n");
            }
            reader.close();
            node.contents = contents;

        } catch (Exception e) {
            node.contents = contents;
            System.err.println(e.getMessage());
        }

        return contents;
    }

    private void ExtractLinks(LinkNode node) {
        Pattern p = Pattern.compile("href=\"(.*?)\"");
        Matcher m = p.matcher(node.contents);

        while (!m.hitEnd()) {
            boolean a = m.find();
            if (a) {
                String match = node.contents.substring(m.start(), m.end());
                String link = match.substring(match.indexOf("\"") + 1, match.lastIndexOf("\""));

                if (LinkFilter(link)) {
                    LinkNode ln = new LinkNode(link);
                    ln.preds.add(node);
                    node.succs.add(ln);
                    links.add(ln); //todo faster if it's a set? and check if it's not in the set already before running linkfilter? Do i care about iteration order of the links? set = random now = order of find..
                }
            }
        }
    }

    private boolean LinkFilter(String link) {
        String copy = link.toLowerCase();
        String[] toFilterStart = {"#", "/wiki/help:", "/wiki/category:", "/wiki/portal:", "/wiki/special:", "/wiki/file:", "/wiki/template:", "/wiki/wikipedia:", "/wiki/template_talk:", "/wiki/talk:", "http://", "https://", "//", "/w/", "/wiki/Wikipedia:", "android-app:"};
        String[] toFilterEnd = {".jpg", ".avi", ".png", ".ico"};

        for (String s : toFilterStart) {
            if (copy.startsWith(s)) {
                return false;
            }
        }

        for (String s : toFilterEnd) {
            if (copy.endsWith(s)) {
                return false;
            }
        }

        return true;
    }
}
