package ar.edu.itba.pod.query5;

import com.hazelcast.mapreduce.Context;
import com.hazelcast.mapreduce.Mapper;

public class Q5Mapper implements Mapper<String, Q5PartialAnswer, Integer, String> {

    private static int truncateLastDigit(final int number) {
        return (number / 10) * 10;
    }

    @Override
    public void map(String s, Q5PartialAnswer ans, Context<Integer, String> context) {
        final int group = truncateLastDigit(ans.getCount());
        if(group > 0) {
            context.emit(group, ans.getStreet());
        }
    }
}
