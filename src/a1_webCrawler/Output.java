package a1_webCrawler;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.BiConsumer;

public class Output {
    private Map<String, LinkNode> linkNodes;
    private LinkNode root;


    public Output(Map<String, LinkNode> linkNodes, LinkNode root) {
        this.linkNodes = linkNodes;
        this.root = root;
        linkNodes.forEach( (key, value) -> Build(value) );

    }

    private void Build(LinkNode node) {
        File links = CreateFile(node.link, "Links");
        WriteFile(links, getLinkString(node));
        File words = CreateFile(node.link, "Words");
        WriteFile(words, node.text);
    }


    //todo handle links that contain slashes..
    private File CreateFile(String name, String dir) {
        String link = root.link;

        while (link.indexOf("/") > 0) {
            link = link.replace("/", "#sl#");
        }

        File f = new File(System.getProperty("user.dir") + "/data/" + dir + "/" + link);
        f = new File(f,name);
        return f;
    }


    private void WriteFile(File directory, String content) {
        File file = new File(directory + ".txt");
        try {
            if (!file.exists()) file.getParentFile().mkdirs();
            file.createNewFile();
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            fw.write(content);
            fw.close();
        } catch (IOException exc) {
            exc.printStackTrace();
        }

    }

    private String getLinkString(LinkNode node) {
        StringBuilder content = new StringBuilder();
        Iterator it = node.successors();

        while (it.hasNext()) {
            LinkNode n = (LinkNode) it.next();
            content.append(n.link + "\n");
        }

        return content.toString();
    }


}
