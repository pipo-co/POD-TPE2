package ar.edu.itba.pod.query2;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import com.hazelcast.mapreduce.Reducer;
import com.hazelcast.mapreduce.ReducerFactory;

import ar.edu.itba.pod.models.Neighbourhood;

public class Q2ReducerFactory implements ReducerFactory<Neighbourhood, Map<String, Long>, Q2Answer> {

    @Override
    public Reducer<Map<String, Long>, Q2Answer> newReducer(Neighbourhood neighbourhood) {
        return new Q2Reducer(neighbourhood);
    }

    private static class Q2Reducer extends Reducer<Map<String, Long>, Q2Answer> {

        private static final Comparator<Map.Entry<String, Long>> NATURAL_ORDER = Comparator
            .comparing   (Map.Entry::getKey)
        ;
        private final Map<String, Long> treesCountMap;
        private final Q2Answer answer;

        public Q2Reducer(final Neighbourhood neighbourhood) {
            this.treesCountMap = new HashMap<>();
            this.answer = new Q2Answer(neighbourhood);
        }

        @Override
        public Q2Answer finalizeReduce() {
            this.answer.setTreeParams(treesCountMap.entrySet().stream().max(NATURAL_ORDER).get());
            return answer;
        }

        @Override
        public void reduce(final Map<String, Long> entryMap) {
            entryMap.forEach(
                (key, value) -> treesCountMap.put(key,
                treesCountMap.containsKey(key) ? treesCountMap.get(key) + value : value));
        }
            


    }
}
