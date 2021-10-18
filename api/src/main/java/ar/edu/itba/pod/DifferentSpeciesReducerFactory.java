package ar.edu.itba.pod;

import static java.util.Objects.requireNonNull;

import java.util.HashSet;
import java.util.Set;

import com.hazelcast.mapreduce.Reducer;
import com.hazelcast.mapreduce.ReducerFactory;

import ar.edu.itba.pod.query3.Q3Answer;

public class DifferentSpeciesReducerFactory implements ReducerFactory<String, Set<String>, Q3Answer>  {

    @Override
    public Reducer<Set<String>, Q3Answer> newReducer(final String hood) {
        return new Q3Reducer(hood);
    }
    
    private static class Q3Reducer extends Reducer<Set<String>, Q3Answer> {

        private String          hoodName;
        private Set<String>     treeNames;

        public Q3Reducer(final String hoodName) {
            this.hoodName = requireNonNull(hoodName);
        }

        @Override
        public void beginReduce() {
            treeNames = new HashSet<>();
        }
        
        @Override
        public void reduce(final Set<String> trees) {
            treeNames.addAll(trees);            
        }
        
        @Override
        public Q3Answer finalizeReduce() {
            return new Q3Answer(hoodName, treeNames.size());
        }

    }
}
