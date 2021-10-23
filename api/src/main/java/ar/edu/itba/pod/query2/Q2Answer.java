package ar.edu.itba.pod.query2;

import java.io.IOException;
import java.util.Objects;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;

import ar.edu.itba.pod.models.Neighbourhood;

public class Q2Answer implements DataSerializable {

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
    public void readData(final ObjectDataInput input) throws IOException {
        hoodName            = input.readUTF();
        treeName            = input.readUTF();
        treesPerInhabitant  = input.readDouble();
    }

    @Override
    public void writeData(final ObjectDataOutput output) throws IOException {
        output.writeUTF     (hoodName);
        output.writeUTF     (treeName);
        output.writeDouble  (treesPerInhabitant);
    }

    @Override
    public boolean equals(final Object o) {
        if(this == o) return true;
        if(!(o instanceof Q2Answer)) return false;
        final Q2Answer q2Answer = (Q2Answer) o;
        return Double.compare(q2Answer.treesPerInhabitant, treesPerInhabitant) == 0
            && Objects.equals(hoodName, q2Answer.hoodName)
            && Objects.equals(treeName, q2Answer.treeName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hoodName, treeName, treesPerInhabitant);
    }

    @Override
    public String toString() {
        return "Q2Answer{" +
            "hood='" + hoodName + '\'' +
            ", tree=" + treeName + '\'' +
            ", treesPerInhabitant=" + treesPerInhabitant +
            '}';
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
}
