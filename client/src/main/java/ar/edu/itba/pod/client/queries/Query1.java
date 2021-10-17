package ar.edu.itba.pod.client.queries;

import static ar.edu.itba.pod.client.QueryUtils.*;

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
import ar.edu.itba.pod.query1.Q1Answer;
import ar.edu.itba.pod.CountCombinerFactory;
import ar.edu.itba.pod.SortCollator;
import ar.edu.itba.pod.query1.Q1Mapper;
import ar.edu.itba.pod.CountReducerFactory;
import ar.edu.itba.pod.SetContainsKeyPredicate;
import ar.edu.itba.pod.models.Neighbourhood;
import ar.edu.itba.pod.models.Tree;

public final class Query1 {
    private Query1() {
        // static
    }

    private static void writeAnswerToCsv(final Writer writer, final Q1Answer answer) throws IOException {
        writer.write(answer.getHood());
        writer.write(OUT_DELIM);
        writer.write(Integer.toString(answer.getTreeCount()));
        writer.write(NEW_LINE);
    }

    public static void execute(
        final HazelcastInstance hazelcast,
        final Stream<Tree> trees, final Stream<Neighbourhood> hoods,
        final Writer queryOut, final Writer timeOut) throws IOException, ExecutionException, InterruptedException {

        final MultiMap<String, Tree> treeMap = hazelcast.getMultiMap(hazelcastNamespace("q1-tree-map"));
        treeMap.clear();

        final String hoodsNameSetName = hazelcastNamespace("q1-hoods-name-set");
        final Set<String> hoodsName = hazelcast.getSet(hoodsNameSetName);
        hoodsName.clear();

        logInputProcessingStart(timeOut);

        trees.forEach(tree -> treeMap.put(tree.getHoodName(), tree));
        hoods.map(Neighbourhood::getName).forEach(hoodsName::add);

        logInputProcessingEnd(timeOut);

        final Job<String, Tree> job = hazelcast
            .getJobTracker(hazelcastNamespace("q1-job-tracker"))
            .newJob(KeyValueSource.fromMultiMap(treeMap))
            ;

        logMapReduceJobStart(timeOut);

        final ICompletableFuture<List<Q1Answer>> future = job
            .keyPredicate   (new SetContainsKeyPredicate<>(hoodsNameSetName))
            .mapper         (new Q1Mapper())
            .combiner       (new CountCombinerFactory())
            .reducer        (new CountReducerFactory())
            .submit         (new SortCollator<>(Q1Answer::fromEntry))
            ;

        final List<Q1Answer> answers = future.get();

        for(final Q1Answer answer : answers) {
            writeAnswerToCsv(queryOut, answer);
        }

        logMapReduceJobEnd(timeOut);

        // Limpiamos recursos usados
        treeMap.clear();
        hoodsName.clear();
    }
}
