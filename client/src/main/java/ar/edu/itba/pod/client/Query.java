package ar.edu.itba.pod.client;

import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.stream.Stream;

import com.hazelcast.core.HazelcastInstance;

import ar.edu.itba.pod.models.Neighbourhood;
import ar.edu.itba.pod.models.Tree;

@FunctionalInterface
public interface Query<Answer> {
    QueryMetrics execute(
        final HazelcastInstance hazelcast,
        final Stream<Tree> trees, final Stream<Neighbourhood> hoods,
        final Consumer<Answer> queryOut) throws ExecutionException, InterruptedException;
}
