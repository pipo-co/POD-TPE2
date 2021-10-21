package ar.edu.itba.pod.query2;

import java.util.HashMap;
import java.util.Map;

import com.hazelcast.mapreduce.Combiner;
import com.hazelcast.mapreduce.CombinerFactory;

import ar.edu.itba.pod.models.Neighbourhood;

public class Q2CombinerFactory implements CombinerFactory<Neighbourhood, String, Map<String, Long>> {

    @Override
    public Combiner<String, Map<String, Long>> newCombiner(final Neighbourhood hood) {
        return new Q2Combiner();
    }
    
    private static class Q2Combiner extends Combiner<String, Map<String, Long>> {

        private Map<String, Long> treesCountMap;

        @Override
        public void beginCombine() {
            treesCountMap = new HashMap<>();
        }

        @Override
        public void combine(final String treeName) {
            final Long currentCount = treesCountMap.get(treeName);
            treesCountMap.put(treeName, currentCount == null ? 1 : currentCount + 1);
        }

        @Override
        public Map<String, Long> finalizeChunk() {
            return treesCountMap;
        }

        @Override
        public void reset() {
            beginCombine();
        }
    }
}
