package ar.edu.itba.pod.query1;

import java.util.Comparator;
import java.util.Map;

public class Q1Answer implements Comparable<Q1Answer> {

    private static final Comparator<Q1Answer> NATURAL_ORDER = Comparator
        .comparingInt   (Q1Answer::getTreeCount).reversed()
        .thenComparing  (Q1Answer::getHood)
        ;

    private final String    hood;
    private final int       treeCount;

    public Q1Answer(final String hood, final int treeCount) {
        this.hood       = hood;
        this.treeCount  = treeCount;
    }

    public static Q1Answer fromEntry(final Map.Entry<String, Integer> entry) {
        return new Q1Answer(entry.getKey(), entry.getValue());
    }

    @Override
    public int compareTo(final Q1Answer o) {
        return NATURAL_ORDER.compare(this, o);
    }

    public String getHood() {
        return hood;
    }

    public int getTreeCount() {
        return treeCount;
    }
}
