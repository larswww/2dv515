package a1_webCrawler;

import org.jsoup.*;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CrawlRoot {
    private int maxLinks = 999; // + root hence if 1000 = 999
    List<LinkNode> links = new ArrayList<>();
    Set<String> visited = new HashSet<String>();
    Map<String, LinkNode> bfsResult = new HashMap<String, LinkNode>();

    public CrawlRoot(LinkNode root) {
        root.bfsNo = 0;
        visited.add(root.link);
        VisitLink(root);
        BFS();
    }

    private void VisitLink(LinkNode node) {
        CreateURL(node);
        ExtractLinks(node);
        getText(node);
        bfsResult.put(node.link, node);

    }

    private void getText(LinkNode node) {
        Document doc = Jsoup.parse(node.contents);
        //todo do i want these links?
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
        int bfsNo = 1; //starts from 1 since root is initiated with bfs 0

        while (visited.size() <= maxLinks && !links.isEmpty()) {
            LinkNode node = links.remove(0);

            if (!visited.contains(node.link)) {
                node.bfsNo = bfsNo++;
                visited.add(node.link);

                VisitLink(node);
            }
        }
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
            if (contents == null) {
                CreateURL(node);
            } else {
               return node.contents = contents;
            }
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
                    if (!visited.contains(ln.link) || !links.contains(ln)) {
                        ln.preds.add(node);
                        node.succs.add(ln);
                        links.add(ln);
                    }
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
