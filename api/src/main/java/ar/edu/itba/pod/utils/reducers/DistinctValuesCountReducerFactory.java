package ar.edu.itba.pod.utils.reducers;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.hazelcast.mapreduce.Reducer;
import com.hazelcast.mapreduce.ReducerFactory;

public class DistinctValuesCountReducerFactory<Key, Values extends Collection<?>> implements ReducerFactory<Key, Values, Integer> {

    @Override
    public DistinctValuesCountReducer<Values> newReducer(final Key key) {
        return new DistinctValuesCountReducer<>();
    }
    
    private static class DistinctValuesCountReducer<Values extends Collection<?>> extends Reducer<Values, Integer> {

        private Set<Object> distinctValues;

        @Override
        public void beginReduce() {
            distinctValues = new HashSet<>();
        }

        @Override
        public void reduce(final Values values) {
            distinctValues.addAll(values);
        }
        
        @Override
        public Integer finalizeReduce() {
            return distinctValues.size();
        }
    }
}
