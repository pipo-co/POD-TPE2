package ar.edu.itba.pod.client;

import static ar.edu.itba.pod.client.Queries.*;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ICompletableFuture;
import com.hazelcast.core.MultiMap;
import com.hazelcast.mapreduce.Job;
import com.hazelcast.mapreduce.KeyValueSource;
import ar.edu.itba.pod.query1.SetContainsKeyPredicate;
import ar.edu.itba.pod.query1.Q1Answer;
import ar.edu.itba.pod.CountCombinerFactory;
import ar.edu.itba.pod.SortCollator;
import ar.edu.itba.pod.query1.Q1Mapper;
import ar.edu.itba.pod.CountReducerFactory;
import ar.edu.itba.pod.models.Neighbourhood;
import ar.edu.itba.pod.models.Tree;

public final class Query1 {
    private Query1() {
        // static
    }

    public static void execute(
        final HazelcastInstance hazelcast,
        final Stream<Tree> trees, final Stream<Neighbourhood> hoods,
        final Path queryOut, final Path timeOut) throws IOException, ExecutionException, InterruptedException {

        final MultiMap<String, Tree> treeMap = hazelcast.getMultiMap(hazelcastNamespace("q1-tree-map"));
        trees.forEach(tree -> treeMap.put(tree.getNeighbourhoodName(), tree));

        final String hoodsNameSetName = hazelcastNamespace("q1-hoods-name-set");
        final Set<String> hoodsName = hazelcast.getSet(hoodsNameSetName);
        hoods.map   (Neighbourhood::getName)
            .forEach(hoodsName::add)
            ;


        final Job<String, Tree> job = hazelcast
            .getJobTracker(hazelcastNamespace("q1-job-tracker"))
            .newJob(KeyValueSource.fromMultiMap(treeMap))
            ;

        final ICompletableFuture<List<Q1Answer>> future = job
            .keyPredicate   (new SetContainsKeyPredicate<>(hoodsNameSetName))
            .mapper         (new Q1Mapper())
            .combiner       (new CountCombinerFactory())
            .reducer        (new CountReducerFactory())
            .submit         (new SortCollator<>(Q1Answer::fromEntry))
            ;

        final List<Q1Answer> result = future.get();

        System.out.println(result);
    }
}
