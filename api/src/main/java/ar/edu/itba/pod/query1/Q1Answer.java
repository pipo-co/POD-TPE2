package ar.edu.itba.pod.query1;

import java.io.IOException;
import java.util.Map;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;

public class Q1Answer implements DataSerializable {

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
}
