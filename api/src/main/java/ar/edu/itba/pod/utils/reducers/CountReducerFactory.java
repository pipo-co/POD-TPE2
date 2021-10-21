package ar.edu.itba.pod.utils.reducers;

import com.hazelcast.mapreduce.Reducer;
import com.hazelcast.mapreduce.ReducerFactory;

public class CountReducerFactory implements ReducerFactory<String, Integer, Integer> {

    @Override
    public Reducer<Integer, Integer> newReducer(final String key) {
        return new CountReducer();
    }
    
    private static class CountReducer extends Reducer<Integer, Integer> {

        private int count;

        @Override
        public void beginReduce() {
            count = 0;
        }

        @Override
        public void reduce(final Integer num) {
            count += num;
        }

        @Override
        public Integer finalizeReduce() {
            return count;
        }
    }
}
