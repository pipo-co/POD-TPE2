package ar.edu.itba.pod.query2;

import java.io.IOException;
import java.util.Comparator;
import java.util.Map;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;

import ar.edu.itba.pod.SortCollator;
import ar.edu.itba.pod.models.Neighbourhood;

public class Q2Answer implements DataSerializable, Comparable<Q2Answer> {

    public static final EntryToAnswerMapper FROM_ENTRY_MAPPER = new EntryToAnswerMapper();

    private static final Comparator<Q2Answer> NATURAL_ORDER = Comparator.comparing(Q2Answer::getHoodName);

    private String hoodName;
    private String treeName;
    private double treesPerInhabitant;

    private Q2Answer() {
        // Serialization
    }

    public Q2Answer(final Neighbourhood hood, final String treeName, final long totalTrees) {
        this.hoodName            = hood.getName();
        this.treeName            = treeName;
        this.treesPerInhabitant  = (double) totalTrees / hood.getPopulation();
    }

    @Override
    public int compareTo(final Q2Answer o) {
        return NATURAL_ORDER.compare(this, o);
    }

    @Override
    public String toString() {
        return "Q2Answer{" +
            "hood='" + hoodName + '\'' +
            ", tree=" + treeName + '\'' +
            ", treesPerInhabitant=" + treesPerInhabitant +
            '}';
    }

    @Override
    public void readData(ObjectDataInput input) throws IOException {
        hoodName            = input.readUTF();
        treeName            = input.readUTF();
        treesPerInhabitant  = input.readDouble();
    }

    @Override
    public void writeData(ObjectDataOutput output) throws IOException {
        output.writeUTF     (hoodName);
        output.writeUTF     (treeName);
        output.writeDouble  (treesPerInhabitant);
    }

    public String getHoodName() {
        return hoodName;
    }
    public String getTreeName() {
        return treeName;
    }
    public double getTreesPerInhabitant() {
        return treesPerInhabitant;
    }

    private static class EntryToAnswerMapper implements SortCollator.EntryToSortableMapper<Neighbourhood, Q2Answer, Q2Answer> {
        @Override
        public Q2Answer toSortable(final Map.Entry<Neighbourhood, Q2Answer> entry) {
            return entry.getValue();
        }
    }
}
