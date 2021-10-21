package ar.edu.itba.pod.query4;

import com.hazelcast.mapreduce.Context;
import com.hazelcast.mapreduce.Mapper;

import ar.edu.itba.pod.query3.Q3Answer;

public class Q4Mapper implements Mapper<String, Q3Answer, Integer, String>  {

    private static int truncate2Digits(final int number) {
        return (number / 100) * 100;
    }

    @Override
    public void map(final String collectionName, final Q3Answer ans, final Context<Integer, String> context) {
        final int group = truncate2Digits(ans.getDistinctSpecies());
        if(group > 0) {
            context.emit(group, ans.getHoodName());
        }
    }
}
