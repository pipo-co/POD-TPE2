package ar.edu.itba.pod.query4;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.hazelcast.mapreduce.Reducer;
import com.hazelcast.mapreduce.ReducerFactory;

public class Q4ReducerFactory implements ReducerFactory<Integer, Set<String>, List<Q4Answer>>{

    @Override
    public Reducer<Set<String>, List<Q4Answer>> newReducer(final Integer hundred) {
        return new Q4Reducer(hundred);
    }
    
    private static class Q4Reducer extends Reducer<Set<String>, List<Q4Answer>> {
        
        private final int hundred;
        private Set<String> hoods;
        private List<Q4Answer> answers;

        private static final Comparator<Q4Answer> ANSWER_ORDER = Comparator
        .comparing(Q4Answer::getHoodA)
        .thenComparing(Q4Answer::getHoodB)
        ; 

        public Q4Reducer(final int hundred) {
            this.hundred = hundred;
        }

        @Override
        public void beginReduce() {
            hoods       = new HashSet<>();
            answers     = new ArrayList<>();
        }

        @Override
        public void reduce(Set<String> hoodNames) {
            for (String hoodA : hoodNames) {
                for (String hoodB : hoods) {
                    answers.add(new Q4Answer(hundred, hoodA, hoodB));
                }
                hoods.add(hoodA);
            }
        }

        @Override
        public List<Q4Answer> finalizeReduce() {
            answers.sort(ANSWER_ORDER);
            return answers;
        }

    }
}
