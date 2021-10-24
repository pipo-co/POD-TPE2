package ar.edu.itba.pod.query5;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

public class Q5PartialAnswer implements DataSerializable {

    private String  street;
    private int     count;

    private Q5PartialAnswer(){
        // Serialization
    }

    public Q5PartialAnswer(final String street, final int count) {
        this.street = street;
        this.count  = count;
    }

    public static Q5PartialAnswer fromEntry(final Map.Entry<String, Integer> entry) {
        return new Q5PartialAnswer(entry.getKey(), entry.getValue());
    }

    @Override
    public void readData(final ObjectDataInput input) throws IOException {
        street  = input.readUTF();
        count   = input.readInt();
    }

    @Override
    public void writeData(final ObjectDataOutput output) throws IOException {
        output.writeUTF(street);
        output.writeInt(count);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Q5PartialAnswer)) return false;
        Q5PartialAnswer that = (Q5PartialAnswer) o;
        return count == that.count
                && Objects.equals(street, that.street);
    }

    @Override
    public int hashCode() {
        return Objects.hash(street, count);
    }

    @Override
    public String toString() {
        return "Q5PartialAnswer{" +
                "street='" + street + '\'' +
                ", count=" + count +
                '}';
    }

    public String getStreet() {
        return street;
    }

    public int getCount() {
        return count;
    }
}
