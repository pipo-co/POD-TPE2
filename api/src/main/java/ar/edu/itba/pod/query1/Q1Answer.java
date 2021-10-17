package ar.edu.itba.pod.query1;

import java.io.IOException;
import java.util.Comparator;
import java.util.Map;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;

import ar.edu.itba.pod.SortCollator;

public class Q1Answer implements DataSerializable, Comparable<Q1Answer> {

    public static final EntryToAnswerMapper FROM_ENTRY_MAPPER = new EntryToAnswerMapper();

    private static final Comparator<Q1Answer> NATURAL_ORDER = Comparator
        .comparingInt   (Q1Answer::getTreeCount).reversed()
        .thenComparing  (Q1Answer::getHood)
        ;

    private String    hood;
    private int       treeCount;

    private Q1Answer() {
        // Serialization
    }

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

    @Override
    public void writeData(final ObjectDataOutput out) throws IOException {
        out.writeUTF(hood);
        out.writeInt(treeCount);
    }

    @Override
    public void readData(final ObjectDataInput in) throws IOException {
        hood        = in.readUTF();
        treeCount   = in.readInt();
    }

    @Override
    public String toString() {
        return "Q1Answer{" +
            "hood='" + hood + '\'' +
            ", treeCount=" + treeCount +
            '}';
    }

    public String getHood() {
        return hood;
    }
    public int getTreeCount() {
        return treeCount;
    }

    private static class EntryToAnswerMapper implements SortCollator.EntryToSortableMapper<String, Integer, Q1Answer> {
        @Override
        public Q1Answer toSortable(final Map.Entry<String, Integer> entry) {
            return Q1Answer.fromEntry(entry);
        }
    }
}
