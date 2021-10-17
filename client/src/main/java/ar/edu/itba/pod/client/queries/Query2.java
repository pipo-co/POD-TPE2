package ar.edu.itba.pod.client;

import static ar.edu.itba.pod.client.Queries.hazelcastNamespace;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ICompletableFuture;
import com.hazelcast.core.MultiMap;
import com.hazelcast.mapreduce.Job;
import com.hazelcast.mapreduce.KeyValueSource;

import ar.edu.itba.pod.SortCollator;
import ar.edu.itba.pod.models.Neighbourhood;
import ar.edu.itba.pod.models.Tree;
import ar.edu.itba.pod.query2.Q2Answer;
import ar.edu.itba.pod.query2.Q2CombinerFactory;
import ar.edu.itba.pod.query2.Q2Mapper;
import ar.edu.itba.pod.query2.Q2ReducerFactory;
import ar.edu.itba.pod.SetContainsKeyPredicate;

public final class Query2 {

    private Query2() {
        // static
    }

    public static void execute(final HazelcastInstance hazelcast, final Stream<Tree> trees,
            final Stream<Neighbourhood> hoods, final Path queryOut, final Path timeOut)
            throws IOException, ExecutionException, InterruptedException {

        final MultiMap<String, Tree> treeMap = hazelcast.getMultiMap(hazelcastNamespace("q2-tree-map"));
        treeMap.clear();

        final String hoodMap = hazelcastNamespace("q2-neighbourhood-map");
        final Map<String, Neighbourhood> neighbourhoodMap = hazelcast.getMap(hazelcastNamespace(hoodMap));
        hoodMap.clear();

        final String hoodsNameSetName = hazelcastNamespace("q2-hoods-name-set");
        Set<String> hoodsName = hazelcast.getSet(hoodsNameSetName);
        hoodsName.clear();

        logInputProcessingStart(timeOut);

        trees.forEach(tree -> treeMap.put(tree.getHoodName(), tree));

        hoods.forEach(hood -> neighbourhoodMap.put(hood.getName(), hood));

        hoodsName = neighbourhoodMap.keySet();

        logInputProcessingEnd(timeOut);
        

        final Job<String, Tree> job = hazelcast
            .getJobTracker(hazelcastNamespace("q2-job-tracker"))
            .newJob(KeyValueSource.fromMultiMap(treeMap))
            ;

        logMapReduceJobStart(timeOut);

        final ICompletableFuture<List<Q2Answer>> future = job
            .keyPredicate   (new SetContainsKeyPredicate<>(hoodsNameSetName))
            .mapper         (new Q2Mapper(hoodMap))
            .combiner       (new Q2CombinerFactory())
            .reducer        (new Q2ReducerFactory())
            .submit         (new SortCollator<>(Map.Entry::getValue))
            ;
        
            final List<Q2Answer> result = future.get();

            for(final Q2Answer answer : answers) {
                writeAnswerToCsv(queryOut, answer);
            }
    
            logMapReduceJobEnd(timeOut);
    
            // Limpiamos recursos usados
            treeMap.clear();
            hoodsName.clear();
            hoodMap.clear();
    }
}
