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

    public Q4Answer(final int group, final String firstHood, final String secondHood) {
        this.group = group;
        setHoods(firstHood, secondHood);
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
    public void readData(final ObjectDataInput input) throws IOException {
        group               = input.readInt();  
        hoodA               = input.readUTF();
        hoodB               = input.readUTF();
        
    }

    @Override
    public void writeData(final ObjectDataOutput output) throws IOException {
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

    private void setHoods(final String firstHood, final String secondHood) {
        final int compare = firstHood.compareTo(secondHood);
        hoodA = compare < 0? firstHood : secondHood;
        hoodB = compare < 0? secondHood : firstHood;
    }
    
}
