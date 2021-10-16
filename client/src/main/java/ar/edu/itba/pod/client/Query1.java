package ar.edu.itba.pod.client;

import static ar.edu.itba.pod.client.Queries.*;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ICompletableFuture;
import com.hazelcast.core.MultiMap;
import com.hazelcast.mapreduce.Job;
import com.hazelcast.mapreduce.KeyValueSource;
import ar.edu.itba.pod.query1.NeighbourhoodPresentPredicate;
import ar.edu.itba.pod.query1.SortCollator;
import ar.edu.itba.pod.query1.TreeCountMapper;
import ar.edu.itba.pod.query1.UselessReducerFactory;
import ar.edu.itba.pod.models.Neighbourhood;
import ar.edu.itba.pod.models.Tree;

public final class Query1 {
    private Query1() {
        // static
    }

    public static void execute(
        final HazelcastInstance hazelcast,
        final Stream<Tree> trees, final Stream<Neighbourhood> hoods,
        final Path queryOut, final Path timeOut) throws IOException {

        final MultiMap<String, Tree> treeMap = hazelcast.getMultiMap(hazelcastNamespace("q1-tree-map"));
        trees.forEach(tree -> treeMap.put(tree.getNeighbourhoodName(), tree));

        final Set<Neighbourhood> hoodNames = hoods.collect(Collectors.toSet());

        final Job<String, Tree> job = hazelcast
            .getJobTracker(hazelcastNamespace("q1-job-tracker"))
            .newJob(KeyValueSource.fromMultiMap(treeMap))
            ;

        final ICompletableFuture<Long> future = job
            .keyPredicate(new NeighbourhoodPresentPredicate())
            .mapper(new TreeCountMapper())
            .reducer(new UselessReducerFactory())
            .submit(new SortCollator());

        final Map<String, Long> result = future.get();
    }
}
