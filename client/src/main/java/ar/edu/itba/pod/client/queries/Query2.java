package ar.edu.itba.pod.client.queries;

import static ar.edu.itba.pod.client.QueryUtils.*;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ICompletableFuture;
import com.hazelcast.core.MultiMap;
import com.hazelcast.mapreduce.Job;
import com.hazelcast.mapreduce.KeyValueSource;

import ar.edu.itba.pod.CollectionContainsKeyPredicate;
import ar.edu.itba.pod.HazelcastCollectionExtractor;
import ar.edu.itba.pod.SortCollator;
import ar.edu.itba.pod.models.Neighbourhood;
import ar.edu.itba.pod.models.Tree;
import ar.edu.itba.pod.query2.Q2Answer;
import ar.edu.itba.pod.query2.Q2CombinerFactory;
import ar.edu.itba.pod.query2.Q2Mapper;
import ar.edu.itba.pod.query2.Q2ReducerFactory;

public final class Query2 {
    private Query2() {
        // static
    }

    private static final String CSV_HEADER = csvHeaderJoiner()
        .add("NEIGHBOURHOOD")
        .add("COMMON_NAME")
        .add("TREES_PER_PEOPLE")
        .toString()
        ;

    private static void writeAnswerToCsv(final Writer writer, final Q2Answer answer) throws IOException {
        writer.write(answer.getHoodName());
        writer.write(OUT_DELIM);
        writer.write(answer.getTreeName());
        writer.write(OUT_DELIM);
        writer.write(Double.toString(answer.getTreesPerInhabitant()));
        writer.write(NEW_LINE);
    }

    public static void execute(
        final HazelcastInstance hazelcast,
        final Stream<Tree> trees, final Stream<Neighbourhood> hoods,
        final Writer queryOut, final Writer timeOut) throws IOException, ExecutionException, InterruptedException {

        final MultiMap<String, Tree> treeMap = hazelcast.getMultiMap(hazelcastNamespace("q2-tree-map"));
        treeMap.clear();

        final String hoodMapName = hazelcastNamespace("q2-neighbourhood-map");
        final Map<String, Neighbourhood> hoodMap = hazelcast.getMap(hazelcastNamespace(hoodMapName));
        hoodMap.clear();

        final String hoodsNameSetName = hazelcastNamespace("q2-hoods-name-set");
        Set<String> hoodsName = hazelcast.getSet(hoodsNameSetName);
        hoodsName.clear();

        logInputProcessingStart(timeOut);

        trees.forEach(tree -> treeMap.put(tree.getHoodName(), tree));

        hoods.forEach(hood -> hoodMap.put(hood.getName(), hood));

        hoodsName = hoodMap.keySet();

        logInputProcessingEnd(timeOut);
        

        final Job<String, Tree> job = hazelcast
            .getJobTracker(hazelcastNamespace("q2-job-tracker"))
            .newJob(KeyValueSource.fromMultiMap(treeMap))
            ;

        logMapReduceJobStart(timeOut);

        final ICompletableFuture<List<Q2Answer>> future = job
            .keyPredicate   (new CollectionContainsKeyPredicate<>(hoodMapName, HazelcastCollectionExtractor.MAP_KEYS))
            .mapper         (new Q2Mapper(hoodMapName))
            .combiner       (new Q2CombinerFactory())
            .reducer        (new Q2ReducerFactory())
            .submit         (new SortCollator<>(Q2Answer.FROM_ENTRY_MAPPER))
            ;
        
            final List<Q2Answer> answers = future.get();

            queryOut.write(CSV_HEADER);
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
