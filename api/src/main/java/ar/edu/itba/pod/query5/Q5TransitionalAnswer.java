package ar.edu.itba.pod.query5;

import static java.util.Objects.*;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

public class Q5TransitionalAnswer implements DataSerializable {

    private String  street;
    private int     treeCount;

    private Q5TransitionalAnswer(){
        // Serialization
    }

    public Q5TransitionalAnswer(final String street, final int count) {
        this.street     = requireNonNull(street);
        this.treeCount  = count;
    }

    public static Q5TransitionalAnswer fromEntry(final Map.Entry<String, Integer> entry) {
        return new Q5TransitionalAnswer(entry.getKey(), entry.getValue());
    }

    @Override
    public void readData(final ObjectDataInput input) throws IOException {
        street      = input.readUTF();
        treeCount   = input.readInt();
    }

    @Override
    public void writeData(final ObjectDataOutput output) throws IOException {
        output.writeUTF(street);
        output.writeInt(treeCount);
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(!(o instanceof Q5TransitionalAnswer)) return false;
        final Q5TransitionalAnswer that = (Q5TransitionalAnswer) o;
        return treeCount == that.treeCount
            && Objects.equals(street, that.street)
            ;
    }

    @Override
    public int hashCode() {
        return hash(street, treeCount);
    }

    @Override
    public String toString() {
        return "Q5PartialAnswer{" +
            "street='" + street + '\'' +
            ", count=" + treeCount +
            '}';
    }

    public String getStreet() {
        return street;
    }
    public int getTreeCount() {
        return treeCount;
    }
}
