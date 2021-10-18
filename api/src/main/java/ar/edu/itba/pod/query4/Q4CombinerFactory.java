package ar.edu.itba.pod.query4;

import java.util.HashSet;
import java.util.Set;

import com.hazelcast.mapreduce.Combiner;
import com.hazelcast.mapreduce.CombinerFactory;

public class Q4CombinerFactory implements CombinerFactory<Integer, String, Set<String>> {

    @Override
    public Combiner<String, Set<String>> newCombiner(final Integer hundred) {
        return new Q4Combiner();
    }
    
    private static class Q4Combiner extends Combiner<String, Set<String>> {

        private Set<String> hoodsSet;

        @Override
        public void beginCombine() {
            hoodsSet = new HashSet<>();
        }


        @Override
        public void combine(String hoodName) {
            hoodsSet.add(hoodName);
            
        }

        @Override
        public Set<String> finalizeChunk() {
            return hoodsSet;
        }

        @Override
        public void reset() {
            hoodsSet = new HashSet<>();
        }
    }
}
