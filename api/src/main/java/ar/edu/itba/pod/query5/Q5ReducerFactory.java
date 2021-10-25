package ar.edu.itba.pod.query5;

import static java.util.Objects.requireNonNull;

import com.hazelcast.mapreduce.Reducer;
import com.hazelcast.mapreduce.ReducerFactory;

import java.util.*;

public class Q5ReducerFactory implements ReducerFactory<Integer, Set<String>, Collection<Q5Answer>> {

    @Override
    public Reducer<Set<String>, Collection<Q5Answer>> newReducer(final Integer group) {
        return new Q5Reducer(requireNonNull(group));
    }

    private static class Q5Reducer extends Reducer<Set<String>, Collection<Q5Answer>> {

        private final int       group;
        private List<String>    streets;
        private List<Q5Answer>  answers;

        public Q5Reducer(final int group) {
            this.group = group;
        }

        @Override
        public void beginReduce() {
            streets = new LinkedList<>();
            answers = new ArrayList<>();
        }

        @Override
        public void reduce(final Set<String> streetNames) {
            for(final String streetA : streetNames) {
                for(final String streetB : streets) {
                    answers.add(new Q5Answer(group, streetA, streetB));
                }
                streets.add(streetA);
            }
        }

        @Override
        public List<Q5Answer> finalizeReduce() {
            answers.sort(null);
            return answers;
        }
    }
}