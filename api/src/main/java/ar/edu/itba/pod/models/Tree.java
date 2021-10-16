package ar.edu.itba.pod.models;

import static java.util.Objects.*;

import java.io.IOException;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;

public class Tree implements DataSerializable {

    private String hoodName;
    private String hoodStreet;
    private String name;

    private Tree() {
        // Serialization
    }

    public Tree(final String hoodName, final String hoodStreet, final String name) {
        this.hoodName      = requireNonNull(hoodName);
        this.hoodStreet    = requireNonNull(hoodStreet);
        this.name          = requireNonNull(name);
    }

    @Override
    public void writeData(final ObjectDataOutput out) throws IOException {
        out.writeUTF(hoodName);
        out.writeUTF(hoodStreet);
        out.writeUTF(name);
    }

    @Override
    public void readData(final ObjectDataInput in) throws IOException {
        hoodName    = in.readUTF();
        hoodStreet  = in.readUTF();
        name        = in.readUTF();
    }

    public String getHoodName() {
        return hoodName;
    }
    public String getHoodStreet() {
        return hoodStreet;
    }
    public String getName() {
        return name;
    }
}
