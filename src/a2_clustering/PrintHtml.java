package a2_clustering;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class PrintHtml {
    private String top = "<!DOCTYPE html>\n" +
            "<html>\n" +
            "<head>\n" +
            "    <meta charset=\"utf-8\">\n" +
            "    <title>jsTree test</title>\n" +
            "    <!-- 2 load the theme CSS file -->\n" +
            "    <link rel=\"stylesheet\" href=\"https://cdnjs.cloudflare.com/ajax/libs/jstree/3.2.1/themes/default/style.min.css\" /></head>\n" +
            "<body>\n" +
            "<!-- 3 setup a container element -->\n" +
            "<div id=\"jstree\">";

    private String bottom = "</div>\n" +
            "<button>demo button</button>\n" +
            "\n" +
            "<!-- 4 include the jQuery library -->\n" +
            "<script src=\"https://cdnjs.cloudflare.com/ajax/libs/jquery/1.12.1/jquery.min.js\"></script>\n" +
            "<!-- 5 include the minified jstree source -->\n" +
            "<script src=\"https://cdnjs.cloudflare.com/ajax/libs/jstree/3.2.1/jstree.min.js\"></script>\n" +
            "<script>\n" +
            "    $(function () {\n" +
            "        // 6 create an instance when the DOM is ready\n" +
            "        $('#jstree').jstree();\n" +
            "        // 7 bind to events triggered on the tree\n" +
            "        $('#jstree').on(\"changed.jstree\", function (e, data) {\n" +
            "            console.log(data.selected);\n" +
            "        });\n" +
            "        // 8 interact with the tree - either way is OK\n" +
            "        $('button').on('click', function () {\n" +
            "            $('#jstree').jstree(true).select_node('child_node_1');\n" +
            "            $('#jstree').jstree('select_node', 'child_node_1');\n" +
            "            $.jstree.reference('#jstree').select_node('child_node_1');\n" +
            "        });\n" +
            "    });\n" +
            "</script>\n" +
            "</body>\n" +
            "</html>";

    public PrintHtml(ArrayList<String> html) {
        StringBuilder sb = new StringBuilder();
        sb.append(top);
        html.forEach(s -> sb.append(s).append("\n"));
        sb.append(bottom);
        File f = CreateFile("lectureHtml");
        WriteFile(f, sb.toString());
    }

    private File CreateFile(String name) {
        File f = new File(System.getProperty("user.dir") + "/data/");
        f = new File(f,name);
        return f;
    }

    private void WriteFile(File directory, String content) {
        File file = new File(directory + ".html");
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
}
