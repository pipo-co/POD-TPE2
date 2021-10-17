package ar.edu.itba.pod.query2;

import static java.util.Objects.*;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import com.hazelcast.mapreduce.Reducer;
import com.hazelcast.mapreduce.ReducerFactory;

import ar.edu.itba.pod.models.Neighbourhood;

public class Q2ReducerFactory implements ReducerFactory<Neighbourhood, Map<String, Long>, Q2Answer> {

    @Override
    public Reducer<Map<String, Long>, Q2Answer> newReducer(final Neighbourhood neighbourhood) {
        return new Q2Reducer(neighbourhood);
    }

    private static class Q2Reducer extends Reducer<Map<String, Long>, Q2Answer> {

        private static final Comparator<Map.Entry<String, Long>> BY_VALUE_ORDER = Map.Entry.comparingByValue();

        private final   Neighbourhood       hood;
        private         Map<String, Long>   treesCountMap;

        public Q2Reducer(final Neighbourhood neighbourhood) {
            this.hood = requireNonNull(neighbourhood);

        }

        @Override
        public void beginReduce() {
            this.treesCountMap = new HashMap<>();
        }

        @Override
        public void reduce(final Map<String, Long> entryMap) {
            entryMap.forEach((k, v) -> treesCountMap.merge(k, v, Long::sum));
        }

        @Override
        public Q2Answer finalizeReduce() {
            final Map.Entry<String, Long> bestTree = treesCountMap.entrySet()
                .stream()
                .max(BY_VALUE_ORDER)
                .orElseThrow()
                ;
            return new Q2Answer(hood, bestTree.getKey(), bestTree.getValue());
        }
    }
}
