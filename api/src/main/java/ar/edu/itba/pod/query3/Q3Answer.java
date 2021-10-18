package ar.edu.itba.pod.query3;

import java.io.IOException;
import java.util.Comparator;
import java.util.Map;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;

import ar.edu.itba.pod.SortCollator;

public class Q3Answer implements DataSerializable, Comparable<Q3Answer> {

    public static final EntryToAnswerMapper FROM_ENTRY_MAPPER = new EntryToAnswerMapper();

    private static final Comparator<Q3Answer> NATURAL_ORDER = Comparator.comparing(Q3Answer::getDifferentSpecies).reversed();
    
    private String hoodName;
    private int differentSpecies;

    private Q3Answer() {
        // Serialization
    }

    public Q3Answer(final String hoodName, final int differentSpecies) {
        this.hoodName = hoodName;
        this.differentSpecies = differentSpecies;
    }

    @Override
    public int compareTo(Q3Answer o) {
        return NATURAL_ORDER.compare(this, o);
    }

    @Override
    public String toString() {
        return "Q3Answer{" +
            "hood='" + hoodName + '\'' +
            ", differentTreeSpecies=" + differentSpecies +
            '}';
    }

    @Override
    public void readData(ObjectDataInput input) throws IOException {
        hoodName            = input.readUTF();
        differentSpecies    = input.readInt();        
    }

    @Override
    public void writeData(ObjectDataOutput output) throws IOException {
        output.writeUTF(hoodName);
        output.writeInt(differentSpecies);        
    }

    public String getHoodName() {
        return hoodName;
    }

    public int getDifferentSpecies() {
        return differentSpecies;
    }

    private static class EntryToAnswerMapper implements SortCollator.EntryToSortableMapper<String, Q3Answer, Q3Answer> {
        @Override
        public Q3Answer toSortable(final Map.Entry<String, Q3Answer> entry) {
            return entry.getValue();
        }
    }

}
