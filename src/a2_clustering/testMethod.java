package a2_clustering;
import org.junit.*;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;



public class testMethod {
    List<Cluster> clusters = new ArrayList<Cluster>();
    ArrayList<Article> articles;
    ArrayList<String> html = new ArrayList<>();
    CalculatePearson metric = new CalculatePearson();
    ClusteringDB db = new ClusteringDB();
    ArrayList<Centroid> centroids = new ArrayList<>();

    @Before
    public void setup() {
        db.seedDatabase("/Users/mbp/Documents/Code/2dv515/A1/blogdata.txt");
        articles = db.articles;

        for (Article art : articles )
        {
            clusters.add(new Cluster(art));
        }

        kMeans();

//        while (clusters.size() > 1) {
//            iterate();
//        }
//
//        buildTree();
//        new PrintHtml(html);
    }

    @Test
    public void testStuff() {

    }

    public void buildTree() {
        html = new ArrayList<>();
        html.add("<ul>");
        html.add("</ul>");

        addNodes(1, clusters.get(0));
    }

    private void addNodes(int i, Cluster c) {
        if (c.right != null) generateHtml(i, c.right);
        if (c.left != null) generateHtml(i, c.left);
    }

    private void generateHtml(int i, Cluster c) {

        String art = c.toString();
        if (art.equals("")) {
            html.add(i, "<li><ul>");
            html.add(i+1, "</ul></li>");
        } else {
            art = art.replaceAll("\"", "'");
            html.add(i, "<li data-jstree='{\"disabled\":true}'>" + art + "</li>");
        }
        addNodes(i + 1, c);
    }

    private void addNodes(DefaultMutableTreeNode tnode, Cluster c) {

        if (c.left != null) {
            DefaultMutableTreeNode nNode = new DefaultMutableTreeNode(c.left.toString());
            tnode.add(nNode);
            addNodes(nNode, c.left);
        }

        if (c.right != null) {
            DefaultMutableTreeNode nNode = new DefaultMutableTreeNode(c.right.toString());
            tnode.add(nNode);
            addNodes(nNode, c.right);
        }

    }

    private void kMeans() {
        centroids = new ArrayList<>();
        Randomizer rnd = new Randomizer(articles);
        int k = 10;

        for (int i = 0; i < k; i++) {
            centroids.add(rnd.createRandom());
        }

        boolean done = false;
        int cnt = 0;

        while (!done) {
            iterateCentroids();

            for (Centroid c : centroids) {
                c.recalcCenter();
            }

            done = true;

            for (Centroid c : centroids) {

                if (!c.matchesPreviousAssignment()) {
                    done = false;
                }

            }

            cnt++;
        }

        System.out.print("Iterations: " + cnt + " Centroids: " + centroids.size());

    }

    public void iterateCentroids() {

        for (Centroid c : centroids) {
            c.cluster = null;
        }

        for (int i = 0; i < articles.size(); i++) {
            double closest = Double.MAX_VALUE;
            Centroid bestCentroid = null;
            Article bestArticle = null;

            for (int j = 0; j < centroids.size(); j++) {
                Centroid centroid = centroids.get(j);
                Article a = articles.get(i);


                double distance = metric.getSimilarity(centroid.center, a);
                if (distance < closest) {
                    closest = distance;
                    bestCentroid = centroid;
                    bestArticle = a;
                }

            }

            if (bestCentroid.cluster == null) bestCentroid.cluster = new Cluster(bestCentroid.center);
                bestCentroid.cluster = bestCentroid.cluster.merge(new Cluster(bestArticle), closest);
        }
    }

    public void iterate() {

        double closest = Double.MAX_VALUE;
        Cluster bestA = null;
        Cluster bestB = null;

        for (int i = 0; i < clusters.size(); i++) {

            for (int j = i + 1; j < clusters.size(); j++) {
                Cluster cA = clusters.get(i);
                Cluster cB = clusters.get(j);

                double distance = metric.getSimilarity(cA.article, cB.article);
                if (distance < closest) {
                    closest = distance;
                    bestA = cA;
                    bestB = cB;
                }
            }
        }

        Cluster m = bestA.merge(bestB, closest);
        clusters.add(m);

        clusters.remove(bestA);
        clusters.remove(bestB);
    }
}
