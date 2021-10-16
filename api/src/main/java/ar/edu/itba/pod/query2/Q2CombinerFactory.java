package ar.edu.itba.pod.query2;

import java.util.HashMap;
import java.util.Map;

import com.hazelcast.mapreduce.Combiner;
import com.hazelcast.mapreduce.CombinerFactory;

import ar.edu.itba.pod.models.Neighbourhood;

public class Q2CombinerFactory implements CombinerFactory<Neighbourhood, String, Map<String, Long>> {

    @Override
    public Combiner<String, Map<String, Long>> newCombiner(Neighbourhood hood) {
        return new Q2Combiner();
    }
    
    private static class Q2Combiner extends Combiner<String, Map<String, Long>> {

        private Map<String, Long> treesCountMap;

        @Override
        public void beginCombine() {
            treesCountMap = new HashMap<>();
        }

        @Override
        public void combine(String treeName) {
            treesCountMap.put(treeName, treesCountMap.containsKey(treeName) ? treesCountMap.get(treeName) + 1 : 1);
            
        }

        @Override
        public Map<String, Long> finalizeChunk() {
            return treesCountMap;
        }

        @Override
        public void reset() {
            treesCountMap = new HashMap<>();
        }

    }
}
