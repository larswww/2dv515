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

        File link = CreateFile(node.link, "Links");
        WriteFile(link, getLinkString(node));

        File word = CreateFile(node.link, "Words");
        WriteFile(word, node.text);

        File raw = CreateFile(node.link, "Raw");
        WriteFile(raw, node.contents);
    }

    private File CreateFile(String name, String dir) {

        //handle links with / in linkname such as http://en.wikipedia.org/wiki/input/output
        while (name.indexOf("/") > 0) {
            name = name.replace("/", "#sl#");
        }

        //to avoid creating hidden files fx .NET.txt
        if (name.startsWith(".")) {
            name = name.replaceFirst(".", "#.#");
        }

        File f = new File(System.getProperty("user.dir") + "/data/" + dir + "/" + root.link);
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
