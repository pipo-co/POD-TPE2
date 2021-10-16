package ar.edu.itba.pod.query1;

import com.hazelcast.mapreduce.Reducer;
import com.hazelcast.mapreduce.ReducerFactory;

public class UselessReducerFactory implements ReducerFactory<String, Integer, Integer> {

    @Override
    public Reducer<Integer, Integer> newReducer(String neighbourhood) {
        // TODO Auto-generated method stub
        return new UselessReducer();
    }
    
    private class UselessReducer extends Reducer<Integer, Integer> {

        private volatile int value;
        @Override
        public Integer finalizeReduce() {
            return value;
        }

        @Override
        public void reduce(Integer value) {
            this.value = value; 
        }

    }
}
