package ar.edu.itba.pod.query4;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.util.Comparator;
import java.util.Objects;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;

public class Q4Answer implements DataSerializable, Comparable<Q4Answer> {

    private static final Comparator<Q4Answer> NATURAL_ORDER = Comparator
        .comparing      (Q4Answer::getHoodA)
        .thenComparing  (Q4Answer::getHoodB)
        ;

    private int     group;
    private String  hoodA;
    private String  hoodB;

    private Q4Answer() {
        // Serialization
    }

    public Q4Answer(final int group, final String firstHood, final String secondHood) {
        this.group = group;
        requireNonNull(firstHood);
        requireNonNull(secondHood);

        final boolean ordered = firstHood.compareTo(secondHood) < 0;
        this.hoodA = ordered ? firstHood  : secondHood;
        this.hoodB = ordered ? secondHood : firstHood;
    }

    @Override
    public int compareTo(final Q4Answer o) {
        return NATURAL_ORDER.compare(this, o);
    }

    @Override
    public void readData(final ObjectDataInput input) throws IOException {
        group = input.readInt();
        hoodA = input.readUTF();
        hoodB = input.readUTF();
    }

    @Override
    public void writeData(final ObjectDataOutput output) throws IOException {
        output.writeInt(group);
        output.writeUTF(hoodA);
        output.writeUTF(hoodB);  
    }

    @Override
    public boolean equals(final Object o) {
        if(this == o) return true;
        if(!(o instanceof Q4Answer)) return false;
        final Q4Answer q4Answer = (Q4Answer) o;
        return group == q4Answer.group
            && Objects.equals(hoodA, q4Answer.hoodA)
            && Objects.equals(hoodB, q4Answer.hoodB)
            ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(group, hoodA, hoodB);
    }

    @Override
    public String toString() {
        return "Q4Answer{" +
            "group='" + group + '\'' +
            ", neighbourhood=" + hoodA +
            ", neighbourhood=" + hoodB +
            '}';
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
