package ar.edu.itba.pod.utils.combiners;

import com.hazelcast.mapreduce.Combiner;
import com.hazelcast.mapreduce.CombinerFactory;

public class CountCombinerFactory implements CombinerFactory<String, Integer, Integer> {

    @Override
    public Combiner<Integer, Integer> newCombiner(final String key) {
        return new CountCombiner();
    }

    private static class CountCombiner extends Combiner<Integer, Integer> {

        private int count;

        @Override
        public void beginCombine() {
            count = 0;
        }

        @Override
        public void combine(final Integer num) {
            count += num;
        }

        @Override
        public Integer finalizeChunk() {
            return count;
        }

        @Override
        public void reset() {
            beginCombine();
        }
    }
}
