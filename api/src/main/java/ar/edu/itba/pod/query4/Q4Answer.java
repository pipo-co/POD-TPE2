package ar.edu.itba.pod.query4;

import java.io.IOException;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;

public class Q4Answer implements DataSerializable {
    
    private int group;
    private String hoodA;
    private String hoodB;

    private Q4Answer() {
        //static
    }


    @Override
    public String toString() {
        return "Q4Answer{" +
            "group='" + group + '\'' +
            ", neighbourhood=" + hoodA +
            ", neighbourhood=" + hoodB +
            '}';
    }

    @Override
    public void readData(ObjectDataInput input) throws IOException {
        group               = input.readInt();  
        hoodA               = input.readUTF();
        hoodB               = input.readUTF();
        
    }

    @Override
    public void writeData(ObjectDataOutput output) throws IOException {
        output.writeInt(group);
        output.writeUTF(hoodA);
        output.writeUTF(hoodB);  
    }

    public int getGroup() {
        return group;
    }

    public String getHoodA() {
        return hoodA;
    }

    public String getHoodB() {
        return hoodB;
    }
    
}
