package a1_webCrawler;

import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OpenPage {
    //    String pageUrl = "/wiki/Programming_language";
    private int maxLevel = 2;
    private int maxLinks = 1000;
    List<LinkNode> links = new ArrayList<>();
    Set<LinkNode> visited = new HashSet<LinkNode>();
    Map<String, LinkNode> traversed = new HashMap<String, LinkNode>();

    public OpenPage() {
        links.add(new LinkNode("/wiki/Programming_language"));
        BFS();

        Iterator it = links.iterator();
        while (it.hasNext()) {
            System.out.println(it.next());
        }
    }

    private void VisitLink(LinkNode node) {
        CreateURL(node);
        traversed.put(node.link, node); //todo how to handle not found/dead links?
        ExtractLinks(node);
    }

    private class VisitThread extends Thread {

        public VisitThread(LinkNode node) {
            CreateURL(node);
            traversed.put(node.link, node); //todo how to handle not found/dead links?
            ExtractLinks(node);
        }
    }

    private void BFS() {
        int bfsNo = 0;

        // loop based on depth requirement
        // and only whilst max links is below max links requirement
        while (traversed.size() <= maxLinks && !links.isEmpty()) {
            LinkNode link = links.remove(0); //todo use a treeSet instead?

            if (!visited.contains(link)) {
                link.bfsNo = bfsNo++;
                visited.add(link);
                VisitThread vl = new VisitThread(link);
                vl.start();

//                VisitLink(link);
            }
        }

        System.out.println("done");
    }

    private String CreateURL(LinkNode node) {
        String contents = "";
        try {
            URL url = new URL("https://en.wikipedia.org" + node.link);
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            String line;

            while ((line = reader.readLine()) != null) {
                //todo use a StringBuilder instead?
                contents += (line + "\n");
            }
            reader.close();
            node.contents = contents;

        } catch (Exception e) {
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

    private class LinkNode {
        //todo associate the contents with each node?
        Set<LinkNode> preds = new HashSet<LinkNode>();
        Set<LinkNode> succs = new HashSet<LinkNode>();
        int bfsNo;
        String link;
        String contents;

        LinkNode(String lk) {
            link = lk;
        }

        @Override
        public int hashCode() {
            return link.hashCode();
        }
    }
}
