package a2_clustering;

public class CalculatePearson {

    public CalculatePearson() {

    }

    public double getSimilarity(Article A, Article B) {

        // calculate simple and squared sums
        double sumA = 0;
        double sumAsq = 0;
        for (Word w : A.words) {
            sumA += w.count;
            sumAsq += Math.pow(w.count, 2);
        }

        double sumB = 0;
        double sumBsq = 0;
        for (Word w : B.words) {
            sumB += w.count;
            sumBsq += Math.pow(w.count, 2);
        }

        double pSum = 0;
        int n = Math.min(A.words.size(), B.words.size());

        for (int i = 0; i < n; i++) {
            pSum += A.words.get(i).count * B.words.get(i).count;
        }

        double num = pSum - (sumA * sumB / n);
        double den = Math.sqrt((sumAsq - Math.pow(sumA, 2) / n) * (sumBsq - Math.pow(sumB, 2) / n));

        if (den == 0) return 0;

        return 1.0 - num / den;
    }
}
