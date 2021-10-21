package ar.edu.itba.pod;

import java.util.HashSet;
import java.util.Set;

import com.hazelcast.mapreduce.Combiner;
import com.hazelcast.mapreduce.CombinerFactory;

public class ValueSetCombinerFactory<Key, Value> implements CombinerFactory<Key, Value, Set<Value>> {

    @Override
    public Combiner<Value, Set<Value>> newCombiner(final Key key) {
        return new ValueSetCombiner<>();
    }
    
    private static class ValueSetCombiner<Value> extends Combiner<Value, Set<Value>> {

        private Set<Value> valueSet;

        @Override
        public void beginCombine() {
            valueSet = new HashSet<>();
        }


        @Override
        public void combine(final Value value) {
            valueSet.add(value);
        }

        @Override
        public Set<Value> finalizeChunk() {
            return valueSet;
        }

        @Override
        public void reset() {
            beginCombine();
        }
    }
}
