package ar.edu.itba.pod;

import java.util.HashSet;
import java.util.Set;

import com.hazelcast.mapreduce.Combiner;
import com.hazelcast.mapreduce.CombinerFactory;

public class DifferentSpeciesCombinerFactory implements CombinerFactory<String, String, Set<String>> {

    @Override
    public Combiner<String, Set<String>> newCombiner(String hoodName) {
        return new Q3Combiner();
    }
    
    private static class Q3Combiner extends Combiner<String, Set<String>> {

        private Set<String> treeNames;

        @Override
        public void beginCombine() {
            treeNames = new HashSet<>();
        }


        @Override
        public void combine(String treeName) {
            treeNames.add(treeName);            
        }

        @Override
        public Set<String> finalizeChunk() {
            return treeNames;
        }

        @Override
        public void reset() {
            treeNames = new HashSet<>();
        }

    }
}
