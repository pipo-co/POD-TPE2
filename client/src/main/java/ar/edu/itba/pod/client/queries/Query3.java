package ar.edu.itba.pod.client.queries;

import static ar.edu.itba.pod.client.QueryUtils.*;

import static ar.edu.itba.pod.client.QueryUtils.hazelcastNamespace;
import static ar.edu.itba.pod.client.QueryUtils.logInputProcessingEnd;
import static ar.edu.itba.pod.client.QueryUtils.logInputProcessingStart;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Set;
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
import ar.edu.itba.pod.query3.Q3Answer;
import ar.edu.itba.pod.query3.Q3CombinerFactory;
import ar.edu.itba.pod.query3.Q3Mapper;
import ar.edu.itba.pod.query3.Q3ReducerFactory;

public class Query3 {

    public Query3() {
        // static
    }

    private static final String CSV_HEADER = csvHeaderJoiner()
        .add("NEIGHBOURHOOD")
        .add("COMMON_NAME_COUNT")
        .toString()
        ;

    private static void writeAnswerToCsv(final Writer writer, final Q3Answer answer) throws IOException {
        writer.write(answer.getHoodName());
        writer.write(OUT_DELIM);
        writer.write(Integer.toString(answer.getDifferentSpecies()));
        writer.write(NEW_LINE);
    }


    public static void execute(
            final HazelcastInstance hazelcast,
            final Stream<Tree> trees, final Stream<Neighbourhood> hoods,
            final Writer queryOut, final Writer timeOut) throws IOException, ExecutionException, InterruptedException {

        final MultiMap<String, Tree> treeMap = hazelcast.getMultiMap(hazelcastNamespace("q3-tree-map"));
        treeMap.clear();

        final String hoodsNameSetName = hazelcastNamespace("q3-hoods-name-set");
        final Set<String> hoodsName = hazelcast.getSet(hoodsNameSetName);
        hoodsName.clear();

        logInputProcessingStart(timeOut);

        trees.forEach(tree -> treeMap.put(tree.getHoodName(), tree));
        hoods.map(Neighbourhood::getName).forEach(hoodsName::add);

        logInputProcessingEnd(timeOut);

        final Job<String, Tree> job = hazelcast
            .getJobTracker(hazelcastNamespace("q3-job-tracker"))
            .newJob(KeyValueSource.fromMultiMap(treeMap))
            ;
        
        logMapReduceJobStart(timeOut);

        final ICompletableFuture<List<Q3Answer>> future = job
            .keyPredicate   (new CollectionContainsKeyPredicate<>(hoodsNameSetName, HazelcastCollectionExtractor.SET))
            .mapper         (new Q3Mapper())
            .combiner       (new Q3CombinerFactory())
            .reducer        (new Q3ReducerFactory())
            .submit         (new SortCollator<>(Q3Answer.FROM_ENTRY_MAPPER))   
            ;
    
        final List<Q3Answer> answers = future.get();

        queryOut.write(CSV_HEADER);
        for(final Q3Answer answer : answers) {
            writeAnswerToCsv(queryOut, answer);
        }

        logMapReduceJobEnd(timeOut);

        // Limpiamos recursos usados
        treeMap.clear();
        hoodsName.clear();
    
    
        }
}
