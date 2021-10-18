package ar.edu.itba.pod.query4;

import com.hazelcast.mapreduce.Context;
import com.hazelcast.mapreduce.Mapper;

import ar.edu.itba.pod.query3.Q3Answer;

public class Q4Mapper implements Mapper<String, Q3Answer, Integer, String>  {

    @Override
    public void map(String collectionName, Q3Answer ans, Context<Integer, String> context) {
       context.emit(toHundreds(ans.getDifferentSpecies()), ans.getHoodName());
        
    }
    
    private int toHundreds(int number) {
        return (number / 100) * 100;
    }
}
