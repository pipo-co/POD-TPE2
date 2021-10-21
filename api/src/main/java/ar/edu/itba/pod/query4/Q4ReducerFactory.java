package ar.edu.itba.pod.query4;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.hazelcast.mapreduce.Reducer;
import com.hazelcast.mapreduce.ReducerFactory;

public class Q4ReducerFactory implements ReducerFactory<Integer, Set<String>, List<Q4Answer>>{

    @Override
    public Reducer<Set<String>, List<Q4Answer>> newReducer(final Integer group) {
        return new Q4Reducer(group);
    }
    
    private static class Q4Reducer extends Reducer<Set<String>, List<Q4Answer>> {

        private final   int             group;
        private         List<String>    hoods;
        private         List<Q4Answer>  answers;

        public Q4Reducer(final int group) {
            this.group = group;
        }

        @Override
        public void beginReduce() {
            hoods       = new LinkedList<>();
            answers     = new ArrayList<>();
        }

        @Override
        public void reduce(final Set<String> hoodNames) {
            for(final String hoodA : hoodNames) {
                for(final String hoodB : hoods) {
                    answers.add(new Q4Answer(group, hoodA, hoodB));
                }
                hoods.add(hoodA);
            }
        }

        @Override
        public List<Q4Answer> finalizeReduce() {
            answers.sort(Comparator.naturalOrder());
            return answers;
        }
    }
}
