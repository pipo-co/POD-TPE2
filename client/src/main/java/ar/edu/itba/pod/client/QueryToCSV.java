package ar.edu.itba.pod.client;

import java.io.IOException;
import java.io.Writer;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

import com.hazelcast.core.HazelcastInstance;

import ar.edu.itba.pod.models.Neighbourhood;
import ar.edu.itba.pod.models.Tree;

@FunctionalInterface
public interface QueryToCSV {
    QueryMetrics executeToCSV(
        final HazelcastInstance hazelcast,
        final Stream<Tree> trees, final Stream<Neighbourhood> hoods,
        final Writer queryOut) throws IOException, ExecutionException, InterruptedException;
}
