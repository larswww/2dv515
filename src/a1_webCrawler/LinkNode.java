package a1_webCrawler;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class LinkNode {
    Set<LinkNode> preds = new HashSet<LinkNode>();
    Set<LinkNode> succs = new HashSet<LinkNode>();
    int bfsNo;
    String link;
    String contents;
    String text;

    LinkNode(String lk) {
        link = lk.substring(6);
    }

    @Override
    public int hashCode() {
        return link.hashCode();
    }

    @Override
    public boolean equals(Object o) {
            if (!(o instanceof LinkNode)) return false;
            LinkNode ln = (LinkNode) o;
            return this.link.equals(ln.link);

    }

    public Iterator<LinkNode> successors() {

        return succs.iterator();
    }
}
