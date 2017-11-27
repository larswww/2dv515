package a2_clustering;

public class Centroid {
    Article center;
    Cluster cluster;
    Double[] previous;

    public Centroid(Article art) {
        this.center = art;
        previous = new Double[art.words.size()];
    }

    public void recalcCenter() {

        for (int i = 0; i < center.words.size(); i++) {
            double avg = 0.0;
            for (Article a : cluster) {
                avg += a.words.get(i).count;
            }
            avg /= (double)cluster.size();

            center.words.get(i).count = avg;

        }
    }

    public boolean matchesPreviousAssignment() {
        boolean matches = true;
        for (int i = 0; i < center.words.size(); i++) {
            double current = center.words.get(i).count;
            if (previous[i] == null|| current != previous[i]) matches = false;
            previous[i] = current;
        }

        return matches;
    }

}
